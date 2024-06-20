package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record GenericErrorMessage(String message) implements Message {
    public static final int TAG = 4;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(message);
    }

    public static GenericErrorMessage parse(DataInputStream in) throws IOException {
        var message = in.readUTF();
        return new GenericErrorMessage(message);
    }
}
