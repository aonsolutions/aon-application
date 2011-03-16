package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.file.payroll.fdi.FDI;
import com.esferalia.aon.file.payroll.fdi.data.DEC;
import com.esferalia.aon.file.payroll.fdi.data.DIT;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.file.payroll.fdi.data.ODP;
import com.esferalia.aon.file.payroll.fdi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;


public class FDIWriter {
	
	private ETI eti;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	public FileOutput createFDI(List<ContractLeaveDetail> partes, String loggedUser ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( partes, loggedUser );
			File file = File.createTempFile("XXXXXXXX", ".FDI");
			FileFiller fdi = new FDI(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fdi.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} 
	}

	private ETI createETIRecord( List<ContractLeaveDetail> partes, String loggedUser ) {
		ETI eti = new ETI();
		// clave de autorizacion
		eti.setClave(00000000);
		for (ContractLeaveDetail parte: partes) {
			EMP emp = createEMPrecord(parte);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(ContractLeaveDetail detail) {
		Contract contract = detail.getContractLeave().getContract();
		EnterpriseActivity activity = detail.getContractLeave().getContract().getActivity();
		Enterprise enterprise = activity.getEnterprise();
		EMP emp = new EMP();
		String cccss = null;
//		if (activity.getRegimen() == Regimen.AGRARIO) {
//			cccss = "0613";
//		} else if (actividad.getRegimen() == Regimen.GENERAL) {
//			cccss = "0111";
//		} else if (actividad.getRegimen() == Regimen.ARTISTAS) {
//			cccss = "0112";
//		} else if (actividad.getRegimen() == Regimen.MARITIMO) {
//			cccss = "0811";
//		} else {
//			cccss = "    ";
//		}
		
		
		if (contract.getRegimeType() == SSRegimeType.GENERAL) {
			cccss = "0111";
		} else if (contract.getRegimeType() == SSRegimeType.AGRICULTURAL) {
			cccss = "0613";
		} else if (contract.getRegimeType() == SSRegimeType.ARTIST) {
			cccss = "0112";
		} else if (contract.getRegimeType() == SSRegimeType.SEA_WORKERS) {
			cccss = "0811";
		} else {
			cccss = "    ";
		}
		String provincia = null;
		String numero = null;
		if (contract.getEnterpriseCCC().getCcc() != null) {
			provincia = StringUtils.substring(contract.getEnterpriseCCC().getCcc(), 0,2);
			numero = StringUtils.substring(contract.getEnterpriseCCC().getCcc(), 2,12);
		}
		cccss += provincia;
		cccss += numero;
		emp.setCodigoCuentaCotizacionSeguridadSocial(cccss);
		DocumentType dType = enterprise.getRegistry().getDocumentType();
		String type;
		if(dType==DocumentType.NIF){
			type = "1";
		} else if(dType==DocumentType.PASSPORT){
			type = "2";
		} else if(dType==DocumentType.NIE){
			type = "6"; 
		} else {
			type = "9";
		}
		emp.setTipo(type);
		String country = Integer.toString(enterprise.getRegistry().getDocumentCountry().getIsoNum());
		if (StringUtils.isBlank(country)) {
			country = "   ";
		}
		emp.setPais(country);
		emp.setNumero(enterprise.getRegistry().getDocument());
		emp.setCodigoCuentaCotizacionPrincipal(cccss);
		TRA tra = createTRARecord(detail);
		emp.getTrabajadores().add(tra);
		return emp;
	}
	
	private TRA createTRARecord(ContractLeaveDetail detail) {
		Contract contract = detail.getContractLeave().getContract();
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		DocumentType type = contract.getPerson().getRegistry().getDocumentType();
		String tipo; 
		if(type == DocumentType.NIF){
			tipo = "1";
		} else if(type == DocumentType.PASSPORT){
			tipo = "2";
		} else if(type == DocumentType.NIE){
			tipo = "6";
		} 
//		- L - Españoles sin DNI no residentes en España
//		else if(type == DocumentType.NIF){
//			tipo = "L";
//		} 
//		- M - Extranjeros sin NIE en España
//		else if(type == DocumentType.NIF){
//			tipo = "M";
//		}
		else {
			tipo = "9";
		}
		String pais = Integer.toString(contract.getPerson().getRegistry().getDocumentCountry().getIsoNum());
		String doc = contract.getPerson().getRegistry().getDocument();
		StringBuilder builder = new StringBuilder(tipo);
		builder.append(StringUtils.isBlank(pais)?"   ":pais);
		builder.append(doc);
		tra.setIpf(builder.toString());
		DIT dit = createDITRecord( detail );
		List<DIT> dits = new LinkedList<DIT>();
		dits.add(dit);
		tra.setDatosIT(dits);
		return tra;
	}

	private DIT createDITRecord(ContractLeaveDetail detail) {
		DIT dit = new DIT();
		if (detail.getType() == LeaveReportType.CONFIRM) {
			dit.setAccion("PC ");
			ODP odp = createODPRecord(detail);
			dit.setOdp(odp);
		} else if (detail.getType() == LeaveReportType.LEAVE) {
			dit.setAccion("PB ");
			DEC dec = createDECRecord(detail);
			dit.setDec(dec);
		} else if (detail.getType() == LeaveReportType.DISCHARGE) {
			dit.setAccion("PA ");
			dit.setCausa(detail.getContractLeave().getDischargeCause().ordinal());
			dit.setFechaAlta(  Integer.parseInt( dateFormatter.format( detail.getContractLeave().getEndDate() )) );
		}
		if (detail.getContractLeave().getType() == LeaveType.COMMON_DISEASE) {
			dit.setContingencia("1");
		} else if (detail.getContractLeave().getType() == LeaveType.NON_OCCUPATIONAL_DISEASE) {
			dit.setContingencia("2");
		} else if (detail.getContractLeave().getType() == LeaveType.OCCUPATIONAL_DISEASE) { 
			dit.setContingencia("3");
			dit.setFechaATEP( Integer.parseInt( dateFormatter.format( detail.getContractLeave().getEndDate() )) );
		}
		dit.setFechaBaja(  Integer.parseInt( dateFormatter.format( detail.getContractLeave().getStartDate() )) );
		dit.setNumeroColegiado(detail.getCollegeNumber());
		dit.setCias(detail.getCias());
		if (dit.getNumeroColegiado() != null) {
			String prov = dit.getNumeroColegiado().substring(0, 2);
			dit.setNumeroColegiado(prov + dit.getNumeroColegiado());
		}
		if(detail.getContractLeave().getParent()!=null && detail.getContractLeave().getParent().getId()!=null){
			dit.setRecaida("S");
		} else {
			dit.setRecaida("N");
		}
		return dit;
	}

	private ODP createODPRecord(ContractLeaveDetail detail) {
		ODP odp = new ODP();
		odp.setFecha(Integer.parseInt( dateFormatter.format(detail.getDate())));
		odp.setNumero(detail.getConfirmOrder());
		odp.setEntidadAseguradora(0);
		odp.setFechaCambioEntidad(0);
		return odp;
	}

	private DEC createDECRecord(ContractLeaveDetail detail) {
		DEC dec = new DEC();
//		IContrato contrato = getParteITDAO().getContrato(parte);
		Contract contract = detail.getContractLeave().getContract();
//		if (contrato.getTipoContrato() == TipoContrato.TIEMPO_COMPLETO) {
//			dec.setBaseCotizacion( parte.getBaseRetribucionPeriodoAnterior() );
//			dec.setDiasCotizados( parte.getDiasPeriodoAnterior() );
//		} else {
//			dec.setSumaBasesCotizacion(parte.getBaseRetribucionPeriodoAnterior() );
//			dec.setSumaDiasCotizados( parte.getDiasPeriodoAnterior() );
//		}
		try {
			dec.setBaseCotizacion( contract.getSalary().getCommonBase() );
			dec.setDiasCotizados( contract.getSalary().getTimeUnits() );
		} catch (SalaryException e) {
			// TODO DAR SOPORTE A LA OBTENCION DE LAS BASES Y DIAS COTIZADOS
			dec.setBaseCotizacion( 0.0 );
			dec.setDiasCotizados( 0 );
		}
		dec.setCotizacionAnteriorHorasExtras(0.0);
		dec.setCotizacionAnteriorOtros(0.0);
		return dec;
	}

	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}
	
}
