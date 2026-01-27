package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class AonCustomerSuggestBox extends AonSuggestBox implements HasSelectionHandlers<Customer>{

	private Customer customer;
	
	public <T> AonCustomerSuggestBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.customer());
	}
	
	public AonCustomerSuggestBox(AonModuleOptions<?> opts, String title) {
		super(title, new AonCustomerSuggestOracle(opts) );
		this.setAutoSelectEnabled(false);
		this.setPlaceHolder(AON.MSG.registryPlaceHolder());
		getSuggestBox().addSelectionHandler(e -> {
			CustomerSuggestion selected = (CustomerSuggestion) e.getSelectedItem();
			setCustomer( selected.getCustomer() );	
		});
		getSuggestBox().addSelectionHandler(event -> {
			CustomerSuggestion selected = (CustomerSuggestion) event.getSelectedItem();
			setCustomer( selected.getCustomer() );
		});
		getSuggestBox().addValueChangeHandler( e -> {
			if ( AonStringUtils.isBlank( getSuggestBox().getValue() )) {
				setCustomer( null );	
			}
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Customer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<Customer> getCustomer() {
		return Optional.ofNullable(customer);
	}
	private void setCustomer(Customer customer) {
		setCustomer(customer, true);
	}
	private void setCustomer(Customer customer, boolean fireEvent) {
		this.customer = customer;
		if (fireEvent) SelectionEvent.fire(AonCustomerSuggestBox.this, this.customer );
	}
	
	private static class CustomerSuggestion extends MultiWordSuggestion {
		
		private Customer customer;
		
		private CustomerSuggestion(Customer customer, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.customer = customer;
		}
		
		public Customer getCustomer() {
			return customer;
		}
	}
	
	private static class AonCustomerSuggestOracle extends MultiWordSuggestOracle {
		private Timer searchTimer;
		private AonModuleOptions<?> opts;
		private AonCustomerSuggestOracle(AonModuleOptions<?> opts) {
			this.opts = opts;
		}
		@Override
		public void requestSuggestions(final Request request,final Callback callback) {
			String query = request.getQuery();
	        int length = AonStringUtils.length(query);
	        if (length < MIN_CHARACTERS || length > MAX_CHARACTERS) return;
	        if (searchTimer != null && searchTimer.isRunning()) {
	            searchTimer.cancel();
	        }
	        searchTimer = new Timer() {
	        	@Override
	            public void run() {
					SERVICE.getCustomers(opts.getOccam(),opts.getDomain(),request.getQuery(),new AsyncCallback<LinkedList<Customer>>() {
						public void onSuccess(LinkedList<Customer> result) {
							LinkedList<Suggestion> suggestions = AonCollectionUtils.stream(result)
								.map( c -> new CustomerSuggestion( c, c.getDisplayName(),decorate(c, request.getQuery())))
								.collect( Collectors.toCollection(LinkedList::new)); 
							callback.onSuggestionsReady(request, new Response(suggestions));
						}
		
						public void onFailure(Throwable caught) {
							callback.onSuggestionsReady(request, new Response());
						}
					});
	        	}
	        };
	        searchTimer.schedule(300);				
		}

		private static String decorate(Customer customer, String query) {
			String text = customer.getDisplayName();
			int index = AonStringUtils.indexOfIgnoreCase(text, query);

			SafeHtmlBuilder builder = new SafeHtmlBuilder()
				.appendHtmlConstant("<span style=\"font-size: 0.7rem;\" class=\"" 
					+ AON.CSS.aonTabIcon()
					+ AonStringUtils.SPACE 
					+ AON.CSS.aonIconBullet() 
				+ "\">");
			if (index != -1) {
				int end = index + AonStringUtils.length(query);
				builder
					.appendEscaped(AonStringUtils.substring(text, 0, index))
					.appendHtmlConstant(BEGIN_STRONG)
					.appendEscaped(AonStringUtils.substring(text, index, end)).appendHtmlConstant(END_STRONG)
					.appendEscaped(AonStringUtils.substring(text, end));
			} else {
				builder.appendEscaped(text);
			}

			return builder
				.appendHtmlConstant("</span>")
				.toSafeHtml()
				.asString();
		}

	}

}
