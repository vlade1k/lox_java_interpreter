package ru.vlade1k;

import ru.vlade1k.interpreter.Interpreter;
import ru.vlade1k.interpreter.exceptions.RuntimeLoxException;
import ru.vlade1k.parser.Parser;
import ru.vlade1k.parser.ast.statements.Statement;
import ru.vlade1k.scanner.Scanner;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JLoxInterpreter {
  private static final Interpreter interpreter = new Interpreter();
  private static boolean hadError;
  private static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox[script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    }
    else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(path));
    run(new String(bytes, Charset.defaultCharset()));

    if (hadError) {
      System.exit(65);
    }
    if (hadRuntimeError) {
      System.exit(70);
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.println(">> ");
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    if (hadError) return;

    interpreter.interpret(statements);
  }

  public static void error(int line, String message) {
    report(line, "", message);
  }

  public static void error(Token token, String message) {
    if (token.getType() == TokenType.EOF) {
      report(token.getLine(), " at end", message);
    } else {
      report(token.getLine(), " at '" + token.getLexeme() + "'", message);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  public static void runtimeError(RuntimeLoxException error) {
    System.err.println(error.getMessage() + "\n[line " + error.getToken().getLine() + "]");
    hadRuntimeError = true;
  }
}
