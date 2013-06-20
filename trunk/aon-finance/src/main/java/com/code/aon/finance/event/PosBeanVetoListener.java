package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Pos;

public class PosBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Pos to = (Pos)evt.getTo();
    	if (StringUtils.isEmpty(to.getSeries())) {
    		to.setSeries(null);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Pos to = (Pos)evt.getTo();
    	if (StringUtils.isEmpty(to.getSeries())) {
    		to.setSeries(null);
    	}
    }

}
