package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record ExitGameMessage(String name) implements Message {
    public static final int TAG = 9;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public static ExitGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        return new ExitGameMessage(name);
    }
}
