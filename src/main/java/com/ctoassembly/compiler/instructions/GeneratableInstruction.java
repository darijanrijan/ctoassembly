package com.ctoassembly.compiler.instructions;


/**
 * Represents a hypothetical assembly instruction. Implementations of this interface
 * must implement the method to generate assembly code for this instruction.
 * 
 * @author Darijan Jankovic
 * @version 0.2
 */
public interface GeneratableInstruction {
	
	/**
	 * Returns generated assembly code for this instruction.
	 */
	public String getGeneratedCode();

}