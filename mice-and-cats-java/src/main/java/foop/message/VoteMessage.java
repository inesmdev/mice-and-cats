package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record VoteMessage(int vote) implements Message {

    public static final int TAG = 13;

    public static VoteMessage parse(DataInputStream in) throws IOException {
        var vote = in.readInt();
        return new VoteMessage(vote);
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(vote);
    }
}
