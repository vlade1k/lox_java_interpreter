package ru.vlade1k.interpreter;

import ru.vlade1k.JLoxInterpreter;
import ru.vlade1k.interpreter.callable.LoxCallable;
import ru.vlade1k.interpreter.callable.LoxFunction;
import ru.vlade1k.interpreter.exceptions.ReturnException;
import ru.vlade1k.interpreter.exceptions.RuntimeLoxException;
import ru.vlade1k.parser.ast.expression.AssignmentExpression;
import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.CallExpression;
import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.LogicalExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;
import ru.vlade1k.parser.ast.expression.VariableExpression;
import ru.vlade1k.parser.ast.statements.FunctionDeclarationStatement;
import ru.vlade1k.parser.ast.statements.IfStatement;
import ru.vlade1k.parser.ast.statements.ReturnStatement;
import ru.vlade1k.parser.ast.statements.Statement;
import ru.vlade1k.parser.ast.statements.BlockStatement;
import ru.vlade1k.parser.ast.statements.ExpressionStatement;
import ru.vlade1k.parser.ast.statements.PrintStatement;
import ru.vlade1k.parser.ast.statements.VarStatement;
import ru.vlade1k.parser.ast.statements.WhileStatement;
import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {
  private final Environment globalEnvironment = new Environment();
  private Environment environment = globalEnvironment;

  public Interpreter() {
    globalEnvironment.define("clock", new LoxCallable() {
      @Override
      public int getArgumentsCount() {
        return 0;
      }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000;
      }

      @Override
      public String toString() {
        return "<native function>";
      }
    });
  }

  public void interpret(List<Statement> statements) {
    try {
      for (Statement statement : statements) {
        execute(statement);
      }
    } catch (RuntimeLoxException error) {
      JLoxInterpreter.runtimeError(error);
    }
  }

  private void execute(Statement stmt) {
    stmt.accept(this);
  }

  private Object evaluate(Expression expression) {
    return expression.accept(this);
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
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left <= (double) right;
      case MINUS:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left - (double) right;
      case SLASH:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(binaryExpression.getOperator(), left, right);
        return (double) left * (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return left + (String) right;
        }
        if (left instanceof String && right instanceof Number) {
          return (String)left + right;
        }
        if (left instanceof Number && right instanceof String) {
          return left + (String) right;
        }
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitVariable(VariableExpression expression) {
    return environment.get(expression.getName());
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
        return -(double) right;
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitLiteral(LiteralExpression expression) {
    return expression.getValue();
  }

  @Override
  public Object visitAssignment(AssignmentExpression expression) {
    Expression value = expression.getValue();
    environment.assign(expression.getName(), evaluate(value));
    return value;
  }

  @Override
  public Object visitLogical(LogicalExpression expression) {
    Object left = evaluate(expression.getLeft());

    if (expression.getOperator().getType() == TokenType.OR) {
      if (isTruthy(left)) {
        return left;
      }
    } else {
      if (!isTruthy(left)) {
        return left;
      }
    }

    return evaluate(expression.getRight());
  }

  @Override
  public Object visitCall(CallExpression callExpression) {
    Object callee = evaluate(callExpression.getCallee());

    List<Object> arguments = new ArrayList<>();
    for (Expression param : callExpression.getParams()) {
      arguments.add(evaluate(param));
    }

    if (!(callee instanceof LoxCallable function)) {
      throw new RuntimeLoxException(callExpression.getParen(), "Can only call functions and classes.");
    }

    if (arguments.size() != function.getArgumentsCount()) {
      throw new RuntimeLoxException(callExpression.getParen(), "Expected "
                                                              + function.getArgumentsCount()
                                                              + " arguments, but got " + arguments.size());
    }

    return function.call(this, arguments);
  }

  @Override
  public Void visitVarStatement(VarStatement statementVar) {
    Object value = null;
    if (statementVar.getValue() != null) {
      value = evaluate(statementVar.getValue());
    }

    environment.define(statementVar.getName().getLexeme(), value);
    return null;
  }

  @Override
  public Void visitBlockStatement(BlockStatement statementBlock) {
    executeBlock(statementBlock.getStatements(), new Environment(environment));
    return null;
  }

  @Override
  public Void visitStatementExpression(ExpressionStatement statement) {
    evaluate(statement.getValue());
    return null;
  }

  @Override
  public Void visitPrintStatement(PrintStatement statementPrint) {
    Object value = evaluate(statementPrint.getValue());
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visitIfStatement(IfStatement ifStatement) {
    if (isTruthy(evaluate(ifStatement.getCondition()))) {
      execute(ifStatement.getIfBranch());
    } else if (ifStatement.getElseBranch() != null) {
      execute(ifStatement.getElseBranch());
    }

    return null;
  }

  @Override
  public Void visitWhileStatement(WhileStatement whileStatement) {
    while (isTruthy(evaluate(whileStatement.getCondition()))) {
      execute(whileStatement.getBody());
    }

    return null;
  }

  @Override
  public Void visitFunctionDeclarationStatement(FunctionDeclarationStatement funcDeclarationStatement) {
    LoxFunction function = new LoxFunction(funcDeclarationStatement, environment);
    environment.define(funcDeclarationStatement.getFunctionName().getLexeme(), function);
    return null;
  }

  @Override
  public Void visitReturnStatement(ReturnStatement returnStatement) throws ReturnException {
    Object value = null;
    if (returnStatement.getValue() != null) {
      value = evaluate(returnStatement.getValue());
    }

    throw new ReturnException(value);
  }

  public void executeBlock(List<Statement> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for (Statement statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  private boolean isTruthy(Object object) {
    if (object == null) {
      return false;
    }
    if (object instanceof Boolean) {
      return (boolean) object;
    }
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null) {
      return false;
    }

    return a.equals(b);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) {
      return;
    }
    throw new RuntimeLoxException(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }

    throw new RuntimeLoxException(operator, "Operands must be numbers.");
  }

  private String stringify(Object object) {
    if (object == null) {
      return "nil";
    }

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