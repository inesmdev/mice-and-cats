package foop.message;

import foop.world.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record EntityUpdateMessage(int id, String name, Position position, boolean isUnderground, boolean isDead) implements Message {
    public static final int TAG = 8;

    public static EntityUpdateMessage parse(DataInputStream in) throws IOException {
        var id = in.readInt();
        var name = in.readUTF();
        var position = new Position(in.readInt(), in.readInt());
        var isUnderground = in.readBoolean();
        var isDead = in.readBoolean();
        return new EntityUpdateMessage(id, name, position, isUnderground, isDead);
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeUTF(name);
        out.writeInt(position.x());
        out.writeInt(position.y());
        out.writeBoolean(isUnderground);
        out.writeBoolean(isDead);
    }

}
