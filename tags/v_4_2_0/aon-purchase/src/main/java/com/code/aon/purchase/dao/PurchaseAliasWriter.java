package com.code.aon.purchase.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;

public class PurchaseAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-purchase/src/main/java/com/code/aon/purchase/dao/IPurchaseAlias.java");
        String[] classes = new String[2];
        classes[0] = PurchaseDetail.class.getName();
        classes[1] = Purchase.class.getName();
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.purchase.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");
	}
}