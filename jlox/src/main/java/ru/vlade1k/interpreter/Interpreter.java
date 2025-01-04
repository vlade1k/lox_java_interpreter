package ru.vlade1k.interpreter;

import ru.vlade1k.JLoxInterpreter;
import ru.vlade1k.interpreter.exceptions.RuntimeLoxException;
import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;
import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

public class Interpreter implements ExpressionVisitor<Object> {

  public void interpret(Expression expression) {
    try {
      Object value = evaluate(expression);
      System.out.println(stringify(value));
    } catch (RuntimeLoxException error) {
      JLoxInterpreter.runtimeError(error);
    }
  }

  @Override
  public Object visitBinary(BinaryExpression binaryExpression) {
    Object left = evaluate(binaryExpression.getLeft());
    Object right = evaluate(binaryExpression.getRight());

    switch (binaryExpression.getOperator().getType()) {
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
      case GREATER:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left > (double)right;
      case GREATER_EQUAL:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left >= (double)right;
      case LESS:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left < (double)right;
      case LESS_EQUAL:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left <= (double)right;
      case MINUS:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left - (double)right;
      case SLASH:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left / (double)right;
      case STAR:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double)left * (double)right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        }
        if (left instanceof String && right instanceof String) {
          return (String)left + (String)right;
        }
        throw new RuntimeLoxException(binaryExpression.getOperator(),
            "Operands must be two numbers or two strings.");
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitGrouping(GroupingExpression groupingExpression) {
    return evaluate(groupingExpression.getExpression());
  }

  @Override
  public Object visitUnary(UnaryExpression unaryExpression) {
    Object right = evaluate(unaryExpression.getRight());

    switch (unaryExpression.getOperator().getType()) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(unaryExpression.getOperator(), right);
        return -(double)right;
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitLiteral(LiteralExpression expression) {
    return expression.getValue();
  }

  private Object evaluate(Expression expr) {
    return expr.accept(this);
  }

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean)object;
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeLoxException(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator,
      Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;

    throw new RuntimeLoxException(operator, "Operands must be numbers.");
  }

  private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }
}