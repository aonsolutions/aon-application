package com.code.aon.tas.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.TasItem;

public class TasAliasWriter {
	public static void main(String[] args) throws IOException {
        File file = new File("/home/ecastellano/AON-6.0.0/aon-tas/src/main/java/com/code/aon/tas/dao/ITASAlias.java");
		String[] classes = new String[] {
			 	 Make.class.getName()
				,Model.class.getName()
				,TasItem.class.getName() 
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.tas.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
		
	}
}