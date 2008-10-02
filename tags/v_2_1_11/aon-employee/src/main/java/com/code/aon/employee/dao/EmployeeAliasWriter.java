package com.code.aon.employee.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.employee.Expenditures;
import com.code.aon.employee.ExpendituresItems;

public class EmployeeAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-employee/src/com/code/aon/employee/dao/IEmployeeAlias.java");
		String[] classes = new String[2];
		classes[0] = ExpendituresItems.class.getName(); 
		classes[1] = Expenditures.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.employee.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}