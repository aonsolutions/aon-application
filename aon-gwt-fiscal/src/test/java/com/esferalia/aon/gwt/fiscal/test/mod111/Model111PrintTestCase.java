package com.esferalia.aon.gwt.fiscal.test.mod111;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.server.Mod111ExcelAction;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Model111PrintTestCase {

	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";

	public static void main(String[] args) throws IOException {
		LinkedList<Mod111> list = FISCAL.getMod111s(DOMAIN_NAME, DOMAIN_ID, USER);
		for (Mod111 mod111 : list) {
			if (mod111.getYear() == 2015) {
				mod111 = FISCAL.getMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111.getId());
				toExcel(mod111, Model111ScriptProvider.obtainScript(mod111));
			}
		}
	}

	private static void toExcel(Mod111 mod111, IModelScript<Mod111Key>[] script) throws IOException {
		Mod111ExcelAction action = new Mod111ExcelAction(mod111);
		action.initialize(FiscalModelUtils.getModelName(mod111));
		
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
			+ FiscalModelUtils.getModelName(mod111)
			+ "_" + mod111.getYear() 
			+ "_" + mod111.getPeriod().getName()
			+ "_" + mod111.getAdministration().toString()
			+ "_" + sb.toString()
			+ ".xlsx";

		FileOutputStream out = new FileOutputStream(fileName);
		for (IModelScript<Mod111Key> ms : script) {
			action.accept(ms);
		}
		action.finalize(out);
	}
}
