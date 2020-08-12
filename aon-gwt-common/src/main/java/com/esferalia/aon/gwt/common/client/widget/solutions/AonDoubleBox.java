package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ValueBox;

public class AonDoubleBox extends ValueBox<Double> implements HasErrorHandlers{

	private native static double resolve(String expression) /*-{
		d = eval(expression);
		return d;
	}-*/;	

	public static interface ExpressionResolver {
		void resolve(String expression, AsyncCallback<Double> callback);
	}
	private static final ExpressionResolver ARITHMETIC_RESOLVER = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			try {
				double ret = AonDoubleBox.resolve(expression);
				callback.onSuccess(ret);
			} catch (Throwable t) {
				callback.onFailure(t);
			}
		}
		
	};
	
	public static final String EQUAL = AonStringUtils.EQUAL;
	public static final int VISIBLE_LENGTH = 12;
	public static final int PRECISION = 2;
	public static final int MAX_LENGTH = 15;
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
	
	public AonDoubleBox() {
		this(VISIBLE_LENGTH, PRECISION );
	}
	public AonDoubleBox(int visibleLength) {
		this(visibleLength, PRECISION );
	}
	public AonDoubleBox(int visibleLength, int precision) {
		super(Document.get().createTextInputElement(), RENDERER, PARSER);
		setPrecision( precision );
		setVisibleLength(visibleLength);
		setMaxLength(MAX_LENGTH);
		setStyleName(AON.CSS.aonInputText());
		addStyleName(AON.CSS.aonNumberBox());

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( !AonStringUtils.startsWith(getText(),EQUAL) ) {
					removeStyleName(AON.CSS.aonInputCalc());
					try {
						getValueOrThrow();
						removeStyleName(AON.CSS.aonInputError());
					} catch (ParseException e) {
						addStyleName(AON.CSS.aonInputError());
						
					}
				}
			}
		});
		this.setResolver(ARITHMETIC_RESOLVER);
	}

	public void setResolver(final ExpressionResolver resolver) {
		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( resolver != null) {
					if ( AonStringUtils.startsWith(getText(),EQUAL)) {
						setMaxLength(Integer.MAX_VALUE);
						addStyleName(AON.CSS.aonInputCalc());	
						if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							String exp = AonStringUtils.substringAfter(getText(), AonDoubleBox.EQUAL);
							resolver.resolve(exp , new AsyncCallback<Double>() {
								
								@Override
								public void onSuccess(Double result) {
									setMaxLength(MAX_LENGTH);
									removeStyleName(AON.CSS.aonInputError());
									removeStyleName(AON.CSS.aonInputCalc());
									setValue(result, true, true);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									setMaxLength(MAX_LENGTH);
									removeStyleName(AON.CSS.aonInputCalc());
									addStyleName(AON.CSS.aonInputError());
									NativeEvent event = Document.get().createErrorEvent();
									DomEvent.fireNativeEvent(event, AonDoubleBox.this);
								}
							});
						}
					}
				} else {
					addStyleName(AON.CSS.aonInputError());
				}
			}
		});
/*
 	
 		// TODO DIALOGO PARA AÑADIR EXPRESIONES - CODE MIRROR.
 	
  		addClickHandler(new ClickHandler() {
 
			
			@Override
			public void onClick(ClickEvent event) {
				if ( resolver != null) {
					if ( AonStringUtils.startsWith(getText(),EQUAL)) {
						Window.alert("DIALOGO DE EXPRESIONES");
						Code
					}
				}
			}
		});
*/
	}
	
	private int getPrecision() {
		return this.precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		if (value == null) value = 0.0;
		super.setValue(AonMathUtils.round(value,getPrecision()), fireEvents);
	}
	
	public void setValue(Double value, boolean fireEvents, boolean shouldDisplayChange) {
		setValue(value, fireEvents);
		if (shouldDisplayChange) {
			addStyleName(AON.CSS.aonValueChanged());
			if (CHANGE_DISPLAY_MILLIS > 0)
				new Timer() {
					@Override
					public void run() {
						removeStyleName(AON.CSS.aonValueChanged());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
		}
	}

	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler) {
		return addHandler(handler, ErrorEvent.getType());
	}
}
