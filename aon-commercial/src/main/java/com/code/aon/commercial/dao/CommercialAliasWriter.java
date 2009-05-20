package com.code.aon.commercial.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialSegment;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetItem;
import com.code.aon.commercial.TargetSegment;
import com.code.aon.commercial.TargetSeller;
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
		File file = new File("/AON-PROJECT/aon-commercial/src/main/java/com/code/aon/commercial/dao/ICommercialAlias.java");
//		File file = new File("c:/ICommercialAlias.java");
		String[] classes = new String[] { 
				Offer.class.getName(),
				OfferDetail.class.getName(),
				Target.class.getName(),
				CommercialActivity.class.getName(),
				CommercialSegment.class.getName(),
				CommercialTracking.class.getName(),
				TargetItem.class.getName(),
				TargetSegment.class.getName(),
				TargetSeller.class.getName() };
		AliasWriter writer = new AliasWriter("com.code.aon.commercial.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}