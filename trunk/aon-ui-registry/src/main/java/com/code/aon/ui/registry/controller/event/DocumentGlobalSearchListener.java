package com.code.aon.ui.registry.controller.event;


import static com.code.aon.registry.enumeration.RegistryAttachmentType.CORPORATE_IDENTITY;
import static com.esferalia.aon.entity.IEntityAlias.REGISTRY_ATTACHMENT_DOMAIN;
import static com.esferalia.aon.entity.IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE;
import static com.esferalia.aon.entity.IEntityAlias.REGISTRY_ATTACHMENT_SCOPE_ID;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;

public class DocumentGlobalSearchListener extends CorporateIdentitySearchListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentGlobalSearchListener.class);
	
	private Domain domain;
	
	private List<Integer> childDomains;
	
	public DocumentGlobalSearchListener() {
		this.childDomains = getChildDomains();
	}
	
	private List<Integer> getChildDomains() {
		List<Integer> ids = new LinkedList<Integer>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), DomainManager.getCurrentDomain());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_ACTIVE), Boolean.TRUE);
			for( ITransferObject to : bean.getList(criteria) ) {
				ids.add( ((Domain)to).getId() );
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
		return ids;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	@Override
	protected void reset() throws ManagerBeanException {
		super.reset();
		setDomain((Domain)BeanManager.getManagerBean(Domain.class).createNewTo());		
	}

	private Expression getCurrentDomainExpression() throws ManagerBeanException {
		Expression expr1 = DomainManager.getCurrentDomainExpression(RegistryAttachment.class);
		Expression expr2 = UserUtils.getInstance().getNullableScopeExpression(getFieldName(REGISTRY_ATTACHMENT_SCOPE_ID));
		return ExpressionUtilities.getAndExpression(expr1, expr2);		
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		criteria.addEqualExpression(getFieldName(REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), CORPORATE_IDENTITY);
		if ((getDomain() != null) && (getDomain().getId() != null)) {
			if ( DomainManager.getCurrentDomain().equals(getDomain().getId()) ) {
				criteria.addExpression(getCurrentDomainExpression());	
			} else {
				criteria.addEqualExpression(getFieldName(REGISTRY_ATTACHMENT_DOMAIN), getDomain().getId());	
			}			
		} else {
			Expression expression = getCurrentDomainExpression();
			if (! childDomains.isEmpty() ) {
				Expression expr2 = ExpressionUtilities.getInExpression(getFieldName(REGISTRY_ATTACHMENT_DOMAIN), childDomains);
				expression = ExpressionUtilities.getOrExpression(expression, expr2);				
			}
			criteria.addExpression(expression);
		}
	}
	
}