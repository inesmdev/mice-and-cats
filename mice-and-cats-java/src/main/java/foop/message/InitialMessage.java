package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record InitialMessage(String playerName) implements Message {
    public static final int TAG = 6;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(playerName);
    }

    public static InitialMessage parse(DataInputStream in) throws IOException {
        var playerName = in.readUTF();
        return new InitialMessage(playerName);
    }
}
