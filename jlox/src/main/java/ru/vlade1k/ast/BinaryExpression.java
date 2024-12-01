package ru.vlade1k.ast;

import ru.vlade1k.ast.visitor.ExpressionVisitor;
import ru.vlade1k.token.Token;

public class BinaryExpression extends Expression {
  private final Expression left;
  private final Token operator;
  private final Expression right;

  public BinaryExpression(Expression left, Token operator, Expression right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitBinary(this);
  }
}
