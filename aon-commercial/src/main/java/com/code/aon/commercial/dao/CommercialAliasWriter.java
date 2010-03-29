package com.code.aon.commercial.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Expense;
import com.code.aon.commercial.ExpenseAccount;
import com.code.aon.commercial.ExpenseAccountDetail;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetItem;
import com.code.aon.commercial.TargetSeller;
import com.code.aon.commercial.TargetThirdParty;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

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
		File file = new File("/AON-TRUNK/aon-commercial/src/main/java/com/code/aon/commercial/dao/ICommercialAlias.java");
		String[] classes = new String[] { 
				CommercialActivity.class.getName(),
				CommercialTracking.class.getName(),
				CommercialTerm.class.getName(),
				Expense.class.getName(),
				ExpenseAccount.class.getName(),
				ExpenseAccountDetail.class.getName(),
				Offer.class.getName(),
				OfferAttachment.class.getName(),
				OfferDetail.class.getName(),
				OfferTerm.class.getName(),
				Target.class.getName(),
				TargetItem.class.getName(),
				TargetSeller.class.getName(),
				TargetThirdParty.class.getName(),};
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.commercial.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}