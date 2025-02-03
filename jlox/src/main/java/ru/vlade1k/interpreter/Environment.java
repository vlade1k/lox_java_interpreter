package ru.vlade1k.interpreter;

import ru.vlade1k.interpreter.exceptions.RuntimeLoxException;
import ru.vlade1k.scanner.token.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
  private final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();

  public Environment() {
    enclosing = null;
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  /**
   * Добавляет объекты в среду.
   *
   * @param name    имя добавляемого объекта
   * @param value   значение, которое ассоциируется с именем
   */
  public void define(String name, Object value) {
    values.put(name, value);
  }

  /**
   * По имени объекта возвращает его значение.
   *
   * @param name    имя добавляемого объекта
   */
  public Object get(Token name) {
    if (values.containsKey(name.getLexeme())) {
      return values.get(name.getLexeme());
    }

    if (enclosing != null) {
      return enclosing.get(name);
    }

    throw new RuntimeLoxException(name, "Undefined variable '" + name.getLexeme() + "'.");
  }

  /**
   * Переприсваивание. Т.е., объектам в среде назначается новое значение
   *
   * @param name    имя объекта, чьё значение переопределяется
   * @param value   переприсваиваемое значение
   */
  public void assign(Token name, Object value) {
    if (values.containsKey(name.getLexeme())) {
      values.put(name.getLexeme(), value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeLoxException(name,
        "Undefined variable '" + name.getLexeme() + "'.");
  }
}
