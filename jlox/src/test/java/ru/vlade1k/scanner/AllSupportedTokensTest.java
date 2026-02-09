package ru.vlade1k.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.vlade1k.scanner.token.Token;
import ru.vlade1k.scanner.token.TokenType;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class AllSupportedTokensTest extends ScannerAbstractTest {
  private static final EnumSet<TokenType> NOT_SUPPORTED_TOKENS = EnumSet.of(
      TokenType.CLASS,
      TokenType.SUPER,
      TokenType.THIS,
      TokenType.DOT
  );

  private static final Set<TokenType>
      SUPPORTED_TOKEN_TYPES = EnumSet.copyOf(Arrays.stream(TokenType.values())
                                              .filter(tokenType -> !NOT_SUPPORTED_TOKENS.contains(tokenType))
                                              .toList());

  protected AllSupportedTokensTest() throws IOException {
    super("all-tokens.lox");
  }

  @Test
  void hasAllSupportedTokenTypes() {
    var actualTokenTypes = EnumSet.copyOf(scanner.scanTokens().stream().map(Token::getType).toList());
    assertEquals(SUPPORTED_TOKEN_TYPES, actualTokenTypes);
  }
}
