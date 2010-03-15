package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.auxiliares.convenios.Percniv;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.PagaExtra;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class PercnivLinesController extends LinesController implements IPayrollConstants{

	private List<SelectItem> pagas;
	private Complemento complemento;
	
	public Complemento getComplemento() {
		return complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);
		setComplemento( new Complemento() );
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if( (complemento != null) && (! StringUtils.isEmpty(complemento.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCNIV_COMPLEMENTO_CDG), getComplemento().getCdg());
			}
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}

	/**
	 * Recupera los tipos de redondeo de pagas extra 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaPagaExtra() {
		if(pagas==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			pagas = new LinkedList<SelectItem>();
			for (PagaExtra paga : PagaExtra.values()) {
				String name = paga.getName( locale );
				SelectItem item = new SelectItem( paga, name );
				pagas.add(item);
			}
		}
		return pagas;
	}
	
	
	public void impuniChange(ValueChangeEvent event){
		
		Percniv p = (Percniv)FormUtil.getController(IPayrollConstants.PERCNIV_CONTROLLER_NAME).getTo();
		
		p.setImporte(((BigDecimal)event.getNewValue()).multiply(p.getUnidades()));
		
	}

}
