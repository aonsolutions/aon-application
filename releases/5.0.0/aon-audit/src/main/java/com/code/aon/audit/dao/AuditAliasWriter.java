package com.code.aon.audit.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.Application;
import com.code.aon.audit.Session;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author Consulting & Development. Aimar Tellitu - 26-ago-2008
 *
 */
public class AuditAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-audit/src/main/java/com/code/aon/audit/dao/IAuditAlias.java");
		String[] classes = new String[] { 
				Action.class.getName(),
				ActionDenied.class.getName(),
				ActionEntry.class.getName(),
				ActionFavorite.class.getName(),
				Application.class.getName(),
				Session.class.getName() };
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());		
		AliasWriter writer = new AliasWriter("com.code.aon.audit.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}