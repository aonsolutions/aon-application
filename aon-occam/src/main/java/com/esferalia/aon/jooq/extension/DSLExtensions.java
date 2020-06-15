package com.esferalia.aon.jooq.extension;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class DSLExtensions {
    public static Field<String> hex(Field<byte[]> field) {
        return DSL.field("hex({0})", String.class, field);
    }
    
    public static Field<byte[]> unhex(String str) {
        return DSL.field("unhex({0})", byte[].class, str);
    }
    
    public static Field<String> uuid() {
    	return DSL.field("select uuid()", String.class);
    }
}