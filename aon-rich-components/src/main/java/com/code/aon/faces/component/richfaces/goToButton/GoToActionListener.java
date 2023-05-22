package com.code.aon.faces.component.richfaces.goToButton;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
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
	
	private ValueExpression controllerExpression;
	
	private ValueExpression toExpression;
	
	private ValueExpression idExpression;
	
	private MethodExpression actionListener;
	
	private String backAction;
	
	private String backActionListener;
	
	private boolean isTransient;
	
	public GoToActionListener() {
	}

	public GoToActionListener(ValueExpression controllerExpression) {
		this.controllerExpression = controllerExpression;
	}

	public MethodExpression getActionListener() {
		return actionListener;
	}

	public void setActionListener(MethodExpression actionListener) {
		this.actionListener = actionListener;
	}

	public ValueExpression getToExpression() {
		return toExpression;
	}

	public void setToExpression(ValueExpression toExpression) {
		this.toExpression = toExpression;
	}
	
	public ValueExpression getIdExpression() {
		return idExpression;
	}

	public void setIdExpression(ValueExpression idExpression) {
		this.idExpression = idExpression;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String getBackActionListener() {
		return backActionListener;
	}

	public void setBackActionListener(String backActionListener) {
		this.backActionListener = backActionListener;
	}

	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		BasicController controller = (BasicController) controllerExpression.getValue(ctx.getELContext());		
		if ( actionListener != null ) {
			actionListener.invoke(ctx.getELContext(), new Object[] {event} );
		} else {
			ITransferObject to = GoToButtonHandler.getTo(ctx.getELContext(), toExpression, idExpression, controller);
			try {
				IManagerBean bean = BeanManager.getManagerBean(to.getClass());
				controller.select(event, bean.getId(to));
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException(e);
			}			
		}
		controller.setBackAction(backAction);
		controller.setBackActionListener(backActionListener);
	}

    public Object saveState(FacesContext context) {
        return new Object[] { controllerExpression, toExpression, actionListener,
        		backAction, backActionListener, idExpression };
    }

    public void restoreState(FacesContext context, Object state) {
    	Object[] _state = (Object[]) state;
    	controllerExpression = (ValueExpression) _state[0];
        toExpression = (ValueExpression) _state[1];
        actionListener = (MethodExpression) _state[2];
        backAction = (String) _state[3];
        backActionListener = (String) _state[4];
        idExpression = (ValueExpression) _state[5];
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean newTransientValue) {
        isTransient = newTransientValue;
    }	
}
