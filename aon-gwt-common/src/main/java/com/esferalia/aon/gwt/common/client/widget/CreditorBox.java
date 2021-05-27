package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
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

public class CreditorBox extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<Creditor>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 8;

	private CommonServiceAsync commonService;

	private Integer id;
	private String description;
	
	private FlowPanel rooPanel; 
	private SuggestBox creditor;
	private TextBox creditorTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private CreditorSuggestionDisplay suggestionDisplay;
	
	private static class CreditorSuggestionDisplay extends DefaultSuggestionDisplay {
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
	
	private static class CreditorSuggestion extends MultiWordSuggestion {
		
		private Creditor creditor;
		
		private CreditorSuggestion(Creditor creditor, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.creditor = creditor;
		}
		
		public Creditor getCreditor() {
			return creditor;
		}
		
	}
	
	public CreditorBox(final String domainName, final int domain, final String user) {
		this(domainName,domain,user,true);
	}
	
	public CreditorBox(final String domainName, final int domain, final String user, boolean showDescription) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					commonService.getBasicCreditors(domainName,domain, user,request.getQuery()
							,new AsyncCallback<LinkedList<Creditor>>() {
		
								public void onFailure(Throwable caught) {
									descriptionLabel.setText(AON.MSG.creditorNotFound());
									descriptionLabel.addStyleName(AON.AON_CSS.aonColorRed());
									callback.onSuggestionsReady(request, new Response());
								}
		
								public void onSuccess(LinkedList<Creditor> result) {
									LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
									if (result != null) {
										for (final Creditor creditor : result) {
											suggestions.add(new CreditorSuggestion(creditor, creditor.getDocument()
									        		, decorate(Registry.getFullDescription(creditor), request.getQuery())));
										}
									}
									Response resp = new Response(suggestions);
									callback.onSuggestionsReady(request, resp);
									
								}
							});
				}
			}
		};
		creditorTextBox = new TextBox();
		suggestionDisplay =  new CreditorSuggestionDisplay();
		creditor = new SuggestBox(oracle,creditorTextBox,suggestionDisplay);
		creditorTextBox.setStyleName(AON.AON_CSS.aonInputText());
		creditorTextBox.setVisibleLength(15);
		creditorTextBox.setMaxLength(15);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		descriptionLabel.setVisible(showDescription);
		
		creditor.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				CreditorSuggestion selected = (CreditorSuggestion) event.getSelectedItem();
				select( selected.getCreditor() );
			}
		});
		
		rooPanel = new FlowPanel();
		rooPanel.addStyleName(AON.AON_CSS.aonNowrap() );
		rooPanel.add(creditor);
		rooPanel.add(descriptionLabel);
		initWidget(rooPanel);
	}
	
	private void select(Creditor creditor) {
		creditorTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		id = creditor.getId();
		description = creditor.getName();
		descriptionLabel.setText(description);
		descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		SelectionEvent.fire(CreditorBox.this, creditor );
	}
	
	public void setValue(Creditor creditor) {
		if (creditor != null && creditor.getId() != null) {
			id = creditor.getId();	
			creditorTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			creditorTextBox.setValue(creditor.getDocument());
			description = creditor.getName();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id = null;	
			creditorTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			creditorTextBox.setValue(null);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}

	private void reset() {
		creditorTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
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
		return creditor.getValue();
	}

	public void setValue(Integer id, String code,String description) {
		this.id = id;
		this.description = description;
		this.descriptionLabel.setText(description);
		setValue(code,false);
	}

	@Override
	public void setValue(String value) {
		creditor.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			creditorTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			creditorTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		creditor.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return creditorTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return creditorTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return creditorTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		creditorTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		creditorTextBox.selectAll();
		creditorTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		creditorTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return creditor.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Creditor> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return creditorTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return creditorTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return creditorTextBox.addKeyPressHandler(handler);
	}
	
	private static String decorate(String text, String query) {
		int i = AonStringUtils.indexOfIgnoreCase(text, query);
		SafeHtmlBuilder bld = new SafeHtmlBuilder();
		bld.appendHtmlConstant("<span class=\"" 
				+ AON.AON_CSS.aonIconPointLightGreen() 
				+ AonStringUtils.SPACE
				+ AON.AON_CSS.aonPaddingLeft20()
				+ "\" >");
		bld.appendEscaped(AonStringUtils.substring(text, 0, i));
		bld.appendHtmlConstant(BEGIN_STRONG);
		bld.appendEscaped(AonStringUtils.substring(text, i, (i + AonStringUtils.length(query) )));
        bld.appendHtmlConstant(END_STRONG);
        bld.appendEscaped(AonStringUtils.substring(text, (i + AonStringUtils.length(query) )));
        bld.appendHtmlConstant("</span>");
        return bld.toSafeHtml().asString(); 
	}

	@Override
	public boolean isEnabled() {
		return creditor.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		creditor.setEnabled(enabled);
	}
}
