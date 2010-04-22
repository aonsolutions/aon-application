package com.code.aon.tasDelivery.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.tasDelivery.TasDelivery;

public class TasDeliveryAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("./src/com/code/aon/tasDelivery/dao/ITasDeliveryAlias.java");
		String[] classes = new String[1];
		classes[0] = TasDelivery.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.tasDelivery.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}