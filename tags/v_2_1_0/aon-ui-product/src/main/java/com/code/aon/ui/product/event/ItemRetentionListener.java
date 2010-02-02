package com.code.aon.ui.product.event;

import com.code.aon.product.Item;
import com.code.aon.product.Tax;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemRetentionListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getRetention() != null){
			if(item.getProduct().getRetention().getId() == null){
				item.getProduct().setRetention(null);
			}
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getRetention() != null){
			if(item.getProduct().getRetention().getId() == null){
				item.getProduct().setRetention(null);
			}
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getRetention() == null){
			item.getProduct().setRetention(new Tax());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getRetention() == null){
			item.getProduct().setRetention(new Tax());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getRetention() == null){
			item.getProduct().setRetention(new Tax());
		}
	}
}