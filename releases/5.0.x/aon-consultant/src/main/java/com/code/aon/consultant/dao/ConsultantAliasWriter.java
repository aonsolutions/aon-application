package com.code.aon.consultant.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.consultant.RecordData;
import com.code.aon.consultant.RegistryDirStaff;

public class ConsultantAliasWriter {

	public static void main(String[] args) throws IOException {

//		File file = new File("c:/IConsultantAlias.java");
		File file = new File("/PROYECTOS/aon-consultant/src/com/code/aon/consultant/dao/IConsultantAlias.java");
		String[] classes = new String[2]; 
		classes[0] = RecordData.class.getName();
		classes[1] = RegistryDirStaff.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.consultant.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
