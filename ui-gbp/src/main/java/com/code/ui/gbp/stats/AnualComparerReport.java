package com.code.ui.gbp.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class AnualComparerReport implements ICollectionProvider {

	private Integer init;

	private Integer end;

	
	public Integer getInit() {
		return init;
	}

	public void setInit(Integer init) {
		this.init = init;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		return getAnualComparerCollection();
	}

	private List<AnualComparer> getAnualComparerCollection(){
		List<AnualComparer> list = new ArrayList<AnualComparer>();
		list.add(getAnualComparer(true));
		list.add(getAnualComparer(false));
		return list;
	}

	private AnualComparer getAnualComparer(boolean prev){
		Session s = HibernateUtil.getSession();
		String stmt1 = "SELECT COUNT(*) FROM Campaign c WHERE c.offerDueDate >= ? AND c.offerDueDate <= ?";
		String subStmt = "SELECT c.id FROM  WHERE c.offerDueDate >= ? AND c.offerDueDate <= ?";
		String stmt2 = "SELECT SUM(off.price) FROM Campaign c, Offer off " +
				" WHERE c.offerDueDate >= ? AND c.offerDueDate <= ?" +
				" AND off.campaign.id = c.id ";
		String stmt3 = "SELECT SUM(inv.amount) FROM ProFormaInvoice inv, Campaign c " +
				" WHERE c.offerDueDate >= ? AND c.offerDueDate <= ? " +
				" AND inv.campaign.id = c.id ";
		
		Query query = s.createQuery(stmt1.toString());
		GregorianCalendar current = new GregorianCalendar();
		GregorianCalendar initDate;
		if (prev)
			initDate = new GregorianCalendar(current.get(Calendar.YEAR)-1,init-1,1);
		else
			initDate = new GregorianCalendar(current.get(Calendar.YEAR),init-1,1);
		GregorianCalendar endDate;
		if (prev)
			endDate= new GregorianCalendar(current.get(Calendar.YEAR)-1,end-1,CalendarUtils.diasDelMes(current.get(Calendar.YEAR)-1,end-1));
		else
			endDate = new GregorianCalendar(current.get(Calendar.YEAR),end-1,CalendarUtils.diasDelMes(current.get(Calendar.YEAR),end-1));
		query.setDate(0, initDate.getTime());
		query.setDate(1, endDate.getTime());
		List<Long> list_1 = query.list();
		
		query = s.createQuery(stmt2.toString());
		query.setDate(0, initDate.getTime());
		query.setDate(1, endDate.getTime());
		List<BigDecimal> list_2 = query.list();

		query = s.createQuery(stmt3.toString());
		query.setDate(0, initDate.getTime());
		query.setDate(1, endDate.getTime());
		List<BigDecimal> list_3 = query.list();
		
		AnualComparer ac = new AnualComparer(initDate,endDate,list_1.get(0),list_2.get(0),list_3.get(0));
		return ac;
	}
	
	public void onInitialize(ActionEvent event) {
		setInit(null);
		setEnd(null);
	}
	
	public static void main(String[] args) {
		AnualComparerReport rep = new AnualComparerReport();
		rep.setInit(new Integer(1));
		rep.setEnd(new Integer(12));
		System.out.println(rep.getCollection().size());
		System.out.println("->");		
	}

}
