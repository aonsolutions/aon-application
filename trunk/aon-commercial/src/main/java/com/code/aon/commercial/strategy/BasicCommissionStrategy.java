package com.code.aon.commercial.strategy;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.CommissionCategory;
import com.code.aon.commercial.CommissionItem;
import com.code.aon.commercial.CommissionTypeCommission;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.CommissionType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

public class BasicCommissionStrategy implements ICommissionStrategy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicCommissionStrategy.class.getName());

	@SuppressWarnings("unchecked")
	public double getCommission(OfferDetail calc) {
		double commission = 0;
		CommissionType commissionType = (calc.getOffer().getSeller() != null) ? calc.getOffer().getSeller().getCommissionType() : null;
		try {
			if (commissionType != null && commissionType.getId() != null) {
				IManagerBean bean = BeanManager.getManagerBean(CommissionTypeCommission.class);
				IManagerBean commissionItemBean = BeanManager.getManagerBean(CommissionItem.class);
				IManagerBean commissionCategoryBean = BeanManager.getManagerBean(CommissionCategory.class);

				commission = commissionType.getRate();
				Date date = calc.getOffer().getIssueDate();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICommercialAlias.COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE_ID), commissionType.getId());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(ICommercialAlias.COMMISSION_TYPE_COMMISSION_COMMISSION_START_DATE), date);
				Expression dateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(ICommercialAlias.COMMISSION_TYPE_COMMISSION_COMMISSION_END_DATE), date);
				Expression nullExpr = ExpressionUtilities.getNullExpression(bean.getFieldName(ICommercialAlias.COMMISSION_TYPE_COMMISSION_COMMISSION_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(dateExpr, nullExpr));
				criteria.addOrder(bean.getFieldName(ICommercialAlias.COMMISSION_TYPE_COMMISSION_COMMISSION_START_DATE), false);
				Iterator iterator = bean.getList(criteria).iterator();
				while (iterator.hasNext()) {
					CommissionTypeCommission to = (CommissionTypeCommission)iterator.next();

					criteria = new Criteria();
					criteria.addEqualExpression(commissionItemBean.getFieldName(ICommercialAlias.COMMISSION_ITEM_COMMISSION_ID), to.getCommission().getId());
					criteria.addEqualExpression(commissionItemBean.getFieldName(ICommercialAlias.COMMISSION_ITEM_ITEM_ID), calc.getItem().getId());
					criteria.addLessThanOrEqualExpression(commissionItemBean.getFieldName(ICommercialAlias.COMMISSION_ITEM_QUANTITY), calc.getQuantity());
					criteria.addOrder(commissionItemBean.getFieldName(ICommercialAlias.COMMISSION_ITEM_QUANTITY), false);
					Iterator itemIterator = commissionItemBean.getList(criteria, 0, 1).iterator();
					if (itemIterator.hasNext()) {
						CommissionItem commissionItem = (CommissionItem)itemIterator.next();
						return commissionItem.getRate();
					}

					criteria = new Criteria();
					criteria.addEqualExpression(commissionCategoryBean.getFieldName(ICommercialAlias.COMMISSION_CATEGORY_COMMISSION_ID), to.getCommission().getId());
					criteria.addEqualExpression(commissionCategoryBean.getFieldName(ICommercialAlias.COMMISSION_CATEGORY_CATEGORY_ID), calc.getItem().getProduct().getCategory().getId());
					criteria.addLessThanOrEqualExpression(commissionCategoryBean.getFieldName(ICommercialAlias.COMMISSION_CATEGORY_QUANTITY), calc.getQuantity());
					criteria.addOrder(commissionCategoryBean.getFieldName(ICommercialAlias.COMMISSION_CATEGORY_QUANTITY), false);
					Iterator categoryIterator = commissionCategoryBean.getList(criteria, 0, 1).iterator();
					if (categoryIterator.hasNext()) {
						CommissionCategory commissionCategory = (CommissionCategory)categoryIterator.next();
						return commissionCategory.getRate();
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining commission for type = " + commissionType.getName(), e);
		}
		return commission;
	}
	
}
