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

import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;

public class ContractAfiLoader implements IContractLoader, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public String getFileTypeDescription(){
		return "Fichero AFI";
	}
	
	public boolean isValidFile(BufferedReader reader, InputStream input) throws AonException{
		try {
			if (reader.ready()) {
				String line = reader.readLine();
				String afiOpenTag = "ETI";
				String afiFileTag = "AFI";
				if(!line.startsWith(afiOpenTag) 
						|| !line.replaceFirst(afiOpenTag, "").startsWith(afiFileTag)){
					return false;
				}
			}
			reader.close();
			input.close();
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
				String line = reader.readLine();
				String afiOpenTag = "ETI";
				String afiFileTag = "AFI";
				if(!line.startsWith(afiOpenTag) 
						|| !line.replaceFirst(afiOpenTag, "").startsWith(afiFileTag)){
					raiseException(i, "La declaración del fichero AFI es incorrecta o no se corresponde con una declaracion válida");
				}
				logInfo("Fichero AFI detectado");
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
		logInfo("Para validar el fichero se debe utilizar el programa WinSuite que proporciona la Seguridad Social.");
	}
	
	public void load(InputStream input,Session session) throws AonException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		
		int i = 0;
		try {
			input.reset();
			int errors = 0;
			int warnings = 0;
			logInfo(" Comienza la carga de datos!");
			
			AFIReader afiReader = new AFIReader();
			ETI eti = afiReader.readFile( input, SEPEFileUtils.XML_FILE_ENCODING );
			
			if(eti.getEmpresas().isEmpty()){
				String msg = "El fichero no contiene datos de ninguna empresa.";
				logWarn(msg);
				warnings++;
			}
			
			for(EMP emp: eti.getEmpresas()){
				
				Enterprise enterprise = obtainEnterprise(emp.getNumeroIdentificacion());
				if(enterprise==null){
					logError("La empresa " + emp.getRzs().getRazonSocial() + " no esta dada de alta. Se omiten los contratos incluidos en esta empresa.");
					errors++;
					logInfo("Se omiten los contratos de la empresa " + emp.getRzs().getRazonSocial());
				} else if(emp.getTrabajadores().size()==0){
					logWarn("La empresa " + emp.getRzs().getRazonSocial() + " no contiene datos de contratos.");
					warnings++;
				} else {
					String quoteRegimeValue = emp.getCodigoCuentaCotizacionSeguridadSocial().substring(0, 4);
					String cccValue = emp.getCodigoCuentaCotizacionSeguridadSocial().substring(4, emp.getCodigoCuentaCotizacionSeguridadSocial().length());
					EnterpriseCCC ccc = obtainCcc(enterprise, cccValue);
					PayrollWorkPlace pwp = null;
					if(ccc==null){
						logError("No se ha dado de alta en la empresa la cuenta de cotizacion " + cccValue);
						errors++;
						logInfo("Se omiten los contratos incluidos de la cuenta de cotizacion " + cccValue);
					} else {
						if( !PayrollUtils.getInstance().getRegimeCode(ccc).equals(quoteRegimeValue) ){
							logError("Regimen incorrecto para la actividad" + ccc.getActivity().getDescription()+" ["+enterprise.getRegistry().getFullName()+"]");
							errors++;
						}
						pwp = obtainWorkPlace(ccc);
						if(pwp==null){
							logWarn("No se ha dado de alta en la empresa el centro de trabajo para la actividad " + ccc.getActivity().getDescription());
							warnings++;
							logInfo("Se crearán los datos necesarios del dentro de trabajo, reviselos para actualizarlos.");
							
							IManagerBean raddressBean = BeanManager.getManagerBean(RegistryAddress.class);
							RegistryAddress address = new RegistryAddress();
							address.setRegistry(enterprise.getRegistry());
							address.setAddressType(AddressType.DELEGATION);
							address.setStreetType(StreetType.CL);
							address.setAddress("");
							address.setNumber("");
							address.setGeozone(obtainGeoZone(cccValue.substring(0, 2)));
							address.setDomain(enterprise.getDomain());
							raddressBean.insert(address);
							
							IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
							WorkPlace wp = new WorkPlace();
							wp = new WorkPlace();
							wp.setActive(true);
							wp.setAddress(address);
							wp.setDescription(address.getAddress()+" "+address.getNumber());
							if(wp.getAddress().getGeozone().getCode().equals("01")){
								wp.setEconomicAgreement(Administration.ALAVA);
							} else if(wp.getAddress().getGeozone().getCode().equals("48")){
								wp.setEconomicAgreement(Administration.BIZKAIA);
							} else if(wp.getAddress().getGeozone().getCode().equals("20")){
								wp.setEconomicAgreement(Administration.GIPUZKOA);
							} else if(wp.getAddress().getGeozone().getCode().equals("31")){
								wp.setEconomicAgreement(Administration.NAVARRA);
							} else {
								wp.setEconomicAgreement(Administration.COMMON_TERRITORY);
							}
							wp.setEnterprise(enterprise);
							wp.setScope(enterprise.getScope());
							wp.setDomain(enterprise.getDomain());
							wpBean.insert(wp);
							
							IManagerBean pwpBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
							pwp = new PayrollWorkPlace();
							pwp.setEnterpriseActivity(ccc.getActivity());
							pwp.setWorkPlace(wp);
							pwp.setDomain(enterprise.getDomain());
							pwpBean.insert(pwp);
						}
						
						for(TRA tra: emp.getTrabajadores()){
							String document = tra.getIpf().substring(4,tra.getIpf().length());
							while(document.startsWith("0")){
								document = document.replaceFirst("0", "");
							}
							Person person = obtainPerson(document);
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
								registry.setName(tra.getAyn().getNombre());
								registry.setAlias(null);
								registry.setType(RegistryType.NATURAL);
								registry.setConfidential(false);
								registry.setSecurityLevel(SecurityLevel.OFFICIAL);
								for(Country country: Country.values()){
									if(String.valueOf(country.getIsoNum()).equals(tra.getIpf().substring(1, 4))){
										registry.setDocumentCountry(country);
									}
									if(String.valueOf(country.getIsoNum()).equals(tra.getNacionalidad())){
										registry.setNationality(country);
									}
								}
								registry.setDocument(document);
								/*
								1 - D.N.I. (N.I.F.)
								2 - Pasaporte
								6 - Número de Identificación de Extranjero
								L - Españoles sin DNI no residentes en España
								M - Extranjeros sin NIE en España
								 */
								String type = tra.getIpf().substring(0, 1);
								if(type.equals("1")){
									registry.setDocumentType(DocumentType.NIF);
								} else if(type.equals("2")){
									registry.setDocumentType(DocumentType.PASSPORT);
								} else if(type.equals("6")){
									registry.setDocumentType(DocumentType.NIE);
								} else if(type.equals("L")){
									registry.setDocumentType(DocumentType.OTHER);
								} else if(type.equals("M")){
									registry.setDocumentType(DocumentType.OTHER);
								} 
								registry.setDomain(enterprise.getDomain());
								registry = (Registry) registryBean.insert(registry);
								
								/*
								 * REGISTRY ADDRESS
								 */
								IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
								RegistryAddress address = new RegistryAddress();
								address.setRegistry(registry);
								address.setGeozone(obtainGeoZone(tra.getNumeroAfiliacion().substring(0, 2)));
								address.setStreetType(StreetType.CL);
								address.setDomain(enterprise.getDomain());
								rAddressBean.insert(address);
								
								/*
								 * PERSON
								 */
								IManagerBean personBean = BeanManager.getManagerBean(Person.class);
								person = new Person();
								person.setRegistry(registry);
								person.setName(tra.getAyn().getNombre());
								person.setFirstSurname(tra.getAyn().getApellido1());
								person.setSecondSurname(tra.getAyn().getApellido2());
								try {
									person.setBirthDate(dateFormatter.parse(tra.getFab().getFechaNacimiento()));
								} catch (ParseException e1) {
									logError("Error de formato al obtener la fecha de nacimiento del trabajador " + person.getFullName());
									errors++;
								}
								if(tra.getFab().getSexo()==null){
									person.setGender(Gender.UNKNOWN);
								} else if(tra.getFab().getSexo().equals(1)){
									person.setGender(Gender.MALE);
								} else if(tra.getFab().getSexo().equals(2)){
									person.setGender(Gender.FEMALE);
								} else {
									person.setGender(Gender.UNKNOWN);
								}
								person.setMaritalStatus(MaritalStatus.UNKNOWN);
								person.setSocialSecurityNumber(tra.getNumeroAfiliacion());
								person.setDomain(enterprise.getDomain());
								person = (Person) personBean.insert(person);
							}
							
							Date startDate = null;
							try {
								startDate = dateFormatter.parse(tra.getFab().getFechaReal().toString());
							} catch (ParseException e) {
								logError("Error de formato al obtener la fecha de inicio del contrato del trabajador " + person.getFullName());
								errors++;
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
								contract.setStartDate(startDate);
								contract.setSeniorityDate(startDate);
								contract.setEnterpriseCCC(ccc);
								contract.setWorkPlace(pwp.getWorkPlace());
								contract.setActivity(pwp.getEnterpriseActivity());
								contract.setAgreementLevel(null);
//								contract.setCategoryDescription(tra.getFab().getCategoriaProfesional());
								contract.setCalendar(null);
								contract.setDescription(null);
								contract.setRegistration(null);
								contract.setRegimeType(ccc.getActivity().getType());
								
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
								
								ContractCode code = ContractCode.getContractCodeByValue(tra.getFab().getClaveContrato().toString());
								if(code!=null){
									data.setContract(contract);
									data.setStartDate(contract.getStartDate());
									data.setEndDate(contract.getEndDate());
									data.setName(ContextVariable.TC2.getName());
									data.setExpression("\""+code.getValue()+"\"");
									data.setDomain(enterprise.getDomain());
									contractDataBean.insert(data);
								} else{
									logError("El codigo de contrato no es correcto para el trabajador " + person.getFullName() + " ("+person.getRegistry().getDocument()+")");
									errors++;
								}
								
								QuoteGroup quoteGroup = QuoteGroup.getQuoteGroupByValue(tra.getFab().getGrupoCotizacion().toString());
								if(quoteGroup!=null){
									data = new ContractData();
									data.setContract(contract);
									data.setStartDate(contract.getStartDate());
									data.setEndDate(contract.getEndDate());
									data.setName( ContextVariable.QUOTE_GROUP.getName() );
									data.setExpression("\"" + quoteGroup.getValue() + "\"");
									data.setDomain(enterprise.getDomain());
									contractDataBean.insert(data);
								} else{
									logError("El grupo de cotizacion no es correcto para el trabajador " + person.getFullName() + " ("+person.getRegistry().getDocument()+")");
									errors++;
								}
							} 
						}
					}
				}

			}
			
			logInfo("" + i + " contratos creados");
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
	
	private GeoZone obtainGeoZone(String geozoneCode) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_CODE), geozoneCode);
		completeDomainCriteria("GeoZone", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (GeoZone) list.get(0);
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
	
	private PayrollWorkPlace obtainWorkPlace(EnterpriseCCC ccc) throws ManagerBeanException {
		if(ccc!=null && ccc.getActivity()!=null){
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_ENTERPRISE_ACTIVITY_ID), ccc.getActivity().getId());
			completeDomainCriteria("PayrollWorkPlace", criteria);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (PayrollWorkPlace) list.get(0);
			}
		}
		return null;
	}
	private EnterpriseCCC obtainCcc(Enterprise enterprise, String codigocuentacotizacion) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), obtainGeoZone(codigocuentacotizacion.substring(0, 2)).getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_CCC), codigocuentacotizacion.substring(0, codigocuentacotizacion.length()));
		completeDomainCriteria("EnterpriseCCC", criteria);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (EnterpriseCCC) list.get(0);
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
