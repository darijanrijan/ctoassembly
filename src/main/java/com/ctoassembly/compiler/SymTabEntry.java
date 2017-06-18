package com.ctoassembly.compiler;

/**
 * This class represents a single entry in the table of symbols. An entry is represented with 
 * name, kind, type and arbitrary attribute. It also keeps information on the number and type 
 * of the parameters in case of a function. Lastly, registers can keep track to which variable
 * they point to.
 * 
 * @author Darijan Jankovic
 * @version 0.1.0
 */
class SymTabEntry {

	public static final SymTabEntry CONSTANT_0= new SymTabEntry("$0", Definitions.CONSTANT, new Type(Definitions.INT_TYPE));
	
	/**
	 * Name of the symbol.
	 */
	private String fName;
	/**
	 * Kind of the symbol. It can represent a local, global or parameter variable.
	 * Also, it can represent a function, constant or a working register.
	 */
	private int fKind;
	/**
	 * Type of the symbol. It can represent an <code>int</code> or no type.
	 */
	private Type fType;
	/**
	 * This property depends on the context. For local variables means the 
	 * position on the stack and for parameters the ordinal.
	 */
	private int fAttribute;
	/**
	 * If a symbol represents a function, types of the parameters are placed here.
	 */
	private Type[] fParamTypes;
	/**
	 * This is used by register symbols. They sometimes hold a value of certain variable. 
	 * For convenience, the register should now to which variable it points.
	 */
	private SymTabEntry fPointee;
	
	private int fArrayIndex= 0;

	public void setArrayIndex(int arrayIndex) {
		fArrayIndex= arrayIndex;
	}
	
	public int getArrayIndex() {
		return fArrayIndex;
	}
	
	/**
	 * Creates a symbol with only name and no type or kind.
	 * @param name
	 * 			name of the symbol
	 */
	public SymTabEntry(final String name, final Type type) {	
		this(name, Definitions.NO_KIND, type);
	}
	
	/**
	 * Creates a symbol with a name, a type and a kind.
	 * @param name
	 * 			name of the symbol
	 * @param kind
	 * 			kind of the symbol
	 * @param type
	 * 			type of the symbol
	 */
	public SymTabEntry(final String name, final int kind, final Type type) {
		this(name, kind, type, null);
	}
	
	/**
	 * Copy constructor.
	 * @param other
	 * 			symbol to create a copy from
	 */
	public SymTabEntry(final SymTabEntry other) {
		this(other.fName, other.fKind, other.fType, other.fPointee);
		fAttribute= other.fAttribute;
		fParamTypes= other.fParamTypes;
	}

	/**
	 * Creates a symbol with a name, a type, a kind and a pointer level and a pointee.
	  * @param name
	 * 			name of the symbol
	 * @param kind
	 * 			kind of the symbol
	 * @param type
	 * 			type of the symbol
	 * @param pointerLevel
	 * 			pointer level of the symbol
	 * @param pointee
	 * 			{@link SymTabEntry} this symbol points to
	 */
	public SymTabEntry(final String name, final int kind, final Type type,final  SymTabEntry pointee) {
		fName= name;
		fKind= kind;
		fType= type;		
		fPointee= (pointee == null ? null : new SymTabEntry(pointee));
		fParamTypes= new Type[Definitions.PARAM_NUMBER];
	}
	
	/**
	 * Gets the name of the symbol.
	 * @return the name of the symbol
	 */
	public String getName() {
		return fName;
	}

	public void setName(final String name) {
		fName= name;
	}
	
	/**
	 * Gets the kind of the symbol.
	 * @return the kind of the symbol
	 */
	public int getKind() {
		return fKind;
	}
	
	/**
	 * Sets the kind of the symbol.
	 * @param kind
	 * 			kind of the symbol
	 */
	public void setKind(final Integer kind) {
		fKind= kind;
	}
	
	/**
	 * Gets the kind of the symbol.
	 * @return the kind of the symbol
	 */
	public Type getType() {
		return fType;
	}
	
	/**
	 * Sets the kind of the symbol.
	 * @param kind
	 * 			kind of the symbol
	 */
	public void setType(final Type type) {
		fType = type;
	}
	
	/**
	 * Gets the attribute of the symbol.
	 * @return the attribute of the symbol
	 */
	public int getAttribute() {
		return fAttribute;
	}
	
	/**
	 * Sets the attribute of the symbol.
	 * @param kind
	 * 			attribute of the symbol
	 */
	public void setAttribute(final int attribute) {
		fAttribute = attribute;
	}
	
	/**
	 * Gets the {@link SymTabEntry pointee} of the symbol.
	 * @return the {@link SymTabEntry pointee} of the symbol
	 */
	public SymTabEntry getPointee() {
		return fPointee;
	}
	
	/**
	 * Sets the {@link SymTabEntry pointee} of the symbol.
	 * @param kind
	 * 			{@link SymTabEntry pointee} of the symbol
	 */
	public void setPointee(final SymTabEntry pointee) {
		fPointee= (pointee == null ? null : new SymTabEntry(pointee));
	}
	
	/**
	 * Gets the type of the parameter at index.
	 * @param index
	 * 			index of the parameter
	 * @return
	 * 			the type of the parameter at index
	 */
	public Type getParamType(final int index) {
		return fParamTypes[index];
	}
	
	/**
	 * Sets the type of the parameter at index.
	 * @param index
	 * 			index of the parameter
	 * @param type
	 * 			type of the parameter
	 */
	public void setParamType(final int index, Type type) {
		fParamTypes[index]= type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof SymTabEntry)) {
			return false;
		}
		SymTabEntry other= (SymTabEntry)o;
		if (!fName.equals(other.fName)) {
			return false;
		}
		if (fKind != other.fKind) {
			return false;
		}
		return true;
	}
	
}