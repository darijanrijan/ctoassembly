package com.ctoassembly.compiler.instructions;


public abstract class IndentedInstruction implements GeneratableInstruction {

	@Override
	public final String getGeneratedCode() {
		final StringBuilder builder= new StringBuilder();
		builder.append("\t\t\t");
		builder.append(doGenerateCode());
		return builder.toString();
	}

	/**
	 * Generates actual assembly code after the leading indentation has been generated in {@link IndentedInstruction#getGeneratedCode()}.
	 */
	public abstract String doGenerateCode();
	
}