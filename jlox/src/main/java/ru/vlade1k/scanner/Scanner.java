package ru.vlade1k.scanner;

import static ru.vlade1k.scanner.token.TokenType.AND;
import static ru.vlade1k.scanner.token.TokenType.BANG;
import static ru.vlade1k.scanner.token.TokenType.BANG_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.CLASS;
import static ru.vlade1k.scanner.token.TokenType.COMMA;
import static ru.vlade1k.scanner.token.TokenType.DOT;
import static ru.vlade1k.scanner.token.TokenType.ELSE;
import static ru.vlade1k.scanner.token.TokenType.EOF;
import static ru.vlade1k.scanner.token.TokenType.EQUAL;
import static ru.vlade1k.scanner.token.TokenType.EQUAL_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.FALSE;
import static ru.vlade1k.scanner.token.TokenType.FOR;
import static ru.vlade1k.scanner.token.TokenType.FUN;
import static ru.vlade1k.scanner.token.TokenType.GREATER;
import static ru.vlade1k.scanner.token.TokenType.GREATER_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.IDENTIFIER;
import static ru.vlade1k.scanner.token.TokenType.IF;
import static ru.vlade1k.scanner.token.TokenType.LEFT_BRACE;
import static ru.vlade1k.scanner.token.TokenType.LEFT_PAREN;
import static ru.vlade1k.scanner.token.TokenType.LESS;
import static ru.vlade1k.scanner.token.TokenType.LESS_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.MINUS;
import static ru.vlade1k.scanner.token.TokenType.NIL;
import static ru.vlade1k.scanner.token.TokenType.NUMBER;
import static ru.vlade1k.scanner.token.TokenType.OR;
import static ru.vlade1k.scanner.token.TokenType.PLUS;
import static ru.vlade1k.scanner.token.TokenType.PRINT;
import static ru.vlade1k.scanner.token.TokenType.RETURN;
import static ru.vlade1k.scanner.token.TokenType.RIGHT_BRACE;
import static ru.vlade1k.scanner.token.TokenType.RIGHT_PAREN;
import static ru.vlade1k.scanner.token.TokenType.SEMICOLON;
import static ru.vlade1k.scanner.token.TokenType.SLASH;
import static ru.vlade1k.scanner.token.TokenType.STAR;
import static ru.vlade1k.scanner.token.TokenType.STRING;
import static ru.vlade1k.scanner.token.TokenType.SUPER;
import static ru.vlade1k.scanner.token.TokenType.THIS;
import static ru.vlade1k.scanner.token.TokenType.TRUE;
import static ru.vlade1k.scanner.token.TokenType.VAR;
import static ru.vlade1k.scanner.token.TokenType.WHILE;

import ru.vlade1k.JLoxInterpreter;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scanner {
  private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
    Map.entry("and",    AND),
    Map.entry("class",  CLASS),
    Map.entry("else",   ELSE),
    Map.entry("false",  FALSE),
    Map.entry("for",    FOR),
    Map.entry("fun",    FUN),
    Map.entry("if",     IF),
    Map.entry("nil",    NIL),
    Map.entry("or",     OR),
    Map.entry("print",  PRINT),
    Map.entry("return", RETURN),
    Map.entry("super",  SUPER),
    Map.entry("this",   THIS),
    Map.entry("true",   TRUE),
    Map.entry("var",    VAR),
    Map.entry("while",  WHILE)
  );

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
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          JLoxInterpreter.error(line, "Unexpected character");
        }
        break;
    }
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

  private void identifier() {
    while(isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    TokenType type = KEYWORDS.get(text);
    if (type == null) {
      type = IDENTIFIER;
    }
    addToken(type);
  }

  private boolean isAlpha(char c) {
    return    (c >= 'a' && c <= 'z')
           || (c >= 'A' && c <= 'Z')
           || (c == '_');
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void number() {
    while(isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())) {
      do {
        advance();
      }
      while (isDigit(peek()));
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    advance();
    return true;
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }
}
