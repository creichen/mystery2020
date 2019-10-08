package mystery2020;

import mystery2020.runtime.Runtime;
import AST.*;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Interpreter {
	private static final Interpreter interpreter = new Interpreter(); // this only exists so we can use :: notation conveniently
	private static final String VERSION = "0.1.0";
	
	static Configuration configuration = new Configuration();
	
	private static Consumer<String[]> action = Interpreter::run;  

	private CommandLineOption[] command_line_options = new CommandLineOption[] {
			new CommandLineOption('v', "Print version number", false, s -> { Interpreter.action = Interpreter::print_version; }), 
			new CommandLineOption('P', "Print input program after parsing, rather than running it", false, s -> { Interpreter.action = Interpreter::print_after_parse; }), 
			new CommandLineOption('h', "Print this help", false,      s -> { Interpreter.action = Interpreter::print_help; }),
			new CommandLineOption('c', "Configure one or more subsystem(s)", true,  s -> Interpreter.configuration.setOptions(s)) 
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
				if (arg.startsWith("-")) {
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

        try {
        	// Construct the AST
        	Program m = parseFile(filename);
        	m.setConfiguration(Interpreter.configuration);
        	Runtime rt = new Runtime();
        	m.run(rt);
        	for (String s : rt.getOutput()) {
        		System.out.println(s);
        	}
        } catch (IOException exn) {
        	throw new RuntimeException(exn);
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
