package com.ctoassembly.compiler;

/**
 * Represents a function scope that starts with <code>{</code> right after function definition and ends with <code>}</code>.
 * <pre>
 * {@code
 * int main() { // function scope starts here
 * 	int x;
 * } // function scope ends here
 * </pre>
 * 			
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class FunctionScope extends Scope {

	private final Codegen fCodegen;
	protected int fVarCount= 0;

	public FunctionScope(final Codegen fCodegen, final Scope parentScope) {
		super(parentScope);
		this.fCodegen = fCodegen;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scope getChildScope(final Function function) {
		return new BlockScope(fCodegen, this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertVariable(final SymTabEntry variable, final SymTabEntry ... initValues) throws CompilerException {
		Semantic.assertArraySize(variable, initValues);
		
		variable.setKind(Definitions.LOCAL_VAR);
		fSymTable.insertVariable(variable);
		
		int nbInitValues= variable.getArrayIndex() > initValues.length ? variable.getArrayIndex() : initValues.length;
		nbInitValues = nbInitValues == 0 ? 1 : nbInitValues;
		
		fVarCount+= nbInitValues;
		variable.setAttribute(fVarCount - 1);

		fCodegen.genLocVars(nbInitValues);
		
		//int x[3] = {1, 2, 3};
		final SymTabEntry offsetVariable= new SymTabEntry(variable);
		for (int i= 0; i < nbInitValues; i++) {
			//x[0] is at the lowest position on the stack, x[2] is at the highest
			offsetVariable.setAttribute(variable.getAttribute() - i);
			if (i < initValues.length) {
				fCodegen.genMov(initValues[i], offsetVariable);
			}
		}
	}
}