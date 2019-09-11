package mystery2020;
import AST.*;
import java.io.*;

public class Compiler {

    public static void main(String[] args) {
        String filename = getFilename(args);

        // Construct the AST
        Block m = parse(filename);
	System.out.println(m.toString());
    }

    public static String getFilename(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java Compiler filename");
            System.exit(1);
        }
        return args[0];
    }

    public static Block parse(String filename) {
        try {
            FileReader reader = new FileReader(filename);
            ProgramScanner scanner = new ProgramScanner(reader);
            ProgramParser parser = new ProgramParser();
            Block result = (Block)parser.parse(scanner);
            return result;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
