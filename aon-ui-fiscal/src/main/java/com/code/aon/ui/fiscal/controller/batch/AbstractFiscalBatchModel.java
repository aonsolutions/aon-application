package com.code.aon.ui.fiscal.controller.batch;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class AbstractFiscalBatchModel implements IFiscalBatchModel {

	protected static final String DOMAIN = "domain";
	protected static final String ID = "id";
	protected static final String YEAR = "year";
	protected static final String PERIOD = "period";
	protected static final String REPLACEMENT = "replacement";
	protected static final String COMPLEMENTARY = "complementary";
	protected static final String COMPANY = "company";
	protected static final String RESULT = "result";
	protected static final String DESCRIPTION = "description";


	@Override
	public List<Batchable> getDetailsList(FiscalBatch fiscalBatch) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_BATCH_DETAIL_FISCAL_BATCH_ID), fiscalBatch.getId());
		List<ITransferObject> list  = bean.getList(criteria);
		List<Batchable> details = new LinkedList<Batchable>(); 
		for (ITransferObject to : list) {
			FiscalBatchDetail detail = (FiscalBatchDetail) to;
			Batchable b = new Batchable();
			b.setId(detail.getId());
			b.setDomain(detail.getChildDomain());
			b.setCompany(detail.getCompany());
			b.setResult(detail.getResult());
			b.setComplementary(detail.isComplementary());
			b.setReplacement(detail.isReplacement());
			b.setDescription(detail.getDescription());
			details.add(b);
		}
		return details;
	}

}
