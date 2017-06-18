var Util = new Util();

/**
 * Utilities class.
 * @class
 */ 
function Util() {

	/** @private */ var constants = new Constants();
	
	this.isKeyword = function(word) {
		if (word == "int") {
			return true;
		} else if (word == "return") {
			return true;
		} else if (word == "if") {
			return true;
		} else if (word == "else") {
			return true;
		} else if (word == "while") {
			return true;
		} else if (word == "sizeof") {
			return true;
		} else if (word == "unsigned") {
			return true;
		}
		return false;
	};
	
	this.getClassForOperation = function(operation) {
		if (operation == constants.JMP || operation == constants.JGE || operation == constants.JGT || operation == constants.JLT || operation == constants.JEQ || operation == constants.JNE) {
			return "jumpClass";
		} else if (operation == constants.CALL || operation == constants.RET) {
			return "functionClass";
		} else if (operation.contains(constants.MOV) || operation.contains(constants.LEA) || operation == constants.PUSH || operation == constants.POP) {
			return "dataClass";
		} else if (operation.contains(constants.ADD) || operation.contains("ADC") || operation.contains(constants.SUB) || operation.contains("SBC")|| operation.contains(constants.DIV) || 
			operation.contains(constants.MUL) || operation.contains(constants.CMP) || operation.contains(constants.AND) || operation.contains(constants.OR)) {
			return "arithClass";
		} else if (operation.contains("@") || operation.contains(":")) {
			return "labelClass";
		} else if (operation == constants.WORD || operation == constants.STRING) {
			return "staticClass";
		};
		return "";
	};
	
	this.isLabel = function(token) {
		if (token.endsWith(":")) {
			return true;
		};
		return false;
	};
	
	this.isJump = function(operation) {
		if (operation == constants.JMP) { 
			return true;
		};
		if (operation == constants.CALL) { 
			return true;
		}; 
		if (operation == constants.RET) { 
			return true;
		}; 
		if (operation == constants.JGE) { 
			return true;
		};
		if (operation == constants.JLE) { 
			return true;
		};
		if (operation == constants.JNE) { 
			return true;
		};
		if (operation == constants.JEQ) { 
			return true;
		};
		if (operation == constants.JGT) { 
			return true;
		};
		if (operation == constants.JLT) {
			return true;
		};
		return false;
	};
	
	this.getOperation = function(line) {
		var operationStr = line.split(new RegExp('\\s+'))[0];
		if (operationStr.startsWith(constants.MOV)) {
			return constants.MOV;
		} else if (operationStr.startsWith(constants.ADD)) {
			return constants.ADD;
		} else if (operationStr.startsWith(constants.SUB)) {
			return constants.SUB;
		} else if (operationStr.startsWith(constants.MUL)) {
			return constants.MUL;
		} else if (operationStr.startsWith(constants.DIV)) {
			return constants.DIV;
		} else if (operationStr.startsWith(constants.INC)) {
			return constants.INC;
		} else if (operationStr.startsWith(constants.DEC)) {
			return constants.DEC;
		} else if (operationStr.startsWith(constants.NOT)) {
			return constants.NOT;
		} else if (operationStr.startsWith(constants.AND)) {
			return constants.AND;
		} else if (operationStr.startsWith(constants.OR)) {
			return constants.OR;
		} else if (operationStr.startsWith(constants.XOR)) {
			return constants.XOR;
		} else if (operationStr.startsWith(constants.SHL)) {
			return constants.SHL;
		} else if (operationStr.startsWith(constants.SHR)) {
			return constants.SHR;
		} else if (operationStr.startsWith(constants.MOD)) {
			return constants.MOD;
		} else if (operationStr.startsWith(constants.PUSH)) {
			return constants.PUSH;
		} else if (operationStr.startsWith(constants.POP)) {
			return constants.POP;
		} else if (operationStr.startsWith(constants.JMP)) {
			return constants.JMP;
		} else if (operationStr.startsWith(constants.CALL)) {
			return constants.CALL;
		} else if (operationStr.startsWith(constants.RET)) {
			return constants.RET;
		} else if (operationStr.startsWith(constants.CMP)) {
			return constants.CMP;
		} else if (operationStr.startsWith(constants.JGT)) {
			return constants.JGT;
		} else if (operationStr.startsWith(constants.JGE)) {
			return constants.JGE;
		} else if (operationStr.startsWith(constants.JLT)) {
			return constants.JLT;
		} else if (operationStr.startsWith(constants.JLE)) {
			return constants.JLE;
		} else if (operationStr.startsWith(constants.JEQ)) {
			return constants.JEQ;
		} else if (operationStr.startsWith(constants.JNE)) {
			return constants.JNE;
		};
		//return the first token of the line
		return operationStr;
	};
	
	this.getOperands = function(line) {
		var lineSplit = line.split(new RegExp('\\s+'));
		if (lineSplit.length == 2) {
			return lineSplit[1].split(",");
		} else {
			return [""];
		};
	};
	
	this.getOperandType = function(operand) {
		operand = "" + operand;
		if (operand.contains("(")) {
			return constants.POINTER;
		} else if (operand.startsWith("$")) {
			return constants.CONSTANT;
		} else if (operand.startsWith("%")) {
			return constants.REGISTER;
		} else {
			return constants.GLOBAL;
		};
	};

};