package com.code.aon.ui.fiscal.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.ProfessionalRetention;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.WithholdingDetailSubkey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ProfessionalRetentionController extends BasicController{
	
	private static final CharSequence PERCENT = "%";
	private Date date;
	private Enterprise enterprise;
	private boolean resolved;
	private List<SelectItem> withholdingDetailSubkeys;

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public boolean isResolved() {
		return resolved;
	}
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	public void documentChanged(ActionEvent event) {
		setResolved(false);
		ProfessionalRetention pr = (ProfessionalRetention) getTo();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_ENTERPRISE_ID), pr.getEnterprise().getId());
			criteria.addEqualExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_DOCUMENT), pr.getDocument());
			List<ITransferObject> list = getManagerBean().getList(criteria);
			if (list != null && list.size() > 0) {
				ProfessionalRetention old = (ProfessionalRetention) list.get(0);
				pr.setDocument(old.getDocument());
				pr.setDocumentType(old.getDocumentType());
				pr.setDocumentCountry(old.getDocumentCountry());
				pr.setName(old.getName());
				setResolved(true);
			}
			
		} catch (ManagerBeanException e) {
			String message = "Imposible buscar si existe una perceptor con el documento '" +pr.getDocument()+ "'";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
	public void nameSelected(ActionEvent event) {
		setResolved(false);
		ProfessionalRetention pr = (ProfessionalRetention) getTo();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_ENTERPRISE_ID), pr.getEnterprise().getId());
			criteria.addEqualExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_NAME), pr.getName());
			List<ITransferObject> list = getManagerBean().getList(criteria);
			if (list != null && list.size() > 0) {
				ProfessionalRetention old = (ProfessionalRetention) list.get(0);
				pr.setDocument(old.getDocument());
				pr.setDocumentType(old.getDocumentType());
				pr.setDocumentCountry(old.getDocumentCountry());
				setResolved(true);
			}
			
		} catch (ManagerBeanException e) {
			String message = "Imposible buscar si existe una perceptor con el documento '" +pr.getDocument()+ "'";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	public void taxableBaseChanged(ActionEvent event) {
		ProfessionalRetention pr = (ProfessionalRetention) getTo();
		if (pr.getQuota() == 0.0 && pr.getPercent() != 0.0) {
			pr.setQuota( CommonUtil.round( pr.getTaxableBase() * pr.getPercent() / 100) );
		}
	}
	public void percentChanged(ActionEvent event) {
		ProfessionalRetention pr = (ProfessionalRetention) getTo();
		if (pr.getQuota() == 0.0) {
			pr.setQuota( CommonUtil.round( pr.getTaxableBase() * pr.getPercent() / 100) );
		}
	}
	public void quotaChanged(ActionEvent event) {
		ProfessionalRetention pr = (ProfessionalRetention) getTo();
		if (pr.getTaxableBase() == 0.0 && pr.getPercent() != 0.0) {
			pr.setTaxableBase( CommonUtil.round( pr.getQuota() * 100 / pr.getPercent()) );
		}
	}

	public List<?> autocompleteName(Object suggest) {
		try {
			ProfessionalRetention pr = (ProfessionalRetention) getTo();
			String condition = PERCENT + (String) suggest + PERCENT;
			ProjectionList projectionList = new ProjectionList();
			projectionList.add(Projection.group(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_NAME)));
			Criteria criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getLikeExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_NAME), condition));
			criteria.addEqualExpression(getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_ENTERPRISE_ID), pr.getEnterprise().getId());
			List<?> list = getManagerBean().getList(projectionList, criteria); 
			return list;
		} catch (ManagerBeanException e) {
			String message = "Imposible buscar por nombre. "+e.getMessage();
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
	public void onWithholdingDetailKeyChanged(ActionEvent event) {
		setWithholdingDetailSubkeys(null);
	}
	public List<SelectItem> getWithholdingDetailSubkeys() {
		if (withholdingDetailSubkeys == null) {
			setWithholdingDetailSubkeys(new LinkedList<SelectItem>());
			ProfessionalRetention pr = (ProfessionalRetention) getTo();
			if (pr.getKey() != null && pr.getKey().isSubkeyEnabled()) {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				for (WithholdingDetailSubkey key:WithholdingDetailSubkey.values()) {
					if (pr.getKey() == key.getKey()) {
						String name = StringUtils.abbreviate(key.getName(locale),150);
						SelectItem item = new SelectItem(key, name);
						getWithholdingDetailSubkeys().add(item);
					}
				}
			}
		}
		return withholdingDetailSubkeys;
	}
	public void setWithholdingDetailSubkeys(List<SelectItem> list) {
		withholdingDetailSubkeys = list;
	}
}
