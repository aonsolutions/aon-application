package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

public class Mod3032022AEATAccountEntryScript extends AccountEntryDetailExpressionScript {
	private static final String MODEL_FULL_NAME_EXPRESSION = "nombreModelo(model)";

	private static final long serialVersionUID = 2085902232416480013L;
	
	private static final LinkedList<AccountEntryDetailExpression> details = new LinkedList<>();
	static {
		
		// Total cuota devengada
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("477000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("CT_C27")); 			// DEBIT
		// Total cuota deducible
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("472000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("CT_C45"));  		// CREDIT
		// Cuotas a compensar de periodos anteriores aplicadas en este periodo
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("CT_C78")); 			// DEBIT
		// A deducir.(exclusivamente en caso de autoliquidación complementaria)	
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esComplementaria(model) && CT_C70 > 0)?CT_C70:0.0")); // DEBIT
		// A deducir.(exclusivamente en caso de autoliquidación complementaria)	
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esComplementaria(model) && CT_C70 < 0)?CT_C70:0.0")); // CREDIT
		// Resultado a pagar
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar(model)?CT_C71:0.0")); // CREDIT
		// Resultado a devolver/compensar
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar(model)?0.0:CT_C71")); // DEBIT
	}

	@Override
	public boolean accept(Mod303 mod) {
		return mod.isAEAT() && mod.getYear() >= 2022;
	}

	@Override
	public LinkedList<AccountEntryDetailExpression> getDetails() {
		return details;
	}

}
