package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class AonSuggestBox extends FlowPanel implements Focusable {
	
	protected static final String BEGIN_STRONG = "<strong>";
	protected static final String END_STRONG = "</strong>";
	protected static final int MIN_CHARACTERS = 3;
	protected static final int MAX_CHARACTERS = 14;
	
	protected static final AonSuggestionServiceAsync SERVICE;
	static {
		AonSuggestionServiceAsync serviceRaw = GWT.create(AonSuggestionService.class);
		SERVICE = new AonSuggestionServiceAsyncDecorator(serviceRaw);
	}

	private FlowPanel suggestBoxPanel = new FlowPanel();
	private SuggestBox suggestBox;
	private TextBox textBox;
	
	public AonSuggestBox(String title, MultiWordSuggestOracle oracle) {
		this(title, oracle, new AonSuggestionDisplay());
	}
	
	public AonSuggestBox(String title, MultiWordSuggestOracle oracle, AonSuggestionDisplay suggestionDisplay) {
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBox());
		
		Label titleLabel = new Label(AonStringUtils.trimToEmpty(title) );
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
		
		suggestBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		suggestBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		suggestBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		textBox = new TextBox();
		suggestBox = new SuggestBox(oracle,textBox, suggestionDisplay);
		suggestBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		suggestBoxPanel.add(suggestBox);
		add(suggestBoxPanel);
	}
	
	public SuggestBox getSuggestBox() {
		return this.suggestBox;
	}

	public void setValue(String value) {
		this.suggestBox.setValue(value);
	}

	public void setValue(String value, boolean fireEvent) {
		this.suggestBox.setValue(value, fireEvent);
	}

	public String getValue() {
		return this.suggestBox.getValue();
	}

	public void setAutoSelectEnabled(boolean autoSelect) {
		this.suggestBox.setAutoSelectEnabled(autoSelect);
	}
	
	public void setPlaceHolder(String placeHolder) {
		this.suggestBox.getElement().setPropertyString("placeholder", placeHolder);
	}

	@SuppressWarnings("deprecation")	// SuggestionDisplay.hideSuggestions is protected!! :(
	public void hideSuggestionList() {
		if (this.suggestBox.getSuggestionDisplay() instanceof AonSuggestionDisplay) {
			((AonSuggestionDisplay) this.suggestBox.getSuggestionDisplay()).hideSuggestionList();
		} else {
			this.suggestBox.hideSuggestionList();
		}
	}
	public void showSuggestionList() {
		this.suggestBox.showSuggestionList();
	}

	/**
	 * @deprecated use {@link #setEnabled(boolean)} instead
	 */
	@Deprecated
	public void setEnable(boolean enabled) {
		setEnabled(enabled);
	}
	public void setEnabled(boolean enabled) {
		suggestBox.setEnabled(enabled);
	}
	
	public void addButton(AonTableButton button) {
		suggestBoxPanel.add(button);
	}

	public void setMaxLength(int maxLength) {
		if ( getSuggestBox().getValueBox() instanceof TextBox ) {
			((TextBox) getSuggestBox().getValueBox()).setMaxLength(maxLength);
		}
	}

	public void addError() {
		addStyleName(AON.CSS.aonCustomError());
	}

	public void removeError() {
		removeStyleName(AON.CSS.aonCustomError());
	}

	public void addWarning() {
		addStyleName(AON.CSS.aonCustomWarning());
	}

	public void removeWarning() {
		removeStyleName(AON.CSS.aonCustomWarning());
	}
	
	public void setMaxWidth(String maxWidth) {
		getElement().getStyle().setProperty("max-width", maxWidth);
	}
	
	public void setMinWidth(String minWidth) {
		getElement().getStyle().setProperty("min-width", minWidth);
	}
	
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.suggestBox.ensureDebugId(baseID + "Input");
	}

	@Override
	public int getTabIndex() {
		return this.suggestBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		this.suggestBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		this.suggestBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		this.suggestBox.setTabIndex(index);
	}

}
