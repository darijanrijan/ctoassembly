package com.ctoassembly.compiler;
import java.io.IOException;
import java_cup.runtime.Symbol;


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	public final int YYEOF = -1;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"40:9,43,42,40:2,41,40:18,43,20,39,40:2,19,16,40,3,4,12,10,1,11,40,13,38:10," +
"40,2,15,9,14,40:2,37:26,7,40,8,17,37,40,37:3,29,28,30,27,33,22,37:2,31,37,2" +
"3,36,37:2,34,26,24,25,37,32,37:2,35,5,18,6,21,40,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,95,
"0,1:9,2,3,4,5,6,7,8,9,10,11,12,13,1,14,15,16,1:2,17,1:7,18,1,19,1,20,1:7,21" +
",16,1:2,21,22,21:4,1,22,21,1,16,23,24,25,26,27,28,29,30,31,32,33,34,35,36,3" +
"7,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54")[0];

	private int yy_nxt[][] = unpackFromString(55,44,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,60:2,94,92,60,8" +
"0,60:3,85,60,89,60:3,24,25,61,26,27,28,-1:53,29,-1:43,30,31,-1:42,32,-1,33," +
"-1:41,34,-1:43,35,-1:2,59,36,-1:39,37,-1:4,38,-1:38,39,-1:5,40,-1:37,41,-1:" +
"6,42,-1:36,43,-1:43,44,-1:8,45,-1:34,46,-1:43,47,-1:56,60,63,60:6,48,60:8,-" +
"1:43,24,-1:6,62:38,49,62,-1:2,62,-1:43,28,-1,36:40,-1:2,36,-1:9,50,-1:43,51" +
",-1:56,60:17,-1:6,59:11,64,59:28,66,59:2,-1:22,60:2,52,60:14,-1:6,59:11,64," +
"53,59:27,66,59:2,-1:22,60:6,54,60:10,-1:47,59,-1:23,60:6,55,60:10,-1:27,70," +
"-1:43,60,56,60:15,-1:28,72,-1:23,68,-1:18,60:17,-1:29,74,-1:41,60:7,57,60:9" +
",-1:9,58,-1:61,60:4,65,60:12,-1:27,60:9,67,60:7,-1:27,60:12,69,60:4,-1:27,6" +
"0:8,71,60:8,-1:27,60:6,73,60:10,-1:27,60:9,75,60:7,-1:27,76,60:16,-1:27,60:" +
"3,77,60:13,-1:27,60:14,78,60:2,-1:27,60,79,60:15,-1:27,60:11,81,60:5,-1:27," +
"60:2,82,60:14,-1:27,60:6,83,60:10,-1:27,60:5,84,60:11,-1:27,60:6,86,60:10,-" +
"1:27,60:13,87,60:3,-1:27,88,60:16,-1:27,90,60:16,-1:27,60:4,91,60:12,-1:27," +
"60,93,60:15,-1:5");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ appendCode(); return new Symbol(sym.COMMA); }
					case -3:
						break;
					case 3:
						{ appendCode(); return new Symbol(sym.SEMICOLON); }
					case -4:
						break;
					case 4:
						{ appendCode(); return new Symbol(sym.LPAREN); }
					case -5:
						break;
					case 5:
						{ appendCode(); return new Symbol(sym.RPAREN); }
					case -6:
						break;
					case 6:
						{ appendCode(); return new Symbol(sym.LBRACKET); }
					case -7:
						break;
					case 7:
						{ appendCode(); return new Symbol(sym.RBRACKET); }
					case -8:
						break;
					case 8:
						{ appendCode(); return new Symbol(sym.LSQBRACKET); }
					case -9:
						break;
					case 9:
						{ appendCode(); return new Symbol(sym.RSQBRACKET); }
					case -10:
						break;
					case 10:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.ASSIGN_OP)); }
					case -11:
						break;
					case 11:
						{ appendCode(); return new Symbol(sym.PLUS, new Integer(Definitions.ADD_OP)); }
					case -12:
						break;
					case 12:
						{ appendCode(); return new Symbol(sym.MINUS, new Integer(Definitions.SUB_OP)); }
					case -13:
						break;
					case 13:
						{ appendCode(); return new Symbol(sym.MUL, new Integer(Definitions.MUL_OP)); }
					case -14:
						break;
					case 14:
						{ appendCode(); return new Symbol(sym.DIV, new Integer(Definitions.DIV_OP)); }
					case -15:
						break;
					case 15:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.GT)); }
					case -16:
						break;
					case 16:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.LT)); }
					case -17:
						break;
					case 17:
						{ appendCode(); return new Symbol(sym.AND_OP, new Integer(Definitions.AND_OP)); }
					case -18:
						break;
					case 18:
						{ appendCode(); return new Symbol(sym.XOR_OP, new Integer(Definitions.XOR_OP)); }
					case -19:
						break;
					case 19:
						{ appendCode(); return new Symbol(sym.OR_OP, new Integer(Definitions.OR_OP)); }
					case -20:
						break;
					case 20:
						{ appendCode(); return new Symbol(sym.MOD, new Integer(Definitions.MOD_OP)); }
					case -21:
						break;
					case 21:
						{ appendCode(); return new Symbol(sym.NOT_LOG_OP); }
					case -22:
						break;
					case 22:
						{ appendCode(); return new Symbol(sym.NOT_OP, new Integer(Definitions.NOT_OP)); }
					case -23:
						break;
					case 23:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -24:
						break;
					case 24:
						{
								appendCode(); 
								return new Symbol(sym.INT_NUMBER, yytext());
							}
					case -25:
						break;
					case 25:
						{
								appendCode(); 
								throw new IOException("Illegal character " + yytext());
							}
					case -26:
						break;
					case 26:
						{
								//Ignore carriage return
							}
					case -27:
						break;
					case 27:
						{ 	
								newLine();
							}
					case -28:
						break;
					case 28:
						{	
								appendCode(); 
							}
					case -29:
						break;
					case 29:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.EQ)); }
					case -30:
						break;
					case 30:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.ADD_OP)); }
					case -31:
						break;
					case 31:
						{ appendCode(); return new Symbol(sym.INCDEC, new Integer(Definitions.INC_OP)); }
					case -32:
						break;
					case 32:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SUB_OP)); }
					case -33:
						break;
					case 33:
						{ appendCode(); return new Symbol(sym.INCDEC, new Integer(Definitions.DEC_OP)); }
					case -34:
						break;
					case 34:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.MUL_OP)); }
					case -35:
						break;
					case 35:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.DIV_OP)); }
					case -36:
						break;
					case 36:
						{
								appendCode(); 
							}
					case -37:
						break;
					case 37:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.GE)); }
					case -38:
						break;
					case 38:
						{ appendCode(); return new Symbol(sym.SHIFT, new Integer(Definitions.SHIFT_RIGHT_OP)); }
					case -39:
						break;
					case 39:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.LE)); }
					case -40:
						break;
					case 40:
						{ appendCode(); return new Symbol(sym.SHIFT, new Integer(Definitions.SHIFT_LEFT_OP)); }
					case -41:
						break;
					case 41:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.AND_OP)); }
					case -42:
						break;
					case 42:
						{ appendCode(); return new Symbol(sym.LOG_AND, new Integer(Definitions.LOG_AND)); }
					case -43:
						break;
					case 43:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.XOR_OP)); }
					case -44:
						break;
					case 44:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.OR_OP)); }
					case -45:
						break;
					case 45:
						{ appendCode(); return new Symbol(sym.LOG_OR, new Integer(Definitions.LOG_OR)); }
					case -46:
						break;
					case 46:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.MOD_OP)); }
					case -47:
						break;
					case 47:
						{ appendCode(); return new Symbol(sym.RELOP, new Integer(Definitions.NE)); }
					case -48:
						break;
					case 48:
						{ appendCode(); return new Symbol(sym.IF); }
					case -49:
						break;
					case 49:
						{
								appendCode();
								return new Symbol(sym.STRING, yytext());			
							}
					case -50:
						break;
					case 50:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SHIFT_RIGHT_OP)); }
					case -51:
						break;
					case 51:
						{ appendCode(); return new Symbol(sym.ASSIGN, new Integer(Definitions.SHIFT_LEFT_OP)); }
					case -52:
						break;
					case 52:
						{ appendCode(); return new Symbol(sym.TYPE, new Integer(Definitions.INT_TYPE)); }
					case -53:
						break;
					case 53:
						{
								final String comment= yytext();
								int newLinesCount= comment.split("\n", -1).length - 1;
								while(newLinesCount-- > 0) {
									newLine();
								}
								appendCode(); 
							}
					case -54:
						break;
					case 54:
						{ appendCode(); return new Symbol(sym.ELSE); }
					case -55:
						break;
					case 55:
						{ appendCode(); return new Symbol(sym.WHILE); }
					case -56:
						break;
					case 56:
						{ appendCode(); return new Symbol(sym.RETURN); }
					case -57:
						break;
					case 57:
						{ appendCode(); return new Symbol(sym.TYPE, new Integer(Definitions.UNSIGNED_TYPE)); }
					case -58:
						break;
					case 58:
						{
								appendCode(); 
								return new Symbol(sym.INT_NUMBER, "4");
							}
					case -59:
						break;
					case 60:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -60:
						break;
					case 61:
						{
								appendCode(); 
								throw new IOException("Illegal character " + yytext());
							}
					case -61:
						break;
					case 63:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -62:
						break;
					case 65:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -63:
						break;
					case 67:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -64:
						break;
					case 69:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -65:
						break;
					case 71:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -66:
						break;
					case 73:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -67:
						break;
					case 75:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -68:
						break;
					case 76:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -69:
						break;
					case 77:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -70:
						break;
					case 78:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -71:
						break;
					case 79:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -72:
						break;
					case 80:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -73:
						break;
					case 81:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -74:
						break;
					case 82:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -75:
						break;
					case 83:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -76:
						break;
					case 84:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -77:
						break;
					case 85:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -78:
						break;
					case 86:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -79:
						break;
					case 87:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -80:
						break;
					case 88:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -81:
						break;
					case 89:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -82:
						break;
					case 90:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -83:
						break;
					case 91:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -84:
						break;
					case 92:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -85:
						break;
					case 93:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -86:
						break;
					case 94:
						{
								appendCode(); 
								return new Symbol(sym.ID, yytext());
							}
					case -87:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
