package ru.vlade1k.util;

import static ru.vlade1k.token.TokenType.BANG;
import static ru.vlade1k.token.TokenType.BANG_EQUAL;
import static ru.vlade1k.token.TokenType.COMMA;
import static ru.vlade1k.token.TokenType.DOT;
import static ru.vlade1k.token.TokenType.EOF;
import static ru.vlade1k.token.TokenType.EQUAL;
import static ru.vlade1k.token.TokenType.EQUAL_EQUAL;
import static ru.vlade1k.token.TokenType.GREATER;
import static ru.vlade1k.token.TokenType.GREATER_EQUAL;
import static ru.vlade1k.token.TokenType.LEFT_BRACE;
import static ru.vlade1k.token.TokenType.LEFT_PAREN;
import static ru.vlade1k.token.TokenType.LESS;
import static ru.vlade1k.token.TokenType.LESS_EQUAL;
import static ru.vlade1k.token.TokenType.MINUS;
import static ru.vlade1k.token.TokenType.PLUS;
import static ru.vlade1k.token.TokenType.RIGHT_BRACE;
import static ru.vlade1k.token.TokenType.RIGHT_PAREN;
import static ru.vlade1k.token.TokenType.SEMICOLON;
import static ru.vlade1k.token.TokenType.SLASH;
import static ru.vlade1k.token.TokenType.STAR;
import static ru.vlade1k.token.TokenType.STRING;

import ru.vlade1k.JLoxInterpreter;
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

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private char advance() {
    return source.charAt(current++);
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
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;
      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;
      case '"': string(); break;
      default:
        JLoxInterpreter.error(line, "Unexpected character");
        break;
    }
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      JLoxInterpreter.error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }
}
