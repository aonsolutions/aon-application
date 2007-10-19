package com.code.aon.tasCommercial.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.tasCommercial.TasOffer;

public class TasCommercialAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("c:/ITasCommercialAlias.java");
		String[] classes = new String[1];
		classes[0] = TasOffer.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.tasCommercial.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}