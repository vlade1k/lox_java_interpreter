package ru.vlade1k.ast.expression;

import ru.vlade1k.ast.visitor.ExpressionVisitor;

public class LiteralExpression extends Expression {
  private Object value;

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitLiteral(this);
  }
}