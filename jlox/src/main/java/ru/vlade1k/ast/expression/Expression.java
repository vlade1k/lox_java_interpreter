package ru.vlade1k.ast.expression;

import ru.vlade1k.ast.visitor.ExpressionVisitor;

public abstract class Expression {
  public abstract <R> R accept(ExpressionVisitor<R> visitor);
}
