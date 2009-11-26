package com.code.aon.common.xml;

/**
 * This class defines a pair of name-value attribute.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-feb-2005
 * @since 1.0
 *  
 */
public class Attribute {

    /**
     * Attribute name.
     */
    private String name;

    /**
     * Attribute value.
     */
    private String value;

    /**
     * Construct a pair of name-value <code>Attribute<code>.
     *  
     * @param name
     * @param value
     */
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Return the name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the value.
     * 
     * @return The value.
     */
    public String getValue() {
        return value;
    }

}