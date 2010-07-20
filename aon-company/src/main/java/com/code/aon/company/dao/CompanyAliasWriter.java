package com.code.aon.company.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.DAOConstantsWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.CNAE;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.EnterpriseCCC;
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
		String[] classes = new String[] { 
				Company.class.getName(),
				Employee.class.getName(),
				Resource.class.getName(),
				WorkPlace.class.getName(),
				WorkActivity.class.getName(),
				Enterprise.class.getName(),
				EnterpriseCCC.class.getName(),
				CNAE.class.getName(),
				EnterpriseActivity.class.getName() };
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.code.aon.company.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
		DAOConstantsWriter.write( new File( "/tmp/constants.xml") );
	}
}