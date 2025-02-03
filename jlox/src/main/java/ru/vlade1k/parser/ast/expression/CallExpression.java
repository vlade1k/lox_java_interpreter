package ru.vlade1k.parser.ast.expression;

import ru.vlade1k.parser.ast.visitor.ExpressionVisitor;
import ru.vlade1k.scanner.token.Token;

import java.util.List;

public class CallExpression extends Expression {
  private final Expression callee;
  private final Token paren;
  private final List<Expression> params;

  public CallExpression(Expression callee, Token paren, List<Expression> params) {
    this.callee = callee;
    this.paren = paren;
    this.params = params;
  }

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitCall(this);
  }

  public Expression getCallee() {
    return callee;
  }

  public List<Expression> getParams() {
    return params;
  }

  public Token getParen() {
    return paren;
  }
}
