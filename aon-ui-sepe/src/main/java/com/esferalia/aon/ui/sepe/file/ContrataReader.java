package com.esferalia.aon.ui.sepe.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.contrata.TBONVFOR;
import com.esferalia.aon.payroll.enumeration.contrata.TEIINTER;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.payroll.enumeration.contrata.TELCOLBO;
import com.esferalia.aon.payroll.enumeration.contrata.TEOCOLDE;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.contrata.TERFIRCB;
import com.esferalia.aon.payroll.enumeration.contrata.TESCETCO;
import com.esferalia.aon.payroll.enumeration.contrata.TETPGMEM;
import com.esferalia.aon.payroll.enumeration.contrata.TEWEINVE;
import com.esferalia.aon.payroll.enumeration.contrata.TEXTINVE;
import com.esferalia.aon.payroll.enumeration.contrata.TEYTRELE;
import com.esferalia.aon.payroll.enumeration.contrata.THPCOLFO;
import com.esferalia.aon.payroll.enumeration.contrata.THYDISLE;
import com.esferalia.aon.payroll.enumeration.contrata.TQOCOLRE;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO100TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO130TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO150TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO200TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO230TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO250TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO300TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO330TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO350TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO401TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO402TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO403TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO410TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO420TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO421TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO430TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO441TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO450TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO452TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO501TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO502TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO503TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO510TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO520TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO530TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO540TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO541TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO550TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO552TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO970TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO980TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO990TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSBONIFICACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOEXTRANJEROTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINSERCIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINTERINIDADTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINVESTIGACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOPRACTICASTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCOPIABASICATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSEMPRESAINSERCIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSETCOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSETTTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSEXCLUSIONSOCIALTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSPROGEMPLEOPUBLICOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSREDUCCIONFORMACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSREDUCCIONRDL12011TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSADICIONALESPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSGENERALESPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGAS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGATIPOTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION109TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION139TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION189TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION209TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION239TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION289TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION309TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION389TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;


public class ContrataReader {
	
	private final static String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	private final static String CONTRATA_TRANSFORMACIONES_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.transformaciones";
	private final static String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.prorrogas";
	
	private IContrataParams params;
	
	private boolean isContratoFile = false;
	private boolean isTransformacionFile = false;
	private boolean isProrrogaFile = false;

	private CONTRATOS contratos;
	private TRANSFORMACIONES transformaciones;
	private PRORROGAS prorrogas;
	
	public CONTRATOS getContratos() {
		return contratos;
	}

	public void setContratos(CONTRATOS contratos) {
		this.contratos = contratos;
	}

	public TRANSFORMACIONES getTransformaciones() {
		return transformaciones;
	}

	public void setTransformaciones(TRANSFORMACIONES transformaciones) {
		this.transformaciones = transformaciones;
	}

	public PRORROGAS getProrrogas() {
		return prorrogas;
	}

	public void setProrrogas(PRORROGAS prorrogas) {
		this.prorrogas = prorrogas;
	}

	public ContrataContratoParams readFile(ContractAttachment attach) throws ManagerBeanException, IOException{
		return null;
	}
	

	static final JAXBContext contratoContext = initContratoContext();
	static final JAXBContext transformacionContext = initTransformacionContext();
	static final JAXBContext prorrogaContext = initProrrogaContext();

    private static JAXBContext initContratoContext() {
        try {
			return JAXBContext.newInstance(CONTRATA_CONTRATOS_MODEL_PATH);
		} catch (JAXBException e) {
			String msg = "Error al obtener el contexto de Contrat@ para contratos";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		}
		return null;
    }
    private static JAXBContext initTransformacionContext() {
    	try {
			return JAXBContext.newInstance(CONTRATA_TRANSFORMACIONES_MODEL_PATH);
		} catch (JAXBException e) {
			String msg = "Error al obtener el contexto de Contrat@ para transformaciones";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		}
		return null;
    }
    private static JAXBContext initProrrogaContext() {
    	try {
			return JAXBContext.newInstance(CONTRATA_PRORROGAS_MODEL_PATH);
		} catch (JAXBException e) {
			String msg = "Error al obtener el contexto de Contrat@ para prorrogas";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		}
		return null;
    }
	
	public IContrataParams readFile(InputStream input) throws ManagerBeanException, IOException{
		
		processContractDocumentType(input);
		
		try {
			input.reset();
			setContratos(null);
			setTransformaciones(null);
			setProrrogas(null);
			if( isContratoFile ){
				Unmarshaller unmarshaller = contratoContext.createUnmarshaller();
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
				contratos = (CONTRATOS) unmarshaller.unmarshal(input);
				IContratoType contratoType = (IContratoType) contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
				this.params = new ContrataContratoParams();
				completeContratosParams(contratoType, (ContrataContratoParams) params);
			} else if( isTransformacionFile ) {
				Unmarshaller unmarshaller = transformacionContext.createUnmarshaller();
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
				transformaciones = (TRANSFORMACIONES) unmarshaller.unmarshal(input);
				ITransformacionType transformacionType = (ITransformacionType) transformaciones.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().get(0);
				this.params = new ContrataTransformacionesParams();
				completeTransformacionesParams(transformacionType, (ContrataTransformacionesParams) params);
			} else if( isProrrogaFile ) {
				Unmarshaller unmarshaller = prorrogaContext.createUnmarshaller();
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
				prorrogas = (PRORROGAS) unmarshaller.unmarshal(input);
				PRORROGATIPOTYPE prorrogaType = (PRORROGATIPOTYPE) prorrogas.getPRORROGATIPO().get(0);
				this.params = new ContrataProrrogaParams();
				completeProrrogasParams(prorrogaType, (ContrataProrrogaParams) params);
			} 
			
			return params;
		} catch (JAXBException e) {
			String msg = "Error al obtener los datos de Contrat@";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		} finally {
			input.close();
		}
		return null;
	}
	
	private void processContractDocumentType(InputStream input) {
		isContratoFile = false;
		isTransformacionFile = false;
		isProrrogaFile = false;
	
		try {
			InputStreamReader inputReader = new InputStreamReader(input,SEPEFileUtils.XML_FILE_ENCODING);
			LineNumberReader reader = new LineNumberReader(inputReader);
			if (reader.ready()) {
				String line = reader.readLine();
				line = reader.readLine();
				String contratoFile = "<CONTRATOS>";
				String transformacionFile = "<TRANSFORMACIONES>";
				String prorrogaFile = "<PRORROGAS>";
				if(line.equals(contratoFile)){
					isContratoFile = true;
				} else if(line.equals(transformacionFile)){
					isTransformacionFile = true;
				} else if(line.equals(prorrogaFile)){
					isProrrogaFile = true;
				} else {
					String msg = "No se reconoce la estructura de datos de Contrat@.";
					AonUtil.addErrorMessage(msg);
//					throw new AbortProcessingException(msg);
				}
			}
			reader.close();
			inputReader.close();
		} catch (UnsupportedEncodingException e) {
			String msg = "Error de codificacion";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
			throw new AbortProcessingException(msg, e);
		} catch (IOException e) {
			String msg = "Error de lectura del archivo.";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
			throw new AbortProcessingException(msg, e);
		}
	}

	public void completeContratosParams(IContratoType o, ContrataContratoParams params) throws JAXBException, IOException {
		
		if (o instanceof CONTRATO100TYPE) {
			readContract100((CONTRATO100TYPE)o, params);
		} else if (o instanceof CONTRATO130TYPE) {
			readContract130((CONTRATO130TYPE)o, params);
		} else if (o instanceof CONTRATO150TYPE) {
			readContract150((CONTRATO150TYPE)o, params);
		} else if (o instanceof CONTRATO200TYPE) {
			readContract200((CONTRATO200TYPE)o, params);
		} else if (o instanceof CONTRATO230TYPE) {
			readContract230((CONTRATO230TYPE)o, params);
		} else if (o instanceof CONTRATO250TYPE) {
			readContract250((CONTRATO250TYPE)o, params);
		} else if (o instanceof CONTRATO300TYPE) {
			readContract300((CONTRATO300TYPE)o, params);
		} else if (o instanceof CONTRATO330TYPE) {
			readContract330((CONTRATO330TYPE)o, params);
		} else if (o instanceof CONTRATO350TYPE) {
			readContract350((CONTRATO350TYPE)o, params);
		} else if (o instanceof CONTRATO401TYPE) {
			readContract401((CONTRATO401TYPE)o, params);
		} else if (o instanceof CONTRATO402TYPE) {
			readContract402((CONTRATO402TYPE)o, params);
		} else if (o instanceof CONTRATO403TYPE) {
			readContract403((CONTRATO403TYPE)o, params);
		} else if (o instanceof CONTRATO410TYPE) {
			readContract410((CONTRATO410TYPE)o, params);
		} else if (o instanceof CONTRATO420TYPE) {
			readContract420((CONTRATO420TYPE)o, params);
		} else if (o instanceof CONTRATO421TYPE) {
			readContract421((CONTRATO421TYPE)o, params);
		} else if (o instanceof CONTRATO430TYPE) {
			readContract430((CONTRATO430TYPE)o, params);
		} else if (o instanceof CONTRATO441TYPE) {
			readContract441((CONTRATO441TYPE)o, params);
		} else if (o instanceof CONTRATO450TYPE) {
			readContract450((CONTRATO450TYPE)o, params);
		} else if (o instanceof CONTRATO452TYPE) {
			readContract452((CONTRATO452TYPE)o, params);
		} else if (o instanceof CONTRATO501TYPE) {
			readContract501((CONTRATO501TYPE)o, params);
		} else if (o instanceof CONTRATO502TYPE) {
			readContract502((CONTRATO502TYPE)o, params);
		} else if (o instanceof CONTRATO503TYPE) {
			readContract503((CONTRATO503TYPE)o, params);
		} else if (o instanceof CONTRATO510TYPE) {
			readContract510((CONTRATO510TYPE)o, params);
		} else if (o instanceof CONTRATO520TYPE) {
			readContract520((CONTRATO520TYPE)o, params);
		} else if (o instanceof CONTRATO530TYPE) {
			readContract530((CONTRATO530TYPE)o, params);
		} else if (o instanceof CONTRATO540TYPE) {
			readContract540((CONTRATO540TYPE)o, params);
		} else if (o instanceof CONTRATO541TYPE) {
			readContract541((CONTRATO541TYPE)o, params);
		} else if (o instanceof CONTRATO550TYPE) {
			readContract550((CONTRATO550TYPE)o, params);
		} else if (o instanceof CONTRATO552TYPE) {
			readContract552((CONTRATO552TYPE)o, params);
		} else if (o instanceof CONTRATO970TYPE) {
			readContract970((CONTRATO970TYPE)o, params);
		} else if (o instanceof CONTRATO980TYPE) {
			readContract980((CONTRATO980TYPE)o, params);
		} else if (o instanceof CONTRATO990TYPE) {
			readContract990((CONTRATO990TYPE)o, params);
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
	
	private void readContract100(CONTRATO100TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract130(CONTRATO130TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
	    completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
	    completeDatosEtCote(o.getDATOSETCOTE(), params);
	    completeDatosEtt(o.getDATOSETT(), params); 
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
	    completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract150(CONTRATO150TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract200(CONTRATO200TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
	}
	private void readContract230(CONTRATO230TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract250(CONTRATO250TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract300(CONTRATO300TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
	    completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
	    completeDatosEtCote(o.getDATOSETCOTE(), params);
	    completeDatosEtt(o.getDATOSETT(), params); 
	    completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
    	completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract330(CONTRATO330TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract350(CONTRATO350TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract401(CONTRATO401TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract402(CONTRATO402TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract403(CONTRATO403TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract410(CONTRATO410TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract420(CONTRATO420TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract421(CONTRATO421TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosReduccionFormacion(o.getDATOSREDUCCIONFORMACION(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract430(CONTRATO430TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract441(CONTRATO441TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract450(CONTRATO450TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract452(CONTRATO452TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract501(CONTRATO501TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract502(CONTRATO502TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract503(CONTRATO503TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract510(CONTRATO510TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract520(CONTRATO520TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract530(CONTRATO530TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract540(CONTRATO540TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract541(CONTRATO541TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract550(CONTRATO550TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract552(CONTRATO552TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), params);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract970(CONTRATO970TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract980(CONTRATO980TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readContract990(CONTRATO990TYPE o, ContrataContratoParams params) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), params);
		completeDatosEtCote(o.getDATOSETCOTE(), params);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), params);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), params);
		completeDatosEtt(o.getDATOSETT(), params);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), params);
	}
	
	private void completeDatosGeneralesContrato(DATOSGENERALESCONTRATOTYPE datos, ContrataContratoParams params) {
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
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			try {
				if(datos.getFECHAINICIO()!=null){
					params.setStartDate(formatter.parse(datos.getFECHAINICIO()));
				}
			} catch (ParseException e) {
				// nada
			}
			try {
				if(datos.getFECHATERMINO()!=null){
					params.setEndDate(formatter.parse(datos.getFECHATERMINO()));
				}
			} catch (ParseException e) {
				// nada
			}
		}
	}
	private void completeDatosMedidasFomento(DATOSMEDIDASFOMENTOTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setMedidasFomentoData(true);
			params.setIndCosteDespido(datos.getINDCOSTEDESPIDO().equals("1")?true:false);
			if(datos.getCODIGOCOLECTIVODESPIDO()!=null){
				params.setCodigoColectivoDespido(TEOCOLDE.getEnumByValue(datos.getCODIGOCOLECTIVODESPIDO()));
			}
		}
	}
	private void completeDatosAnexoContratoRelevo(DATOSANEXOCONTRATORELEVOTYPE datos, ContrataContratoParams params) {
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
	private void completeDatosEtCote(DATOSETCOTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setSchoolWorkshopData(true);
			params.setCodigoEtCoTe(TESCETCO.getEnumByValue(datos.getCODIGOETCOTE()));
		}
	}
	private void completeDatosEtt(DATOSETTTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setEttData(true);
			params.setEttCif(datos.getCIFNIFEMPRESAUSUARIA().getCIFNIF());
			params.setEttName(datos.getRAZONSOCIALEMPRESAUSUARIA());
			params.setEttContractTemplate(!StringUtils.isBlank(datos.getINDCTOPLANTILLA()) && datos.getINDCTOPLANTILLA().equals("S"));
			params.setEttForeignEnterprise(!StringUtils.isBlank(datos.getINDEMPRESAEXTRANJERA()) && datos.getINDEMPRESAEXTRANJERA().equals("S"));
		}
	}
	private void completeDatosReduccionFormacion(DATOSREDUCCIONFORMACIONTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setReductionData(true);
			params.setCodigoColectivoReduccion(TQOCOLRE.getEnumByValue(datos.getCODIGOCOLECTIVOREDUCCIONFORMACION()));
			params.setPorcentajeReduccion(datos.getPORCENTAJEREDUCCIONFORMACION());
		}
	}
	private void completeDatosContratoExtranjero(DATOSCONTRATOEXTRANJEROTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setAnnexData(true);
			params.setAnexEmploymentYear(datos.getAÑOCONTINGENTE());
			params.setEmploymentCharacter(datos.getINDCARACTEROFERTA());
		}
	}
	private void completeDatosComunicacionCopiaBasica(DATOSCOMUNICACOPIABASICATYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setTextoCopiaBasica(datos.getTEXTOCOPIABASICA());
			params.setTipoFirmaCopiaBasica(TERFIRCB.getEnumByValue(datos.getTIPOFIRMA()));
		}
	}

	private void completeDatosUsoLibreEmpresa(DATOSUSOLIBREEMPRESATYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setUsoLibreEmpresa(datos.getUSOLIBREEMPRESA());
		}
	}
	
	private void completeDatosContratoTiempoParcial(DATOSCONTRATOTIEMPOPARCIALTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setActividadSinFechaCierta(datos.getACTIVIDADSINFECHACIERTA());
			params.setColectivoEdad(THPCOLFO.getEnumByValue(datos.getCOLECTIVOEDAD()));
			if(datos.getFIJODISCONTINUOPERIODICO()!=null){
				params.setFijoDiscontinuoPeriodico(datos.getFIJODISCONTINUOPERIODICO().equals("S"));
			}
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
	private void completeDatosReduccionRdl2011(DATOSREDUCCIONRDL12011TYPE datos, ContrataContratoParams params) {
		// TODO
	}
	private void completeDatosBonificacion(DATOSBONIFICACIONTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			IContratoType c = (IContratoType) contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
			params.setApoyoEmprendedoresData(true);
			if( datos.getCODIGOCOLECTIVOBONIF()!=null ){
				params.setColectivoBonificacion(TELCOLBO.getEnumByValue(datos.getCODIGOCOLECTIVOBONIF()));	
			}
			if(datos.getINDICEMPLEADAUTONOMO()!=null){
				if(datos.getINDICEMPLEADAUTONOMO().equals("1")){
					params.setIndEmpleadAutonomo(true);
				} else if(datos.getINDICEMPLEADAUTONOMO().equals("2")){
					params.setIndEmpleadAutonomo(false);
				}
			}
		}
	}
	private void completeDatosEmpresaInsercion(DATOSEMPRESAINSERCIONTYPE dato, ContrataContratoParams params) {
		// TODO
	}
	private void completeDatosCopiaBasica(DATOSCOPIABASICATYPE datos, ContrataContratoParams params) {
		// TODO
	}
	private void completeDatosProgramaEmpleoPublico(DATOSPROGEMPLEOPUBLICOTYPE datos, ContrataContratoParams params) {
		// TODO
		if(datos != null){
			params.setEmploymentProgramData(true);
//			datos.setACTUACION("");
//			datos.setCORPORACIONLOCAL("");
//			datos.setEJERCICIOPRESUPUESTARIO("");
//			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
		}
	}
	private void completeDatosContratoInvestigacion(DATOSCONTRATOINVESTIGACIONTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setResearchData(true);
			params.setIndEmpleador(TEWEINVE.getEnumByValue(datos.getINDEMPLEADOR()));
			params.setIndTrabajador(TEXTINVE.getEnumByValue(datos.getINDTRABAJADOR()));
			params.setIndRd632006(datos.getINDRD632006()!=null && datos.getINDRD632006().equals("S"));
		}
	}
	private void completeDatosContratoInsercion(DATOSCONTRATOINSERCIONTYPE datos, ContrataContratoParams params) {
		// TODO
	}
	private void completeDatosContratoInterinidad(DATOSCONTRATOINTERINIDADTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setInterimData(true);
			params.setCausaInterinidad(TEIINTER.getEnumByValue(datos.getCAUSAINTERINIDAD()));
		}
	}
	private void completeDatosContratoPracticas(DATOSCONTRATOPRACTICASTYPE datos, ContrataContratoParams params) {
		if(datos != null){
			params.setTitulacionAcademica(datos.getTITULACIONACADEMICA());
			if(!StringUtils.isEmpty(datos.getINDCERTIFPROFESIONALIDAD())){
				params.setIndCertifProfesionalidad(datos.getINDCERTIFPROFESIONALIDAD().equals("S"));
			}
		}
	}
	private void completeDatosExclusionSocial(DATOSEXCLUSIONSOCIALTYPE datos, ContrataContratoParams params) {
		// TODO
	}
	
	/*
	 * 
	 * PRORROGAS
	 * 
	 */
	public void completeProrrogasParams(PRORROGATIPOTYPE prorrogaType, ContrataProrrogaParams params) throws JAXBException, IOException {
		PRORROGATIPOTYPE prorroga = (PRORROGATIPOTYPE) prorrogaType;
		completeDATOSADICIONALESPRORROGA(prorroga.getDATOSADICIONALESPRORROGA(), params);
		completeDATOSCONTRATO(prorroga.getDATOSCONTRATO(), params);
		completeDATOSEMPRESA(prorroga.getDATOSEMPRESA(), params);
		completeDATOSGENERALESPRORROGA(prorroga.getDATOSGENERALESPRORROGA(), params);
		completeDATOSUSOLIBREEMPRESA(prorroga.getDATOSUSOLIBREEMPRESA(), params);
	}
	
	private void completeDATOSCONTRATO(DATOSCONTRATOTYPE datos, ContrataProrrogaParams params){
		datos.getCLAVECONTRATO();
		datos.getFECHAINICIOCTO();
		datos.getIDENTIFICADORPFISICA();
	}
	private void completeDATOSEMPRESA(DATOSEMPRESATYPE datos, ContrataProrrogaParams params){
		datos.getCIFNIFEMPRESA();
		datos.getCCC();
	}
	private void completeDATOSGENERALESPRORROGA(DATOSGENERALESPRORROGATYPE datos, ContrataProrrogaParams params){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
			params.setFechaInicio(formatter.parse(datos.getFECHAINICIO()));
			params.setFechaFin(formatter.parse(datos.getFECHAFIN()));
		} catch (Exception e) {
			// nada
		}
		if(StringUtils.isNotBlank(datos.getINDICADORCONVCOL())){
			params.setIndicadorConvCol(datos.getINDICADORCONVCOL().equals("S"));
		}
		if(StringUtils.isNotBlank(datos.getINDICADORDISCONTINUIDAD())){
			params.setIndicadorDiscontinuidad(datos.getINDICADORDISCONTINUIDAD().equals("I"));
		}
		if(StringUtils.isNotBlank(datos.getINDEMPRESAAAPPUNIVERSIDAD())){
			params.setIndEmpresaAappUniversidad(datos.getINDEMPRESAAAPPUNIVERSIDAD().equals("S"));
		}
		if(StringUtils.isNotBlank(datos.getINDPERIODOAUTORIZADURACION())){
			params.setIndPeriodoAutorizaDuracion(datos.getINDPERIODOAUTORIZADURACION().equals("S"));
		}
	}
	private void completeDATOSADICIONALESPRORROGA(DATOSADICIONALESPRORROGATYPE datos, ContrataProrrogaParams params){
		if(datos!=null && StringUtils.isNotBlank(datos.getHORASFORMACION()) && datos.getHORASFORMACION().length()==6){
			params.setHorasFormacion(datos.getHORASFORMACION().substring(0, 4));
			params.setMinutosFormacion(datos.getHORASFORMACION().substring(3, 5));
		}
		if(datos!=null && StringUtils.isNotBlank(datos.getINDDURACINFERIOR())){
			params.setIndDuracInferior(datos.getINDDURACINFERIOR().equals("S"));
		}
	}
	private void completeDATOSUSOLIBREEMPRESA(com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSUSOLIBREEMPRESATYPE datos, ContrataProrrogaParams params){
		if(datos!=null){
			params.setUsoLibreEmpresa(datos.getUSOLIBREEMPRESA());
		}
	}
	
	
	/*
	 * 
	 * TRANSFORMACIONES
	 * 
	 */
	public void completeTransformacionesParams(ITransformacionType transformacionType, ContrataTransformacionesParams params) throws JAXBException, IOException {
		
		if (transformacionType instanceof TRANSFORMACION109TYPE) {
			readTransformacion109((TRANSFORMACION109TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION139TYPE) {
			readTransformacion139((TRANSFORMACION139TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION189TYPE) {
			readTransformacion189((TRANSFORMACION189TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION209TYPE) {
			readTransformacion209((TRANSFORMACION209TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION239TYPE) {
			readTransformacion239((TRANSFORMACION239TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION289TYPE) {
			readTransformacion289((TRANSFORMACION289TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION309TYPE) {
			readTransformacion309((TRANSFORMACION309TYPE)transformacionType, params);
//	TODO: nueva clave de contrato - Boletin Noticias RED 2012/05
//		} else if (transformacionType instanceof TRANSFORMACION339TYPE) {
//			readTransformacion339((TRANSFORMACION339TYPE)transformacionType, params);
		} else if (transformacionType instanceof TRANSFORMACION389TYPE) {
			readTransformacion389((TRANSFORMACION389TYPE)transformacionType, params);
		}
		
		
	}
	
	private void readTransformacion109(TRANSFORMACION109TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion139(TRANSFORMACION139TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion189(TRANSFORMACION189TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
    	completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion209(TRANSFORMACION209TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion239(TRANSFORMACION239TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion289(TRANSFORMACION289TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion309(TRANSFORMACION309TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosBonificacion(transformacionType.getDATOSBONIFICACION(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	private void readTransformacion389(TRANSFORMACION389TYPE transformacionType, ContrataTransformacionesParams params){
		completeDatosEmpresa(transformacionType.getDATOSEMPRESA(), params);
		completeDatosContrato(transformacionType.getDATOSCONTRATO(), params);
		completeDatosGeneralesTransformacion(transformacionType.getDATOSGENERALESTRANSFORMACION(), params);
		completeDatosContratoTiempoParcial(transformacionType.getDATOSCONTRATOTIEMPOPARCIAL(), params);
		completeDatosMedidasFomento(transformacionType.getDATOSMEDIDASFOMENTO(), params);
		completeDatosAdicionalesTransformacion(transformacionType.getDATOSADICIONALESTRANSFORMACION(), params);
		completeDatosAnexoContratoRelevo(transformacionType.getDATOSANEXOCONTRATORELEVO(), params);
		completeDatosComunicaCopiaBasica(transformacionType.getDATOSCOMUNICACOPIABASICA(), params);
		completeDatosUsoLibreEmpresa(transformacionType.getDATOSUSOLIBREEMPRESA(), params);
	}
	
	
	private void completeDatosUsoLibreEmpresa(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSUSOLIBREEMPRESATYPE datosusolibreempresa, ContrataTransformacionesParams params) {
		if(datosusolibreempresa != null){
			params.setUsoLibreEmpresa(datosusolibreempresa.getUSOLIBREEMPRESA());
		}
	}

	private void completeDatosComunicaCopiaBasica(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCOMUNICACOPIABASICATYPE datoscomunicacopiabasica, ContrataTransformacionesParams params) {
		if(datoscomunicacopiabasica != null){
			// TODO
			datoscomunicacopiabasica.getDOMICCENTROTRABAJO();
			params.setTextoCopiaBasica(datoscomunicacopiabasica.getTEXTOCOPIABASICA());
			params.setTipoFirmaCopiaBasica(TERFIRCB.getEnumByValue(datoscomunicacopiabasica.getTIPOFIRMA()));
		}
	}

	private void completeDatosAnexoContratoRelevo(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSANEXOCONTRATORELEVOTYPE datosanexocontratorelevo, ContrataTransformacionesParams params) {
		if(datosanexocontratorelevo != null){
			params.setReliefData(true);
			Person person = new Person();
			person.setName(datosanexocontratorelevo.getNOMBREAPELLIDOS().getNOMBRE());
			person.setFirstSurname(datosanexocontratorelevo.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
			person.setSecondSurname(datosanexocontratorelevo.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
			params.setReliefPerson(person);
		}
	}

	private void completeDatosAdicionalesTransformacion(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSADICIONALESTRANSFORMACIONTYPE datosadicionalestransformacion, ContrataTransformacionesParams params) {
		// TODO 
		if(datosadicionalestransformacion!=null){
			datosadicionalestransformacion.getCODIGOCOLECTIVOREDUCCION();
			if(datosadicionalestransformacion.getINDDISCAPACIDAD()!=null){
				params.setIndDiscapacidad(TEJINDIS.getEnumByValue(datosadicionalestransformacion.getINDDISCAPACIDAD()));
//			params.setDisabilityData(true);
			}
		}
	}

	private void completeDatosMedidasFomento(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSMEDIDASFOMENTOTYPE datosmedidasfomento, ContrataTransformacionesParams params) {
		if(datosmedidasfomento != null){
//			params.setMedidasFomentoData(true);
			params.setIndCosteDespido(datosmedidasfomento.getINDCOSTEDESPIDO().equals("1")?true:false);
			if(datosmedidasfomento.getCODIGOCOLECTIVODESPIDO()!=null){
				params.setCodigoColectivoDespido(TEOCOLDE.getEnumByValue(datosmedidasfomento.getCODIGOCOLECTIVODESPIDO()));
			}
		}
	}

	private void completeDatosGeneralesTransformacion(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSGENERALESTRANSFORMACIONTYPE datosgeneralestransformacion, ContrataTransformacionesParams params) {
		// TODO 
		
		if(datosgeneralestransformacion != null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			
			try {
				if(datosgeneralestransformacion.getFECHAINICIO()!=null){
					params.setFechaInicio( formatter.parse(datosgeneralestransformacion.getFECHAINICIO()) );
				}
			} catch (ParseException e) {
				// nada
			}
			
//			datosgeneralestransformacion.getFECHATERMINOREAL()
//			datosgeneralestransformacion.getINDICADORDISCONTINUIDAD()
//			datosgeneralestransformacion.getMUNICIPIOCT()
//			datosgeneralestransformacion.getNACIONALIDADCT()
			
			if(datosgeneralestransformacion.getCODIGOOCUPACION()!=null){
				params.setCno(getCno(datosgeneralestransformacion.getCODIGOOCUPACION()));
			}
		}
	}

	private void completeDatosContrato(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCONTRATOTYPE datoscontrato, ContrataTransformacionesParams params) {
		// TODO 
		
		if(datoscontrato != null){
			params.setSourceContractSepeId(datoscontrato.getCLAVECONTRATO());
//			datoscontrato.getFECHAINICIOCTO()
//			datoscontrato.getIDENTIFICADORPFISICA()
		}
	}

	private void completeDatosEmpresa(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSEMPRESATYPE datosempresa, ContrataTransformacionesParams params) {
		// TODO 
		if(datosempresa != null){
//			datosempresa.getCIFNIFEMPRESA()
//			datosempresa.getCODIGOCUENTACOTIZACION()
		}
	}
	
	private void completeDatosBonificacion(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSBONIFICACIONTYPE datosbonificacion, ContrataTransformacionesParams params) {
		params.setDatosBonificacionData(false);
		if(datosbonificacion != null
			&& (datosbonificacion.getACOGIDOMATERNIDADEXCEDENCIA() != null
			|| datosbonificacion.getCOLECTIVODISCAPACITADOS() != null
			|| datosbonificacion.getCODIGOCOLECTIVOBONIF() != null)){
			params.setDatosBonificacionData(true);
			// TODO 
//			datosbonificacion.getACOGIDOMATERNIDADEXCEDENCIA()
			// TODO 
//			datosbonificacion.getCOLECTIVODISCAPACITADOS()
			params.setColectivoBonificacion(TELCOLBO.getEnumByValue(datosbonificacion.getCODIGOCOLECTIVOBONIF()));
		}
	}

	private void completeDatosContratoTiempoParcial(
			com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCONTRATOTIEMPOPARCIALTYPE datoscontratotiempoparcial, ContrataTransformacionesParams params) {
		if(datoscontratotiempoparcial != null){
			params.setActividadSinFechaCierta(datoscontratotiempoparcial.getACTIVIDADSINFECHACIERTA());
			params.setFijoDiscontinuoPeriodico(datoscontratotiempoparcial.getFIJODISCONTINUOPERIODICO().equals("S"));
			params.setHorasConvenio(getHoras(datoscontratotiempoparcial.getHORASCONVENIO()));
			params.setMinutosConvenio(getMinutos(datoscontratotiempoparcial.getHORASCONVENIO()));
			params.setHorasJornada(getHoras(datoscontratotiempoparcial.getHORASJORNADA()));
			params.setMinutosJornada(getMinutos(datoscontratotiempoparcial.getHORASJORNADA()));
			params.setTipoJornada(TEQPTIEM.getEnumByValue(datoscontratotiempoparcial.getTIPOJORNADA()));
		}
	}
	

	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */
//	private Map<String, String> contractDataMap;
//	
//	protected Map<String, String> getContractDataMap(Contract contract) {
//		if(contractDataMap==null){
//			SEPEUtils utils = new SEPEUtils();
//			contractDataMap = utils.getContractDataMap(contract);
//		}
//		return contractDataMap;
//	}
//	protected Map<String, String> getContractDataMap() {
//		return contractDataMap;
//	}
	

	public class XMLNamespaceFilter extends XMLFilterImpl {
	    public XMLNamespaceFilter(XMLReader arg0) {
	       super(arg0);
	    }
	    @Override
	    public void startElement(String uri, String localName,
	                             String qName, Attributes attributes)
	                             throws SAXException {
//	       super.startElement(<required namespace>, localName, qName, attributes);
	       super.startElement("", localName, qName, attributes);
	    }
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

