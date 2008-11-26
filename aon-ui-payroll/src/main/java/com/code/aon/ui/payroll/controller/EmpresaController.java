package com.code.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.ConciertoEconomico;
import com.code.aon.payroll.enumeration.EnvioSS;
import com.code.aon.payroll.enumeration.IndicadorIrpf;
import com.code.aon.payroll.enumeration.PagoImpuestos;
import com.code.aon.payroll.enumeration.Sexo;
import com.code.aon.payroll.principales.empresa.Empresa;

public class EmpresaController extends PayrollBasicController implements IPayrollBackAction {

	private String edad;

	public String getEdad() {
		calcularEdad();
		return edad;
	}

	public void setEdad(String edad) {
		this.edad = edad;
	}
	
	private void calcularEdad(){
		Date fecnac = ((Empresa)getTo()).getFecnac();
		if(fecnac != null){
			Calendar currentCalendar = Calendar.getInstance(FacesContext.getCurrentInstance().getViewRoot().getLocale());
			Calendar birthCalendar = Calendar.getInstance(FacesContext.getCurrentInstance().getViewRoot().getLocale());
			birthCalendar.setTime(fecnac);
			
			Integer años = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR); 
			birthCalendar.add(Calendar.YEAR, años);
			
			if (currentCalendar.before(birthCalendar))
				años--;
			
			setEdad(años.toString());
		} 
	}
	
	private List<SelectItem> sexo;
	/**
	 * Recupera los tipos de sexo 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaSexo() {
		if(sexo==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			sexo = new LinkedList<SelectItem>();
			for (Sexo p : Sexo.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				sexo.add(item);
			}
		}
		return sexo;
	}
	
	private List<SelectItem> envioss;
	/**
	 * Recupera los tipos de envios de s.s. 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEnvioss() {
		if(envioss==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			envioss = new LinkedList<SelectItem>();
			for (EnvioSS p : EnvioSS.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				envioss.add(item);
			}
		}
		return envioss;
	}
	
	private List<SelectItem> indicadoresIrpf;
	/**
	 * Recupera los Indicadores de Irpf
	 * 
	 * @return
	 */
	public List<SelectItem> getListaIndicadoresIrpf() {
		if(indicadoresIrpf==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicadoresIrpf = new LinkedList<SelectItem>();
			for (IndicadorIrpf p : IndicadorIrpf.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				indicadoresIrpf.add(item);
			}
		}
		return indicadoresIrpf;
	}
	
	private List<SelectItem> conciertos;
	/**
	 * Recupera los conciertos economicos
	 * 
	 * @return
	 */
	public List<SelectItem> getListaConciertos() {
		if(conciertos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			conciertos = new LinkedList<SelectItem>();
			for (ConciertoEconomico p : ConciertoEconomico.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				conciertos.add(item);
			}
		}
		return conciertos;
	}
	
	private List<SelectItem> pagoImpuestos;
	/**
	 * Recupera los pagos de impuestos
	 * 
	 * @return
	 */
	public List<SelectItem> getListaPagoImpuestos() {
		if(pagoImpuestos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			pagoImpuestos = new LinkedList<SelectItem>();
			for (PagoImpuestos p : PagoImpuestos.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				pagoImpuestos.add(item);
			}
		}
		return pagoImpuestos;
	}

	@Override
	public String returnAction() {
		
		return null;
	}

	
}


