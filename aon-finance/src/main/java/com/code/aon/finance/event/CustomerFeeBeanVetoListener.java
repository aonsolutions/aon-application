package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.util.DiscountExpression;

public class CustomerFeeBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	CustomerFee to = (CustomerFee)evt.getTo();
		if (to.getDiscountExpression() == null || StringUtils.isBlank(to.getDiscountExpression().getDiscountExpr())) {
			to.setDiscountExpression(new DiscountExpression("0.0"));
		}
    	if (to.getSecurityLevel() == null) {
    		to.setSecurityLevel(SecurityLevel.OFFICIAL);
    	}
    }

}
