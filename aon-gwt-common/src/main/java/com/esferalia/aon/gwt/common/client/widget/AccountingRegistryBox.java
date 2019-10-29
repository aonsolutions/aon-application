package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryPanel.AccountingRegistryPanelCallback;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
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

public class AccountingRegistryBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<AccountingRegistry>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private CommonServiceAsync commonService;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox accountingRegistry;
	private TextBox accountingRegistryTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private AccountingRegistrySuggestionDisplay suggestionDisplay;
	
	private static class AccountingRegistrySuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	private static class AccountingRegistrySuggestion extends MultiWordSuggestion {
		
		private AccountingRegistry accountingRegistry;
		
		private AccountingRegistrySuggestion(AccountingRegistry accountingRegistry, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.accountingRegistry = accountingRegistry;
		}
		
		public AccountingRegistry getAccountingRegistry() {
			return accountingRegistry;
		}
		
	}
	
	public AccountingRegistryBox(final String domainName, final int domain) {
		this(domainName,domain,null,true);
	}
	
	public AccountingRegistryBox(final String domainName, final int domain, final AonConfiguration config, boolean showDescription) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					commonService.getAccountingRegistries(domainName,domain,request.getQuery()
							,new AsyncCallback<LinkedList<AccountingRegistry>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.noData());
									descriptionLabel.addStyleName(AON.AON_CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<AccountingRegistry> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										for (final AccountingRegistry accountingRegistry : result) {
											suggestions.add(new AccountingRegistrySuggestion(
												accountingRegistry
											   ,accountingRegistry.getAccountCode()
											   ,decorate(accountingRegistry, request.getQuery())));
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}
							});
				}
			}
		};
		accountingRegistryTextBox = new TextBox();
		suggestionDisplay =  new AccountingRegistrySuggestionDisplay();
		accountingRegistry = new SuggestBox(oracle,accountingRegistryTextBox,suggestionDisplay);
		accountingRegistryTextBox.setStyleName(AON.AON_CSS.aonInputText());
		accountingRegistryTextBox.setVisibleLength(9);
		accountingRegistryTextBox.setMaxLength(9);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		accountingRegistry.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				AccountingRegistrySuggestion selected = (AccountingRegistrySuggestion) event.getSelectedItem();
				select( selected.getAccountingRegistry() );
			}
		});
		accountingRegistry.addKeyUpHandler( new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( config != null &&	(isControlF3(event) || isPlusKeyAlone(event))) {
					showDialog(domainName,domain,config);
				}
			}
		});
		accountingRegistry.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ( AonStringUtils.isBlank( accountingRegistry.getValue() )) {
					select( null );	
				}
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.addStyleName(AON.AON_CSS.aonInline() );
		rooPanel.add(accountingRegistry);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	private boolean isControlF3(KeyUpEvent event) {
		return (
			(event.isControlKeyDown() || event.isControlKeyDown())
			&& event.getNativeKeyCode() == KeyCodes.KEY_F3);
	}
	private boolean isPlusKeyAlone(KeyUpEvent event) {
		return (event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS 
			|| event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS)
				&& AonStringUtils.PLUS.equals(accountingRegistryTextBox.getValue());
	}
	
	public void set(AccountingRegistry accountingRegistry) {
		accountingRegistryTextBox.setText(accountingRegistry.getAccountCode());
		select(accountingRegistry);
	}
	
	private void select(AccountingRegistry accountingRegistry) {
		if (accountingRegistry != null) {
			accountingRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			id = accountingRegistry.getId();
			descriptionLabel.setText(accountingRegistry.getName());
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				accountingRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			} else {
				accountingRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
		SelectionEvent.fire(AccountingRegistryBox.this, accountingRegistry );
	}
	
	public void setValue(AccountingRegistry accountingRegistry, boolean fireEvents) {
		if (accountingRegistry != null && accountingRegistry.getId() != null) {
			id = accountingRegistry.getId();
			accountingRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			accountingRegistryTextBox.setValue(accountingRegistry.getAccountCode(),fireEvents);
			description = accountingRegistry.getName();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				accountingRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			accountingRegistryTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}
	
	public void setValue(AccountingRegistry accountingRegistry) {
		setValue(accountingRegistry,true);
	}

	private void reset() {
		accountingRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
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
		return accountingRegistry.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		accountingRegistry.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			accountingRegistryTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			accountingRegistryTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		accountingRegistry.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return accountingRegistryTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return accountingRegistryTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return accountingRegistryTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		accountingRegistryTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		accountingRegistryTextBox.selectAll();
		accountingRegistryTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		accountingRegistryTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return accountingRegistry.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingRegistry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return accountingRegistryTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return accountingRegistryTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return accountingRegistryTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(AccountingRegistry accountingRegistry, String query) {
		String text = AccountingRegistry.getFullDescription(accountingRegistry);

		String icon = AON.AON_CSS.aonLetterCGreenIcon();
		if (accountingRegistry.getType() == AccountingRegistryType.SUPPLIER) {
			icon = AON.AON_CSS.aonLetterPBlueIcon();
		} else if (accountingRegistry.getType() == AccountingRegistryType.CREDITOR) {
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
		return accountingRegistry.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		accountingRegistry.setEnabled(enabled);
	}
	
	public void showDialog(final String domainName, final int domain, final AonConfiguration config) {
		final CustomDialog dialog = new CustomDialog();
		dialog.setCaption(AON.MSG.titular());
		final AccountingRegistryPanel accountPanel = new AccountingRegistryPanel( domainName, domain
				, id
				,config, new AccountingRegistryPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
				setFocus(true);
			}
			
			@Override
			public void onAccept(AccountingRegistry registry) {
				dialog.hide();
				setValue(registry,false);
				select(registry);
			}

			@Override
			public void setFocus(boolean b) {
				AccountingRegistryBox.this.setFocus(b);
			}
		});
		
		dialog.add( accountPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	accountPanel.setFocus(true);
	        }
	    });		
	}
}
   