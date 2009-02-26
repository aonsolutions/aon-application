package com.code.aon.ui.finance.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class InvoicingGroupDetailControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(InvoicingGroupDetailControllerListener.class.getName());

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoicingGroupDetail invoicingGroupDetail = (InvoicingGroupDetail)event.getController().getTo();
		checkInvoicingGroup(invoicingGroupDetail.getChild());
		checkInvoicingGroupDetail(invoicingGroupDetail);
	}

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoicingGroupDetail invoicingGroupDetail = (InvoicingGroupDetail)event.getController().getTo();
		checkInvoicingGroup(invoicingGroupDetail.getChild());
		checkInvoicingGroupDetail(invoicingGroupDetail);
    }

    private void checkInvoicingGroup(Registry registry) throws ControllerListenerException {
    	try {
        	IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(invoicingGroupBean.getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID), registry.getId());
        	if (invoicingGroupBean.getCount(criteria) > 0) {
        		String message = AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.INVALID_INVOICING_GROUP_CHILD_KEY);
        		throw new ControllerListenerException(message);
        	}
    	} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining InvoicingGroup with parent=" + registry.getId(), e);
    	}
    }

    private void checkInvoicingGroupDetail(InvoicingGroupDetail detail) throws ControllerListenerException {
    	try {
        	IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_CHILD_ID), detail.getChild().getId());
        	if (detail.getId() != null) {
            	criteria.addExpression(ExpressionUtilities.getNotEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_ID), detail.getId()));
        	}
        	if (invoicingGroupDetailBean.getCount(criteria) > 0) {
        		String message = AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.INVALID_INVOICING_GROUP_DETAIL_CHILD_KEY);
        		throw new ControllerListenerException(message);
        	}
    	} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining InvoicingGroupDetail with child=" + detail.getChild().getId(), e);
    	}
    }

}