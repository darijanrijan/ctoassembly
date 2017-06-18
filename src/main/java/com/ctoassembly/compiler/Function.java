package com.ctoassembly.compiler;

/**
 * Represents a function symbol.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class Function extends SymTabEntry {

	private int fParameterCount;

	public Function(String name, Type type) {
		super(name, Definitions.FUNCTION, type);
	}

	/**
	 * Returns the number of parameters of the function.
	 */
	public int getParameterCount() {
		return fParameterCount;
	}
	
	/**
	 * Sets the number of parameters of the function.
	 */
	public void setParameterCount(final int parameterCount) {
		fParameterCount= parameterCount;
	}
}
