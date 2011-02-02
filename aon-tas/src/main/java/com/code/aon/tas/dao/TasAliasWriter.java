package com.code.aon.tas.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.tas.Appraiser;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.SupportOrderInsurance;
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
        File file = new File("/AON-PROJECT/aon-tas/src/main/java/com/code/aon/tas/dao/ITASAlias.java");
		String[] classes = new String[6];
		classes[0] = Appraiser.class.getName();
		classes[1] = Make.class.getName();
		classes[2] = Model.class.getName();
		classes[3] = SupportOrder.class.getName();
		classes[4] = SupportOrderInsurance.class.getName();
		classes[5] = TasItem.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.tas.dao");
		writer.write(classes,file);
		System.out.println("Alias generados");
	}
}