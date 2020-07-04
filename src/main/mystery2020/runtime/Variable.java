package mystery2020.runtime;

import AST.ASTNode;
import AST.Expr;
import mystery2020.Configuration;
import mystery2020.InvalidModeException;
import mystery2020.MType;

public class Variable {
	private String name;
	private MType type;
	protected Value value;

	/**
	 * Constructs a new variable
	 *
	 * The value is initialised to null, which is not a valid value.  Use initDefault() to properly initialise.
	 *
	 * @param type Type of the variable, must be set
	 * @param name Name of the variable, may be null
	 */
	public Variable(MType type, String name) {
		assert type != null;
		this.type = type;
		this.name = name;
		this.value = null;
	}

	public void
	initDefault(ASTNode node) {
		this.value = this.type.getDefaultValue(node);
	}

	public Variable
	copyAndInit(ASTNode node) {
		Variable v = new Variable(this.type, this.name);
		v.initDefault(node);
		return v;
	}

	public String
	getName() {
		return this.name;
	}

	public boolean
	hasName() {
		return this.name != null;
	}

	public MType
	getType() {
		return this.type;
	}

	public Value
	getValue() {
		return this.value;
	}

	public void
	checkAndSetValue(ASTNode<?> node, Value v, Configuration config) {
		this.type.ensureCanAssignFrom(node, v.getType());
		this.type.ensureValueIsAssignable(node, v);
		this.setValue(v, config);
	}

	/**
	 * No type checking here, type checking happens at assignments and calls
	 *
	 * @param v
	 * @param config Configuration for special assignment semantics, or null for default semantics
	 */
	public void
	setValue(Value v, Configuration config) {
		Object raw_value = v.getValue();
		if (config != null) {
			if (this.type instanceof MType.ArrayType
					&& this.value != null
			                && this.value.getValue() instanceof VariableVector
					&& v.getValue() instanceof VariableVector) {
				VariableVector lhs = (VariableVector) this.value.getValue();
				VariableVector rhs = (VariableVector) v.getValue();
				raw_value = config.array_assignment.get().assign(lhs, rhs, config);
				System.err.println("Raw value: " + System.identityHashCode(v.getValue())  + " -> " + System.identityHashCode(raw_value));
			}
		}
		this.internalAssignValue(raw_value, v.getType(), config);
	}

	// after all value processing is done, this is the  internal value assignment
	protected void
	internalAssignValue(Object raw_value, MType type, Configuration config) {
		this.value = new Value(this.type, raw_value);
	}

	@Override
	public String
	toString() {
		return this.name + ":" + this.type + "=" + this.value;
	}

	public void
	setName(String new_name) {
		this.name = new_name;
	}

	public static class Proxy extends Variable {
		private Variable remote;
		protected Configuration config;

		public Proxy(ASTNode astnode, Variable outer_variable, Configuration config) {
			super(outer_variable.getType(), outer_variable.getName() + ".proxy");
			this.initDefault(astnode);
			this.remote = outer_variable;
			this.config = config;
		}

		public void
		loadFromRemote() {
			this.setValue(this.remote.getValue(), this.config);
		}

		public Value
		getAssignedValue() {
			return super.getValue();
		}

		public void
		writeToRemote() {
			if (this.getAssignedValue() != null) {
				this.remote.setValue(this.getAssignedValue(), this.config);
			}
		}

		@Override
		public String
		toString() {
			return "PROXY[" + remote + "]" + this.getName() + ":" + this.getType() + "="
					+ this.value;
		}

	}

	public static class WriteOnlyProxy extends Proxy{
		private ASTNode<?> owner;

		public WriteOnlyProxy(ASTNode node, Variable remote, Configuration config, ASTNode<?> owner) {
			super(node, remote, config);
			this.owner = owner;
		}

		@Override
		public Value
		getValue() {
			throw new InvalidModeException(this.owner.line(), "Cannot read from write-only variable");
		}

		@Override
		public void
		internalAssignValue(Object raw_value, MType type, Configuration config) {
			if (this.config.type_check.get().dynamic_checks()) {
				this.getType().ensureCanAssignFrom(this.owner, type);
			}
			super.internalAssignValue(raw_value, type, config);
		}
	}

	public static class Dynamic extends Variable {
		public Dynamic(MType type, String name) {
			super(type, name);
		}

		@Override
		protected void
		internalAssignValue(Object raw_value, MType type, Configuration config) {
			this.value = new Value(type, raw_value);
		}
	}

	public static class Lazy extends Dynamic {
		protected Expr expr;
		protected Runtime rt;
		protected VariableStack stack;

		public Lazy(MType type, String name, Expr expr, Runtime rt) {
			super(type, name);
			this.rt = rt;
			this.stack = rt.getStack().copy();
			this.expr = expr;
		}

		protected final Variable
		var() {
			final VariableStack old_stack = this.rt.getStack();
			this.rt.setStack(this.stack);
			Variable v = this.expr.variable(this.rt);
			this.rt.setStack(old_stack);
			return v;
		}

		@Override
		public Value
		getValue() {
			final VariableStack old_stack = this.rt.getStack();
			this.rt.setStack(this.stack);
			Value v = this.expr.eval(this.rt);
			this.rt.setStack(old_stack);
			return v;
		}

		protected void
		assignToVar(Variable var, MType type, Object raw_value) {
			if (var == null) {
				throw new InvalidModeException(this.expr.line(), "Cannot write to non-variable expression");
			}
			var.checkAndSetValue(this.expr, new Value(type, raw_value), this.rt.getConfiguration());
		}

		@Override
		public void
		internalAssignValue(Object raw_value, MType type, Configuration config) {
			Variable var = var();
			if (var == null) {
				return;
			}
			if (config.type_check.get().dynamic_checks()) {
				var.getType().ensureCanAssignFrom(this.expr, type);
			}
			this.assignToVar(var, type, raw_value);
		}
	}

	public static class LazyCached extends Lazy {
		private Variable var;
		private Value val;

		public LazyCached(MType type, String name, Expr expr, Runtime rt) {
			super(type, name, expr, rt);
			this.val = null;
			this.var = null;
		}

		private Variable
		getVar() {
			if (this.var == null) {
				this.var = this.var();
			}
			return this.var;
		}

		@Override
		public Value
		getValue() {
			if (this.val == null) {
				Variable var = this.getVar();
				if (var != null) {
					this.val = var.getValue();
				} else {
					this.val = super.getValue();
				}
			}
			return this.val;
		}

		@Override
		public void
		internalAssignValue(Object raw_value, MType type, Configuration config) {
			Variable var = this.getVar();
			if (var == null) {
				return;
			}
			if (config.type_check.get().dynamic_checks()) {
				var.getType().ensureCanAssignFrom(this.expr, type);
			}
			this.assignToVar(var, type, raw_value);
		}
	}
}
