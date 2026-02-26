package studio.meraki.vynapi.model.statement.wait;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import studio.meraki.vynapi.handler.client.BackgroundLoopHandler;

import java.util.List;

public final class WaitStatement implements Statement {

    private final Expression timeExpr;
    private final List<Statement> body;

    public WaitStatement(final Expression timeExpr, final List<Statement> body) {
        this.body = body;
        this.timeExpr = timeExpr;
    }

    @Override
    public void execute(final Environment environment) {
        final int time = timeExpr.evaluate(environment).asInt();
        BackgroundLoopHandler.waitTicks("wait_" + time, time, () -> {
            for (final Statement statement : body)
                statement.execute(environment);
        });
    }
}