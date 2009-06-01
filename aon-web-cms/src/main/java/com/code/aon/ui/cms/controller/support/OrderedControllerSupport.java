package com.code.aon.ui.cms.controller.support;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.IPositionObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.IController;

public class OrderedControllerSupport {
	
	private static final Logger LOGGER = Logger.getLogger(OrderedControllerSupport.class.getName());
	
	protected OrderedControllerListenerSupport orderedControllerListenerSupport = new OrderedControllerListenerSupport(); 
	
    private String positionAlias = "";
    
    public OrderedControllerSupport(String positionAlias){
    	this.positionAlias = positionAlias;
    }
    
    public void addListenerSupport(IOrderedControllerListener listener){
    	this.orderedControllerListenerSupport.addOrderedControllerListener(listener);
    }
    
	public String getPositionAlias() {
		return positionAlias;
	}

	public void setPositionAlias(String positionAlias) {
		this.positionAlias = positionAlias;
	}
	
	@SuppressWarnings("unchecked")
	private void move(IController controller, IPositionObject object, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = object.getPosition();
		int newPosition = oldPosition + movement;
		
		IPositionObject otherObject = null;
		
		Criteria criteria = new Criteria();
		orderedControllerListenerSupport.fireBeforeUseCriteria(criteria);
		criteria.addEqualExpression(controller.getManagerBean().getFieldName(getPositionAlias()), newPosition);
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		if (!list.isEmpty()) {
			otherObject = (IPositionObject)list.get(0);
		}

		otherObject.setPosition(oldPosition);
		controller.getManagerBean().update((ITransferObject)otherObject);
		
		object.setPosition(newPosition);
		controller.getManagerBean().update((ITransferObject)object);
		
		controller.initializeModel();
	}
	
    public void onMoveUp(IController controller) throws ManagerBeanException, ExpressionException {
		IPositionObject object = (IPositionObject) controller.getModel().getRowData();
    	onMoveUp(controller, object);
    }

    public void onMoveUp(IController controller, IPositionObject object) throws ManagerBeanException, ExpressionException {
    	move(controller, object, -1);
    }
    
    public void onMoveDown(IController controller) throws ManagerBeanException, ExpressionException {
    	IPositionObject object = (IPositionObject) controller.getModel().getRowData();
    	onMoveDown(controller, object);    	
    }

    public void onMoveDown(IController controller, IPositionObject object) throws ManagerBeanException, ExpressionException {
    	move(controller, object, 1);    	
    }
    
	public void reorderObjects(IController controller) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		orderedControllerListenerSupport.fireBeforeUseCriteria(criteria);
		criteria.addOrder(controller.getManagerBean().getFieldName(getPositionAlias()));
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			IPositionObject object = (IPositionObject)list.get(i);
			int oldPosition = object.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				object.setPosition(newPosition);
				controller.getManagerBean().update((ITransferObject)object);
			}
		}
	}

	public int getLastPosition(IController controller) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			orderedControllerListenerSupport.fireBeforeUseCriteria(criteria);
			position = controller.getManagerBean().getCount(criteria);
		}catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return position;
	}

}
