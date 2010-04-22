package com.code.aon.ui.payroll.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.ui.form.LinesController;

public class CuentaController extends LinesController {

	
	
private Cliente cliente;
private List<SelectItem> entidades;
private List<SelectItem> sucursales;



	@Override
   public void onEditSearch(ActionEvent arg0) {
	   super.onEditSearch(arg0);
	   setCliente( new Cliente() );


}
	
	
	@Override
	public void onSearch(ActionEvent event) {
		System.out.println("----------c"+cliente.getCdg());
	
		try {
			if  (cliente.getCdg() != null)  {
				
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CUENTAS_CLIENTE_CDG), getCliente().getCdg());
				System.out.println("----------c"+cliente.getCdg());
			}
		
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println("----------c"+cliente.getCdg());
		
		super.onSearch(event);
	}



	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	Integer code ;
    public Integer getCode() throws ManagerBeanException {	
    	
    	
	       	code = 0;		
			String consulta = "select max(cdg) from Cuentas";			
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

	
/*	  
	public List<SelectItem> getListaEntidades() throws ManagerBeanException  {
		if(entidades==null){
			
			entidades = new LinkedList<SelectItem>();
		Controller controller = FormUtil.getController(IPayrollConstants.ENTIDAD_CONTROLLER_NAME);
		List<ITransferObject> empresa  =  controller.getManagerBean().getList(controller.getCriteria());
		for (ITransferObject to: empresa) {
			Empresa e = (Empresa) to;	
			SelectItem item = new SelectItem(e.getCdg(),""+e.getCdg()+" "+e.getDescripcion());
			entidades.add(item);
		}
		}
		
		return entidades;
	}	
	
	
	public void refreshListaAct(ValueChangeEvent event) throws ManagerBeanException  {
		
		Integer newCodEmpresa = (Integer) event.getNewValue();	
		
		if (newCodEmpresa != null) {
			IManagerBean bean = BeanManager.getManagerBean(Actividad.class);
			String emp = bean.getFieldName(IPayrollAlias.ACTIVIDAD_EMPRESA_CDG);
			Criteria cri1 = new Criteria();
			cri1.addEqualExpression(emp, newCodEmpresa);
			List<ITransferObject> filtroact;
			filtroact = bean.getList(cri1);
			sucursales.clear();
			sucursales.clear();
			for (ITransferObject to : filtroact) {
				Actividad a = (Actividad) to;
				SelectItem item = new SelectItem(a.getCdg(),""+a.getCdg()+": "+a.getDescripcion());
				sucursales.add(item);
			}
		
		}        
	}*/
	
}


