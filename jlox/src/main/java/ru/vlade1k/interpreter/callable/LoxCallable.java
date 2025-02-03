package ru.vlade1k.interpreter.callable;

import ru.vlade1k.interpreter.Interpreter;

import java.util.List;

public interface LoxCallable {
  int getArgumentsCount();
  Object call(Interpreter interpreter, List<Object> arguments);
}
