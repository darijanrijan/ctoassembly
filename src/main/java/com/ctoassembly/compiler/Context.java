package com.ctoassembly.compiler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * This class represents the context of the current parsing job. 
 * 
 * It keeps track of the current scope of the parsing job (c code),
 * current function (if the code is in one),  number of parameters 
 * to the function, etc.
 * 
 * @author Darijan Jankovic
 * @version 0.1
 */
public class Context {
	
	/* default */ Function fFunction; //current function
	/* default */ int fArgNum= 0;
	/* default */ int fLabNum= 0;
	/* default */ int fFalseLabNum= -1;
	/* default */ String fFunctionCallName= "";
	/* default */ final Stack<String> fFunctionCallStack= new Stack<String>();
	/* default */ final Stack<Integer> fArgumentCallStack= new Stack<Integer>();
	/* default */ final Stack<Integer> fLabelStack= new Stack<Integer>();
	/* default */ final Stack<Integer> fLocalVariableNumStack= new Stack<Integer>();
	/* default */ final Stack<SymTabEntry> fArgumentStack= new Stack<SymTabEntry>();
	/* default */ final Stack<SymTabEntry> fLocalVariableStack= new Stack<SymTabEntry>();
	/* default */ final Queue<IncDecDescriptor> fIncDecQueue= new LinkedList<IncDecDescriptor>();

	private final Stack<Scope> fScopeStack= new Stack<Scope>();
	private final Codegen fCodegen;
	
	public Context(final Codegen codegen) {
		this.fCodegen= codegen;
		fScopeStack.push(new GlobalScope(codegen));
	}
	
	/**
	 * Switches the scope according to following algorithm:
	 * if the current scope is global scope, move into parameter scope (defining a function),
	 * if the current scope is parameter scope, move into function scope (local scope),
	 * if the current scope is function scope, move into block scope (block inside a function).
	 */
	public void enterChildScope() {
		fScopeStack.push(getCurrentScope().getChildScope(fFunction));
	}

	/**
	 * Switches back to the parent scope. This happens when the current scope ends (e.g. after the closing bracket <tt>{ ... }</tt>).
	 */
	public void enterParentScope() {
		fScopeStack.pop();
	}
	
	/**
	 * Switches the scope back to the global scope. This happens when the current parsing job finishes parsing a function.
	 */
	public void enterGlobalScope() {
		while (fScopeStack.size() > 1) {
			fScopeStack.pop();
		}
	}

	/**
	 * Returns the current scope.
	 */
	private Scope getCurrentScope() {
		return fScopeStack.peek();
	}
	
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
		return getCurrentScope().getFunction(functionName);
	}

	/**
	 * Tries to insert the function.
	 * @param function
	 * 			the function to insert
	 * @throws CompilerException
	 * 			if the function already exists
	 */
	public void insertFunction(final Function function) throws CompilerException {
		getCurrentScope().insertFunction(function);
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
	public SymTabEntry getVariable(final String variableName) throws CompilerException {
		return getCurrentScope().getVariable(variableName);
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
	public void insertVariable(final SymTabEntry variable, final SymTabEntry ... initValues) throws CompilerException {
		getCurrentScope().insertVariable(variable, initValues);
	}

	/**
	 * Inserts the constant.
	 * @param constant
	 * 			the constant to insert
	 * @throws SemanticsException
	 * 			if the variable is too big or too small
	 */
	public SymTabEntry insertConstant(final Integer constant) throws SemanticsException {
		return getCurrentScope().insertConstant(constant);
	}
	
	/**
	 * Pops the inc dec queue and generate all increment/decrement statements that were present in the previous statement.
	 * @throws CodegenException
	 */
	public void generateIncDecStatements() {
		while (!fIncDecQueue.isEmpty()) {
			final IncDecDescriptor idd= fIncDecQueue.poll();
			fCodegen.genIncDec(idd.fEntry, idd.fOperation, idd.fLineNo);
		}
	}

}