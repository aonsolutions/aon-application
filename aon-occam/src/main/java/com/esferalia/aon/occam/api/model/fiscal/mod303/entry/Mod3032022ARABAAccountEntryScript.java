package com.esferalia.aon.occam.api.model.fiscal.mod303.entry;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

public class Mod3032022ARABAAccountEntryScript extends AccountEntryDetailExpressionScript<Mod303> {
	private static final long serialVersionUID = 2085902232416480013L;
	
	private static final LinkedList<AccountEntryDetailExpression> details = new LinkedList<>();
	static {
		
		// Total cuota devengada DEBIT
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("477000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("C28"));
		// Total cuota deducible CREDIT
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("472000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("C38")); 
		// Cuotas a compensar de periodos anteriores aplicadas en este periodo DEBIT
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("C45")); 			// 
		// A deducir.(exclusivamente en caso de autoliquidación complementaria) DEBIT	
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esSustitutiva() && C63 > 0)?C63:0.0"));
		// A deducir.(exclusivamente en caso de autoliquidación complementaria) CREDIT	
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("(esSustitutiva() && C63 < 0)?C63:0.0"));
		// Resultado a pagar CREDIT
		details.add(new AccountEntryDetailExpression( true )
				.setAccount("475000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?C80:0.0")); 
		// Resultado a devolver/compensar DEBIT
		details.add(new AccountEntryDetailExpression( false )
				.setAccount("470000000")
				.setConceptExpression(MODEL_FULL_NAME_EXPRESSION)
				.setExpression("aIngresar()?0.0:abs(C80)"));
	}

	@Override
	public boolean accept(Mod303 mod) {
		return mod.isAraba() && mod.getYear() >= 2022;
	}

	@Override
	public LinkedList<AccountEntryDetailExpression> getDetails() {
		return details;
	}

}
