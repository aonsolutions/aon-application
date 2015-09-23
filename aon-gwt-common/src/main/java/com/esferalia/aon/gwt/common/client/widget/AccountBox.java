package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AccountBox extends ResizeComposite implements HasValue<String>, HasSelectionHandlers<Suggestion>{
	
	private static final int MIN_CHARACTERS = 3;
	private static final String BEGIN_STRONG = "<strong>";
	private static final String END_STRONG = "</strong>";

	
	private CommonServiceAsync commonService;

	private String domainName;
	private int domain;

	private Integer id;

	private SuggestBox account;
	private TextBox accountTextBox;
	private InlineLabel description;
	private AccountSuggestionDisplay suggestionDisplay;
	
	private static class AccountSuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	public AccountBox(String domainName, int domain) {
		this.domainName = domainName;
		this.domain = domain;
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		AccountSuggestOracle oracle = new AccountSuggestOracle();
		accountTextBox = new TextBox();
		suggestionDisplay =  new AccountSuggestionDisplay();
		account = new SuggestBox(oracle,accountTextBox,suggestionDisplay);
		accountTextBox.setStyleName(AON.AON_CSS.aonInputText());
		accountTextBox.setVisibleLength(9);
		accountTextBox.setMaxLength(9);
		description = new InlineLabel();
		description.addStyleName(AON.AON_CSS.aonMarginLeft() );
				
		accountTextBox.addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String newValue = autoComplete(event.getValue());
				if (AonValidationUtil.isValidAccount(newValue)) {
					accountTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
					commonService.getAccount(AccountBox.this.domainName,AccountBox.this.domain,newValue
							,new AsyncCallback<Account>() {

						@Override
						public void onSuccess(Account result) {
							AccountSuggestion as = new AccountSuggestion(result, result.getCode(),result.getFullName());
							if (Window.confirm("Lanzando evento")) {
								SelectionEvent.fire(account, as );		
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO manage exception.
							Window.alert("Cuenta Contable no encontrada");
						}
					});
				} else {
					accountTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
				}
			}

		});
		
		addSelectionHandler(new SelectionHandler<Suggestion>() {
			
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof AccountSuggestion) {
					AccountSuggestion as = ((AccountSuggestion) suggestion);
					id = as.getAccount().getId();
					description.setText(as.getAccount().getDescription());
					accountTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
				}
				
			}
		});
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.AON_CSS.aonNowrap() );
		panel.add(account);
		panel.add(description);
		initWidget(panel);
	}
	
	public Integer getId() {
		return id;
	}
	
	private String autoComplete(String value) {
		if (AonStringUtils.isNotBlank(value) && AonStringUtils.contains(value,AonStringUtils.DOT)) {
			String b = AonStringUtils.trimToEmpty( AonStringUtils.substringBefore(value, AonStringUtils.DOT));
			String a = AonStringUtils.trimToEmpty(  AonStringUtils.substringAfter(value, AonStringUtils.DOT));
			String c = AonStringUtils.rightPad(b, (9 - AonStringUtils.length(a)), AonStringUtils.ZERO) + a;
			account.setValue(c,false);
			suggestionDisplay.hideSuggestions();
			return c;
		}
		return value;
	}

	class AccountSuggestOracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(final Request request,final Callback callback) {
			suggestionDisplay.hideSuggestions();
			id = null;
			description.setText(null);
			if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS) {
				commonService.getAccounts(AccountBox.this.domainName,AccountBox.this.domain,request.getQuery()
						,new AsyncCallback<LinkedList<Account>>() {
	
							public void onFailure(Throwable caught) {
								Window.alert("Error while getting suggestions.");
								callback.onSuggestionsReady(request, new Response());
							}
	
							public void onSuccess(LinkedList<Account> result) {
								LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
								if (result != null) {
									
									for (final Account account : result) {
										SafeHtmlBuilder bld = new SafeHtmlBuilder();
										String ds = account.getFullName();
										int i = AonStringUtils.indexOfIgnoreCase(ds, request.getQuery());
										bld.appendHtmlConstant("<span class=\"" 
												+ ((account.getDomain() != domain)
													?AON.AON_CSS.aonIconPointOrange()
													:AON.AON_CSS.aonIconPointLightGreen() )
												+ AonStringUtils.SPACE
												+ AON.AON_CSS.aonIconPaddingLeft()
												+ "\" >");
										bld.appendEscaped(AonStringUtils.substring(ds, 0, i));
										bld.appendHtmlConstant(BEGIN_STRONG);
										bld.appendEscaped(AonStringUtils.substring(ds, i, (i + AonStringUtils.length(request.getQuery()) )));
								        bld.appendHtmlConstant(END_STRONG);
								        bld.appendEscaped(AonStringUtils.substring(ds, (i + AonStringUtils.length(request.getQuery()) )));
								        bld.appendHtmlConstant("</span>");
										AccountSuggestion as = new AccountSuggestion(account,account.getCode(), bld.toSafeHtml().asString());
										suggestions.add(as);
									}
								}
								Response resp = new Response(suggestions);
								callback.onSuggestionsReady(request, resp);
								
							}
						});
			}
		}
	}
	
	public class AccountSuggestion extends MultiWordSuggestion {
		private Account account;

		public AccountSuggestion(Account account, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.account = account;
		}
//		public AccountSuggestion(Account account) {
//			this(account, account.getCode(), account.getFullName() );
//			
//		}
		public Account getAccount() {
			return account;
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return account.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return account.addSelectionHandler(handler);
	}

	@Override
	public String getValue() {
		return account.getValue();
	}

	@Override
	public void setValue(String value) {
		account.setValue(value);
		if (value == null || value.length() == 0 || AonValidationUtil.isValidAccount(value)) {
			accountTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			accountTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		account.setValue(value,fireEvents);
	}
	
}
