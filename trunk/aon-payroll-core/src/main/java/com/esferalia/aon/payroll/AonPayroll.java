package com.esferalia.aon.payroll;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.Classpath;

public class AonPayroll {

	private static final String PAYROLL_CONFIG_FILE = "aon-payroll.properties";

	private static final String PAYROLL_PARTEIT_DAO = "aon.payroll.parteIT.dao.class";
	private static final String PAYROLL_PARTEIT_CALCULATORS = "aon.payroll.parteIT.calculators.classes";
	private static final String PAYROLL_NOMINA_DAO = "aon.payroll.nomina.dao.class";
	private static final String PAYROLL_EMPLEADO_DAO = "aon.payroll.empleado.dao.class";

	public static void configure() throws PayrollException {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/", PAYROLL_CONFIG_FILE);
			if (!ArrayUtils.isEmpty(urls)) {
				Properties props = new Properties();
				for (URL url : urls) {
					props.load(url.openStream());
				}
				String classes = props.getProperty(PAYROLL_PARTEIT_CALCULATORS);
				if (classes != null) {
					String[] array = StringUtils.split(classes, ",");
					for (String c : array) {
						Class.forName(c);
					}
				}
				String clazz = props.getProperty(PAYROLL_NOMINA_DAO);
				if (clazz != null ) {
					Class.forName(clazz);	
				}
				clazz = props.getProperty(PAYROLL_EMPLEADO_DAO);
				if (clazz != null ) {
					Class.forName(clazz);
				}
				clazz = props.getProperty(PAYROLL_PARTEIT_DAO);
				if (clazz != null ) {
					Class.forName(clazz);
				}
			}
		} catch (IOException e) {
			throw new PayrollException(e);
		} catch (ClassNotFoundException e) {
			throw new PayrollException(e);
		}
	}
}
