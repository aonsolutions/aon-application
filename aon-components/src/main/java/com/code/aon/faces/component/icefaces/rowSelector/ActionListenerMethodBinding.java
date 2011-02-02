package com.code.aon.faces.component.icefaces.rowSelector;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.event.ActionEvent;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.sun.faces.application.MethodBindingMethodExpressionAdapter;

public class ActionListenerMethodBinding extends MethodBindingMethodExpressionAdapter {

    public ActionListenerMethodBinding() {} // for StateHolder
    
    public ActionListenerMethodBinding(MethodExpression methodExpression) {
    	super( methodExpression );
    }
    
	@Override
	public Object invoke(FacesContext arg0, Object[] arg1)
			throws EvaluationException, MethodNotFoundException {
		RowSelectorEvent rse = (RowSelectorEvent) arg1[0];
		ActionEvent event = new ActionEvent(rse.getComponent());
		event.setPhaseId(rse.getPhaseId());
		return super.invoke(arg0, new Object[]{event});
	}
	
}
