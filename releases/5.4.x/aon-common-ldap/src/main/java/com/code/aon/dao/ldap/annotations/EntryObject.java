package com.code.aon.dao.ldap.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an LDAP entity. This annotation is 
 * applied to the entity class.
 *
 */
@Target(TYPE) 
@Retention(RUNTIME)
public @interface EntryObject {

	/**
	 * Main object class.
	 * 
	 * @return the string
	 */
	String mainObjectClass();
	
    /**
     * Object classes.
     * 
     * @return the string[]
     */
    String[] objectClasses() default {};
    
    /**
     * Base dn.
     * 
     * @return the string
     */
    String baseDN() default "";
	
}
