package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.file.payroll.afi.AFI;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.FAB;
import com.esferalia.aon.file.payroll.afi.data.RZS;
import com.esferalia.aon.file.payroll.afi.data.TRA;
import com.esferalia.aon.payroll.Contract;

public class AFIWriter {
	
//	private ICommonsPayrollDAO commonsPayrollDAO;
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
//	private ICommonsPayrollDAO getCommonsPayrollDAO() {
//		if (commonsPayrollDAO == null) {
//			commonsPayrollDAO = CommonsPayrollDAOFactory.getInstance().getCommonsPayrollDAO();
//		}
//		return commonsPayrollDAO;
//	}
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createAFI(List<Contract> contractList ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( contractList );
			File file = File.createTempFile("XXXXXXXX", ".AFI");
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
//		IUsuario usuario = getCommonsPayrollDAO().getUsuarioActivo(loggedUser);
//		eti.setClave(Integer.parseInt(usuario.getAutorizacion()));
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
		EnterpriseController ent = (EnterpriseController)FormUtil.getController("enterprise");
		ent.getCriteria().addEqualExpression(ent.getFieldName(ICompanyAlias.ENTERPRISE_ID), enterprise.getId());
		ent.onSearch(null);
		ent.onSelectFirst(null);
		ent.initMainActiviy();
		String ccc = ent.getCcc().getCcc();
		emp.setCodigoCuentaCotizacionSeguridadSocial(ccc);
		String tipo = String.valueOf(enterprise.getRegistry().getType().ordinal());
		if (StringUtils.isBlank(tipo)) {
			tipo = "9";
		}
		emp.setTipo(tipo);
		String pais = null;
		try {
			pais = enterprise.getRegistry().getDefaultAddress().getGeozone().getName();
		} catch (ManagerBeanException e) {
		}			
		if (StringUtils.isBlank(pais)) {
			pais = "   ";
		}
		emp.setPais(pais);
		emp.setNumero(enterprise.getRegistry().getDocument());
		emp.setCalificador("  ");
		emp.setCodigoCuentaCotizacionPrincipal(ccc);
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
		fab.setAccion("MA ");
		fab.setFechaReal(Integer.parseInt(dateFormatter.format(contract.getStartDate())));
		/*
		Acciones a nivel de Trabajador Se cumplimentan en el segmento FAB
		MA Alta sucesiva
		MB Baja
		MG Cambio de grupo de cotización
		ME Eliminación de movimientos previos
		MC Cambio de contrato (tipo/coeficiente)
		MT Cambio de ocupación
		MD Eliminación de altas consolidadas.
		MR Eliminación de bajas consolidadas
		CP Consulta de movimientos previos de un afiliado
		CH Consulta de situación del afiliado en la empresa
		CE Informe de Situación I.T. por Contingencias Comunes
		CD Duplicados de TA2.
		CA Corrección del alta, régimen 0132
		CB Corrección de la baja, régimen 0132
		CCP Cambio de Categoría Profesional
		CCJ Cambio de Coeficiente Reductor de la Edad Jubilación
		MJR Mecanización de Jornadas Reales (régimen 0613)
		MFR Modificación Fecha Real del Alta (régimen 0613)
		ASA Anotación de periodos de situaciones adicionales de
		afiliación
		MSA Modificación de periodos de situaciones adicionales de
		afiliación
		ESA Eliminación de periodos de situaciones adicionales de
		afiliación
		CJR Informe de Jornadas Reales
		ASC Alta de Subcontratación o Cesión
		MSC Modificación de Subcontratación o Cesión
		ESC Eliminación de Subcontratación o Cesión
		ACT Anotación Convenio Colectivo de trabajador
		ADT Anotación de Días Trabajados
		EDT Eliminación de Días Trabajados
		AMC Anotación Modalidad de cotización
		AIT Anotación de Períodos de Incapacidad Temporal
		MIT Modificación de Períodos de Incapacidad Temporal
		EIT Eliminación de Períodos de Incapacidad Temporal
		*/
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
