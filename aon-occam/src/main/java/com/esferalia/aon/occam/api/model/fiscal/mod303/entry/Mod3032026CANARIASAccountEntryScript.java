package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Mod3032026CANARIASAccountEntryScript extends AccountEntryDetailExpressionScript<Mod303> {
	
	private static final long serialVersionUID = 5101328272400058340L;
	
	private static final LinkedList<AccountEntryDetailExpression> details = new LinkedList<>();
	static {

		// Total cuota devengada
//		details.add(new AccountEntryDetailExpression( false )
//				.setAccount("477000000")
//				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
//				.setExpression("C27")); 			// DEBIT
		// Total cuota deducible
//		details.add(new AccountEntryDetailExpression( true )
//				.setAccount("472000000")
//				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
//				.setExpression("C45"));  		// CREDIT
		// Cuotas a compensar de periodos anteriores aplicadas en este periodo
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("C43")); 			// DEBIT
		// A deducir.(exclusivamente en caso de autoliquidación complementaria)	
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esComplementaria() && C44 > 0)?C44:0.0")); // DEBIT
		// A deducir.(exclusivamente en caso de autoliquidación complementaria)	
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esComplementaria() && C44 < 0)?C44:0.0")); // CREDIT
		
//		// A deducir.(exclusivamente en caso de autoliquidación complementaria)	
//		details.add(new AccountEntryDetailExpression( true )
//				.setAccount("472000000")
//				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
//				.setExpression("prorrataIVA()")); // CREDIT
		
		// Resultado a pagar
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?C45:0.0")); // CREDIT
		// Resultado a devolver/compensar
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?0.0:abs(C45)")); // DEBIT
	}

	@Override
	public boolean accept(Mod303 mod) {
		return mod.isCanarias() && mod.getYear() >= 2026;
	}

	@Override
	public LinkedList<AccountEntryDetailExpression> getDetails() {
		return details;
	}
	
	@Override
	public IFiscalModelKey getAccruedKey() {
		return Mod303Key.CA_C025;
	}

	@Override
	public IFiscalModelKey getDeductibleKey() {
		return Mod303Key.CA_C040;
	}

}
