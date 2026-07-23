package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AonUncheckedEvent<T> extends GwtEvent<AonUncheckedHandler<T>> {

	private static final Type<AonUncheckedHandler<?>> TYPE = new Type<>();
	private final T t;
	
	public AonUncheckedEvent(HasUncheckedHandlers<T> source, T t) {
		setSource(source);
		this.t = t;
	}

	public T getValue() {
		return this.t;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Type<AonUncheckedHandler<T>> getType() {
    	return (Type) TYPE;
    }

	@Override
	public Type<AonUncheckedHandler<T>> getAssociatedType() {
		return getType();
	}
    
	@Override
	protected void dispatch(AonUncheckedHandler<T> handler) {
		handler.onUncheck(this);
	}

	public static <T> void fire(HasUncheckedHandlers<T> source, T t) {
		source.fireEvent(new AonUncheckedEvent<T>(source, t));
	}

}

