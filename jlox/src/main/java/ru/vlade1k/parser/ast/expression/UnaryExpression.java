package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

public class UnaryExpression extends Expression {
  private final Token operator;
  private final Expression right;

  public UnaryExpression(Token operator, Expression right) {
    this.operator = operator;
    this.right = right;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnary(this);
  }

  public Expression getRight() {
    return right;
  }

  public Token getOperator() {
    return operator;
  }
}
