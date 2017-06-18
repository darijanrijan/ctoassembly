package com.ctoassembly.compiler;

/**
 * Constants.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
final class Definitions {

	/** 
	 * Default private constructor. The class should not be instantiated.
	 */
	private Definitions() {
	}
	
	public static final int INT_MAX_32_BIT= 0x7FFFFFFF;
	public static final int SYMBOL_TABLE_LENGTH= 40;
	public static final int PARAM_NUMBER= 5;
	
	/** Important registers */
	public static final int LAST_WORKING_REGISTER= 11;
	public static final int DIV_REMAINDER_REGISTER= 12;
	public static final int FUNCTION_REGISTER= 13;

	/** Context levels */
	public static final int GLOBAL_LEVEL= 0x4;
	public static final int PARAMETER_LEVEL= 0x10;
	public static final int LOCAL_LEVEL= 0x20;
	
	/** Symbol types */
	public static final int NO_TYPE= 0;
	public static final int INT_TYPE= 1;
	public static final int UNSIGNED_TYPE= 2;
	
	/** Symbol kinds */
	public static final int NO_KIND= 0x1;
	public static final int WORKING_REGISTER= 0x2;
	public static final int GLOBAL_VAR= 0x4;
	public static final int FUNCTION= 0x8;
	public static final int PARAMETER= 0x16;
	public static final int LOCAL_VAR= 0x32;
	public static final int CONSTANT= 0x64;
	public static final int STRING= 0x128;
	
	/** Operations */
	public static final int ASSIGN_OP= -1; // =
	public static final int ADD_OP= 0; // +
	public static final int SUB_OP= 1; // -
	public static final int MUL_OP= 2; // *
	public static final int DIV_OP= 3; // /
	public static final int XOR_OP= 4; // ^
	public static final int OR_OP= 5; // |
	public static final int AND_OP= 6; // &
	public static final int NOT_OP= 7; // ~
	public static final int INC_OP= 8; // ++
	public static final int DEC_OP= 9; // --
	public static final int SHIFT_LEFT_OP= 10; // <<
	public static final int SHIFT_RIGHT_OP= 11; // >>
	public static final int MOD_OP= 12; // %
	public static final int LOG_AND= 13;
	public static final int LOG_OR= 14;
	
	/** Relations */
	public static final int LT= 0; 
 	public static final int GT= 1;
	public static final int LE= 2;
	public static final int GE= 3;
	public static final int EQ= 4;
	public static final int NE= 5;

	/** Symbol and label names */
	public static final String OP_TOKEN[]= { "+", "-", "*", "/", "^", "|", "&", "~", "++", "--", "<<", ">>", "%", "&&", "||" };
	public static final String ASSIGN_TOKEN[]= { "+=", "-=", "*=", "/=", "^=", "|=", "&=", "", "", "", "<<=", ">>=", "%=", "", "" };
	public static final String symbol_kinds[]= { "NONE", "WORKING_REGISTER", "GLOBAL_VAR", "FUNCTION", "PARAMETER", "LOCAL_VAR", "CONSTANT" };
	public static final String ARITHMETIC_OPERATORS[][]= { { "ADD", "SUB", "MUL", "DIV", "XOR", "OR", "AND", "NOT", "INC", "DEC", "SHL", "SHR", "DIV" },
														   { "ADD", "SUB", "MUL", "DIV", "XOR", "OR", "AND", "NOT", "INC", "DEC", "SHL", "SHR", "DIV" } };
	public static final String JUMPS[]= { "JLT", "JGT", "JLE", "JGE", "JEQ", "JNE",
										  "JLT", "JGT", "JLE", "JGE", "JEQ", "JNE" };
    public static final String OPPOSITE_JUMPS[]= { "JGE", "JLE", "JGT", "JLT", "JNE", "JEQ",
											       "JGE", "JLE", "JGT", "JLT", "JNE", "JEQ" };
	public static final String UNCONDITIONAL_JUMP= "JMP";
	public static final String CMPS[]= { "CMP", "CMP" };
	public static final String TYPE_NAMES[]= { "void", "int", "unsigned int" };

}
