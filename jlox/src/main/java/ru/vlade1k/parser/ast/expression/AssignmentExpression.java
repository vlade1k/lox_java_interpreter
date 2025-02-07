package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

public class AssignmentExpression extends Expression {
  private final Token name;
  private final Expression value;

  public AssignmentExpression(Token name, Expression expression) {
    this.name = name;
    this.value = expression;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAssignment(this);
  }

  public Token getName() {
    return name;
  }

  public Expression getValue() {
    return value;
  }
}
