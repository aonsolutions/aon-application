package com.esferalia.aon.payroll;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class AonPayroll {
	
	public static void configure() throws PayrollException{
		try {
			Properties props = new Properties();
			props.load( AonPayroll.class.getResourceAsStream("/aon-payroll.properties"));
			String classes = props.getProperty("aon.payroll.parteIT.class");
			String[] array =  StringUtils.split(classes,",");
			for (String c: array) {
				Class.forName(c);	
			}
			String clazz  = props.getProperty("aon.payroll.nominadao.class");
			Class.forName(clazz);
		} catch (IOException e) {
			throw new PayrollException(e);
		} catch (ClassNotFoundException e) {
			throw new PayrollException(e);
		}
	}
}
