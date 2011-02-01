package com.esferalia.aon.file.payroll.certificate.data;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Trabajador {
	
	private static final String DATOS_TRABAJADOR = "Datos_Trabajador";
	private static final String DNI_NIE = "DNI_NIE";
	private static final String NOMBRE = "Nombre";
	private static final String APELLIDO1 = "Apellido1";
	private static final String APELLIDO2 = "Apellido2";
	private static final String NUM_SS = "NumSS";
	private static final String GRUPO_COTIZACION = "GrupoCotizacion";
	private static final String TIPO_CONTRATO = "TipoContrato";
	private static final String DURACION_CONTRATO = "DuracionContrato";
	private static final String INDICADOR_DURACION_CONTRATO = "IndicadorDuracionContrato";
	private static final String COD_PROFESION = "CodProfesion";
	private static final String CARGO_PUBLICO_SINDICAL = "CargoPublicoSindical";
	private static final String PORCENTUAL_DEDICACION = "PorcentualDedicacion";
	private static final String FECHA_ALTA_EMPRESA = "FechaAltaEmpresa";
	private static final String COD_CAUSA_SUSPENSION = "CodCausaSuspension";
	private static final String FECHA_SUSPENSION_EXTINCION = "FechaSuspensionExtincion";
	private static final String FECHA_FIN_SUSPENSION = "FechaFinSuspension";
	private static final String ERE = "ERE";
	private static final String PORCENTUAL_REDUCCION_ERE = "PorcentualReduccionERE";
	private static final String PORCENTUAL_REDUCCION_OTROS = "PorcentualReduccionOTROS";
	private static final String COD_CAUSA_PORCENT_REDUCCION = "CodCausaPorcentReduccion";
	private static final String FECHA_DESDE_PERIODO_SALARIOS = "FechaDesdePeriodoSalarios";
	private static final String FECHA_HASTA_PERIODO_SALARIOS = "FechaHastaPeriodoSalarios";
	private static final String DIAS_SALARIO_TRAMITACION = "DiasSalarioTramitacion";
	
	private String dniNie;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String numSs;
	private String grupoCotizacion;
	private String tipoContrato;
	private String duracionContrato;
	private String indicadorDuracionContrato;
	private String codProfesion;
	private String cargoPublicoSindical;
	private String porcentualDedicacion;
	private String fechaAltaEmpresa;
	private String codCausaSuspension;
	private String fechaSuspensionExtincion;
	private String fechaFinSuspension;
	private String ere;
	private String porcentualReduccionERE;
	private String porcentualReduccionOTROS;
	private String codCausaPorcentReduccion;
	private String fechaDesdePeriodoSalarios;
	private String fechaHastaPeriodoSalarios;
	private String diasSalarioTramitacion;
	private DistribucionJornada distribucionJornada;
	private List<Cotizacion> datosCotizacion;
	private List<CotizacionRea> datosCotizacionRea;
	private Vacaciones datosVacacionesCotizadas;
	private VacacionesRea datosVacacionesCotizadasRea;
	
	public String getDniNie() {
		return dniNie;
	}
	public void setDniNie(String dniNie) {
		this.dniNie = dniNie;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	public String getNumSs() {
		return numSs;
	}
	public void setNumSs(String numSs) {
		this.numSs = numSs;
	}
	public String getGrupoCotizacion() {
		return grupoCotizacion;
	}
	public void setGrupoCotizacion(String grupoCotizacion) {
		this.grupoCotizacion = grupoCotizacion;
	}
	public String getTipoContrato() {
		return tipoContrato;
	}
	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}
	public String getDuracionContrato() {
		return duracionContrato;
	}
	public void setDuracionContrato(String duracionContrato) {
		this.duracionContrato = duracionContrato;
	}
	public String getIndicadorDuracionContrato() {
		return indicadorDuracionContrato;
	}
	public void setIndicadorDuracionContrato(String indicadorDuracionContrato) {
		this.indicadorDuracionContrato = indicadorDuracionContrato;
	}
	public String getCodProfesion() {
		return codProfesion;
	}
	public void setCodProfesion(String codProfesion) {
		this.codProfesion = codProfesion;
	}
	public String getCargoPublicoSindical() {
		return cargoPublicoSindical;
	}
	public void setCargoPublicoSindical(String cargoPublicoSindical) {
		this.cargoPublicoSindical = cargoPublicoSindical;
	}
	public String getPorcentualDedicacion() {
		return porcentualDedicacion;
	}
	public void setPorcentualDedicacion(String porcentualDedicacion) {
		this.porcentualDedicacion = porcentualDedicacion;
	}
	public String getFechaAltaEmpresa() {
		return fechaAltaEmpresa;
	}
	public void setFechaAltaEmpresa(String fechaAltaEmpresa) {
		this.fechaAltaEmpresa = fechaAltaEmpresa;
	}
	public String getCodCausaSuspension() {
		return codCausaSuspension;
	}
	public void setCodCausaSuspension(String codCausaSuspension) {
		this.codCausaSuspension = codCausaSuspension;
	}
	public String getFechaSuspensionExtincion() {
		return fechaSuspensionExtincion;
	}
	public void setFechaSuspensionExtincion(String fechaSuspensionExtincion) {
		this.fechaSuspensionExtincion = fechaSuspensionExtincion;
	}
	public String getFechaFinSuspension() {
		return fechaFinSuspension;
	}
	public void setFechaFinSuspension(String fechaFinSuspension) {
		this.fechaFinSuspension = fechaFinSuspension;
	}
	public String getEre() {
		return ere;
	}
	public void setEre(String ere) {
		this.ere = ere;
	}
	public String getPorcentualReduccionERE() {
		return porcentualReduccionERE;
	}
	public void setPorcentualReduccionERE(String porcentualReduccionERE) {
		this.porcentualReduccionERE = porcentualReduccionERE;
	}
	public String getPorcentualReduccionOTROS() {
		return porcentualReduccionOTROS;
	}
	public void setPorcentualReduccionOTROS(String porcentualReduccionOTROS) {
		this.porcentualReduccionOTROS = porcentualReduccionOTROS;
	}
	public String getCodCausaPorcentReduccion() {
		return codCausaPorcentReduccion;
	}
	public void setCodCausaPorcentReduccion(String codCausaPorcentReduccion) {
		this.codCausaPorcentReduccion = codCausaPorcentReduccion;
	}
	public String getFechaDesdePeriodoSalarios() {
		return fechaDesdePeriodoSalarios;
	}
	public void setFechaDesdePeriodoSalarios(String fechaDesdePeriodoSalarios) {
		this.fechaDesdePeriodoSalarios = fechaDesdePeriodoSalarios;
	}
	public String getFechaHastaPeriodoSalarios() {
		return fechaHastaPeriodoSalarios;
	}
	public void setFechaHastaPeriodoSalarios(String fechaHastaPeriodoSalarios) {
		this.fechaHastaPeriodoSalarios = fechaHastaPeriodoSalarios;
	}
	public String getDiasSalarioTramitacion() {
		return diasSalarioTramitacion;
	}
	public void setDiasSalarioTramitacion(String diasSalarioTramitacion) {
		this.diasSalarioTramitacion = diasSalarioTramitacion;
	}
	public DistribucionJornada getDistribucionJornada() {
		return distribucionJornada;
	}
	public void setDistribucionJornada(DistribucionJornada distribucionJornada) {
		this.distribucionJornada = distribucionJornada;
	}
	public List<Cotizacion> getDatosCotizacion() {
		return datosCotizacion;
	}
	public void setDatosCotizacion(List<Cotizacion> datosCotizacion) {
		this.datosCotizacion = datosCotizacion;
	}
	public List<CotizacionRea> getDatosCotizacionRea() {
		return datosCotizacionRea;
	}
	public void setDatosCotizacionRea(List<CotizacionRea> datosCotizacionRea) {
		this.datosCotizacionRea = datosCotizacionRea;
	}
	public Vacaciones getDatosVacacionesCotizadas() {
		return datosVacacionesCotizadas;
	}
	public void setDatosVacacionesCotizadas(Vacaciones datosVacacionesCotizadas) {
		this.datosVacacionesCotizadas = datosVacacionesCotizadas;
	}
	public VacacionesRea getDatosVacacionesCotizadasRea() {
		return datosVacacionesCotizadasRea;
	}
	public void setDatosVacacionesCotizadasRea(VacacionesRea datosVacacionesCotizadasRea) {
		this.datosVacacionesCotizadasRea = datosVacacionesCotizadasRea;
	}
	
	public Element getElement(Document xmldoc){
		Element trabajador = xmldoc.createElement(DATOS_TRABAJADOR);

		Element dniNie = xmldoc.createElement(DNI_NIE);
		dniNie.appendChild(xmldoc.createTextNode(getDniNie() ));
		trabajador.appendChild(dniNie);

		Element nombre = xmldoc.createElement(NOMBRE);
		nombre.appendChild(xmldoc.createTextNode(getNombre())); 
		trabajador.appendChild(nombre);

		Element apellido1 = xmldoc.createElement(APELLIDO1);
		apellido1.appendChild(xmldoc.createTextNode(getApellido1())); 
		trabajador.appendChild(apellido1);

		Element apellido2 = null;
		if(!StringUtils.isBlank(getApellido2())){
			apellido2 = xmldoc.createElement(APELLIDO2);
			apellido2.appendChild(xmldoc.createTextNode(getApellido2()));
			trabajador.appendChild(apellido2);
		}
		
		Element numSs = xmldoc.createElement(NUM_SS);
		numSs.appendChild(xmldoc.createTextNode(getNumSs())); 
		trabajador.appendChild(numSs);

		Element grupoCotizacion = null;
		if(!StringUtils.isBlank(getGrupoCotizacion())){
			grupoCotizacion = xmldoc.createElement(GRUPO_COTIZACION);
			grupoCotizacion.appendChild(xmldoc.createTextNode(getGrupoCotizacion())); 
			trabajador.appendChild(grupoCotizacion);
		}

		Element tipoContrato = xmldoc.createElement(TIPO_CONTRATO);
		tipoContrato.appendChild(xmldoc.createTextNode(getTipoContrato())); 
		trabajador.appendChild(tipoContrato);

		Element duracionContrato = null;
		if(!StringUtils.isBlank(getDuracionContrato())){
			duracionContrato = xmldoc.createElement(DURACION_CONTRATO);
			duracionContrato.appendChild(xmldoc.createTextNode(getDuracionContrato())); 
			trabajador.appendChild(duracionContrato);
		}

		Element indicadorDuracionContrato = null;
		if(!StringUtils.isBlank(getIndicadorDuracionContrato())){
			indicadorDuracionContrato = xmldoc.createElement(INDICADOR_DURACION_CONTRATO);
			indicadorDuracionContrato.appendChild(xmldoc.createTextNode(getIndicadorDuracionContrato())); 
			trabajador.appendChild(indicadorDuracionContrato);
		}
		
		Element codProfesion = xmldoc.createElement(COD_PROFESION);
		codProfesion.appendChild(xmldoc.createTextNode(getCodProfesion())); 
		trabajador.appendChild(codProfesion);
		
		Element cargoPublicoSindical = null;
		if(!StringUtils.isBlank(getCargoPublicoSindical())){
			cargoPublicoSindical = xmldoc.createElement(CARGO_PUBLICO_SINDICAL);
			cargoPublicoSindical.appendChild(xmldoc.createTextNode(getCargoPublicoSindical())); 
			trabajador.appendChild(cargoPublicoSindical);
		}
		
		Element porcentualDedicacion = null;
		if(!StringUtils.isBlank(getPorcentualDedicacion())){
			porcentualDedicacion = xmldoc.createElement(PORCENTUAL_DEDICACION);
			porcentualDedicacion.appendChild(xmldoc.createTextNode(getPorcentualDedicacion())); 
			trabajador.appendChild(porcentualDedicacion);
		}
		
		Element fechaAltaEmpresa = xmldoc.createElement(FECHA_ALTA_EMPRESA);
		fechaAltaEmpresa.appendChild(xmldoc.createTextNode(getFechaAltaEmpresa())); 
		trabajador.appendChild(fechaAltaEmpresa);
		
		Element codCausaSuspension = xmldoc.createElement(COD_CAUSA_SUSPENSION);
		codCausaSuspension.appendChild(xmldoc.createTextNode(getCodCausaSuspension())); 
		trabajador.appendChild(codCausaSuspension);
		
		Element fechaSuspensionExtincion = xmldoc.createElement(FECHA_SUSPENSION_EXTINCION);
		fechaSuspensionExtincion.appendChild(xmldoc.createTextNode(getFechaSuspensionExtincion())); 
		trabajador.appendChild(fechaSuspensionExtincion);
		
		Element fechaFinSuspension = null;
		if(!StringUtils.isBlank(getFechaFinSuspension())){
			fechaFinSuspension = xmldoc.createElement(FECHA_FIN_SUSPENSION);
			fechaFinSuspension.appendChild(xmldoc.createTextNode(getFechaFinSuspension())); 
			trabajador.appendChild(fechaFinSuspension);
		}
		
		Element ere = null;
		if(!StringUtils.isBlank(getEre())){
			ere = xmldoc.createElement(ERE);
			ere.appendChild(xmldoc.createTextNode(getEre())); 
			trabajador.appendChild(ere);
		}
		
		Element porcentualReduccionERE = null;
		if(!StringUtils.isBlank(getPorcentualReduccionERE())){
			porcentualReduccionERE = xmldoc.createElement(PORCENTUAL_REDUCCION_ERE);
			porcentualReduccionERE.appendChild(xmldoc.createTextNode(getPorcentualReduccionERE())); 
			trabajador.appendChild(porcentualReduccionERE);
		}
		
		Element porcentualReduccionOTROS = null;
		if(!StringUtils.isBlank(getPorcentualReduccionOTROS())){
			porcentualReduccionOTROS = xmldoc.createElement(PORCENTUAL_REDUCCION_OTROS);
			porcentualReduccionOTROS.appendChild(xmldoc.createTextNode(getPorcentualReduccionOTROS())); 
			trabajador.appendChild(porcentualReduccionOTROS);
		}
		
		Element codCausaPorcentReduccion = null;
		if(!StringUtils.isBlank(getCodCausaPorcentReduccion())){
			codCausaPorcentReduccion = xmldoc.createElement(COD_CAUSA_PORCENT_REDUCCION);
			codCausaPorcentReduccion.appendChild(xmldoc.createTextNode(getCodCausaPorcentReduccion())); 
			trabajador.appendChild(codCausaPorcentReduccion);
		}
		
		Element fechaDesdePeriodoSalarios = null;
		if(!StringUtils.isBlank(getFechaDesdePeriodoSalarios())){
			fechaDesdePeriodoSalarios = xmldoc.createElement(FECHA_DESDE_PERIODO_SALARIOS);
			fechaDesdePeriodoSalarios.appendChild(xmldoc.createTextNode(getFechaDesdePeriodoSalarios())); 
			trabajador.appendChild(fechaDesdePeriodoSalarios);
		}
		
		Element fechaHastaPeriodoSalarios = null;
		if(!StringUtils.isBlank(getFechaHastaPeriodoSalarios())){
			fechaHastaPeriodoSalarios = xmldoc.createElement(FECHA_HASTA_PERIODO_SALARIOS);
			fechaHastaPeriodoSalarios.appendChild(xmldoc.createTextNode(getFechaHastaPeriodoSalarios())); 
			trabajador.appendChild(fechaHastaPeriodoSalarios);
		}
		
		Element diasSalarioTramitacion = xmldoc.createElement(DIAS_SALARIO_TRAMITACION);
		diasSalarioTramitacion.appendChild(xmldoc.createTextNode(getDiasSalarioTramitacion())); 
		trabajador.appendChild(diasSalarioTramitacion);
		
		if(getDistribucionJornada()!=null){
			trabajador.appendChild(getDistribucionJornada().getElement(xmldoc));
		}
		
		for(Cotizacion c: getDatosCotizacion()){
			trabajador.appendChild(c.getElement(xmldoc));
		}

//		for(CotizacionRea c: getDatosCotizacionRea()){
//			trabajador.appendChild(c.getElement(xmldoc));
//		}
		if(getDatosVacacionesCotizadas()!=null){
			trabajador.appendChild(getDatosVacacionesCotizadas().getElement(xmldoc));
		}
//		trabajador.appendChild(getDatosVacacionesCotizadasRea().getElement(xmldoc));
		
		return trabajador;
	}

}
