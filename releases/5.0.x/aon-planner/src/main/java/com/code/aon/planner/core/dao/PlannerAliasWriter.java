package com.code.aon.planner.core.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.planner.core.Calendar;
import com.code.aon.planner.core.IncidenceType;

public class PlannerAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-planner/src/com/code/aon/planner/core/dao/IPlannerAlias.java");
		String[] classes = new String[2];
		classes[0] = Calendar.class.getName();
		classes[1] = IncidenceType.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.planner.core.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}