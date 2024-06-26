package foop.message;

import java.io.*;

/**
 * Subclasses of this interface represent the messages that are exchanged for network communication.
 * They should be immutable records, so that they can be passed to another thread for serialization.
 * The network representation is a simple binary format, that is implemented manually in a parse
 * and in a serialize method.
 * <br>
 * <b>All subclasses must have a unique integer TAG and they must be added to the switch statement
 * in the static {@link #parse(InputStream)} method</b>
 */
public interface Message {
    /**
     * Writes the tag, the body size and message specific body.
     *
     * @param message The message to serialize.
     * @param out     The stream to write into.
     * @throws IOException If writing to the stream fails.
     */
    static void serialize(Message message, OutputStream out) throws IOException {
        var bodyByteArrayOutputStream = new ByteArrayOutputStream();
        var bodyDataOutputStream = new DataOutputStream(bodyByteArrayOutputStream);
        message.serialize(bodyDataOutputStream);

        var dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeInt(message.tag());
        dataOutputStream.writeInt(bodyDataOutputStream.size());
        bodyByteArrayOutputStream.writeTo(out);
    }

    /**
     * Reads a message from an input stream.
     *
     * @param in The stream to read from.
     * @return The parsed message.
     * @throws IOException If reading from the stream fails.
     */
    static Message parse(InputStream in) throws IOException {
        var dataInputStream = new DataInputStream(in);

        int tag = dataInputStream.readInt();
        ParserFunction parser = switch (tag) {
            case AvailableGamesMessage.TAG -> AvailableGamesMessage::parse;
            case CreateGameMessage.TAG -> CreateGameMessage::parse;
            case EntityUpdateMessage.TAG -> EntityUpdateMessage::parse;
            case GenericErrorMessage.TAG -> GenericErrorMessage::parse;
            case GameWorldMessage.TAG -> GameWorldMessage::parse;
            case PlayerNameMessage.TAG -> PlayerNameMessage::parse;
            case JoinGameMessage.TAG -> JoinGameMessage::parse;
            case SetReadyForGameMessage.TAG -> SetReadyForGameMessage::parse;
            case ExitGameMessage.TAG -> ExitGameMessage::parse;
            case PlayerCommandMessage.TAG -> PlayerCommandMessage::parse;
            case GameOverMessage.TAG -> GameOverMessage::parse;
            case JoinedGameMessage.TAG -> JoinedGameMessage::parse;
            case VoteMessage.TAG -> VoteMessage::parse;
            case TimeUpdateMessage.TAG -> TimeUpdateMessage::parse;
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

    /**
     * <p>
     * Can write arbitrary bytes to out that represent this message, as long as there is
     * a parse method, that can be called from the static {@link #parse(InputStream)}} method,
     * to turn it back into an object that is equal to the initial one.
     * </p>
     * <p>
     * It is not necessary to write the length or tag of the message.
     * </p>
     *
     * @param out Essentially just a more convenient way of constructing and returning a byte array that
     *            represents the body of this message.
     * @throws IOException If writing to out fails.
     */
    void serialize(DataOutputStream out) throws IOException;

    /**
     * The unique tag for this message type.
     *
     * @return The value of the static field TAG on the class that can parse this message.
     */
    int tag();
}
