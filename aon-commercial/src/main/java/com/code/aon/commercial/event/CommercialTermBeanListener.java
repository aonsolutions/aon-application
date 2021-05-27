package com.code.aon.commercial.event;

import java.util.List;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author Consulting & Development
 * 
 */
public class CommercialTermBeanListener extends ManagerBeanListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		CommercialTerm term = (CommercialTerm)event.getTo();
		IManagerBean termBean = BeanManager.getManagerBean(CommercialTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), term.isGeneral());
		criteria.addNotEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_ID), term.getId());
		criteria.addGreaterThanOrEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE), term.getLine());
		criteria.addOrder(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
		List<ITransferObject> list = termBean.getList(criteria);
		int index = term.getLine();
		for (ITransferObject to : list) {
			CommercialTerm commercialTerm = (CommercialTerm)to;
			if (index == commercialTerm.getLine()) {
				commercialTerm.setLine(index + 1);
				termBean.update(commercialTerm);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			CommercialTerm term = (CommercialTerm)event.getTo();
			IManagerBean termBean = BeanManager.getManagerBean(CommercialTerm.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), term.isGeneral());
			criteria.addNotEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_ID), term.getId());
			criteria.addEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE), term.getLine());
			if (termBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), term.isGeneral());
				criteria.addNotEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_ID), term.getId());
				criteria.addOrder(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
				List<ITransferObject> list = termBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					CommercialTerm commercialTerm = (CommercialTerm)to;
					if (index == term.getLine()) {
						++index;
					}
					commercialTerm.setLine(index);
					termBean.update(commercialTerm);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		CommercialTerm term = (CommercialTerm)event.getTo();
		IManagerBean termBean = BeanManager.getManagerBean(CommercialTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), term.isGeneral());
		criteria.addNotEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_ID), term.getId());
		criteria.addGreaterThanOrEqualExpression(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE), term.getLine());
		criteria.addOrder(termBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
		List<ITransferObject> list = termBean.getList(criteria);
		int index = term.getLine() + 1;
		for (ITransferObject to : list) {
			CommercialTerm commercialTerm = (CommercialTerm)to;
			if (index == commercialTerm.getLine()) {
				commercialTerm.setLine(index - 1);
				termBean.update(commercialTerm);
				++ index;
			}
		}
	}

}
