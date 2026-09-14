package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AonCustomTextArea extends HTMLPanel implements RequiresResize  {

	private static final String EMPTY_STRING = "";
	private static final int DEFAULT_MIN_HEIGHT = 40;

	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private TextArea textArea;

	private boolean autoResize = false;
	private int minHeight = DEFAULT_MIN_HEIGHT;
	private Runnable afterResize;

	private HandlerRegistration autoResizeKeyUpRegistration;
	private HandlerRegistration autoResizeValueChangeRegistration;

    public AonCustomTextArea(String title) {
        super(EMPTY_STRING);
        addStyleName(AON.CSS.aonFlexColumn2());
        addStyleName(AON.CSS.aonCustomTextArea());

        createTitle(title);
        createInput(title);
    }

    @Override
    public void onResize() {
        Widget parent = getParent();
        if (parent != null && parent instanceof RequiresResize) {
            ((RequiresResize) parent).onResize();
        }

        // Con auto-resize el alto lo manda el contenido, no el contenedor
        if (autoResize) {
        	adjustHeight();
        	return;
        }

        // Ajustar el textarea al alto disponible del contenedor
        int height = getOffsetHeight();
        if (height > 0) {
            textArea.setHeight(height + "px");
        }
    }


	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput(String title) {
		textBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		textBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		textBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		textArea = new TextArea();
		textArea.setVisibleLines(6);
		textArea.setStyleName(AON.CSS.aonCustomTextAreaInput());
		//textArea.getElement().setPropertyString("placeholder", "Introduce un valor");

		textArea.getElement().getStyle().setProperty("flex", "1");
		textArea.getElement().getStyle().setProperty("width", "100%");
		textArea.getElement().getStyle().setProperty("box-sixing", "border-box");
		
		textBoxPanel.add(textArea);
		add(textBoxPanel);
	}

	// ------------------------------------------------- Auto resize

	public void setAutoResize(boolean enabled) {
		setAutoResize(enabled, null);
	}

	/**
	 * @param enabled     si el textarea debe crecer/encoger con el contenido
	 * @param afterResize acci\u00f3n a ejecutar tras cada ajuste (por ejemplo recentrar
	 *                    el di\u00e1logo que lo contiene). Puede ser <code>null</code>.
	 */
	public void setAutoResize(boolean enabled, Runnable afterResize) {
		this.autoResize = enabled;
		this.afterResize = afterResize;

		if (enabled) {
			textArea.setVisibleLines(2);
			textArea.getElement().getStyle().setProperty("resize", "none");
			textArea.getElement().getStyle().setProperty("overflow", "hidden");

			if (null == autoResizeKeyUpRegistration)
				autoResizeKeyUpRegistration = textArea.addKeyUpHandler(e -> scheduleAdjustHeight());
			if (null == autoResizeValueChangeRegistration)
				autoResizeValueChangeRegistration = textArea.addValueChangeHandler(e -> scheduleAdjustHeight());

			scheduleAdjustHeight();
		} else {
			if (null != autoResizeKeyUpRegistration) {
				autoResizeKeyUpRegistration.removeHandler();
				autoResizeKeyUpRegistration = null;
			}
			if (null != autoResizeValueChangeRegistration) {
				autoResizeValueChangeRegistration.removeHandler();
				autoResizeValueChangeRegistration = null;
			}

			textArea.getElement().getStyle().clearProperty("overflow");
		}
	}

	public boolean isAutoResize() {
		return this.autoResize;
	}

	public void setMinHeight(int minHeightPx) {
		this.minHeight = minHeightPx;
		scheduleAdjustHeight();
	}

	private void scheduleAdjustHeight() {
		if (!autoResize)
			return;

		Scheduler.get().scheduleDeferred(() -> adjustHeight());
	}

	private void adjustHeight() {
		if (!autoResize || !isVisible())
			return;

		Element element = textArea.getElement();
		// "auto" primero para que tambi\u00e9n pueda encoger al borrar texto
		element.getStyle().setProperty("height", "auto");
		int newHeight = Math.max(element.getScrollHeight(), minHeight);
		element.getStyle().setProperty("height", newHeight + "px");

		if (null != afterResize)
			afterResize.run();
	}

	// ------------------------------------------------- Accessors

	public TextArea getTextBox() {
		return this.textArea;
	}

	public void setValue(String value) {
		setValue(value, false);
	}

	public void setValue(String value, boolean fireEvents) {
		this.textArea.setValue(value, fireEvents);
		scheduleAdjustHeight();
	}

	public String getValue() {
		return this.textArea.getValue();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			scheduleAdjustHeight();
	}

	public void setVisibleLines(int lines) {
		this.textArea.setVisibleLines(lines);
	}

	public void setPlaceHolder(String placeHolder) {
		this.textArea.getElement().setPropertyString("placeholder", placeHolder);
	}

	public void setEnable(boolean enabled) {
		this.textArea.setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.textArea.setFocus(focused);
	}

	public void addButton(Widget button) {
		textBoxPanel.add(button);
	}

	public void addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		textArea.addValueChangeHandler(valueChangeHandler);
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
		this.textArea.ensureDebugId(baseID + "Input");
	}

}