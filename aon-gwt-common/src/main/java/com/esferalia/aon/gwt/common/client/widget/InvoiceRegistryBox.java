package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonModuleOptions;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSuggestionService;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSuggestionServiceAsync;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSuggestionServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistryType.InvoiceRegistryTypeVisitor;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

abstract class InvoiceRegistryBox extends ResizeComposite implements Focusable, HasSelectionHandlers<InvoiceRegistry>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	private static final AonSuggestionServiceAsync SERVICE;
	static {
		AonSuggestionServiceAsync serviceRaw = GWT.create(AonSuggestionService.class);
		SERVICE = new AonSuggestionServiceAsyncDecorator(serviceRaw);
	}
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 15;


	private InvoiceRegistry invoiceRegistry;
	
	private FlowPanel rooPanel; 
	protected SuggestBox suggestBox;
	protected TextBox suggestTextBox;
	
	private boolean required = true;
	
	private InvoiceRegistrySuggestionDisplay suggestionDisplay;
	
	InvoiceRegistryBox(AonModuleOptions<?> options) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			
			private Timer searchTimer;
			
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				String query = request.getQuery();
				suggestionDisplay.hideSuggestions();
		        int length = AonStringUtils.length(query);
		        if (length < MIN_CHARACTERS || length > MAX_CHARACTERS) return;

		        if (searchTimer != null && searchTimer.isRunning()) {
		            searchTimer.cancel();
		        }
		        searchTimer = new Timer() {
		        	@Override
		            public void run() {
						reset();
						SERVICE.getInvoiceRegistries(options.getOccam(), options.getDomain(), query
							,new AsyncCallback<LinkedList<InvoiceRegistry>>() {
		
								public void onFailure(Throwable caught) {
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<InvoiceRegistry> result) {
									LinkedList<Suggestion> suggestions = AonCollectionUtils.stream(result)
										.map( ir -> new InvoiceRegistrySuggestion(ir, getDisplayValue(ir) ,decorate(ir, request.getQuery())))
										.collect(Collectors.toCollection(LinkedList::new))											
									;
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
								}
							}
						);
		        	}
		        };
		        searchTimer.schedule(300);				
			}
		};
		suggestTextBox = new TextBox();
		suggestionDisplay =  new InvoiceRegistrySuggestionDisplay();
		suggestBox = new SuggestBox(oracle,suggestTextBox,suggestionDisplay);
		suggestTextBox.setStyleName(AON.AON_CSS.aonInputText());
		suggestTextBox.setVisibleLength(15);
		suggestTextBox.setMaxLength(15);
		
		suggestBox.addSelectionHandler(event -> {
			InvoiceRegistrySuggestion selected = (InvoiceRegistrySuggestion) event.getSelectedItem();
			select( selected.getInvoiceRegistry() );
		});
		suggestBox.addValueChangeHandler( event -> {
			if ( AonStringUtils.isBlank( suggestBox.getValue() )) {
				select( null );	
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.addStyleName(AON.AON_CSS.aonInline() );
		rooPanel.add(suggestBox);
		initWidget(rooPanel);
	}
	
	public void set(InvoiceRegistry invoiceRegistry) {
		suggestTextBox.setText( getDisplayValue( invoiceRegistry ) );
		select(invoiceRegistry);
	}

	private void select(InvoiceRegistry invoiceRegistry) {
		select(invoiceRegistry, true);
	}
	private void select(InvoiceRegistry invoiceRegistry, boolean fireEvents) {
		this.invoiceRegistry = invoiceRegistry;
		if (invoiceRegistry != null) {
			suggestTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		} else {
			if (isRequired()) {
				suggestTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			} else {
				suggestTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			}
		}
		if (fireEvents) {
			SelectionEvent.fire(InvoiceRegistryBox.this, invoiceRegistry );
		}
	}
	
	public InvoiceRegistry getValue() {
		return this.invoiceRegistry;
	}
	public void setValue(InvoiceRegistry invoiceRegistry) {
		setValue(invoiceRegistry,true);
	}
	public void setValue(InvoiceRegistry invoiceRegistry, boolean fireEvents) {
		this.invoiceRegistry = invoiceRegistry;
		suggestTextBox.setValue(getDisplayValue(invoiceRegistry),fireEvents);
		select(invoiceRegistry, false);
	}
	
	private void reset() {
		select(null, false);
	}

	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return suggestTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return suggestTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return suggestTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		suggestTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		suggestTextBox.selectAll();
		suggestTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		suggestTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<InvoiceRegistry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return suggestTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return suggestTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return suggestTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(InvoiceRegistry invoiceRegistry, String query) {
		String text = InvoiceRegistry.getFullDescription(invoiceRegistry);
		String icon = invoiceRegistry.getType().visit( new InvoiceRegistryTypeVisitor<String>() {
			@Override public String visitSupplier() { return AON.CSS.aonIconSupplier(); }
			@Override public String visitCreditor() { return AON.CSS.aonIconCreditor(); }
			@Override public String visitCustomer() { return AON.CSS.aonIconCustomer(); }
		});

		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span class=\"" 
			+ AON.CSS.aonTabIcon()
			+ AonStringUtils.SPACE
			+ icon 
			+ "\" >");
		if (i != -1) {
			bld.appendEscaped(AonStringUtils.substring(text, 0, i));
			bld.appendHtmlConstant(BEGIN_STRONG);
			bld.appendEscaped(AonStringUtils.substring(text, i, (i + AonStringUtils.length(query) )));
			bld.appendHtmlConstant(END_STRONG);
			bld.appendEscaped(AonStringUtils.substring(text, (i + AonStringUtils.length(query) )));
		} else {
			bld.appendEscaped(text);
		}
		bld.appendHtmlConstant("</span>");
		return bld.toSafeHtml().asString(); 
	}

	@Override
	public boolean isEnabled() {
		return suggestBox.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		suggestBox.setEnabled(enabled);
	}
	
	protected abstract String getDisplayValue(InvoiceRegistry invoiceRegistry);

}
   