package com.code.aon.record.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.record.Contract;
import com.code.aon.record.Course;
import com.code.aon.record.Position;
import com.code.aon.record.Work;

public class RecordAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("c:/PROYECTOS/aon-employee/src/com/code/aon/record/dao/IRecordAlias.java");
		String[] classes = new String[4];
		classes[0] = Work.class.getName();
		classes[1] = Course.class.getName();
		classes[2] = Position.class.getName();
		classes[3] = Contract.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.record.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}