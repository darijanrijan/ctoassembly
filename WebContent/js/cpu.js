/**
 * Hypothetical CPU with registers, stack and memory.
 * @param {object} scope AngularJS scope.
 * @class 
 */ 
function CPU() {

	var flags = [0, 0, 0, 0, 0];
	this.instructionPointer = 0;
	this.stack = new Stack();
	this.registers = new Registers();
	this.globals = new Globals();
	this.memory = new Memory();
	
	this.init = function() {
		flags = [0, 0, 0, 0, 0];
		this.stack = new Stack();
		this.registers = new Registers();
		this.globals = new Globals();
		this.memory = new Memory();
	};
	
	/**
	 * Sets the flag bits.
	 * @param {number} number
	 * @param {number} carry
	 * @public
	 */
	this.setFlags = function(number, carry, overflow) {
		//zero
		flags[0] = (number == 0) | 0;
		//parity
		var mask = Const.INT_MAX_UNSIGNED;
		var count = 0;
		while (mask != 0) {
			if (number & mask) {
				count++;
			}
			mask >>>= 1;
		}
		flags[1] = ((count % 2) == 0) | 0;
		//sign
		flags[2] = ((number & Const.INT_MAX_SIGNED) == Const.INT_MAX_SIGNED) | 0;
		//carry
		flags[3] = carry;
		//overflow
		flags[4] = overflow;
	};
	
	this.getFlags = function() {
		return flags;
	};
};


/**
 * Part of the CPU where global variables are stored.
 * @class
 */
function Globals() {

	/** @private */ var store = {};
	/** @private */ var count = 0;
	
	/**
	 * Returns an array of all global variables names.
	 * @public
	 */
	this.getValues = function() {
		var result = new Array();
		for (element in store) {
			if (store.hasOwnProperty(element)) {
				result.push(store[element]);
			};
		};
		return result;
	};
	
	/**
	 * Returns the value of a global variable with the name.
	 * @param {string} name Name of the global variable.
	 * @public
	 */
	this.getValue = function(name) {
		return store[name].value;
	};
	
	/**
	 * Sets the value of a global variable with the name.
	 * @param {string} name Name of the global variable.
	 * @param {number} value Value of the global variable.
	 * @public
	 */
	this.setValue = function(name, value) {
		if (store[name] == null) {
			store[name] = {
				address: (Const.ADDRESS_SPACE_SIZE + Const.GLOBALS_OFFSET_FROM_STACK + count++) * Const.ADDRESS_OFFSET,
				name: name,
				value: value
			};
		} else {
			store[name].value = value;
		};
	};
	
};

/**
 * CPU registers. There are 14 CPU registers, first 12 for general purpose,
 * 13th for remainder of the DIV operation and 14th as a function return register.
 * @constructor
 */
function Registers() {

	/** @private */ var array = new Array();
	
	/** Initialize with random values taking value from 0 to 2^32 */
	for (var i = 0; i < Const.REGISTERS_SIZE; i++) {
		array[i] = Math.floor(Math.random() * Const.INT_MAX_UNSIGNED) >> 0;
	};
	
	/**
	 * Returns the list of values of registers.
	 * @public
	 */
	this.getValues = function() {
		return array;
	};
	
	/**
	 * Returns a value from a register.
	 * @param {number} index Index of the register.
	 * @public
	 */
	this.getValue = function(index) {
		return array[index];
	};

	/**
	 * Sets a value to a register.
	 * @param {number} index Index of the register.
	 * @param {number} value Value to be set.
	 * @public
	 */
	this.setValue= function(index, value) {
		array[index] = value;
	};
};

/**
 * RAM memory. 
 * @constructor
 */
function Memory() {

	/** @private */ var array = new Array();
	/** @private */ var free = 0;
	
	/** Initialize with random values from 0 to 2^32 */
	for (var i = 0; i < Const.MEMORY_SIZE + 1; i++) {
		array[i] = {
			address: (Const.MEMORY_START_OFFSET + i) * Const.ADDRESS_OFFSET,
			value: Math.floor(Math.random() * Const.INT_MAX_UNSIGNED) >> 0
		};
	};
	
	/**
	 * Returns the list of values in the memory.
	 * @public
	 */
	this.getValues = function() {
		return array;
	};
	
	/**
	 * Returns internal index of the memory location.
	 * @param {number} address Memory location.
	 * @private
	 */
	var getRealIndex = function(address) {
		return (address / Const.ADDRESS_OFFSET) - Const.MEMORY_START_OFFSET;
	};
	
	/**
	 * Returns a value from a memory location.
	 * @param {number} address Address of the memory location.
	 * @public
	 */
	this.getValue = function(address) {
		var realIndex = getRealIndex(address);
		if (realIndex < 0 || realIndex > Const.MEMORY_SIZE) {
			throw "Segmentation fault";
		};
		return array[realIndex].value;
	};
	
	/**
	 * Sets a value to a memory location.
	 * @param {number} address Address of the memory location.
	 * @param {number} value Value to be set.
	 * @public
	 */
	this.setValue = function(address, value) {
		var realIndex = getRealIndex(address);
		if (realIndex < 0 || realIndex > Const.MEMORY_SIZE) {
			throw "Segmentation fault";
		};
		array[realIndex].value = value;
	};
	
	/**
	 * Returns a next free memory location. This method is usually invoked
	 * by 'malloc' function.
	 * @public
	 */
	this.getNextFreeLocation = function() {
		return array[free++].address;
	};
};

/**
 * Stack part of the memory. Function calls, return addresses and function arguments
 * are stored on the stack.
 * @constructor
 */
function Stack() {

	/** @private */ var array = new Array();
	/** @public */ this.bp = (Const.ADDRESS_SPACE_SIZE + 1) * Const.ADDRESS_OFFSET;
	/** @public */ this.sp = (Const.ADDRESS_SPACE_SIZE + 1) * Const.ADDRESS_OFFSET;

	/** Initialize with random values taking value from 0 to 2^32 */
	for (var i = 0; i < Const.STACK_SIZE; i++) {
		array[i] = {
			address: (Const.ADDRESS_SPACE_SIZE - i) * Const.ADDRESS_OFFSET,
			value: Math.floor(Math.random() * Const.INT_MAX_UNSIGNED) >> 0
		};
	};
	
	/**
	 * Returns the list of values in the stack.
	 * @public
	 */
	this.getValues = function() {
		return array;
	};
	
	/**
	 * Returns internal index of the stack memory location.
	 * @param {number} address Stack location.
	 * @private
	 */
	var getRealIndex = function(address) {
		return -address / Const.ADDRESS_OFFSET + Const.ADDRESS_SPACE_SIZE;
	};
	
	this.getBP = function() {
		return this.bp;
	};

	/**
	 * Sets a value to the base pointer register.
	 * @param {number} value Value to be set.
	 * @public
	 */
	this.setBP = function(value) {
		this.bp = value;
	};
	
	this.getSP = function() {
		return this.sp;
	};
	
	/**
	 * Sets a value to the stack pointer register.
	 * @param {number} value Value to be set.
	 * @public
	 */
	this.setSP = function(value) {
		this.sp = value;
		
		if (this.sp < (Const.ADDRESS_SPACE_SIZE - Const.STACK_SIZE + 1) * Const.ADDRESS_OFFSET) {
			throw "Stack overflow";
		};
	};
	
	/**
	 * Returns a value from a stack location.
	 * @param {number} address Address of the stack location.
	 * @public
	 */
	this.getValue = function(address) {
		var realIndex = getRealIndex(address);
		if (realIndex < 0 || realIndex > Const.STACK_SZIE) {
			throw "Segmentation fault";
		};
		return array[realIndex].value;
	};

	/**
	 * Sets a value to a stack location.
	 * @param {number} address Address of the stack location.
	 * @param {number} value Value to be set.
	 * @public
	 */
	this.setValue = function(address, value) {
		var realIndex = getRealIndex(address);
		if (realIndex < 0 || realIndex > Const.STACK_SZIE) {
			throw "Segmentation fault";
		};
		array[realIndex].value = value;
	};

	/**
	 * Pushes an item to the top of the stack.
	 * @param {number} item item to be set.
	 * @public
	 */
	this.push = function(item) {
		var address = this.sp - Const.ADDRESS_OFFSET;
		this.setSP(address);
		this.setValue(address, item);
		
		if (this.sp < (Const.ADDRESS_SPACE_SIZE - Const.STACK_SIZE + 1) * Const.ADDRESS_OFFSET) {
			throw "Stack overflow";
		};
	};
	
	/**
	 * Pops the top of the stack and returns the value.
	 * @public
	 */
	this.pop = function() {
		var result = this.getValue(this.sp);
		this.setSP(this.sp + Const.ADDRESS_OFFSET);
		return result;
	};
};