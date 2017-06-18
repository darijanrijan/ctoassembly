package com.ctoassembly.compiler;

/**
 * Represents a scope of a c program. A scope can be {@link GlobalScope}, {@link ParameterScope}, 
 * {@link FunctionScope}, {@link BlockScope}.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public abstract class Scope {
	/**
	 * Every scope (except {@link GlobalScope} has a parent {@link Scope} from which they are derived.
	 */
	private final Scope fParentScope;
	/**
	 * Every context (i.e. scope) should have its own Table of Symbols.
	 */
	protected final SymTab fSymTable= new SymTab();
	
	public Scope(final Scope parentScope) {
		fParentScope= parentScope;
	}
	
	/**
	 * Returns a child scope of this scope. A child scope is determined by this algorithm:
	 * if the current scope is global scope, move into parameter scope (defining function),
	 * if the current scope is parameter scope, move into function scope (local scope),
	 * if the current scope is function scope, move into block scope (block inside a function).
	 */
	public abstract Scope getChildScope(final Function function);

	/**
	 * Tries to find a function for the given function name and returns it. 
	 * @param functionName
	 * 			the name of the function
	 * @return
	 * 			the function
	 * @throws CompilerException
	 * 			when a function for the given name does not exist
	 */
	public Function getFunction(final String functionName) throws CompilerException {
		Function identifier= fSymTable.lookupFunction(functionName);
		if (identifier != null) {
			return identifier;
		}
		if (fParentScope != null && (identifier= fParentScope.getFunction(functionName)) != null) {
			return identifier;
		}
		throw new CompilerException("Undefined reference to function '" + functionName + "'");
	}

	/**
	 * Tries to insert the function.
	 * @param function
	 * 			the function to insert
	 * @throws CompilerException
	 * 			if the function already exists
	 */
	public void insertFunction(final Function function) throws CompilerException {
		fSymTable.insertFunction(function);
	}
	
	/**
	 * Tries to find a variable for the given variable name and returns it.
	 * First it looks in the current scope then tries to find the variable in 
	 * any of the parent scopes.
	 *  
	 * @param variableName
	 * 			the name of the variable
	 * @return
	 * 			the variable
	 * @throws CompilerException
	 * 			when a variable for the given name does not exist (redefiniton)
	 */
	public SymTabEntry getVariable(final String name) throws CompilerException {
		SymTabEntry identifier= fSymTable.lookupVariable(name);
		if (identifier != null) {
			return identifier;
		}
		if (fParentScope != null && (identifier= fParentScope.getVariable(name)) != null) {
			return identifier;
		}
		throw new CompilerException(name + " undeclared");
	}
	
	/**
	 * Tries to insert the variable and initialize it with init values.
	 * @param variable
	 * 			the variable to insert
	 * @param initValues
	 * 			values to init the variable
	 * @throws CompilerException
	 * 			if the variable already exists in the current scope (redefinition)
	 */
	public abstract void insertVariable(final SymTabEntry variable, final SymTabEntry ... initValues) throws CompilerException;

	/**
	 * Inserts the constant.
	 * @param constant
	 * 			the constant to insert
	 * @throws SemanticsException
	 * 			if the variable is too big or too small
	 */
	public SymTabEntry insertConstant(final Integer constant) throws SemanticsException {
		Semantic.assertValidConstant(constant);
		return fSymTable.insertConstant(new SymTabEntry("$" + constant, Definitions.CONSTANT, new Type.SimpleType(Definitions.INT_TYPE)));
	}
	
	
}
