package com.esferalia.aon.ui.payroll.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Enterprise;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.fdi.FDI;
import com.esferalia.aon.file.payroll.fdi.data.DEC;
import com.esferalia.aon.file.payroll.fdi.data.DIT;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.ETF;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.file.payroll.fdi.data.ODP;
import com.esferalia.aon.file.payroll.fdi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.DischargeCause;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T34;
import com.esferalia.aon.payroll.enumeration.ss.T35;
import com.esferalia.aon.payroll.enumeration.ss.T36;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class FDIWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String FDI 				= "FDI";
	private final String WINSUITE_VERSION 	= "30WSxxx";
	
	private ETI eti;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createFDI(List<ContractLeaveDetail> partes, String loggedUser ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( partes, loggedUser );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller fdi = new FDI(eti, writer);
			FileOutput output = new FileOutput();
			output.setErrors(fdi.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} 
	}

	private ETI createETIRecord( List<ContractLeaveDetail> partes, String loggedUser ) {
		ETI eti = new ETI();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String f =  formatter.format(date);
		eti.setFecha(Integer.parseInt(f));
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		eti.setHora(Integer.parseInt(t));
		formatter = new SimpleDateFormat("ddMMHHmm");
		eti.setFichero(formatter.format(date));
		
		eti.setIdentificador(FDI+WINSUITE_VERSION);
		String authorizationKey = getAuthorizationKey(DomainManager.getCurrentDomain());
		if(StringUtils.isBlank(authorizationKey)){
			authorizationKey = getAuthorizationKey(DomainManager.getDomainProvider().getParentDomain());
		}
		if(StringUtils.isNotBlank(authorizationKey)){
			eti.setClave(StringUtils.leftPad(authorizationKey, 8, '0'));
		} else {
			AonUtil.addErrorMessage("No se ha definido la clave de autorizaci�n.");
		}
		EMP emp = null;
		for (ContractLeaveDetail detail: partes) {
			if(emp==null || !emp.getNumero().equals(detail.getContractLeave().getContract().getActivity().getEnterprise().getRegistry().getDocument())){
				emp = createEMPrecord(detail.getContractLeave().getContract());
				eti.getEmpresas().add(emp);
			}
			TRA tra = createTRARecord(detail);
			emp.getTrabajadores().add(tra);
		}
	setEti( eti );
		createETFRecord( eti );
		return getEti();
		
	}
	
	private void createETFRecord( ETI eti ) {
		ETF etf = new ETF();
		etf.setIdentificador(eti.getIdentificador());
		etf.setClave(eti.getClave());
//		etf.setPrueba(eti.getPrueba());
		etf.setFichero(eti.getFichero());
		etf.setFecha(eti.getFecha());
		etf.setHora(eti.getHora());
		eti.setEtf(etf);
	}
	
	private EMP createEMPrecord(Contract contract) {
		Enterprise enterprise = contract.getActivity().getEnterprise();
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
//		TRA tra = createTRARecord(detail);
//		emp.getTrabajadores().add(tra);
		return emp;
	}
	
	private TRA createTRARecord(ContractLeaveDetail detail) {
		Contract contract = detail.getContractLeave().getContract();
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		String tipo;
		if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIF){
			tipo = "1";
		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.PASSPORT){
			tipo = "2";
		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIE){
			tipo = "6";
		} else {
			tipo = "1";
		}
		String pais = String.valueOf(contract.getPerson().getRegistry().getDocumentCountry().getIsoNum());
		String doc = autoComplete(contract.getPerson().getRegistry().getDocument(), 14, "0", true);
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?"   ":pais; 
		ipf += doc;
		tra.setIpf(ipf);
		tra.setDatosIT(createDITRecord( detail ));
		return tra;
	}

	private DIT createDITRecord(ContractLeaveDetail detail) {
		DIT dit = new DIT();
		if (detail.getType() == LeaveReportType.CONFIRM) {
			dit.setAccion(T34.T34_PC.getCode()+" ");
			dit.setOdp(createODPRecord(detail));
		} else if (detail.getType() == LeaveReportType.LEAVE) {
			dit.setAccion(T34.T34_PB.getCode()+" ");
			dit.setDec(createDECRecord(detail));
		} else if (detail.getType() == LeaveReportType.DISCHARGE) {
			dit.setAccion(T34.T34_PA.getCode()+" ");
			if(detail.getContractLeave().getDischargeCause()!=null){
				if (detail.getContractLeave().getDischargeCause() == DischargeCause.CURATION) {
//					01	Curación		
					dit.setCausa(T36.T36_01.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.DEATH) {
//					02	Fallecimiento - Procesos de duración inferior a 365 días		
					dit.setCausa(T36.T36_02.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.MEDICAL_INSPECTION) {
//					03	Inspección médica - Procesos de duración inferior a 365 días		
					dit.setCausa(T36.T36_03.getCode());				
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.DISABILITY) {
//					04	Propuesta incapacidad
					dit.setCausa(T36.T36_04.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.TIME_EXHAUSTION) {
//					05	Agotamiento de plazo
					dit.setCausa(T36.T36_05.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.IMPROVEMENT) {
//					06	Mejoría que permite realizar el trabajo habitual
					dit.setCausa(T36.T36_06.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.ENTERING) {
//					07	Incomparecencia		
					dit.setCausa(T36.T36_07.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.CONTROL_INSS) {
//					10	Control INSS duración 12 meses
					dit.setCausa(T36.T36_10.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.RECOVERY) {
//					17	Recuperación capacidad profesional
					dit.setCausa(T36.T36_17.getCode());
				} else if (detail.getContractLeave().getDischargeCause() == DischargeCause.ENTERING_EDUCATION) {
//					18	Incomparecencia contratos formación
					dit.setCausa(T36.T36_18.getCode());
				}
//				20	Inicio Maternidad comunicada por el SPS		
//				53	Alta inspección INSS		
//				55	Propuesta IP antes 12 meses		
//				56	Fallecimiento		
//				57	Alta MATEPSS (Art. 128)
				
			}
			dit.setFechaAlta( Integer.parseInt( dateFormatter.format( detail.getContractLeave().getEndDate() )) );
		}
		if(dit.getCausa()==null){
			dit.setCausa("00");
		}
		if (detail.getContractLeave().getType() == LeaveType.COMMON_DISEASE) {
//			1	Enfermedad común		
			dit.setContingencia(T35.T35_1.getCode());
		} else if (detail.getContractLeave().getType() == LeaveType.NON_OCCUPATIONAL_DISEASE) {
//			2	Accidente no laboral		
			dit.setContingencia(T35.T35_2.getCode());
		} else if (detail.getContractLeave().getType() == LeaveType.OCCUPATIONAL_DISEASE) { 
//			3	Accidente de trabajo		
			dit.setContingencia(T35.T35_3.getCode());
			dit.setFechaATEP( Integer.parseInt( dateFormatter.format( detail.getContractLeave().getStartDate() )) );
		} else {
			dit.setContingencia("0");
		}
//		4	Enfermedad profesional		
//		5	Periodos de observación de enfermedad profesional

		
		dit.setFechaBaja(  Integer.parseInt( dateFormatter.format( detail.getContractLeave().getStartDate() )) );
		if( StringUtils.isBlank(detail.getCollegeNumber()) && StringUtils.isBlank(detail.getCias()) ){
			AonUtil.addErrorMessage("Ausencia de n� de colegiado o CIAS para el parte de "+detail.getContractLeave().getContract().getPerson().getFullName());
		}
		dit.setNumeroColegiado(detail.getCollegeNumber());
		dit.setCias(detail.getCias());
		if (StringUtils.isNotBlank(dit.getNumeroColegiado())) {
			String prov = dit.getNumeroColegiado().substring(0, 2);
			dit.setNumeroColegiado(prov + dit.getNumeroColegiado());
		}
		if (detail.getType() != LeaveReportType.CONFIRM) {
			if(detail.getContractLeave().getParent()!=null && detail.getContractLeave().getParent().getId()!=null){
				dit.setRecaida("S");
			} else {
				dit.setRecaida("N");
			}
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
		// TODO DAR SOPORTE A LA OBTENCION DE LAS BASES Y DIAS COTIZADOS
		String code = getContractCode(detail.getContractLeave().getContract()).getValue();
		Double baseReg = getBaseReg(detail);
		if(baseReg==null){
			baseReg = 0.0;
			AonUtil.addErrorMessage("No se ha encontrado la base reguladora de " 
					+ detail.getContractLeave().getContract().getPerson().getFullName());
		}
		if(code.startsWith("1") || code.startsWith("4")){
			dec.setBaseCotizacion( baseReg * 30 );
			dec.setDiasCotizados( 30 );
		} else {
			dec.setSumaBasesCotizacion( baseReg * 30 );
			dec.setSumaDiasCotizados( 30 );
		}
		dec.setCotizacionAnteriorHorasExtras(0.0);
		dec.setCotizacionAnteriorOtros(0.0);
		return dec;
	}
	
	
	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	
	private ContractCode getContractCode(Contract contract) {
		String tc2 = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.TC2.getName());
		return ContractCode.getContractCodeByValue(tc2);
	}
	
	private Double getBaseReg(ContractLeaveDetail detail) {
		Double base = null;
		if(detail.getContractLeave().getType()==LeaveType.OCCUPATIONAL_DISEASE){
			base = detail.getContractLeave().getDailyCgpBase();
		} else {
			base = detail.getContractLeave().getDailyCgcBase();
		}
		try {
			if(base==null){
				ISalary salary = PayrollUtils.getInstance().getSalary(detail.getContractLeave().getContract(), detail.getDate(), detail.getDate());
				if(salary!=null){
					Date start = DateUtils.setDays(detail.getDate(), 1);
					Date end = DateUtils.addMonths(detail.getDate(), 1);
					end = DateUtils.setDays(end, 1);
					end = DateUtils.addDays(end, -1);
					List<SalaryData> list = SEPEUtils.getInstance()
							.getSalaryDataList(salary, start, end,
									ContextVariable.REGULATORY_BASE.getName());
					if(list!=null && !list.isEmpty()){
						base = Double.valueOf(list.get(0).getExpression());
					}
				} else {
					AonUtil.addErrorMessage("No hay nomina de la que obtener la base reguladora de " 
							+ detail.getContractLeave().getContract().getPerson().getFullName());
				}
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage("ERROR al obtener la base reguladora de " 
					+ detail.getContractLeave().getContract().getPerson().getFullName());
		}
		return base;
	}
	
	private String getAuthorizationKey(Integer domain) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + domain;
			select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}

}
