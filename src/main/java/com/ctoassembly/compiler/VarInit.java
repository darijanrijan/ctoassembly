package com.ctoassembly.compiler;

/**
 * A helper class for the current parsing job when defining and initializing variables.
 * This class is used only in {@link parser}.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class VarInit extends Pair<SymTabEntry, SymTabEntry[]> {

	public VarInit(final SymTabEntry identifier, final SymTabEntry ... initValues) {
		super(identifier, initValues);
	}

	public SymTabEntry getIdentifier() {
		return fLeft;
	}

	public SymTabEntry[] getInitValues() {
		return fRight;
	}

}
