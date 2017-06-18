package com.ctoassembly.compiler;

import java.util.ArrayList;
import java.util.List;

import com.ctoassembly.compiler.Type.ArrayType;
import com.ctoassembly.compiler.Type.PointerType;
import com.ctoassembly.compiler.Type.SimpleType;
import com.ctoassembly.compiler.instructions.GeneratableInstruction;
import com.ctoassembly.compiler.instructions.InstructionDescription;
import com.ctoassembly.compiler.instructions.LabelInstruction;
import com.ctoassembly.compiler.instructions.MappedInstruction;
import com.ctoassembly.compiler.instructions.OperandsInstruction;

/**
 * Code generation class. 
 * This class is used by the parser for assembly code generation. 
 * 
 * @author Darijan Jankovic
 * @version 0.1
 */
public class Codegen {

	public static class Program {
		
		private final List<GeneratableInstruction> fInstructionQueue= new ArrayList<>();

		public void enqueueInstruction(final GeneratableInstruction instruction, final GeneratableInstruction marker) {
			if (marker == null) {
				fInstructionQueue.add(instruction);
			} else {
				GeneratableInstruction markerParent= null;
				for (final GeneratableInstruction gi : fInstructionQueue) {
					if (gi.getGeneratedCode().equals(marker.getGeneratedCode())) {
						markerParent= gi;
						break;
					}
				}
				fInstructionQueue.add(fInstructionQueue.indexOf(markerParent), instruction);
			}
		}
		
		public List<GeneratableInstruction> getInstructions() {
			return fInstructionQueue;
		}

		public String asText() {
			StringBuilder builder= new StringBuilder();
			for (GeneratableInstruction instruction : fInstructionQueue) {
				builder.append(instruction.getGeneratedCode());
				builder.append("\n\r");
			}
			return builder.toString();
		}

	}
	
	/** 
	 * Buffer for storing assembly code.
	 */
	private final Program fProgram= new Program();
	private final LexLineNoResolver fLexLineNoResolver;
	private final Registers fRegisters;
	private int fStringsCount= 0;
	private int fLexLineNoModifier= 0;
	private LabelInstruction fFunctionLabel;

	public Codegen(final Registers registers, final LexLineNoResolver lexLineNoResolver) {
		fRegisters= registers;
		fLexLineNoResolver= lexLineNoResolver;
	}

	public Program getProgram() {
		return fProgram;
	}

	/**
	 * Super hack because of a special case with mapping the closing bracket of an if statement.
	 */
	public void incLexLineNoModifier() {
		fLexLineNoModifier++;
	}

	/**
	 * Super hack because of a special case with mapping the closing bracket of an if statement.
	 */
	public void decLexLineNoModifier() {
		fLexLineNoModifier--;
	}
	
	private void enqueueInstruction(final GeneratableInstruction instruction, final InstructionDescription description) {
		enqueueInstruction(instruction, null, description);
	}

	private void enqueueInstruction(final GeneratableInstruction instruction, final GeneratableInstruction marker, final InstructionDescription description) {
		enqueueInstruction(instruction, marker, description, fLexLineNoResolver.getCurrentCodeLineNo() + fLexLineNoModifier);
	}

	private void enqueueInstruction(final GeneratableInstruction instruction, final InstructionDescription description, final Integer cLineNo) {
		enqueueInstruction(instruction, null, description, cLineNo);
	}

	private void enqueueInstruction(final GeneratableInstruction instruction, final GeneratableInstruction marker, final InstructionDescription description, Integer cLineNo) {
		//We don't really want to map c code to labels
		if (instruction instanceof LabelInstruction) {
			cLineNo= null;
		}
		fProgram.enqueueInstruction(new MappedInstruction(instruction, description, cLineNo), marker);
	}
	
	
	/**
	 * Prints an assembly operand - an assembly representation of a symbol from the table of symbols
	 * @param symbol	
	 * 			symbol to print
	 * @return
	 * 			assembly operand string
	 */
	private String printSymbol(final SymTabEntry symbol) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		
		final int index= symbol.getAttribute() + 1;
		
		if (symbol.getKind() == Definitions.LOCAL_VAR) {
			int indexLocal= index;
			if (isArray(symbol)) {
				indexLocal= indexLocal - symbol.getArrayIndex();
			}
			indexLocal= indexLocal * 4;
			return "-" + Integer.toString(indexLocal) + "(%BP)";
		} else if(symbol.getKind() == Definitions.PARAMETER) {
			final int indexParam= index * 4 + 4;
			return Integer.toString(indexParam) + "(%BP)";
		} else if(symbol.getKind() == Definitions.STRING) {
			return "$" + symbol.getName();
		} else {
			return symbol.getName();
		}
	}
	
	/**
	 * Generates a label for a function with a possible '@' as prefix, and possible appendix (e.g. '_exit').
	 */
	public void genStrLab(final String label, final String labelAppendix, final boolean hasPrefix) {
		enqueueInstruction(new LabelInstruction((hasPrefix ? "@" : "") + label + labelAppendix), new InstructionDescription("Label pointing to an instruction that starts a logical block of code (e.g. function body, function exit routine, etc)."));
	}
	
	public void genFunctionLabel(final SymTabEntry function) {
		fFunctionLabel= new LabelInstruction(function.getName());
		enqueueInstruction(fFunctionLabel, new InstructionDescription("Label pointing to an instruction from which a functoin starts."));
	}

	/**
	 * Generates a jump instruction to a label.
	 */
	public void genJumpToStrLab(final String jmpInstruction, final String label, final boolean hasPrefix) {
		final StringBuilder description= new StringBuilder();
		if (jmpInstruction.equals(Definitions.UNCONDITIONAL_JUMP)) {
			description.append("Unconditional");
		} else {
			description.append("Conditional");
		}
		description.append(" jump to the label.");
		enqueueInstruction(new OperandsInstruction(jmpInstruction, (hasPrefix ? "@" : "") + label), new InstructionDescription(description.toString()));
	}

	/**
	 * Generates a jump instruction to a label.
	 */
	public void genReturn(final String label) {
		enqueueInstruction(new OperandsInstruction(Definitions.UNCONDITIONAL_JUMP, "@" + label), new InstructionDescription("Unconditional jump to the function's exit routine."));
	}

	/**
	 * Generates a numeric label for conditional operations.
	 */
	public void genNumLab(final String label, final int index) {
		enqueueInstruction(new LabelInstruction("@" + label + index), new InstructionDescription("Numeric label for conditional statements and blocks."));
	}

	/**
	 * Generates a jump instruction to a numbered label
	 */
	void genJumpToNumLab(final String jmpInstruction, final String label, final int index) {
		final StringBuilder description= new StringBuilder();
		if (jmpInstruction.equals(Definitions.UNCONDITIONAL_JUMP)) {
			description.append("Unconditional jump to the numeric label.");
		} else {
			description.append("Conditional jump to the numeric label. Conditional jumps are executed if the conditional jump operation matches the state of the flags bits.");
		}
		enqueueInstruction(new OperandsInstruction(jmpInstruction, "@" + label + index), new InstructionDescription(description.toString()));
	}
		
	/**
	 * Generates global variable declaration.
	 * @param name
	 * 			global variable name
	 * @param values
	 * 			values of this global variable
	 */
	public void genGlobVar(final SymTabEntry variable, final Integer ... values) {
		enqueueInstruction(new LabelInstruction(variable.getName()), new InstructionDescription("Label pointing to a global variable."));
		for (final Integer value : values) {
			enqueueInstruction(new OperandsInstruction(".word", String.valueOf(value)), new InstructionDescription("Global variable initialization."));
		}
	}	
	
	/**
	 * Generates a frame base instructions that precedes every function.
	 */
	public void genFrameBase() {
		enqueueInstruction(new OperandsInstruction("PUSH", "%BP"), new InstructionDescription("Function entry routine", "Saves the state of the base pointer onto the stack and increments the stack pointer."));
		enqueueInstruction(new OperandsInstruction("MOV", "%SP", "%BP"), new InstructionDescription("Move the value of the stack pointer to the base pointer. Let the base pointer point to the current top of the stack (stack pointer always points to the top of the stack)."));
	}
	
	/**
	 * Generates local variables.
	 * @param count
	 * 			number of local variables
	 */
	public void genLocVars(final int count) {
		if (count != 0) {
			enqueueInstruction(new OperandsInstruction("SUB", "%SP", "$" + String.valueOf(count * 4), "%SP"), new InstructionDescription("Reserve space on the stack for " + count + " local variable" + (count == 1 ? "." : "s.")));
		}
	}
	
	/**
	 * Generates a function return instructions.
	 */
	public void genFunctionReturn() {
		enqueueInstruction(new OperandsInstruction("MOV", "%BP", "%SP"), new InstructionDescription("Function exit routine", "Move the value of the base pointer to the stack pointer. Since stack pointer has a new value (greater than before) there is a new 'top of the stack'. Practically the stack is \"popped\" to the beginning of the function (where base pointer was pointing to)."));
		enqueueInstruction(new OperandsInstruction("POP", "%BP"), new InstructionDescription("Pops the value from the stack (previous state of the base pointer) that was added in function entry routine and stores it into the base pointer. A value from the stack is removed and so the stack poitner is decremented."));
		enqueueInstruction(new OperandsInstruction("RET"), new InstructionDescription("Pops the value from the stack into the program counter register (the address of the next instruction to be executed), i.e. unconditionally jumps to the address in the PC register."));
	}
	
	/**
	 * Generates instructions for removing local variables from the stack.
	 * @param count
	 * 			number of local variables
	 */
	public void genClearLocVar(final int count) {
		if (count > 0) {
			enqueueInstruction(new OperandsInstruction("ADD", "%SP", "$" + String.valueOf(count * 4), "%SP"), new InstructionDescription("Removes " + count + " variables from the stack. Function arguments are removed in this way after the function call has finished."));
		}
	}
	
	/**
	 * Generates a call to a function.
	 * @param name
	 * 			function name
	 */
	public void genFunctionCall(final SymTabEntry function) {
		enqueueInstruction(new OperandsInstruction("CALL", printSymbol(function)), new InstructionDescription("Jumps to the labeled instruction and saves the next instruction address onto the stack as a return value (called function will eventually execute function exit routine and the RET instruction will use this value as a return address)."));
	}
	
	/**
	 * Generates a code that pushes an argument to the stack.
	 * @throws CodegenException
	 * 			if argument is in a wrong working register
	 */
	public void genPush(final SymTabEntry argument) throws CodegenException {
		fRegisters.releaseReg(argument);
		enqueueInstruction(new OperandsInstruction("PUSH", printSymbol(argument)), new InstructionDescription("Pushes an argument onto the stack. This is, for example, how arguments are passed to be used as function parameters."));
	}

	/**
	 * Generates a set of instructions that save the working registers that have values prior to a function call.
	 */
	public void genRegSave() {
		final Integer usedRegsCount= fRegisters.saveRegisterStates();
		for (int i= 0; i < usedRegsCount; i++) {
			enqueueInstruction(new OperandsInstruction("PUSH", "%" + i), new InstructionDescription("Save the state of the working registers that are still being used by the function before the function call gets executed."));
		}
	}
	
	/**
	 * Generates a set of instructions that repopulate the working registers from the stack after a function call has finished.
	 */
	public void genRegRestore() {
		final Integer previouslyUsedRegsCount= fRegisters.restoreRegisterStates();
		for (int i= previouslyUsedRegsCount; i >= 0; i--) {
			enqueueInstruction(new OperandsInstruction("POP", "%" + i), new InstructionDescription("Restores the state of the working registers that were still used just before the function call."));
		}
	}
	
	public SymTabEntry genString(final String string) {
		final String label= ".S" + fStringsCount++;
		enqueueInstruction(new LabelInstruction(label), fFunctionLabel, new InstructionDescription("Label pointing to the string initalization instruction."));
		enqueueInstruction(new OperandsInstruction(".string", string), fFunctionLabel, new InstructionDescription("String initialization instruction."));
		return new SymTabEntry(label, Definitions.STRING, new PointerType(Definitions.INT_TYPE));
	}
	
	/******************************************** OPERATIONS ********************************************/

	/**
	 * Generates the compare instruction.
	 * @param operand1
	 * 			first operand
	 * @param operand2
	 * 			second operand
	 * @throws CodegenException
	 * 			if any operand is in a wrong working register
	 */
	public void genCmp(final SymTabEntry operand1, final SymTabEntry operand2) throws CodegenException {
		enqueueInstruction(new OperandsInstruction(Definitions.CMPS[0], printSymbol(operand1), printSymbol(operand2)), new InstructionDescription("Compares the values of the two operands. Depending on the result, the flags bits are set."));
		fRegisters.releaseReg(operand1);
		fRegisters.releaseReg(operand2);
	}
	
	/**
	 * Generates an arithmetic instruction. It moves the result to a next free working register.
	 * @param operation
	 * 			arithmetic operation
	 * @param operand1
	 * 			first operand
	 * @param operand2
	 * 			second operand
	 * @return 
	 * 			the destination working register
	 * @throws CodegenException
	 * 			if input is in a wrong working register, or no free registers exist for storing a value
	 */
	public SymTabEntry genArith(final int operation, final SymTabEntry operand1, final SymTabEntry operand2) throws CodegenException {
		return genArith(operation, operand1, operand2, null);
	}

	/**
	 * Generates an arithmetic instruction.
	 * @param operation
	 * 			arithmetic operation
	 * @param operand1
	 * 			first operand
	 * @param operand2
	 * 			second operand
	 * @param output
	 * 			destination operand
	 * @return 
	 * 			the destination operand
	 * @throws CodegenException
	 * 			if input is in a wrong working register, or no free registers exist for storing a value
	 */
	public SymTabEntry genArith(final int operation, final SymTabEntry operand1, final SymTabEntry operand2, SymTabEntry output) throws CodegenException {
		final Type type= operand1.getType();
		fRegisters.releaseReg(operand1);
		fRegisters.releaseReg(operand2);
		if (output == null) {
			output= fRegisters.takeReg();
		}
		enqueueInstruction(new OperandsInstruction(Definitions.ARITHMETIC_OPERATORS[0][operation], printSymbol(operand1), printSymbol(operand2), printSymbol(output)), new InstructionDescription("Straight forwawrd binary arithmetic instruction. The arithemtic operation is executed on the first two operands and the result is stored in the third one."));
		if (operation == Definitions.MOD_OP) {
			fRegisters.releaseReg(output);
			output= fRegisters.getRemainderRegister();
		}
		output.setType(type);
		return output;
	}

	/**
	 * Generates the unary minus instruction. It moves result to a next free working register.
	 * @param input
	 * 			source operand
	 * @return
	 * 			the input operand
	 * @throws CodegenException
	 * 			if input is in a wrong working register, or no free registers exist for storing a value
	 */
	public SymTabEntry genUnaryMinus(final SymTabEntry input) throws CodegenException {
		SymTabEntry output= input;
		if (input.getKind() != Definitions.WORKING_REGISTER) {
			output= fRegisters.takeReg();
			genMov(input, output);
		}
		enqueueInstruction(new OperandsInstruction(Definitions.ARITHMETIC_OPERATORS[0][Definitions.SUB_OP], "$0,", printSymbol(output), printSymbol(output)), new InstructionDescription("Unary minus."));
		return output;
	}
	
	/**
	 * Generates the not instruction. It moves the result to a next free working register.
	 * @param input
	 * 			source operand
	 * @return
	 * 			the input operand
	 * @throws CodegenException
	 * 			if input is in a wrong working register, or no free registers exist for storing a value
	 */
	public SymTabEntry genNot(final SymTabEntry input) throws CodegenException {
		SymTabEntry output= input;
		if (input.getKind() != Definitions.WORKING_REGISTER) {
			output= fRegisters.takeReg();
			genMov(input, output);
		}
		enqueueInstruction(new OperandsInstruction(Definitions.ARITHMETIC_OPERATORS[0][Definitions.NOT_OP], printSymbol(output)), new InstructionDescription("Logical not."));
		return output;
	}
	
	/**
	 * Generates the inc or dec instruction. 
	 * @param input
	 * 			source operand
	 * @param operation
	 * 			inc or dec operation
	 * @return
	 * 			
	 * @throws CodegenException
	 * 			if input is in a wrong working register
	 */
	public void genIncDec(final SymTabEntry input, final int operation) throws CodegenException {
		enqueueInstruction(new OperandsInstruction(Definitions.ARITHMETIC_OPERATORS[0][operation], printSymbol(input)), new InstructionDescription("Increment / decrement."));
	}
	
	public void genIncDec(final SymTabEntry input, final int operation, final Integer lineNo) {
		enqueueInstruction(new OperandsInstruction(Definitions.ARITHMETIC_OPERATORS[0][operation], printSymbol(input)), new InstructionDescription("Increment / decrement."), lineNo);
	}
	
	/******************************************** DATA TRANSFER, POINTER & ARRAY SHIZZLE ********************************************/

	public SymTabEntry genMov(final SymTabEntry source, final SymTabEntry destination) throws CodegenException {
		if (source == destination) {
			return source;
		}
		if (isPointerToVariable(destination)) {
			return genMovToAddress(source, destination);
		} else {
			enqueueInstruction(new OperandsInstruction("MOV", printSymbol(source), printSymbol(destination)), new InstructionDescription("Moves the value from the first operand into the second."));
		}
		if (destination.getKind() == Definitions.WORKING_REGISTER && destination != fRegisters.getFunctionReturnRegister()) {
			destination.setType(source.getType());
			destination.setPointee(source.getPointee());
		}
		fRegisters.releaseReg(source);
		return destination;
	}

	private SymTabEntry genMovToAddress(final SymTabEntry source, final SymTabEntry destination) throws CodegenException {
		final SymTabEntry tempRegister= fRegisters.takeReg();
		enqueueInstruction(new OperandsInstruction("MOV", printSymbol(destination), printSymbol(tempRegister)), new InstructionDescription("Store the value of the second operand (memory address) into a next free working register."));
		if (destination.getArrayIndex() > 0) {
			enqueueInstruction(new OperandsInstruction("ADD", printSymbol(tempRegister), "$" + 4 * destination.getArrayIndex(), printSymbol(tempRegister)), new InstructionDescription("Memory offset."));
		}
		enqueueInstruction(new OperandsInstruction("MOV", printSymbol(source), "(" + printSymbol(tempRegister) + ")"), new InstructionDescription("Move the value of the the first operand into the memory location addressed by the value of the register."));
		if (destination.getKind() == Definitions.WORKING_REGISTER && destination != fRegisters.getFunctionReturnRegister()) {
			destination.setType(source.getType());
			destination.setPointee(source.getPointee());
		}
		fRegisters.releaseReg(source);
		fRegisters.releaseReg(tempRegister);
		return destination;
	}
	
	public SymTabEntry genMovToRegister(final SymTabEntry variable) throws CodegenException {
		final SymTabEntry declaredVariable= variable.getPointee();
		if (declaredVariable != null) {
			if (isPointer(variable) || isArray(variable) && declaredVariable.getKind() == Definitions.PARAMETER) {
				return genPointer(variable); 
			} else if (variable.getType() instanceof SimpleType && declaredVariable.getType() instanceof ArrayType) {
				return genLea(variable);
			}
		}
		return variable;
	}
	
	private SymTabEntry genPointer(final SymTabEntry input) throws CodegenException {
		final SymTabEntry inputCopy= new SymTabEntry(input);
		fRegisters.releaseReg(input);
		final SymTabEntry output= fRegisters.takeReg();
		//first level: MOV -8(%BP), output (getting local variable value into output before dereferencing)
		enqueueInstruction(new OperandsInstruction("MOV", printSymbol(input), printSymbol(output)), new InstructionDescription("Move the value of the operand into a next free working register (an address)."));
		//if it's pointer dereferencing, e.g. RESULT = *x
		if (input.getType() instanceof PointerType) {
			//for loop would be useful if pointerLevel above 1 was allowed by the compiler
			for (int i= 0; i < ((PointerType)input.getType()).getLevel(); i++) { 
				enqueueInstruction(new OperandsInstruction("MOV", "(" + printSymbol(output) + ")", printSymbol(output)), new InstructionDescription("Move the value stored in the memory location addressed by the value of the register into itself."));
			}
			//if it's array index, e.g. RESULT = x[2];
		} else if (input.getType() instanceof ArrayType) {
			String offsetString= "";
			int offset= 0;
			offset+= input.getArrayIndex();
			if (offset > 0) {
				offsetString= String.valueOf(offset * 4);
			}
			enqueueInstruction(new OperandsInstruction("MOV", offsetString + "(" + printSymbol(output) + ")", printSymbol(output)), new InstructionDescription("Move the value stored in the memory location addressed by the value of the register (and offsetted) into itself."));
		}
		if (output.getKind() == Definitions.WORKING_REGISTER) {
			output.setType(inputCopy.getType());
			output.setPointee(inputCopy.getPointee());
		}
		return output;
	}
	
	/**
	 * Generates the lea instruction (load effective address). It moves the result to a next free working register. 
	 * @param input
	 * 			input operand
	 * @return
	 * 			the destination operand
	 * @throws CodegenException
	 */
	public SymTabEntry genLea(final SymTabEntry input) throws CodegenException {
		final SymTabEntry inputCopy= new SymTabEntry(input);
		fRegisters.releaseReg(input);
		final SymTabEntry output= fRegisters.takeReg();
		enqueueInstruction(new OperandsInstruction("LEA", printSymbol(input), printSymbol(output)), new InstructionDescription("Loads the effetive address of the operand into a next free working register."));
		if (output.getKind() == Definitions.WORKING_REGISTER) {
			output.setType(inputCopy.getType());
			output.setPointee(inputCopy.getPointee());
		}
		return output;
	}
	
	private boolean isPointerToVariable(final SymTabEntry variable) {
		if (variable.getPointee() == null) {
			return false;
		}
		final SymTabEntry originalVariable= variable.getPointee();
		if (originalVariable.getType() instanceof PointerType && (variable.getType() instanceof PointerType || variable.getType() instanceof ArrayType)) {
			return true;
		} else if (isArray(variable) && originalVariable.getKind() == Definitions.PARAMETER) {
			return true;
		}
		return false;
	}
	
	private boolean isPointer(final SymTabEntry variable) {
		return variable.getType() instanceof PointerType || (variable.getType() instanceof ArrayType && variable.getPointee() != null && variable.getPointee().getType() instanceof PointerType);
	}

	private boolean isArray(final SymTabEntry variable) {
		return variable.getType() instanceof ArrayType && variable.getPointee() != null && variable.getPointee().getType() instanceof ArrayType;
	}
}
