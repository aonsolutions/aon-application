package com.code.aon.department.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.department.Department;

public class DepartmentAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-employee/src/com/code/aon/department/dao/IDepartmentAlias.java");
		String[] classes = new String[1];
		classes[0] = Department.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.department.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}