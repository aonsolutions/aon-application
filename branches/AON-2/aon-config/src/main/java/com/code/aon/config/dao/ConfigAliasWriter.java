package com.code.aon.config.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;

public class ConfigAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-config/src/com/code/aon/config/dao/IConfigAlias.java");
		String[] classes = new String[7]; 
		classes[0] = ApplicationParameter.class.getName();
		classes[1] = User.class.getName();
		classes[2] = Scope.class.getName();
		classes[3] = UserScope.class.getName();
		classes[4] = Series.class.getName();
		classes[5] = WorkGroup.class.getName();
		classes[6] = UserWorkGroup.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.config.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}