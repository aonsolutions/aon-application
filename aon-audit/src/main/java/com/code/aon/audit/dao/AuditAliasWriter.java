package com.code.aon.audit.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.audit.Domain;
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
//		File file = new File("c:/ICommercialAlias.java");
		String[] classes = new String[] { 
				Domain.class.getName() };
		AliasWriter writer = new AliasWriter("com.code.aon.audit.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}