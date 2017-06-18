package com.ctoassembly.compiler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Table of symbols for a current scope of the current parsing job. 
 * It stores symbols such as functions, global variables, variables local to a function, 
 * function parameters and working registers. 
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class SymTab {	
	/**
	 * A map holding all table of symbol entries.
	 */
	private final Map<String, SymTabEntry> fSymMap= new HashMap<String, SymTabEntry>();
	
	/**
	 * Inserts a symbol to the table.
	 * @param variable
	 * 			symbol to be inserted
	 * @return
	 * 			inserted symbol
	 * @throws SymTabException 
	 */
	public SymTabEntry insertVariable(final SymTabEntry variable) throws SymTabException  {
		if (fSymMap.put(variable.getName(), variable) != null) {
			throw new SymTabException("Redefinition of " + variable.getName());
		}
		return variable;
	}
	
	public SymTabEntry lookupVariable(final String variableName) {
		return fSymMap.get(variableName);
	}
	
	public Function insertFunction(final Function function) throws SymTabException  {
		if (fSymMap.put(getKey(function.getName(), Definitions.FUNCTION), function) != null) {
			throw new SymTabException("Redefinition of " + function.getName());
		}
		return function;
	}
	
	public Function lookupFunction(final String functionName) {
		return (Function) fSymMap.get(getKey(functionName, Definitions.FUNCTION));
	}
	
	public SymTabEntry insertConstant(SymTabEntry constant) {
		fSymMap.put(constant.getName(), constant);
		return constant;
	}

	public SymTabEntry lookupConstant(String constant) {
		return fSymMap.get(constant);
	}
	
	/**
	 * Returns the name of the symbol for the table of symbols. The name is uniquely identified by its name and kind.
	 * @param name
	 * 			name of the symbol
	 * @param kind
	 * 			kind of the symbol
	 * @return
	 * 			unique name generated from the symbol's name and kind
	 */
	private static String getKey(final String name, final int kind) {
		return name + "|" + Integer.toString(kind);
	}
}
