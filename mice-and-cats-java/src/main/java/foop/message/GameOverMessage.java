package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record GameOverMessage(Result result) implements Message {
    public static final int TAG = 11;

    public enum Result {
        YOU_DIED,
        ALL_BUT_YOU_DIED,
        VICTORY,
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(result.ordinal());
    }

    public static GameOverMessage parse(DataInputStream in) throws IOException {
        var result = Result.values()[in.readInt()];
        return new GameOverMessage(result);
    }
}
