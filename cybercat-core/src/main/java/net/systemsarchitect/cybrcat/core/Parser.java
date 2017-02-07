package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.Cybercat.CybercatLexer;
import net.systemsarchitect.Cybercat.CybercatParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Parser {

    public void parse(String filename) throws IOException {
        String evaluate = new String(Files.readAllBytes(Paths.get(filename)));

        ANTLRInputStream input = new ANTLRInputStream(evaluate);

        CybercatLexer lexer = new CybercatLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CybercatParser parser = new CybercatParser(tokens);

        ParseTree tree = parser.parse();

        Visitor eval = new Visitor(new UniverseImpl());

        //System.out.println(tree.toStringTree(parser));

        eval.visit(tree);
    }

}
