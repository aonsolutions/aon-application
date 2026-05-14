package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
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

public class AonInvoicingGroupSuggestBox extends AonSuggestBox implements HasSelectionHandlers<InvoicingGroup>{

	private InvoicingGroup invoicingGroup;
	
	public <T> AonInvoicingGroupSuggestBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.invoicingGroup());
	}
	
	public AonInvoicingGroupSuggestBox(AonModuleOptions<?> opts, String title) {
		super(title, new AonInvoicingGroupSuggestOracle(opts) );
		this.setAutoSelectEnabled(false);
		this.setPlaceHolder(AON.MSG.description());
		getSuggestBox().addSelectionHandler(e -> {
			InvoicingGroupSuggestion selected = (InvoicingGroupSuggestion) e.getSelectedItem();
			setInvoicingGroup( selected.getInvoicingGroup() );	
		});
		getSuggestBox().addSelectionHandler(event -> {
			InvoicingGroupSuggestion selected = (InvoicingGroupSuggestion) event.getSelectedItem();
			setInvoicingGroup( selected.getInvoicingGroup() );
		});
		
		getSuggestBox().addValueChangeHandler( e -> {
			if ( AonStringUtils.isBlank( getSuggestBox().getValue() )) {
				setInvoicingGroup( null );	
			}
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<InvoicingGroup> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<InvoicingGroup> getInvoicingGroup() {
		return Optional.ofNullable(invoicingGroup);
	}
	private void setInvoicingGroup(InvoicingGroup invoicingGroup) {
		setInvoicingGroup(invoicingGroup, true);
	}
	private void setInvoicingGroup(InvoicingGroup invoicingGroup, boolean fireEvent) {
		this.invoicingGroup = invoicingGroup;
		if (fireEvent) SelectionEvent.fire(AonInvoicingGroupSuggestBox.this, this.invoicingGroup );
	}
	
	private static class InvoicingGroupSuggestion extends MultiWordSuggestion {
		
		private InvoicingGroup invoicingGroup;
		
		private InvoicingGroupSuggestion(InvoicingGroup invoicingGroup, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.invoicingGroup = invoicingGroup;
		}
		
		public InvoicingGroup getInvoicingGroup() {
			return invoicingGroup;
		}
	}
	
	private static class AonInvoicingGroupSuggestOracle extends MultiWordSuggestOracle {
		private Timer searchTimer;
		private AonModuleOptions<?> opts;
		private AonInvoicingGroupSuggestOracle(AonModuleOptions<?> opts) {
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
					SERVICE.getInvoicingGroups(opts.getOccam(),opts.getDomain(),request.getQuery(),new AsyncCallback<LinkedList<InvoicingGroup>>() {
						@Override
						public void onSuccess(LinkedList<InvoicingGroup> result) {
							LinkedList<Suggestion> suggestions = AonCollectionUtils.stream(result)
								.map( ig -> new InvoicingGroupSuggestion(ig,ig.getDescription(),decorate(ig, request.getQuery())))
								.collect( Collectors.toCollection(LinkedList::new)); 
							callback.onSuggestionsReady(request, new Response(suggestions));
						}
		
						@Override
						public void onFailure(Throwable caught) {
							callback.onSuggestionsReady(request, new Response());
						}
					});
	        	}
	        };
	        searchTimer.schedule(300);				
		}

		private static String decorate(InvoicingGroup group, String query) {
			String text = group.getDescription();
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
