package ru.vlade1k;

import static ru.vlade1k.token.TokenType.BANG;
import static ru.vlade1k.token.TokenType.BANG_EQUAL;
import static ru.vlade1k.token.TokenType.EOF;
import static ru.vlade1k.token.TokenType.EQUAL_EQUAL;
import static ru.vlade1k.token.TokenType.FALSE;
import static ru.vlade1k.token.TokenType.GREATER;
import static ru.vlade1k.token.TokenType.GREATER_EQUAL;
import static ru.vlade1k.token.TokenType.LEFT_PAREN;
import static ru.vlade1k.token.TokenType.LESS;
import static ru.vlade1k.token.TokenType.LESS_EQUAL;
import static ru.vlade1k.token.TokenType.MINUS;
import static ru.vlade1k.token.TokenType.NIL;
import static ru.vlade1k.token.TokenType.NUMBER;
import static ru.vlade1k.token.TokenType.PLUS;
import static ru.vlade1k.token.TokenType.RIGHT_PAREN;
import static ru.vlade1k.token.TokenType.SLASH;
import static ru.vlade1k.token.TokenType.STAR;
import static ru.vlade1k.token.TokenType.STRING;
import static ru.vlade1k.token.TokenType.TRUE;

import ru.vlade1k.ast.exceptions.ParseException;
import ru.vlade1k.ast.expression.BinaryExpression;
import ru.vlade1k.ast.expression.Expression;
import ru.vlade1k.ast.expression.GroupingExpression;
import ru.vlade1k.ast.expression.LiteralExpression;
import ru.vlade1k.ast.expression.UnaryExpression;
import ru.vlade1k.token.Token;
import ru.vlade1k.token.TokenType;

import java.util.List;

public class Parser {
  private final List<Token> tokens;
  private int current;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  private Expression expression() {
    return equality();
  }

  private Expression equality() {
    Expression expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expression right = comparison();
      expr = new BinaryExpression(expr, operator, right);
    }

    return expr;
  }

  private Expression comparison() {
    Expression expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expression right = term();
      expr = new BinaryExpression(expr, operator, right);
    }

    return expr;
  }

  private Expression term() {
    Expression expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expression right = factor();
      expr = new BinaryExpression(expr, operator, right);
    }

    return expr;
  }

  private Expression factor() {
    Expression expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expression right = unary();
      expr = new BinaryExpression(expr, operator, right);
    }

    return expr;
  }

  private Expression unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expression right = unary();
      return new UnaryExpression(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    if (match(FALSE)) return new LiteralExpression(false);
    if (match(TRUE)) return new LiteralExpression(true);
    if (match(NIL)) return new LiteralExpression(null);

    if (match(NUMBER, STRING)) {
      return new LiteralExpression(previous().getLiteral());
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new GroupingExpression(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type: types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) {
      return false;
    }
    return peek().getType() == type;
  }

  private boolean isAtEnd() {
    return peek().getType() == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseException error(Token token, String message) {
    JLoxInterpreter.error(token, message);
    return new ParseException();
  }
}
