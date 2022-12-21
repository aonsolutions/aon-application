package com.esferalia.aon.gwt.fiscal.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
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

public class AccountingInvoiceBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<AccountingInvoice>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private AccountEntryServiceAsync SERVICE;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox accountingInvoice;
	private TextBox accountingInvoiceTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private AccountingInvoiceSuggestionDisplay suggestionDisplay;
	
	private static class AccountingInvoiceSuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	private static class AccountingInvoiceSuggestion extends MultiWordSuggestion {
		
		private AccountingInvoice accountingInvoice;
		
		private AccountingInvoiceSuggestion(AccountingInvoice accountingInvoice, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.accountingInvoice = accountingInvoice;
		}
		
		public AccountingInvoice getAccountingInvoice() {
			return accountingInvoice;
		}
		
	}
	
	public AccountingInvoiceBox(final String domainName, final int domain,final String user) {
		this(domainName,domain,user,null,true);
	}
	public AccountingInvoiceBox(final String domainName, final int domain, final String user,final AonConfiguration config) {
		this(domainName,domain,user,config,true);
	}
	
	public AccountingInvoiceBox(final String domainName, final int domain, final String user,final AonConfiguration config, boolean showDescription) {
		AccountEntryServiceAsync serviceRaw = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(serviceRaw );
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					SERVICE.getPendingImportAccountingInvoices(domainName,domain,user,request.getQuery()
							,new AsyncCallback<LinkedList<AccountingInvoice>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.noData());
									descriptionLabel.addStyleName(AON.CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<AccountingInvoice> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										for (final AccountingInvoice accountingInvoice : result) {
											suggestions.add(new AccountingInvoiceSuggestion(
												accountingInvoice
											   ,accountingInvoice.getInvoice().getReferenceCode()
											   ,decorate(accountingInvoice, request.getQuery())));
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}

							});
				}
			}
		};
		accountingInvoiceTextBox = new TextBox();
		suggestionDisplay =  new AccountingInvoiceSuggestionDisplay();
		accountingInvoice = new SuggestBox(oracle,accountingInvoiceTextBox,suggestionDisplay);
		accountingInvoiceTextBox.setStyleName(AON.CSS.aonInputText());
		accountingInvoiceTextBox.setVisibleLength(15);
		accountingInvoiceTextBox.setMaxLength(15);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		accountingInvoice.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				AccountingInvoiceSuggestion selected = (AccountingInvoiceSuggestion) event.getSelectedItem();
				AccountingInvoiceBox.this.setValue( selected.getAccountingInvoice(), true );
			}
		});
		accountingInvoice.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ( AonStringUtils.isBlank( accountingInvoice.getValue() )) {
					AccountingInvoiceBox.this.setValue( (AccountingInvoice) null , true);	
				}
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.CSS.aonNowrap() );
		rooPanel.addStyleName(AON.CSS.aonInline() );
		rooPanel.add(accountingInvoice);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	public void set(AccountingInvoice accountingInvoice) {
		if (accountingInvoice != null && accountingInvoice.getInvoice() != null && accountingInvoice.getInvoice().getId() != null) {
			accountingInvoiceTextBox.setText(accountingInvoice.getInvoice().getReferenceCode());
		} else {
			accountingInvoiceTextBox.setText(null);
		}
		setValue(accountingInvoice,true);
	}
	
	public void setValue(AccountingInvoice accountingInvoice, boolean fireEvents) {
		if (accountingInvoice != null && accountingInvoice.getInvoice() != null && accountingInvoice.getInvoice().getId() != null) {
			id = accountingInvoice.getInvoice().getId();
			accountingInvoiceTextBox.removeStyleName(AON.CSS.aonInputError() );
			accountingInvoiceTextBox.setValue(accountingInvoice.getInvoice().getReferenceCode(),fireEvents);
			descriptionLabel.setText(getDescription(accountingInvoice));
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				accountingInvoiceTextBox.addStyleName(AON.CSS.aonInputError() );
			} else {
				accountingInvoiceTextBox.removeStyleName(AON.CSS.aonInputError() );
			}
			accountingInvoiceTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
		}
		if (fireEvents) {
			SelectionEvent.fire(AccountingInvoiceBox.this, accountingInvoice );
		}
	}
	
	public void setValue(AccountingInvoice accountingInvoice) {
		setValue(accountingInvoice,true);
	}

	private void reset() {
		accountingInvoiceTextBox.removeStyleName(AON.CSS.aonInputError() );
		id = null;
		description = null;
		descriptionLabel.setText(null);
		descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
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
		return accountingInvoice.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		accountingInvoice.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			accountingInvoiceTextBox.removeStyleName(AON.CSS.aonInputError() );	
		} else {
			accountingInvoiceTextBox.addStyleName(AON.CSS.aonInputError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		accountingInvoice.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return accountingInvoiceTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return accountingInvoiceTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return accountingInvoiceTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		accountingInvoiceTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		accountingInvoiceTextBox.selectAll();
		accountingInvoiceTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		accountingInvoiceTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return accountingInvoice.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return accountingInvoiceTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return accountingInvoiceTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return accountingInvoiceTextBox.addKeyPressHandler(handler);
	}
	
	private static String getDescription(AccountingInvoice accountingInvoice) {
		StringBuffer buf = new StringBuffer();
		buf.append( AON.DATE_FORMAT.format(accountingInvoice.getInvoice().getIssueDate()) );
		buf.append( AonStringUtils.SPACE );
		buf.append( AonStringUtils.HYPHEN );
		buf.append( AonStringUtils.SPACE );
		buf.append( AonStringUtils.OPEN_PARENTHESIS );
		buf.append( AON.ACCOUNT_FMT.format(accountingInvoice.getInvoice().getTotal()) );
		buf.append( AonStringUtils.EURO );
		buf.append( AonStringUtils.CLOSE_PARENTHESIS );
		buf.append( AonStringUtils.SPACE );
		buf.append( AonStringUtils.OPEN_BRACKET );
		buf.append( accountingInvoice.getInvoice().getReferenceCode() );
		buf.append( AonStringUtils.CLOSE_BRACKET );
		buf.append( AonStringUtils.SPACE );
		buf.append( AonStringUtils.HYPHEN );
		buf.append( AonStringUtils.SPACE );
		buf.append( AonStringUtils.abbreviate(accountingInvoice.getInvoice().getRegistryName(), 40) );
		buf.append( AonStringUtils.SPACE );
		return buf.toString();
	}

	private static String decorate(AccountingInvoice accountingInvoice, String query) {
		String text = getDescription(accountingInvoice);

		String icon = AON.CSS.aonIconLevelTop();
		if (accountingInvoice.isCanCeuMel()) {
			icon = AON.CSS.aonIconLevelThis();
		}
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span style=\"padding-left: 20px;\" class=\"" + icon + "\" >");
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
		return accountingInvoice.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		accountingInvoice.setEnabled(enabled);
	}
}
   