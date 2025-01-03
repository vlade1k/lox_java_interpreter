package ru.vlade1k.token;

public class Token {
  private final TokenType type;
  private final String lexeme;
  private final Object literal;
  private final int line;

  public Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  @Override
  public String toString() {
    return type + " " + lexeme + " " + literal;
  }

  public TokenType getType() {
    return type;
  }

  public Object getLiteral() {
    return literal;
  }

  public int getLine() {
    return line;
  }

  public String getLexeme() {
    return this.lexeme;
  }
}
