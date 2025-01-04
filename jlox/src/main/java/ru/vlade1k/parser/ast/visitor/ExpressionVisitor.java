package ru.vlade1k.parser.ast.visitor;

import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;

public interface ExpressionVisitor<R> {
  R visitBinary(BinaryExpression binaryExpression);
  R visitGrouping(GroupingExpression groupingExpression);
  R visitUnary(UnaryExpression unaryExpression);
  R visitLiteral(LiteralExpression expression);
}
