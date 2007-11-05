package com.code.aon.ui.tasCommercial.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tasCommercial.TasOffer;
import com.code.aon.tasCommercial.dao.ITasCommercialAlias;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used to manage <code>TasOffer</code> class
 */
public class TasOfferController extends BasicController {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TasOfferController.class.getName());
	
	/** OfferController name. */
	private static final String OFFER_CONTROLLER_NAME = "offer";
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		OfferController offerController = (OfferController)AonUtil.getController(OFFER_CONTROLLER_NAME);
		Offer offer = (Offer)offerController.getTo();
		try {
			IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_OFFER_ID), offer.getId());
			Iterator iter = tasOfferBean.getList(criteria).iterator();
			if(iter.hasNext()){
				List<TasOffer> l = new LinkedList<TasOffer>();
				l.add((TasOffer)iter.next());
				return l;
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining collection for offer with id=" + offer.getId(), e);
		}
		return super.getCollection();
	}
	
	
    /**
     * Gets some data as a String of the supportOrder related with the current offer.
     * 
     * @return the support order data
     * 
     * @throws ManagerBeanException the manager bean exception
     */
	@SuppressWarnings("unchecked")
    public String getSupportOrderData() throws ManagerBeanException{
        OfferController offerController = (OfferController)AonUtil.getController(OFFER_CONTROLLER_NAME);
        IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), offerController.getSupportOrderId());
        Iterator iter = supportOrderBean.getList(criteria).iterator();
        String data = new String();
        if(iter.hasNext()){
            SupportOrder supportOrder = (SupportOrder)iter.next();
            data = supportOrder.getTasItem().getPublicCode() + " / " + supportOrder.getTasItem().getModel().getMake().getName()+ " " + supportOrder.getTasItem().getModel().getName();
            offerController.setDescription(supportOrder.getDescription());
        }
        return data;
    }
}