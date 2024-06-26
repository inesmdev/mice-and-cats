package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record TimeUpdateMessage(long durationMills) implements Message{

    public static final int TAG = 14;

    public static TimeUpdateMessage parse(DataInputStream in) throws IOException {
        var durationMillis = in.readLong();
        return new TimeUpdateMessage(durationMillis);
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeLong(durationMills);
    }

    @Override
    public int tag() {
        return TAG;
    }
}
