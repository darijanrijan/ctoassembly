//http://js-bits.blogspot.ch/2010/08/javascript-inheritance-done-right.html
function surrogateCtor() {};
function extend(base, sub, methods) {
  surrogateCtor.prototype = base.prototype;
  sub.prototype = new surrogateCtor();
  sub.prototype.constructor = sub;
  //Add a reference to the parent's prototype
  sub.base = base.prototype;
 
  //Copy the methods passed in to the prototype
  for (var name in methods) {
    sub.prototype[name] = methods[name];
  };
  //so we can define the constructor in line
  return sub;
};

String.prototype.endsWith= function(suffix) {
	return this.match(suffix + "$") == suffix;
};
String.prototype.startsWith = function(str) {
    return this.indexOf(str) == 0;
};
String.prototype.contains = function(str) {
    return this.indexOf(str) !== -1;
};
String.prototype.remove = function(str) {
	return this.replace(str, "");
};
String.prototype.trim = function() {
	return this.remove(/^\s+|\s+$/g);
};
String.prototype.isEmpty = function() {
	return this === "";
};
Array.prototype.contains = function(object) {
	return this.indexOf(object) !== -1;
};

function Code() {};
Code.prototype = new Array();
Code.prototype.findLine = function(instructionPointer) {
	for (var i = 0; i < this.length; i++) {
		if (this[i].instructionPointer == instructionPointer) {
			return this[i];
		};
	};
};
Code.prototype.findLabelInstructionPointer = function(label) {
	for (var i = 0; i < this.length; i++) {
		if (this[i].line == label + ":") {
			return this[i].labelInstructionPointer;
		};
	};
};
Code.prototype.codeToArray = function() {
	var result = [];
	for (var i = 0; i < this.length; i++) {
		result.push(this[i].line);
	};
	return result;
};