package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountPanel.AonAccountPanelCallback;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonAccountBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<Account>, HasAllFocusHandlers
	,HasAllKeyHandlers {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 8;
	protected static final int KEY_PLUS = 171;

	private CommonServiceAsync commonService;

	private Integer id;
	private String description;
	
	private FlowPanel rootPanel; 
	private SuggestBox account;
	private TextBox accountTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	private String domainName;
	private int domain;
	private String user;
	
	
	private AccountSuggestionDisplay suggestionDisplay;
	
	private static class AccountSuggestionDisplay extends DefaultSuggestionDisplay {
	    private Widget suggestionMenu;

	    @Override
	    protected PopupPanel createPopup() {
	    	PopupPanel popupPanel = super.createPopup();
	    	popupPanel.setStyleName(AON.CSS.aonSuggestBoxPopup());
	    	return popupPanel;
	    }

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
	                trElement.setScrollLeft(0);
	                getPopupPanel().getElement().setScrollLeft(0);
	                break;
	            }
	        }
	    }
	}
	
	private static class AccountSuggestion extends MultiWordSuggestion {
		
		private Account account;
		
		private AccountSuggestion(Account account, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.account = account;
		}
		
		public Account getAccount() {
			return account;
		}
		
	}
	
	public AonAccountBox(final String domainName, final int domain,final String user) {
		this(domainName,domain,user,true);
	}
	
	public AonAccountBox(final String domainName, final int domain, final String user,boolean showDescription) {
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
			
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					commonService.getAccounts(domainName,domain,user,request.getQuery()
							,new AsyncCallback<LinkedList<Account>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.accountNotFound());
									descriptionLabel.addStyleName(AON.CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<Account> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										
										for (final Account account : result) {
											SafeHtmlBuilder bld = new SafeHtmlBuilder();
											String ds = account.getFullName();
											int i = AonStringUtils.indexOfIgnoreCase(ds, request.getQuery());
											bld.appendHtmlConstant("<span style=\"white-space: pre;\" class=\""
													+ ((account.getDomain() != domain)
														?AON.CSS.aonIconLevelTop()
														:AON.CSS.aonIconLevelThis() )
													+ AonStringUtils.SPACE
													+ AON.CSS.aonTabIcon()
													+ "\" >");
											bld.appendEscaped(AonStringUtils.substring(ds, 0, i));
											bld.appendHtmlConstant(BEGIN_STRONG);
											bld.appendEscaped(AonStringUtils.substring(ds, i, (i + AonStringUtils.length(request.getQuery()) )));
									        bld.appendHtmlConstant(END_STRONG);
									        bld.appendEscaped(AonStringUtils.substring(ds, (i + AonStringUtils.length(request.getQuery()) )));
									        bld.appendHtmlConstant("</span>");
									        AccountSuggestion as = new AccountSuggestion(account, account.getCode(), bld.toSafeHtml().asString());
											suggestions.add(as);
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}
							});
				}
			}
		};
		accountTextBox = new TextBox();
		suggestionDisplay =  new AccountSuggestionDisplay();
		account = new SuggestBox(oracle,accountTextBox,suggestionDisplay);
		accountTextBox.setStyleName(AON.CSS.aonInputText());
		accountTextBox.setVisibleLength(9);
		accountTextBox.setMaxLength(9);
		
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.CSS.aonFontSmall());
		descriptionLabel.setVisible(showDescription);
		
		account.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				AccountSuggestion selected = (AccountSuggestion) event.getSelectedItem();
				select( selected.getAccount() );
			}
		});
		
		accountTextBox.addBlurHandler( new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				autoComplete(account.getValue());
			}
		});
			
		accountTextBox.addKeyUpHandler( new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_F3)
						|| event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS
						|| (!event.isShiftKeyDown() && event.getNativeKeyCode() == KEY_PLUS)) {
					showAccountDialog();
				}
			}
		});
		
		rootPanel = new FlowPanel();
		rootPanel.addStyleName(AON.CSS.aonNowrap() );
		rootPanel.addStyleName(AON.CSS.aonInline() );
		rootPanel.add(account);
		rootPanel.add(descriptionLabel);
		initWidget(rootPanel);
	}
	
	private void autoComplete(String value) {
		if (AonStringUtils.isNotBlank(value) && AonStringUtils.contains(value,AonStringUtils.DOT)) {
			String b = AonStringUtils.trimToEmpty( AonStringUtils.substringBefore(value, AonStringUtils.DOT));
			String a = AonStringUtils.trimToEmpty(  AonStringUtils.substringAfter(value, AonStringUtils.DOT));
			String c = AonStringUtils.rightPad(b, (9 - AonStringUtils.length(a)), AonStringUtils.ZERO) + a;
			suggestionDisplay.hideSuggestions();
			account.setValue(c,false);
			select(c);
		} else {
//			if (id == null && !((DefaultSuggestionDisplay) account.getSuggestionDisplay()).isSuggestionListShowing()) {
				select(value);
//			}
		}
	}
	private void select(String accountCode) {
		if (AonValidationUtil.isValidAccount(accountCode,isRequired())) {
			if (!isRequired() && AonStringUtils.isEmpty(accountCode)) {
				// No es obligatorio y lo han dejado vacio, por lo que 
				// hay que borrar lo que haya de antes.
				reset();
				SelectionEvent.fire(AonAccountBox.this, null );
			} else {
				accountTextBox.removeStyleName(AON.CSS.aonInputTextError() );
				commonService.getAccount(AonAccountBox.this.domainName,AonAccountBox.this.domain,AonAccountBox.this.user
						,accountCode,new AsyncCallback<Account>() {
					@Override
					public void onSuccess(Account result) {
						if (result != null) {
							select( result );
						} else {
							reset();
							SelectionEvent.fire(AonAccountBox.this, null );
							accountTextBox.addStyleName(AON.CSS.aonInputTextError() );
							descriptionLabel.setText(AON.MSG.accountNotFound());
							descriptionLabel.addStyleName(AON.CSS.aonColorRed());
							AonAccountBox.this.setFocus(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						accountTextBox.addStyleName(AON.CSS.aonInputTextError() );
						descriptionLabel.setText(AON.MSG.accountNotFound());
						descriptionLabel.addStyleName(AON.CSS.aonColorRed());
					}
				});

				
			}
		} else {
			reset();
			accountTextBox.addStyleName(AON.CSS.aonInputTextError() );
			SelectionEvent.fire(AonAccountBox.this, null );
		}
		
	}

	private void select(Account result) {
		accountTextBox.removeStyleName(AON.CSS.aonInputTextError());
		id = result.getId();
		description = result.getDescription();
		descriptionLabel.setText(description);
		descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
		SelectionEvent.fire(AonAccountBox.this, result );
	}
	
	private void reset() {
		accountTextBox.removeStyleName(AON.CSS.aonInputTextError() );
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
		return account.getValue();
	}
	public void setAccount(Account account) {
		setAccount(account, false);
	}
	public void setAccount(Account account,boolean fire) {
		if (account == null ) {
			setValue(null,null,null,fire);	
		} else {
			setValue(account.getId(),account.getCode(),account.getDescription(),fire);	
		}
	}
	public void setValue(Integer id, String code,String description) {
		setValue(id,code,description,false);
	}
	public void setValue(Integer id, String code,String description, boolean fire) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,fire);
	}

	@Override
	public void setValue(String value) {
		account.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidAccount(value,isRequired())) {
			accountTextBox.removeStyleName(AON.CSS.aonInputTextError() );	
		} else {
			accountTextBox.addStyleName(AON.CSS.aonInputTextError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		account.setValue(value,fireEvents);
	}

	public String getCode() {
		return getValue();	
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return accountTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return accountTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return accountTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		accountTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		accountTextBox.selectAll();
		accountTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		accountTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return account.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return accountTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return accountTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return accountTextBox.addKeyPressHandler(handler);
	}
	public void setEnabled(boolean enabled) {
		accountTextBox.setEnabled(enabled);
	}
	
	private void showAccountDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.account());
		final AonAccountPanel accountPanel = new AonAccountPanel( domainName, domain, user, getId(), new AonAccountPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
				accountTextBox.setValue(AonStringUtils.remove(accountTextBox.getValue(), AonStringUtils.PLUS),false);
				accountTextBox.selectAll();
				accountTextBox.setFocus(true);
			}
			
			@Override
			public void onAccept(Account result) {
				dialog.hide();
				accountTextBox.setValue(result.getCode(),false);
				select(result);
				accountTextBox.selectAll();
				accountTextBox.setFocus(true);
			}
		});
		
		dialog.add( accountPanel );
		dialog.center();
		dialog.show();
	}

}
