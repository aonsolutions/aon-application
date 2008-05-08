package com.code.aon.webmail.dao;

import java.io.File;
import java.util.LinkedList;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class WebMailAliasWriter {
	public static void main(String[] args) throws Exception {
		
		File file = new File("/AON-PROJECT/aon-webmail/src/main/java/com/code/aon/webmail/dao/IWebMailAlias.java");
		LinkedList<String> classes = new LinkedList<String>();

		classes.add(MailAccount.class.getName());
		classes.add(Signature.class.getName());

		AliasWriter writer = new AliasWriter("com.code.aon.webmail.dao");
		String[] array = new String[ classes.size() ];
		writer.write(classes.toArray( array ), file);
		System.out.println(file.getAbsolutePath());
		System.out.println("Alias generados");
	}
}
