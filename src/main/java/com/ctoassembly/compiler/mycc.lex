package com.ctoassembly.compiler;

import java.io.IOException;

import java_cup.runtime.Symbol;

%%
%notunix
%cup
%yyeof
%eofval{
    return new Symbol(sym.EOF);
%eofval} 
   
 
%{
	/** 
	 * Current line number of C-code 
	 */
	private Integer fLineNo = 0;
	
	/** 
	 * Buffer for the line of C-code 
	 */
	private StringBuilder fBuffer;
	
	/**
	 * Returns an ordinal of the line that is currently being processed.
	 * @return
	 *			a line ordinal
	 */
	public Integer getLineNo() { 
		return fLineNo; 
	}
	
	public String getLastToken() {
		final String token= yytext();
		return token == null ? "" : token;
	}
	
	/**
	 * Returns the accumulated C-code in the current line.
	 * @return
	 *			the C-code
	 */
	public String getLineBuffer() {
		return fBuffer == null ? "" : fBuffer.toString();
	}
	
	/**
	 * Appends the current token to the buffer.
	 */
	private void appendCode() {
		if (fBuffer == null) {
			fBuffer = new StringBuilder();
		}
		fBuffer.append(yytext());
	}
	
	/**
	 * Processes the line break symbol. Clears the buffer and increases the line number counter.
	 */
	private void newLine() {
		fLineNo++;
		fBuffer = new StringBuilder();
	}
	
%}

letter=		[_a-zA-Z]
digit= 		[0-9]
startcom=	\/\*
endcom=		\*\/
quote=		\"

%%

","  		{ appendCode(); return new Symbol(sym.COMMA); }
";"  		{ appendCode(); return new Symbol(sym.SEMICOLON); }
"("  		{ appendCode(); return new Symbol(sym.LPAREN); }
")"  		{ appendCode(); return new Symbol(sym.RPAREN); }
"{"  		{ appendCode(); return new Symbol(sym.LBRACKET); }
"}" 		{ appendCode(); return new Symbol(sym.RBRACKET); }
"[" 		{ appendCode(); return new Symbol(sym.LSQBRACKET); }
"]" 		{ appendCode(); return new Symbol(sym.RSQBRACKET); }

"="  		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.ASSIGN_OP)); }
"+="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.ADD_OP)); }
"-="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SUB_OP)); }
"*="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.MUL_OP)); }
"/="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.DIV_OP)); }
">>="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SHIFT_RIGHT_OP)); }
"<<="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SHIFT_LEFT_OP)); }
"&="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.AND_OP)); }
"^="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.XOR_OP)); }
"|="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.OR_OP)); }
"%="		{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.MOD_OP)); }	

"==" 		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.EQ)); }
"!=" 		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.NE)); }
">=" 		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.GE)); }
"<=" 		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.LE)); }
"<"  		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.LT)); }
">"  		{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.GT)); }

"!" 		{ appendCode(); return new Symbol(sym.NOT_LOG_OP); }
"|" 		{ appendCode(); return new Symbol(sym.OR_OP, new Integer(Definitions.OR_OP)); }
"&" 		{ appendCode(); return new Symbol(sym.AND_OP, new Integer(Definitions.AND_OP)); }
"~" 		{ appendCode(); return new Symbol(sym.NOT_OP, new Integer(Definitions.NOT_OP)); }
"^" 		{ appendCode(); return new Symbol(sym.XOR_OP, new Integer(Definitions.XOR_OP)); }
"||" 		{ appendCode(); return new Symbol(sym.LOG_OR, new Integer(Definitions.LOG_OR)); }
"&&" 		{ appendCode(); return new Symbol(sym.LOG_AND, new Integer(Definitions.LOG_AND)); }
"+"  		{ appendCode(); return new Symbol(sym.PLUS, new Integer(Definitions.ADD_OP)); }
"-"  		{ appendCode(); return new Symbol(sym.MINUS, new Integer(Definitions.SUB_OP)); }
"*"  		{ appendCode(); return new Symbol(sym.MUL, new Integer(Definitions.MUL_OP)); }
"/" 		{ appendCode(); return new Symbol(sym.DIV, new Integer(Definitions.DIV_OP)); }
"%"			{ appendCode(); return new Symbol(sym.MOD, new Integer(Definitions.MOD_OP)); }		
"++"		{ appendCode(); return new Symbol(sym.INCDEC, new Integer(Definitions.INC_OP)); }
"--"		{ appendCode(); return new Symbol(sym.INCDEC, new Integer(Definitions.DEC_OP)); }
"<<"		{ appendCode(); return new Symbol(sym.SHIFT, new Integer(Definitions.SHIFT_LEFT_OP)); }
">>"		{ appendCode(); return new Symbol(sym.SHIFT, new Integer(Definitions.SHIFT_RIGHT_OP)); }

"int" 		{ appendCode(); return new Symbol(sym.TYPE, new Integer(Definitions.INT_TYPE)); }
"unsigned"  { appendCode(); return new Symbol(sym.TYPE, new Integer(Definitions.UNSIGNED_TYPE)); }
"if" 		{ appendCode(); return new Symbol(sym.IF); }
"else" 		{ appendCode(); return new Symbol(sym.ELSE); }
"while" 	{ appendCode(); return new Symbol(sym.WHILE); }
"return" 	{ appendCode(); return new Symbol(sym.RETURN); }

"sizeof(int)" 				{
								appendCode(); 
								return new Symbol(sym.INT_NUMBER, "4");
							}

{letter}({letter}|{digit})* {
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
								
{digit}+					{
								appendCode(); 
								return new Symbol(sym.INT_NUMBER, yytext());
							}
							
{quote}.*{quote}			{
								appendCode();
								return new Symbol(sym.STRING, yytext());			
							}
							
{startcom}(.|\r?\n)*{endcom} {
								final String comment= yytext();
								int newLinesCount= comment.split("\n", -1).length - 1;
								while(newLinesCount-- > 0) {
									newLine();
								}
								appendCode(); 
							}

\/\/.*						{
								appendCode(); 
							}							

[ \t]+ 						{	
								appendCode(); 
							}

\n 							{ 	
								newLine();
							}
	
\r							{
								//Ignore carriage return
							}

. 							{
								appendCode(); 
								throw new IOException("Illegal character " + yytext());
							}