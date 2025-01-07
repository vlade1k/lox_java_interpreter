package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class StatementPrint extends Statement {
  public StatementPrint(Expression value) {
    super(value);
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitPrintStatement(this);
  }
}
