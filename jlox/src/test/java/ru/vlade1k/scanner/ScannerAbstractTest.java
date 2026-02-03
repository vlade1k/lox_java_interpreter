package ru.vlade1k.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.vlade1k.scanner.token.TokenType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ScannerAbstractTest {
  protected final String artifactName;
  protected final String sources;
  protected final Scanner scanner;

  protected ScannerAbstractTest(String artifactName) throws IOException {
    this.artifactName = artifactName;
    this.sources = String.join("", Files.readAllLines(Path.of("src", "test", "resources", "scanner", artifactName)));
    this.scanner = new Scanner(sources);
  }

  @Test
  void isAlwaysEndsWithEOF() {
    var tokens = scanner.scanTokens();
    assertEquals(tokens.getLast().getType(), TokenType.EOF);
  }
}
