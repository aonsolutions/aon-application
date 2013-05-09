package com.code.aon.ui.manager.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.manager.DomainUser;
import com.code.aon.manager.Signature;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.SignatureController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.dao.IWebMailAlias;

public class CompanyDomainControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(CompanyDomainControllerListener.class);

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Company company = (Company) event.getController().getTo();
		company.setDomain(1);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Company company = (Company) event.getController().getTo();
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		try {
			DomainUser admin = dc.getDomain().getAdministrator();
			String companyName = company.getName();
			updateSignature(admin, companyName);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void updateSignature( DomainUser admin, String companyName ) throws ManagerBeanException {
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		SignatureController controller = (SignatureController) AonUtil.getRegisteredBean(BEAN_SIGNATURE);
		controller.updateBaseDN(admin.getId());		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(controller.getFieldName(IWebMailAlias.SIGNATURE_NAME), admin.getDomain());
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		Signature signature = list.isEmpty() ? new Signature() : (Signature) list.get(0);
		duc.initDefaultSignature(signature, admin, companyName);
		controller.getManagerBean().insertOrUpdate(signature);
	}
	
}