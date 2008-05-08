package com.code.aon.tas.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.TasItem;

/**
 * ITASAlias.java writer
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class TasAliasWriter {
	public static void main(String[] args) throws IOException {
        File file = new File("./src/com/code/aon/tas/dao/ITASAlias.java");
		String[] classes = new String[4];
		classes[0] = Make.class.getName();
		classes[1] = Model.class.getName();
		classes[2] = TasItem.class.getName();
		classes[3] = SupportOrder.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.tas.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");

	}
}
