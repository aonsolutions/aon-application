package com.code.aon.fiscal.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class FiscalModelManager implements IFiscalModelManager {
	
	protected Date getInitialDate( FiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		return c.getTime();
		
	}
	
	protected Date getDueDate( FiscalModel fiscalModel) {
		return fiscalModel.getPeriod().getDueDate(fiscalModel.getYear());
	}
	
	
	protected void fillDeclaredData(IFiscalDeclaration declaration ) throws ManagerBeanException {
		FiscalModel fiscalModel = declaration.getHeader();
		// Primera declaración del ejercicio, si no es complementaria, 
		// no se debe tener en cuenta lo almacenado en ese periodo.
		if ((fiscalModel.getPeriod() == Period.M01 || fiscalModel.getPeriod() == Period.T1) && !fiscalModel.isComplementary()) {
			return;  
		}
		IManagerBean bean = BeanManager.getManagerBean(FiscalModel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_YEAR), fiscalModel.getYear());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION), fiscalModel.getAdministration());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_MODEL), fiscalModel.getModel());
		String periodAlias = bean.getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);

		int i = fiscalModel.getPeriod().ordinal(); 
		if (i == 0 ) {
			criteria.addEqualExpression(periodAlias, Period.M01);  // ENERO y COMPLENTARIA
		}
		if (i == 12 ) {
			criteria.addEqualExpression(periodAlias, Period.T1); // 1 TRIMESTRE y COMPLENTARIA
		}
		if (i >0 && i<12) {
			criteria.addBetweenExpression(periodAlias, Period.M01, Period.values()[fiscalModel.getPeriod().ordinal() -1 ]);
		}
		if (i >12 && i<16) {
			criteria.addBetweenExpression(periodAlias, Period.T1, Period.values()[fiscalModel.getPeriod().ordinal() -1 ]);
		}
		// Si params.getPeriod() == Period.YEAR Se saca todo lo del ejercicio, o sea, no se añaden filtros.
		if (fiscalModel.isExtraDeclaration() && fiscalModel.getPeriod() == Period.YEAR) {
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(periodAlias, Period.YEAR));
		}
		IManagerBean detailBean = BeanManager.getManagerBean(FiscalModelDetail.class);
		String alias = detailBean.getFieldName(IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID);
		Criteria crit = null;
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			FiscalModel prev = (FiscalModel) to;
			crit = new Criteria();
			crit.addEqualExpression(alias, prev.getId());
			List<ITransferObject> details = detailBean.getList(crit);
			for (ITransferObject det : details) {
				FiscalModelDetail detail = (FiscalModelDetail) det;
				IFiscalModelKey key = declaration.getKey( detail.getType() );
				declaration.ensureDetail(key).addDeclaredAmount( detail.getAmount() );
			}
		}
	}
	
	@Override
	public IFiscalDeclaration refreshFiscalModel(IFiscalDeclaration declaration, FiscalModel fiscalModel) {
		declaration.setHeader( fiscalModel );
		return declaration;
	}

}
