package ru.vlade1k.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.io.IOException;
import java.util.List;

public class OneLineTest extends ScannerAbstractTest {
  protected OneLineTest() throws IOException {
    super("one-line-artifact.lox");
  }

  @Test
  void tokenSequenceMeetsExpectations() {
    var expectedSequence = List.of(
        new Token(TokenType.VAR, "var", null, 1),
        new Token(TokenType.IDENTIFIER, "a", null, 1),
        new Token(TokenType.EQUAL, "=", null, 1),
        new Token(TokenType.NUMBER, "5", Double.parseDouble("5.0"), 1),
        new Token(TokenType.SEMICOLON, ";", null, 1),
        new Token(TokenType.EOF, "", null, 1)
    );

    assertEquals(expectedSequence, scanner.scanTokens());
  }
}
