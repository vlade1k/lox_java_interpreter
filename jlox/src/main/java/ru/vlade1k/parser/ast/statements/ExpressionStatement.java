package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class ExpressionStatement extends Statement {
  private final Expression value;

  public ExpressionStatement(Expression value) {
    this.value = value;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitStatementExpression(this);
  }
}
