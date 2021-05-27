package com.esferalia.aon.pms.event;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProductBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Product product = (Product)evt.getTo();
    	try {
    		if (isVatChanged(product)) {
    			IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
    			Criteria criteria = new Criteria();
    			String alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_ID);
    			criteria.addEqualExpression(alias, product.getId());
    			alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_REMOVED);
    			criteria.addEqualExpression(alias, Boolean.FALSE);
    			alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_STATUS);
    			criteria.addNotEqualExpression(alias, ReservationStatus.INVOICED);
    			criteria.addNotEqualExpression(alias, ReservationStatus.CANCELLED);
    			if (reservationServiceBean.getCount(criteria) > 0) {
    				throw new ManagerBeanVetoListenerException("No se puede modificar el IVA. El Servicio está asociado a una Reserva.");
    			}
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}	

	private boolean isVatChanged(Product product) {
		String stmt = "SELECT 1" +
						" FROM product as product" +
    					" WHERE product.id = :id" +
    					" AND product.vat = :vat";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("id", product.getId());
		query.setInteger("vat", product.getVat().getId());
        return query.list().isEmpty();
	}

}
