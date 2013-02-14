package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;

import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.file.ContrataParams;


public class ContractContrataHandler {
	
	private ContrataParams params;
	private ContractCode contractCode;
	
	public ContrataParams getParams() {
		return params;
	}

	public void setParams(ContrataParams params) {
		this.params = params;
	}

	public ContractCode getContractCode() {
		return contractCode;
	}

	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}

	////////////////////////////////////////
	// fields otros datos contrato
	////////////////////////////////////////
	/**
	 * <xsd:element name="TIPO_JORNADA">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Período de tiempo. Sus posibles valores se encuentran codificados 
			en la tabla TEQPTIEM.txt de la Ayuda XML - Ultima versión - Tablas de códigos. 
			En el caso de  contratos fijos discontinuos (300, 330 y 350) el tipo de jornada será siempre ANUAL (A). 
			</xsd:documentation>
		</xsd:annotation>
		<xsd:simpleType>
			<xsd:restriction base="xsd:string">
				<xsd:length value="1"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsd:element>
	 */
	public Boolean getShowWorkingDayType() {
		return getContractCode()!=ContractCode.C300 || getContractCode()!=ContractCode.C330 || getContractCode()!=ContractCode.C350;
	}
/**
 * <xsd:element name="HORAS_JORNADA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas por jornada. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Obligatorias para todos los contratos a tiempo parcial menos para los fijos discontinuos (300, 330 y 350) 
					que podran no llevarlas dependiendo del valor de ACTIVIDAD_SIN_FECHACIERTA.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
 * @return
 */
	public Boolean getShowWorkingDayDuration() {
		return getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350;
	}

	/**
	 * <xsd:element name="HORAS_CONVENIO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas por convenio. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Sólo vendrá cumplimentado, y de manera opcional, para los contratos de códigos 200, 250, 230, 300, 350, 330.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowAgreementDuration() {
		return getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230
				|| getContractCode()==ContractCode.C250 || getContractCode()==ContractCode.C300
				|| getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350;
	}

	/**
	 * <xsd:element name="HORAS_FORMACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas de formación. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Obligatorias para los contratos de código 421 (salvo que el elemento INDIC_FORMACION_TEORICA sea "S"). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowFormationDuration() {
		return getContractCode()==ContractCode.C421;
	}
	/**
	 * <xsd:element name="INDIC_FORMACION_TEORICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de formación teórica recibida. 
					Obligatorio para los contratos de código 421 si no vienen especificadas las horas de formación. 
					Los posibles valores que puede tomar son "S" (SI) ó "N" (NO) para indicar si ya había sido recibida 
					con anterioridad la formación o no. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowFormationReceived() {
		return getContractCode()==ContractCode.C421;
	}
	/**
	 * <xsd:element name="COLECTIVO_EDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Colectivo de edad en formación. Obligatorio para los contratos de código 421 
					cuando el trabajador tiene una edad mayor o igual a 21 años  ( para los contratos de código 421 iniciados 
					entre el 18/06/2010 y el 31/12/2011 esta edad pasa a ser de 24 años). Sus posibles valores se encuentran 
					codificados en la tabla THPCOLFO.txt de la Ayuda XML - Ultima versión - Tablas de códigos. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowAgeCollective() {
		return getContractCode()==ContractCode.C421;
	}
	/**
	 * <xsd:element name="PORCENTAJE_JUBILACION_PARCIAL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Porcentaje sobre la jornada en contratos de jubilación parcial. 
					Obligatorio para los contratos de código 540. Su valor debe estar comprendido entre el 25% y el 85%. 
					Su formato pasa a ser EEDD (03-2009), siendo las dos primeras posiciones la parte entera del porcentaje 
					y las dos últimas la parte decimal (ej. 25% será enviado como 2500). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowRetirementPercent() {
		return getContractCode()==ContractCode.C540;
	}
	/**
	 * <xsd:element name="FIJODISCONTINUO_PERIODICO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de trabajo fijo discontinuo o periódico. 
					Obligatorio para los contratos de código 200, 230 ó 250 iniciados a partir del 01/07/2006. 
					Los posibles valores que puede tomar son "S" (SI) ó "N" (NO) para indicar si el contrato indefinido 
					a tiempo parcial corresponde a la realización de trabajos fijos discontinuos o periódicos que se repiten 
					en fechas ciertas dentro del volumen normal de la actividad de la empresa. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
	 * @return
	 */
	public Boolean getShowPeriodicallyDiscontinuous() {
		Calendar cal = Calendar.getInstance();
		cal.set(2006, 6, 1);
		return getParams().getContract().getStartDate().after(cal.getTime()) 
				&& ( getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230
				|| getContractCode()==ContractCode.C250 );
	}
	/**
	<xsd:element name="TITULACION_ACADEMICA">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Código de titulación académica. 
			No es necesario especificar su valor en los contratos de prácticas, 
			cuando el Nivel Formativo del trabajador sea 60. 
			Sus posibles valores se encuentran codificados en la tabla THITIACA.txt 
			de la Ayuda XML - Ultima versión - Tablas de códigos.  
			La codificación tabulada de este elemento corresponde a códigos de 12 posiciones, 
			hasta ahora se debían enviar códigos de 4 posiciones con 8 ceros por la izquierda hasta completar las 12, 
			a partir de ahora los códigos ya son de 12 posiciones por lo que no habrá que rellenar con ceros.</xsd:documentation>
		</xsd:annotation>
		<xsd:simpleType>
			<xsd:restriction base="xsd:string">
				<xsd:pattern value="\d{12}"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsd:element>
	 * @return
	 */
	public Boolean getShowAcademicTitulation() {
		return true;
	}
	/**
	<xsd:element name="IND_CERTIF_PROFESIONALIDAD" minOccurs="0">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">
			Obligatorio (S/N) para los códigos 420, 520, 450 con modalidad 420 
			y 550 con modalidad 520 iniciados a partir del 18/06/2010 
			cuando el Nivel Formativo del trabajador no sea uno de los siguientes códigos: 33, 51, 54, 55, 59 o 60. 
			Indica si el trabajador tiene o no un certificado de profesionalidad. </xsd:documentation>
		</xsd:annotation>
		<xsd:simpleType>
			<xsd:restriction base="xsd:string">
				<xsd:pattern value="[SN\s]"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsd:element>
	 * @return
	 */
	public Boolean getShowProfessionalCertificate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 6, 18);
		return getParams().getContract().getStartDate().after(cal.getTime()) 
				&& ( getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C520
				|| getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C550 );
	}
	
	////////////////////////////////////////
	// checks datos especificos contrato
	////////////////////////////////////////
	public Boolean getShowEmploymentProgramData(){
		return true;
	}
	public Boolean getShowEttData(){
		return true;
	}
	public Boolean getShowReliefData(){
		return true;	
	}
	public Boolean getShowOfferData(){
		return true;
	}
	public Boolean getShowWorkshopData(){
		return true;
	}
	public Boolean getShowDisabilityData(){
		return true;
	}
	public Boolean getShowOlderThan52Data(){
		return true;
	}
	public Boolean getShowAnnexData(){
		return true;
	}
	public Boolean getShowCanpaignData(){
		return true;
	}
	public Boolean getShowInterimData(){
		return true;
	}
	public Boolean getShowResearchData(){
		return true;
	}
	public Boolean getShowReductionData(){
		return true;
	}
	

	
	
	public void buildDataStructure(){
		
	}	
	
	

//	private static final long serialVersionUID = 3733409240562499848L;
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(ContractContrataHandler.class.getName());
//	private static final String CONTRACT_XML_CONTEXT_PATH = "com.esferalia.aon.ui.payroll.utils.contractMojo";

//	private ContractaWriter xmlWriter;
	
//	private ContractContrataFactory factory;
	
//	private ContractController contractController;
	
//	private CONTRATOS contratos;
	
//	private ContractAttachment contrataAttach;
	
//	public ContractContrataHandler(ContractController controller) {
//		contractController = controller;
//		factory = new ContractContrataFactory();
//		factory.setParams(new ContrataParams());
////		factory.setContractCode(ContractCode.getContractCodeByValue(getContractDataMap().get(ContextVariable.TC2.getName())));
//		factory.setContract((Contract)contractController.getTo());
//	}
	
//	public void setContrataAttach(ContractAttachment contrataAttach) {
//		this.contrataAttach = contrataAttach;
//	}
//	public ContractAttachment getContrataAttach(){
//		return contrataAttach;
//	}
	
//	public CONTRATOS getContratos() {
//		return contratos;
//	}
//	public void setContratos(CONTRATOS contratos) {
//		this.contratos = contratos;
//	}
	
//	public ContractaWriter getXmlWriter() {
//		return xmlWriter;
//	}
//	public void setXmlWriter(ContractaWriter xmlWriter) {
//		this.xmlWriter = xmlWriter;
//	}
	
//	public ContractContrataFactory getFactory() {
//		return factory;
//	}
//	public void setFactory(ContractContrataFactory factory) {
//		this.factory = factory;
//	}

//	public ContrataParams getParams() {
//		return getFactory().getParams();
//	}

//	public void readXml() throws JAXBException, IOException {
//		searchContrataAttach();
//		if(getContrataAttach()!=null){
//			byte[] f = getContrataAttach().getData();
//			if(f!=null && f.length>0){
//				File file = File.createTempFile("aon-temp", ".XML");
//				FileOutputStream fos = new FileOutputStream(file);
//				fos.write(f);
//				fos.close();
//				JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
//				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//				setContratos((CONTRATOS) unmarshaller.unmarshal(file));
//				ContractaReader reader = new ContractaReader();
//				reader.completeContrataParams(getContratos(), getFactory().getParams());
//				unmarshaller.setEventHandler(new ContractValidationEventHandler());
//			}
//		}
//	}
//	
//	private void searchContrataAttach(){
//		setContrataAttach(null);
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), ((Contract)contractController.getTo()).getId());
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA);
//			List<ITransferObject> list = bean.getList(criteria);
//			if(!list.isEmpty()){
//				setContrataAttach((ContractAttachment) list.get(0));
//			}
//		} catch (ManagerBeanException e) {
//			// NADA
//		}
//	}
	
//	public void generateXml() throws JAXBException, ManagerBeanException, IOException{
//		JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
//		
//		ObjectFactory factory = new ObjectFactory();
//		setContratos(factory.createCONTRATOS());
//		setXmlWriter(new ContractaWriter());
//		getXmlWriter().setContract((Contract) contractController.getTo());
//		getXmlWriter().setParams(getFactory().getParams());
//		
//		getContratos().getCONTRATO100AndCONTRATO130AndCONTRATO150().add(getXmlWriter().execute());
//		
//		Marshaller marshaller = jaxbContext.createMarshaller();
//		
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		File file = File.createTempFile("aon-temp", ".XML"); 
//		marshaller.marshal( getContratos(), file );
//		
//		validateXmlPattern(file);
//		
//		FileInputStream fin = new FileInputStream(file);
//		byte fileContent[] = new byte[(int)file.length()];
//		fin.read(fileContent);
//		if(getContrataAttach()==null){
//			setContrataAttach(new ContractAttachment());
//		}
//		getContrataAttach().setContract((Contract) contractController.getTo());
//		getContrataAttach().setData(fileContent);
//		getContrataAttach().setAttachmentType(ContractAttachmentType.SPEE_CONTRATA);
//		getContrataAttach().setMimeType(MimeType.MIME_XML);
//		getContrataAttach().setDescription("fichero_contrata");
//		fin.close();
//	}
	
//	private void validateXmlPattern(File xml) {
//		final String SCHEMA = "EsquemaContratos50.xsd";
//		
//		try {
//			ClassLoader cl = Thread.currentThread().getContextClassLoader();
//			URL[] urls = Classpath.search(cl, "META-INF/", SCHEMA);
//			
//			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//			Schema schema = sf.newSchema(getSchemaFile(urls[0]));
//			Validator validator = schema.newValidator();
//			StreamSource source = new StreamSource(xml);
//			validator.validate(source);
//		} catch (Exception e) {
//			String msg = "Error de formato al generar el XML";
//			AonUtil.addErrorMessage(msg);
//			AonUtil.addErrorMessage(e.getMessage());
//			throw new AbortProcessingException(msg, e);
//		}
//	}
	
//	private File getSchemaFile(URL url) {
//		Map<String, Boolean> map = new HashMap<String, Boolean>();
//		map.put("CONTRATO_100", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C100.getValue()));
//		map.put("CONTRATO_130", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C130.getValue()));
//		map.put("CONTRATO_150", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C150.getValue()));
//		map.put("CONTRATO_200", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C200.getValue()));
//		map.put("CONTRATO_230", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C230.getValue()));
//		map.put("CONTRATO_250", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C250.getValue()));
//		map.put("CONTRATO_300", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C300.getValue()));
//		map.put("CONTRATO_330", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C330.getValue()));
//		map.put("CONTRATO_350", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C350.getValue()));
//		map.put("CONTRATO_401", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C401.getValue()));
//		map.put("CONTRATO_402", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C402.getValue()));
//		map.put("CONTRATO_403", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C403.getValue()));
//		map.put("CONTRATO_410", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C410.getValue()));
//		map.put("CONTRATO_420", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C420.getValue()));
//		map.put("CONTRATO_421", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C421.getValue()));
//		map.put("CONTRATO_430", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C430.getValue()));
//		map.put("CONTRATO_441", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C441.getValue()));
//		map.put("CONTRATO_450", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C450.getValue()));
//		map.put("CONTRATO_452", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C452.getValue()));
//		map.put("CONTRATO_501", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C501.getValue()));
//		map.put("CONTRATO_502", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C502.getValue()));
//		map.put("CONTRATO_503", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C503.getValue()));
//		map.put("CONTRATO_510", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C510.getValue()));
//		map.put("CONTRATO_520", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C520.getValue()));
//		map.put("CONTRATO_530", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C530.getValue()));
//		map.put("CONTRATO_540", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C540.getValue()));
//		map.put("CONTRATO_541", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C541.getValue()));
//		map.put("CONTRATO_550", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C550.getValue()));
//		map.put("CONTRATO_552", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C552.getValue()));
//		map.put("CONTRATO_970", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C970.getValue()));
//		map.put("CONTRATO_980", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C980.getValue()));
//		map.put("CONTRATO_990", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C990.getValue()));
//		try {
//			File tempFile = new File("tmpEsquemaContratos50.xsd");
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
//
//			String currentLine;
//
//			while((currentLine = reader.readLine()) != null) {
//				if( (StringUtils.contains(currentLine, "CONTRATO_100") && !map.get("CONTRATO_100")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_130") && !map.get("CONTRATO_130")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_150") && !map.get("CONTRATO_150")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_200") && !map.get("CONTRATO_200")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_230") && !map.get("CONTRATO_230")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_250") && !map.get("CONTRATO_250")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_300") && !map.get("CONTRATO_300")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_330") && !map.get("CONTRATO_330")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_350") && !map.get("CONTRATO_350")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_401") && !map.get("CONTRATO_401")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_402") && !map.get("CONTRATO_402")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_403") && !map.get("CONTRATO_403")) || 
//					(StringUtils.contains(currentLine, "CONTRATO_410") && !map.get("CONTRATO_410")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_420") && !map.get("CONTRATO_420")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_421") && !map.get("CONTRATO_421")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_430") && !map.get("CONTRATO_430")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_441") && !map.get("CONTRATO_441")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_450") && !map.get("CONTRATO_450")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_452") && !map.get("CONTRATO_452")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_501") && !map.get("CONTRATO_501")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_502") && !map.get("CONTRATO_502")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_503") && !map.get("CONTRATO_503")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_510") && !map.get("CONTRATO_510")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_520") && !map.get("CONTRATO_520")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_530") && !map.get("CONTRATO_530")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_540") && !map.get("CONTRATO_540")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_541") && !map.get("CONTRATO_541")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_550") && !map.get("CONTRATO_550")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_552") && !map.get("CONTRATO_552")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_970") && !map.get("CONTRATO_970")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_980") && !map.get("CONTRATO_980")) ||
//					(StringUtils.contains(currentLine, "CONTRATO_990") && !map.get("CONTRATO_990")) ){
//					continue;
//				}
//			    writer.write(currentLine);
//			}
//			writer.close();
//			return tempFile;
//		} catch (FileNotFoundException e) {
//			String msg = "Error al obtener el esquema de validacion";
//			LOGGER.error(msg);
//			AonUtil.addErrorMessage(msg);
//		} catch (IOException e) {
//			String msg = "Error al obtener el esquema de validacion";
//			LOGGER.error(msg);
//			AonUtil.addErrorMessage(msg);
//		}
//		return null;
//	}

//	public class ContractValidationEventHandler implements ValidationEventHandler {
//		public boolean handleEvent(ValidationEvent ve) {
//			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
//				ValidationEventLocator locator = ve.getLocator();
//				// Print message from valdation event
//				System.out.println("Invalid booking document: " + locator.getURL());
//				System.out.println("Error: " + ve.getMessage());
//				// Output line and column number
//				System.out.println("Error at column "
//						+ locator.getColumnNumber() + ", line "
//						+ locator.getLineNumber());
//			}
//			return true;
//		}
//	}
	
//	private Map<String, String> contractDataMap;
//	
//	protected Map<String, String> getContractDataMap() {
//		if(contractDataMap==null || contractDataMap.isEmpty()){
//			PayrollUtils utils = new PayrollUtils();
//			contractDataMap = utils.getContractDataMap((Contract)contractController.getTo());
//		}
//		return contractDataMap;
//	}
	
//	public List<SelectItem> getTownNames(){
//		String BASE_NAME = "com.esferalia.aon.payroll.i18n.towns";
//		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
//		Contract contract = (Contract)contractController.getTo();
//		List<SelectItem> towns = new LinkedList<SelectItem>();
//		try {
//			String geozone = contract.getPerson().getRegistry().getDefaultAddress().getGeozone().getCode();
//			TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
//			for(String key: tree){
//				if(key.startsWith(geozone)){
//					String name = bundle.getString(key);
//					SelectItem item = new SelectItem(key, name);
//					towns.add(item);
//				}
//			}
//			return towns;
//		} catch (ManagerBeanException e) {
//			// NADA, se devuelve una lista vacia
//		}
//		return null;
//	}
	
}
