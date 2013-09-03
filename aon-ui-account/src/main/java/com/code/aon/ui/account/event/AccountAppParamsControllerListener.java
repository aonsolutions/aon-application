package com.code.aon.ui.account.event;

import static com.code.aon.ui.common.ICommonMessages.ACCOUNT_APP_PARAM_ERROR;
import static com.code.aon.ui.common.ICommonMessages.ACCOUNT_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.ACCOUNT_SYSTEM_ERROR;

import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AccountAppParamsControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Account account = (Account)event.getController().getTo();
			IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
			List<ITransferObject> list = appParamBean.getList(null);
			for (ITransferObject to : list) {
				ApplicationParameter param = (ApplicationParameter) to;
				String value = param.getValue(); 
				if(value != null && value.equals(account.getId())){
					String msg = AonUtil.getMessage(ACCOUNT_BUNDLE, ACCOUNT_SYSTEM_ERROR);
					throw new ControllerListenerException(msg);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = AonUtil.getMessage(ACCOUNT_BUNDLE, ACCOUNT_APP_PARAM_ERROR);
			throw new ControllerListenerException(msg,e);
		}
	}

}