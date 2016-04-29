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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.afi.AFI;
import com.esferalia.aon.file.payroll.afi.data.AYN;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETF;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.FAB;
import com.esferalia.aon.file.payroll.afi.data.RZS;
import com.esferalia.aon.file.payroll.afi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T21;
import com.esferalia.aon.payroll.enumeration.ss.T7;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class AFIWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String AFI 				= "AFI";
	private final String WINSUITE_VERSION 	= "71WSxxx";
	private final String TESTING_CHECK		= "P";
	
	private final String WHITESPACE_1  = " ";
	private final String WHITESPACE_2  = "  ";
	private final String WHITESPACE_3  = "   ";
	private final String WHITESPACE_15 = "               ";
	private final String WHITESPACE_20 = "                    ";

	
	private ETI eti;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createAFI(List<Contract> contractList ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( isAfiTestEnvironmentActive(), contractList );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller afi = new AFI(eti, writer);
			FileOutput output = new FileOutput();
			output.setErrors(afi.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private ETI createETIRecord( boolean testFile, List<Contract> contractList ) throws ManagerBeanException {
		ETI eti = new ETI();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String f =  formatter.format(date);
		eti.setFecha(Integer.parseInt(f));
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		eti.setHora(Integer.parseInt(t));
		formatter = new SimpleDateFormat("ddHHmmss");
		eti.setFichero(formatter.format(date));
		
		eti.setIdentificador(AFI+WINSUITE_VERSION);
		String authorizationKey = getAuthorizationKey();
		if(StringUtils.isNotBlank(authorizationKey)){
			eti.setClave(StringUtils.leftPad(authorizationKey, 8, '0'));
		} else {
			AonUtil.addErrorMessage("No se ha definido la clave de autorización.");
		}
		eti.setPrueba( testFile?TESTING_CHECK:WHITESPACE_1 );
		for (Enterprise e: getEnterprises(contractList)) {
			EMP emp = createEMPrecord(e, contractList);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		createETFRecord( eti );
		return getEti();
	}

	private void createETFRecord( ETI eti ) throws ManagerBeanException {
		ETF etf = new ETF();
		etf.setIdentificador(eti.getIdentificador());
		etf.setClave(eti.getClave());
		etf.setPrueba(eti.getPrueba());
		etf.setFichero(eti.getFichero());
		etf.setFecha(eti.getFecha());
		etf.setHora(eti.getHora());
		eti.setEtf(etf);
	}
	
	private EMP createEMPrecord(Enterprise enterprise, List<Contract> contractList) throws  ManagerBeanException {
		EMP emp = new EMP();
		emp.setCodigoCuentaCotizacionSeguridadSocial(obtainMainCCC(enterprise));
		
		DocumentType docType = enterprise.getRegistry().getDocumentType();
    	if(docType==DocumentType.NIF){
    		emp.setTipoDocumento("1");
    	} else if(docType==DocumentType.PASSPORT){
    		emp.setTipoDocumento("2");
    	} else if(docType==DocumentType.NIE){
    		emp.setTipoDocumento("6");
    	} else if(docType==DocumentType.CIF){
    		emp.setTipoDocumento("9");
    	} else {
    		emp.setTipoDocumento("9");
    	}
		
		String pais = null;
		try {
			pais = String.valueOf(enterprise.getRegistry().getDocumentCountry().getIsoNum());
		} catch (NullPointerException e) {
		}			
		if (StringUtils.isBlank(pais)) {
			pais = WHITESPACE_3;
		}
		emp.setPais(pais);
		emp.setNumeroIdentificacion(autoComplete(enterprise.getRegistry().getDocument(), 14, "0", true));
		emp.setCalificador(WHITESPACE_2);
		emp.setCodigoCuentaCotizacionPrincipal(obtainMainCCC(enterprise));

		emp.setRzs(createRZSrecord(enterprise));
		emp.setAccion(WHITESPACE_3);
		for(Contract c: getContracts(enterprise, contractList)){
			TRA tra = createTRARecord(c);
			emp.getTrabajadores().add(tra);
		}
		return emp;
	}
	
	private RZS createRZSrecord(Enterprise enterprise) {
		RZS rzs = new RZS();
		rzs.setIndicador("0");
		/*
		TipoAlfabeticoEmpresario
		1 Individual
		2 Colectivo
		3 Sin personalidad jurídica
		4 Entidad u Organismo de las Admones.Públicas
		*/
		rzs.setTipoAlfabeticoEmpresario("1");
		rzs.setRazonSocial(enterprise.getRegistry().getName());
		return rzs;
	}
	
	private TRA createTRARecord(Contract contract) throws ManagerBeanException {
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
		FAB fab = createFABRecord(contract);
		tra.setAyn(createAYNRecord(contract));
		tra.setFab(fab);
		return tra;
	}
	
	private AYN createAYNRecord(Contract contract) {
		AYN ayn = new AYN();
		String ap1 = contract.getPerson().getFirstSurname();
		String ap2 = contract.getPerson().getSecondSurname();
		String n = contract.getPerson().getName();
		ayn.setApellido1(ap1!=null?ap1:WHITESPACE_20);
		ayn.setApellido2(ap2!=null?ap2:WHITESPACE_20);
		ayn.setNombre(n!=null?n:WHITESPACE_15);
		return ayn;
	}

	private FAB createFABRecord(Contract contract) throws  ManagerBeanException{
		FAB fab = new FAB();
		
		// TODO Actions  
//		MA  - Alta sucesiva
		fab.setAccion(autoComplete(T7.T7_MA.getCode(), 3, " ", false));
//		MB  - Baja
//		fab.setAccion(autoComplete(T7.T7_MB.getCode(), 3, " ", false));
//		MG  - Cambio de grupo de cotización
//		fab.setAccion(autoComplete(T7.T7_MG.getCode(), 3, " ", false));
//		MC  - Cambio de contrato (tipo/coeficiente)
//		fab.setAccion(autoComplete(T7.T7_MC.getCode(), 3, " ", false));
//		MT  - Cambio de ocupación
//		fab.setAccion(autoComplete(T7.T7_MT.getCode(), 3, " ", false));
//		CCP - Cambio de Categoría Profesional
//		fab.setAccion(autoComplete(T7.T7_CCP.getCode(), 3, " ", false));
//		CIT - Cierre de Períodos de Incapacidad Temporal
//		fab.setAccion(autoComplete(T7.T7_CIT.getCode(), 3, " ", false));
//		MHU - Mecanización de HUelga
//		fab.setAccion(autoComplete(T7.T7_MHU.getCode(), 3, " ", false));
		
		
		// TODO
		// Clave obligatoria para altas y bajas que indica el motivo de alta o baja. Ver capítulo Tablas.
		fab.setSituacion(autoComplete(T21.T21_1.getCode(), 2, "0", true));
		
		fab.setFechaReal(Integer.parseInt(dateFormatter.format(contract.getStartDate())));
		
		Integer quoteGroup = getQuoteGroup(contract);
		if(quoteGroup!=null){
			fab.setGrupoCotizacion(quoteGroup);
		}
		// TODO 
		fab.setGradoDiscapacidad(null);
		fab.setClaveContrato(Integer.parseInt(getContractCode(contract).getValue()));
		fab.setCondicionDesempleado(null);	// TODO
		fab.setMujerSubrepresentada(null);	// TODO
		if( !fab.getClaveContrato().toString().startsWith("1") && !fab.getClaveContrato().toString().startsWith("4") ){
			String weekHours = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.WEEK_HOURS.getName());
			if(weekHours!=null && NumberUtils.isNumber(weekHours)){
				Double hoursPercent = Double.parseDouble(weekHours) * 2.5;
				int percent = (int)CommonUtil.ceil(hoursPercent, 0);
				String percentValue = autoComplete(String.valueOf(percent), 2, "0", true);
				fab.setCoeficienteTiempoParcial( autoComplete(percentValue, 3, "0", false) );
			}
		}
		fab.setColectivoTrabajador(null);	// TODO
		/*
		IndicadorImpresion. Sus posibles valores son: 
		Espacio=no impresión 
		S= Impresión resolución
		C= Impresión resolución + IDC 
		I=IDC
		*/
		fab.setIndicadorImpresion(WHITESPACE_1);
		if(PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC()).equals("0911")
				|| contract.getEnterpriseCCC().getActivity().getType()==SSRegimeType.SEA_WORKERS){
			fab.setCategoriaProfesional(null);	// TODO
		}
		if(contract.getPerson().getBirthDate()!=null){
			fab.setFechaNacimiento(dateFormatter.format(contract.getPerson().getBirthDate()));
		}
		if(contract.getPerson().getGender()==Gender.MALE){
			fab.setSexo(1);
		} else if(contract.getPerson().getGender()==Gender.FEMALE){
			fab.setSexo(2);
		} else {
			fab.setSexo(1);
		}
		fab.setTipoInactividad(null);	// TODO
		fab.setExclusionDesempleo(null);	// TODO
		fab.setCoeficienteActividadHuelgaParcialEre(null);	// TODO
		fab.setMujerReincorporada(null);	// TODO
		fab.setIncapacitadoReadmitido(null);	// TODO
		fab.setTrabajadorDeAutonomo(null);	// TODO
		fab.setSemamaSegunConvenio5jr(null);	// TODO
		fab.setIndNumTrabajadoresEmpresa(null);	// TODO
		fab.setExclusionSocialVictimas(null);	// TODO
		fab.setRentaActivaInsercion(null);	// TODO
		fab.setCostratadasPostAlumbramiento(null);	// TODO
		
		return fab;
	}
	
	private List<Enterprise> getEnterprises(List<Contract> contractList){
		List<Enterprise> list = new LinkedList<Enterprise>();
		for(Contract c: contractList){
			if(!list.contains(c.getWorkPlace().getEnterprise())){
				list.add(c.getWorkPlace().getEnterprise());
			}
		}
		return list;
	}
	
	private List<Contract> getContracts(Enterprise enterprise, List<Contract> contractList) throws ManagerBeanException{
		List<Contract> list = new LinkedList<Contract>();
		for(Contract c: contractList){
			if(c.getWorkPlace().getEnterprise().equals(enterprise)){
				list.add(c);
			}
		}
		return list;
	}

	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	
	private String obtainMainCCC(Enterprise enterprise) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL );
			List<ITransferObject> cccList = bean.getList(criteria);
			if ( !cccList.isEmpty() ) {
				EnterpriseCCC ccc = (EnterpriseCCC) cccList.get(0);	
				return PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc();	
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}

	private Integer getQuoteGroup(Contract contract) {
		String q = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.QUOTE_GROUP.getName());
		return q!=null && !q.isEmpty()?Integer.parseInt(q):null;
	}
	private ContractCode getContractCode(Contract contract) {
		String tc2 = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.TC2.getName());
		return ContractCode.getContractCodeByValue(tc2);
	}
	
	private boolean isAfiTestEnvironmentActive() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + AppParam.PAY_afi_test_env_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getBoolean(1);
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return false;
	}
	
	private String getAuthorizationKey() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			} else {
				select = "SELECT value FROM app_param";
				select += " WHERE domain = " + DomainManager.getDomainProvider().getParentDomain();
				select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
				
				ps = conn.prepareStatement(select);
				rs = ps.executeQuery();
				if(rs.next()){
					return rs.getString(1);
				}
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
