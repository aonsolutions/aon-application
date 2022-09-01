package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonDomainBox extends ResizeComposite implements HasValue<String>
	, Focusable, HasSelectionHandlers<Domain>, HasAllFocusHandlers
	,HasAllKeyHandlers {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	
	private static final int MIN_CHARACTERS = 3;
	private static final int MAX_CHARACTERS = 15;

	private String schema;
	private Domain domain;
	
	private FlowPanel rootPanel; 
	private SuggestBox domainBox;
	private AonTextBox domainTextBox;
	
	private AccountSuggestionDisplay suggestionDisplay;
	
	static final ConsoleServiceAsync CONSOLE_SERVICE;
	static {
		ConsoleServiceAsync consoleServiceRaw = GWT.create(ConsoleService.class);
		CONSOLE_SERVICE = new ConsoleServiceAsyncDecorator(consoleServiceRaw); 
	}

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
	
	private static class DomainSuggestion extends MultiWordSuggestion {
		
		private Domain domain;
		
		private DomainSuggestion(Domain domain, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.domain = domain;
		}
		
		public Domain getDomain() {
			return domain;
		}
		
	}
	
	public AonDomainBox(final Occam occam) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
			@Override
			public void requestSuggestions(final Request request,final Callback callback) {
				suggestionDisplay.hideSuggestions();
				if (AonStringUtils.length(request.getQuery()) >= MIN_CHARACTERS
				 && AonStringUtils.length(request.getQuery()) <= MAX_CHARACTERS) {
					reset();
					CONSOLE_SERVICE.getDomains(occam,getSchema(),request.getQuery(),new AsyncCallback<LinkedList<Domain>>() {
		
						public void onFailure(Throwable caught) {
							callback.onSuggestionsReady(request, new Response());
						}

						public void onSuccess(LinkedList<Domain> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<>();
							if (result != null) {
								
								for (final Domain domain : result) {
									SafeHtmlBuilder bld = new SafeHtmlBuilder();
									String ds = domain.getDescription() + " (" + domain.getName() + ")";
									int i = AonStringUtils.indexOfIgnoreCase(ds, request.getQuery());
									bld.appendHtmlConstant("<span style=\"white-space: pre;\" class=\"" + AON.CSS.aonTabIcon() + "\" >");
									bld.appendEscaped(AonStringUtils.substring(ds, 0, i));
									bld.appendHtmlConstant(BEGIN_STRONG);
									bld.appendEscaped(AonStringUtils.substring(ds, i, (i + AonStringUtils.length(request.getQuery()) )));
							        bld.appendHtmlConstant(END_STRONG);
							        bld.appendEscaped(AonStringUtils.substring(ds, (i + AonStringUtils.length(request.getQuery()) )));
							        bld.appendHtmlConstant("</span>");
							        DomainSuggestion as = new DomainSuggestion(domain, domain.getName(), bld.toSafeHtml().asString());
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
		domainTextBox = new AonTextBox();
		domainTextBox.addStyleName(AON.CSS.aonInputText());
		domainTextBox.setVisibleLength(25);
		domainTextBox.setMaxLength(25);
		
		suggestionDisplay =  new AccountSuggestionDisplay();
		domainBox = new SuggestBox(oracle,domainTextBox,suggestionDisplay);
		domainBox.addSelectionHandler(event -> select( ((DomainSuggestion)event.getSelectedItem()).getDomain() ));

		rootPanel = new FlowPanel();
		rootPanel.addStyleName(AON.CSS.aonNowrap() );
		rootPanel.addStyleName(AON.CSS.aonInline() );
		rootPanel.add(domainBox);
		initWidget(rootPanel);
	}
	
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	private void select(Domain domain) {
		domainTextBox.removeStyleName(AON.CSS.aonInputTextError());
		this.domain = domain;
		SelectionEvent.fire(AonDomainBox.this, this.domain);
	}
	
	private void reset() {
		domainTextBox.removeStyleName(AON.CSS.aonInputTextError() );
		domain = null;
	}

	public Domain getDomain() {
		return this.domain;
	}

	@Override
	public String getValue() {
		return domainBox.getValue();
	}

	@Override
	public void setValue(String value) {
		domainBox.setValue(value);
		if (AonStringUtils.isEmpty(value)) {
			reset();
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		domainBox.setValue(value,fireEvents);
	}

	public String getCode() {
		return getValue();	
	}

	// --------------------------------------------------------- HANDLERS
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return domainTextBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return domainTextBox.addFocusHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return domainTextBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		domainTextBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		domainTextBox.selectAll();
		domainTextBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		domainTextBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return domainBox.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Domain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return domainTextBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return domainTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return domainTextBox.addKeyPressHandler(handler);
	}
	public void setEnabled(boolean enabled) {
		domainTextBox.setEnabled(enabled);
	}

}
