package ru.vlade1k.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vlade1k.scanner.token.TokenType;

import java.io.IOException;

public class MultiLineTest extends ScannerAbstractTest {
  protected MultiLineTest() throws IOException {
    super("multi-line-artifact.lox");
  }

  @Test
  void hasThreeSemicolonTokens() {
    var tokens = scanner.scanTokens();
    Assertions.assertEquals(3, tokens.stream().filter(token -> token.getType().equals(TokenType.SEMICOLON)).count());
  }
}
