package com.esferalia.aon.ui.payroll.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
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
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.file.ContrataReader;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;

public class ContractContrataLoader implements IContractLoader, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public String getFileTypeDescription(){
		return "Fichero Contrat@";
	}
	
	public boolean isValidFile(BufferedReader reader, InputStream input) throws AonException{
		try {
			if (reader.ready()) {
				String line = reader.readLine();
				String xmlDeclarationOpenTag = "<?xml";
				String xmlDeclarationEncoding = "encoding=\"ISO-8859-1\"";
				String xmlDeclarationEndTag = "?>";
				if(!line.startsWith(xmlDeclarationOpenTag) 
						|| !line.endsWith(xmlDeclarationEndTag)
						|| !line.contains(xmlDeclarationEncoding)){
					return false;
				}
				
			}
		} catch (UnsupportedEncodingException e) {
			raiseException(0, "La codificación no es válida");
		} catch (IOException e) {
			logError(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
		return true;
	}
	
	public void checkMetadata(InputStream input) throws AonException {
		logInfo("Comienza el checkeo de la meta información.");
		if (input == null) {
			raiseException(0, "La entrada está vacia!");
		}
		try {
			InputStreamReader inputReader = new InputStreamReader(input, SEPEFileUtils.XML_FILE_ENCODING);
			LineNumberReader reader = new LineNumberReader(inputReader);
			int i = 0;
			if (reader.ready()) {
				++i;
				if(!isValidFile(reader, input)){
					raiseException(i, "La declaración del fichero xml es incorrecta o no se corresponde con una declaracion válida");
				}
				++i;
				String line = reader.readLine();
				String contratoFile = "<CONTRATOS>";
				String transformacionFile = "<TRANSFORMACION>";
				String prorrogaFile = "<PRORROGA>";
				if(line.equals(contratoFile)){
					logInfo("Fichero de Contrat@ detectado (Contratos)");
				} else if(line.equals(transformacionFile)){
					logInfo("Fichero de Contrat@ detectado (Transformaciones)");
				} else if(line.equals(prorrogaFile)){
					logInfo("Fichero de Contrat@ detectado (Prorrogas)");
				} else {
					raiseException(i, "El fichero xml no se corresponde con un fichero de Contrat@ válido");
				}
			}
			if (i == 0) {
				raiseException(0, "No existen datos en el canal de entrada");
			}
			logInfo("La meta información es correcta.");
		} catch (UnsupportedEncodingException e) {
			raiseException(0, "La codificación no es válida");
		} catch (IOException e) {
			logError(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	public void logInfo( String msg  ) {
		LogPanelController logger = LogPanelController.getInstance();
		logger.info(msg);
	}
	
	public void logWarn( String msg  ) {
		LogPanelController logger = LogPanelController.getInstance();
		logger.warn(msg);
	}
	
	public void logError( String msg  ) {
		LogPanelController logger = LogPanelController.getInstance();
		logger.error(msg);
	}
	
	private void raiseException(int i, String message) throws AonException {
		String msg = "Línea " + i +": " + message;
		logError("ERROR: " + msg);
		throw new AonException(msg);
	}
	
	public void validate(InputStream input) throws AonException {
		int errors = 0;
		int warnings = 0;
		logInfo("Comienza la validación de formato!");
		
		try {
			input.reset();
			SEPEFileUtils.validateContrataXmlPattern(input, SEPEFileUtils.CONTRATOS_SCHEMA_FILE_NAME, "");
		} catch (Exception e) {
			raiseException(0, e.getMessage());
		}
			
		logInfo(" Finalizada la validación de formato.");
		logInfo(" " + errors + " errores, " + warnings + " avisos");
	}
	
	public void load(InputStream input,Session session) throws AonException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		
		int i = 0;
		try {
			input.reset();
			int errors = 0;
			int warnings = 0;
			logInfo(" Comienza la carga de datos!");
			
			ContrataReader contrataReader = new ContrataReader();
			contrataReader.readFile( input );
			CONTRATOS contratos = contrataReader.getContratos();
			
			if(contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().isEmpty()){
				String msg = "El fichero no contiene datos de ningun contrato.";
				logWarn(msg);
				warnings++;
			}
			
			for(Object o: contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150()){
				
				IContratoType contrato = (IContratoType) o;
				ContractCode code = obtainContractCode(contrato);
				
				Enterprise enterprise = obtainEnterprise(contrato.getDATOSEMPRESA().getCIFNIFEMPRESA().getCIFNIF());
				if(enterprise==null){
					String msg = "No se ha dado de alta la empresa del trabajador " +" " 
					+ contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE() +" "
					+ contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO() +" "
					+ contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO();
					logError(msg);
					raiseException(i, msg);
				}
				
				Person person = obtainPerson(contrato.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(1));
				if(person!=null){
					logWarn("Datos personales existentes para " + person.getFullName()+" ("+person.getRegistry().getDocument()+")");
					warnings++;
					logInfo("Se excluye la carga de los datos personales para " + person.getFullName()+" ("+person.getRegistry().getDocument()+")");
				} else {
					/*
					 * REGISTRY
					 */
					IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
					Registry registry = new Registry();
					registry.setName(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE());
					registry.setAlias(null);
					registry.setType(RegistryType.NATURAL);
					registry.setConfidential(false);
					registry.setSecurityLevel(SecurityLevel.OFFICIAL);
					for(Country country: Country.values()){
						if(String.valueOf(country.getIsoNum()).equals(contrato.getDATOSTRABAJADOR().getNACIONALIDAD())){
							registry.setNationality(country);
							registry.setDocumentCountry(country);
						}
					}
					registry.setDocument(contrato.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(1));
					/*
					"D";"D.N.I"
					"E";"NUMERO IDENTIFICATIVO EXTRANJERO"
					"U";"CIUDADANOS DE LA UE/EEE SIN NIE"
					"W";"CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE"
					 */
					String type = contrato.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(0, 1);
					if(type.equals("D")){
						registry.setDocumentType(DocumentType.NIF);
					} else if(type.equals("E")){
						registry.setDocumentType(DocumentType.NIE);
					}
					registry.setDomain(enterprise.getDomain());
					registry = (Registry) registryBean.insert(registry);

					/*
					 * PERSON
					 */
					IManagerBean personBean = BeanManager.getManagerBean(Person.class);
					person = new Person();
					person.setRegistry(registry);
					person.setName(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE());
					person.setFirstSurname(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
					person.setSecondSurname(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
					try {
						person.setBirthDate(dateFormatter.parse(contrato.getDATOSTRABAJADOR().getFECHANACIMIENTO()));
					} catch (ParseException e1) {
						logError("Error de formato al obtener la fecha de nacimiento del trabajador " + person.getFullName());
					}
					if(StringUtils.isBlank(contrato.getDATOSTRABAJADOR().getSEXO())){
						person.setGender(Gender.UNKNOWN);
					} else if(contrato.getDATOSTRABAJADOR().getSEXO().equals("1")){
						person.setGender(Gender.MALE);
					} else if(contrato.getDATOSTRABAJADOR().getSEXO().equals("2")){
						person.setGender(Gender.FEMALE);
					}
					person.setMaritalStatus(MaritalStatus.UNKNOWN);
					if(!StringUtils.isBlank(contrato.getDATOSTRABAJADOR().getNUMEROSEGURIDADSOCIAL())){
						person.setSocialSecurityNumber(contrato.getDATOSTRABAJADOR().getNUMEROSEGURIDADSOCIAL());
					} else {
						logError("No existe número de la S.S. para el trabajador " + person.getFullName());
					}
					person.setDomain(enterprise.getDomain());
					person = (Person) personBean.insert(person);
				}
				
				
				Date startDate = null;
				try {
					startDate = dateFormatter.parse(contrato.getDATOSGENERALESCONTRATO().getFECHAINICIO());
				} catch (ParseException e1) {
					logError("Error de formato al obtener la fecha de inicio y la fecha fin del contrato del trabajador " + person.getFullName());
				}
				GeoZone wpGeoZone = obtainWorkPlaceGeoZone(contrato.getDATOSGENERALESCONTRATO().getMUNICIPIOCT());
				EnterpriseCCC ccc = obtainCcc(enterprise, wpGeoZone, contrato.getDATOSEMPRESA().getCODIGOCUENTACOTIZACION().substring(4));
				PayrollWorkPlace pwp = null;
				if(ccc!=null){
					pwp = obtainWorkPlace(ccc, contrato.getDATOSEMPRESA().getCODIGOCUENTACOTIZACION());
				} else {
					logError("No se ha dado de alta en la empresa el CCC del trabajador " + person.getFullName());
				}
				if(pwp==null){
					String msg = "No se ha dado de alta en la empresa el centro de trabajo del trabajador " + person.getFullName();
					logError(msg);
					raiseException(i, msg);
				}
				
				Contract contract = obtainContract(person, startDate, ccc, pwp.getWorkPlace());
				if(contract!=null){
					logWarn("Contrato existente para " + person.getFullName()+" ("+person.getRegistry().getDocument()+")");
					warnings++;
					logInfo("Se excluye la carga del contrato para " + person.getFullName()+" ("+person.getRegistry().getDocument()+")");
				} else {
					
					/*
					 * CONTRACT
					 */
					IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
					contract = new Contract();
					contract.setPerson(person);
					try {
						if(!StringUtils.isBlank(contrato.getDATOSGENERALESCONTRATO().getFECHAINICIO())){
							contract.setStartDate(dateFormatter.parse(contrato.getDATOSGENERALESCONTRATO().getFECHAINICIO()));
							contract.setSeniorityDate(dateFormatter.parse(contrato.getDATOSGENERALESCONTRATO().getFECHAINICIO()));
						}
						if(!StringUtils.isBlank(contrato.getDATOSGENERALESCONTRATO().getFECHATERMINO())){
							contract.setEndDate(dateFormatter.parse(contrato.getDATOSGENERALESCONTRATO().getFECHATERMINO()));
						}
					} catch (ParseException e) {
						logError("Error de formato al obtener la fecha de inicio y la fecha fin del contrato del trabajador " + person.getFullName());
					}
					
					contract.setEnterpriseCCC(ccc);
					contract.setWorkPlace(pwp.getWorkPlace());
					contract.setActivity(pwp.getEnterpriseActivity());
					contract.setAgreementLevel(null);
					contract.setCategoryDescription(null);
					contract.setCalendar(null);
					contract.setDescription(null);
					contract.setRegistration(null);
					contract.setRegimeType(ccc.getActivity().getType());
					if(obtainContractModel(code)!=null){
						contract.setModel(obtainContractModel(code));
					} else {
						logError("No se ha podido obtener en modelo de contrato del trabajador " + person.getFullName());
					}
					contract.setSepeStatus(ContractStatus.PENDING);
					contract.setSsStatus(ContractStatus.PENDING);
					contract.setDomain(enterprise.getDomain());
					contract = (Contract) contractBean.insert(contract);
					++i;
					
					/*
					 * CONTRACT DATA
					 */
					IManagerBean contractDataBean = BeanManager.getManagerBean(ContractData.class);
					ContractData data = new ContractData();
					data.setContract(contract);
					data.setStartDate(contract.getStartDate());
					data.setEndDate(contract.getEndDate());
					data.setName(ContextVariable.TC2.getName());
					data.setExpression("\""+code.getValue()+"\"");
					data.setDomain(enterprise.getDomain());
					contractDataBean.insert(data);
					
					CNO cno = obtainCno(contrato.getDATOSGENERALESCONTRATO().getCODIGOOCUPACION());
					if(cno!=null){
						data = new ContractData();
						data.setContract(contract);
						data.setStartDate(contract.getStartDate());
						data.setEndDate(contract.getEndDate());
						data.setName(ContextVariable.CNO.getName());
						data.setExpression("\""+cno.getCode()+"\"");
						data.setDomain(enterprise.getDomain());
						contractDataBean.insert(data);
					}
					
					/*
					 * CONTRACT ATTACH
					 */
					IManagerBean attachBean = BeanManager.getManagerBean(ContractAttachment.class);
					ContractAttachment attach = new ContractAttachment();
					attach.setContract(contract);
					attach.setData(IOUtils.toByteArray(input));
					attach.setAttachmentType(ContractAttachmentType.SEPE_CONTRACT_FILE);
					attach.setMimeType(MimeType.MIME_XML);
					attach.setDescription("Fichero contrat@");
					attach.setDomain(enterprise.getDomain());
					attachBean.insert(attach);
				}
				
				
			}
			
			logInfo("" + i + " contratos insertados");
			logInfo("Carga de datos finalizada!");
			logInfo(" " + errors + " errores, " + warnings + " avisos");
		} catch (IOException e) {
			logInfo(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	private Contract obtainContract(Person person, Date startDate, EnterpriseCCC ccc, WorkPlace workPlace) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_ID), person.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), startDate);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ENTERPRISE_CCC_ID), ccc.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID), workPlace.getId());
		completeDomainCriteria("Contract", criteria);
		Iterator<ITransferObject> it = bean.getList(criteria).iterator();
		while(it.hasNext()){
			return (Contract) it.next();
		}
		return null;
	}

	private Person obtainPerson(String document) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Person.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT), document);
		completeDomainCriteria("Person", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (Person) list.get(0);
		}
		return null;
	}
	
	private GeoZone obtainWorkPlaceGeoZone(String municipioct) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_CODE), municipioct.substring(0, 2));
		completeDomainCriteria("GeoZone", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (GeoZone) list.get(0);
		}
		return null;
	}
	private ContractModel obtainContractModel(ContractCode code) {
		for(ContractModelCode model: ContractModelCode.values()){
			if(model.getCode()==code){
				return model.getModel();
			}
		}
		return null;
	}
	private Enterprise obtainEnterprise(String cifnif) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
		Criteria criteria = new Criteria(); 
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_DOCUMENT), cifnif);
		completeDomainCriteria("Enterprise", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(list.size()==0){
			String msg = "No existe ninguna empresa con NIF " + cifnif;
			logError(msg);
			return null;
		} else if(list.size()>1){
			String msg = "Existen varias empresa con el NIF " + cifnif;
			logError(msg);
			return null;
		}
		return (Enterprise) list.get(0);
	}
	private PayrollWorkPlace obtainWorkPlace(EnterpriseCCC ccc, String codigocuentacotizacion) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_ENTERPRISE_ACTIVITY_ID), ccc.getActivity().getId());
		completeDomainCriteria("PayrollWorkPlace", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (PayrollWorkPlace) list.get(0);
		}
		return null;
	}
	private EnterpriseCCC obtainCcc(Enterprise enterprise, GeoZone geozone, String codigocuentacotizacion) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), geozone.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_CCC), codigocuentacotizacion);
		completeDomainCriteria("EnterpriseCCC", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (EnterpriseCCC) list.get(0);
		}
		return null;
	}
	private CNO obtainCno(String codigoocupacion) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CNO.class);
		Criteria criteria = new Criteria(); 
		ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CNO_CODE), codigoocupacion.substring(0, 4)+"%");
		criteria.addExpression(ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CNO_CODE), codigoocupacion.substring(0, 4)+"%"));
		List<ITransferObject> list = bean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (CNO) list.get(0);
	}
	
	private ContractCode obtainContractCode(IContratoType o) {
		if (o instanceof CONTRATO100TYPE) {
			return ContractCode.C100;
		} else if (o instanceof CONTRATO130TYPE) {
			return ContractCode.C130;
		} else if (o instanceof CONTRATO150TYPE) {
			return ContractCode.C150;
		} else if (o instanceof CONTRATO200TYPE) {
			return ContractCode.C200;
		} else if (o instanceof CONTRATO230TYPE) {
			return ContractCode.C230;
		} else if (o instanceof CONTRATO250TYPE) {
			return ContractCode.C250;
		} else if (o instanceof CONTRATO300TYPE) {
			return ContractCode.C300;
		} else if (o instanceof CONTRATO330TYPE) {
			return ContractCode.C330;
		} else if (o instanceof CONTRATO350TYPE) {
			return ContractCode.C350;
		} else if (o instanceof CONTRATO401TYPE) {
			return ContractCode.C401;
		} else if (o instanceof CONTRATO402TYPE) {
			return ContractCode.C402;
		} else if (o instanceof CONTRATO403TYPE) {
			return ContractCode.C403;
		} else if (o instanceof CONTRATO410TYPE) {
			return ContractCode.C410;
		} else if (o instanceof CONTRATO420TYPE) {
			return ContractCode.C420;
		} else if (o instanceof CONTRATO421TYPE) {
			return ContractCode.C421;
		} else if (o instanceof CONTRATO430TYPE) {
			return ContractCode.C430;
		} else if (o instanceof CONTRATO441TYPE) {
			return ContractCode.C441;
		} else if (o instanceof CONTRATO450TYPE) {
			return ContractCode.C450;
		} else if (o instanceof CONTRATO452TYPE) {
			return ContractCode.C452;
		} else if (o instanceof CONTRATO501TYPE) {
			return ContractCode.C501;
		} else if (o instanceof CONTRATO502TYPE) {
			return ContractCode.C502;
		} else if (o instanceof CONTRATO503TYPE) {
			return ContractCode.C503;
		} else if (o instanceof CONTRATO510TYPE) {
			return ContractCode.C510;
		} else if (o instanceof CONTRATO520TYPE) {
			return ContractCode.C520;
		} else if (o instanceof CONTRATO530TYPE) {
			return ContractCode.C530;
		} else if (o instanceof CONTRATO540TYPE) {
			return ContractCode.C540;
		} else if (o instanceof CONTRATO541TYPE) {
			return ContractCode.C541;
		} else if (o instanceof CONTRATO550TYPE) {
			return ContractCode.C550;
		} else if (o instanceof CONTRATO552TYPE) {
			return ContractCode.C552;
		} else if (o instanceof CONTRATO970TYPE) {
			return ContractCode.C970;
		} else if (o instanceof CONTRATO980TYPE) {
			return ContractCode.C980;
		} else if (o instanceof CONTRATO990TYPE) {
			return ContractCode.C990;
		}
		return null;
	}
	
	private void completeDomainCriteria(String beanName, Criteria criteria) {
		if(DomainManager.isDomainManagementAvailable()){
			PayrollUtils utils = PayrollUtils.getInstance();
			utils.getCurrentChildDomainIds();
			List<Integer> idList = utils.getCurrentChildDomainIds();
			idList.add(DomainManager.getCurrentDomain());
			criteria.setSkipDomainFilter(true);
			criteria.addInExpression(beanName+".domain", idList);
		}
	}

}
