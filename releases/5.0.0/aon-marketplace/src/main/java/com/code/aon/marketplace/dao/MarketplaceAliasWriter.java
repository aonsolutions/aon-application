package com.code.aon.marketplace.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.marketplace.Scale;
import com.code.aon.marketplace.ScaleRelation;

public class MarketplaceAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/AONPROJECTS/aon-marketplace/src/com/code/aon/marketplace/dao/IMarketplaceAlias.java");
		String[] classes = new String[2];
		classes[0] = Scale.class.getName();
		classes[1] = ScaleRelation.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.marketplace.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}