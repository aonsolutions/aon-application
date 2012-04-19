package com.esferalia.aon.ui.pms.event;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.util.IEmailControllerListener;
import com.code.aon.purchase.util.IEmailable;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;


public class PurchaseEmailListener implements IEmailControllerListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEmailListener.class.getName());
	
	private String masterControllerName;
	
	public String getMasterControllerName() {
		return masterControllerName;
	}

	public void setMasterControllerName(String masterControllerName) {
		this.masterControllerName = masterControllerName;
	}

	public void beforeEmailSend(ITransferObject to){
		// TODO por peticion, no poner como cc al hotel en los envios de email de pedidos de compra
//		addHotelToRecipients(to);
	}
	
	private void addHotelToRecipients(ITransferObject to) {
		LogPanelController logger = LogPanelController.getInstance();
		try {
			Purchase purchase = (Purchase) to;
			IManagerBean bean = BeanManager.getManagerBean(Hotel.class);
			if(purchase!=null){
				Hotel hotel = (Hotel) bean.get(purchase.getWorkPlace().getId());
				if(StringUtils.isEmpty(hotel.getEmail())){
					String text = "No hay email definido para el hotel "+hotel.getWorkPlace().getDescription();
					LOGGER.error( text );
					logger.warn( text );
				}
				IEmailable controller = (IEmailable) AonUtil.getRegisteredBean(getMasterControllerName());
				controller.setMoreRecipients(null);
				controller.getMoreRecipients().add(hotel.getEmail());
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}


}