package com.code.aon.employee.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractTracking;
import com.code.aon.employee.ContractType;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class EmployeeAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-employee/src/main/java/com/code/aon/employee/dao/IEmployeeAlias.java");
		String[] classes = new String[] { 
				Contract.class.getName(),
				ContractTracking.class.getName(),
				ContractType.class.getName() };
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.code.aon.employee.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}