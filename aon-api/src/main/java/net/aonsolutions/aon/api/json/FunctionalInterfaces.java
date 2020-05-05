package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;


public class FunctionalInterfaces {

	@FunctionalInterface
	public static interface IAonAccountFromJSON {
		Account from(Account t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountToJSON {
		JSONObject to(Account t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceTaxFromJSON {
		InvoiceBreakdown from(InvoiceBreakdown t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceTaxToJSON {
		JSONObject to(InvoiceBreakdown t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceDetailFromJSON {
		InvoiceDetail from(InvoiceDetail t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceDetailToJSON {
		JSONObject to(InvoiceDetail t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceFromJSON {
		Invoice from(Invoice t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonInvoiceToJSON {
		JSONObject to(Invoice t, Company c, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonFinanceFromJSON {
		Finance from(Finance t,JSONObject json);
	}
	
	@FunctionalInterface
	public static interface IAonFinanceToJSON {
		JSONObject to(Finance t,JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAddressFromJSON {
		RAddress from(RAddress t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAddressToJSON {
		JSONObject to(RAddress t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonRegistryFromJSON {
		Registry from(Registry t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonRegistryToJSON {
		JSONObject to(Registry t, JSONObject json);
	}

}
