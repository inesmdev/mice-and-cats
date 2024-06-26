package foop.message;

import foop.server.PlayerId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record InitialMessage(PlayerId id) implements Message {
    public static final int TAG = 15;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        id.serialize(out);
    }

    public static InitialMessage parse(DataInputStream in) throws IOException {
        var id = PlayerId.parse(in);
        return new InitialMessage(id);
    }
}
