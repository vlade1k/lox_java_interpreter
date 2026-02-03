package ru.vlade1k.scanner.token;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Token other)) {
      return false;
    }

    if (other == this) {
      return true;
    }

    return Objects.equals(this.type, other.type)
        && Objects.equals(this.lexeme, other.lexeme)
        && Objects.equals(this.literal, other.literal)
        && Objects.equals(this.line, other.line);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type) + Objects.hashCode(lexeme) + Objects.hashCode(literal) + Objects.hashCode(line);
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
