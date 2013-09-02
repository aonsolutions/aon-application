package com.esferalia.aon.ui.sepe.controller.handler;

import java.io.IOException;
import java.util.Date;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataProrrogasHandler implements IContrataHandler {
	
	private static final String AVAILABLE_CONTRACT_CODE_COMMUNICATION = "";
	
	private Contract contract;

	private ContrataProrrogaParams params;
	
	private ContractCode contractCode;

	
	@Override
	public Contract getContract() {
		return contract;
	}

	@Override
	public ContrataProrrogaParams getParams() {
		if(params==null){
			params = new ContrataProrrogaParams();
		}
		return params;
	}
	
	@Override
	public ContractCode getContractCode() {
		return contractCode;
	}
	
	@Override
	public boolean isCommunicationAvailable(){
		if(getContractCode()!=null){
			return AVAILABLE_CONTRACT_CODE_COMMUNICATION.contains(getContractCode().getValue());
		}
		return false;
	}

	@Override
	public void initialize(Contract contract){
		SEPEUtils utils = new SEPEUtils();
		this.contract = contract;
		this.contractCode = ContractCode.getContractCodeByValue( utils.getContractDataMap(this.contract).get(ContextVariable.TC2.getName()) );
	}
	
	@Override
	public void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException{
		// TODO make ContrataReader return correct params type
		if(attach==null){
			attach = new ContractAttachment();
		} 
//		else {
//			ContrataReader reader = new ContrataReader();
//			this.params = reader.readFile( new ByteArrayInputStream(contrataAttach.getData()) );
//		}
	}

	/**
	<xsd:documentation xml:lang="es">Indicador de convenio colectivo.   Obligatorio para prorrogar : 
		- contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
		- contratos de código 421 cuando su duración está entre 24 y 36 meses.  
		- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 
				cuando su duración está entre 36 y 48 meses.    
		Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones.</xsd:documentation>
	 * 
	 * @return
	 */
	public boolean isIndicadorConvCol(){
		// TODO
		Integer months = getMonthsBetweenDates(getContract().getStartDate(), getContract().getEndDate());
		if( months!=null && months>=6 && months<=12 ){
			if ( getContractCode() == ContractCode.C402 || getContractCode() == ContractCode.C502 ){
				return true;
			}
		}
		if( months!=null && months>=24 && months<=36 ){
			if ( getContractCode() == ContractCode.C421 ){
				return true;
			}
		}
		if( months!=null && months>=36 && months<=48 ){
			if ( getContractCode() == ContractCode.C401 || getContractCode() == ContractCode.C501
					|| getContractCode() == ContractCode.C450 || getContractCode() == ContractCode.C550 ){
				return true;
			}
		}
		return false;
	}
	
	/**
		<xsd:documentation xml:lang="es">Indicador de empresa. 
			Obligatorio (S/N) para los códigos 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir de 19/09/2010 
			cuando no cumpla con la duración válida y tampoco esté acogido a convenio colectivo que justifique esta duración. 
			Indica si la prórroga se realiza (S) por la Administración Pública , Organismo Público vinculado o Universidad , 
			o no es una empresa de estos tipos (N). </xsd:documentation>
	 * 
	 * @return
	 */
	public boolean isIndEmpresaAappUniversidad(){
		if ( getContractCode() == ContractCode.C401 || getContractCode() == ContractCode.C501
				|| getContractCode() == ContractCode.C450 || getContractCode() == ContractCode.C550
				|| getContractCode() == ContractCode.C550 ) {
			return true;
		}
		return false;
	}
	
	/**
		<xsd:documentation xml:lang="es">Indicador de autorización de la duración. 
			Obligatorio (S/N) para los códigos 420, 421, 450 con modalidad 420, 450 con modalidad 421, 520 y 550 con modalidad 520 iniciados 
			a partir de 19/09/2010 cuando no cumpla con la duración válida y tampoco esté acogido a convenio colectivo que justifique esta duración. 
			Indica si han existido períodos de incapacidad , maternidad, adopción , riesgo o paternidad que justifiquen la superación 
			de la duración máxima (S) , o no (N). </xsd:documentation>
	 * 
	 * @return
	 */
	public boolean isIndPeriodoAutorizaDuracion(){
		if ( getContractCode() == ContractCode.C420 || getContractCode() == ContractCode.C421
			|| getContractCode() == ContractCode.C450 || getContractCode() == ContractCode.C520
			|| getContractCode() == ContractCode.C550 ) {
			return true;
		}
		return false;
	}
	
	/**
		<xsd:documentation xml:lang="es">Número de horas de formación. Formato: HHHHMM (Horas(4)Minutos(2)). 
			Opcionales para las prórrogas de contratos de código 421 (Formación), para el resto no deben ser cumplimentadas. 
			En el caso de no ser especificadas en las prórrogas de los contratos de Formación,  le son asignadas a la prórroga las que tuviera 
			el contrato que está prorrogando. </xsd:documentation>
	 * 
	 * @return
	 */
	public boolean isHorasFormacion(){
		if ( getContractCode() == ContractCode.C421 ) {
			return true;
		}
		return false;
	}
	
	/**
		<xsd:documentation xml:lang="es">Indicador de duración inferior. Obligatorio para contratos de códigos 452 y 552 cuando 
			su duración está entre 6 y 12 meses.  Refleja si la duración inferior del contrato está aconsejada (S) ó no (N) por 
			los Servicios Sociales Públicos para el seguimiento del proceso de inserción. </xsd:documentation>
	 * 
	 * @return
	 */
	public boolean isIndDuracInferior(){
		Integer months = getMonthsBetweenDates(getContract().getStartDate(), getContract().getEndDate());
		if( months!=null && months>=6 && months<=12 ){
			if( getContractCode() == ContractCode.C452 || getContractCode() == ContractCode.C552 ) {
				return true;
			}
		}
		return false;
	}
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}
	
}
