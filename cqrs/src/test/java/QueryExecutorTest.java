import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.danilarassokhin.cqrs.query.Query;
import ru.danilarassokhin.cqrs.query.QueryExecutor;
import ru.danilarassokhin.cqrs.query.QueryHandler;
import ru.danilarassokhin.cqrs.query.impl.QueryExecutorImpl;

public class QueryExecutorTest {

  private final QueryExecutor queryExecutor = new QueryExecutorImpl(
      List.of(new FindByIdQueryHandler())
  );

  @Test
  public void itShouldExecuteQueryAndReturnResult() {
    Integer input = 1;
    var result = queryExecutor.execute(new FindByIdQuery(input));
    Assertions.assertEquals(input.toString(), result);
  }

  public static class FindByIdQueryHandler implements QueryHandler<Integer, String, FindByIdQuery> {

    @Override
    public String handle(FindByIdQuery query) {
      return query.id.toString();
    }

    @Override
    public Class<FindByIdQuery> getType() {
      return FindByIdQuery.class;
    }
  }

  public record FindByIdQuery(Integer id) implements Query<Integer, String> {}

}
