package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record SetReadyForGameMessage(boolean ready) implements Message {
    public static final int TAG = 5;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeBoolean(ready);
    }

    public static SetReadyForGameMessage parse(DataInputStream in) throws IOException {
        var ready = in.readBoolean();
        return new SetReadyForGameMessage(ready);
    }
}
