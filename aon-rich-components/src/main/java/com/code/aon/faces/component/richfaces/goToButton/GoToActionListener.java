package com.code.aon.faces.component.richfaces.goToButton;

import javax.el.ValueExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class GoToActionListener implements ActionListener, StateHolder {
	
	private BasicController controller;
	
	private ValueExpression toExpression;
	
	private boolean isTransient;
	
	public GoToActionListener() {
	}

	public GoToActionListener(BasicController controller, ValueExpression to) {
		this.controller = controller;
		this.toExpression = to;
	}

	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ITransferObject to = (ITransferObject) toExpression.getValue(ctx.getELContext());
		try {
			IManagerBean bean = BeanManager.getManagerBean(to.getClass());
			controller.select(event, bean.getId(to));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e);
		}
	}

    public Object saveState(FacesContext context) {
        return new Object[] { controller, toExpression };
    }

    public void restoreState(FacesContext context, Object state) {
        controller = (BasicController) ((Object[]) state)[0];
        toExpression = (ValueExpression) ((Object[]) state)[1];
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean newTransientValue) {
        isTransient = newTransientValue;
    }	
}
