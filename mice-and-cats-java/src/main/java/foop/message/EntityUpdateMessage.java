package foop.message;

import foop.world.Entity;
import foop.world.Position;
import foop.world.Type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record EntityUpdateMessage(Entity entity) implements Message {
    public static final int TAG = 8;

    public static EntityUpdateMessage parse(DataInputStream in) throws IOException {
        var id = in.readInt();
        var type = Type.valueOf(in.readUTF());
        var name = in.readUTF();
        var position = new Position(in.readInt(), in.readInt());
        var isUnderground = in.readBoolean();
        var isDead = in.readBoolean();
        return new EntityUpdateMessage(new Entity(id, type, name, position, isUnderground, isDead));
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(entity().getId());
        out.writeUTF(entity().getType().name());
        out.writeUTF(entity().getName());
        out.writeInt(entity.getPosition().x());
        out.writeInt(entity.getPosition().y());
        out.writeBoolean(entity.isUnderground());
        out.writeBoolean(entity.isDead());
    }

}
