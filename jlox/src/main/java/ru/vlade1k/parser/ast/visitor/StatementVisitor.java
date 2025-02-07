package ru.vlade1k.parser.ast.visitor;

import ru.vlade1k.interpreter.exceptions.ReturnException;
import ru.vlade1k.parser.ast.statements.FunctionDeclarationStatement;
import ru.vlade1k.parser.ast.statements.IfStatement;
import ru.vlade1k.parser.ast.statements.ReturnStatement;
import ru.vlade1k.parser.ast.statements.BlockStatement;
import ru.vlade1k.parser.ast.statements.ExpressionStatement;
import ru.vlade1k.parser.ast.statements.PrintStatement;
import ru.vlade1k.parser.ast.statements.VarStatement;
import ru.vlade1k.parser.ast.statements.WhileStatement;

public interface StatementVisitor<T> {
  T visitStatementExpression(ExpressionStatement expression);
  T visitPrintStatement(PrintStatement statementPrint);
  T visitVarStatement(VarStatement statementVar);
  T visitBlockStatement(BlockStatement statementBlock);
  T visitIfStatement(IfStatement ifStatement);
  T visitWhileStatement(WhileStatement whileStatement);
  T visitFunctionDeclarationStatement(FunctionDeclarationStatement funcDeclarationStatement);
  T visitReturnStatement(ReturnStatement returnStatement) throws ReturnException;
}
