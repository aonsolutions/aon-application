package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ValueBoxBase;

public abstract class AonValueChangeHandler<T> implements ValueChangeHandler<T> {

	T prevValue = null;
	T value = null;

	public AonValueChangeHandler(final ValueBoxBase<T> widget) {
		widget.addFocusHandler(new FocusHandler() {
        	public void onFocus(FocusEvent event) {
        		prevValue = widget.getValue();
        	}
    	});
	}

	@Override
	public void onValueChange(ValueChangeEvent<T> event) {
		value = event.getValue();
    	onValueChange(value, prevValue);
    
    	prevValue = value;
	}

	public abstract void onValueChange(T value, T prevValue);

}