package com.code.aon.accounting.annualReport;

import java.util.List;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AnnualReportParameters {

	private SummaryProviderParameters params;
	private SummaryProviderParameters previousParams;
	private Period previousPeriod; 

	public SummaryProviderParameters getParams() {
		return params;
	}
	public void setParams(SummaryProviderParameters params) {
		this.params = params;
	}
	
	public SummaryProviderParameters getPreviousParams() {
		if (previousParams == null && params != null) {
			try {
				previousParams = getParams().clone();
				previousParams.setPeriod(getPreviousPeriod());
			} catch (CloneNotSupportedException e) {
				previousParams = null;
			}
		}
		return previousParams;
	}
	public void setPreviousParams(SummaryProviderParameters previousParams) {
		this.previousParams = previousParams;
	}
	
	public Period getPreviousPeriod() {
		if (previousPeriod == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Period.class);
				Criteria criteria = new Criteria();
				criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE),  
						getParams().getPeriod().getInitiationDate() );
				criteria.addOrder(bean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE),  false );
				List<ITransferObject> list = bean.getList(criteria);
				if (list != null && list.size() > 0 ) {
					ITransferObject to = list.get(0);
					previousPeriod = (Period) to;
				}
			} catch (ManagerBeanException e) {
				previousPeriod = null;
			}
		}
		return previousPeriod;
	}
	public void setPreviousPeriod(Period previousPeriod) {
		this.previousPeriod = previousPeriod;
	}
	
	
	public SummaryProviderParameters getPreviousParams(String accountExp) {
		getPreviousParams().setAccountExpression(accountExp);
		return getPreviousParams();
	}
	public SummaryProviderParameters getParams(String accountExp) {
		getParams().setAccountExpression(accountExp);
		return getParams();
	}
	
}
