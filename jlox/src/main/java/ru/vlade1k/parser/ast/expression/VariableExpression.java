package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

public class VariableExpression extends Expression {
  private Token name;

  public VariableExpression(Token name) {
    this.name = name;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitVariable(this);
  }

  public Token getName() {
    return this.name;
  }
}
