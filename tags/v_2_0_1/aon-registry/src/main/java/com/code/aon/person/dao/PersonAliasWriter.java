package com.code.aon.person.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.person.Person;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class PersonAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-registry/src/com/code/aon/person/dao/IPersonAlias.java");
		String[] classes = new String[1]; 
		classes[0] = Person.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.person.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}