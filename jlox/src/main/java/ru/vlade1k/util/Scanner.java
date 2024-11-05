package ru.vlade1k.util;

import static ru.vlade1k.token.TokenType.COMMA;
import static ru.vlade1k.token.TokenType.DOT;
import static ru.vlade1k.token.TokenType.EOF;
import static ru.vlade1k.token.TokenType.LEFT_BRACE;
import static ru.vlade1k.token.TokenType.LEFT_PAREN;
import static ru.vlade1k.token.TokenType.MINUS;
import static ru.vlade1k.token.TokenType.PLUS;
import static ru.vlade1k.token.TokenType.RIGHT_BRACE;
import static ru.vlade1k.token.TokenType.RIGHT_PAREN;
import static ru.vlade1k.token.TokenType.SEMICOLON;
import static ru.vlade1k.token.TokenType.STAR;

import ru.vlade1k.token.Token;
import ru.vlade1k.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while(!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
    }
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
