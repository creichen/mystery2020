package mystery2020;

import mystery2020.runtime.Runtime;
import AST.*;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Interpreter {
	private static final Interpreter interpreter = new Interpreter(); // this only exists so we can use :: notation conveniently
	private static final String VERSION = "0.2.0";

	public static final String READ_FROM_STDIN = "-";

	static Configuration configuration = new Configuration();

	private static Consumer<String[]> action = Interpreter::run;
	
	private static boolean long_exceptions = false;

	private CommandLineOption[] command_line_options = new CommandLineOption[] {
			new CommandLineOption('v', "Print version number", false, s -> { Interpreter.action = Interpreter::print_version; }),
			new CommandLineOption('P', "Print input program after parsing, rather than running it", false, s -> { Interpreter.action = Interpreter::print_after_parse; }),
			new CommandLineOption('l', "List configuration options (human-readable)", false, s -> { Interpreter.action = Interpreter::print_config_options; }),
			new CommandLineOption('L', "List configuration options (excl. operators) (machine-readable)", false, s -> { Interpreter.action = Interpreter::print_config_options_machine; }),
			new CommandLineOption('h', "Print this help", false,      s -> { Interpreter.action = Interpreter::print_help; }),
			new CommandLineOption('c', "Configure one or more subsystem(s)", true,  s -> Interpreter.configuration.setOptions(s)),
			new CommandLineOption('d', "Debugging", false, s -> { Interpreter.long_exceptions = true; }),
			new CommandLineOption('s', "Set the maximum number of steps to execute", true,  s -> Interpreter.configuration.setStepLimit(Integer.parseInt(s))),
			new CommandLineOption('u', "Set the maximum number of subprogram calls to execute", true,  s -> Interpreter.configuration.setCallLimit(Integer.parseInt(s)))
	};

    public static void
    main(String[] args) throws FileNotFoundException {
		ArrayList<String> leftover_args = new ArrayList<>();

		boolean quick_append_mode = false;
		CommandLineOption active_option = null;
		for (String arg : args) {
			if (quick_append_mode) {
				leftover_args.add(arg);
				continue;
			}

			if (active_option == null) {
				if (arg.length() > 1 && arg.startsWith("-")) {
					if (arg.equals("--")) {
						// append all remaining arguments
						quick_append_mode = true;
						continue;
					}
					boolean found = false;

					for (CommandLineOption option: Interpreter.interpreter.command_line_options) {
						if (option.accepts(arg)) {
							found = true;
							if (option.takesArgument()) {
								active_option = option;
							} else {
								option.run(null);
							}
							break;
						}
					}
					if (!found) {
						System.err.println("Unsupported option `" + arg + "'");
						System.exit(1);
					}
				} else {
					leftover_args.add(arg);
				}
			} else {
				active_option.run(arg);
				active_option = null;
			}
		}

		Interpreter.action.accept(leftover_args.toArray(new String[leftover_args.size()]));
    }

	private static void
	run(String[] args) {
		String filename = getFilename(args);
		Runtime rt = new Runtime(Interpreter.configuration);

		try {
			// Construct the AST
			Program m;
			if (filename.equals(READ_FROM_STDIN)) {
				m = parse(new InputStreamReader(System.in));
			} else {
				m = parseFile(filename);
			}
			m.setConfiguration(Interpreter.configuration);
			m.run(rt);
		} catch (IOException exn) {
			throw new RuntimeException(exn);
		} catch (MysteryException exn) {
			if (Interpreter.long_exceptions) {
				exn.printStackTrace();
			}
			System.err.println(exn);
		}
		for (String s : rt.getOutput()) {
			System.out.println(s);
		}
	}

	private static void
	print_after_parse(String[] args) {
        String filename = getFilename(args);

        try {
        	// Construct the AST
        	Program m = parseFile(filename);
        	m.setConfiguration(Interpreter.configuration);
        	System.out.println(m.toString());
        } catch (IOException exn) {
        	throw new RuntimeException(exn);
        }
	}

	private static String
	pad(String s, int len) {
		while (s.length() < len) {
			s += " ";
		}
		return s;
	}

	private static void
	print_config_options(String[] args) {
		System.out.println("Configuration options are written\n  SUBSYS0:OPT0,SUBSYS1:OPT1,...,SUBSYSn:OPTn");
		System.out.println("\nEach SUBSYSi is a subsystem.  There are multiple subsystems that can be configured (listed below).\n");
		System.out.println("Subsystem " + Configuration.OP_SUBSYSTEM_CODE + ":");
		System.out.println("  This subsystem uses a string of " + (Configuration.Op.values().length) + " pairs of the form");
		System.out.println("     pa        where p = precedence ('0'...'9'), and a = associativity('l', 'r', or '-' for non-associative.");
		System.out.println("  For example:\n");
		System.out.println("     2r        selects precedence 2 (lower than 3, higher than 1), right-associativity.");
		System.out.println("  The order in which these pairs are specified for the different operators is:");

		for (Configuration.Op op : Configuration.Op.values()) {
			System.out.println("    " + op);
		}
		for (ConfigSubsystem<?> subsys : Configuration.getSubsystems()) {
			System.out.println("\nSubsystem " + subsys.getCode() + " : " + subsys.getName());
			for (int i = 0; i < subsys.size(); i++) {
				ConfigOption<?> opt = subsys.getAt(i);
				System.out.println("    " + pad(opt.getCode(), 10) + opt.getName());
			}
		}
	}

	private static void
	print_config_options_machine(String[] args) {
		for (ConfigSubsystem<?> subsys : Configuration.getSubsystems()) {
			System.out.println(subsys.getCode() + " " + subsys.getName());
			for (int i = 0; i < subsys.size(); i++) {
				ConfigOption<?> opt = subsys.getAt(i);
				System.out.println(":" + opt.getCode() + " " + opt.getName());
			}
		}
	}

	private static void
	print_version(String[] args) {
		System.out.println("Mystery2020, version " + VERSION);
		System.out.println("Copyright (C) 2019 Christoph Reichenbach (christoph.reichenbach@cs.lth.se)");
	}

	private static void
	print_help(String[] args) {
		for (CommandLineOption opt : Interpreter.interpreter.command_line_options) {
			System.out.println(opt);
		}
		System.out.print("\nDefault configuration is:\n  ");
		System.out.println((new Configuration()).toString());
	}

    public static String
    getFilename(String[] args) {
        if(args.length != 1) {
            System.err.println("Must specify exactly one mystery file to run");
            System.exit(1);
        }
        return args[0];
    }

    public static Program
    parseFile(String filename) throws FileNotFoundException {
    	try {
    		return parse(new FileReader(filename));
    	} catch (FileNotFoundException exn) {
    		throw new IOFailureException(exn);
    	}
    }

    public static Program
    parseString(String body) {
    	return parse(new StringReader(body));
    }

    /**
     * Parses the Mystery program from the given reader
     *
     * @throw LexerException
     * @throw ParserException
     * @throw IOException
     */
    public static Program
    parse(Reader reader) {
    	ProgramScanner scanner = new ProgramScanner(reader);

    	ProgramParser parser = new ProgramParser();
    	try {
    		Program result = (Program)parser.parse(scanner);
    		return result;
    	} catch (IOException exn) {
    		throw new IOFailureException(exn);
    	} catch (beaver.Parser.Exception e) {
    		throw new Error(e);
    	}
    }

    private class CommandLineOption {
    	private boolean takes_arg;
    	private char short_option;
    	private String description;
    	private Consumer<String> action;

    	public CommandLineOption(char short_option, String description, boolean takes_arg, Consumer<String> action) {
    		this.short_option = short_option;
    		this.description = description;
    		this.takes_arg = takes_arg;
    		this.action = action;
    	}

    	public void
    	run(String arg) {
    		this.action.accept(arg);
    	}

    	@Override
    	public String
    	toString() {
    		StringBuffer sb = new StringBuffer(" -" + this.short_option);
    		if (this.takes_arg) {
    			sb.append(" arg ");
    		} else {
    			sb.append("     ");
    		}
    		sb.append(this.description);
    		return sb.toString();
    	}

    	public boolean
    	takesArgument() {
    		return this.takes_arg;
    	}

    	public boolean
    	accepts(String s) {
    		return s.equals("-" + this.short_option);
    	}
    }
}
