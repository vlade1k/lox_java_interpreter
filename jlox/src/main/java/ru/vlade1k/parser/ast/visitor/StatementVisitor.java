package ru.vlade1k.parser.ast.visitor;

import ru.vlade1k.parser.ast.statements.StatementExpression;
import ru.vlade1k.parser.ast.statements.StatementPrint;

public interface StatementVisitor<T> {
  T visitStatementExpression(StatementExpression expression);
  T visitPrintStatement(StatementPrint statementPrint);

}
