package ru.vlade1k.interpreter.callable;

import ru.vlade1k.interpreter.Environment;
import ru.vlade1k.interpreter.Interpreter;
import ru.vlade1k.interpreter.exceptions.ReturnException;
import ru.vlade1k.parser.ast.expression.VariableExpression;
import ru.vlade1k.parser.ast.statements.FunctionDeclarationStatement;

import java.util.List;

public class LoxFunction implements LoxCallable {
  private final FunctionDeclarationStatement declaration;
  private final Environment closure;

  public LoxFunction(FunctionDeclarationStatement declaration, Environment closure) {
    this.declaration = declaration;
    this.closure = closure;
  }

  @Override
  public int getArgumentsCount() {
    return declaration.getArguments().size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment functionEnvironment = new Environment(closure);

    for (int i = 0; i < arguments.size(); i++) {
      functionEnvironment.define(((VariableExpression)declaration.getArguments().get(i)).getName().getLexeme(),
                                 arguments.get(i));
    }
    try {
      interpreter.executeBlock(declaration.getBody(), functionEnvironment);
    } catch (ReturnException returnEx) {
      return returnEx.getValue();
    }
    return null;
  }
}
