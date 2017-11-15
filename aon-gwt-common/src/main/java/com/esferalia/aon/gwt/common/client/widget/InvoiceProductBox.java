package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.product.Product;
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

public class InvoiceProductBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<Product>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 9;

	private CommonServiceAsync commonService;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox product;
	private TextBox productTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private InvoiceProductSuggestionDisplay suggestionDisplay;
	
	private static class InvoiceProductSuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	private static class ProductSuggestion extends MultiWordSuggestion {
		
		private Product product;
		
		private ProductSuggestion(Product product, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.product = product;
		}
		
		public Product getProduct() {
			return product;
		}
		
	}
	
	public InvoiceProductBox(final String domainName, final int domain) {
		this(domainName,domain,null,true);
	}
	
	public InvoiceProductBox(final String domainName, final int domain, final AonConfiguration config, boolean showDescription) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					commonService.getInvoiceProducts(domainName,domain,request.getQuery()
							,new AsyncCallback<LinkedList<Product>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.noData());
									descriptionLabel.addStyleName(AON.AON_CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<Product> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										for (final Product product : result) {
											suggestions.add(new ProductSuggestion(
													product
											   ,product.getCode()
											   ,decorate(product, request.getQuery())));
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}
							});
				}
			}
		};
		productTextBox = new TextBox();
		suggestionDisplay =  new InvoiceProductSuggestionDisplay();
		product = new SuggestBox(oracle,productTextBox,suggestionDisplay);
		productTextBox.setStyleName(AON.AON_CSS.aonInputText());
		productTextBox.setVisibleLength(9);
		productTextBox.setMaxLength(12);
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
	
	public void set(Product product) {
		productTextBox.setText(product.getCode());
		select(product);
	}
	
	private void select(Product product) {
		if (product != null) {
			productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			id = product.getId();
			descriptionLabel.setText(product.getName());
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
		SelectionEvent.fire(InvoiceProductBox.this, product );
	}
	
	public void setValue(Product product, boolean fireEvents) {
		if (product != null && product.getId() != null) {
			id = product.getId();
			productTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			productTextBox.setValue(product.getCode(),fireEvents);
			description = product.getName();
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
	
	public void setValue(Product product) {
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<Product> handler) {
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
	
	private static String decorate(Product product, String query) {
		String text = product.getCode() + AonStringUtils.SPACE + product.getName();
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span>");
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
	
}
   