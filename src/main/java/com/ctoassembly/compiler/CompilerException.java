package com.ctoassembly.compiler;

/**
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class CompilerException extends Exception {

	public enum Severity {
		WARNING, ERROR;
	}
	
	private static final long serialVersionUID = -24088446836954537L;
	private final Severity fSeverity;
	private final String fMessage;
	
	public CompilerException(final String message) {
		this(Severity.ERROR, message, null);
	}

	public CompilerException(final Exception e) {
		this(Severity.ERROR, e.getMessage(), e);
	}
	
	public CompilerException(final Severity severity, final String message, final Exception e) {
		super(message, e);
		
		fMessage= message;
		fSeverity= severity;
	}
	
	@Override
	public String getMessage() {
		return fMessage;
	}
	
	public Severity getSeverity() {
		return fSeverity;
	}

}
