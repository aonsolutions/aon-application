package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediComments;
import es.translogia.tedi.ewok.TediCompany;
import es.translogia.tedi.ewok.TediEmailInfo;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInsightInvoice;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceFile;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediPlan;
import es.translogia.tedi.ewok.TediRegistry;

public class FunctionalInterfaces {

	@FunctionalInterface
	public static interface ITediInvoiceFileFromJSON {
		TediInvoiceFile from(TediInvoiceFile t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceFileToJSON {
		JSONObject to(TediInvoiceFile t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceTaxFromJSON {
		TediInvoiceTax from(TediInvoiceTax t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceTaxToJSON {
		JSONObject to(TediInvoiceTax t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceDetailFromJSON {
		TediInvoiceDetail from(TediInvoiceDetail t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceDetailToJSON {
		JSONObject to(TediInvoiceDetail t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceFromJSON {
		TediInvoice from(TediInvoice t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInvoiceToJSON {
		JSONObject to(TediInvoice t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediFinanceFromJSON {
		TediFinance from(TediFinance t,JSONObject json);
	}
	
	@FunctionalInterface
	public static interface ITediFinanceToJSON {
		JSONObject to(TediFinance t,JSONObject json);
	}
	
	@FunctionalInterface
	public static interface ITediCompanyFromJSON {
		TediCompany from(TediCompany t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediCompanyToJSON {
		JSONObject to(TediCompany t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediAddressFromJSON {
		TediAddress from(TediAddress t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediAddressToJSON {
		JSONObject to(TediAddress t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediPlanFromJSON {
		TediPlan from(TediPlan t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediPlanToJSON {
		JSONObject to(TediPlan t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediRegistryFromJSON {
		TediRegistry from(TediRegistry t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediRegistryToJSON {
		JSONObject to(TediRegistry t, JSONObject json);
	}
	
	@FunctionalInterface
	public static interface ITediEmailInfoFromJSON {
		TediEmailInfo from(TediEmailInfo t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediEmailInfoToJSON {
		JSONObject to(TediEmailInfo t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediCommentsFromJSON {
		TediComments from(TediComments t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediCommentsToJSON {
		JSONObject to(TediComments t, JSONObject json);
	}
	
	@FunctionalInterface
	public static interface ITediInsightInvoiceFromJSON {
		TediInsightInvoice from(TediInsightInvoice t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediInsightInvoiceToJSON {
		JSONObject to(TediInsightInvoice t, JSONObject json);
	}
	
	@FunctionalInterface
	public static interface ITediNifFromJSON {
		TediNif from(TediNif t, JSONObject json);
	}

	@FunctionalInterface
	public static interface ITediNifToJSON {
		JSONObject to(TediNif t, JSONObject json);
	}
}
