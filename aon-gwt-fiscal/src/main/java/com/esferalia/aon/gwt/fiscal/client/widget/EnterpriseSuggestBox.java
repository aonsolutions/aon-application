package com.esferalia.aon.gwt.fiscal.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;

public class EnterpriseSuggestBox extends ResizeComposite implements
		HasValue<String>, HasSelectionHandlers<Suggestion> {

	private CommonServiceAsync commonService;
	

	Integer enterpriseId;
	Integer domainId;
	String domainName;
	
	SuggestBox document;
	TextBox name;
	
	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public TextBox getName() {
		return name;
	}
	public SuggestBox getDocument() {
		return document;
	}

	public EnterpriseSuggestBox() {
		AON.ensureInjected();

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		EnterpriseSuggestOracle oracle = new EnterpriseSuggestOracle();
		
		
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonNowrap());
		
		InlineLabel docLabel = new InlineLabel( AON.MSG.document());
		docLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		panel.add(docLabel);
		
		document = new SuggestBox(oracle);
		document.setLimit(20);
		document.setWidth("100px");
		document.setStyleName(AON.AON_CSS.aonInputText());
		document.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
			
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof EnterpriseSuggestion) {
					EnterpriseSuggestion es = ((EnterpriseSuggestion) suggestion);
					enterpriseId = es.getEnterprise().getId();
					domainId = es.getEnterprise().getDomain();
				}
				name.setValue(event.getSelectedItem().getDisplayString());
			}
		});
		panel.add(document);

		InlineLabel nameLabel = new InlineLabel( AON.MSG.enterpriseName());
		nameLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		panel.add(nameLabel);
		
		name = new TextBox();
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setVisibleLength(42);
		name.setMaxLength(40);
		panel.add(name);

		initWidget(panel);
	}

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			String query = '%' + request.getQuery() + '%';
			commonService.getParentEnterprises(getCurrentDomainName()
					,getCurrentDomain()
					,query
					,new AsyncCallback<LinkedList<Enterprise>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(LinkedList<Enterprise> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
							if (result != null) {
								for (final Enterprise enterprise : result) {
									suggestions.add(new EnterpriseSuggestion(enterprise));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}

	public class EnterpriseSuggestion implements Suggestion {
		private Enterprise enterprise;

		public EnterpriseSuggestion(Enterprise enterprise) {
			this.enterprise = enterprise;
		}

		public Enterprise getEnterprise() {
			return enterprise;
		}

		@Override
		public String getDisplayString() {
			return enterprise.getName();
		}

		@Override
		public String getReplacementString() {
			return enterprise.getDocument();
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return document.addValueChangeHandler(handler);
	}

	@Override
	public String getValue() {
		return document.getValue();
	}

	@Override
	public void setValue(String value) {
		document.setValue(value);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		document.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Suggestion> handler) {
		return document.addSelectionHandler(handler);
	}
	
	public void setValue(String document, String name, boolean fireEvents) {
		this.document.setValue(document, fireEvents);
		this.name.setValue(name);
	}
	public void setValue(String document, String name) {
		this.setValue(document,name,true);
	}

}
