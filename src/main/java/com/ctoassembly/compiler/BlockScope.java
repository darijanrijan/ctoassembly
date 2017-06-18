package com.ctoassembly.compiler;

/**
 * Represents a scope inside a function that starts with <code>{</code> and ends with <code>}</code>.
 * <pre>
 * {@code
 * int main() {
 * 	int x;
 * 	{ // block scope starts here
 * 		x = 2;
 * 	} // block scope ends here
 * }
 * </pre>
 * 			
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class BlockScope extends FunctionScope {

	public BlockScope(final Codegen codegen, final FunctionScope parentScope) {
		super(codegen, parentScope);
		fVarCount= parentScope.fVarCount;
	}

}
