package com.esferalia.aon.in.payroll.tgss.ivl;

import java.io.IOException;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class IvlTestMain {

	public static void main(String[] args) {

		String socialReason = "OPERA DIVERTIMENTO S.L.";
		String ccc = "";
		String nif = "9 0B9567771";
		String economicActivityCode = "9001";
		String economicActivityDescription = " Artes escénicas";
		String regime = "";
		String fullCCC = "0112 48 112115285";
		String city = "GETXO";
		String address = "AV BASAGOITI 75 3 IZD";
		String cp = "";

		IvlTest iv = new IvlTest();
		try {
			iv.testIvlCcc();
			iv.onEnterprise(socialReason, ccc, nif, economicActivityCode, economicActivityDescription, regime, fullCCC);
			iv.onEnterpriseAddress(city, address, cp);

		} catch (UnknownPDFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
