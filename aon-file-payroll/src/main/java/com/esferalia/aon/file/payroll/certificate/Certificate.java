package com.esferalia.aon.file.payroll.certificate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.file.payroll.certificate.data.Cotizacion;
import com.esferalia.aon.file.payroll.certificate.data.CuentaCotizacion;
import com.esferalia.aon.file.payroll.certificate.data.Periodo;
import com.esferalia.aon.file.payroll.certificate.data.Trabajador;

public class Certificate {

	private static final String CERTIFICADO_EMPRESA = "Certificado_empresa";
	
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private Locale locale;
	private ArrayList<Integer> errors;// = new ArrayList<Integer>();

	private List<CuentaCotizacion> cuentaCotizacion;

	public Certificate(String cif) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String f = formatter.format(date);
		fecha = Integer.parseInt(f);
		formatter = new SimpleDateFormat("HHmmss");
		String t = formatter.format(date);
		hora = Integer.parseInt(t);
		fichero = cif + fecha.toString() + hora.toString();
	}

	public Integer getFecha() {
		return fecha;
	}

	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public String getFichero() {
		return fichero;
	}

	public void setFichero(String fichero) {
		this.fichero = fichero;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ArrayList<Integer> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<Integer> errors) {
		this.errors = errors;
	}

	public List<CuentaCotizacion> getCuentaCotizacion() {
		return cuentaCotizacion;
	}

	public void setCuentaCotizacion(List<CuentaCotizacion> cuentaCotizacion) {
		this.cuentaCotizacion = cuentaCotizacion;
	}

	public Element getElement(Document xmldoc) {
		Element certificadoEmpresa = xmldoc.createElement(CERTIFICADO_EMPRESA);
		return certificadoEmpresa;
	}

	public void fillElement(Document xmldoc, Element certificadoEmpresa) {
		validate();
		for (CuentaCotizacion cc : getCuentaCotizacion()) {
			certificadoEmpresa.appendChild(cc.getElement(xmldoc));
		}

	}

	public void validate() {
		errors = new ArrayList<Integer>();

		for (CuentaCotizacion cc : getCuentaCotizacion()) {
			if (StringUtils.isBlank(cc.getRepresentante().getCifNif())) {
				cc.getRepresentante().setCifNif(cc.getRepresentante().getCifNif()+getErrorMessage(0));
				errors.add(0);
			}
			if (StringUtils.isBlank(cc.getRepresentante().getNombre())) {
				cc.getRepresentante().setNombre(cc.getRepresentante().getNombre()+getErrorMessage(1));
				errors.add(1);
			}
			if (StringUtils.isBlank(cc.getRepresentante().getApellido1())) {
				cc.getRepresentante().setApellido1(cc.getRepresentante().getApellido1()+getErrorMessage(2));
				errors.add(2);
			}
			if (StringUtils.isBlank(cc.getEmpresa().getCifNif())) {
				cc.getEmpresa().setCifNif(cc.getEmpresa().getCifNif()+getErrorMessage(3));
				errors.add(3);
			}
			if (StringUtils.isBlank(cc.getEmpresa().getCcc())) {
				cc.getEmpresa().setCcc(cc.getEmpresa().getCcc()+getErrorMessage(4));
				errors.add(4);
			}
			if (cc.getListaTrabajadores() != null) {
				for (Trabajador t : cc.getListaTrabajadores()) {
					if (t != null) {
						if (StringUtils.isBlank(t.getDniNie())) {
							t.setDniNie(t.getDniNie()+getErrorMessage(5));
							errors.add(5);
						}
						if (StringUtils.isBlank(t.getNombre())) {
							t.setNombre(t.getNombre()+getErrorMessage(6));
							errors.add(6);
						}
						if (StringUtils.isBlank(t.getApellido1())) {
							t.setApellido1(t.getApellido1()+getErrorMessage(7));
							errors.add(7);
						}
						if (StringUtils.isBlank(t.getNumSs())) {
							t.setNumSs(t.getNumSs()+getErrorMessage(8));
							errors.add(8);
						}
						if (StringUtils.isBlank(t.getTipoContrato())) {
							t.setTipoContrato(t.getTipoContrato()+getErrorMessage(9));
							errors.add(9);
						}
						if (StringUtils.isBlank(t.getCodProfesion())) {
							t.setCodProfesion(t.getCodProfesion()+getErrorMessage(10));
							errors.add(10);
						}
						if (StringUtils.isBlank(t.getFechaAltaEmpresa())) {
							t.setFechaAltaEmpresa(t.getFechaAltaEmpresa()+getErrorMessage(11));
							errors.add(11);
						}
						if (StringUtils.isBlank(t.getCodCausaSuspension())) {
							t.setCodCausaSuspension(t.getCodCausaSuspension()+getErrorMessage(12));
							errors.add(12);
						}
						if (StringUtils.isBlank(t.getFechaSuspensionExtincion())) {
							t.setFechaSuspensionExtincion(t.getFechaSuspensionExtincion()+getErrorMessage(13));
							errors.add(13);
						}
						if (StringUtils.isBlank(t.getDiasSalarioTramitacion())) {
							t.setDiasSalarioTramitacion(t.getDiasSalarioTramitacion()+getErrorMessage(14));
							errors.add(14);
						}

						/*
						 * NODOS
						 */
						if (!(t.getDistribucionJornada() == null && (t.getDatosCotizacion() == null || t.getDatosCotizacion().size() == 0))) {
							if (t.getDistribucionJornada() != null){
								for (Periodo p : t.getDistribucionJornada()
										.getListaPeriodos()) {
									if (StringUtils.isBlank(p.getTipoDistribucion())) {
										p.setTipoDistribucion(p.getTipoDistribucion()+getErrorMessage(15));
										errors.add(15);
									}
									if (StringUtils.isBlank(p.getFechaInicioPeriodo())) {
										p.setFechaInicioPeriodo(p.getFechaInicioPeriodo()+getErrorMessage(16));
										errors.add(16);
									}
									if (StringUtils.isBlank(p.getFechaFinPeriodo())) {
										p.setFechaFinPeriodo(p.getFechaFinPeriodo()+getErrorMessage(17));
										errors.add(17);
									}
									if (StringUtils.isBlank(p.getNumeroDiasTrabajadosPorSemanaOPeriodo())) {
										p.setNumeroDiasTrabajadosPorSemanaOPeriodo(p.getNumeroDiasTrabajadosPorSemanaOPeriodo()+getErrorMessage(18));
										errors.add(18);
									}
								}
							}
							if (t.getDatosCotizacion() != null){
								for (Cotizacion c : t.getDatosCotizacion()) {
									if (StringUtils.isBlank(c.getAno())) {
										c.setAno(c.getAno()+getErrorMessage(19));
										errors.add(19);
									}
									if (StringUtils.isBlank(c.getMes())) {
										c.setMes(c.getMes()+getErrorMessage(20));
										errors.add(20);
									}
									if (StringUtils.isBlank(c.getNumDiasCotizados())) {
										c.setNumDiasCotizados(c.getNumDiasCotizados()+getErrorMessage(21));
										errors.add(21);
									}
									if (StringUtils.isBlank(c.getBaseCotizacionDesempleo())) {
										c.setBaseCotizacionDesempleo(c.getBaseCotizacionDesempleo()+getErrorMessage(22));
										errors.add(22);
									}
								}
							}
						} else {
							// t.setDistribucionJornada(getErrorMessage());
							// t.setDatosCotizacion(getErrorMessage());
							errors.add(23);
						}

						if (t.getDatosVacacionesCotizadas() != null) {
							if (StringUtils.isBlank(t.getDatosVacacionesCotizadas().getNumDiasCotizados())) {
								t.getDatosVacacionesCotizadas().setNumDiasCotizados(t.getDatosVacacionesCotizadas().getNumDiasCotizados()+getErrorMessage(24));
								errors.add(24);
							}
							if (StringUtils.isBlank(t.getDatosVacacionesCotizadas().getBaseCotizacionDesempleo())) {
								t.getDatosVacacionesCotizadas().setBaseCotizacionDesempleo(t.getDatosVacacionesCotizadas().getBaseCotizacionDesempleo()+getErrorMessage(25));
								errors.add(25);
							}
						}
					}
				}
			}
		}
	}

	public ArrayList<Exception> getExceptions() {
		List<Exception> list = new ArrayList<Exception>();
		if (!errors.isEmpty()) {
			setFichero("ERROR - " + getFichero());
			for (Integer i : errors) {
				list.add(new Exception(i.toString()));
			}
		}
		return (ArrayList<Exception>) list;
	}
	
	public String getErrorMessage(Integer i){
		final String ERROR_TAG = " - ERROR: ";
		String errorMsg = ResourceBundle.getBundle("com.esferalia.aon.payroll.core.impl.messages").getString("aon_payroll_certificate_error_" + i);
		return ERROR_TAG + errorMsg;
	}
	
	

}
