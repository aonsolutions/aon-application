package com.esferalia.aon.gwt.common.client.widget;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonMathUtils;
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
	private static final int PRECISION = 2;
	private static final int MAX_LENGTH = 15;
	private static final int CHANGE_DISPLAY_MILLIS = 4000;
	
	private int precision;
	
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
				return 0.0;
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
		setPrecision( PRECISION );
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

	private int getPrecision() {
		return this.precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		super.setValue(AonMathUtils.round(value,getPrecision()), fireEvents);
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
