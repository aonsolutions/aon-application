package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

public class Mod303DefaultAccountEntryScript {
	
	private static final AccountEntryDetailExpressionScript[] SCRIPTS = new AccountEntryDetailExpressionScript[] {
			new Mod3032022AEATAccountEntryScript()
	};
	
	public static boolean accept(Mod303 mod) {
		return Arrays
			.stream(SCRIPTS)
			.anyMatch( script -> script.accept(mod) );
	}
	
	public static Optional<AccountEntryDetailExpressionScript> getScript(Mod303 mod) {
		return Arrays
			.stream(SCRIPTS)
			.filter( script -> script.accept(mod) )
			.findFirst();
	}

}
