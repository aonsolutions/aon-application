package com.esferalia.aon.gwt.common.client.widget.solutions;

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

public class AonIntegerBox extends ValueBox<Integer> {

	private static final int CHANGE_DISPLAY_MILLIS = 4000;
	private static final int VISIBLE_LENGTH = 10;
	private static final int MAX_LENGTH = 10;

	private static final Renderer<Integer> RENDERER = new AbstractRenderer<Integer>() {

		@Override
		public String render(Integer object) {
			if (object == null)
				return "";
			return Integer.toString(object);
		}
	};

	private static final Parser<Integer> PARSER = new Parser<Integer>() {

		@Override
		public Integer parse(CharSequence text) throws ParseException {
			if (AonStringUtils.isEmpty(text))
				return null;
			try {
				return Integer.parseInt(text.toString());
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage(), 0);
			}

		}
	};

	public AonIntegerBox() {
		super(Document.get().createTextInputElement(), RENDERER, PARSER);
		setVisibleLength(VISIBLE_LENGTH);
		setMaxLength(MAX_LENGTH);
		setStyleName(AON.CSS.aonInputText());
		addStyleName(AON.CSS.aonNumberBox());

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				try {
					getValueOrThrow();
					removeStyleName(AON.CSS.aonInputError());
				} catch (ParseException e) {
					addStyleName(AON.CSS.aonInputError());
				}
			}
		});
	}
	
	public void setValue(Byte value) {
		super.setValue(value==null?null:value.intValue());
	}
	
	public void setValue(Integer value, boolean fireEvents, boolean shouldDisplayChange) {
		setValue(value, fireEvents);
		if (shouldDisplayChange) {
			addStyleName(AON.CSS.aonValueChanged());
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
