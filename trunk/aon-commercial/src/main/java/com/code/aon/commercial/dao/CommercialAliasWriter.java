package com.code.aon.commercial.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.common.dao.AliasWriter;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class CommercialAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-commercial/src/com/code/aon/commercial/dao/ICommercialAlias.java");
//		File file = new File("c:/ICommercialAlias.java");
		String[] classes = new String[3]; 
		classes[0] = Offer.class.getName();
		classes[1] = OfferDetail.class.getName();
		classes[2] = Target.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.commercial.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}