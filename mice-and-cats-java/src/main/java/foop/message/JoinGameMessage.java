package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record JoinGameMessage(String name) implements Message {
    public static final int TAG = 2;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public static JoinGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        return new JoinGameMessage(name);
    }
}
