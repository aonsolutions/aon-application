package com.code.aon.ice.project;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.project.controller.PeriodicalTaskController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEPeriodicalTaskController extends PeriodicalTaskController {

	public void onSelect(RowSelectorEvent event){
		this.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "period_task_form");
	}
}
