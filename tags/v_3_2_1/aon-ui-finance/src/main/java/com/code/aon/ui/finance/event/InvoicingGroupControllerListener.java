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
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class InvoicingGroupControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(InvoicingGroupControllerListener.class.getName());

	private final static String BUNDLE_KEY = "financeBundle";

	private final static String INVALID_INVOICING_GROUP_KEY = "finance_invalid_invoicing_group_parent";
	private final static String INVALID_INVOICING_GROUP_DETAIL_KEY = "finance_invalid_invoicing_group_detail_parent";
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoicingGroup invoicingGroup = (InvoicingGroup)event.getController().getTo();
		checkInvoicingGroup(invoicingGroup);
		checkInvoicingGroupDetail(invoicingGroup.getParent());
	}

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoicingGroup invoicingGroup = (InvoicingGroup)event.getController().getTo();
    	checkInvoicingGroup(invoicingGroup);
		checkInvoicingGroupDetail(invoicingGroup.getParent());
    }

    private void checkInvoicingGroup(InvoicingGroup group) throws ControllerListenerException {
    	try {
        	IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(invoicingGroupBean.getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID), group.getParent().getId());
        	if (group.getId() != null) {
            	criteria.addExpression(ExpressionUtilities.getNotEqualExpression(invoicingGroupBean.getFieldName(IFinanceAlias.INVOICING_GROUP_ID), group.getId()));
        	}
        	if (invoicingGroupBean.getCount(criteria) > 0) {
        		throw new ControllerListenerException(AonUtil.addInfoMessageFromBundle(BUNDLE_KEY, INVALID_INVOICING_GROUP_KEY));
        	}
    	} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining InvoicingGroup with parent=" + group.getParent().getId(), e);
    	}
    }

    private void checkInvoicingGroupDetail(Registry registry) throws ControllerListenerException {
    	try {
        	IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_CHILD_ID), registry.getId());
        	if (invoicingGroupDetailBean.getCount(criteria) > 0) {
        		throw new ControllerListenerException(AonUtil.addInfoMessageFromBundle(BUNDLE_KEY, INVALID_INVOICING_GROUP_DETAIL_KEY));
        	}
    	} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining InvoicingGroupDetail with child=" + registry.getId(), e);
    	}
    }

}