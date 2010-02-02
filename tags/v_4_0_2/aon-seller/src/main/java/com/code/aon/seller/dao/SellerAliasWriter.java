package com.code.aon.seller.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.seller.Seller;

public class SellerAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("/AON-PROJECT/aon-seller/src/main/java/com/code/aon/seller/dao/ISellerAlias.java");
		String[] classes = new String[1];
		classes[0] = Seller.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.seller.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}