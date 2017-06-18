package com.ctoassembly.compiler;

/**
 * Represents a scope in which function parameters are given. 
 * It starts with <code>(</code> right after function name and ends with <code>)</code>.
 * <pre>
 * {@code
 * int foo(
 * // parameter scope starts here
 * int x, int * y
 * // parameter scope ends here
 * ) { 
 * 	int x;
 * }
 * </pre>
 * 			
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class ParameterScope extends Scope {
	
	private final Function fFunction;
	private final Codegen fCodegen;
	private Integer fParameterCount= 0;

	public ParameterScope(final Codegen fCodegen, final Function function, final Scope parentScope) {
		super(parentScope);
		this.fCodegen = fCodegen;
		fFunction= function;
	}
	
	public Function getFunction() {
		return fFunction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scope getChildScope(final Function currentFunction) {
		return new FunctionScope(fCodegen, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertVariable(final SymTabEntry variable, final SymTabEntry ... initValues) throws CompilerException {
		variable.setKind(Definitions.PARAMETER);
		variable.setAttribute(fParameterCount);
		fSymTable.insertVariable(variable);
		fFunction.setParamType(fParameterCount++, variable.getType());
		fFunction.setParameterCount(fParameterCount);
	}
	
}