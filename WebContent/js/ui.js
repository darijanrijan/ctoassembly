var Ui = new UI();

function UI() {

	this.showGotoNextLine = function(previousLineNumber, currentLineNumber) {
		toggleUiElementClass("#code" + previousLineNumber, "selected");
		toggleUiElementClass("#code" + currentLineNumber, "selected");
		document.getElementById("code").scrollTop = (currentLineNumber / Const.ADDRESS_OFFSET) * 12;
	};
	
	this.writeCompareCCodeTable = function(ccode) {
		updateUiElement("#ccode-compare", createCompareCCodeTable(ccode));
	};
	
	function createCompareCCodeTable(ccode) {
		var result = "<table>";
		result += "<tbody>";
		for (var i = 0; i < ccode.length; i++) {
			result += "<tr onmouseover='Ui.connect(this, null, true); Ui.showDescriptions(this, null);' onmouseout='Ui.connect(this, null, false); Ui.hideDescriptions();'>";
			result += "<td>";
			var line = ccode[i].split(" ");
			var currentLineLength = 0;
			for (var j = 0; j < ccode[i].search(/\S/); j++) {
				result += "&nbsp;";
				currentLineLength++;
			};
			for (var j = 0; j < line.length; j++) {
				var word = line[j].trim();
				if (Util.isKeyword(word)) {
					result += "<span class=\"keyword\">" + word + "</span>&nbsp;";
				} else if (currentLineLength + word.length <= 30) {
					result += word + "&nbsp;";
				} else {
					result += word.slice(0, 30 - currentLineLength) + ' ...';
					break;
				};
				currentLineLength += word.length + 1;
			};
			result += "</td>";
			result += "</tr>";
		};
		result += "</tbody>";
		result += "</table>";
		return result;
	};
	
	this.writeCompareAsmCodeTable = function(asmcode) {
		updateUiElement("#asmcode-compare", createCompareAsmCodeTable(asmcode));
	};
	
	function createCompareAsmCodeTable(asmcode) {
		var result = "<table>";
		result += "<tbody>";
		for (var i = 0; i < asmcode.length; i++) {
			result += "<tr onmouseover='Ui.connect(null, this, true); Ui.showDescriptions(null, this);' onmouseout='Ui.connect(null, this, false); Ui.hideDescriptions();'>";
			result += "<td>";
			var line = asmcode[i].line;
			var operation = Util.getOperation(line);
			if (!Util.isLabel(operation)) {
				result += "&nbsp;&nbsp;&nbsp;";
			};
			result += "<span class=\"" + Util.getClassForOperation(operation.toUpperCase()) + "\">" + operation + "</span>";
			if (operation.length == 2) {
				result += "&nbsp;&nbsp;";
			} else if (operation.length == 3) {
				result += "&nbsp;";
			};
			result += line.substring(operation.length).replace(new RegExp(",", "g"), ", ");
			result += "</td>";
			result += "</tr>";
		};
		result += "</tbody>";
		result += "</table>";
		return result;
	};
			
	this.writeCode = function(code) {
		updateUiElement("#codetable", createCodeTableHTML(code));
		toggleUiElementClass("#code" + code.findLabelInstructionPointer("main"), "selected");
	};
	
	this.showTile = function(id) {
		$(id).show();
		$('html,body').animate({
			scrollTop : $(id).offset().top
		}, 400);
	};
	
	function updateUiElement(name, value) {
		$(name).html(value);
	};
	
	function toggleUiElementClass(name, className) {
		$(name).toggleClass(className);
	};
	
	function createCodeTableHTML(code) {
		var result = "";//<table id=\"codetable\">";
		var indent = "";
		var address = "<td class=\"linenumber\"></td>";
		var operation;
		var operands;
		var id = "";
		var colspan = "1";
		result += "<thead><tr><th colspan=\"5\">text</th></tr></thead><tbody>";
		
		for (var i = 0; i < code.length; i++) {
			operation = Util.getOperation(code[i].line);
			operands = Util.getOperands(code[i].line);
	
			if (!Util.isLabel(operation)) {
				indent = "&nbsp;&nbsp;&nbsp;";
				id="id=\"code" + code[i].instructionPointer + "\"";
				address = "<td class=\"linenumber\">" + code[i].instructionPointer + "</td>";
			};
			if (operation == Const.WORD || operation == Const.STRING) {
				colspan = 3;
			}
			result += "<tr " + id + ">" + address + "<td class=\"" + Util.getClassForOperation(operation.toUpperCase()) + "\">" + indent + operation + "</td>";
			for (var operandIndex = 0; operandIndex < 3; operandIndex++) {
				if (operands != null && operands[operandIndex] != null) {
					result += "<td colspan=\"" + colspan + "\" class=\"" + Util.getClassForOperation(operands[operandIndex]) + "\">" + operands[operandIndex] + "</td>";
				} else {
					if (operation != Const.WORD && operation != Const.STRING) {
						result += "<td></td>";
					}
				};
			};
			result += "</tr>";

			//Reset temp variables
			id = "";
			indent = "";
			className = "";
			colspan = "1";
			address = "<td class=\"linenumber\"></td>";
		};
		result += "</tbody>";//</table>";
		return result;
	};

	this.showDescriptions = function(rowLeft, rowRight) {
		var scope = angular.element($("body")).scope();
		if (scope == null) {
			return;
		};
		
		// Get the mapping from c to asm code
		var cToAsmCodeLineMapping = scope.getCtoAsmCodeLineMapping();//.invertMap();

		if (rowLeft == null) {
			var cCompareTable = $("#ccode-compare > table").eq(0);
			for (var key in cToAsmCodeLineMapping) {
				if (cToAsmCodeLineMapping[key].contains(rowRight.rowIndex + 1)) {
					rowLeft = cCompareTable.find("tr")[key];
					break;
				};
			};
			if (rowLeft == null) {
				return;
			};
		};

		var cToAsmLines = cToAsmCodeLineMapping[rowLeft.rowIndex];
		if (cToAsmLines == null) {
			return;
		};
		
		var asmCode = scope.getASMCode();
		for (var i = 0; i < cToAsmLines.length; i++) {
			var asmLineIndex = cToAsmLines[i];
			createDescription(asmCode[asmLineIndex - 1].line, asmCode[asmLineIndex - 1].description);
		};
	};
	
	function createDescription(code, description) {
		var title = '';
		if (!!description.title) {
			title = '<h4>' + description.title + '</h4>';
		}
		var content = '<div class="panel panel-default"><!--div class="panel-heading">' + code + '</div--><div class="panel-body">' + title + '<tt>' + code + '</tt><br />' + description.description + '</div></div>';
		$("#descriptions-compare").html($("#descriptions-compare").html() + "<div>" + content + "</div>");
	};

	this.hideDescriptions = function() {
		$("#descriptions-compare").html("");
	};
	
	this.connect = function(rowLeft, rowRight, draw) {
		// Get the canvas element
		var asmCompareTable = $("#asmcode-compare > table").eq(0);
		var cCompareTable = $("#ccode-compare > table").eq(0);
		var canvas = $("#connecter_canvas")[0];

		if (canvas.getContext == null) {
			return;
		};

		var scope = angular.element($("body")).scope();
		if (scope == null) {
			return;
		};
		
		// Resize the canvas to match the height of the asm table
		var asmTableHeight = asmCompareTable.height();
		if (canvas.height != asmTableHeight) {
			canvas.height = asmTableHeight;
		};

		// Get the mapping from c to asm code
		var cToAsmCodeLineMapping = scope.getCtoAsmCodeLineMapping();//.invertMap();

		if (rowLeft == null) {
			for (var key in cToAsmCodeLineMapping) {
				if (cToAsmCodeLineMapping[key].contains(rowRight.rowIndex + 1)) {
					rowLeft = cCompareTable.find("tr")[key];
					break;
				};
			};
			if (rowLeft == null) {
				return;
			};
		};

		var cToAsmLines = cToAsmCodeLineMapping[rowLeft.rowIndex];
		if (cToAsmLines == null) {
			return;
		};

		var asmTableRows = asmCompareTable.find("tr");
		var rowHeight = rowLeft.offsetHeight;
		var rowRightTop = asmTableRows[cToAsmLines[0] - 1];
		var rowRightBottom = asmTableRows[cToAsmLines[cToAsmLines.length - 1] - 1];

		rowLeft.className = 'selectedLeft';
		for ( var i = 0; i < cToAsmLines.length; i++) {
			asmTableRows[cToAsmLines[i] - 1].className = 'selectedRight';
		};
		rowRightTop.className = 'selectedRightTop';
		rowRightBottom.className = 'selectedRightBottom';
		if (rowRightTop == rowRightBottom) {
			rowRightTop.className = 'selectedRightOnlyOne';
		};

		// Get the line coordinates
		var topLeftX = 0;
		var topLeftY = rowLeft.offsetTop;
		var bottomLeftX = 0;
		var bottomLeftY = rowLeft.offsetTop + rowHeight;
		var topRightX = canvas.width;
		var topRightY = rowRightTop.offsetTop;
		var bottomRightX = canvas.width;
		var bottomRightY = rowRightBottom.offsetTop + rowHeight;

		// Drawing
		var ctx = canvas.getContext('2d');
		if (draw) {
			ctx.lineWidth = 0;
			ctx.beginPath();
			ctx.moveTo(topLeftX, topLeftY);
			ctx.lineTo(topRightX, topRightY);
			ctx.lineTo(bottomRightX, bottomRightY);
			ctx.lineTo(bottomLeftX, bottomLeftY);
			ctx.fillStyle = "rgba(255, 220, 0, 1)";
			ctx.fill();
		} else {
			ctx.clearRect(0, 0, canvas.width, canvas.height);
			rowLeft.className = '';
			for ( var i = rowRightTop.rowIndex + 1; i < rowRightBottom.rowIndex; i++) {
				asmTableRows[i].className = '';
			};
			rowRightTop.className = '';
			rowRightBottom.className = '';
		};
	};
};