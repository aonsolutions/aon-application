package com.code.aon.ui.product.event;

import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemBrandListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getBrand() != null){
			if(item.getProduct().getBrand().getId() == null){
				item.getProduct().setBrand(null);
			}
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getBrand() != null){
			if(item.getProduct().getBrand().getId() == null){
				item.getProduct().setBrand(null);
			}
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getBrand() == null){
			item.getProduct().setBrand(new Brand());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getBrand() == null){
			item.getProduct().setBrand(new Brand());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if(item.getProduct().getBrand() == null){
			item.getProduct().setBrand(new Brand());
		}
	}
}