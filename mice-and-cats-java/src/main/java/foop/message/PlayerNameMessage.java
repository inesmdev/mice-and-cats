package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record PlayerNameMessage(String playerName) implements Message {
    public static final int TAG = 6;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(playerName);
    }

    public static PlayerNameMessage parse(DataInputStream in) throws IOException {
        var playerName = in.readUTF();
        return new PlayerNameMessage(playerName);
    }
}
