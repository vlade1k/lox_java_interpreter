package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class StatementExpression extends Statement {
  public StatementExpression(Expression value) {
    super(value);
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitStatementExpression(this);
  }
}
