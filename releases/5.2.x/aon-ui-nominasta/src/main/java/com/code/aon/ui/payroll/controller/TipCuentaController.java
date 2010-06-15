package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.enumeration.Tipcuenta;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.ui.form.LinesController;

public class TipCuentaController extends LinesController {


	private List<ITransferObject> tiposcta;
	private Tipcuenta typeChanged;
	private List<SelectItem> listatiposcuentas;

	
	public void loadTypes()
	throws ManagerBeanException {
		
        IManagerBean bean = BeanManager.getManagerBean(Emprdom.class);
        List<ITransferObject> list = bean.getList(null);
        tiposcta =list;			
}
	
	
	
	public List<SelectItem> getListaTipos() {
		if(listatiposcuentas==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listatiposcuentas = new LinkedList<SelectItem>();
			for (Tipcuenta e : Tipcuenta.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listatiposcuentas.add(item);
			}
		}
		return listatiposcuentas;
	}
	
	Integer code ;
    public Integer getCode() throws ManagerBeanException {	    	
	       	code = 0;		
			String consulta = "select max(cdg) from Emprlban";			
			Query q = HibernateUtil.getSession().createQuery(consulta);			
			List results = q.list();
			System.out.println("Max Code: " + results.get(0));   
			code= (Integer)results.get(0) +1;
			System.out.println("New Code: " + code);
			return code;		      
	     				
		}
    
    
	public void setCode(Integer code) {
		this.code = code;
	}


	public Tipcuenta getTypeChanged() {
		return typeChanged;
	}


	public void setTypeChanged(Tipcuenta typeChanged) {
		this.typeChanged = typeChanged;
	}



	public List<ITransferObject> getTiposcta() {
		return tiposcta;
	}



	public void setTiposcta(List<ITransferObject> tiposcta) {
		this.tiposcta = tiposcta;
	}


	



	

}
