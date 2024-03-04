package com.esferalia.aon.in.payroll.tgss.ivl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

class IvlCompositeListener implements IvlParserListener {
    
    private Collection<IvlParserListener> listeners ;
    
    public IvlCompositeListener() {
	this.listeners = new ArrayList<>();
    }
    
    public IvlCompositeListener add(IvlParserListener ...listeners) {
	Arrays.stream(listeners).forEach(this.listeners::add);
	return this;
    }


    @Override
    public void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
            String docType, String docNumber, String enterpriseAddress, String enterpriseCity, String enterpriseCP,
            String enterpriseCNAENumber, String enterpriseCNAEDescription) {
	listeners.forEach(l -> l.onEnterprise(enterpriseName, cccRegime, cccProvince, cccNumber, docType, docNumber, enterpriseAddress, enterpriseCity, enterpriseCP, enterpriseCNAENumber, enterpriseCNAEDescription));
    }
    
    @Override
    public void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
            String employeeName) {
	listeners.forEach(l -> l.onEmployee(nafProvince, nafNumber, docType, docNumber, employeeName));
    }
    
    @Override
    public void onEmployeeContract(Date realStartDate, Date efectiveStartDate, Date realEndDate, Date efectiveEndDate,
            String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims,
            Integer quoteDays) {
	listeners.forEach(l -> l.onEmployeeContract(realStartDate, efectiveStartDate, realEndDate, efectiveEndDate, quoteGroup, monthly, tc2, partialFactor, it, ims, quoteDays));
    }
}
