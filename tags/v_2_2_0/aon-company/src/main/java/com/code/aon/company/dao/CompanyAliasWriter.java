package com.code.aon.company.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.company.Company;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class CompanyAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-company/src/main/java/com/code/aon/company/dao/ICompanyAlias.java");
		String[] classes = new String[5]; 
		classes[0] = Company.class.getName();
		classes[1] = Employee.class.getName();
		classes[2] = Resource.class.getName();
		classes[3] = WorkPlace.class.getName();
		classes[4] = WorkActivity.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.company.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}