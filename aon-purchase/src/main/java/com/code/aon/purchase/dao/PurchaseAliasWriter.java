package com.code.aon.purchase.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.Supplier;
import com.code.aon.purchase.SupplierSegment;

public class PurchaseAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-purchase/src/com/code/aon/purchase/dao/IPurchaseAlias.java");
        String[] classes = new String[4];
        classes[0] = PurchaseDetail.class.getName();
        classes[1] = Purchase.class.getName();
        classes[2] = Supplier.class.getName();
        classes[3] = SupplierSegment.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.purchase.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");
	}
}

