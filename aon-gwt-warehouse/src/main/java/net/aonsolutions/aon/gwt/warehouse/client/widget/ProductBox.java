package net.aonsolutions.aon.gwt.warehouse.client.widget;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.product.JsProduct;
import com.esferalia.aon.gwt.common.client.AON;
//import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
//import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
//import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox.AccountingRegistrySuggestion;
//import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox.AccountingRegistrySuggestionDisplay;
//import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryPanel.AccountingRegistryPanelCallback;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.AonConfiguration;
//import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
//import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
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

public class ProductBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<JsProduct>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {

	private API API;
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox product;
	private TextBox productTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private ProductSuggestionDisplay suggestionDisplay;
	
	private static class ProductSuggestionDisplay extends DefaultSuggestionDisplay {
	    private Widget suggestionMenu;

	    @Override
	    protected Widget decorateSuggestionList(Widget suggestionList) {
	        suggestionMenu = suggestionList;
	        return suggestionList;
	    }
	    
	    @Override
		protected void moveSelectionDown() {
			super.moveSelectionDown();
			scrollSelectedProductIntoView();
		}

		@Override
		protected void moveSelectionUp() {
			super.moveSelectionUp();
			scrollSelectedProductIntoView();
		}
		
	    private void scrollSelectedProductIntoView() {
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
	
	private static class ProductSuggestion extends MultiWordSuggestion {
		
		private JsProduct product;
		
		private ProductSuggestion(JsProduct product, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.product = product;
		}
		
		public JsProduct getProduct() {
			return product;
		}
		
	}
	 
	public ProductBox(API API) {
		this(null,-1);
		this.API = API;
	}
	
	public ProductBox(final String domainName, final int domain) {
		this(domainName,domain,null,true);
	}
	
	public ProductBox(final String domainName, final int domain, final AonConfiguration config, boolean showDescription) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
						&& AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					
					API.getProduct().getProductList(new HashMap<>(), new AsyncCallback<JSON<JsProduct>>() {
						
						@Override
						public void onSuccess(JSON<JsProduct> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
							if (result != null) {
								result.getData().stream().forEach(jsProduct -> 
								suggestions.add(new ProductSuggestion(
										jsProduct
										,jsProduct.getCode()
										,decorate(jsProduct, request.getQuery())))
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
		productTextBox = new TextBox();
		suggestionDisplay =  new ProductSuggestionDisplay();
		product = new SuggestBox(oracle,productTextBox,suggestionDisplay);
		productTextBox.setStyleName(AON.AON_CSS.aonInputText());
		productTextBox.setVisibleLength(9);
		productTextBox.setMaxLength(9);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonBold());
		descriptionLabel.setVisible(showDescription);
		
		product.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				ProductSuggestion selected = (ProductSuggestion) event.getSelectedItem();
				select( selected.getProduct() );
			}
		});
		product.addKeyUpHandler( new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( config != null &&	(isControlF3(event) || isPlusKeyAlone(event))) {
					showDialog(domainName,domain,config);
				}
			}
		});
		product.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ( AonStringUtils.isBlank( product.getValue() )) {
					select( null );	
				}
			}
		});
		rooPanel = new FlowPanel();
		rooPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.addStyleName(AON.AON_CSS.aonInline() );
		rooPanel.add(product);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	private boolean isControlF3(KeyUpEvent event) {
		return (event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_F3);
	}
	private boolean isPlusKeyAlone(KeyUpEvent event) {
		return event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS && AonStringUtils.PLUS.equals(productTextBox.getValue());
	}
	
	public void set(JsProduct product) {
		productTextBox.setText(product.getCode());
		select(product);
	}
	
	private void select(JsProduct product) {
		if (product != null) {
			productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			id = product.getId();
			descriptionLabel.setText(product.getCode());
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				productTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			} else {
				productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
		SelectionEvent.fire(ProductBox.this, product );
	}
	
	public void setValue(JsProduct product, boolean fireEvents) {
		if (product != null && product.getId() != null) {
			id = product.getId();
			productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			productTextBox.setValue(product.getCode(),fireEvents);
			description = product.getCode();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;
			if (isRequired()) {
				productTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			}
			productTextBox.setValue(null,fireEvents);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}
	
	public void setValue(JsProduct product) {
		setValue(product,true);
	}

	private void reset() {
		productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
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
		return product.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		product.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			productTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		product.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return productTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return productTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return productTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		productTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		productTextBox.selectAll();
		productTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		productTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return product.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsProduct> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return productTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return productTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return productTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(JsProduct product, String query) {
//		String text = AccountingRegistry.getFullDescription(accountingRegistry);
		String text = product.getCode() + " - " + product.getName();

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
		return product.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		product.setEnabled(enabled);
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
//				ProductBox.this.setFocus(b);
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
