package ru.vlade1k.parser;

import static ru.vlade1k.scanner.token.TokenType.BANG;
import static ru.vlade1k.scanner.token.TokenType.BANG_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.EOF;
import static ru.vlade1k.scanner.token.TokenType.EQUAL_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.FALSE;
import static ru.vlade1k.scanner.token.TokenType.GREATER;
import static ru.vlade1k.scanner.token.TokenType.GREATER_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.LEFT_PAREN;
import static ru.vlade1k.scanner.token.TokenType.LESS;
import static ru.vlade1k.scanner.token.TokenType.LESS_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.MINUS;
import static ru.vlade1k.scanner.token.TokenType.NIL;
import static ru.vlade1k.scanner.token.TokenType.NUMBER;
import static ru.vlade1k.scanner.token.TokenType.PLUS;
import static ru.vlade1k.scanner.token.TokenType.PRINT;
import static ru.vlade1k.scanner.token.TokenType.RIGHT_PAREN;
import static ru.vlade1k.scanner.token.TokenType.SEMICOLON;
import static ru.vlade1k.scanner.token.TokenType.SLASH;
import static ru.vlade1k.scanner.token.TokenType.STAR;
import static ru.vlade1k.scanner.token.TokenType.STRING;
import static ru.vlade1k.scanner.token.TokenType.TRUE;

import ru.vlade1k.JLoxInterpreter;
import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;
import ru.vlade1k.parser.ast.statements.Statement;
import ru.vlade1k.parser.ast.statements.StatementExpression;
import ru.vlade1k.parser.ast.statements.StatementPrint;
import ru.vlade1k.parser.exceptions.ParseException;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
  private final List<Token> tokens;
  private int current;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(statement());
    }

    return statements;
  }

  private Statement statement() {
    if (match(PRINT)) {
      return printStatement();
    }

    return expressionStatement();
  }

  private Statement printStatement() {
    Expression value = expression();
    consume(SEMICOLON, "Expected ';' after value");
    return new StatementPrint(value);
  }

  private Statement expressionStatement() {
    Expression expression = expression();
    consume(SEMICOLON, "Expected ';' after value");
    return new StatementExpression(expression);
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

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().getType() == SEMICOLON) return;

      switch (peek().getType()) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }
}
