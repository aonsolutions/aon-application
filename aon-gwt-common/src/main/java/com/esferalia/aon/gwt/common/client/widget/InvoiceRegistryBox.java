package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceRegistryBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<InvoiceRegistry>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private CommonServiceAsync commonService;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox invoiceRegistry;
	private TextBox invoiceRegistryTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private InvoiceRegistrySuggestionDisplay suggestionDisplay;
	
	private static class InvoiceRegistrySuggestionDisplay extends DefaultSuggestionDisplay {
	    private Widget suggestionMenu;

	    @Override
	    protected Widget decorateSuggestionList(Widget suggestionList) {
	        suggestionMenu = suggestionList;
	        return suggestionList;
	    }
	    
	    @Override
		protected void moveSelectionDown() {
			super.moveSelectionDown();
			scrollSelectedItemIntoView();
		}

		@Override
		protected void moveSelectionUp() {
			super.moveSelectionUp();
			scrollSelectedItemIntoView();
		}
		
	    private void scrollSelectedItemIntoView() {
	        NodeList<Node> trList = suggestionMenu.getElement().getChild(1).getChild(0).getChildNodes();
	        for (int trIndex = 0; trIndex < trList.getLength(); ++trIndex) {
	            Element trElement = (Element)trList.getItem(trIndex);
	            if (((Element)trElement.getChild(0)).getClassName().contains("selected")) {
	                trElement.scrollIntoView();
	                break;
	            }
	        }
	    }
	}
	
	private static class InvoiceRegistrySuggestion extends MultiWordSuggestion {
		
		private InvoiceRegistry invoiceRegistry;
		
		private InvoiceRegistrySuggestion(InvoiceRegistry invoiceRegistry, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.invoiceRegistry = invoiceRegistry;
		}
		
		public InvoiceRegistry getInvoiceRegistry() {
			return invoiceRegistry;
		}
		
	}
	
	public InvoiceRegistryBox(final String domainName, final int domain,final String user) {
		this(domainName,domain,user, null,true);
	}
	
	public InvoiceRegistryBox(final String domainName, final int domain, final String user,final AonConfiguration config, boolean showDescription) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					commonService.getInvoiceRegistries(domainName,domain,user, request.getQuery()
							,new AsyncCallback<LinkedList<InvoiceRegistry>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.noData());
									descriptionLabel.addStyleName(AON.AON_CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<InvoiceRegistry> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										for (final InvoiceRegistry invoiceRegistry : result) {
											suggestions.add(new InvoiceRegistrySuggestion(
													invoiceRegistry
											   ,invoiceRegistry.getDocument()
											   ,decorate(invoiceRegistry, request.getQuery())));
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}
							});
				}
			}
		};
		invoiceRegistryTextBox = new TextBox();
		suggestionDisplay =  new InvoiceRegistrySuggestionDisplay();
		invoiceRegistry = new SuggestBox(oracle,invoiceRegistryTextBox,suggestionDisplay);
		invoiceRegistryTextBox.setStyleName(AON.AON_CSS.aonInputText());
		invoiceRegistryTextBox.setVisibleLength(9);
		invoiceRegistryTextBox.setMaxLength(12);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		invoiceRegistry.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				InvoiceRegistrySuggestion selected = (InvoiceRegistrySuggestion) event.getSelectedItem();
				select( selected.getInvoiceRegistry() );
			}
		});
		invoiceRegistry.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ( AonStringUtils.isBlank( invoiceRegistry.getValue() )) {
					select( null );	
				}
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.addStyleName(AON.AON_CSS.aonInline() );
		rooPanel.add(invoiceRegistry);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	public void set(InvoiceRegistry invoiceRegistry) {
		invoiceRegistryTextBox.setText(invoiceRegistry.getDocument());
		select(invoiceRegistry);
	}
	
	private void select(InvoiceRegistry invoiceRegistry) {
		if (invoiceRegistry != null) {
			invoiceRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			id = invoiceRegistry.getId();
			descriptionLabel.setText(invoiceRegistry.getName());
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				invoiceRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			} else {
				invoiceRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
		SelectionEvent.fire(InvoiceRegistryBox.this, invoiceRegistry );
	}
	
	public void setValue(InvoiceRegistry invoiceRegistry, boolean fireEvents) {
		if (invoiceRegistry != null && invoiceRegistry.getId() != null) {
			id = invoiceRegistry.getId();
			invoiceRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			invoiceRegistryTextBox.setValue(invoiceRegistry.getDocument(),fireEvents);
			description = invoiceRegistry.getName();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				invoiceRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			invoiceRegistryTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}
	
	public void setValue(InvoiceRegistry invoiceRegistry) {
		setValue(invoiceRegistry,true);
	}

	private void reset() {
		invoiceRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		id = null;
		description = null;
		descriptionLabel.setText(null);
		descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
	}

	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String getValue() {
		return invoiceRegistry.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		invoiceRegistry.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			invoiceRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			invoiceRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		invoiceRegistry.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return invoiceRegistryTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return invoiceRegistryTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return invoiceRegistryTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		invoiceRegistryTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		invoiceRegistryTextBox.selectAll();
		invoiceRegistryTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		invoiceRegistryTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return invoiceRegistry.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<InvoiceRegistry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return invoiceRegistryTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return invoiceRegistryTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return invoiceRegistryTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(InvoiceRegistry invoiceRegistry, String query) {
		String text = InvoiceRegistry.getFullDescription(invoiceRegistry);

		String icon = AON.AON_CSS.aonLetterCGreenIcon();
		if (invoiceRegistry.getType() == AccountingRegistryType.SUPPLIER) {
			icon = AON.AON_CSS.aonLetterPBlueIcon();
		} else if (invoiceRegistry.getType() == AccountingRegistryType.CREDITOR) {
			icon = AON.AON_CSS.aonLetterAOrangeIcon();
		}
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span class=\"" 
				+ icon 
				+ AonStringUtils.SPACE
				+ AON.AON_CSS.aonPaddingLeft20()
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
		return invoiceRegistry.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		invoiceRegistry.setEnabled(enabled);
	}
	
}
   