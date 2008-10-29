package com.code.aon.ui.purchase.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.purchase.Purchase;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the ItemController.
 */
public class PurchaseVetoListener extends ControllerAdapter {

	/**
	 * Sets a default productType to the current Item
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = wpBean.getList(null);
			if (wpLst.size() > 0) {
				WorkPlace wp = (WorkPlace)wpLst.get(0);
				Purchase purchase = (Purchase)event.getController().getTo();
				purchase.setWorkPlace(wp);
			}
			else throw new ControllerListenerException("No WorkPlace defined.");
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
			throw new ControllerListenerException(mbe.getMessage());
		}
	}
}