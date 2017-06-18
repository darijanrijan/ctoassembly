var Const = new Constants();

function Constants() {

	/** @const */ this.MAX_INPUT_LENGTH = 2000;
	
	/** @const */ this.INT_MAX_UNSIGNED = 0xFFFFFFFF;
	/** @const */ this.INT_MAX_SIGNED = 0x80000000;
	/** @const */ this.MEMORY_START_OFFSET = 128;
	/** @const */ this.MEMORY_SIZE = 10;
	/** @const */ this.STACK_SIZE = 20;
	/** @const */ this.ADDRESS_SPACE_SIZE = this.MEMORY_START_OFFSET + this.MEMORY_SIZE + this.STACK_SIZE;		
	/** @const */ this.ADDRESS_OFFSET = 4;		
	/** @const */ this.REGISTERS_SIZE = 14;		
	/** @const */ this.GLOBALS_OFFSET_FROM_STACK = 8;		
	
	/** ASM Labels and Addresses */
	
	/** @const */ this.STACK_POINTER = "%SP";
	/** @const */ this.BASE_POINTER = "%BP";
	/** @const */ this.POINTER = "POINTER";
	/** @const */ this.CONSTANT = "CONSTANT";
	/** @const */ this.REGISTER = "REGISTER";
	/** @const */ this.GLOBAL = "GLOBAL";
	/** @const */ this.FUNCTION_RETURN_REGISTER = "%13";
	
	/** ASM operations */
	
	/** @const */ this.MOV = "MOV";
	/** @const */ this.LEA = "LEA";
	/** @const */ this.ADD = "ADD";
	/** @const */ this.SUB = "SUB";
	/** @const */ this.MUL = "MUL";
	/** @const */ this.DIV = "DIV";
	/** @const */ this.INC = "INC";
	/** @const */ this.DEC = "DEC";
	/** @const */ this.MOD = "MOD";
	/** @const */ this.NOT = "NOT";
	/** @const */ this.AND = "AND";
	/** @const */ this.OR = "OR";
	/** @const */ this.XOR = "XOR";
	/** @const */ this.SHL = "SHL";
	/** @const */ this.SHR = "SHR";
	/** @const */ this.CMP = "CMP";
	/** @const */ this.CALL = "CALL";
	/** @const */ this.RET = "RET";
	/** @const */ this.PUSH = "PUSH";
	/** @const */ this.POP = "POP";
	/** @const */ this.JMP = "JMP";
	/** @const */ this.JGT = "JGT";
	/** @const */ this.JGE = "JGE";
	/** @const */ this.JLT = "JLT";
	/** @const */ this.JLE = "JLE";
	/** @const */ this.JEQ = "JEQ";
	/** @const */ this.JNE = "JNE";
	/** @const */ this.WORD = ".word";
	/** @const */ this.STRING = ".string";

};