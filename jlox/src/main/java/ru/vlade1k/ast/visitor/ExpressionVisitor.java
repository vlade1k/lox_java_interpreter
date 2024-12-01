package ru.vlade1k.ast.visitor;

import ru.vlade1k.ast.BinaryExpression;
import ru.vlade1k.ast.GroupingExpression;

public interface ExpressionVisitor<R> {
  R visitBinary(BinaryExpression expression);
  R visitGrouping(GroupingExpression expression);
}
