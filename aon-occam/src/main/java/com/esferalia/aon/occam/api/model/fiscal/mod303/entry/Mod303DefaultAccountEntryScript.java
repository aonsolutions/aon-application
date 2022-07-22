package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

public class Mod303DefaultAccountEntryScript {
	
	private Mod303DefaultAccountEntryScript() {
		
	}
	
	private static final LinkedList<AccountEntryDetailExpressionScript<Mod303>> SCRIPTS = new LinkedList<>();
	static {
		SCRIPTS.add(new Mod3032022AEATAccountEntryScript());
		SCRIPTS.add(new Mod3032022BIZKAIAAccountEntryScript());
		SCRIPTS.add(new Mod3032022GIPUZKOAAccountEntryScript());
		SCRIPTS.add(new Mod3032022ARABAAccountEntryScript());
		SCRIPTS.add(new Mod3032022NAVARRAAccountEntryScript());
	}
	
	public static boolean accept(Mod303 mod) {
		return SCRIPTS
			.stream()
			.anyMatch( script -> script.accept(mod) );
	}
	
	public static Optional<AccountEntryDetailExpressionScript<Mod303>> getScript(Mod303 mod) {
		return SCRIPTS
			.stream()
			.filter( script -> script.accept(mod) )
			.findFirst();
	}

}
