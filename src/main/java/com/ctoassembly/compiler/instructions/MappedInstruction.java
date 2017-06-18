package com.ctoassembly.compiler.instructions;


/**
 * Instruction wrapper that maps the current instruction with a line number of the c code
 * that initiated generation of the instruction.
 * 
 * @author Darijan Jankovic
 * @version 0.2
 */
public class MappedInstruction implements GeneratableInstruction {

	private final GeneratableInstruction fInstruction;
	private final InstructionDescription fDescription;
	private final Integer fCLineNo;

	public MappedInstruction(final GeneratableInstruction instruction, final InstructionDescription description, final Integer cLineNo) {
		fInstruction= instruction;
		fDescription= description;
		fCLineNo= cLineNo;
	}
	
	public Integer getCLineNo() {
		return fCLineNo;
	}
	
	@Override
	public String getGeneratedCode() {
		return fInstruction.getGeneratedCode();
	}
	
	public InstructionDescription getDescription() {
		return fDescription;
	}
	
}