package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.interpreter.exceptions.ReturnException;
import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;
import ru.vlade1k.scanner.token.Token;

public class ReturnStatement extends Statement {
  private final Token keyword;
  private final Expression value;

  public ReturnStatement(Token keyword, Expression value) {
    this.keyword = keyword;
    this.value = value;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) throws ReturnException {
    return visitor.visitReturnStatement(this);
  }
}
