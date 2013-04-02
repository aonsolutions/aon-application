package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.sepe.api.contrata.contratos.*;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.contrata.enumeration.TBONVFOR;
import com.esferalia.aon.payroll.contrata.enumeration.TEIINTER;
import com.esferalia.aon.payroll.contrata.enumeration.TEJINDIS;
import com.esferalia.aon.payroll.contrata.enumeration.TEOCOLDE;
import com.esferalia.aon.payroll.contrata.enumeration.TEQPTIEM;
import com.esferalia.aon.payroll.contrata.enumeration.TERFIRCB;
import com.esferalia.aon.payroll.contrata.enumeration.TESCETCO;
import com.esferalia.aon.payroll.contrata.enumeration.TETPGMEM;
import com.esferalia.aon.payroll.contrata.enumeration.TEWEINVE;
import com.esferalia.aon.payroll.contrata.enumeration.TEXTINVE;
import com.esferalia.aon.payroll.contrata.enumeration.TEYTRELE;
import com.esferalia.aon.payroll.contrata.enumeration.THPCOLFO;
import com.esferalia.aon.payroll.contrata.enumeration.THYDISLE;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.IProrrogaType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContrataReader {
	
	private final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	private final String CONTRATA_TRANSFORMACIONES_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.transformaciones";
	private final String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.prorrogas";
	
	private ContrataParams params;
	
	public ContrataParams readFile(ContractAttachment attach) throws ManagerBeanException, IOException{
		
		
		boolean contratoFile = false;
		boolean transfonacionFile = false;
		boolean prorrogaFile = false;
		
		String code = getContractDataMap(attach.getContract()).get(ContextVariable.TC2.getName());

		if(code.equals(ContractCode.C109.getValue())
				 || code.equals(ContractCode.C139.getValue())
				 || code.equals(ContractCode.C189.getValue())
				 || code.equals(ContractCode.C209.getValue())
				 || code.equals(ContractCode.C239.getValue())
				 || code.equals(ContractCode.C289.getValue())
				 || code.equals(ContractCode.C309.getValue())
//	TODO: nueva clave de contrato - Boletin Noticias RED 2012/05
//				 || code.equals(ContractCode.C339.getValue())
				 || code.equals(ContractCode.C389.getValue()) ){
			transfonacionFile = true;
		} else if (code.equals(ContractCode.C408.getValue())
				 || code.equals(ContractCode.C418.getValue())
				 || code.equals(ContractCode.C508.getValue())
				 || code.equals(ContractCode.C518.getValue()) ){
			String msg = "Contrato no implementado para el fichero contrat@";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else if( StringUtils.isBlank(code) ) {
			// DO NOTHING
			String msg = "Contrato no reconocido";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else {
			contratoFile = true;
		}
		
		
		
		byte[] f = attach.getData();
		if(f!=null && f.length>0){
			File file = File.createTempFile("aon-temp", ".XML");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(f);
			fos.close();
			try {
			
				CONTRATOS contratos = null;
				TRANSFORMACIONES transformaciones = null;
//				PRORROGAS prorrogas = null;
				if( contratoFile ){
					JAXBContext jaxbContext = JAXBContext.newInstance(CONTRATA_CONTRATOS_MODEL_PATH);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					unmarshaller.setEventHandler(new ContractValidationEventHandler());
					contratos = (CONTRATOS) unmarshaller.unmarshal(file);
				} else if( transfonacionFile ) {
					JAXBContext jaxbContext = JAXBContext.newInstance(CONTRATA_TRANSFORMACIONES_MODEL_PATH);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					unmarshaller.setEventHandler(new ContractValidationEventHandler());
					transformaciones = (TRANSFORMACIONES) unmarshaller.unmarshal(file);
				} 
//				else if( prorrogaFile ) {
//					JAXBContext jaxbContext = JAXBContext.newInstance(CONTRATA_PRORROGAS_MODEL_PATH);
//					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//					unmarshaller.setEventHandler(new ContractValidationEventHandler());
//					prorrogas = (PRORROGAS) unmarshaller.unmarshal(file);
//				}
				

				this.params = new ContrataParams();
				
				
				if( contratoFile ){
					IContratoType contratoType = (IContratoType) contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
					completeContratosParams(contratoType, params);
				} else if( transfonacionFile ) {
					ITransformacionType transformacionType = (ITransformacionType) transformaciones.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().get(0);
					completeTransformacionesParams(transformacionType, params);
				} 
//				else if( prorrogaFile ) {
//					IProrrogaType prorrogaType = (IProrrogaType) prorrogas.getPRORROGATIPO().get(0);
//					completeProrrogasParams(prorrogaType, params);
//				}

				return params;
			} catch (JAXBException e) {
				String msg = "Error al obtener los datos del documento xml de contrata";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
		return null;
	}
	
	public void completeContratosParams(IContratoType o, ContrataParams params) throws JAXBException, IOException {
		
		if (o instanceof CONTRATO100TYPE) {
			readContract100((CONTRATO100TYPE)o);
		} else if (o instanceof CONTRATO130TYPE) {
			readContract130((CONTRATO130TYPE)o);
		} else if (o instanceof CONTRATO150TYPE) {
			readContract150((CONTRATO150TYPE)o);
		} else if (o instanceof CONTRATO200TYPE) {
			readContract200((CONTRATO200TYPE)o);
		} else if (o instanceof CONTRATO230TYPE) {
			readContract230((CONTRATO230TYPE)o);
		} else if (o instanceof CONTRATO250TYPE) {
			readContract250((CONTRATO250TYPE)o);
		} else if (o instanceof CONTRATO300TYPE) {
			readContract300((CONTRATO300TYPE)o);
		} else if (o instanceof CONTRATO330TYPE) {
			readContract330((CONTRATO330TYPE)o);
		} else if (o instanceof CONTRATO350TYPE) {
			readContract350((CONTRATO350TYPE)o);
		} else if (o instanceof CONTRATO401TYPE) {
			readContract401((CONTRATO401TYPE)o);
		} else if (o instanceof CONTRATO402TYPE) {
			readContract402((CONTRATO402TYPE)o);
		} else if (o instanceof CONTRATO403TYPE) {
			readContract403((CONTRATO403TYPE)o);
		} else if (o instanceof CONTRATO410TYPE) {
			readContract410((CONTRATO410TYPE)o);
		} else if (o instanceof CONTRATO420TYPE) {
			readContract420((CONTRATO420TYPE)o);
		} else if (o instanceof CONTRATO421TYPE) {
			readContract421((CONTRATO421TYPE)o);
		} else if (o instanceof CONTRATO430TYPE) {
			readContract430((CONTRATO430TYPE)o);
		} else if (o instanceof CONTRATO441TYPE) {
			readContract441((CONTRATO441TYPE)o);
		} else if (o instanceof CONTRATO450TYPE) {
			readContract450((CONTRATO450TYPE)o);
		} else if (o instanceof CONTRATO452TYPE) {
			readContract452((CONTRATO452TYPE)o);
		} else if (o instanceof CONTRATO501TYPE) {
			readContract501((CONTRATO501TYPE)o);
		} else if (o instanceof CONTRATO502TYPE) {
			readContract502((CONTRATO502TYPE)o);
		} else if (o instanceof CONTRATO503TYPE) {
			readContract503((CONTRATO503TYPE)o);
		} else if (o instanceof CONTRATO510TYPE) {
			readContract510((CONTRATO510TYPE)o);
		} else if (o instanceof CONTRATO520TYPE) {
			readContract520((CONTRATO520TYPE)o);
		} else if (o instanceof CONTRATO530TYPE) {
			readContract530((CONTRATO530TYPE)o);
		} else if (o instanceof CONTRATO540TYPE) {
			readContract540((CONTRATO540TYPE)o);
		} else if (o instanceof CONTRATO541TYPE) {
			readContract541((CONTRATO541TYPE)o);
		} else if (o instanceof CONTRATO550TYPE) {
			readContract550((CONTRATO550TYPE)o);
		} else if (o instanceof CONTRATO552TYPE) {
			readContract552((CONTRATO552TYPE)o);
		} else if (o instanceof CONTRATO970TYPE) {
			readContract970((CONTRATO970TYPE)o);
		} else if (o instanceof CONTRATO980TYPE) {
			readContract980((CONTRATO980TYPE)o);
		} else if (o instanceof CONTRATO990TYPE) {
			readContract990((CONTRATO990TYPE)o);
		}
	}
	
	public void completeTransformacionesParams(ITransformacionType transformacionType, ContrataParams params) throws JAXBException, IOException {
		
//		if (transformacionType instanceof TRANSFORMACION109TYPE) {
//			readTransformacion109((TRANSFORMACION109TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION139TYPE) {
//			readTransformacion139((TRANSFORMACION139TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION189TYPE) {
//			readTransformacion189((TRANSFORMACION189TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION209TYPE) {
//			readTransformacion209((TRANSFORMACION209TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION239TYPE) {
//			readTransformacion239((TRANSFORMACION239TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION289TYPE) {
//			readTransformacion289((TRANSFORMACION289TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION309TYPE) {
//			readTransformacion309((TRANSFORMACION309TYPE)transformacionType);
////	TODO: nueva clave de contrato - Boletin Noticias RED 2012/05
////		} else if (transformacionType instanceof TRANSFORMACION339TYPE) {
////			readTransformacion339((TRANSFORMACION339TYPE)transformacionType);
//		} else if (transformacionType instanceof TRANSFORMACION389TYPE) {
//			readTransformacion389((TRANSFORMACION389TYPE)transformacionType);
//		}
		
		
	}
			
	public void completeProrrogasParams(IProrrogaType prorrogaType, ContrataParams params) throws JAXBException, IOException {
		// TODO
//	} else if (code.equals(ContractCode.C408.getValue())
//			|| code.equals(ContractCode.C418.getValue())
//			|| code.equals(ContractCode.C508.getValue())
//			|| code.equals(ContractCode.C518.getValue()) ){	
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
	
	private void readContract100(CONTRATO100TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract130(CONTRATO130TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
	    completeDatosBonificacion(o.getDATOSBONIFICACION());
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
	    completeDatosEtCote(o.getDATOSETCOTE());
	    completeDatosEtt(o.getDATOSETT()); 
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
	    completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract150(CONTRATO150TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract200(CONTRATO200TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
	}
	private void readContract230(CONTRATO230TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract250(CONTRATO250TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract300(CONTRATO300TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
	    completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
	    completeDatosEtCote(o.getDATOSETCOTE());
	    completeDatosEtt(o.getDATOSETT()); 
	    completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
    	completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract330(CONTRATO330TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract350(CONTRATO350TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract401(CONTRATO401TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract402(CONTRATO402TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract403(CONTRATO403TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract410(CONTRATO410TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract420(CONTRATO420TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract421(CONTRATO421TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract430(CONTRATO430TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract441(CONTRATO441TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract450(CONTRATO450TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract452(CONTRATO452TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract501(CONTRATO501TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract502(CONTRATO502TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract503(CONTRATO503TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract510(CONTRATO510TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract520(CONTRATO520TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract530(CONTRATO530TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract540(CONTRATO540TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract541(CONTRATO541TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract550(CONTRATO550TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract552(CONTRATO552TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract970(CONTRATO970TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract980(CONTRATO980TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract990(CONTRATO990TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	
	
	private void completeDatosTrabajador(DATOSTRABAJADORTYPE datos) {
		if(datos != null){
			params.setTownCode(datos.getMUNICIPIORESIDENCIA());
		}
	}
	
	private void completeDatosGeneralesContrato(DATOSGENERALESCONTRATOTYPE datos) {
		if(datos != null){
			if(datos.getINDCONVENIOCOLECTIVO()!=null){
				params.setCollectiveAgreement(datos.getINDCONVENIOCOLECTIVO().equals("S")?true:false);
			}
			if(datos.getNIVELFORMATIVO()!=null){
				params.setNivelFormativo(TBONVFOR.getEnumByValue(datos.getNIVELFORMATIVO()));
			}
			if(datos.getINDDISCAPACIDAD()!=null){
				params.setIndDiscapacidad(TEJINDIS.getEnumByValue(datos.getINDDISCAPACIDAD()));
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
				params.setCodigoProgramaEmpleo(TETPGMEM.getEnumByValue(datos.getCODIGOPROGRAMAEMPLEO()));
				params.setEmploymentProgramData(true);
			}
			if(datos.getOTRASLEGISLACIONES()!=null){
				params.setOtrasLegislaciones(THYDISLE.getEnumByValue(datos.getOTRASLEGISLACIONES()));
				params.setOlderThan52Data(true);
			}
			if(datos.getDATOSCAMPAÑAS()!=null){
				params.setCampaignGeozone(datos.getDATOSCAMPAÑAS().substring(0, 2));
				params.setCampaign(datos.getDATOSCAMPAÑAS().substring(2, 5));
				params.setCampaignYear(datos.getDATOSCAMPAÑAS().substring(datos.getDATOSCAMPAÑAS().length()-4, datos.getDATOSCAMPAÑAS().length()));
				params.setCanpaignData(true);
			}
		}
	}
	private void completeDatosMedidasFomento(DATOSMEDIDASFOMENTOTYPE datos) {
		if(datos != null){
			params.setMedidasFomentoData(true);
			params.setIndCosteDespido(datos.getINDCOSTEDESPIDO().equals("1")?true:false);
			if(datos.getCODIGOCOLECTIVODESPIDO()!=null){
				params.setCodigoColectivoDespido(TEOCOLDE.getEnumByValue(datos.getCODIGOCOLECTIVODESPIDO()));
			}
		}
	}
	private void completeDatosAnexoContratoRelevo(DATOSANEXOCONTRATORELEVOTYPE datos) {
		if(datos != null){
			params.setReliefData(true);
			if(datos.getTIPOTRABAJADOR()!=null){
				params.setTipoTrabajadorRelevo(TEYTRELE.getEnumByValue(datos.getTIPOTRABAJADOR()));
			}
			Person person = new Person();
			person.setName(datos.getNOMBREAPELLIDOS().getNOMBRE());
			person.setFirstSurname(datos.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
			person.setSecondSurname(datos.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
			params.setReliefPerson(person);
		}
	}
	private void completeDatosEtCote(DATOSETCOTYPE datos) {
		if(datos != null){
			params.setSchoolWorkshopData(true);
			params.setCodigoEtCoTe(TESCETCO.getEnumByValue(datos.getCODIGOETCOTE()));
		}
	}
	private void completeDatosEtt(DATOSETTTYPE datos) {
		if(datos != null){
			params.setEttData(true);
			params.setEttCif(datos.getCIFNIFEMPRESAUSUARIA().getCIFNIF());
			params.setEttName(datos.getRAZONSOCIALEMPRESAUSUARIA());
			params.setEttContractTemplate(!StringUtils.isBlank(datos.getINDCTOPLANTILLA()) && datos.getINDCTOPLANTILLA().equals("S"));
			params.setEttForeignEnterprise(!StringUtils.isBlank(datos.getINDEMPRESAEXTRANJERA()) && datos.getINDEMPRESAEXTRANJERA().equals("S"));
		}
	}
	private void completeDatosContratoExtranjero(DATOSCONTRATOEXTRANJEROTYPE datos) {
		if(datos != null){
			params.setAnnexData(true);
			params.setAnexEmploymentYear(datos.getAÑOCONTINGENTE());
			params.setEmploymentCharacter(datos.getINDCARACTEROFERTA());
		}
	}
	private void completeDatosComunicacionCopiaBasica(DATOSCOMUNICACOPIABASICATYPE datos) {
		if(datos != null){
			params.setTextoCopiaBasica(datos.getTEXTOCOPIABASICA());
			params.setTipoFirmaCopiaBasica(TERFIRCB.getEnumByValue(datos.getTIPOFIRMA()));
		}
	}

	private void completeDatosUsoLibreEmpresa(DATOSUSOLIBREEMPRESATYPE datos) {
		if(datos != null){
			params.setUsoLibreEmpresa(datos.getUSOLIBREEMPRESA());
		}
	}
	
	private void completeDatosContratoTiempoParcial(DATOSCONTRATOTIEMPOPARCIALTYPE datos) {
		if(datos != null){
			params.setActividadSinFechaCierta(datos.getACTIVIDADSINFECHACIERTA());
			params.setColectivoEdad(THPCOLFO.getEnumByValue(datos.getCOLECTIVOEDAD()));
			params.setFijoDiscontinuoPeriodico(datos.getFIJODISCONTINUOPERIODICO().equals("S"));
			params.setHorasAnualesTiempoCompleto(datos.getHORASANUALESTIEMPOCOMPLETO());
			params.setHorasConvenio(getHoras(datos.getHORASCONVENIO()));
			params.setMinutosConvenio(getMinutos(datos.getHORASCONVENIO()));
			params.setHorasFormacion(getHoras(datos.getHORASFORMACION()));
			params.setMinutosFormacion(getMinutos(datos.getHORASFORMACION()));
			params.setHorasJornada(getHoras(datos.getHORASJORNADA()));
			params.setMinutosJornada(getMinutos(datos.getHORASJORNADA()));
			params.setIndicFormacionTeorica(datos.getINDICFORMACIONTEORICA());
			params.setPorcentajeJubilacionParcial(datos.getPORCENTAJEJUBILACIONPARCIAL());
			params.setPorcJornadaPactada(datos.getPORCJORNADAPACTADA());
			params.setTipoJornada(TEQPTIEM.getEnumByValue(datos.getTIPOJORNADA()));
		}
	}
	private String getHoras(String duracion){
		return StringUtils.isBlank(duracion)?null:duracion.substring(0,4);
	}
	private String getMinutos(String duracion){
		return StringUtils.isBlank(duracion)?null:duracion.substring(4,6);
	}
	private void completeDatosReduccionRdl2011(DATOSREDUCCIONRDL12011TYPE datos) {
		// TODO
	}
	private void completeDatosBonificacion(DATOSBONIFICACIONTYPE datos) {
		// TODO
	}
	private void completeDatosEmpresaInsercion(DATOSEMPRESAINSERCIONTYPE dato) {
		// TODO
	}
	private void completeDatosCopiaBasica(DATOSCOPIABASICATYPE datos) {
		// TODO
	}
	private void completeDatosProgramaEmpleoPublico(DATOSPROGEMPLEOPUBLICOTYPE datos) {
		// TODO
		if(datos != null){
			params.setEmploymentProgramData(true);
//			datos.setACTUACION("");
//			datos.setCORPORACIONLOCAL("");
//			datos.setEJERCICIOPRESUPUESTARIO("");
//			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
		}
	}
	private void completeDatosContratoInvestigacion(DATOSCONTRATOINVESTIGACIONTYPE datos) {
		if(datos != null){
			params.setIndEmpleador(TEWEINVE.getEnumByValue(datos.getINDEMPLEADOR()));
			params.setIndTrabajador(TEXTINVE.getEnumByValue(datos.getINDTRABAJADOR()));
			params.setIndRd632006(datos.getINDRD632006().equals("S"));
		}
	}
	private void completeDatosContratoInsercion(DATOSCONTRATOINSERCIONTYPE datos) {
		// TODO
	}
	private void completeDatosContratoInterinidad(DATOSCONTRATOINTERINIDADTYPE datos) {
		if(datos != null){
			params.setInterimData(true);
			params.setCausaInterinidad(TEIINTER.getEnumByValue(datos.getCAUSAINTERINIDAD()));
		}
	}
	private void completeDatosContratoPracticas(DATOSCONTRATOPRACTICASTYPE datos) {
		if(datos != null){
			params.setTitulacionAcademica(datos.getTITULACIONACADEMICA());
			if(!StringUtils.isEmpty(datos.getINDCERTIFPROFESIONALIDAD())){
				params.setIndCertifProfesionalidad(datos.getINDCERTIFPROFESIONALIDAD().equals("S"));
			}
		}
	}
	private void completeDatosExclusionSocial(DATOSEXCLUSIONSOCIALTYPE datos) {
		// TODO
	}
	
	
	/*
	 * 
	 * TRANSFORMACIONES
	 * 
	 */
//	private void readTransformacion109(TRANSFORMACION109TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion139(TRANSFORMACION139TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion189(TRANSFORMACION189TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//    	completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion209(TRANSFORMACION209TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion239(TRANSFORMACION239TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion289(TRANSFORMACION289TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion309(TRANSFORMACION309TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	private void readTransformacion389(TRANSFORMACION389TYPE transformacionType){
//		completeDatosEmpresa(transformacionType.getDATOSEMPRESA());
//		completeDatosContrato(transformacionType.getDATOSCONTRATO());
//		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION());
//		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL());
//		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO());
//		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION());
//		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO());
//		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA());
//		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA());
//	}
//	
//	
//	private void completeDatosUsoLibreEmpresa(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSUSOLIBREEMPRESATYPE datosusolibreempresa) {
//		if(datosusolibreempresa != null){
//			params.setUsoLibreEmpresa(datosusolibreempresa.getUSOLIBREEMPRESA());
//		}
//	}
//
//	private void completeDatosComunicaCopiaBasica(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSCOMUNICACOPIABASICATYPE datoscomunicacopiabasica) {
//		if(datoscomunicacopiabasica != null){
//			// TODO
//			datoscomunicacopiabasica.getDOMICCENTROTRABAJO();
//			params.setTextoCopiaBasica(datoscomunicacopiabasica.getTEXTOCOPIABASICA());
//			params.setTipoFirmaCopiaBasica(TERFIRCB.getEnumByValue(datoscomunicacopiabasica.getTIPOFIRMA()));
//		}
//	}
//
//	private void completeDatosAnexoContratoRelevo(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSANEXOCONTRATORELEVOTYPE datosanexocontratorelevo) {
//		if(datosanexocontratorelevo != null){
//			params.setReliefData(true);
//			Person person = new Person();
//			person.setName(datosanexocontratorelevo.getNOMBREAPELLIDOS().getNOMBRE());
//			person.setFirstSurname(datosanexocontratorelevo.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
//			person.setSecondSurname(datosanexocontratorelevo.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
//			params.setReliefPerson(person);
//		}
//	}
//
//	private void completeDatosAdicionalesTransformacion(
//			DATOSADICIONALESTRANSFORMACIONTYPE datosadicionalestransformacion) {
//		// TODO 
//		datosadicionalestransformacion.getCODIGOCOLECTIVOREDUCCION();
//		if(datosadicionalestransformacion.getINDDISCAPACIDAD()!=null){
//			params.setIndDiscapacidad(TEJINDIS.getEnumByValue(datosadicionalestransformacion.getINDDISCAPACIDAD()));
//			params.setDisabilityData(true);
//		}
//	}
//
//	private void completeDatosMedidasFomento(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSMEDIDASFOMENTOTYPE datosmedidasfomento) {
//		if(datosmedidasfomento != null){
//			params.setMedidasFomentoData(true);
//			params.setIndCosteDespido(datosmedidasfomento.getINDCOSTEDESPIDO().equals("1")?true:false);
//			if(datosmedidasfomento.getCODIGOCOLECTIVODESPIDO()!=null){
//				params.setCodigoColectivoDespido(TEOCOLDE.getEnumByValue(datosmedidasfomento.getCODIGOCOLECTIVODESPIDO()));
//			}
//		}
//	}
//
//	private void completeDatosGeneralesTransformacion(
//			DATOSGENERALESTRANSFORMACIONTYPE datosgeneralestransformacion) {
//		// TODO 
//		
//		if(datosgeneralestransformacion != null){
//			
////			datosgeneralestransformacion.getFECHAINICIO()
////			datosgeneralestransformacion.getFECHATERMINOREAL()
////			datosgeneralestransformacion.getINDICADORDISCONTINUIDAD()
////			datosgeneralestransformacion.getMUNICIPIOCT()
////			datosgeneralestransformacion.getNACIONALIDADCT()
//			
//			if(datosgeneralestransformacion.getCODIGOOCUPACION()!=null){
//				params.setCno(getCno(datosgeneralestransformacion.getCODIGOOCUPACION()));
//			}
//		}
//	}
//
//	private void completeDatosContrato(DATOSCONTRATOTYPE datoscontrato) {
//		// TODO 
//		
//		if(datoscontrato != null){
//			
////			datoscontrato.getCLAVECONTRATO()
////			datoscontrato.getFECHAINICIOCTO()
////			datoscontrato.getIDENTIFICADORPFISICA()
//			
//		}
//	}
//
//	private void completeDatosEmpresa(DATOSEMPRESATYPE datosempresa) {
//		// TODO 
//		
//		if(datosempresa != null){
////			datosempresa.getCIFNIFEMPRESA()
////			datosempresa.getCODIGOCUENTACOTIZACION()
//		}
//	}
//	
//	private void completeDatosBonificacion(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSBONIFICACIONTYPE datosbonificacion) {
//		// TODO 
//		
//		if(datosbonificacion != null){
////			datosbonificacion.getACOGIDOMATERNIDADEXCEDENCIA()
////			datosbonificacion.getCODIGOCOLECTIVOBONIF()
////			datosbonificacion.getCOLECTIVODISCAPACITADOS()
//		}
//	}
//
//	private void completeDatosContratoTiempoParcial(
//			com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSCONTRATOTIEMPOPARCIALTYPE datoscontratotiempoparcial) {
//		if(datoscontratotiempoparcial != null){
//			params.setActividadSinFechaCierta(datoscontratotiempoparcial.getACTIVIDADSINFECHACIERTA());
//			params.setFijoDiscontinuoPeriodico(datoscontratotiempoparcial.getFIJODISCONTINUOPERIODICO().equals("S"));
//			params.setHorasConvenio(getHoras(datoscontratotiempoparcial.getHORASCONVENIO()));
//			params.setMinutosConvenio(getMinutos(datoscontratotiempoparcial.getHORASCONVENIO()));
//			params.setHorasJornada(getHoras(datoscontratotiempoparcial.getHORASJORNADA()));
//			params.setMinutosJornada(getMinutos(datoscontratotiempoparcial.getHORASJORNADA()));
//			params.setTipoJornada(TEQPTIEM.getEnumByValue(datoscontratotiempoparcial.getTIPOJORNADA()));
//		}
//	}
	

	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap(Contract contract) {
		if(contractDataMap==null){
			PayrollUtils utils = new PayrollUtils();
			contractDataMap = utils.getContractDataMap(contract);
		}
		return contractDataMap;
	}
	protected Map<String, String> getContractDataMap() {
		return contractDataMap;
	}
	

	
	public class ContractValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	
}

