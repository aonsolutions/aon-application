package com.code.aon.composition.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.CompositionExpense;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.composition.ProductionExpense;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class CompositionAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		File file = new File("/PROYECTOS/aon-account/src/com/code/aon/commercial/dao/ICommercialAlias.java");
		File file = new File("c:/ICompositionAlias.java");
		String[] classes = new String[6]; 
		classes[0] = Composition.class.getName();
		classes[1] = CompositionDetail.class.getName();
		classes[2] = CompositionExpense.class.getName();
		classes[3] = Production.class.getName();
		classes[4] = ProductionDetail.class.getName();
		classes[5] = ProductionExpense.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.composition.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}