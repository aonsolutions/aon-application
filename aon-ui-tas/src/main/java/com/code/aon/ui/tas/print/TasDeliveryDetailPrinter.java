package com.code.aon.ui.tas.print;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class TasDeliveryDetailPrinter {

	private static final Logger LOGGER = Logger.getLogger(TasDeliveryDetailPrinter.class.getName());

	public TasDeliveryDetailPrinter getInstance() {
		return new TasDeliveryDetailPrinter();
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(Integer deliveryId) {
		List<DeliveryDetail> deliveryDetailList = new LinkedList<DeliveryDetail>();
		Map<Integer, List<DeliveryDetail>> projectMap = new HashMap<Integer, List<DeliveryDetail>>();
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), deliveryId);
			criteria.addOrder(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ITEM_PRODUCT_TYPE));
			criteria.addOrder(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
			for (ITransferObject ito : deliveryDetailBean.getList(criteria)) {
				DeliveryDetail deliveryDetail = (DeliveryDetail)ito;
				Project project = deliveryDetail.getDelivery().getProject();
				if (project == null) {
					deliveryDetailList.add(deliveryDetail);
				} else {
					if (!projectMap.containsKey(project.getId())) {
						projectMap.put(project.getId(), new LinkedList<DeliveryDetail>());
					}
					projectMap.get(project.getId()).add(deliveryDetail);
				}
			}

			if (projectMap.size() > 0) {
				deliveryDetailList.addAll(getListOrdered(projectMap));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining deliveryDetailList Collection", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining deliveryDetailList Collection", e);
		}
		return deliveryDetailList;
	}
	
	protected List<DeliveryDetail> getListOrdered(Map<Integer, List<DeliveryDetail>> projectMap) throws ManagerBeanException, ExpressionException {
		List<DeliveryDetail> deliveryDetailList = new LinkedList<DeliveryDetail>();
		IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		for (Integer projectId : projectMap.keySet()) {
			criteria.addOrExpression(projectBean.getFieldName(IProjectAlias.PROJECT_ID), projectId.toString());
		}
		for (ITransferObject ito : projectBean.getList(criteria)) {
			deliveryDetailList.addAll(projectMap.get(((Project)ito).getId()));
		}

		return deliveryDetailList;
	}

}