package com.ctoassembly.compiler.instructions;


public class LabelInstruction implements GeneratableInstruction {

	private final String fLabel;

	public LabelInstruction(final String label) {
		fLabel= label;
	}
	
	@Override
	public String getGeneratedCode() {
		return fLabel + ":";
	}
	
}