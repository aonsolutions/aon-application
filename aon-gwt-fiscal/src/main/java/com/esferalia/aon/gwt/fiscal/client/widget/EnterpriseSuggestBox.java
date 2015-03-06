package com.esferalia.aon.gwt.fiscal.client.widget;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseSuggestBox extends ResizeComposite implements
		HasValue<String>, HasSelectionHandlers<Suggestion> {

	private final AonResources aonResources = GWT.create(AonResources.class);

	interface EnterpriseSuggestBoxBinder extends
			UiBinder<Widget, EnterpriseSuggestBox> {
	}

	private static final EnterpriseSuggestBoxBinder enterpriseBinder = GWT
			.create(EnterpriseSuggestBoxBinder.class);

	private FiscalServiceAsync fiscalService;
	private final CommonMessages msg = GWT.create(CommonMessages.class);

	Integer enterpriseId;
	Integer domainId;
	
	@UiField(provided = true)
	SuggestBox document;
	
	@UiField
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
		aonResources.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		EnterpriseSuggestOracle oracle = new EnterpriseSuggestOracle();
		document = new SuggestBox(oracle);
		document.setLimit(20);
		Widget ui = enterpriseBinder.createAndBindUi(this);
		initWidget(ui);
	}

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {

			fiscalService.getEnterprises(getCurrentDomain(),
					request.getQuery(),
					new AsyncCallback<ArrayList<Enterprise>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(ArrayList<Enterprise> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
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

	@UiHandler("document")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		Suggestion suggestion = event.getSelectedItem();
		if (suggestion instanceof EnterpriseSuggestion) {
			EnterpriseSuggestion es = ((EnterpriseSuggestion) suggestion);
			enterpriseId = es.getEnterprise().getId();
			domainId = es.getEnterprise().getDomain();
		}
		name.setValue(event.getSelectedItem().getDisplayString());
	}
}
