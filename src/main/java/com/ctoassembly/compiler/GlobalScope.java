package com.ctoassembly.compiler;

/**
 * Represents a global scope of a c program</code>.
 * <pre>
 * {@code
 * // global scope starts here
 * int main() {
 * 	int x;
 * 	x = 2;
 * }
 * // global scope ends here
 * </pre>
 * 			
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public final class GlobalScope extends Scope {
	
	private Codegen fCodegen;
	
	public GlobalScope(final Codegen codegen) {
		super(null);
		fCodegen= codegen;
		initSymTable();
	}

	/**
	 * Initializes the global scope with the 'malloc' function.
	 */
	private void initSymTable() {
		try {
			final Function mallocFunction= fSymTable.insertFunction(new Function("malloc", new Type.SimpleType(Definitions.INT_TYPE)));
			mallocFunction.setParamType(0, new Type.SimpleType(Definitions.INT_TYPE));
			mallocFunction.setParameterCount(1);
		} catch (SymTabException e) {
			//This cannot happen.
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scope getChildScope(final Function function) {
		return new ParameterScope(fCodegen, function, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertVariable(final SymTabEntry variable, final SymTabEntry ... initValues) throws CompilerException {
		Semantic.assertArraySize(variable, initValues);
		
		variable.setKind(Definitions.GLOBAL_VAR);
		fSymTable.insertVariable(variable);
		
		int nbInitValues= variable.getArrayIndex() > initValues.length ? variable.getArrayIndex() : initValues.length;
		nbInitValues = nbInitValues == 0 ? 1 : nbInitValues;
		
		final Integer[] intInitValues= new Integer[nbInitValues];
		for (int i= 0; i < nbInitValues; i++) {
			if (i < initValues.length) {
				Semantic.assertValidRValueForGlobalVariableDeclaration(variable, initValues[i]);					
				//we can safely do this here because previous line asserted that it's a constant
				intInitValues[i]= Integer.decode(initValues[i].getName().replace("$", ""));
			} else {
				intInitValues[i]= 0;
			}
		}
		fCodegen.genGlobVar(variable, intInitValues);
	}

}