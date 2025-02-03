package ru.vlade1k.interpreter.callable;

import ru.vlade1k.interpreter.Environment;
import ru.vlade1k.interpreter.Interpreter;
import ru.vlade1k.parser.ast.expression.VariableExpression;
import ru.vlade1k.parser.ast.statements.FunctionDeclarationStatement;

import java.util.List;

public class LoxFunction implements LoxCallable {
  private final FunctionDeclarationStatement declaration;

  public LoxFunction(FunctionDeclarationStatement declaration) {
    this.declaration = declaration;
  }

  @Override
  public int getArgumentsCount() {
    return declaration.getArguments().size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment functionEnvironment = new Environment(interpreter.getGlobalEnvironment());

    for (int i = 0; i < arguments.size(); i++) {
      functionEnvironment.define(((VariableExpression)declaration.getArguments().get(i)).getName().getLexeme(),
                                 arguments.get(i));
    }

    interpreter.executeBlock(declaration.getBody(), functionEnvironment);
    return null;
  }
}
