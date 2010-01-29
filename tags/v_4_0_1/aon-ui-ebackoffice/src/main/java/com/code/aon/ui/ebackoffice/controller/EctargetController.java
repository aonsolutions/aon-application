package com.code.aon.ui.ebackoffice.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import com.code.aon.ebackoffice.enumeration.TargetType;
import com.code.aon.ui.form.BasicController;


public class EctargetController extends BasicController {

	
	private List<SelectItem> targetTypes;

	public List<SelectItem> getTargetTypes() {
		if(targetTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			targetTypes = new LinkedList<SelectItem>();
			for (TargetType e : TargetType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				targetTypes.add(item);
				
			}
		}
		
		return targetTypes;
	}
	
	
	@Override
	public void onRemove(ActionEvent event) {
		// TODO Auto-generated method stub
		
		System.out.println("2fdds");
		super.onRemove(event);
	}
	
}
