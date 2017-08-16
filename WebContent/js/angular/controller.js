/** @public */ var startAnimating = false;

app.controller("Controller", function($scope) {
	
	/** @scope */ $scope.constants = Const;

	/** C code entered by user in the editor. */
	/** @scope */ $scope.ccode = "//You must always define the 'main()' function, as it is an execution entry point\nint main() {\n\n}";
	
	/** C code entered by user has errors */
	/** @scope */ $scope.isCompileErrors = false;
	
	/** Assembly to C code (line numbers) mapping. */
	/** private */ var cToAsmCodeLineMapping = {};

	/** @scope */ $scope.cpu = new CPU();
				  $scope.cpu.asmcode = {};
				  $scope.cpu.previousInstructionPointer = 0;
	/** ASM code returned by the compiler. */

	$scope.$watch('ccode', function(newCode, oldCode) {
		if (newCode.length > Const.MAX_INPUT_LENGTH) {
			$scope.ccode = oldCode;
		};
	});
	
	/**
	 * Invokes the compiler on the server and returns the
	 * assembly code and assembly to c code line mapping 
	 * or, in case of an error, a line number and a message 
	 * describing the error.
	 * 
	 * @param {string} ccode C code
	 * @param {boolean} emoveFormatting whether to remove formatting (tabs).
	 * @private
	 */
	function invokeCompiler(ccode) {
		var result = $.ajax({
			type: "post",
			url: "/compile",
			async: false,
			data: {
				ccode: ccode
			},
			dataType: "json"
		});
		return result.responseJSON;
	};


	$scope.checkCode = function() {
		var response = invokeCompiler($scope.ccode);
		if (!!response.error) {
			$scope.isCompileErrors = true;
			return response;
		};
		$scope.isCompileErrors = false;
		return null;
	};

	$scope.compile = function() {
		$("#compile-button").button("loading");
		
		var response = invokeCompiler($scope.ccode);
		if (!!response.error) {
			$("#compile-button").button("reset");
			return;
		}
		
		$scope.cpu.asmcode = enumerateCode(response);
		cToAsmCodeLineMapping = extractMapping(response);
		
		startAnimating = false;
		Ui.writeCompareCCodeTable($scope.ccode.split("\n"));		Ui.writeCompareAsmCodeTable($scope.cpu.asmcode);
		Ui.showTile("#compare-page");
		
		//Look at the docs.css, heights settings for html and body. Dirty hack to make
		//the gray background 100% of viewport before and after compiling (displaying second tile).
		$('body').css('height', 'initial');
		
		$('#interpreter').hide();
		$("#compile-button").button("reset");
	};

	$scope.interprete = function() {
		startAnimating = false;
		$(".interprete-button").button("loading");
		initializeInterpreting();
		Ui.showTile("#interpreter");
		setTimeout(function () {
            $(".interprete-button").button("reset");
		}, 400);
		$("#execute").attr("disabled", false);
		$(".error-message").hide();
		$(".success-message").hide();
	};

	/**
	 * @scope
	 */
	$scope.getCCode = function() {
		return $scope.ccode.split("\n");
	};

	/**
	 * @scope
	 */
	$scope.getASMCode = function() {
		return $scope.cpu.asmcode;
	};

	/**
	 * @scope
	 */
	$scope.getCtoAsmCodeLineMapping = function() {
		return cToAsmCodeLineMapping;
	};

	/**
	 * @private
	 */
	function enumerateCode(program) {
		var result = new Code();
		var addressCnt = 0;
		for (var i = 0; i < program.instructions.length; i++) {
			var line = program.instructions[i].generatedCode.trim();
			if (line.length > 0) {
				var operation = Util.getOperation(line);
				if (!Util.isLabel(operation)) {
					result.push({
						instructionPointer: addressCnt * Const.ADDRESS_OFFSET,
						line: line,
						description: program.instructions[i].description
					});
					addressCnt++;
				} else {
					result.push({
						labelInstructionPointer: addressCnt * Const.ADDRESS_OFFSET,
						line: line,
						description: program.instructions[i].description
					});
				};
			};
		};
		return result;
	};

	/**
	 * @private
	 */
	function extractMapping(program) {
		var result = {};
		for (var i = 0; i < program.instructions.length; i++) {
			var clineNo = program.instructions[i].clineNo;
			if (clineNo != null) {
				clineNo= parseInt(clineNo);
				if (result[clineNo] == null) {
					result[clineNo] = [];
				}
				//plus one because of legacy indexing on the assembly rows
				result[clineNo].push(i + 1);
			}
		};
		return result;
	};

	/**
	 * Global init function. It initializes the CPU model, sets the instruction pointer
	 * to 'main' function and maps the global variables to the "global" part of the CPU model.
	 * 
	 * @private
	 */
	function initializeInterpreting() {
		$scope.cpu.init();
		$scope.cpu.instructionPointer = $scope.cpu.previousInstructionPointer = $scope.cpu.asmcode.findLabelInstructionPointer("main");
		var asmcodeArray = $scope.cpu.asmcode.codeToArray();
		for (var i = 0; i < asmcodeArray.length; i++) {
			if (asmcodeArray[i].startsWith(Const.WORD) || asmcodeArray[i].startsWith(Const.STRING)) {
				var globalVarName = asmcodeArray[i - 1].remove(":").trim();
				var initValue = null;
				if (asmcodeArray[i].startsWith(Const.WORD)) {
					initValue = asmcodeArray[i].trim().slice(Const.WORD.length).trim();
					$scope.cpu.globals.setValue(globalVarName, parseInt(initValue));
				} else if (asmcodeArray[i].startsWith(Const.STRING)) {
					initValue = asmcodeArray[i].trim().slice(Const.STRING.length).trim();
					$scope.cpu.globals.setValue(globalVarName, initValue);
				}
			};
		};

		/** UI functions that draw the CPU. */
		Ui.writeCode($scope.cpu.asmcode);
		Ui.showGotoNextLine($scope.cpu.previousInstructionPointer, $scope.cpu.instructionPointer);
	};

	/**
	 * Executes a currently addressed line of assembly code. A
	 * user action is wired directly to this function.
	 * 
	 * @scope
	 */
	$scope.executeLine = function() {
		startAnimating = true;
		try {
			$scope.cpu.previousInstructionPointer = $scope.cpu.instructionPointer;
			var currentLine = $scope.cpu.asmcode.findLine($scope.cpu.instructionPointer).line;
			var operation = Util.getOperation(currentLine);
			var operands = Util.getOperands(currentLine);
			var operObj = Operation(operation);
			if (Util.isJump(operation)) {
				if (operation == Const.RET) {
					operObj.execute({
						cpuModel : $scope.cpu
					});
				} else {
					operObj.execute({
						operand1 : {
							label: operands[0],
							labelAddress: $scope.cpu.asmcode.findLabelInstructionPointer(operands[0]),
							fallbackAddress: $scope.cpu.instructionPointer + Const.ADDRESS_OFFSET
						},
						cpuModel : $scope.cpu
					});
				};
			} else {
				if (operation == Const.PUSH) {
					operObj.execute({
						operand1 : Operand(operands[0], $scope.cpu),
						cpuModel : $scope.cpu
					});
				} else if (operation == Const.POP) {
					operObj.execute({
						destinationOperand : Operand(operands[0], $scope.cpu),
						cpuModel : $scope.cpu
					});
				} else if (operation == Const.MOV || operation == Const.LEA || operation == Const.NOT) {
					operObj.execute({
						operand1 : Operand(operands[0], $scope.cpu), 
						destinationOperand : Operand(operands[1], $scope.cpu),
						cpuModel : $scope.cpu
					});
				} else if (operation == Const.INC || operation == Const.DEC) {
					operObj.execute({
						operand1 : Operand(operands[0], $scope.cpu), 
						destinationOperand : Operand(operands[0], $scope.cpu),
						cpuModel : $scope.cpu
					});
				} else if (operation == Const.CMP) {
						operObj.execute({
						operand1 : Operand(operands[0], $scope.cpu), 
						operand2 : Operand(operands[1], $scope.cpu),
						cpuModel : $scope.cpu
					});
				} else {
					operObj.execute({
						operand1 : Operand(operands[0],	$scope.cpu),
						operand2 : Operand(operands[1], $scope.cpu),
						destinationOperand : Operand(operands[2], $scope.cpu),
						cpuModel : $scope.cpu
					});
				};
				$scope.cpu.instructionPointer = $scope.cpu.instructionPointer + Const.ADDRESS_OFFSET;
			};
			
			if ($scope.cpu.instructionPointer == null) {
				$("#execute").attr("disabled", true);
				$(".success-message").html("Done program execution. The result is: " + $scope.cpu.registers.getValue(13));
				$(".success-message").show();
			} else {
				Ui.showGotoNextLine($scope.cpu.previousInstructionPointer, $scope.cpu.instructionPointer);
			}
		} catch (err) {
			$(".error-message").html(err);
			$(".error-message").show();
		}
	};
	
	/** @scope */ $scope.chosenOutput = "Decimal";
	$scope.$watch('chosenOutput', function() {
		startAnimating = false;
	});
	/**
	 * Shows integer value in one of the three representations:
	 * binary, decimal or hexadecimal.
	 * 
	 * @scope
	 */
	$scope.showOutput = function(value) {
		if ($scope.chosenOutput == "Decimal") {
			return value;
		} else if ($scope.chosenOutput == "Binary") {
			var out = "";
			var length = 32;
			while(length--) {
				out += (value >> length) & 1;    
			};
			return out;  
		} else {
		    if (value < 0) {
		    	value = 0xFFFFFFFF + value + 1;
		    }
		    return value.toString(16);
		};
	};
});
