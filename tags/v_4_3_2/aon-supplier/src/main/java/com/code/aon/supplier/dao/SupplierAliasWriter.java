package com.code.aon.supplier.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.supplier.ItemSupplier;
import com.code.aon.supplier.Supplier;

public class SupplierAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-supplier/src/main/java/com/code/aon/supplier/dao/ISupplierAlias.java");
		String[] classes = new String[] {
			ItemSupplier.class.getName(),
			Supplier.class.getName()
		};
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Supplier.class.getName());
		HibernateUtil.getSessionFactory(sessionFactoryName);
		AliasWriter writer = new AliasWriter("com.code.aon.supplier.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");
	}
}