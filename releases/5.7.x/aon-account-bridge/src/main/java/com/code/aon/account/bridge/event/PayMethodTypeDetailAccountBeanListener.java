package com.code.aon.account.bridge.event;



import com.code.aon.account.bridge.PayMethodTypeDetailAccount;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.config.PayMethodTypeDetail;

public class PayMethodTypeDetailAccountBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		PayMethodTypeDetailAccount pmtda = (PayMethodTypeDetailAccount) evt.getTo();
		IManagerBean bean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		bean.remove( pmtda.getPayMethodTypeDetail() );
	}


}
