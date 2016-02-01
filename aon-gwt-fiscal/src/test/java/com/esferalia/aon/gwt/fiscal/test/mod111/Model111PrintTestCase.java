package com.esferalia.aon.gwt.fiscal.test.mod111;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111AEAT;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba2016;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Base.IModelScript;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model715Navarra;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model745Navarra;
import com.esferalia.aon.gwt.fiscal.server.FiscalModelExcelAction;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;

public class Model111PrintTestCase {

	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";

	public static void main(String[] args) throws IOException {
		LinkedList<Mod111> list = AON.getMod111s(DOMAIN_NAME, DOMAIN_ID, USER);
		for (Mod111 mod111 : list) {
			if (mod111.getYear() == 2015) {
				mod111 = AON.getMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111.getId());
				toExcel(mod111, obtainScript(mod111));
			}
		}
	}

	private static IModelScript[] obtainScript(Mod111 mod111) {
		IModelScript[] ms = null;
		if (mod111.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model111AEAT.ModelScript.values();
		} else if (mod111.getAdministration() == Administration.GIPUZKOA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Gipuzkoa.ModelScript.values();
			} else {
				ms = Model111Gipuzkoa.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.BIZKAIA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Bizkaia.ModelScript.values();
			} else {
				ms = Model111Bizkaia.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.NAVARRA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model715Navarra.ModelScript.values();
			} else {
				ms = Model745Navarra.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.ALAVA) {
			if (mod111.getYear() > 2015) {
				ms = Model111Araba2016.ModelScript.values();
			} else {
				ms = Model111Araba.ModelScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod111.getAdministration().toString() + " "
							+ mod111.getYear() + " " + mod111.getPeriod().getDescription());
		}
		return ms;
	}

	private static void toExcel(Mod111 mod111, IModelScript[] script) throws IOException {
		FiscalModelExcelAction action = new FiscalModelExcelAction(mod111);
		action.initialize(mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()));
		
		String s = mod111.getName();
		StringBuilder sb = new StringBuilder();
		if (!Character.isJavaIdentifierStart(s.charAt(0))) {
			sb.append("_");
		}
		for (char c : s.toCharArray()) {
			if (Character.isJavaIdentifierPart(c)) {
				sb.append(c);
			}
		}

		String fileName = "/tmp/test/Mod"
			+ mod111.getModel().getName(mod111.getAdministration(),mod111.getPeriod() )
			+ "_" + mod111.getYear() 
			+ "_" + mod111.getPeriod().getName()
			+ "_" + mod111.getAdministration().toString()
			+ "_" + sb.toString()
			+ ".xlsx";

		FileOutputStream out = new FileOutputStream(fileName);
		for (IModelScript ms : script) {
			action.accept(ms);
		}
		action.finalize(out);
	}
}
