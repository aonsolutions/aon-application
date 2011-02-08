package com.code.aon.faces.component.icefaces.rowSelector;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;

import com.sun.faces.application.MethodBindingMethodExpressionAdapter;

public class ActionMethodBinding extends MethodBindingMethodExpressionAdapter {

    public ActionMethodBinding() {} // for StateHolder
    
    public ActionMethodBinding(MethodExpression methodExpression) {
    	super( methodExpression );
    }
    
	@Override
	public Object invoke(FacesContext facesContext, Object[] arg1)
			throws EvaluationException, MethodNotFoundException {
		Object value = super.invoke(facesContext, arg1);
		if ( value != null ) {
			String action = value.toString();
			facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, action);			
		}
		return null;
	}
	
}
