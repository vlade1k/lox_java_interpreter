package ru.vlade1k.interpreter.exceptions;

import ru.vlade1k.scanner.token.Token;

public class RuntimeLoxException extends RuntimeException {
  private final Token token;

  public RuntimeLoxException(Token token, String message) {
    super(message);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}
