package com.ctoassembly.compiler;

/**
 * @author Darijan Jankovic
 * @version 0.1.0
 */
public class SemanticsException extends CompilerException {

	private static final long serialVersionUID = 6789530575516199143L;
	
	public SemanticsException(final String message) {
		this(Severity.ERROR, message);
	}
	
	public SemanticsException(final Severity severity, final String message) {
		super(severity, message, null);
	}

}
