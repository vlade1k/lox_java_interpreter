package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;
import ru.vlade1k.scanner.token.Token;

import java.util.List;

public class FunctionDeclarationStatement extends Statement {
  private final Token functionName;
  private final List<Expression> arguments;
  private final List<Statement> body;

  public FunctionDeclarationStatement(Token functionName, List<Expression> arguments, List<Statement> body) {
    this.functionName = functionName;
    this.arguments = arguments;
    this.body = body;
  }

  public Token getFunctionName() {
    return functionName;
  }

  public List<Expression> getArguments() {
    return arguments;
  }

  public List<Statement> getBody() {
    return body;
  }

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitFunctionDeclarationStatement(this);
  }
}
