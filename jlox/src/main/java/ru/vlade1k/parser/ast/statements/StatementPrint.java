package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class StatementPrint extends Statement {
  private final Expression value;

  public StatementPrint(Expression value) {
    this.value = value;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitPrintStatement(this);
  }

  public Expression getValue() {
    return value;
  }
}
