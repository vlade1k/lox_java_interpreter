package ru.vlade1k.parser.ast.statements;

import ru.vlade1k.interpreter.exceptions.ReturnException;
import ru.vlade1k.parser.ast.visitor.StatementVisitor;

public abstract class Statement {
    public abstract <R> R accept(StatementVisitor<R> visitor) throws ReturnException;
}
