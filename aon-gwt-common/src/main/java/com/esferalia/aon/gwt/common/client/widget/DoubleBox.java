package com.esferalia.aon.gwt.common.client.widget;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ValueBox;

public class DoubleBox extends ValueBox<Double> {

	private static final int VISIBLE_LENGTH = 12;
	private static final int MAX_LENGTH = 15;
	private static final int CHANGE_DISPLAY_MILLIS = 4000;
	
	private static final Renderer<Double> RENDERER = new AbstractRenderer<Double>() {

		@Override
		public String render(Double object) {
			if (object == null)
				return "";
			return Double.toString(object);
		}
	};

	private static final Parser<Double> PARSER = new Parser<Double>() {

		@Override
		public Double parse(CharSequence text) throws ParseException {
			if (AonStringUtils.isEmpty(text))
				return null;
			try {
				return Double.parseDouble(text.toString());
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage(), 0);
			}

		}
	};
	
	public DoubleBox() {
		this(VISIBLE_LENGTH);
	}
	
	public DoubleBox(int visibleLength) {
		super(Document.get().createTextInputElement(), RENDERER, PARSER);
		setVisibleLength(visibleLength);
		setMaxLength(MAX_LENGTH);
		setStyleName(AON.AON_CSS.aonInputText());
		addStyleName(AON.AON_CSS.aonNumberBox());

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				try {
					getValueOrThrow();
					removeStyleName(AON.AON_CSS.aonInputError());
				} catch (ParseException e) {
					addStyleName(AON.AON_CSS.aonInputError());
				}
			}
		});
	}

	public void setValue(Double value, boolean fireEvents, boolean shouldDisplayChange) {
		setValue(value, fireEvents);
		if (shouldDisplayChange) {
			addStyleName(AON.AON_CSS.aonValueChanged());
			if (CHANGE_DISPLAY_MILLIS > 0)
				new Timer() {
					@Override
					public void run() {
						removeStyleName(AON.AON_CSS.aonValueChanged());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
		}
	}
}
