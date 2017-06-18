package com.ctoassembly.compiler;

/**
 * @author Darijan Jankovic
 * @version 0.1
 */
public class IncDecDescriptor {
	
	public final SymTabEntry fEntry;
	public final Integer fOperation;
	public final Integer fLineNo;

	public IncDecDescriptor(final SymTabEntry entry, final Integer operation, final Integer linoNo) {
		fEntry = entry;
		fOperation = operation;
		fLineNo = linoNo;
	}
}