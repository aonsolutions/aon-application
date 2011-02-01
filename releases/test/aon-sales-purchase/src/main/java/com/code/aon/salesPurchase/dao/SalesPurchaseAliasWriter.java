package com.code.aon.salesPurchase.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.salesPurchase.SalesPurchase;

public class SalesPurchaseAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("c:/ISalesPurchaseAlias.java");
		String[] classes = new String[1];
		classes[0] = SalesPurchase.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.salesPurchase.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}