package com.code.ui.gbp.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.form.LinesController;
import com.code.gbp.BankPercent;
import com.code.gbp.ProFormaBank;
import com.code.gbp.ProFormaInvoice;

public class ProFormaBankController extends LinesController {
	
	public void onDefault(ActionEvent event) throws ManagerBeanException{
		ProFormaInvoice proFormaInvoice = (ProFormaInvoice)getMasterController().getTo();
		
		IManagerBean bean = BeanManager.getManagerBean(ProFormaBank.class);
		Iterator<ITransferObject> iter = bean.getList(getCriteria()).iterator();
		while (iter.hasNext()){
			bean.remove(iter.next());
		}
		
		IManagerBean bankPercentBean = BeanManager.getManagerBean(BankPercent.class);
		iter = bankPercentBean.getList(null).iterator();
		while(iter.hasNext()){
			BankPercent bp = (BankPercent)iter.next();
			ProFormaBank object = new ProFormaBank();
			object.setProFormaInvoice(proFormaInvoice);
			object.setBank(bp.getBank());
			object.setPercent(bp.getDistributionPercent());
			bean.insert(object);
		}
		
		onSearch(event);
	}
	
	public boolean isCompleted(){
        if (getSumOfPercents()==100){
        	return true;
        }
        return false;
	}
	
	public double getSumOfPercents(){
		ProFormaBank to = (ProFormaBank)getTo();
		Session session;
        session = HibernateUtil.getSession();
        String stmt = "SELECT SUM (pfb.percent) " +
        		" FROM ProFormaBank pfb " +
        		" WHERE pfb.proFormaInvoice = "+to.getProFormaInvoice().getId();
        if (to.getId()!=null)
        	stmt += " AND pfb.id <> "+to.getId();
        Query query = session.createQuery(stmt);
        List l = query.list();
        double sum = 0;
        if (!l.isEmpty() && (l.get(0)!=null)){
        	sum = ((Double)l.get(0)).doubleValue();
		}
        double added = to.getPercent();
		return (sum+added);
	}
	
}