package com.esferalia.aon.payroll.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.ContractClause;

/**
 * 
 */
public class ContractClauseBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		ContractClause clause = (ContractClause)event.getTo();
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		if (clause.getContract()!=null && clause.getContract().getId()!=null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), clause.getContract().getId());
		} else {
			criteria.addNullExpression("ContractClause.contract");
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), clause.isGeneral());
		}
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_ID), clause.getId());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE), clause.getLine());
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		List<ITransferObject> list = bean.getList(criteria);
		int index = clause.getLine();
		for (ITransferObject to : list) {
			ContractClause contractClause = (ContractClause)to;
			if (index == contractClause.getLine()) {
				contractClause.setLine(index + 1);
				bean.update(contractClause);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			ContractClause clause = (ContractClause)event.getTo();
			IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
			Criteria criteria = new Criteria();
			if (clause.getContract()!=null && clause.getContract().getId()!=null) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), clause.getContract().getId());
			} else {
				criteria.addNullExpression("ContractClause.contract");
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), clause.isGeneral());
			}
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_ID), clause.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE), clause.getLine());
			if (bean.getCount(criteria) > 0) {
				criteria = new Criteria();
				if (clause.getContract()!=null && clause.getContract().getId()!=null) {
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), clause.getContract().getId());
				} else {
					criteria.addNullExpression("ContractClause.contract");
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), clause.isGeneral());
				}
				criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_ID), clause.getId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
				List<ITransferObject> list = bean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					ContractClause contractClause = (ContractClause)to;
					if (index == clause.getLine()) {
						++index;
					}
					contractClause.setLine(index);
					bean.update(contractClause);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		ContractClause clause = (ContractClause)event.getTo();
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		if (clause.getContract()!=null && clause.getContract().getId()!=null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), clause.getContract().getId());
		} else {
			criteria.addNullExpression("ContractClause.contract");
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), clause.isGeneral());
		}
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_ID), clause.getId());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE), clause.getLine());
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		List<ITransferObject> list = bean.getList(criteria);
		int index = clause.getLine() + 1;
		for (ITransferObject to : list) {
			ContractClause contractClause = (ContractClause)to;
			if (index == contractClause.getLine()) {
				contractClause.setLine(index - 1);
				bean.update(contractClause);
				++ index;
			}
		}
	}

}
