package com.esferalia.aon.ui.payroll.utils;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.BasicCopySignatureType;
import com.esferalia.aon.payroll.enumeration.DisabilityCode;
import com.esferalia.aon.payroll.enumeration.DismissalCollective;
import com.esferalia.aon.payroll.enumeration.EducationalLevel;
import com.esferalia.aon.payroll.enumeration.EmployeeType;
import com.esferalia.aon.payroll.enumeration.EmploymentProgram;
import com.esferalia.aon.payroll.enumeration.OtherLaws;
import com.esferalia.aon.payroll.enumeration.SchoolWorkshop;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO100TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATOS;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETCOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETTTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSMEDIDASFOMENTOTYPE;


public class ContractXmlReader {
	
	
	public void completeContrataParams(CONTRATOS contratos, ContrataParams params) throws JAXBException, IOException {
			CONTRATO100TYPE c = (CONTRATO100TYPE) contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
					
			completeDatosGeneralesContrato(c.getDATOSGENERALESCONTRATO(), params);
			completeDatosMedidasFomento(c.getDATOSMEDIDASFOMENTO(), params);
			if(c.getDATOSANEXOCONTRATORELEVO()!=null){
				completeDatosAnexoContratoRelevo(c.getDATOSANEXOCONTRATORELEVO(), params);
			}
			if(c.getDATOSETCOTE()!=null){
				completeDatosEtCote(c.getDATOSETCOTE(), params);
			}
			if(c.getDATOSETT()!=null){
				completeDatosEtt(c.getDATOSETT(), params);
			}
			completeDatosComunicacionCopiaBasica(c.getDATOSCOMUNICACOPIABASICA(), params);
			
//			c.getDATOSETCOTE();
//			c.getDATOSCONTRATOEXTRANJERO();
//			c.getDATOSUSOLIBREEMPRESA();
	}
	
	private void completeDatosGeneralesContrato(DATOSGENERALESCONTRATOTYPE datos, ContrataParams params) {
		if(datos.getINDCONVENIOCOLECTIVO()!=null){
			params.setCollectiveAgreement(datos.getINDCONVENIOCOLECTIVO().equals("S")?true:false);
		}
		if(datos.getNIVELFORMATIVO()!=null){
			params.setEducationalLevel(EducationalLevel.valueOf("EL"+datos.getNIVELFORMATIVO()));
		}
		if(datos.getINDDISCAPACIDAD()!=null){
			params.setDisabilityCode(DisabilityCode.valueOf("DC_"+datos.getINDDISCAPACIDAD()));
			params.setDisabilityData(true);
		}
		if(datos.getCODIGOOCUPACION()!=null){
			params.setCno(getCno(datos.getCODIGOOCUPACION()));
		}
		if(datos.getIDOFERTA()!=null){
			params.setOffer(datos.getIDOFERTA());
			params.setOfferData(true);
		}
		if(datos.getCODIGOPROGRAMAEMPLEO()!=null){
			params.setEmploymentProgram(EmploymentProgram.valueOf("EP"+datos.getCODIGOPROGRAMAEMPLEO()));
			params.setEmploymentProgramData(true);
		}
		if(datos.getOTRASLEGISLACIONES()!=null){
			params.setOtherLaws(OtherLaws.valueOf("OL"+datos.getOTRASLEGISLACIONES()));
			params.setOlderThan52Data(true);
		}
		if(datos.getDATOSCAMPAÑAS()!=null){
			params.setCampaign(datos.getDATOSCAMPAÑAS());
			params.setCanpaignData(true);
		}
	}
	
	private void completeDatosMedidasFomento(DATOSMEDIDASFOMENTOTYPE datos, ContrataParams params) {
		params.setPermanentContractDevelopment(datos.getINDCOSTEDESPIDO().equals("1")?true:false);
		if(datos.getCODIGOCOLECTIVODESPIDO()!=null){
			params.setDismissalCollective(DismissalCollective.valueOf("DC"+datos.getCODIGOCOLECTIVODESPIDO()));
		}
	}
	
	private void completeDatosAnexoContratoRelevo(DATOSANEXOCONTRATORELEVOTYPE datos, ContrataParams params) {
		if(datos.getTIPOTRABAJADOR()!=null){
			params.setReliefEmployeeType(EmployeeType.valueOf("ET"+datos.getTIPOTRABAJADOR()));
		}
		Person person = new Person();
		person.setName(datos.getNOMBREAPELLIDOS().getNOMBRE());
		person.setFirstSurname(datos.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
		person.setSecondSurname(datos.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
		params.setReliefPerson(person);
		params.setReliefData(true);
	}

	private void completeDatosEtCote(DATOSETCOTYPE datos, ContrataParams params) {
		if(datos.getCODIGOETCOTE()!=null){
			params.setSchoolWorkshop(SchoolWorkshop.valueOf("SW_"+datos.getCODIGOETCOTE()));
		}
		params.setSchoolWorkshopData(true);
	}

	private void completeDatosEtt(DATOSETTTYPE datos, ContrataParams params) {
		params.setEttCif(datos.getCIFNIFEMPRESAUSUARIA().getCIFNIF());
		if(datos.getINDCTOPLANTILLA()!=null){
			params.setEttContractTemplate(datos.getINDCTOPLANTILLA().equals("S")?true:false);
		}
		if(datos.getINDEMPRESAEXTRANJERA()!=null){
			params.setEttForeignEnterprise(true);
		}
		params.setEttName(datos.getRAZONSOCIALEMPRESAUSUARIA());
		params.setEttData(true);
	}

	private void completeDatosComunicacionCopiaBasica(DATOSCOMUNICACOPIABASICATYPE datos, ContrataParams params) {
		params.setBasicCopyComments(datos.getTEXTOCOPIABASICA());
		if(datos.getTIPOFIRMA()!=null){
			params.setBasicCopySignatureType(BasicCopySignatureType.valueOf("BCST"+datos.getTIPOFIRMA()));
		}
	}
	
	private CNO getCno(String value){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CNO.class);
			return (CNO) bean.get(Integer.parseInt(value.substring(0, 4)));
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
}

