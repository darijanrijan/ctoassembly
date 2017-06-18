package com.ctoassembly.compiler;

/**
 * Represents a variable type. 
 * 
 * Type can be: array, pointer or simple type.
 * 
 * Every type is further defined with an inner type. (Currently the only possible inner type is "int")
 * 
 * @author Darijan Jankovic
 * @version 0.1
 */
public class Type {

	private static Type NO_TYPE_INSTANCE= new NoType();

	public static Type getNoType() {
		return NO_TYPE_INSTANCE;
	}
	
	public Type() {
	}
	
	public Type(int innerType) {
		fInnerType= innerType;
	}
	
	private int fInnerType= Definitions.NO_TYPE;
	
	public int getInnerType() {
		return fInnerType;
	}
	
	public void setInnerType(int innerType) {
		fInnerType= innerType;
	}
	
	public static class NoType extends Type {
	}
	
	/**
	 * Pointer type, e.g. int* 
	 * 
	 * @author Darijan Jankovic
	 * @version 0.1
	 */
	public static class PointerType extends Type {

		private int fLevel;

		public PointerType(final int level) {
			fLevel= level;
		}
		
		public int getLevel() {
			return fLevel;
		}
		
		public void setLevel(int level) {
			fLevel= level;
		}
		
	}
	
	/**
	 * Array type, e.g. int[]
	 * 
	 * @author Darijan Jankovic
	 * @version 0.1
	 */
	public static class ArrayType extends Type {
	}
	
	/**
	 * Simple type, e.g.: int
	 * 
	 * @author Darijan Jankovic
	 * @version 0.1
	 */
	public static class SimpleType extends Type {
		
		public SimpleType() {
		}
		
		public SimpleType(final int innerType) {
			super(innerType);
		}
		
	}
	
}
