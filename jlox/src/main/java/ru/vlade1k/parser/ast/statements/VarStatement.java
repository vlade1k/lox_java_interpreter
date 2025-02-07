package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;
import ru.vlade1k.scanner.token.Token;

public class VarStatement extends Statement {
  private final Expression initializer;
  private final Token name;

  public VarStatement(Expression initializer, Token name) {
    this.initializer = initializer;
    this.name = name;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitVarStatement(this);
  }

  public Token getName() {
    return name;
  }

  public Expression getValue() {
    return initializer;
  }
}
