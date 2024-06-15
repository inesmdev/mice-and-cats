package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record GameOverMessage(boolean victory) implements Message {
    public static final int TAG = 11;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeBoolean(victory);
    }

    public static GameOverMessage parse(DataInputStream in) throws IOException {
        var victory = in.readBoolean();
        return new GameOverMessage(victory);
    }
}
