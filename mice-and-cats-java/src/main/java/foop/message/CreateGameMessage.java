package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;

public record CreateGameMessage(String name) implements Message {
    public static final int TAG = 1;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public static CreateGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        return new CreateGameMessage(name);
    }

}
