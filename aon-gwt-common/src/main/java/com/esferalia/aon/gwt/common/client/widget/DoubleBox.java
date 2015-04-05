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
import com.google.gwt.user.client.ui.ValueBox;

public class DoubleBox extends ValueBox<Double> {

	private static final int VISIBLE_LENGTH = 15;
	private static final int MAX_LENGTH = 15;

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
		super(Document.get().createTextInputElement(), RENDERER, PARSER);
		setVisibleLength(VISIBLE_LENGTH);
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
}
