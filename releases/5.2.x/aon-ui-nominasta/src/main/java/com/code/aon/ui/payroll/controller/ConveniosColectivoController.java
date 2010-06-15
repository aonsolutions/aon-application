package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.IndicadorDias;
import com.code.aon.payroll.enumeration.TipoConvenio;

public class ConveniosColectivoController extends PayrollBasicController {

	private List<SelectItem> indicadorDia;
	private List<SelectItem> tipoCon;
	private ConveniosColectivoController convenioPrint;

	
	/**
	 * Recupera el listado de indices de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaIndicadorDias() {
		if(indicadorDia==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicadorDia = new LinkedList<SelectItem>();
			for (IndicadorDias id : IndicadorDias.values()) {
				String name = id.getName( locale );
				SelectItem item = new SelectItem( id, name );
				indicadorDia.add(item);
			}
		}
		return indicadorDia;
	}
	
	/**
	 * Recupera el listado de indices de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipoConvenios() {
		if(tipoCon==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoCon = new LinkedList<SelectItem>();
			for (TipoConvenio tc : TipoConvenio.values()) {
				String name = tc.getName( locale );
				SelectItem item = new SelectItem( tc, name );
				tipoCon.add(item);
			}
		}
		return tipoCon;
	}
	
	private String tabName;

	/**
	 * Devuelve el tab seleccionado
	 * @return
	 */
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	public void changeTabValue(ValueChangeEvent event){
		setTabName(event.getNewValue().toString());
	}

	public ConveniosColectivoController getConvenioPrint() {
		return convenioPrint;
	}

	public void setConvenioPrint(ConveniosColectivoController convenioPrint) {
		this.convenioPrint = convenioPrint;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelect(event);
		if(getTo()!=null){
			try {
				String id = BeanManager.getManagerBean(Convenio.class).getFieldName(IPayrollAlias.CONVENIO_CDG);
				String cdg = ((Convenio)getTo()).getCdg();
				convenioPrint = new ConveniosColectivoController();
				convenioPrint = this;
				convenioPrint.clearCriteria();
				convenioPrint.getCriteria().addEqualExpression(id, cdg);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		// TODO Auto-generated method stub
		try {
			this.clearCriteria();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}
	
}
