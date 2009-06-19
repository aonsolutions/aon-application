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

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class DivisaController extends LinesController {



	
/*	
	public String getnombreDivisa() {
		
		IManagerBean bean = BeanManager.getManagerBean(Divisa.class);
		
		
            Criteria c = new Criteria();
            String alias = bean.getFieldName(IPayrollAlias.LIN_DIVISA_ID_DIVISA_FINAL);
            c.addEqualExpression(alias, this.getTo());
             List<ITransferObject> list = bean.getList(c);
			
		return String name;
                     
	
	}
	*/
	
	
	
}
