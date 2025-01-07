package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public abstract class Statement {
  protected Expression value;

  public Statement(Expression expression) {
    this.value = expression;
  }

  public Expression getExpression() {
    return this.value;
  }

  public abstract <R> R accept(StatementVisitor<R> visitor);
}
