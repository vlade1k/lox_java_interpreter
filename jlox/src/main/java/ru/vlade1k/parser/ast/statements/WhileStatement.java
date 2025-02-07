package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class WhileStatement extends Statement {
  private final Expression condition;
  private final Statement body;

  public WhileStatement(Expression condition, Statement body) {
    this.condition = condition;
    this.body = body;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitWhileStatement(this);
  }

  public Expression getCondition() {
    return condition;
  }

  public Statement getBody() {
    return body;
  }
}
