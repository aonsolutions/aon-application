package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonValidationUtil;
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

public class Epigraph2016Box extends ResizeComposite implements HasValue<String>
	, HasDescription, Focusable, HasSelectionHandlers<Epigraph>, HasAllFocusHandlers
	,HasAllKeyHandlers, HasEnabled {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 8;

	private String epigraph;
	private String description;
	
	private FlowPanel rootPanel; 
	private SuggestBox epigraphBox;
	private TextBox epigraphTextBox;
	private InlineLabel descriptionLabel;
	private boolean required = true;
	
	private DefaultSuggestionDisplay suggestionDisplay;
//	
//	private static class EpigraphSuggestionDisplay extends DefaultSuggestionDisplay {
//	    private Widget suggestionMenu;
//
//	    @Override
//	    protected Widget decorateSuggestionList(Widget suggestionList) {
//	        suggestionMenu = suggestionList;
//	        suggestionMenu.setWidth("300px");
//	        return suggestionList;
//	    }
//	    
//	    @Override
//		protected void moveSelectionDown() {
//			super.moveSelectionDown();
//			scrollSelectedItemIntoView();
//		}
//
//		@Override
//		protected void moveSelectionUp() {
//			super.moveSelectionUp();
//			scrollSelectedItemIntoView();
//		}
//		
//	    private void scrollSelectedItemIntoView() {
//	        NodeList<Node> trList = suggestionMenu.getElement().getChild(1).getChild(0).getChildNodes();
//	        for (int trIndex = 0; trIndex < trList.getLength(); ++trIndex) {
//	            Element trElement = (Element)trList.getItem(trIndex);
//	            if (((Element)trElement.getChild(0)).getClassName().contains("selected")) {
//	                trElement.scrollIntoView();
//	                break;
//	            }
//	        }
//	    }
//	}
	
	private static class EpigraphSuggestion extends MultiWordSuggestion {
		
		private Epigraph epigraph;
		
		private EpigraphSuggestion(Epigraph epigraph, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.epigraph = epigraph;
		}
		
		public Epigraph getEpigraph() {
			return epigraph;
		}
		
	}
	
	public Epigraph2016Box() {
		this(true);
	}
	
	public Epigraph2016Box(boolean showDescription) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
					for (final Epigraph epigraph : Epigraph.values()) {
						if (AonStringUtils.contains(epigraph.getEpigraph(), request.getQuery())
 						 || AonStringUtils.contains(epigraph.getDescription(), request.getQuery())) {
							suggestions.add(new EpigraphSuggestion(epigraph, epigraph.getEpigraph()
									, decorate(epigraph.getFullDescription(), request.getQuery())));
						}
						
					}
					Response resp = new Response(suggestions);
					callback.onSuggestionsReady(request, resp);
				}
			}
		};
		epigraphTextBox = new TextBox();
		epigraphTextBox.setVisibleLength(6);
		epigraphTextBox.setMaxLength(5);
		suggestionDisplay =  new DefaultSuggestionDisplay();
		
		epigraphBox = new SuggestBox(oracle,epigraphTextBox,suggestionDisplay);
		epigraphTextBox.setStyleName(AON.AON_CSS.aonInputText());
		epigraphTextBox.setVisibleLength(15);
		epigraphTextBox.setMaxLength(15);
		descriptionLabel = new InlineLabel();
		descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
		descriptionLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		descriptionLabel.setVisible(showDescription);
		
		epigraphBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				EpigraphSuggestion selected = (EpigraphSuggestion) event.getSelectedItem();
				select( selected.getEpigraph() );
			}
		});
		
		rootPanel = new FlowPanel();
		rootPanel.addStyleName(AON.AON_CSS.aonNowrap() );
		rootPanel.add(epigraphBox);
		rootPanel.add(descriptionLabel);
		initWidget(rootPanel);
	}
	
	private void select(Epigraph epigraph) {
		epigraphTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		this.epigraph = epigraph.getEpigraph();
		description = epigraph.getDescription();
		descriptionLabel.setText(description);
		descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		SelectionEvent.fire(Epigraph2016Box.this, epigraph );
	}
	
	public void setValue(Epigraph epigraph) {
		if (epigraph != null && epigraph.getEpigraph() != null) {
			this.epigraph = epigraph.getEpigraph();	
			epigraphTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
			epigraphTextBox.setValue(epigraph.getEpigraph());
			description = epigraph.getDescription();
			descriptionLabel.setText(description);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		} else {
			this.epigraph = null;	
			epigraphTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
			epigraphTextBox.setValue(null);
			descriptionLabel.setText(null);
			descriptionLabel.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}

	private void reset() {
		epigraphTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		epigraph = null;
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

	public String getEpigraph() {
		return epigraph;
	}

	@Override
	public String getValue() {
		return epigraphBox.getValue();
	}

	@Override
	public void setValue(String value) {
		epigraphBox.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
		if (AonStringUtils.isEmpty(value) || AonValidationUtil.isValidRequired(value, required)) {
			epigraphTextBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			epigraphTextBox.addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		epigraphBox.setValue(value,fireEvents);
	}

	@Override
	public String getDescription() {
		return description;	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return epigraphTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return epigraphTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return epigraphTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		epigraphTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		epigraphTextBox.selectAll();
		epigraphTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		epigraphTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return epigraphBox.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Epigraph> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return epigraphTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return epigraphTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return epigraphTextBox.addKeyPressHandler(handler);
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
		return epigraphBox.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		epigraphBox.setEnabled(enabled);
	}
}
