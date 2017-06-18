package com.ctoassembly.compiler.instructions;


public class InstructionDescription {

	private final String fDescription;
	private String fTitle;
	
	public InstructionDescription(final String description) {
		fDescription= description;
	}

	public InstructionDescription(final String title, final String description) {
		this(description);
		fTitle= title;
	}
	
	public String getTitle() {
		return fTitle;
	}

	public String getDescription() {
		return fDescription;
	}
	
}
