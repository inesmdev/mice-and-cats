package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record JoinedGameMessage(String name) implements Message {
    public static final int TAG = 12;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public static JoinedGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        return new JoinedGameMessage(name);
    }
}
