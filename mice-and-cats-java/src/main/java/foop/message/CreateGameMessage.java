package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;

public record CreateGameMessage(String name, Duration duration) implements Message {
    public static final int TAG = 1;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeLong(duration.getSeconds());
        out.writeInt(duration.getNano());
    }

    public static CreateGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        var seconds = in.readLong();
        var nano = in.readInt();
        var duration = Duration.ofSeconds(seconds, nano);

        return new CreateGameMessage(name, duration);
    }

}
