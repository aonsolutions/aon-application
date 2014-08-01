package com.code.aon.purchase.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Purchase purchase = (Purchase) evt.getTo();

		Project project = (purchase.getProject() != null && purchase.getProject().getId() != null) ? purchase.getProject() : null;
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		for (ITransferObject ito : purchaseDetailBean.getList(criteria)) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
			if (purchaseDetail.getProject() == null || purchaseDetail.getProject().getId() == null) {
				purchaseDetail.setProject(project);
				purchaseDetailBean.update(purchaseDetail);
			}
		}
	}

}
