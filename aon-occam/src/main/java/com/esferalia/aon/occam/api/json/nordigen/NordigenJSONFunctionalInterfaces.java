package com.esferalia.aon.occam.api.json.nordigen;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetails;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;

public class NordigenJSONFunctionalInterfaces {
	
	private NordigenJSONFunctionalInterfaces() {
		  throw new IllegalStateException("Utility class");
	}
	
	//INSTITUTION
	@FunctionalInterface
	public static interface INordigenInstitutionFromJSON {
		NordigenInstitution from(NordigenInstitution institution, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenInstitutionToJSON {
		JSONObject to(NordigenInstitution institution, JSONObject json);
	}
	//-----------
	
	//AGREEMENT
	@FunctionalInterface
	public static interface INordigenAgreementFromJSON {
		NordigenAgreement from(NordigenAgreement institution, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenAgreementToJSON {
		JSONObject to(NordigenAgreement institution, JSONObject json);
	}
	//-----------
	
	//REQUISITION
	@FunctionalInterface
	public static interface INordigenRequisitionFromJSON {
		NordigenRequisition from(NordigenRequisition requisition, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenRequisitionToJSON {
		JSONObject to(NordigenRequisition requisition, JSONObject json);
	}
	//-----------
	
	//ACCOUNT METADATA
	@FunctionalInterface
	public static interface INordigenAccountMetadataFromJSON {
		NordigenAccountMetadata from(NordigenAccountMetadata account, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenAccountMetadataToJSON {
		JSONObject to(NordigenAccountMetadata account, JSONObject json);
	}
	//-----------
	
	//ACCOUNT BALANCE
	@FunctionalInterface
	public static interface INordigenAccountBalanceFromJSON {
		NordigenAccountBalance from(NordigenAccountBalance balance, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenAccountBalanceToJSON {
		JSONObject to(NordigenAccountBalance balance, JSONObject json);
	}
	//-----------
	
	//ACCOUNT DETAILS
	@FunctionalInterface
	public static interface INordigenAccountDetailsFromJSON {
		NordigenAccountDetails from(NordigenAccountDetails details, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenAccountDetailsToJSON {
		JSONObject to(NordigenAccountDetails details, JSONObject json);
	}
	//-----------
	
	//ACCOUNT DETAILS
	@FunctionalInterface
	public static interface INordigenAccountTransactionFromJSON {
		NordigenAccountTransaction from(NordigenAccountTransaction details, JSONObject json);
	}
	@FunctionalInterface
	public static interface INordigenAccountTransactionToJSON {
		JSONObject to(NordigenAccountTransaction details, JSONObject json);
	}
	//-----------
}
