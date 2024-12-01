package ru.vlade1k.ast;

import ru.vlade1k.ast.visitor.ExpressionVisitor;

public class GroupingExpression extends Expression{
  private Expression expression;

  public GroupingExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitGrouping(this);
  }
}
