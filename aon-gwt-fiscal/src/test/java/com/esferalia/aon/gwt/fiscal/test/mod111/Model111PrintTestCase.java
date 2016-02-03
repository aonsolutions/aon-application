package com.esferalia.aon.gwt.fiscal.test.mod111;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.server.FiscalModelExcelAction;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;

public class Model111PrintTestCase {

	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";

	public static void main(String[] args) throws IOException {
		LinkedList<Mod111> list = AON.getMod111s(DOMAIN_NAME, DOMAIN_ID, USER);
		for (Mod111 mod111 : list) {
			if (mod111.getYear() == 2015) {
				mod111 = AON.getMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111.getId());
				toExcel(mod111, Model111ScriptProvider.obtainScript(mod111));
			}
		}
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
