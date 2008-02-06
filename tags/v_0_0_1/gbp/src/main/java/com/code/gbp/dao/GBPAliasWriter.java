package com.code.gbp.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.gbp.Campaign;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.GeoZone;
import com.code.gbp.Incidence;
import com.code.gbp.IncidenceType;
import com.code.gbp.Offer;
import com.code.gbp.OfferSignature;
import com.code.gbp.Office;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.Requirement;
import com.code.gbp.Supplier;
import com.code.gbp.SupplierAddInfo;
import com.code.gbp.SupplierContact;
import com.code.gbp.SupplierEconomicData;
import com.code.gbp.SupplierObservation;

public class GBPAliasWriter {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/gbp/src/main/java/com/code/gbp/dao/IGBPAlias.java");
		String[] classes = new String[16];
		classes[0] = Campaign.class.getName();
		classes[1] = CampaignSupplier.class.getName();
		classes[2] = GeoZone.class.getName();
		classes[3] = Incidence.class.getName();
		classes[4] = IncidenceType.class.getName();
		classes[5] = OfferSignature.class.getName();
		classes[6] = Offer.class.getName();
		classes[7] = Office.class.getName();
		classes[8] = ProFormaInvoice.class.getName();
		classes[9] = ProFormaSignature.class.getName();
		classes[10] = Requirement.class.getName();
		classes[11] = Supplier.class.getName();
		classes[12] = SupplierAddInfo.class.getName();
		classes[13] = SupplierContact.class.getName();
		classes[14] = SupplierEconomicData.class.getName();
		classes[15] = SupplierObservation.class.getName();
		AliasWriter writer = new AliasWriter("com.code.gbp.dao");
		writer.write(classes, file);
		System.out.println(file.getAbsolutePath());
		System.out.println("Alias generados");
	}
}