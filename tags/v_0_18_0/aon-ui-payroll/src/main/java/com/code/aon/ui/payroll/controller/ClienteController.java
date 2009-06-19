package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Tipovia;

public class ClienteController extends PayrollBasicController {

	private List<SelectItem> envioss;

	private Delegacion delegacion;
	private Empresario empresario;
	private Documento documento;
	private Divisa divisa;
	private Tipovia tipovia;
	private Provincia provincia;
	private Cliente cliente;
	private Pais pais;
	private Date fecnew;
	private Date hornew;
	private boolean inactivo;
	private boolean indcal;
	private boolean indnom;
	private boolean indcoste;
	private boolean soloases;
	private ClienteController clientePrint;

	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEnvioss() {
		if (envioss == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			envioss = new LinkedList<SelectItem>();
			for (EnvioSS2 p : EnvioSS2.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				envioss.add(item);
			}
		}
		return envioss;
	}

	public void verifyNullFields() {
		if (StringUtils.isEmpty(((Cliente) getTo()).getTipovia().getCdg()))
			((Cliente) getTo()).getTipovia().setCdg("CL");
		if (StringUtils.isEmpty(((Cliente) getTo()).getDivisa().getCdg()))
			((Cliente) getTo()).getDivisa().setCdg("2");
		if (StringUtils.isEmpty(((Cliente) getTo()).getPais().getCdg()))
			((Cliente) getTo()).setPais(null);
		if (StringUtils.isEmpty(((Cliente) getTo()).getTipdoc().getCdg()))
			((Cliente) getTo()).setTipdoc(null);
		if (StringUtils.isEmpty(((Cliente) getTo()).getTipempr().getCdg()))
			((Cliente) getTo()).setTipempr(null);
		if (StringUtils.isEmpty(((Cliente) getTo()).getProvincia().getCdg()))
			((Cliente) getTo()).setProvincia(null);

	}

	public void setDefaultFields() {
		Cliente c = (Cliente) getTo();
		c.getTipovia().setCdg("CL");
		c.setIndcal(true);
		c.setIndcoste(true);
		c.setIndnom(true);
		c.setEnvioss(EnvioSS2.ENVIO1);

	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);

		setTipovia(new Tipovia());
		setProvincia(new Provincia());
		setDelegacion(new Delegacion());
		setEmpresario(new Empresario());
		setDocumento(new Documento());
		setDivisa(new Divisa());
		setPais(new Pais());
		setCliente(new Cliente());

	}

	@Override
	public void onSearch(ActionEvent event) {

		try {
			this.clearCriteria();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if ((cliente.getCdg() != null) && (cliente != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_CDG),
						getCliente().getCdg());
			}

			if (delegacion.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_DELEGACION_CDG),
						getDelegacion().getCdg());
			}
			if ((empresario.getCdg() != null)
					&& (!StringUtils.isEmpty(empresario.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_TIPEMPR_CDG),
						getEmpresario().getCdg());
			}

			if ((documento.getCdg() != null)
					&& (!StringUtils.isEmpty(documento.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_TIPDOC_CDG),
						getDocumento().getCdg());
			}

			if ((divisa.getCdg() != null)
					&& (!StringUtils.isEmpty(divisa.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_DIVISA_CDG),
						getDivisa().getCdg());
			}

			if ((tipovia.getCdg() != null)
					&& (!StringUtils.isEmpty(tipovia.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_TIPOVIA_CDG),
						getTipovia().getCdg());
			}

			if ((provincia.getCdg() != null)
					&& (!StringUtils.isEmpty(provincia.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_PROVINCIA_CDG),
						getProvincia().getCdg());
			}
			if ((pais.getCdg() != null)
					&& (!StringUtils.isEmpty(pais.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_PAIS_CDG),
						getPais().getCdg());
			}

			if (fecnew != null) {
				getCriteria()
						.addEqualExpression(
								getFieldName(IPayrollAlias.CLIENTE_FECNEW),
								getFecnew());
			}

			if (hornew != null) {

				getCriteria()
						.addEqualExpression(
								getFieldName(IPayrollAlias.CLIENTE_HORNEW),
								getHornew());
			}

			if (inactivo) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_INACTIVO), true);
			}
			if (indcal) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_INDCAL), true);
			}
			if (indnom) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_INDNOM), true);
			}
			if (indcoste) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_INDCOSTE), true);
			}
			if (soloases) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.CLIENTE_SOLOASES), true);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inactivo = false;
		indcal = false;
		indnom = false;
		indcoste = false;
		soloases = false;

		super.onSearch(event);
	}

	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelect(event);
		if(getTo()!=null){
			try {
				String id = BeanManager.getManagerBean(Cliente.class).getFieldName(IPayrollAlias.CLIENTE_CDG);
				Integer cdg = ((Cliente)getTo()).getCdg();
				clientePrint = new ClienteController();
				clientePrint = this;
				clientePrint.clearCriteria();
				clientePrint.getCriteria().addEqualExpression(id, cdg);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Tipovia getTipovia() {
		return tipovia;
	}

	public void setTipovia(Tipovia tipovia) {
		this.tipovia = tipovia;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public Delegacion getDelegacion() {
		return delegacion;
	}

	public void setDelegacion(Delegacion delegacion) {
		this.delegacion = delegacion;
	}

	public Empresario getEmpresario() {
		return empresario;
	}

	public void setEmpresario(Empresario empresario) {
		this.empresario = empresario;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Divisa getDivisa() {
		return divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public Date getFecnew() {
		return fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	public Date getHornew() {
		return hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	public boolean getInactivo() {
		return inactivo;
	}

	public void setInactivo(boolean inactivo) {
		this.inactivo = inactivo;
	}

	public boolean getIndcal() {
		return indcal;
	}

	public void setIndcal(boolean indcal) {
		this.indcal = indcal;
	}

	public boolean getIndnom() {
		return indnom;
	}

	public void setIndnom(boolean indnom) {
		this.indnom = indnom;
	}

	public boolean getIndcoste() {
		return indcoste;
	}

	public void setIndcoste(boolean indcoste) {
		this.indcoste = indcoste;
	}

	public boolean getSoloases() {
		return soloases;
	}

	public void setSoloases(boolean soloases) {
		this.soloases = soloases;
	}

	Integer code;

	public Integer getCode() throws ManagerBeanException {

		code = 0;
		String consulta = "select max(cdg) from Cliente";
		Query q = HibernateUtil.getSession().createQuery(consulta);
		List results = q.list();
		System.out.println("Max Code: " + results.get(0));
		code = (Integer) results.get(0) + 1;
		System.out.println("New Code: " + code);
		return code;

	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public ClienteController getClientePrint() {
		return clientePrint;
	}

	public void setClientePrint(ClienteController clientePrint) {
		this.clientePrint = clientePrint;
	}

}