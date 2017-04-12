package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.ListBox;

public abstract class AonListBoxChangeHandler implements ChangeHandler {

	Integer prevIndex = null;

	public AonListBoxChangeHandler(final ListBox widget) {
		widget.addFocusHandler(new FocusHandler() {
        	public void onFocus(FocusEvent event) {
        		prevIndex = widget.getSelectedIndex();
        	}
    	});
	}

	@Override
	public void onChange(ChangeEvent event) {
    	onChange(prevIndex);
    }

	public abstract void onChange(Integer prevValue);

}