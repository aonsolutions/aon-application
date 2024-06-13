package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AccountingRegistryService;
import com.esferalia.aon.gwt.common.client.AccountingRegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.AccountingRegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryFullPanel.AonAccountingRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryPanel.AonAccountingRegistryPanelCallback;
import com.esferalia.aon.gwt.common.shared.HasDescription;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonAccountingRegistryBox extends ResizeComposite implements HasValue<String>, HasDescription, Focusable, HasSelectionHandlers<AccountingRegistry>, HasAllFocusHandlers ,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 14;

	private static final AccountingRegistryServiceAsync SERVICE;
	static {
		AccountingRegistryServiceAsync commonServiceRaw = GWT.create(AccountingRegistryService.class);
		SERVICE = new AccountingRegistryServiceAsyncDecorator(commonServiceRaw);
	}

	private AccountingRegistry accountingRegistry;
	
	private FlowPanel rooPanel; 
	private SuggestBox accountingRegistryBox;
	private TextBox accountingRegistryTextBox;
	private AonTableButton dataBisButton;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private AccountingRegistrySuggestionDisplay suggestionDisplay;
	
	private static class AccountingRegistrySuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	public AonAccountingRegistryBox(AonModuleOptions<?> options, boolean showDescription) {
		
		dataBisButton = new AonTableButton(AON.MSG.titular(),AON.CSS.aonIconAdd());
		
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					SERVICE.getAccountingRegistries(options.getDomainName(),options.getDomain(),options.getUser(),request.getQuery()
							,new AsyncCallback<LinkedList<AccountingRegistry>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.noData());
									descriptionLabel.addStyleName(AON.CSS.aonColorRed());
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
		accountingRegistryBox = new SuggestBox(oracle,accountingRegistryTextBox,suggestionDisplay);
		accountingRegistryTextBox.setStyleName(AON.CSS.aonInputText());
		accountingRegistryTextBox.setVisibleLength(9);
		accountingRegistryTextBox.setMaxLength(9);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		dataBisButton.getElement().setTabIndex(-1);
		
		accountingRegistryBox.addSelectionHandler(event -> {
			AccountingRegistrySuggestion selected = (AccountingRegistrySuggestion) event.getSelectedItem();
			select( selected.getAccountingRegistry() );
		});
		accountingRegistryBox.addKeyUpHandler( event -> {
			if ( isControlF3(event) || isPlusKeyAlone(event) ) {
				showDialog(options,null);
			}
		});
		accountingRegistryBox.addValueChangeHandler( event -> {
			if ( AonStringUtils.isBlank( accountingRegistryBox.getValue() )) {
				select( null );	
			}
		});
		
		dataBisButton.addClickHandler(event -> showNewDialog(options));

		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.CSS.aonNowrap() );
		rooPanel.addStyleName(AON.CSS.aonFlexBlockInline());
		rooPanel.addStyleName(AON.CSS.aonInline() );
		rooPanel.add(accountingRegistryBox);
		rooPanel.add(dataBisButton);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	private boolean isControlF3(KeyUpEvent event) {
		return (event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_F3);
	}
	private boolean isPlusKeyAlone(KeyUpEvent event) {
		return event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS
				&& AonStringUtils.PLUS.equals(accountingRegistryTextBox.getValue());
	}
	
	public void set(AccountingRegistry accountingRegistry) {
		accountingRegistryTextBox.setText(accountingRegistry.getAccountCode());
		select(accountingRegistry);
	}
	
	private void select(AccountingRegistry accountingRegistry) {
		if (accountingRegistry != null) {
			this.accountingRegistry = accountingRegistry;
			accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );
			descriptionLabel.setText(accountingRegistry.getName());
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
			dataBisButton.addStyleName(AON.CSS.aonIconEdit());
		} else {
			this.accountingRegistry = accountingRegistry;
			if (isRequired()) {
				accountingRegistryTextBox.addStyleName(AON.CSS.aonInputTextError() );
			} else {
				accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );
			}
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
			dataBisButton.addStyleName(AON.CSS.aonIconAdd());
			dataBisButton.removeStyleName(AON.CSS.aonIconEdit());
		}
		SelectionEvent.fire(AonAccountingRegistryBox.this, accountingRegistry );
	}
	
	public void setValue(AccountingRegistry accountingRegistry, boolean fireEvents) {
		if (accountingRegistry != null && accountingRegistry.getId() != null) {
			this.accountingRegistry = accountingRegistry;
			accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );
			accountingRegistryTextBox.setValue(accountingRegistry.getAccountCode(),fireEvents);
			descriptionLabel.setText(accountingRegistry.getName());
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
			dataBisButton.removeStyleName(AON.CSS.aonIconAdd());
			dataBisButton.addStyleName(AON.CSS.aonIconEdit());
		} else {
			this.accountingRegistry = accountingRegistry;
			if (isRequired()) {
				accountingRegistryTextBox.addStyleName(AON.CSS.aonInputTextError() );
			} else {
				accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );
			}
			accountingRegistryTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
			dataBisButton.addStyleName(AON.CSS.aonIconAdd());
			dataBisButton.removeStyleName(AON.CSS.aonIconEdit());
		}
	}
	
	public void setValue(AccountingRegistry accountingRegistry) {
		setValue(accountingRegistry,true);
	}

	private void reset() {
		accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );
		this.accountingRegistry = null;
		descriptionLabel.setText(null);
		descriptionLabel.removeStyleName(AON.CSS.aonColorRed());
		dataBisButton.addStyleName(AON.CSS.aonIconAdd());
		dataBisButton.removeStyleName(AON.CSS.aonIconEdit());
	}

	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public Integer getId() {
		return this.accountingRegistry==null?null:this.accountingRegistry.getId();
	}

	@Override
	public String getValue() {
		return accountingRegistryBox.getValue();
	}

	@Override
	public void setValue(String value) {
		accountingRegistryBox.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			accountingRegistryTextBox.removeStyleName(AON.CSS.aonInputTextError() );	
		} else {
			accountingRegistryTextBox.addStyleName(AON.CSS.aonInputTextError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		accountingRegistryBox.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return this.accountingRegistry==null?null:this.accountingRegistry.getName();	
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
		return accountingRegistryBox.addValueChangeHandler(handler);
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

		String icon = AON.CSS.aonIconCustomer();
		if (accountingRegistry.getType() == AccountingRegistryType.SUPPLIER) {
			icon = AON.CSS.aonIconSupplier();
		} else if (accountingRegistry.getType() == AccountingRegistryType.CREDITOR) {
			icon = AON.CSS.aonIconCreditor();
		}
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span style=\"white-space: pre;\" class=\""
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
		return accountingRegistryBox.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		accountingRegistryBox.setEnabled(enabled);
	}
	
	public void showDialog(AonModuleOptions<?> options, AccountingRegistry ar) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.titular());
		final AonAccountingRegistryPanel accountPanel = getAonAccountingRegistryPanel(options, ar, new AonAccountingRegistryPanelCallback() {
			
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
				AonAccountingRegistryBox.this.setFocus(b);
			}
		});		
		dialog.add( accountPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> accountPanel.setFocus(true));		
	}
	
	public AonAccountingRegistryPanel getAonAccountingRegistryPanel(AonModuleOptions<?> options, AccountingRegistry ar) {
		return getAonAccountingRegistryPanel(options, ar, new AonAccountingRegistryPanelCallback() {
					
					@Override
					public void onCancel() {
						setFocus(true);
					}
					
					@Override
					public void onAccept(AccountingRegistry registry) {
						setValue(registry,false);
						select(registry);
					}

					@Override
					public void setFocus(boolean b) {
						AonAccountingRegistryBox.this.setFocus(b);
					}
				});
	}
	
	public AonAccountingRegistryPanel getAonAccountingRegistryPanel(AonModuleOptions<?> options, AccountingRegistry ar, AonAccountingRegistryPanelCallback callback) {
		return new AonAccountingRegistryPanel( 
				options.getDomainName(), 
				options.getDomain(), 
				options.getUser(), 
				getId(),
				options.getConfiguration(), 
				ar, 
				callback);
	}

	public void showNewDialog(AonModuleOptions<?> options) {
		final AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(AON.MSG.titular());
		AccountingRegistryType type = AccountingRegistryType.CREDITOR;
		Integer id = null; 
		if (this.accountingRegistry != null) {
			id = this.accountingRegistry.getId();
			type = this.accountingRegistry.getType();
		}
		final AonAccountingRegistryFullPanel accountingRegistryFullPanel = new AonAccountingRegistryFullPanel(options, type, id
				, new AonAccountingRegistryFullPanelCallback() {

					@Override
					public void onAccept(AccountingRegistry registry) {
						dialog.hide();
						setValue(registry,false);
						select(registry);
					}

					@Override
					public void onCancel() {
						dialog.hide();
						setFocus(true);
					}

					@Override
					public void setFocus(boolean b) {
						AonAccountingRegistryBox.this.setFocus(b);
					}
			
		});		
		dialog.add( accountingRegistryFullPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> accountingRegistryFullPanel.setFocus(true));		
	}

}
   