package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;

public class ImpNominaWizard implements Serializable, ICollectionProvider {

	private static final long serialVersionUID = -5289046969801597373L;

	private NominaParams params;
	private INominaDAO nominaDAO;
	private List<INomina> list;

	public List<INomina> getList() {
		return list;
	}

	public void setList(List<INomina> list) {
		this.list = list;
	}

	public INominaDAO getNominaDAO() {
		if (nominaDAO == null) {
			NominaDAOFactory f = NominaDAOFactory.getInstance();
			nominaDAO = f.getNominaDAO();
		}
		return nominaDAO;
	}

	public NominaParams getParams() {
		return params;
	}

	public void setParams(NominaParams params) {
		this.params = params;
	}

	public void onStart(ActionEvent event) {
		list = null;
		params = new NominaParams();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		params.setYear(c.get(Calendar.YEAR));
		params.setMes(c.get(Calendar.MONTH) + 1);
		params.setOffset(0);
		params.setCount(10);
	}

	public void onSearch(ActionEvent event) {
		try {
			setList(getNominaDAO().getNominas(getParams()));
		} catch (PayrollException e) {
			String message = e.getMessage();
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		return getList();
	}

}
