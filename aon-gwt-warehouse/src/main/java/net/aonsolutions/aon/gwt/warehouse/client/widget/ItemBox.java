package net.aonsolutions.aon.gwt.warehouse.client.widget;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
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

public class ItemBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<JsItem>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {

	private API API;
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox item;
	private TextBox itemTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private ItemSuggestionDisplay suggestionDisplay;
	
	private static class ItemSuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	private static class ItemSuggestion extends MultiWordSuggestion {
		
		private JsItem item;
		
		private ItemSuggestion(JsItem item, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.item = item;
		}
		
		public JsItem getItem() {
			return item;
		}
		
	}
	 
	public ItemBox(API API) {
		this(null,-1);
		this.API = API;
	}
	
	public ItemBox(final String domainName, final int domain) {
		this(domainName,domain,null,true);
	}
	
	public ItemBox(final String domainName, final int domain, final AonConfiguration config, boolean showDescription) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
						&& AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					
					HashMap<String,LinkedList<String>> map = new HashMap<>();
					map.put("description", new LinkedList<>());
					map.get("description").add(request.getQuery());
					API.getProduct().getItemList(map, new AsyncCallback<JSON<JsItem>>() {
						
						@Override
						public void onSuccess(JSON<JsItem> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
							if (result != null) {
								result.getData().stream().forEach(jsItem -> 
								suggestions.add(new ItemSuggestion(
										jsItem
										,jsItem.getCode()
										,decorate(jsItem, request.getQuery())))
								);
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							descriptionLabel.setText(AON.MSG.noData());
							descriptionLabel.addStyleName(AON.AON_CSS.aonColorRed());
							callback.onSuggestionsReady(request, new Response());
						}
					});
					
				}
			}
		};
		itemTextBox = new TextBox();
		suggestionDisplay =  new ItemSuggestionDisplay();
		item = new SuggestBox(oracle,itemTextBox,suggestionDisplay);
		itemTextBox.setStyleName(AON.AON_CSS.aonInputText());
		itemTextBox.setVisibleLength(9);
		itemTextBox.setMaxLength(9);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		item.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				ItemSuggestion selected = (ItemSuggestion) event.getSelectedItem();
				select( selected.getItem() );
			}
		});
//		item.addKeyUpHandler( new KeyUpHandler() {
//			
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				if ( config != null &&	(isControlF3(event) || isPlusKeyAlone(event))) {
//					showDialog(domainName,domain,config);
//				}
//			}
//		});
		item.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ( AonStringUtils.isBlank( item.getValue() )) {
					select( null );	
				}
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.addStyleName(AON.AON_CSS.aonInline() );
		rooPanel.add(item);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	private boolean isControlF3(KeyUpEvent event) {
		return (event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_F3);
	}
	private boolean isPlusKeyAlone(KeyUpEvent event) {
		return event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS && AonStringUtils.PLUS.equals(itemTextBox.getValue());
	}
	
	public void set(JsItem item) {
		itemTextBox.setText(item.getCode());
		select(item);
	}
	
	private void select(JsItem item) {
		if (item != null) {
			itemTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			id = item.getId();
			descriptionLabel.setText(item.getName());
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				itemTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			} else {
				itemTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
		SelectionEvent.fire(ItemBox.this, item );
	}
	
	public void setValue(JsItem item, boolean fireEvents) {
		if (item != null && item.getId() != null) {
			id = item.getId();
			itemTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			itemTextBox.setValue(item.getCode(),fireEvents);
			description = item.getCode();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				itemTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			itemTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}
	
	public void setValue(JsItem item) {
		setValue(item,true);
	}

	private void reset() {
		itemTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
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
		return item.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		item.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			itemTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			itemTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		item.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return itemTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return itemTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return itemTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		itemTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		itemTextBox.selectAll();
		itemTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		itemTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return item.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsItem> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return itemTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return itemTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return itemTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(JsItem item, String query) {
//		String text = AccountingRegistry.getFullDescription(accountingRegistry);
		String text = item.getCode() + " - " + item.getName();

//		String icon = AON.AON_CSS.aonLetterCGreenIcon();
//		if (accountingRegistry.getType() == AccountingRegistryType.SUPPLIER) {
//			icon = AON.AON_CSS.aonLetterPBlueIcon();
//		} else if (accountingRegistry.getType() == AccountingRegistryType.CREDITOR) {
//			icon = AON.AON_CSS.aonLetterAOrangeIcon();
//		}
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span class=\"" 
//				+ icon 
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
		return item.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		item.setEnabled(enabled);
	}
	
	// TODO showDialog
	private void showDialog(final String domainName, final int domain, final AonConfiguration config) {
		final CustomDialog dialog = new CustomDialog();
		dialog.setCaption(AON.MSG.titular());
//		final AccountingRegistryPanel accountPanel = new AccountingRegistryPanel( domainName, domain
//				, id
//				,config, new AccountingRegistryPanelCallback() {
//			
//			@Override
//			public void onCancel() {
//				dialog.hide();
//				setFocus(true);
//			}
//			
//			@Override
//			public void onAccept(AccountingRegistry registry) {
//				dialog.hide();
//				setValue(registry,false);
//				select(registry);
//			}
//
//			@Override
//			public void setFocus(boolean b) {
//				ItemBox.this.setFocus(b);
//			}
//		});
		
//		dialog.add( accountPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
//	        	accountPanel.setFocus(true);
	        }
	    });		
	}
	
}
