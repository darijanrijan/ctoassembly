package com.ctoassembly.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a CPU register symbol.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public final class Registers {

	private final Map<Integer, SymTabEntry> fRegisters= new HashMap<>();
	private int fFreeRegistersNum= 0;
	/**
	 * Register stack is used for saving used working registers prior to a function call.
	 */
	private final Stack<Integer> fRegisterStack= new Stack<>();
	
	public Registers() {
		for (int i= 0; i < 14; i++) {
			fRegisters.put(i, new SymTabEntry("%" + i, Definitions.WORKING_REGISTER, Type.getNoType()));
		}
	}
	
	/**
	 * Returns a free working register, if such register exists.
	 * @return
	 * 			a free working register
	 * @throws CodegenException
	 * 			if all working registers are used
	 */
	public SymTabEntry takeReg() throws CodegenException {
		if (fFreeRegistersNum == Definitions.LAST_WORKING_REGISTER + 1) {
			throw new CodegenException("No more free registers.");
		}
		return fRegisters.get(fFreeRegistersNum++);
	}
	
	/**
	 * Releases a working register.
	 * @param symbol
	 * 			a register to release
	 * @throws CodegenException
	 * 			if trying to release a register that has not been taken
	 */
	public void releaseReg(SymTabEntry symbol) throws CodegenException {
		if(symbol.getKind() == Definitions.WORKING_REGISTER) {
			final String index= symbol.getName().substring(1, symbol.getName().length());
			if (Integer.parseInt(index) >= 0 && Integer.parseInt(index) <= Definitions.LAST_WORKING_REGISTER) {
				symbol.setType(Type.getNoType());
				symbol.setPointee(null);
				if (fFreeRegistersNum-- < 0) {
					throw new CodegenException("No more registers to free");
				}
			}
		}
	}
	
	public Integer saveRegisterStates() {
		fRegisterStack.push(new Integer(fFreeRegistersNum));
		fFreeRegistersNum= 0;
		return fRegisterStack.peek();
	}
	
	public Integer restoreRegisterStates() {
		fFreeRegistersNum= fRegisterStack.pop().intValue();
		return fFreeRegistersNum - 1;
	}
	
	public SymTabEntry getFunctionReturnRegister() {
		return fRegisters.get(Definitions.FUNCTION_REGISTER);
	}
	
	public SymTabEntry getRemainderRegister() {
		return fRegisters.get(Definitions.DIV_REMAINDER_REGISTER);
	}
}
