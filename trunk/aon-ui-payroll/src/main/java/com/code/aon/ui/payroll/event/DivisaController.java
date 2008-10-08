package com.code.aon.ui.payroll.event;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class DivisaController extends LinesController {

	@Override
	public void onAccept(ActionEvent event) {
		boolean bol = isNew();
		super.onAccept(event);
		if ( bol )
			super.onReset( event );
	}



}
