package com.esferalia.aon.gwt.common.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonSuggestionService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Aon Suggestions Servlet", urlPatterns = { 
	"/aon_gwt_fiscal/ms/AonSuggestions"
	, "/aon_gwt_mod200/ms/AonSuggestions"
	, "/aon_gwt_aio/ms/AonSuggestions"
	, "/aon_gwt_marketing/ms/AonSuggestions"}
)

public class AonSuggestionServiceImpl extends AonStatelessRemoteServiceServlet implements AonSuggestionService {

	private static final long serialVersionUID = 1L;

	// [CUSTOMER]
	@Override
	public LinkedList<Customer> getCustomers(Occam occam, Integer domainId, String query) throws AonCoreException {
		return AON.getCustomersSuggestion(occam, domainId, query)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	// [INVOICING GROUP]
	@Override
	public LinkedList<InvoicingGroup> getInvoicingGroups(Occam occam, Integer domainId, String query) throws AonCoreException {
		return AON.getInvoicingGroupsSuggestion(occam, domainId, query)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	// [ITEM]
	@Override
	public LinkedList<Item> getItems(Occam occam, Integer domainId, String query) throws AonCoreException {
		return AON.getItemsSuggestion(occam, domainId, query)
			.collect(Collectors.toCollection(LinkedList::new));
	}
}
