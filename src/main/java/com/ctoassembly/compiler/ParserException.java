package com.ctoassembly.compiler;

public class ParserException extends CompilerException {

	private static final long serialVersionUID = -7391092271383123252L;
	private final Integer fLineNo;

	public ParserException(final Exception e, final Integer lineNo) {
		super(e);
		fLineNo= lineNo;
	}

	public ParserException(final String message, final Integer lineNo) {
		super(message);
		fLineNo= lineNo;
	}
	
	public Integer getLineNo() {
		return fLineNo;
	}
	
}
