import foop.message.*;
import foop.world.Position;
import foop.world.Subway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.*;
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
                        new AvailableGamesMessage.Game("test1", Duration.ofSeconds(1, 2), List.of(new AvailableGamesMessage.PlayerInfo("a", true), new AvailableGamesMessage.PlayerInfo("b", false)), true),
                        new AvailableGamesMessage.Game("test2", Duration.ofSeconds(3, 4), List.of(), false)
                )),
                new CreateGameMessage("test", 2),
                new EntityUpdateMessage(42, "cat", new Position(1, 2), false, true),
                new GameWorldMessage(new int[][]{new int[]{1, 2, 3}, new int[]{4, 5, 6}}, List.of(new Subway(1, Color.RED, List.of(new Position(1, 2), new Position(3, 4)), List.of(new Position(1, 2), new Position(3, 4))))),
                new GenericResponseMessage("test", true),
                new InitialMessage("test"),
                new JoinGameMessage("test"),
                new SetReadyForGameMessage(true),
                new SetReadyForGameMessage(false),
                new GameOverMessage(GameOverMessage.Result.ALL_BUT_YOU_DIED),
                new PlayerCommandMessage(1)
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
