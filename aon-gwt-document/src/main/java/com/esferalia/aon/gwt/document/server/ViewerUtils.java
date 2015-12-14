package com.esferalia.aon.gwt.document.server;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;

public class ViewerUtils {
	
	protected static Attach getRAttach(Domain domain, User user, Integer attachId){
		return AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(attachId),
				AttachType.REGISTRY);
	}
	
	protected static Integer getEnterpriseID() throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.company.Enterprise.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN),
				getDomainID() );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return null;
		
		return ((Enterprise) tos.get(0)).getId();
		
	}

	protected static Integer getDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getDomainId();
	}
}
