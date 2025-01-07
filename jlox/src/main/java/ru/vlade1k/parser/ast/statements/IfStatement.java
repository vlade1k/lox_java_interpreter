package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public class IfStatement extends Statement {
  private final Expression condition;
  private final Statement ifBranch;
  private final Statement elseBranch;

  public IfStatement(Expression condition, Statement ifBranch, Statement elseBranch) {
    this.condition = condition;
    this.ifBranch = ifBranch;
    this.elseBranch = elseBranch;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIfStatement(this);
  }

  public Expression getCondition() {
    return condition;
  }

  public Statement getIfBranch() {
    return ifBranch;
  }

  public Statement getElseBranch() {
    return elseBranch;
  }
}
