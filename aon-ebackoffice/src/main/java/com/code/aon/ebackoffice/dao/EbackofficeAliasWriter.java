package com.code.aon.ebackoffice.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ebackoffice.EcTarget;

/**
 * @author Esferalia Networks. Ekain Agirrezabal - 31/08/2009
 *
 */
public class EbackofficeAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-ebackoffice/src/main/java/com/code/aon/ebackoffice/dao/IEbackofficeAlias.java");
		String[] classes = new String[] { 
				EcTarget.class.getName() };
		AliasWriter writer = new AliasWriter("com.code.aon.ebackoffice.dao");
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
	
}