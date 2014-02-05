package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.file.payroll.afi.AFI;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.FAB;
import com.esferalia.aon.file.payroll.afi.data.RZS;
import com.esferalia.aon.file.payroll.afi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.batch.ContractBatchController.AFIAction;

public class AFIWriter {
	
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
			ETI eti = createETIRecord( contractList );
			File file = File.createTempFile("temp", ".AFI");
			FileFiller afi = new AFI(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(afi.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private ETI createETIRecord( List<Contract> contractList ) throws ManagerBeanException {
		ETI eti = new ETI();
		/*
		 Clave proporcionada por la seguridad social
		 */
		Integer clave = 12345678;
		eti.setClave(clave);
		for (Enterprise e: getEnterprises(contractList)) {
			EMP emp = createEMPrecord(e, contractList);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(Enterprise enterprise, List<Contract> contractList) throws  ManagerBeanException {
		EMP emp = new EMP();
//		EnterpriseController ent = (EnterpriseController)FormUtil.getController("enterprise");
//		ent.getCriteria().addEqualExpression(ent.getFieldName(ICompanyAlias.ENTERPRISE_ID), enterprise.getId());
//		ent.onSearch(null);
//		ent.onSelectFirst(null);
		// TODO se ha movido el ccc y activity de company a payroll
//		ent.initMainActiviy();
//		String ccc = ent.getCcc().getCcc();
//		emp.setCodigoCuentaCotizacionSeguridadSocial(ccc);
		
		/*
		T-3. Tipo de identificación de empresario
		1 D.N.I., N.I.F.
		2 Pasaporte
		6 Número de Identificación de Extranjero
		9 Código de Identificación Fiscal
		  Documento identificativo de un país comunitario Clave por asignar+
		*/
		String tipo = null;
		if(enterprise.getRegistry().getType()==null){
			tipo = "9";
		} else if(enterprise.getRegistry().getType() == RegistryType.NATURAL){
			tipo = "1";
		} else if(enterprise.getRegistry().getType() == RegistryType.LEGAL){
			tipo = "9";
		} 
		emp.setTipo(tipo);
		String pais = null;
		try {
			pais = enterprise.getRegistry().getDefaultAddress().getGeozone().getName();
		} catch (NullPointerException e) {
		}			
		if (StringUtils.isBlank(pais)) {
			pais = "   ";
		}
		emp.setPais(pais);
		emp.setNumero(enterprise.getRegistry().getDocument());
		emp.setCalificador("  ");
//		emp.setCodigoCuentaCotizacionPrincipal(obtainMainCCC(ccc).getFullCcc());
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
		rzs.setRazonSocial(enterprise.getRegistry().getFullName());
		emp.setRzs(rzs);
		for(Contract c: getContracts(enterprise, contractList)){
			TRA tra = createTRARecord(c);
			emp.getTrabajadores().add(tra);
		}
		return emp;
	}
	
	private TRA createTRARecord(Contract contract) throws ManagerBeanException {
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		String tipo = String.valueOf(contract.getPerson().getRegistry().getType()); 
		String pais = contract.getPerson().getRegistry().getDefaultAddress().getGeozone().getName();
		String doc = contract.getPerson().getRegistry().getDocument();
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?"   ":pais; 
		ipf += doc;
		tra.setIpf(ipf);
		FAB fab = createFABRecord(contract);
		tra.setFab(fab);
		return tra;
	}

	private FAB createFABRecord(Contract contract) throws  ManagerBeanException{
		FAB fab = new FAB();
//		AFIAction.MA.toString();
//		fab.setAccion("MA ");
		fab.setAccion(AFIAction.MA.toString()+" ");
		fab.setFechaReal(Integer.parseInt(dateFormatter.format(contract.getStartDate())));
		
		// TODO: 
		fab.setSituacion(null);
		fab.setGrupoCotizacion(null);
		fab.setClaveContratoTrabajo(null);
		fab.setCondicionDesempleado(null);
		fab.setMujerSubrepresentada(null);
		fab.setCoeficienteTiempoParcial(null);
		fab.setColectivoTrabajador(null);
		fab.setIndicadorImpresion(null);
		fab.setCategoriaProfesional(null);
		fab.setFechaNacimiento(null);
		fab.setSexo(null);
		fab.setTipoInactividad(null);
		fab.setExclusionDesempleo(null);
		fab.setCoeficienteActividadHuelgaParcial(null);
		fab.setMujerReincorporada(null);
		fab.setIncapacitadoReadmitido(null);
		fab.setAutonomo(null);
		fab.setGradoMinusvalia(null);
		fab.setFechaControl(null);
		fab.setExclusionSocialViolenciaDomestica(null);
		fab.setRentaActivaInsercion(null);
		fab.setCostratadasPostAlumbramiento(null);
		
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

	
	
}
