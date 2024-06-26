package foop.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public record PlayerId(long first, long second) {
    public static PlayerId makeRandom() {
        var r = new SecureRandom();
        return new PlayerId(r.nextLong(), r.nextLong());
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeLong(first);
        out.writeLong(second);
    }

    public static PlayerId parse(DataInputStream in) throws IOException {
        long first = in.readLong();
        long second = in.readLong();
        return new PlayerId(first, second);
    }

    @Override
    public String toString() {
        return "PlayerId(" + first + "," + second + ')';
    }
}
