package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record CreateGameMessage(String name, int minPlayer, int durationSec) implements Message {
    public static final int TAG = 1;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeInt(minPlayer);
        out.writeInt(durationSec);
    }

    public static CreateGameMessage parse(DataInputStream in) throws IOException {
        var name = in.readUTF();
        var minPlayer = in.readInt();
        var durationSec = in.readInt();
        return new CreateGameMessage(name, minPlayer, durationSec);
    }

}
