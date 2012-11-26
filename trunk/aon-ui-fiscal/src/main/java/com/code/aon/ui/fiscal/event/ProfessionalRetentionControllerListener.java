package com.code.aon.ui.fiscal.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.ProfessionalRetention;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.fiscal.controller.ProfessionalRetentionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ProfessionalRetentionControllerListener extends ControllerAdapter{
	
	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		try {
			ProfessionalRetentionController prc = (ProfessionalRetentionController) event.getController();
			prc.setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar Empresa.";
			throw new ControllerListenerException(msg, e);
		}
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			ProfessionalRetentionController prc = (ProfessionalRetentionController) event.getController();
			if (prc.getEnterprise() != null && prc.getEnterprise().getId() != null ){
				prc.getCriteria().addEqualExpression(prc.getFieldName(IEntityAlias.PROFESSIONAL_RETENTION_ENTERPRISE_ID), prc.getEnterprise().getId());
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible formar el criterio de la búsqueda.";
			throw new ControllerListenerException(msg, e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProfessionalRetentionController prc = (ProfessionalRetentionController) event.getController();
		ProfessionalRetention pr = (ProfessionalRetention) prc.getTo();
		pr.setDocumentCountry(Country.ES);
		pr.setDocumentType(DocumentType.CIF);
		pr.setEnterprise( prc.getEnterprise() );
		pr.setPaymentDate(prc.getDate()==null?new Date():prc.getDate());
		pr.setTaxableBase(0.0);
		pr.setPercent(0.0);
		pr.setQuota(0.0);
		prc.setResolved(false);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProfessionalRetentionController prc = (ProfessionalRetentionController) event.getController();
		ProfessionalRetention pr = (ProfessionalRetention) prc.getTo();
		prc.setEnterprise( pr.getEnterprise() );
		prc.setDate(pr.getPaymentDate());
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProfessionalRetentionController prc = (ProfessionalRetentionController) event.getController();
		prc.setWithholdingDetailSubkeys(null);
	}
	
}
