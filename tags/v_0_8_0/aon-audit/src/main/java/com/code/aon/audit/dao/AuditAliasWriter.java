package com.code.aon.audit.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionExecution;
import com.code.aon.audit.Application;
import com.code.aon.audit.Domain;
import com.code.aon.audit.DomainApplication;
import com.code.aon.audit.Session;
import com.code.aon.audit.User;
import com.code.aon.common.dao.AliasWriter;

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
				Domain.class.getName(),
				Application.class.getName(),
				User.class.getName(),
				Session.class.getName(),
				Action.class.getName(),
				ActionExecution.class.getName(),
				DomainApplication.class.getName() };
		AliasWriter writer = new AliasWriter("com.code.aon.audit.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}