package ru.vlade1k.parser.ast.visitor;

import ru.vlade1k.parser.ast.expression.AssignmentExpression;
import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.CallExpression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.LogicalExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;
import ru.vlade1k.parser.ast.expression.VariableExpression;

public interface ExpressionVisitor<R> {
  R visitBinary(BinaryExpression binaryExpression);
  R visitGrouping(GroupingExpression groupingExpression);
  R visitUnary(UnaryExpression unaryExpression);
  R visitLiteral(LiteralExpression expression);
  R visitVariable(VariableExpression expression);
  R visitAssignment(AssignmentExpression expression);
  R visitLogical(LogicalExpression expression);
  R visitCall(CallExpression expression);
}
