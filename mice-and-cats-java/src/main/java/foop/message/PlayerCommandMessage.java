package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record PlayerCommandMessage(int direction) implements Message {

    public static final int TAG = 10;

    public static PlayerCommandMessage parse(DataInputStream in) throws IOException {
        var direction = in.readInt();
        return new PlayerCommandMessage(direction);
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(direction);
    }
}

