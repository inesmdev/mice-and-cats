package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record GenericResponseMessage(String message, boolean error) implements Message {
    public static final int TAG = 4;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(message);
        out.writeBoolean(error);
    }

    public static GenericResponseMessage parse(DataInputStream in) throws IOException {
        var message = in.readUTF();
        var error = in.readBoolean();
        return new GenericResponseMessage(message, error);
    }
}
