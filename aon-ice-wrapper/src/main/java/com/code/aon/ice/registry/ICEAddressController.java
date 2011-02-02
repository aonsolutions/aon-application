package com.code.aon.ice.registry;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEAddressController extends BasicController {

	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
