package com.ctoassembly.compiler.instructions;


public class OperandsInstruction extends IndentedInstruction {
	
	private final String fInstruction;
	private final String[] fOperands;
	
	public OperandsInstruction(final String instruciton, final String ... operands) {
		fInstruction= instruciton;
		fOperands= operands;
	}
	
	public String getInstruction() {
		return fInstruction;
	}
	
	public String[] getOperands() {
		return fOperands;
	}
	
	@Override
	public String doGenerateCode() {
		final StringBuilder builder= new StringBuilder();
		builder.append(fInstruction);
		if (fOperands.length > 0) {
			//print tab after the instruction before the operands
			builder.append("\t");
			
			for (int i= 0; i < fOperands.length; i++) {
				if (i > 0) {
					builder.append(",");
				}
				builder.append(fOperands[i]);
			}
		}
		return builder.toString();
	}

}