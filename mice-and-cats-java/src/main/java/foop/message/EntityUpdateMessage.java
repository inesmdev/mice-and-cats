package foop.message;

import foop.world.Entity;
import foop.world.Position;
import foop.world.Type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record EntityUpdateMessage(int id, Type type, String name, Position position, boolean isUnderground, boolean isDead) implements Message {

    public static final int TAG = 8;

    public EntityUpdateMessage(Entity entity) {
        this(entity.getId(), entity.getType(), entity.getName(), entity.getPosition(), entity.isUnderground(), entity.isDead());
    }

    public static EntityUpdateMessage parse(DataInputStream in) throws IOException {
        var id = in.readInt();
        var type = Type.valueOf(in.readUTF());
        var name = in.readUTF();
        var position = new Position(in.readInt(), in.readInt());
        var isUnderground = in.readBoolean();
        var isDead = in.readBoolean();
        return new EntityUpdateMessage(id, type, name, position, isUnderground, isDead);
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeUTF(type.name());
        out.writeUTF(name);
        out.writeInt(position.x());
        out.writeInt(position.y());
        out.writeBoolean(isUnderground);
        out.writeBoolean(isDead);
    }

}
