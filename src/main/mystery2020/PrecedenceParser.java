package mystery2020;

import mystery2020.Configuration.Op;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AST.BinOpSequence;
import AST.BinOp;
import AST.Expr;

public class PrecedenceParser {
	public static boolean testMode = false; // set to true for testing

	Map<Op, OpConfig> config;
	List<Stratum> strata = new ArrayList<>();

	public PrecedenceParser(Map<Op, OpConfig> config) {
		this.config = config;
		List<Integer> stratum_precedences = new ArrayList<>();

		Map<Integer, Set<Op>> operator_order = new HashMap<>();
		for (Op op : Op.class.getEnumConstants()) {
			int precedence = config.get(op).getPrecedence();
			Set<Op> colleagues = operator_order.get(precedence);
			if (colleagues == null) {
				operator_order.put(precedence, EnumSet.of(op));
				stratum_precedences.add(precedence);
			} else {
				colleagues.add(op);
			}
		}
		Collections.sort(stratum_precedences);
		Collections.reverse(stratum_precedences);
		for (int precedence : stratum_precedences) {
			Set<Op> operators = operator_order.get(precedence);
			this.strata.add(this.makeStratum(operators));
		}
	}

	private Stratum
	makeStratum(Set<Op> operators) {
		EnumSet<Op> left_associative = EnumSet.noneOf(Op.class);
		EnumSet<Op> non_associative = EnumSet.noneOf(Op.class);
		EnumSet<Op> right_associative = EnumSet.noneOf(Op.class);
		for (Op op : operators) {
			switch (this.config.get(op).getAssociativity()) {
			case LEFT:  left_associative.add(op); break;
			case RIGHT: right_associative.add(op); break;
			case NONE:  non_associative.add(op); break;
			}
		}
		return new Stratum(left_associative, non_associative, right_associative);
	}

	public static AST.Operator
	makeOperator(Op op) {
		switch (op) {
		case ADD: return new AST.AddOp();
		case AND: return new AST.AndOp();
		case EQ:  return new AST.EqOp();
		case GT:  return new AST.GTOp();
		}
		return null;
	}

	/**
	 * Starts out as list of exprs and binops, turns into tree
	 *
	 * @author creichen
	 */
	private class
	PartTreeList {
		private List<Expr> part_trees = new ArrayList<>();
		private List<Op> operators = new ArrayList<>();

		public
		PartTreeList(Expr bops) {

			Expr rhs = bops;
			while (rhs instanceof BinOpSequence) {
				BinOpSequence bop = (BinOpSequence) rhs;
				this.part_trees.add(bop.getLHS());
				this.operators.add(bop.getOperator().getOperator());
				rhs = bop.getRHS();
			}
			this.part_trees.add(rhs);
		}

		/**
		 * Fix up any recursive BinOpSequences
		 */
		public void
		parsePartTrees() {
			for (int i = 0; i < this.part_trees.size(); i++) {
				Expr expr = part_trees.get(i);
				if (expr instanceof BinOpSequence) {
					part_trees.set(i, PrecedenceParser.this.parse(expr));
				}
			}
		}

		public Op
		getOperatorAfter(int offset) {
			if (offset < 0 || offset >= this.operators.size()) {
				return null;
			}
			return this.operators.get(offset);
		}

		public Expr
		getExpr(int offset) {
			return this.part_trees.get(offset);
		}

		/**
		 * Merge two nodes
		 * @param pos The left node to merge
		 */
		public void
		merge(int pos) {
			Expr lhs = this.getExpr(pos);
			AST.Operator op = this.makeOperatorAfter(pos);
			Expr rhs = this.getExpr(pos + 1);
			BinOp replacement = new BinOp(lhs, op, rhs);
				//lhs.setParent(replacement);
				//rhs.setParent(replacement);

			this.part_trees.remove(pos); // shrink
			this.operators.remove(pos);
			this.part_trees.set(pos, replacement);
		}

		public AST.Operator
		makeOperatorAfter(int pos) {
			AST.Operator op = PrecedenceParser.makeOperator(this.getOperatorAfter(pos));
			if (op == null) {
				throw new RuntimeException("Missing operator at offset " + pos + " of " + this.size());
			}
			return op;
		}

		int size() {
			return this.operators.size();
		}

		@Override
		public String
		toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i <= this.size(); i++) {
				sb.append("{" + this.part_trees.get(i).toString() + "}");
				if (i < this.size()) {
					sb.append(" ");
					sb.append(this.getOperatorAfter(i));
					sb.append(" ");
				}
			}
			return sb.toString();
		}
	}

	public Expr
	parse(Expr bos) {
		// System.err.println(">>>>>>>>>> pp(" + bos.toString() + ") =>\n" + this.strata.toString());
		PartTreeList ptl = new PartTreeList(bos);
		ptl.parsePartTrees(); // fix up immediate subexpressions recursively
		for (Stratum s : this.strata) {
			s.reduce(ptl);
		}
		if (ptl.size() != 0) {
			throw new RuntimeException("Precedence parsing failed with " + ptl);
		}
		// System.err.println(" ==>\n" + ptl.getExpr(0));
		return ptl.getExpr(0);
	}

	// precedence parsing stratum (operators with same precedence)
	private static class Stratum {
		private Pass left_to_right;
		private Pass right_to_left;
		private Pass invariant;

		public Stratum(
				Set<Op> left_associative,
				Set<Op> non_associative,
				Set<Op> right_associative) {

			Set<Op> left_and_nonassociative = new HashSet<Op>(left_associative);
			left_and_nonassociative.addAll(non_associative);
			Set<Op> right_and_nonassociative = new HashSet<Op>(right_associative);
			right_and_nonassociative.addAll(non_associative);

			this.left_to_right = new Pass(left_associative, right_and_nonassociative);
			this.right_to_left = new Pass(right_associative, left_and_nonassociative);
			this.invariant = new Pass(non_associative, non_associative);
		}

		public void
		reduce(PartTreeList ptl) {
			int size = ptl.size();
			this.left_to_right.reduce(ptl, 0, size, +1, 0);
			this.right_to_left.reduce(ptl, size - 1, size, -1, -1);
			this.invariant.reduce(ptl, 0, size, +1, 0);
		}

		@Override
		public String
		toString() {
			return "[l->r: " + this.left_to_right + "; l<-r: " + this.right_to_left + "; inv: " + this.invariant + "]";
		}
	}

	// a pass (left-to-right or right-to-left) within a stratum
	private static class Pass {
		private Set<Op> operators;
		private Set<Op> conflict;

		/**
		 * Creates a new stratum pass
		 * 
		 * @param operators Operators that participate in this pass
		 * @param non_associative Operators that are nonassociative (i.e., in conflict) with this pass
		 */
		public Pass(Set<Op> operators, Set<Op> non_associative) {
			this.operators = operators;
			this.conflict = non_associative;
		}

		public void
		reduce(PartTreeList ptl, int start, int size, int progress_if_not_reduced, int progress_if_reduced) {
			if (this.operators.isEmpty()) {
				return;
			}
			int pos = start;
			while (0 < size--) {

				//System.err.println("]]] At " + pos);
				if (this.operators.contains(ptl.getOperatorAfter(pos))) {
					Op potential_conflict = ptl.getOperatorAfter(pos + progress_if_not_reduced);
					if (this.conflict.contains(potential_conflict)) {
						// operator conflict: to adjacent nonassociative operators, or right + left associative operators with same precedence
						throw new AssociativityException(ptl.getExpr(pos).line());
					}
					//System.err.println("]]] Reducing [" + ptl + "] at " + pos);
					ptl.merge(pos);
					//System.err.println("]]]   => [" + ptl + "], now at " + pos);
					pos += progress_if_reduced;
				} else {
					//System.err.println("]]] Operator uninteresting here ");
					pos += progress_if_not_reduced;
				}
			}
		}

		@Override
		public String
		toString() {
			return this.operators + " conflicts: " + this.conflict;
		}
	}
}
