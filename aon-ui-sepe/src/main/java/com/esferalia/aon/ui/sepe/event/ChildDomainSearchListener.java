package com.esferalia.aon.ui.sepe.event;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ChildDomainSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String alias;
	private String detailAlias;
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getDetailAlias() {
		return detailAlias;
	}

	public void setDetailAlias(String detailAlias) {
		this.detailAlias = detailAlias;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			completeCriteria( this.getController().getCriteria() );
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido construir el filtro de empresas";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			String msg = "No se ha podido construir el filtro de empresas";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
		super.beforeModelInitialized(event);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, alias,
				false);
		if (!DomainManager.isDomainManagementAvailable()
				&& StringUtils.isNotBlank(detailAlias)) {
			criteria.addEqualExpression(detailAlias,
					DomainManager.getCurrentDomain());
		}
	}


}