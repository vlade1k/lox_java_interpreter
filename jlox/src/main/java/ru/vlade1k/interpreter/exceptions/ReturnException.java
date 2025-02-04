package ru.vlade1k.interpreter.exceptions;

public class ReturnException extends RuntimeException {
  private final Object value;

  public ReturnException(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return this.value;
  }
}
