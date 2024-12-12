package com.esferalia.aon.gwt.common.client.widget;

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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ValueBox;

public class DoubleBox extends ValueBox<Double> implements HasErrorHandlers{

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
				double ret = DoubleBox.resolve(expression);
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
	
	private static final NumberFormat EUROPEAN_FORMAT = NumberFormat.getFormat("#,##0.00");

	
	private static final Renderer<Double> RENDERER = new AbstractRenderer<Double>() {

		@Override
		public String render(Double object) {
			if (object == null)
				return "";
			return EUROPEAN_FORMAT.format(object);
//			return Double.toString(object);
		}
	};
	
	private static final Parser<Double> PARSER = new Parser<Double>() {
        @Override
        public Double parse(CharSequence text) throws ParseException {
            if (text == null || text.toString().trim().isEmpty()) {
                return null;
            }

            try {
            	String input = text.toString().trim();

            	 // 1. Si contiene un punto y una coma, eliminamos el punto y convertimos la coma en un punto.
                if (input.contains(".") && input.contains(",")) {
                    input = input.replace(".", "").replace(",", ".");
                }
            	
                // 2. Si contiene más de un punto, eliminamos todos los puntos.
                if (input.chars().filter(ch -> ch == '.').count() > 1) {
                    input = input.replace(".", "");
                }

                // 3. Si contiene solo un punto, lo convertimos en una coma.
                else if (input.contains(".")) {
                    input = input.replace(".", ",");
                }

                // 4. Finalmente, convertir coma en punto para la conversión numérica.
                input = input.replace(",", ".");
            	
            	
                // Eliminar separadores de miles y reemplazar coma decimal por punto decimal
//                String normalized = text.toString().replace(".", "").replace(",", ".");
                return Double.valueOf(input);
            } catch (NumberFormatException e) {
                throw new ParseException("Formato de número no válido: " + text, 0);
            }
        }
    };
    
    @Override
    public void onBrowserEvent(Event event) {
    	super.onBrowserEvent(event);

        if (event.getTypeInt() == com.google.gwt.user.client.Event.ONBLUR) {
            try {
                // Procesar el texto ingresado manualmente
                Double parsedValue = getValueOrThrow(); // Validar y parsear el valor actual
                if (parsedValue != null) {
                    // Formatear y actualizar la vista usando el Renderer
                    setValue(parsedValue, false); // Esto renderiza correctamente con el formato
                }
            } catch (ParseException e) {
                addStyleName(AON.AON_CSS.aonInputError()); // Aplicar estilo de error si el parse falla
            }
        }
    }

//	private static final Parser<Double> PARSER = new Parser<Double>() {
//
//		@Override
//		public Double parse(CharSequence text) throws ParseException {
//			if (AonStringUtils.isEmpty(text))
//				return 0.0;
//			try {
//				return Double.parseDouble(text.toString());
//			} catch (NumberFormatException e) {
//				throw new ParseException(e.getMessage(), 0);
//			}
//
//		}
//	};
	
	public DoubleBox() {
		this(VISIBLE_LENGTH, PRECISION );
	}
	public DoubleBox(int visibleLength) {
		this(visibleLength, PRECISION );
	}
	public DoubleBox(int visibleLength, int precision) {
		super(Document.get().createTextInputElement(), RENDERER, PARSER);
		setPrecision( precision );
		setVisibleLength(visibleLength);
		setMaxLength(MAX_LENGTH);
		setStyleName(AON.AON_CSS.aonInputText());
		addStyleName(AON.AON_CSS.aonNumberBox());

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( !AonStringUtils.startsWith(getText(),EQUAL) ) {
					removeStyleName(AON.AON_CSS.aonInputCalc());
					try {
						getValueOrThrow();
						removeStyleName(AON.AON_CSS.aonInputError());
					} catch (ParseException e) {
						addStyleName(AON.AON_CSS.aonInputError());
						
					}
				}
			}
		});
		this.setResolver(ARITHMETIC_RESOLVER);
		
		addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                Double value = event.getValue();
                if (value != null) {
                    // Asegurarse de renderizar con el formato correcto
                    setValue(value, false);
                }
            }
        });
	}

	public void setResolver(final ExpressionResolver resolver) {
		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ( resolver != null) {
					if ( AonStringUtils.startsWith(getText(),EQUAL)) {
						setMaxLength(Integer.MAX_VALUE);
						addStyleName(AON.AON_CSS.aonInputCalc());	
						if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							String exp = AonStringUtils.substringAfter(getText(), DoubleBox.EQUAL);
							resolver.resolve(exp , new AsyncCallback<Double>() {
								
								@Override
								public void onSuccess(Double result) {
									setMaxLength(MAX_LENGTH);
									removeStyleName(AON.AON_CSS.aonInputError());
									removeStyleName(AON.AON_CSS.aonInputCalc());
									setValue(result, true, true);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									setMaxLength(MAX_LENGTH);
									removeStyleName(AON.AON_CSS.aonInputCalc());
									addStyleName(AON.AON_CSS.aonInputError());
									NativeEvent event = Document.get().createErrorEvent();
									DomEvent.fireNativeEvent(event, DoubleBox.this);
								}
							});
						}
					}
				} else {
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
		if (value == null) {
            value = 0.0;
        }

        // Asegurarse de que se renderice correctamente con el formato europeo
        super.setValue(AonMathUtils.round(value,getPrecision()), fireEvents);
        getElement().setPropertyString("value", EUROPEAN_FORMAT.format(AonMathUtils.round(value,getPrecision())));
    
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

	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler) {
		return addHandler(handler, ErrorEvent.getType());
	}
	
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return super.addValueChangeHandler(handler);
    }
}
