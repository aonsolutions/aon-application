package com.code.aon.dao.ldap.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an LDAP entity. This annotation is 
 * applied to the entity class.
 *
 */
@Target({METHOD}) 
@Retention(RUNTIME)
public @interface RDN {

}
