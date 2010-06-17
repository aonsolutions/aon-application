package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.payroll.certificate.Certificate;
import com.esferalia.aon.file.payroll.certificate.data.Cotizacion;
import com.esferalia.aon.file.payroll.certificate.data.CuentaCotizacion;
import com.esferalia.aon.file.payroll.certificate.data.Empresa;
import com.esferalia.aon.file.payroll.certificate.data.Jornada;
import com.esferalia.aon.file.payroll.certificate.data.Periodo;
import com.esferalia.aon.file.payroll.certificate.data.Representante;
import com.esferalia.aon.file.payroll.certificate.data.Trabajador;
import com.esferalia.aon.file.payroll.certificate.data.Vacaciones;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;

public class CertificateWriter {
	
	private Certificate certificate;
	
	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public FileOutput createCertificate(IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> remesaDetail ) throws ManagerBeanException {
		try {
			setCertificate(new Certificate());
			List<CuentaCotizacion> listaCuentas = new ArrayList<CuentaCotizacion>();
			listaCuentas.add(createCuentaCotizacionRecord(remesa, remesaDetail));
			getCertificate().setCuentaCotizacion(listaCuentas);
			
			File file = File.createTempFile("XXXXXXXX", ".XML");
			FileOutput output = new FileOutput();
			output.setFile(file);
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} 
		catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private CuentaCotizacion createCuentaCotizacionRecord( IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> listaDetalle) throws PayrollException {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
		Representante representante = new Representante();
		representante.setCifNif(remesa.getEmpresa().getRepresentanteDocument());
		representante.setNombre(remesa.getEmpresa().getNombreRepresentante());
		representante.setApellido1(remesa.getEmpresa().getApellido1Representante());
		if(StringUtils.isBlank(remesa.getEmpresa().getApellido2Representante())){
			representante.setApellido2(remesa.getEmpresa().getApellido2Representante());
		}
		if(StringUtils.isBlank(remesa.getEmpresa().getCargo())){
			representante.setCargo(remesa.getEmpresa().getCargo());
		}
		cuentaCotizacion.setRepresentante(representante);
		Empresa empresa = new Empresa();
		empresa.setCcc(remesa.getCodigoCcc());
		empresa.setCifNif(remesa.getEmpresa().getRegistry().getDocument().getValue());
		cuentaCotizacion.setEmpresa(empresa);

		List<Trabajador> listaTrabajadores = new ArrayList<Trabajador>();
		for(IRemesaCertificadoEmpresaDetalle detalle: listaDetalle){
			listaTrabajadores.add(createTrabajadorRecord(detalle));
		}
		cuentaCotizacion.setListaTrabajadores(listaTrabajadores);
		return cuentaCotizacion;
	}

	private Trabajador createTrabajadorRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		Trabajador trabajador = new Trabajador();
		trabajador.setDniNie(detalle.getEmpleado().getPersona().getRegistry().getDocument().getValue());
		trabajador.setNombre(detalle.getEmpleado().getPersona().getName());
		trabajador.setApellido1(detalle.getEmpleado().getPersona().getSurname());
		trabajador.setApellido2(detalle.getEmpleado().getPersona().getLastName());
		trabajador.setNumSs(detalle.getEmpleado().getPersona().getNumSS());
		trabajador.setGrupoCotizacion(null);
		trabajador.setTipoContrato(null);
		trabajador.setDuracionContrato(null);
//		trabajador.setIndicadorDuracionContrato();
		trabajador.setCodProfesion(null);
//		trabajador.setCargoPublicoSindical();
//		trabajador.setPorcentualDedicacion();
		trabajador.setFechaAltaEmpresa(detalle.getEmpleado().getFechaInicio().toString());
		trabajador.setCodCausaSuspension(detalle.getCausaSuspension());
		trabajador.setFechaSuspensionExtincion(detalle.getEmpleado().getFechaFin().toString());
//		trabajador.setFechaFinSuspension();
//		trabajador.setEre();
//		trabajador.setPorcentualReduccionERE();
//		trabajador.setPorcentualReduccionOTROS();
//		trabajador.setCodCausaPorcentReduccion();
//		trabajador.setFechaDesdePeriodoSalarios();
//		trabajador.setFechaHastaPeriodoSalarios();
//		trabajador.setDiasSalarioTramitacion();

		/*
		 * NODOS
		 */
		trabajador.setDistribucionJornada(createDistribucionJornadaRecord());		
		List<Cotizacion> listaDatosCotizacion = new ArrayList<Cotizacion>();
//		for(IRemesaCertificadoEmpresaDetalle detalle: listaDetalle){
			listaDatosCotizacion.add(createDatosCotizacionRecord(detalle));
//		}
		trabajador.setDatosCotizacion(listaDatosCotizacion);
		trabajador.setDatosVacacionesCotizadas(createDatosVacacionesCotizadasRecord());
		
		return trabajador;
	}


	private Jornada createDistribucionJornadaRecord() {
		Jornada jornada = new Jornada();
		List<Periodo> listaPeriodos = new ArrayList<Periodo>();
		jornada.setListaPeriodos(listaPeriodos);
		return jornada;
	}
	
	private Cotizacion createDatosCotizacionRecord(
			IRemesaCertificadoEmpresaDetalle detalle) {
		Cotizacion cotizacion = new Cotizacion();
		cotizacion.setAno(null);
		cotizacion.setMes(null);
		cotizacion.setNumDiasCotizados(null);
		cotizacion.setBaseCotizacionContingenciasComunes(null);
		cotizacion.setBaseCotizacionDesempleo(null);
		cotizacion.setObservaciones(null);
		return cotizacion;
	}
	
	private Vacaciones createDatosVacacionesCotizadasRecord() {
		
		return null;
	}
	
	
	
}
