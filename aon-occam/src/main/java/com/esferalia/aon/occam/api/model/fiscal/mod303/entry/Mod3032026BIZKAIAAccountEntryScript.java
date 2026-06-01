package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Mod3032026BIZKAIAAccountEntryScript extends AccountEntryDetailExpressionScript<Mod303> {

	private static final long serialVersionUID = -3079822104538418595L;
	
	private static final LinkedList<AccountEntryDetailExpression> details = new LinkedList<>();
	static {
		
		// Total cuota devengada DEBIT
//		details.add(new AccountEntryDetailExpression( false )
//				.setAccount("477000000")
//				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
//				.setExpression("C16"));
		// Total cuota deducible CREDIT
//		details.add(new AccountEntryDetailExpression( true )
//				.setAccount("472000000")
//				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
//				.setExpression("C25")); 
		// Cuotas a compensar de periodos anteriores aplicadas en este periodo DEBIT
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("C34")); 			// 
		// Resultado a pagar CREDIT
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?C36:0.0")); 
		// Resultado a devolver/compensar DEBIT
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?0.0:abs(C36)"));
	}

	@Override
	public boolean accept(Mod303 mod) {
		return mod.isBizkaia() && mod.getYear() >= 2026;
	}

	@Override
	public LinkedList<AccountEntryDetailExpression> getDetails() {
		return details;
	}

	@Override
	public IFiscalModelKey getAccruedKey() {
		return Mod303Key.BZ_C023;
	}

	@Override
	public IFiscalModelKey getDeductibleKey() {
		return Mod303Key.BZ_C030;
	}

}
