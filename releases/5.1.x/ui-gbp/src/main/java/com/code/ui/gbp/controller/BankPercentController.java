package com.code.ui.gbp.controller;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.BankPercent;

public class BankPercentController extends BasicController {
	
	public boolean isCompleted(){
        if (getSumOfPercents()==100){
        	return true;
        }
        return false;
	}
	
	public double getSumOfPercents(){
		BankPercent to = (BankPercent)getTo();
		Session session;
        session = HibernateUtil.getSession();
        String stmt = "SELECT SUM (bp.distributionPercent) " +
			" FROM BankPercent bp ";
        if (to.getId()!=null)
        	stmt += " WHERE bp.id <> "+to.getId();
        Query query = session.createQuery(stmt);
        List l = query.list();
        double sum = 0;
        if (!l.isEmpty() && (l.get(0)!=null)){
        	sum = ((Double)l.get(0)).doubleValue();
		}
        double added = to.getDistributionPercent();
		return (sum+added);
	}
	
}