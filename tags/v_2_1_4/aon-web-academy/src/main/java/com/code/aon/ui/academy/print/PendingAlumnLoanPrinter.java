package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.AlumnLoan;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class PendingAlumnLoanPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(PendingAlumnLoanPrinter.class.getName());
	
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ITransferObject> reportList = new LinkedList<ITransferObject>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(AlumnLoan.class);
			Criteria criteria = new Criteria();
			criteria.addNullExpression(bean.getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.DATE, -15);
			criteria.addLessThanExpression(bean.getFieldName(IAcademyAlias.ALUMN_LOAN_LOAN_DATE), calendar.getTime());
			reportList = bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining pendant loan Collection", e);
		}
		return reportList;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
}