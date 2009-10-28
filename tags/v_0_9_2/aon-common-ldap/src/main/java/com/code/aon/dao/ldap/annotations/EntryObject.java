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

	String mainObjectClass();
	
    String[] objectClasses() default {};
    
    String baseDN() default "";
	
}
