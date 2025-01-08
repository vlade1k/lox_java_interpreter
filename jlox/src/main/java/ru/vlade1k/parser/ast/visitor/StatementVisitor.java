package ru.vlade1k.parser.ast.visitor;

import ru.vlade1k.parser.ast.statements.IfStatement;
import ru.vlade1k.parser.ast.statements.StatementBlock;
import ru.vlade1k.parser.ast.statements.StatementExpression;
import ru.vlade1k.parser.ast.statements.StatementPrint;
import ru.vlade1k.parser.ast.statements.StatementVar;
import ru.vlade1k.parser.ast.statements.WhileStatement;

public interface StatementVisitor<T> {
  T visitStatementExpression(StatementExpression expression);
  T visitPrintStatement(StatementPrint statementPrint);
  T visitVarStatement(StatementVar statementVar);
  T visitBlockStatement(StatementBlock statementBlock);
  T visitIfStatement(IfStatement ifStatement);
  T visitWhileStatement(WhileStatement whileStatement);
}
