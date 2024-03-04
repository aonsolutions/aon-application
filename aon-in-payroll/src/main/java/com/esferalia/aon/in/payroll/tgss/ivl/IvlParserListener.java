package com.esferalia.aon.in.payroll.tgss.ivl;

import java.util.Date;

public interface IvlParserListener {

    default void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
	    String docType, String docNumber, String enterpriseAddress, String enterpriseCity, String enterpriseCP,
	    String enterpriseCNAENumber, String enterpriseCNAEDescription) {
    }

    default void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
	    String employeeName) {

    }

    default void onEmployeeContract(Date realStartDate, Date efectiveStartDate, Date realEndDate, Date efectiveEndDate,
	    String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims, Integer quoteDays) {

    }
}
