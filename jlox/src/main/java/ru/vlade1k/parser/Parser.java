package ru.vlade1k.parser;

import static ru.vlade1k.scanner.token.TokenType.AND;
import static ru.vlade1k.scanner.token.TokenType.BANG;
import static ru.vlade1k.scanner.token.TokenType.BANG_EQUAL;
import static ru.vlade1k.scanner.token.TokenType.COMMA;
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
import static ru.vlade1k.scanner.token.TokenType.TRUE;
import static ru.vlade1k.scanner.token.TokenType.VAR;
import static ru.vlade1k.scanner.token.TokenType.WHILE;

import ru.vlade1k.JLoxInterpreter;
import ru.vlade1k.parser.ast.expression.AssignmentExpression;
import ru.vlade1k.parser.ast.expression.BinaryExpression;
import ru.vlade1k.parser.ast.expression.CallExpression;
import ru.vlade1k.parser.ast.expression.Expression;
import ru.vlade1k.parser.ast.expression.GroupingExpression;
import ru.vlade1k.parser.ast.expression.LiteralExpression;
import ru.vlade1k.parser.ast.expression.LogicalExpression;
import ru.vlade1k.parser.ast.expression.UnaryExpression;
import ru.vlade1k.parser.ast.expression.VariableExpression;
import ru.vlade1k.parser.ast.statements.FunctionDeclarationStatement;
import ru.vlade1k.parser.ast.statements.IfStatement;
import ru.vlade1k.parser.ast.statements.ReturnStatement;
import ru.vlade1k.parser.ast.statements.Statement;
import ru.vlade1k.parser.ast.statements.BlockStatement;
import ru.vlade1k.parser.ast.statements.ExpressionStatement;
import ru.vlade1k.parser.ast.statements.PrintStatement;
import ru.vlade1k.parser.ast.statements.VarStatement;
import ru.vlade1k.parser.ast.statements.WhileStatement;
import ru.vlade1k.parser.exceptions.ParseException;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
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
      statements.add(declaration());
    }

    return statements;
  }

  private Statement declaration() {
    try {
      if (match(VAR)) {
        return varDeclaration();
      }
      if (match(FUN)) {
        return functionDeclaration();
      }
      return statement();
    } catch (ParseException ex) {
      synchronize();
      return null;
    }
  }

  private Statement functionDeclaration() {
    Token functionName = consume(IDENTIFIER, "Expect function name.");
    consume(LEFT_PAREN, "Expect left parent after function name");

    List<Expression> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          throw error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while(match(COMMA));
    }

    consume(RIGHT_PAREN, "Expected ')' after arguments.");
    consume(LEFT_BRACE, "Expected '{' before function declaration block.");

    List<Statement> functionBody = blockStatement();
    return new FunctionDeclarationStatement(functionName, arguments, functionBody);
  }

  private Statement varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expression initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new VarStatement(initializer, name);
  }

  private Statement statement() {
    if (match(IF)) {
      return ifStatement();
    }

    if (match(FOR)) {
      return forStatement();
    }

    if (match(PRINT)) {
      return printStatement();
    }

    if (match(RETURN)) {
      return returnStatement();
    }

    if (match(WHILE)) {
      return whileStatement();
    }

    if (match(LEFT_BRACE)) {
      return new BlockStatement(blockStatement());
    }

    return expressionStatement();
  }

  private Statement ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'");
    Expression condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after 'if' logical expression");

    Statement ifBranch = statement();
    Statement elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new IfStatement(condition, ifBranch, elseBranch);
  }

  private Statement forStatement() {
    consume(LEFT_PAREN, "Expected '(' after 'for'");
    Statement initializer;
    if (match(SEMICOLON)) {
      initializer = null;
    } else if (match(VAR)) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expression condition = null;
    if (!check(SEMICOLON)) {
      condition = expression();
    }

    consume(SEMICOLON, "Expected ';' after loop condition");
    Expression increment = null;
    if (!check(RIGHT_PAREN)) {
      increment = expression();
    }

    consume(RIGHT_PAREN, "Expected ';' after for clause.");
    Statement body = statement();

    if (increment != null) {
      body = new BlockStatement(Arrays.asList(body, new ExpressionStatement(increment)));
    }

    if (condition == null) {
      condition = new LiteralExpression(true);
    }

    body = new WhileStatement(condition, body);

    if (initializer != null) {
      body = new BlockStatement(Arrays.asList(initializer, body));
    }

    return body;
  }

  private List<Statement> blockStatement() {
    List<Statement> statements = new ArrayList<>();
    while(!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Statement printStatement() {
    Expression value = expression();
    consume(SEMICOLON, "Expected ';' after value.");
    return new PrintStatement(value);
  }

  private Statement returnStatement() {
    Token keyword = previous();
    Expression value = null;

    if (!check(SEMICOLON)) {
      value = expression();
    }

    consume(SEMICOLON, "Expected ';' after return value.");
    return new ReturnStatement(keyword, value);
  }

  private Statement whileStatement() {
    consume(LEFT_PAREN, "Expected '(' after 'while'.");
    Expression expression = expression();
    consume(RIGHT_PAREN, "Expected ')' after 'while' condition.");
    Statement body = statement();
    return new WhileStatement(expression, body);
  }

  private Statement expressionStatement() {
    Expression expression = expression();
    consume(SEMICOLON, "Expected ';' after value");
    return new ExpressionStatement(expression);
  }

  private Expression expression() {
    return assignment();
  }

  private Expression assignment() {
    Expression expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expression value = assignment();

      if (expr instanceof VariableExpression) {
        Token name = ((VariableExpression)expr).getName();
        return new AssignmentExpression(name, value);
      }

      throw error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expression or() {
    Expression expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expression right = and();
      expr = new LogicalExpression(expr, operator, right);
    }

    return expr;
  }

  private Expression and() {
    Expression expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expression right = equality();
      expr = new LogicalExpression(expr, operator, right);
    }

    return expr;
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

    return call();
  }

  private Expression call() {
    Expression expression = primary();

    while (true) {
      if (match(LEFT_PAREN)) {
        expression = finishCall(expression);
      } else {
        break;
      }
    }

    return expression;
  }

  private Expression finishCall(Expression callee) {
    List<Expression> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          throw error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while(match(COMMA));
    }

    Token paren = consume(RIGHT_PAREN, "Expected ')' after arguments.");

    return new CallExpression(callee, paren, arguments);
  }

  private Expression primary() {
    if (match(FALSE)) return new LiteralExpression(false);
    if (match(TRUE)) return new LiteralExpression(true);
    if (match(NIL)) return new LiteralExpression(null);

    if (match(NUMBER, STRING)) {
      return new LiteralExpression(previous().getLiteral());
    }

    if (match(IDENTIFIER)) {
      return new VariableExpression(previous());
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new GroupingExpression(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  /**
   * Проверяет тип текущего токена. Если это искомый тип, то мы двигаем каретку вперёд
   *
   * @param types массив типов токенов
   * @return      true, если текущий токен - один из переданных типов. Иначе false.
   */
  private boolean match(TokenType... types) {
    for (TokenType type: types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  /**
   * Проверяет текущий тип токена на соответствие переданному типу
   *
   * @param type токен, с которым сравнивают
   * @return     true, если тип текущего токена соответствует типу, переданному в параметре
   */
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

  /**
   * Проверяет тип текущего токена на соответствие переданному.
   * Если соответствует - возвращается текущий токен. Иначе выбрасывается ParseException.
   *
   * @param type    тип переданного токена.
   * @param message сообщение, которое будет в Exception'е.
   * @return        текущий токен.
   */
  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  /**
   * Возвращает текущий токен и двигает каретку вперёд, он не последний
   *
   * @return текущий токен
   */
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
