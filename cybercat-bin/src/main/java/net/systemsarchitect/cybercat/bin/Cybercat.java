package net.systemsarchitect.cybercat.bin;

import net.systemsarchitect.cybrcat.core.Parser;

import java.io.IOException;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Cybercat {

    public static void main(String[] args) throws IOException {
        Parser cybercatParser = new Parser();
        cybercatParser.parse(args[0]);
    }

}
