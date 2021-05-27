package com.esferalia.aon.payroll.calculator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

@Retention(RetentionPolicy.RUNTIME)
public @interface Variable {
	ContextVariable value();
}