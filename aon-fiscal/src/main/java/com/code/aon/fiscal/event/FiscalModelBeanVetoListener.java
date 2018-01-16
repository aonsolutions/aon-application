package com.code.aon.fiscal.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class FiscalModelBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FiscalModel fiscalModel = (FiscalModel) evt.getTo();
		if ( fiscalModel.getModel() == FiscalModelType.M303 && fiscalModel.getYear() > 2017) {
			throw new ManagerBeanVetoListenerException("A partir del ejercicio 2018, utilice el nuevo programa de autoliquidación de IVA");
		}
		checkFiscalModel(fiscalModel);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FiscalModel fiscalModel = (FiscalModel) evt.getTo();
		checkFiscalModel(fiscalModel);
	}

	private void checkFiscalModel(FiscalModel fiscalModel) throws ManagerBeanVetoListenerException {
		
		// El modelo 130 sólo está disponible para Territorio Común
		if ( fiscalModel.getModel() == FiscalModelType.M130 && fiscalModel.getAdministration() != Administration.COMMON_TERRITORY) {
			throw new ManagerBeanVetoListenerException("No existe soporte para la declaración del modelo 130 en esta administración.");
		}
		// El modelo 131 sólo está disponible para Territorio Común
		if ( fiscalModel.getModel() == FiscalModelType.M131 && fiscalModel.getAdministration() != Administration.COMMON_TERRITORY) {
			throw new ManagerBeanVetoListenerException("No existe soporte para la declaración del modelo 131 en esta administración.");
		}

		if (fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
			if (StringUtils.isEmpty(fiscalModel.getAdmonAeat())) {
				throw new ManagerBeanVetoListenerException("No se ha indicado el Código de Administración AEAT.");
			}
		}

		if (fiscalModel.isComplementary() && fiscalModel.isReplacement()) {
			throw new ManagerBeanVetoListenerException("La declaración no puede ser Complementaria y Sustitutiva.");
		}

		if (!fiscalModel.isComplementary() && !fiscalModel.isReplacement()) {
			checkNormalFiscalModel(fiscalModel);
		} else {
			checkPeriodFiscalModel(fiscalModel);
		}
	}

	private void checkNormalFiscalModel(FiscalModel fiscalModel) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(FiscalModel.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_YEAR), fiscalModel.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD), fiscalModel.getPeriod());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION), fiscalModel.getAdministration());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_MODEL), fiscalModel.getModel());
			if (fiscalModel.getModel() == FiscalModelType.M130) {
				c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_DOCUMENT), fiscalModel.getDocument());	
			}
			if (fiscalModel.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_ID), fiscalModel.getId()));
			}
			int size = bean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para este año, periodo y administración.");
			}

		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkPeriodFiscalModel(FiscalModel fiscalModel) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(FiscalModel.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_YEAR), fiscalModel.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD), fiscalModel.getPeriod());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION), fiscalModel.getAdministration());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_MODEL), fiscalModel.getModel());
			if (fiscalModel.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_ID), fiscalModel.getId()));
			}
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanVetoListenerException("No existe una declaración para este periodo y administración a la que complementar o sustituir.");
			}
			for (ITransferObject to : list) {
				FiscalModel r = (FiscalModel) to;	
				if (r.getStatus() != FiscalModelStatus.FINISHED) {
					throw new ManagerBeanVetoListenerException("La declaración de este periodo a la que complementar o sustituir, no está FINALIZADA.");
				}
				if ((fiscalModel.isComplementary() && r.isReplacement()) || (fiscalModel.isReplacement() && r.isComplementary())) {
					throw new ManagerBeanVetoListenerException("No se permite la existencia de declaraciones complementarias y sustitutivas en un mismo periodo.");	
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

}
