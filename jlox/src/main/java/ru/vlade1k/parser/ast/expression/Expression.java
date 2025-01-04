package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;

public abstract class Expression {
  public abstract <R> R accept(ExpressionVisitor<R> visitor);
}
