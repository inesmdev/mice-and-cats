import foop.message.AvailableGamesMessage;
import foop.message.CreateGameMessage;
import foop.message.JoinGameMessage;
import foop.message.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTests {

    static Stream<Message> messages() throws IOException {
        return Stream.of(
                new AvailableGamesMessage(List.of(
                        new AvailableGamesMessage.Game("test1", Duration.ofSeconds(1, 2)),
                        new AvailableGamesMessage.Game("test2", Duration.ofSeconds(3, 4))
                )),
                new CreateGameMessage("test", Duration.ofSeconds(1, 2)),
                new JoinGameMessage("test")
        );
    }

    @ParameterizedTest
    @MethodSource("messages")
    void serializeAndParse(Message message) throws IOException {
        var out = new ByteArrayOutputStream();
        Message.serialize(message, out);

        var in = new ByteArrayInputStream(out.toByteArray());
        var parsed = Message.parse(in);

        assertEquals(message, parsed);
    }
}
