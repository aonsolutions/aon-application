package com.code.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.ConciertoEconomico;
import com.code.aon.payroll.enumeration.EnvioSS;
import com.code.aon.payroll.enumeration.IndicadorIrpf;
import com.code.aon.payroll.enumeration.PagoImpuestos;
import com.code.aon.payroll.enumeration.Sexo;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.ui.form.LinesController;

public class EmpresaController extends LinesController  implements IPayrollAlias, IPayrollBackAction   {

	private String edad;
	private Integer year;
	private List<ITransferObject> selectedList;
	

	
	@Override
	public void onSelect(ActionEvent event) {
		selectedList = new LinkedList<ITransferObject>();
		super.onSelect(event);		
		selectedList.add(this.getTo());		
						
	}
	
	public Integer getYear() {
		Calendar calendar = Calendar.getInstance();
		year = (calendar.get(Calendar.YEAR));
	return year;
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
	
	@Override
	public void accept(ActionEvent event) {

		verifyNullFields();
		super.accept(event);
		
		
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onAccept(event);
		
		
	}
	
	/**
	 * comprueba los nulos del los campos que son lookup
	 * para ponerlos a null en el caso de que esten vacios
	 */
	private void verifyNullFields(){
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getAdmon().getCdg()))
			((Empresa)getTo()).setAdmon(null);
				
		if(StringUtils.isEmpty(((Empresa)getTo()).getDivisa().getCdg()))
			((Empresa)getTo()).setDivisa(null);
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getTipdoc().getCdg()))
			((Empresa)getTo()).setTipdoc(null);
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getTipdoc1().getCdg()))
			((Empresa)getTo()).setTipdoc1(null);
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getTipempr().getCdg()))
			((Empresa)getTo()).setTipempr(null);
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getPais().getCdg()))
			((Empresa)getTo()).setPais(null);
		
		if(StringUtils.isEmpty(((Empresa)getTo()).getPais1().getCdg()))
			((Empresa)getTo()).setPais1(null);
		
	}
	
	/**
	 * Establece los valores por defecto de campos
	 * Campos check y radio.
	 */
	public void setDefaultFields(){
		
		Empresa empresa = (Empresa)getTo(); 
		
		empresa.setSexo(Sexo.EMPRESA);
		empresa.setIndcal(true);
		empresa.setIndnom(true);
		empresa.setIndcoste(true);
		empresa.setIndirpf(IndicadorIrpf.TRIMESTRAL);
		empresa.setCecon(ConciertoEconomico.NO);
		empresa.setEnvioss(EnvioSS.ENVIO1);
		
	}
	
	
	private Date fecini;
	private Date fecfin;
	private Date fecnac;
	private Date feccon;
	private Date cliente_fecini;
	private Date cliente_fecfin;
	private Admon admon;
	private Cliente cliente;
	private Divisa divisa;
	private Documento tipdoc;
	private Documento tipdoc1;
	private Empresario tipempr;
	private Pais pais;
	private Pais pais1;

	public Date getFecini() {
		return fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	public Date getFecfin() {
		return fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	public Date getFecnac() {
		return fecnac;
	}

	public void setFecnac(Date fecnac) {
		this.fecnac = fecnac;
	}

	public Date getFeccon() {
		return feccon;
	}

	public void setFeccon(Date feccon) {
		this.feccon = feccon;
	}
	
	public Date getCliente_fecini() {
		return cliente_fecini;
	}

	public void setCliente_fecini(Date cliente_fecini) {
		this.cliente_fecini = cliente_fecini;
	}
	
	public Date getCliente_fecfin() {
		return cliente_fecfin;
	}

	public void setCliente_fecfin(Date cliente_fecfin) {
		this.cliente_fecfin = cliente_fecfin;
	}

	public Admon getAdmon() {
		return admon;
	}

	public void setAdmon(Admon admon) {
		this.admon = admon;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Divisa getDivisa() {
		return divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	public Documento getTipdoc() {
		return tipdoc;
	}

	public void setTipdoc(Documento tipdoc) {
		this.tipdoc = tipdoc;
	}

	public Documento getTipdoc1() {
		return tipdoc1;
	}

	public void setTipdoc1(Documento tipdoc1) {
		this.tipdoc1 = tipdoc1;
	}

	public Empresario getTipempr() {
		return tipempr;
	}

	public void setTipempr(Empresario tipempr) {
		this.tipempr = tipempr;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public Pais getPais1() {
		return pais1;
	}

	public void setPais1(Pais pais1) {
		this.pais1 = pais1;
	}
	
	
	
	
	
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		super.onEditSearch(arg0);

		admon = new Admon();
		cliente = new Cliente();
		divisa = new Divisa();
		tipdoc = new Documento();
		tipdoc1 = new Documento();
		tipempr = new Empresario();
		pais = new Pais();
		pais1 = new Pais();

	}
	
	/**
	 * Se incluyen manualmente a las búsquedas los campos lookup y de fechas 
	 */
	@Override
	public void onSearch(ActionEvent event) {
		try {
			
			/*
			 * Búsqueda por campos lookup
			 */
			if ((admon.getCdg() != null) && (! StringUtils.isEmpty(admon.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_ADMON_CDG), admon.getCdg());
			}
			if (cliente.getCdg() != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_CLIENTE_CDG), cliente.getCdg());
			}
			if ((divisa.getCdg() != null) && (! StringUtils.isEmpty(divisa.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_DIVISA_CDG), divisa.getCdg());
			}
			if ((tipdoc.getCdg() != null) && (! StringUtils.isEmpty(tipdoc.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_TIPDOC_CDG), tipdoc.getCdg());
			}
			if ((tipdoc1.getCdg() != null) && (! StringUtils.isEmpty(tipdoc1.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_TIPDOC1_CDG), tipdoc1.getCdg());
			}
			if ((tipempr.getCdg() != null) && (! StringUtils.isEmpty(tipempr.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_TIPEMPR_CDG), tipempr.getCdg());
			}
			if ((pais.getCdg() != null) && (! StringUtils.isEmpty(pais.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_PAIS_CDG), pais.getCdg());
			}
			if ((pais1.getCdg() != null) && (! StringUtils.isEmpty(pais1.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_PAIS1_CDG), pais1.getCdg());
			}
			
			/*
			 * Búsqueda por campos Date
			 */
			if (fecini != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_FECINI), fecini);
			}
			if (fecfin != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_FECFIN), fecfin);
			}
			if (fecnac != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_FECNAC), fecnac);
			}
			if (feccon != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_FECCON), feccon);
			}
			if (cliente_fecfin != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_CLIENTE_FECFIN), cliente_fecfin);
			}
			if (cliente_fecini != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_CLIENTE_FECINI), cliente_fecini);
			}
			
			
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onSearch(event);
		
	}
	
	@Override
	public void onReset(ActionEvent event) {
		
		super.onReset(event);
		
		
	}
	
	/**
	 * Aswigna al código un número autonumerico
	 * @throws ManagerBeanException
	 */
    public void generateCdg() {	
    	Integer code ;
    			
		String consulta = "select max(cdg) from Empresa";			
		Query q = HibernateUtil.getSession().createQuery(consulta);			
		List results = q.list();
		   
		code = (Integer)results.get(0)+1;
		
		((Empresa)getTo()).setCdg(code);
	}



	public String getEdad() {
		return edad;
	}
	
   
	public List<ITransferObject> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<ITransferObject> selectedList) {
		this.selectedList = selectedList;
	}

}


