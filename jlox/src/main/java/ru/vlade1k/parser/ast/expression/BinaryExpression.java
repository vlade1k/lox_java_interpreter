package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

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
