package com.ctoassembly.compiler;

import com.ctoassembly.compiler.Type.ArrayType;
import com.ctoassembly.compiler.Type.PointerType;
import com.ctoassembly.compiler.Type.SimpleType;


/**
 * Utility class that checks semantics of the code that is being parsed.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public final class Semantic {

	/**
	 * Default private constructor. The class should not be instantiated.
	 */
	private Semantic() {
	}
	
	static void assertValidConstant(final int constant) throws SemanticsException {
		if (constant > Definitions.INT_MAX_32_BIT || constant < ~Definitions.INT_MAX_32_BIT) {
			throw new SemanticsException("Constant: " + constant + " exceeds 32 bit size");
		}
	}
	
	/**
	 * Asserts whether the program has the 'main' function.
	 * 
	 * @throws CompilerException 
	 */
	static void assertHasMain(final Context context) throws CompilerException {
		context.getFunction("main");
	}
	
	static void assertArraySize(final SymTabEntry variable, final SymTabEntry[] initValues) throws CompilerException {
		if (variable.getArrayIndex() == -1 && initValues == null) {
			throw new SemanticsException("array size missing in '" + variable.getName() + "'");
		} 
		if (variable.getArrayIndex() > 0 && variable.getArrayIndex() < initValues.length) {
			throw new SemanticsException("excess elements in array initializer in '" + variable.getName() + "'");
		}
	}
	
	/**
	 * Asserts whether the assign operation is valid for declaring global variable.
	 * Only allowed equal token is '=' whereas, '+=', '-=', etc. are disallowed.
	 * @param assign
	 * 			assign token
	 * @throws SemanticsException
	 * 			if the assign token is not '='
	 */
	static void assertValidAssignForVariableDeclaration(final int assign) throws SemanticsException {
		if (assign != Definitions.ASSIGN_OP) {
			throw new SemanticsException("Expected '=' or ';' before " + Definitions.ASSIGN_TOKEN[assign] + " token");
		}
	}
	
	/**
	 * Asserts whether a proper symbol is being assign to a global variable symbol.
	 * @param variable
	 * 			global variable
	 * @param rvalue
	 * 			right value
	 * @throws CompilerException
	 * 			if the right value is not a constant of int type
	 */
	static void assertValidRValueForGlobalVariableDeclaration(final SymTabEntry variable, final SymTabEntry rvalue) throws CompilerException {
		if (rvalue.getType().getInnerType() != Definitions.INT_TYPE || rvalue.getKind() != Definitions.CONSTANT) {
			throw new CompilerException("Initializer element of global variable " + variable.getName() + " is not constant");
		}
	}
	
	/**
	 * Asserts whether a symbol is a proper left value, i.e. whether the variable has proper pointer level.
	 * For example, it is wrong to assign a value to an address of a variable, i.e. <code>&x = value</code>.
	 * @param sym
	 * 			left value
	 * @throws CompilerException 
	 */
	static void assertValidLValue(final SymTabEntry variable, final Context context) throws CompilerException {
		if (variable.getType() instanceof PointerType && ((PointerType)variable.getType()).getLevel() < 0) {
			throw new SemanticsException("Expected identifier or '(' before '&' token");
		} 
		final SymTabEntry originalVariable= context.getVariable(variable.getName());
		if (originalVariable.getType() instanceof ArrayType && variable.getType() instanceof SimpleType) {
			throw new SemanticsException("incompatible types when assigning to type ‘" + Definitions.TYPE_NAMES[variable.getType().getInnerType()] + "[" + originalVariable.getArrayIndex() + "]’ from type ‘" + Definitions.TYPE_NAMES[variable.getType().getInnerType()] + "’");
		}
	}

	/**
	 * Asserts whether two types are matching for particular operation.
	 * @param type1
	 * 			type of the first symbol
	 * @param type2
	 * 			type of the second symbol
	 * @param operation
	 * 			operation for which the types are checked
	 * @throws SemanticsException
	 * 			if the types are not matching
	 */
	static void assertTypes(final Type type1, final Type type2, final String operation) throws SemanticsException {
		if (type1.getInnerType() != type2.getInnerType()) {
			throw new SemanticsException("Incompatible types to " + operation);
		}
	}
	
	/**
	 * Asserts whether the function parameter number matches the argument number.
	 * @param function
	 * 			function
	 * @param argsCount
	 * 			number of arguments
	 * @throws SemanticsException
	 * 			if the number of arguments does not match function's number of parameters
	 */
	static void assertArgumentsNumber(final Function function, final int argsCount) throws SemanticsException {
		if (function.getParameterCount() != argsCount) {
			throw new SemanticsException("Wrong number of arguments to function: " + function.getName());
		}
	}

	/**
	 * Asserts whether the function parameter's type is the same as a type of the symbol.
	 * @param function
	 * 			function
	 * @param argument
	 * 			argument whose type is being examined
	 * @param argNum
	 * 			ordinal of the argument
	 * @throws SemanticsException
	 * 			if the types of the function's parameter and the argument are not matching
	 */
	static void assertArgumentType(final Function function, final SymTabEntry argument, int argNum) throws SemanticsException {
		Type expectedParamType= function.getParamType(argNum);
		if (expectedParamType.getInnerType() != argument.getType().getInnerType()) {
			String expected= "no type";
			if (expectedParamType.getInnerType() == Definitions.INT_TYPE) {
				expected= "int";
			}
			throw new SemanticsException("Incompatible type for argument: " + argument.getName() + " in function: " + function.getName() + "\n" + "Expected: " + expected);
		}
	}

	/**
	 * Asserts if an operand has valid pointer level for the operation.
	 * @param operand
	 * 			operand
	 * @param operationId
	 * 			operation identifier
	 * @param operation
	 * 			operation name
	 * @param context
	 * 			table of symbols
	 * @throws CompilerException 
	 */
	static void assertValidOperandPointerLevel(final SymTabEntry operand, final Integer operationId, final String operation, final Context context) throws CompilerException {
		assertValidOperandPointerLevel(operand, operand, operationId, operation, context);
	}
	
	/**
	 * Asserts if two operands are valid in regard to their pointer type and the operation. For example, if <code>x</code> and <code>y</code> are two variables of type <code>int*</code>,
	 * valid operation is <code>x + 2</code>, but not <code>x * 2</code>. Also, <code>*x + y</code> is allowed, whereas <code>x + y</code> is not. Similarly, <code>&x * 2</code> is disallowed.
	 * @param operand1
	 * 			first operand
	 * @param operand2
	 * 			second operand
	 * @param operationId
	 * 			operation identifier
	 * @param operation
	 * 			operation name
	 * @param context
	 * 			table of symbols
	 * @throws CompilerException 
	 */
	static void assertValidOperandPointerLevel(final SymTabEntry operand1, final SymTabEntry operand2, final Integer operationId, final String operation, final Context context) throws CompilerException {
		final SymTabEntry parentVariable1 = getParentVariable(operand1, context);
		final SymTabEntry parentVariable2 = getParentVariable(operand2, context);
		if (parentVariable1 != null) {
			if (operationId == Definitions.NOT_OP) {
				if (isDereferenced(parentVariable1, operand1)) {
					throw new SemanticsException("Invalid operands to " + operation);
				}
			}
			if (parentVariable2 != null) {
				if (operationId == Definitions.ADD_OP || operationId == Definitions.SUB_OP) {
					if (isDereferenced(parentVariable1, operand1) && isDereferenced(parentVariable2, operand2)) {
						throw new SemanticsException("Invalid operands to " + operation);
					}
				} else {
					if (isDereferenced(parentVariable1, operand1) || isDereferenced(parentVariable2, operand2)) {
						throw new SemanticsException("Invalid operands to " + operation);
					}
				}
			}
		}
	}

	/**
	 * Returns the parent variable of an operand.
	 * @param operand
	 * 			operand
	 * @param context
	 * 			table of symbols
	 * @return
	 * 			parent variable of an operand
	 * @throws CompilerException 
	 */
	private static SymTabEntry getParentVariable(final SymTabEntry operand, final Context context) throws CompilerException {
		if (operand.getKind() == Definitions.WORKING_REGISTER) {
			return operand.getPointee();
		} else {
			return context.getVariable(operand.getName());
		}
	}
	
	/**
	 * Checks whether the variable is dereferenced.
	 * @param parent
	 * 			original variable
	 * @param child
	 * 			variable being used
	 * @return
	 * 			<tt>true</tt>if variable is being dereferenced, otherwise <tt>false</tt>
	 */
	private static boolean isDereferenced(final SymTabEntry parent, final SymTabEntry child) {
		return parent.getType() instanceof PointerType && child.getType() instanceof SimpleType || parent.getType() instanceof ArrayType && child.getType() instanceof SimpleType; 
	}
	
}