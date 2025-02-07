package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.visitor.StatementVisitor;

import java.util.List;

public class BlockStatement extends Statement {

  private final List<Statement> statements;

  public BlockStatement(List<Statement> statements) {
    this.statements = statements;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitBlockStatement(this);
  }

  public List<Statement> getStatements() {
    return statements;
  }
}
