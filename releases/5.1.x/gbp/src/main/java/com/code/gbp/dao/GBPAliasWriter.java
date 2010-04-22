package com.code.gbp.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.gbp.AccountContact;
import com.code.gbp.Area;
import com.code.gbp.AreaGroup;
import com.code.gbp.Bank;
import com.code.gbp.BankPercent;
import com.code.gbp.BatchConfig;
import com.code.gbp.Campaign;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.GeoZone;
import com.code.gbp.Incidence;
import com.code.gbp.IncidenceType;
import com.code.gbp.InternalCustomer;
import com.code.gbp.Offer;
import com.code.gbp.OfferSignature;
import com.code.gbp.Office;
import com.code.gbp.ProFormaBank;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.Requirement;
import com.code.gbp.Supplier;
import com.code.gbp.SupplierAddInfo;
import com.code.gbp.SupplierContact;
import com.code.gbp.SupplierContactPerson;
import com.code.gbp.SupplierEconomicData;
import com.code.gbp.SupplierObservation;
import com.code.gbp.SupplierType;

public class GBPAliasWriter {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/gbp/src/main/java/com/code/gbp/dao/IGBPAlias.java");
		String[] classes = new String[26];
		classes[0] = AccountContact.class.getName();
		classes[1] = Campaign.class.getName();
		classes[2] = CampaignSupplier.class.getName();
		classes[3] = GeoZone.class.getName();
		classes[4] = Incidence.class.getName();
		classes[5] = IncidenceType.class.getName();
		classes[6] = InternalCustomer.class.getName();
		classes[7] = OfferSignature.class.getName();
		classes[8] = Offer.class.getName();
		classes[9] = Office.class.getName();
		classes[10] = ProFormaInvoice.class.getName();
		classes[11] = ProFormaSignature.class.getName();
		classes[12] = Requirement.class.getName();
		classes[13] = Supplier.class.getName();
		classes[14] = SupplierAddInfo.class.getName();
		classes[15] = SupplierContact.class.getName();
		classes[16] = SupplierContactPerson.class.getName();
		classes[17] = SupplierEconomicData.class.getName();
		classes[18] = SupplierObservation.class.getName();
		classes[19] = SupplierType.class.getName();
		classes[20] = Bank.class.getName();
		classes[21] = BankPercent.class.getName();
		classes[22] = ProFormaBank.class.getName();
		classes[23] = BatchConfig.class.getName();
		classes[24] = AreaGroup.class.getName();
		classes[25] = Area.class.getName();
		AliasWriter writer = new AliasWriter("com.code.gbp.dao");
		writer.write(classes, file);
		System.out.println(file.getAbsolutePath());
		System.out.println("Alias generados");
	}
}