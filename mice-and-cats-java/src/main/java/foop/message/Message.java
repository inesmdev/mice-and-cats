package foop.message;

import java.io.*;

public interface Message {
    void serialize(DataOutputStream out) throws IOException;

    /**
     * @return The value of the static field TAG on the class that can parse this message.
     */
    int tag();

    static void serialize(Message message, OutputStream out) throws IOException {
        var bodyByteArrayOutputStream = new ByteArrayOutputStream();
        var bodyDataOutputStream = new DataOutputStream(bodyByteArrayOutputStream);
        message.serialize(bodyDataOutputStream);

        var dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeInt(message.tag());
        dataOutputStream.writeInt(bodyDataOutputStream.size());
        bodyByteArrayOutputStream.writeTo(out);
    }

    static Message parse(InputStream in) throws IOException {
        var dataInputStream = new DataInputStream(in);

        int tag = dataInputStream.readInt();
        ParserFunction parser = switch (tag) {
            case AvailableGamesMessage.TAG -> AvailableGamesMessage::parse;
            case CreateGameMessage.TAG -> CreateGameMessage::parse;
            case GenericResponseMessage.TAG -> GenericResponseMessage::parse;
            case InitialMessage.TAG -> InitialMessage::parse;
            case JoinGameMessage.TAG -> JoinGameMessage::parse;
            case SetReadyForGameMessage.TAG -> SetReadyForGameMessage::parse;
            default -> throw new IOException("Unexpected tag: " + tag);
        };

        int size = dataInputStream.readInt();
        byte[] data = new byte[size];
        dataInputStream.readFully(data);

        DataInputStream bodyStream = new DataInputStream(new ByteArrayInputStream(data));
        var result = parser.parse(bodyStream);
        if (bodyStream.available() != 0) {
            throw new IOException("Parser did not fully consume the message body.");
        }

        return result;
    }

    default <T> T into(Class<T> c) throws IOException {
        if (c.isInstance(this)) {
            return (T) this;
        } else {
            throw new IOException("Expected " + c.getName() + " but got " + this);
        }
    }
}
