package com.code.aon.ui.payroll.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.faces.event.ActionEvent;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class DivisaController extends LinesController {

LinDivisa d;
	
	@Override
	public void onAccept(ActionEvent event) {
	
	 
		
		System.out.println("111111111111111111111111111");
		
	    /*if  (((LinDivisa)this.getTo()).getId().getDivisaFinal().isEmpty())  {
		
				
		System.out.println("222222222222222222222222");	
		LinDivisa Div = (LinDivisa)event.getController().getTo();
		((LinDivisa)this.getTo()).setId();  setId().setId(setDivisaFinal((Divisa)this.getTo()).getCdg()));
		}
		DivisaMaestroController controller = (DivisaMaestroController)AonUtil.getController("divisaMaestro");
		LinDivisa Div = (LinDivisa)getTo();		
		Div.getId().setDivisaFinal(controller.getMaestro());
		System.out.println(" fffffffffffffffff               " + controller.getMaestro());
		
		System.out.println(Div.getId().getDivisaFinal());
		System.out.println(Div.getId().getCdg());
		System.out.println(Div.getId().getFecini());
		System.out.println(Div.getImporte());
		System.out.println(Div.getUnidades());
		System.out.println("111111111111111111111111111");
			
		*/
		super.onAccept(event);
	}

	public LinDivisa getD() {
		return d;
	}

	public void setD(LinDivisa d) {
		this.d = d;
	}

	
	
	
}
