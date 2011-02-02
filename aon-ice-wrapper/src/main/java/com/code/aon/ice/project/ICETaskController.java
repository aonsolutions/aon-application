package com.code.aon.ice.project;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.ui.project.controller.TaskController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICETaskController extends TaskController {

    private boolean disableOnSelect;

    public void onSelect(RowSelectorEvent event){
    	if(!this.disableOnSelect){
        	this.onSelect(new ActionEvent(event.getComponent()));
        	FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "task_form");
    	}
    	this.disableOnSelect = false;
    }
    
    public void rowSelected(ValueChangeEvent event){
        if(event.getNewValue() != null){
            setRowChecked(((Boolean)event.getNewValue()).booleanValue());
        }
        this.disableOnSelect = true;
    }
}