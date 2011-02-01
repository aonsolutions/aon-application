package com.code.aon.fiscal.dao;


import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.RentingDetail;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;

public class FiscalAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/home/ecastellano/AON-TRUNK/aon-fiscal/src/main/java/com/code/aon/fiscal/dao/IFiscalAlias.java");
		String[] classes = new String[]{
			Renting.class.getName(),
			RentingDetail.class.getName(),
			VatTax.class.getName(),
			VatTaxDetail.class.getName(),
			VatTaxDeclaration.class.getName()
		};
		AliasWriter writer = new AliasWriter("com.code.aon.fiscal.dao");
		HibernateUtil.getSessionFactory(null);
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
		
	}
}