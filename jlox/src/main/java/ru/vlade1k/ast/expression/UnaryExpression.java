package ru.vlade1k.ast.expression;

import ru.vlade1k.ast.visitor.ExpressionVisitor;
import ru.vlade1k.token.Token;

public class UnaryExpression extends Expression {
  private Token operator;
  private Expression right;

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnary(this);
  }
}