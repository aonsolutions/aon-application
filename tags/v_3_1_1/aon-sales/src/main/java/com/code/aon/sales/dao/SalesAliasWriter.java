package com.code.aon.sales.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesAttachment;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.SalesType;
import com.code.aon.sales.Seller;

public class SalesAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("/AON-PROJECT/aon-sales/src/main/java/com/code/aon/sales/dao/ISalesAlias.java");
		String[] classes = new String[6];
		classes[0] = Sales.class.getName();
		classes[1] = SalesAttachment.class.getName();
		classes[2] = SalesDetail.class.getName();
		classes[3] = SalesType.class.getName();
		classes[4] = Seller.class.getName();
		classes[5] = PointOfSale.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.sales.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}