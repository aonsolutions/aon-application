package com.esferalia.aon.in.payroll.tgss.ivl;

import org.junit.Test;

import com.esferalia.aon.in.payroll.ivl.IvlCccParser;
import com.esferalia.aon.in.payroll.ivl.IvlParserListener;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IvlTest {

	String socialReason;
	String ccc;
	String nif;
	String economicAtivityCode;
	String economicActivityDescription;
	String regime;
	String fullCCC;
	String city;
	String address;
	String cp;
	String startPeriodDate;
	String endPeriodDate;
	String it;
	String ims;
	String total;
	String naf;
	String doctype;
	String docNum;
	String fullName;
	String nss;
	String ident;
	String situation;
	Date start;
	Date effect;
	Date startSit;
	Date effectSit;
	String gc;
	String tc;
	String cotDays;
	String clv;

	@Test
	public void testIvlCcc() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {

		try (InputStream is = IvlTest.class.getResourceAsStream("document-31.pdf")) {

			IvlCccParser.parse(is, new IvlParserListener() {

				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {

					assertEquals("RAZÓN SOCIAL:", "OPERA DIVERTIMENTO S.L", socialReason);
					System.out.println(socialReason);
					assertEquals("CÓDIDO CUENTA DE COTIZACIÓN:", "0112 48 112115285", fullCCC); 
					assertEquals("EMPRESARIO", "9 0B9567771", nif);

					assertEquals("CNAE", "9001 Artes escénicas", economicActivityCode + economicActivityDescription);

				}

				@Override
				public void onEnterprisePeriod(String startPeriodDate, String endPeriodDate) {
					assertEquals("PERIODO SOLICITADO", "01 05 2020 / 17 03 2023",
							startPeriodDate + "/" + endPeriodDate);

				}

				@Override
				public void onAtTypes(String it, String ims, String total) {
					assertEquals("IT", "0,80", it);
					assertEquals("IMS", "0,70", ims);
					assertEquals("TOTAL", "1,50", total);

				}

				@Override
				public void onEmployeeIdent(String nss, String fullname) {
					Map<String, String> NSS_NAME_MAP = new HashMap<String, String>();
					NSS_NAME_MAP.put("18 1029047337", "VIRGINA HERNANDEZ JIMENEZ");
					NSS_NAME_MAP.put("18 1029828892", "BLANCA ISABEL ROMERA LUZON");
					NSS_NAME_MAP.put("28 0392604258", "CRISTINA GARCIA CORRALES");
					NSS_NAME_MAP.put("28 0448829094", "CARMELO LUIS PEÑA MORENO");
					NSS_NAME_MAP.put("28 1000120658", "ANGEL CASTILLA RAMIREZ");
					NSS_NAME_MAP.put("28 1280066092", "RAJIV CEREZO CHUGANI");
					NSS_NAME_MAP.put("28 1352569653", "ROSA MARIA GOMARIZ GAVIRA");
					NSS_NAME_MAP.put("28 1409613939", "RAQUEL MORENO PUCHE");
					NSS_NAME_MAP.put("28 1505398611", "CRISTIAN CAMILO DIAZ NAVARRO");
					NSS_NAME_MAP.put("28 1352569653", "WILLINGERD SAMUEL GIMENEZ AGUILAR");
					NSS_NAME_MAP.put("41 1062192358", "JAVIER CALA ESPADERO");

					assertEquals("NÚMERO DE AFILIACIÓN", NSS_NAME_MAP.get(nss), fullname);

				}

				@Override
				public void onEmployee(String naf, String docType, String docNum, String fullName) {
					Map<String, String> NSS_NAF_MAP = new HashMap<String, String>();
					NSS_NAF_MAP.put("18 1029047337", "1 074654580F");
					NSS_NAF_MAP.put("18 1029828892", "1 075152300F");
					NSS_NAF_MAP.put("28 0392604258", "1 050711784G");
					NSS_NAF_MAP.put("28 0448829094", "1 024406242E");
					NSS_NAF_MAP.put("28 1000120658", "1 001930989R");
					NSS_NAF_MAP.put("28 1280066092", "1 070419332W");
					NSS_NAF_MAP.put("28 1352569653", "1 050632213J");
					NSS_NAF_MAP.put("28 1409613939", "1 053856371P");
					NSS_NAF_MAP.put("28 1453747222", "6 0Y2096155H");
					NSS_NAF_MAP.put("28 1505398611", "6 0Y5996586V");
					NSS_NAF_MAP.put("41 1062192358", "1 028629513X");

					assertEquals("DOCUMENTO IDENTIFICATIVO", NSS_NAF_MAP.get(nss), docType + docNum);

				}

			});

		}

	}

	public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {

		assertEquals("RAZÓN SOCIAL:", "OPERA DIVERTIMENTO S.L.", socialReason);
		System.out.println(socialReason);
		assertEquals("CÓDIDO CUENTA DE COTIZACIÓN:", "0112 48 112115285", fullCCC);
		assertEquals("EMPRESARIO", "9 0B9567771", nif);

		assertEquals("CNAE", "9001 Artes escénicas", economicActivityCode + economicActivityDescription);

	}

	public void onEnterpriseAddress(String city, String address, String cp) {
		assertEquals("DOMICILIO", "AV BASAGOITI 75 3 IZD", address);
		assertEquals("LOCALIDAD", "GETXO", city);
		assertEquals("C.P.", "48991", cp);

	}
}
