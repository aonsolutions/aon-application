package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AonCheckedEvent<T> extends GwtEvent<AonCheckedHandler<T>> {

	private static final Type<AonCheckedHandler<?>> TYPE = new Type<>();
	private final T t;
	
	public AonCheckedEvent(HasCheckedHandlers<T> source, T t) {
		setSource(source);
		this.t = t;
	}

	public T getValue() {
		return this.t;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Type<AonCheckedHandler<T>> getType() {
    	return (Type) TYPE;
    }

	@Override
	public Type<AonCheckedHandler<T>> getAssociatedType() {
		return getType();
	}
    
	@Override
	protected void dispatch(AonCheckedHandler<T> handler) {
		handler.onCheck(this);
	}

	public static <T> void fire(HasCheckedHandlers<T> source, T t) {
		source.fireEvent(new AonCheckedEvent<T>(source, t));
	}

}

