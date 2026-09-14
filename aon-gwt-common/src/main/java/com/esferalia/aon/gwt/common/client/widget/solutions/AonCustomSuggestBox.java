package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Collection;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import java.util.function.Consumer;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Timer;

public class AonCustomSuggestBox extends HTMLPanel {
	
	private static final String EMPTY_STRING = "";
	private HTMLPanel suggestBoxPanel = new HTMLPanel(EMPTY_STRING);
	private SuggestBox suggestBox;
	
	private String lastQuery;
	private int suggestDelayMillis = 250;
	private Timer suggestTimer;
	
	private AonCustomSuggestOracle customOracle;
	
	public AonCustomSuggestBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		if (AonStringUtils.isNotEmpty(title)) {
			createTitle(title);
		}
		createInput();
	}
	
	public AonCustomSuggestBox(String title, AonCustomSuggestOracle customOracle) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		this.customOracle = customOracle;
		
		if (AonStringUtils.isNotEmpty(title)) {
			createTitle(title);
		}
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		suggestBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		suggestBoxPanel.addStyleName(AON.CSS.aonFlexBetween());

		SuggestOracle oracle = (null == customOracle) ? new MultiWordSuggestOracle() : customOracle;
		suggestBox = new SuggestBox(oracle, new TextBox(), new AonSuggestionDisplay());

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

	public void setFocus(boolean focused) {
		this.suggestBox.setFocus(focused);
	}

	public void setAutoSelectEnabled(boolean autoSelect) {
		this.suggestBox.setAutoSelectEnabled(autoSelect);
	}
	
	public void setPlaceHolder(String placeHolder) {
		this.suggestBox.getElement().setPropertyString("placeholder", placeHolder);
	}

	public void showSuggestionList() {
		this.suggestBox.showSuggestionList();
	}

	public void hideSuggestionList() {
		this.suggestBox.hideSuggestionList();
	}

	public void setEnable(boolean enabled) {
		suggestBox.setEnabled(enabled);
	}
	
	public boolean isEnable() {
		return suggestBox.isEnabled();
	}
	
	public void addButton(Widget button) {
		suggestBoxPanel.add(button);
	}
	
	public void removeButton() {
		if(suggestBoxPanel.getWidgetCount() > 1)
			suggestBoxPanel.remove(suggestBoxPanel.getWidgetCount() - 1);
	}

	public void setMaxLength(int maxLength) {
		((TextBox) getSuggestBox().getTextBox()).setMaxLength(maxLength);
	}
	
	public void setLimit(int limit) {
		this.suggestBox.setLimit(limit);
	}
	
	public void setSuggestDelayMillis(int suggestDelayMillis) {
		this.suggestDelayMillis = suggestDelayMillis;
	}

	// ------------------------------------------------- Handlers

	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return this.suggestBox.addSelectionHandler(handler);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return this.suggestBox.addValueChangeHandler(handler);
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return this.suggestBox.getValueBox().addKeyUpHandler(handler);
	}

	// ------------------------------------------------- Oracle

	public AonCustomSuggestOracle getOracle() {
	    return customOracle;
	}

	/**
	 * Sustituye las sugerencias disponibles. Funciona tanto con
	 * {@link AonCustomSuggestOracle} como con el {@link MultiWordSuggestOracle}
	 * por defecto, de modo que el c\u00f3digo cliente no tiene que castear.
	 */
	public void setSuggestions(Collection<String> items) {
		if (null != customOracle) {
			customOracle.setData(items);
			return;
		}

		SuggestOracle oracle = suggestBox.getSuggestOracle();
		if (oracle instanceof MultiWordSuggestOracle) {
			MultiWordSuggestOracle multiWordOracle = (MultiWordSuggestOracle) oracle;
			multiWordOracle.clear();
			if (null != items) {
				multiWordOracle.addAll(items);
				multiWordOracle.setDefaultSuggestionsFromText(items);
			}
		}
	}
	
	/**
	 * Gestiona el ciclo habitual de sugerencias remotas: Ctrl + espacio muestra todas,
	 * se ignoran las teclas de navegaci\u00f3n (para no reconstruir el popup mientras se
	 * navega con las flechas) y no se repite la misma consulta dos veces seguidas.
	 *
	 * @param minChars n\u00famero m\u00ednimo de caracteres para lanzar la b\u00fasqueda
	 * @param loader   recibe la query (o <code>null</code> para "todas")
	 */
	public HandlerRegistration addRemoteSuggestionsHandler(int minChars, Consumer<String> loader) {
		return addKeyUpHandler(e -> {
			int keyCode = e.getNativeKeyCode();

			if (e.isControlKeyDown() && keyCode == 32) {
				cancelPendingSuggestions();
				setValue("");
				lastQuery = null;
				loader.accept(null);
				return;
			}

			if (isNavigationKey(keyCode))
				return;

			String query = getValue();
			query = AonStringUtils.isBlank(query) ? null : query.trim();

			if (null == query || query.length() < minChars) {
				cancelPendingSuggestions();
				lastQuery = null;
				return;
			}

			if (AonStringUtils.equals(query, lastQuery))
				return;

			lastQuery = query;
			scheduleSuggestions(loader, query);
		});
	}

	private void scheduleSuggestions(Consumer<String> loader, String query) {
		cancelPendingSuggestions();

		if (suggestDelayMillis <= 0) {
			loader.accept(query);
			return;
		}

		suggestTimer = new Timer() {
			@Override
			public void run() {
				loader.accept(query);
			}
		};
		suggestTimer.schedule(suggestDelayMillis);
	}

	private void cancelPendingSuggestions() {
		if (null != suggestTimer) {
			suggestTimer.cancel();
			suggestTimer = null;
		}
	}

	private static boolean isNavigationKey(int keyCode) {
		switch (keyCode) {
			case KeyCodes.KEY_DOWN:
			case KeyCodes.KEY_UP:
			case KeyCodes.KEY_LEFT:
			case KeyCodes.KEY_RIGHT:
			case KeyCodes.KEY_ENTER:
			case KeyCodes.KEY_ESCAPE:
			case KeyCodes.KEY_TAB:
			case KeyCodes.KEY_HOME:
			case KeyCodes.KEY_END:
			case KeyCodes.KEY_PAGEUP:
			case KeyCodes.KEY_PAGEDOWN:
			case KeyCodes.KEY_SHIFT:
			case KeyCodes.KEY_CTRL:
			case KeyCodes.KEY_ALT:
				return true;
			default:
				return false;
		}
	}

	// ------------------------------------------------- Styles

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

}