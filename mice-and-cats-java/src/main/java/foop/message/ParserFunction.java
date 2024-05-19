package foop.message;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
interface ParserFunction {
    Message parse(DataInputStream in) throws IOException;
}
