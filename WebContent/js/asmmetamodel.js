function Operand(operandName, cpuModel) {
	if (operandName.contains("(")) {
		var offset = operandName.substring(0, operandName.indexOf("(")) || 0;
		offset = parseInt(isNaN(offset) ? 0 : offset);
		var register = operandName.substring(operandName.indexOf("(") + 2, operandName.indexOf(")"));
		return new PointerOperand(offset, new RegisterOperand(register, cpuModel), cpuModel);
	} else if (operandName.startsWith("$")) {
		return new ImmediateOperand(operandName.substring(1), cpuModel);
	} else if (operandName.startsWith("%")) {
		return new RegisterOperand(operandName.substring(1), cpuModel);
	} else {
		return new GlobalOperand(operandName, cpuModel);
	};
	return null;
};

function RegisterOperand(operandNumber, cpuModel) {
	this.operandNumber = operandNumber;
	this.cpuModel = cpuModel;
};
RegisterOperand.prototype.getValue = function() {
	if (this.operandNumber == "SP") {
		return this.cpuModel.stack.getSP();
	} else if (this.operandNumber == "BP") {
		return this.cpuModel.stack.getBP();
	};
	return this.cpuModel.registers.getValue(this.operandNumber);
};
RegisterOperand.prototype.setValue = function(value) {
	if (this.operandNumber == "SP") {
		this.cpuModel.stack.setSP(value);
	} else if (this.operandNumber == "BP") {
		this.cpuModel.stack.setBP(value);
	};
	this.cpuModel.registers.setValue(this.operandNumber, value);
};

function PointerOperand(offset, registerOperand, cpuModel) {
	this.offset = offset;
	this.registerOperand = registerOperand;
	this.cpuModel = cpuModel;
};
PointerOperand.prototype.getValueAtAddress = function(address) {
	//If the address is not divisible by address offset (i.e. 4 bytes) then it is calculated from two consecutive addresses.
	var modulo = address % Const.ADDRESS_OFFSET;
	var firstAddr = address - modulo;
	var secondAddr = firstAddr + Const.ADDRESS_OFFSET;
	var firstValue;
	var secondValue;
	//Index on the stack.
	if (firstAddr > (Const.ADDRESS_SPACE_SIZE - Const.STACK_SIZE) * Const.ADDRESS_OFFSET && firstAddr <= Const.ADDRESS_SPACE_SIZE * Const.ADDRESS_OFFSET) {
		firstValue = this.cpuModel.stack.getValue(firstAddr);
		secondValue = this.cpuModel.stack.getValue(secondAddr);
	//Index on the memory.
	} else { //if (firstAddr >= Const.MEMORY_START_OFFSET * Const.ADDRESS_OFFSET && firstAddr <= (Const.MEMORY_START_OFFSET + Const.MEMORY_SIZE) * Const.ADDRESS_OFFSET) {
		firstValue = this.cpuModel.memory.getValue(firstAddr);
		secondValue = this.cpuModel.memory.getValue(secondAddr);
	};
	firstValue = firstValue >>> (modulo * 8);
	//http://stackoverflow.com/questions/6798111/bitwise-operations-on-32-bit-unsigned-ints
	secondValue = (lshift(secondValue, (Const.ADDRESS_OFFSET - modulo) * 8) & Const.INT_MAX_UNSIGNED) >>> 0;
	if (modulo == 0) {
		return firstValue;
	} else {
		return firstValue + secondValue;
	};
};
PointerOperand.prototype.setValueAtAddress = function(address, value) {
	//If the address is not divisible by address offset (i.e. 4 bytes) then it is calculated from two consecutive addresses.
	var modulo = address % Const.ADDRESS_OFFSET;
	var firstAddr = address - modulo;
	var secondAddr = firstAddr + Const.ADDRESS_OFFSET;
	//http://stackoverflow.com/questions/6798111/bitwise-operations-on-32-bit-unsigned-ints
	var firstValue = (lshift(value, modulo * 8) & Const.INT_MAX_UNSIGNED) >>> 0;
	var secondValue = value >>> (Const.ADDRESS_OFFSET - modulo);
	//Index on the stack.
	if (firstAddr > (Const.ADDRESS_SPACE_SIZE - Const.STACK_SIZE) * Const.ADDRESS_OFFSET && firstAddr <= Const.ADDRESS_SPACE_SIZE * Const.ADDRESS_OFFSET) {
		this.cpuModel.stack.setValue(firstAddr, firstValue);
		if (modulo != 0) {
			this.cpuModel.stack.setValue(secondAddr, secondValue);
		};
	//Index on the memory.
	} else { //if (firstAddr >= Const.MEMORY_START_OFFSET * Const.ADDRESS_OFFSET && firstAddr <= (Const.MEMORY_START_OFFSET + Const.MEMORY_SIZE) * Const.ADDRESS_OFFSET) {
		this.cpuModel.memory.setValue(firstAddr, firstValue);
		if (modulo != 0) {
			this.cpuModel.memory.setValue(secondAddr, secondValue);
		};
	};
};
PointerOperand.prototype.getValue = function() {
	var address = this.registerOperand.getValue() + this.offset;
	return this.getValueAtAddress(address);
};
PointerOperand.prototype.setValue = function(value) {
	var address = this.registerOperand.getValue() + this.offset;
	this.setValueAtAddress(address, value);
};
PointerOperand.prototype.getAddress = function() {
	return this.registerOperand.getValue() + this.offset;
};

function ImmediateOperand(value, cpuModel) {
	if (value.startsWith(".S")) {
		this.value = parseInt(cpuModel.asmcode.findLabelInstructionPointer(value));
	} else {
		this.value = parseInt(value);
	}
};
ImmediateOperand.prototype.getValue = function() {
	return this.value;
};

function GlobalOperand(globalName, cpuModel) {
	this.globalName = globalName;
	this.cpuModel = cpuModel;
};
GlobalOperand.prototype.getValue = function() {
	return this.cpuModel.globals.getValue(this.globalName);
};
GlobalOperand.prototype.setValue = function(value) {
	this.cpuModel.globals.setValue(this.globalName, value);
};

/**
 * Call this method without the 'new' keyword as this is a factory method.
 */
function Operation(operationName) {
	if (operationName.startsWith(Const.MOV)) {
		return new MovOp();
	} else if (operationName.startsWith(Const.ADD)) {
		return new AddOp();
	} else if (operationName.startsWith(Const.SUB)) {
		return new SubOp();
	} else if (operationName.startsWith(Const.MUL)) {
		return new MulOp();
	} else if (operationName.startsWith(Const.DIV)) {
		return new DivOp();
	} else if (operationName.startsWith(Const.INC)) {
		return new IncOp();
	} else if (operationName.startsWith(Const.DEC)) {
		return new DecOp();
	} else if (operationName.startsWith(Const.NOT)) {
		return new NotOp();
	} else if (operationName.startsWith(Const.AND)) {
		return new AndOp();
	} else if (operationName.startsWith(Const.OR)) {
		return new OrOp();
	} else if (operationName.startsWith(Const.XOR)) {
		return new XorOp();
	} else if (operationName.startsWith(Const.SHL)) {
		return new ShlOp();
	} else if (operationName.startsWith(Const.SHR)) {
		return new ShrOp();
	} else if (operationName.startsWith(Const.MOD)) {
		return new ModOp();
	} else if (operationName.startsWith(Const.LEA)) {
		return new LeaOp();
	} else if (operationName.startsWith(Const.CMP)) {
		return new CmpOp();
	} else if (operationName.startsWith(Const.PUSH)) {
		return new PushOp();
	} else if (operationName.startsWith(Const.POP)) {
		return new PopOp();
	} else if (operationName.startsWith(Const.JMP)) {
		return new JmpOp();
	} else if (operationName.startsWith(Const.CALL)) {
		return new CallOp();
	} else if (operationName.startsWith(Const.RET)) {
		return new RetOp();
	} else if (operationName.startsWith(Const.CMP)) {
		return new CmpOp();
	} else if (operationName.startsWith(Const.JGT)) {
		return new JgtOp();
	} else if (operationName.startsWith(Const.JGE)) {
		return new JgeOp();
	} else if (operationName.startsWith(Const.JLT)) {
		return new JltOp();
	} else if (operationName.startsWith(Const.JLE)) {
		return new JleOp();
	} else if (operationName.startsWith(Const.JEQ)) {
		return new JeqOp();
	} else if (operationName.startsWith(Const.JNE)) {
		return new JneOp();
	};
	return null;
};

/** Move operation */
function MovOp() {};
MovOp.prototype.execute = function(context) {
	var result = context.operand1.getValue();
	context.destinationOperand.setValue(result);
};

/** Addition operation */
function AddOp() {};
AddOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() + context.operand2.getValue();
	var resultNormalized = normalize(result);
	var carry = (result > Const.INT_MAX_UNSIGNED) | 0;
	//http://en.wikipedia.org/wiki/Overflow_flag
	//http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt
	var overflow = ((context.operand1.getValue() < Const.INT_MAX_SIGNED && context.operand2.getValue() < Const.INT_MAX_SIGNED && resultNormalized >= Const.INT_MAX_SIGNED) 
				|| (context.operand1.getValue() >= Const.INT_MAX_SIGNED && context.operand2.getValue() >= Const.INT_MAX_SIGNED && resultNormalized < Const.INT_MAX_SIGNED)) | 0;
	context.destinationOperand.setValue(resultNormalized);
	context.cpuModel.setFlags(resultNormalized, carry, overflow);
};

/** Subtraction operation */
function SubOp() {};
SubOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() - context.operand2.getValue();
	var resultNormalized = normalize(result);
	var carry = (result < 0) | 0;
	//http://en.wikipedia.org/wiki/Overflow_flag
	//http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt
	var overflow = ((context.operand1.getValue() < Const.INT_MAX_SIGNED && context.operand2.getValue() >= Const.INT_MAX_SIGNED && resultNormalized >= Const.INT_MAX_SIGNED) 
				|| (context.operand1.getValue() >= Const.INT_MAX_SIGNED && context.operand2.getValue() < Const.INT_MAX_SIGNED && resultNormalized < Const.INT_MAX_SIGNED)) | 0;
	context.destinationOperand.setValue(resultNormalized);
	context.cpuModel.setFlags(resultNormalized, carry, overflow);
};

/** Multiplication operation */
function MulOp() {};
MulOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() * context.operand2.getValue();
	var resultNormalized = normalize(result);
	var carry = (result > Const.INT_MAX_SIGNED) | 0; //equal to overflow
	context.destinationOperand.setValue(resultNormalized);
	Operand("%12", context.cpuModel).setValue(result >> 32);
	context.cpuModel.setFlags(resultNormalized, carry, carry);
};

/** Division operation */
function DivOp() {};
DivOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() / context.operand2.getValue();
	var resultNormalized = normalize(result);
	context.destinationOperand.setValue(resultNormalized);
	Operand("%12", context.cpuModel).setValue(context.operand1.getValue() % context.operand2.getValue());
	context.cpuModel.setFlags(resultNormalized, 0, 0);
};

/** Modulo operation */
function ModOp() {};
ModOp.prototype = new DivOp();

/** Logical And operation */
function AndOp() {};
AndOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() & context.operand2.getValue();
	context.destinationOperand.setValue(result);
	//http://courses.engr.illinois.edu/ece390/books/artofasm/CH06/CH06-3.html
	context.cpuModel.setFlags(result, 0, 0);
};

/** Logical Or operation */
function OrOp() {};
OrOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() | context.operand2.getValue();
	context.destinationOperand.setValue(result);
	//http://courses.engr.illinois.edu/ece390/books/artofasm/CH06/CH06-3.html
	context.cpuModel.setFlags(result, 0, 0);
};

/** Logical Xor operation */
function XorOp() {};
XorOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() ^ context.operand2.getValue();
	context.destinationOperand.setValue(result);
	//http://courses.engr.illinois.edu/ece390/books/artofasm/CH06/CH06-3.html
	context.cpuModel.setFlags(result, 0, 0);
};

/** Logical Not operation */
function NotOp() {};
NotOp.prototype.execute = function(context) {
	var result = -context.operand1.getValue() - 1;
	context.destinationOperand.setValue(result);
	//http://courses.engr.illinois.edu/ece390/books/artofasm/CH06/CH06-3.html
	context.cpuModel.setFlags(result, 0, 0);
};

/** Shift left operation */
function ShlOp() {};
ShlOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() << context.operand2.getValue();
	var resultNormalized = normalize(result);
	var carry = context.operand1.getValue() > (Const.INT_MAX_UNSIGNED >> context.operand2.getValue());
	context.destinationOperand.setValue(result);
	context.cpuModel.setFlags(resultNormalized, carry, 0);
};

/** Shift right operation */
function ShrOp() {};
ShrOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() >> context.operand2.getValue();
	var carry = context.operand1.getValue() & ((1 << context.operand2.getValue()) - 1);
	context.destinationOperand.setValue(result);
	context.cpuModel.setFlags(result, carry, 0);
};

/** Inc (++) operation */
function IncOp() {};
IncOp.prototype.execute = function(context) {
	Operation("ADD", context.cpuModel).execute({
		operand1 : context.operand1,
		operand2 : Operand("$1"),
		destinationOperand : context.destinationOperand,
		cpuModel : context.cpuModel
	});
};

/** Dec (--) operation */
function DecOp() {};
DecOp.prototype.execute = function(context) {
	Operation("SUB", context.cpuModel).execute({
		operand1 : context.operand1,
		operand2 : Operand("$1"),
		destinationOperand : context.destinationOperand,
		cpuModel : context.cpuModel
	});
};

/** Load effective address operation */
function LeaOp() {};
LeaOp.prototype.execute = function(context) {
	var result = context.operand1.getAddress();
	context.destinationOperand.setValue(result);
};

/** Compare operation */
function CmpOp() {};
CmpOp.prototype.execute = function(context) {
	var result = context.operand1.getValue() - context.operand2.getValue();
	var resultNormalized = normalize(result);
	var carry = (result < 0) | 0;
	var overflow = ((context.operand1.getValue() < Const.INT_MAX_SIGNED && context.operand2.getValue() >= Const.INT_MAX_SIGNED && resultNormalized >= Const.INT_MAX_SIGNED) 
				|| (context.operand1.getValue() >= Const.INT_MAX_SIGNED && context.operand2.getValue() < Const.INT_MAX_SIGNED && resultNormalized < Const.INT_MAX_SIGNED)) | 0;
	context.cpuModel.cmp = result;
	context.cpuModel.setFlags(resultNormalized, carry, overflow);
};

/** Push to the stack operation */
function PushOp() {};
PushOp.prototype.execute = function(context) {
	context.cpuModel.stack.push(context.operand1.getValue());
};

/** Pop from the stack operation */
function PopOp() {};
PopOp.prototype.execute = function(context) {
	context.destinationOperand.setValue(context.cpuModel.stack.pop());
};

/** Call function operation */
function CallOp() {};
CallOp.prototype.execute = function(context) {
	if (context.operand1.label == "malloc") {
		new Operand(Const.FUNCTION_RETURN_REGISTER, context.cpuModel).setValue(context.cpuModel.memory.getNextFreeLocation());
		context.cpuModel.instructionPointer = context.cpuModel.instructionPointer + Const.ADDRESS_OFFSET;
	} else if (context.operand1.label == "free") {
		//TODO not yet implemented
	} else {
		//put next address into instruction pointer
		context.cpuModel.stack.push(context.cpuModel.instructionPointer + Const.ADDRESS_OFFSET);
		context.cpuModel.instructionPointer = context.operand1.labelAddress;
	};
};

/** Return from function operation */
function RetOp() {};
RetOp.prototype.execute = function(context) {
	//special case, last RET
	if (context.cpuModel.stack.getSP() == (Const.ADDRESS_SPACE_SIZE + 1) * Const.ADDRESS_OFFSET && context.cpuModel.stack.getBP() == context.cpuModel.stack.getSP()) {
		context.cpuModel.instructionPointer = null;
	} else {
		context.cpuModel.instructionPointer = context.cpuModel.stack.pop();
	}
};

/** Jump operation */
function JmpOp() {};
JmpOp.prototype.execute = function(context) {
	if (this.isSuccessful(context)) {
		context.cpuModel.instructionPointer = context.operand1.labelAddress;
	} else {
		context.cpuModel.instructionPointer = context.operand1.fallbackAddress;
	};
};
JmpOp.prototype.isSuccessful = function(context) {
	return true;
};

/** Jump if equal operation */
function JeqOp() {
	JeqOp.base.constructor.call(this);
};
extend(JmpOp, JeqOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp == 0;
	}
});

/** Jump if not equal operation */
function JneOp() {
	JneOp.base.constructor.call(this);
};
extend(JmpOp, JneOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp != 0;
	}
});

/** Jump if greater or equal operation */
function JgeOp() {
	JgeOp.base.constructor.call(this);
};
extend(JmpOp, JgeOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp >= 0;
	}
});

/** Jump if greater operation */
function JgtOp() {
	JgtOp.base.constructor.call(this);
};
extend(JmpOp, JgtOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp > 0;
	}
});

/** Jump if lower or equal operation */
function JleOp() {
	 JleOp.base.constructor.call(this);
};
extend(JmpOp, JleOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp <= 0;
	}
});

/** Jump if lower operation */
function JltOp() {
	JltOp.base.constructor.call(this);
};
extend(JmpOp, JltOp, {
	isSuccessful: function(context) {
		return context.cpuModel.cmp < 0;
	}
});
//Shift left (<<) is 32bit operation. This is a workaround.
function lshift(num, bits) {
    return num * Math.pow(2,bits);
}
function normalize(value) {
	var result = value;
	while (result > 0xFFFFFFFF) {
		result %= 0xFFFFFFFF;
	};
	return result < 0 ? Math.ceil(result) : Math.floor(result);
};
