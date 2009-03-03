package com.code.aon.supplier.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.SupplierSegment;

public class SupplierAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-supplier/src/main/java/com/code/aon/supplier/dao/ISupplierAlias.java");
        String[] classes = new String[2];
        classes[0] = Supplier.class.getName();
        classes[1] = SupplierSegment.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.supplier.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");
	}
}