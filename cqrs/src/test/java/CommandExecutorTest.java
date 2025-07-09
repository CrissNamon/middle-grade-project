import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.cqrs.command.CommandExecutor;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.cqrs.command.impl.CommandExecutorImpl;

public class CommandExecutorTest {

  private final CommandExecutor commandExecutor = new CommandExecutorImpl(
      List.of(new ThrowErrorCommandHandler())
  );

  @Test
  public void itShouldExecuteCommandSuccessfully() {
    Integer input = 1;
    Assertions.assertThrows(RuntimeException.class, () -> {
      commandExecutor.execute(new ThrowErrorCommand(input));
    });
  }

  public static class ThrowErrorCommandHandler implements CommandHandler<Integer, ThrowErrorCommand> {

    @Override
    public Void handle(ThrowErrorCommand query) {
      throw new RuntimeException(query.id.toString());
    }

    @Override
    public Class<ThrowErrorCommand> getType() {
      return ThrowErrorCommand.class;
    }
  }

  public record ThrowErrorCommand(Integer id) implements Command<Integer> {}

}
