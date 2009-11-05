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
public @interface Attribute {

    /**
     * (Optional) The name of the column. Defaults to 
     * the property or field name.
     */
    String name() default "";

    /**
     * (Optional) The access path of the value. Defaults to 
     * the property or field name.
     */
    String accessPath() default "";
    
    /**
     * (Optional) Whether the database column is nullable.
     */
    boolean nullable() default true;

    /**
     * (Optional) The column length. (Applies only if a
     * string-valued column is used.)
     */
    int length() default -1;
    
}
