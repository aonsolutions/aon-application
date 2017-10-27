package com.esferalia.aon.gwt.common.client.i18n;

import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.ActivityGroup;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.i18n.client.Messages;

public interface CommonMessages extends Messages {
	// ¡ --> \u00C1 · --> \u00E1 
	// … --> \u00C9 È --> \u00E9 
	// Õ --> \u00CD Ì --> \u00ED 
	// ” --> \u00D3 Û --> \u00F3 
	// ⁄ --> \u00DA ˙ --> \u00FA ... acento
	// ‹ --> \u00DC ¸ --> \u00fc ... diÈresis
	// — --> \u00D1 Ò --> \u00F1
	// ∫ --> \u00BA ™ --> \u00AA 
	// ø --> \u00BF
	
	// ----------------------------------------------------------------- Format
	@DefaultMessage("#,##0")
	String integerPattern();

	@DefaultMessage("#,##0.00")
	String decimalPattern();

	// -----------------------------------------------------------Enum Messages
	@DefaultMessage("----------")
	@AlternateMessage({
		 "0",  "Enero"
		,"1",  "Febrero"
		,"2",  "Marzo"
		,"3",  "Abril"
		,"4",  "Mayo"
		,"5",  "Junio"
		,"6",  "Julio"
		,"7",  "Agosto"
		,"8",  "Septiembre"
		,"9",  "Octubre"
		,"10", "Noviembre"
		,"11", "Diciembre"
		})
	String month(@Select int month);
	
	@DefaultMessage("Mes")
	String monthName();
	
	@DefaultMessage("Meses")
	String months();

	/**
	 * @param province
	 * @return
	 * @deprecated use Province.getName
	 */
	@DefaultMessage("----------")
	@AlternateMessage({ "DESCONOCIDO", "Desconocido", "ARABA",
			"Araba/\u00C1lava", "ALBACETE", "Albacete", "ALICANTE", "Alicante",
			"ALMERIA", "Almer\u00EDa", "ASTURIAS", "Asturias", "AVILA",
			"\u00C1vila", "BADAJOZ", "Badajoz", "BARCELONA", "Barcelona",
			"BIZKAIA", "Bizkaia", "BURGOS", "Burgos", "CACERES",
			"C\u00E1ceres", "CADIZ", "C\u00E1diz", "CANTABRIA", "Cantabria",
			"CASTELLON", "Castell\u00F3n", "CEUTA", "Ceuta", "CIUDAD_REAL",
			"Ciudad Real", "CORDOBA", "C\u00F3rdoba", "A_CORUNA",
			"Coru\u00F1a, A", "CUENCA", "Cuenca", "GIPUZKOA", "Gipuzkoa",
			"GIRONA", "Girona", "GRANADA", "Granada", "GUADALAJARA",
			"Guadalajara", "HUELVA", "Huelva", "HUESCA", "Huesca",
			"ILLES_BALEARS", "Illes Balears", "JAEN", "Jaen", "LEON",
			"Le\u00F3n", "LLEIDA", "Lleida", "LUGO", "Lugo", "MADRID",
			"Madrid", "MALAGA", "M\u00E1laga", "MELILLA", "Melilla", "MURCIA",
			"Murcia", "NAVARRA", "Navarra", "OURENSE", "Ourense", "PALENCIA",
			"Palencia", "LAS_PALMAS", "Palmas, Las", "PONTEVEDRA",
			"Pontevedra", "LA_RIOJA", "Rioja, La", "SALAMANCA", "Salamanca",
			"TENERIFE", "S.C. Tenerife", "SEGOVIA", "Segovia", "SEVILLA",
			"Sevilla", "SORIA", "Soria", "TARRAGONA", "Tarragona", "TERUEL",
			"Teruel", "TOLEDO", "Toledo", "VALENCIA", "Valencia", "VALLADOLID",
			"Valladolid", "ZAMORA", "Zamora", "ZARAGOZA", "Zaragoza",

			"NO_RESIDENTE", "No residente" })
	String provinceName(@Select Province province);

	@DefaultMessage("{0}")
	@AlternateMessage({
	 		 "=0", "-------------------"
	 		,"=1", "Territorio espa\u00F1ol, excepto Pais Vasco y Navarra"
			,"=2", "Pais Vasco o Navarra"
			,"=3", "Sin referencia catastral"
			})
	String buildingLocationValue(@PluralCount int location);

	@DefaultMessage("----------")
	@AlternateMessage(
			{"NIF"				,"NIF"
			,"CIF"				,"CIF"
			,"NIE"				,"NIE"
			,"PASSPORT"			,"Pasaporte"
			,"WORK_PERMIT"		,"Perm. Trab."
			,"COMMUNITY_CARD"	,"Tarj. Comun."
			,"OTHER","Otro"
			})
	String documentType(@Select DocumentType d);

	@DefaultMessage("-----")
	@AlternateMessage(
		{"M111"		,"Mod. 111"
		,"M115" 	,"Mod. 115"
		,"M123"		,"Mod. 123"
		,"M130"		,"Mod. 130"
		,"M131"		,"Mod. 131"
//		,"M303_RG"	,"Mod. 303 R\u00E9g. Gen."
//		,"M303_RS"	,"Mod. 303 R\u00E9g. Sim."
		,"M340"		,"Mod. 340"
		,"M347"		,"Mod. 347"
		,"M349"		,"Mod. 349"
		,"M390"		,"Mod. 390"
		,"M390_HF"	,"Mod. 390 Hac. For."
		,"M180"		,"Mod. 180"
		,"M184"		,"Mod. 184"
		,"M190"		,"Mod. 190"
		,"M193"		,"Mod. 193"
		,"M310"		,"Mod. 310"
		,"M311"		,"Mod. 311"
		,"M200"		,"Mod. 200"
		,"M202"		,"Mod. 202"
		,"M303"		,"Mod. 303"
		})	
	String fiscalModelType(@Select FiscalModelType f);

	@DefaultMessage("-----")
	@AlternateMessage(
		{"M111"		,"Retenciones e ingresos a cuenta. Rendimientos del trabajo y de actividades econ\u00F3micas."
		,"M115" 	,"Retenciones e ingresos a cuenta. Rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos."
		,"M123"		,"Retenciones e ingresos a cuenta. Determinados rendimientos del capital mobiliario o determinadas rentas."
		,"M130"		,"IRPF. Empresarios y profesionales. Pago fraccionado."
		,"M131"		,"IRPF. Empresarios y profesionales en Estimaci\u00F3n Objetiva. Pago fraccionado."
//		,"M303_RG"	,"IVA. Autoliquidaci\u00F3n. R\u00E9gimen General"
//		,"M303_RS"	,"IVA. Autoliquidaci\u00F3n. R\u00E9gimen Simplificado"
		,"M340"		,"Mod. 340"
		,"M347"		,"Mod. 347"
		,"M349"		,"Mod. 349"
		,"M390"		,"IVA. Declaraci\u00F3n Resumen Anual."
		,"M390_HF"	,"Mod. 390 Hac. For."
		,"M180"		,"Declaraci\u00F3n Informativa. Retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles urbanos. Resumen anual."
		,"M184"		,"Declaraci\u00F3n Informativa. Entidades en r\u00E9gimen de atribuci\u00F3n de rentas."
		,"M190"		,"Declaraci\u00F3n Informativa. Retenciones e ingresos a cuenta. Rendimientos del trabajo y de actividades econ\u00F3micas, premios y determinadas ganancias patrimoniales e imputaciones de rentas"
		,"M193"		,"Declaraci\u00F3n Informativa. Retenciones e ingresos a cuenta del IRPF sobre determinados rendimientos del capital mobiliario, del IS e IRNR sobre determinadas rentas."
		,"M310"		,"IVA. Autoliquidaci\u00F3n. R\u00E9gimen Simplificado"
		,"M311"		,"IVA. Autoliquidaci\u00F3n Final. R\u00E9gimen Simplificado"
		,"M200"		,"Declaraci\u00F3n-liquidaci\u00F3n del Impuesto sobre Sociedades."
		,"M202"		,"Impuesto sobre sociedades. Pago fraccionado."
		,"M303"		,"IVA. Autoliquidaci\u00F3n."
		})	
	String fiscalModelDescriptionlong(@Select FiscalModelType f);

	@DefaultMessage("ESP")
	String currencyCode();
	
	@DefaultMessage("Notificaciones")
	String notifications();

	@DefaultMessage("Resumen")
	String summary();

	@DefaultMessage("Desglose informaci\u00F3n")
	String informationBreakdown();

	@DefaultMessage("Integral fiscal")
	String fiscalPanel();

	@DefaultMessage("Modelo")
	String model();

	@DefaultMessage("Modelo 111")
	String mod111();

	@DefaultMessage("Modelo 115")
	String mod115();

	@DefaultMessage("Modelo 123")
	String mod123();

	@DefaultMessage("Modelo 130")
	String mod130();

	@DefaultMessage("Modelo 131")
	String mod131();
	
	@DefaultMessage("IRPF. Empresarios y profesionales en Estimaci\u00F3n Objetiva. Pago fraccionado.")
	String mod131Long();

	@DefaultMessage("Modelo 140")
	String mod140();
	
	@DefaultMessage("Modelo 303")
	String mod303();
	
	@DefaultMessage("Modelo 340")
	String mod340();
	
	@DefaultMessage("Modelo 347")
	String mod347();
	
	@DefaultMessage("Modelo 349")
	String mod349();
	
	@DefaultMessage("Desglose de facturas por series")
	String invoiceSeriesBreakdown();

	@DefaultMessage("Libro-registro de operaciones econ\u00F3micas")
	String mod140Description();

	@DefaultMessage("Modelo 190")
	String mod190();

	@DefaultMessage("Modelo 193")
	String mod193();

	@DefaultMessage("Modelo 180")
	String mod180();

	@DefaultMessage("Modelo 184")
	String mod184();

	@DefaultMessage("Modelo 390")
	String mod390();
	
	@DefaultMessage("Impuesto sobre el Valor A\u00F1adido")
	String mod390Desc();
	
	@DefaultMessage("Declaraci\u00F3n-Resumen anual")
	String mod390Desc2();
	
	@DefaultMessage("Modelo 200")
	String mod200();

	@DefaultMessage("Impuesto sobre Sociedades.")
	String mod200Desc();

	@DefaultMessage("Impuesto sobre la Renta de no Residentes.")
	String mod200Desc2();

	@DefaultMessage("(establecimientos permanentes y entidades en r\u00E9gimen de atribuci\u00F3n de rentas constituidas en el extranjero con presencia en territorio espa\u00F1ol)")
	String mod200Desc3();

	@DefaultMessage("Modelo 202")
	String mod202();
	
	@DefaultMessage("Impuesto sobre sociedades. Pago fraccionado.")
	String mod202Long();
	
	@DefaultMessage("Call Center")
	String callcenterModule();
	
	// ---------------------------------------------------------------- Errores
	@DefaultMessage("La Fecha del Asiento no est\u00E1 dentro del periodo asignado al ejercicio")
	String accountEntryOutOfRange();
	
	@DefaultMessage("El ejericio contable se encuentra en estado \"{0}\". No se permite la modificaci\u00F3n/borrado del asiento.")
	String periodStatusWarning(String periotStatus);

	@DefaultMessage("No se permite la modificaci\u00F3n/borrado del asiento..")
	String entryNoUpdatable();

	@DefaultMessage("Asiento autom\u00E1tico")
	String automaticEntryWarning();

	@DefaultMessage("No se permite la modificaci\u00F3n de asientos autom\u00E1ticos.")
	String automaticEntryNoUpdateWarning();

	@DefaultMessage("No ha sido posible recuperar los bancos de la empresa ({0}) ")
	String unableToShowCompanyBanks(String message);

	@DefaultMessage("No ha sido posible mostrar los datos ({0}) ")
	String unableToShowData(String message);
	
	@DefaultMessage("Valor num\u00E9rico no correcto ({0}) ")
	String numericValueError(String value);
	
	@DefaultMessage("Expresion aritmetica no correcta ({0})")
	String arithmeticExpressionError(String value);

	@DefaultMessage("No se pudo leer la declaraci\u00F3n. Causa: \n {0}")
	String unableToReadDeclaration(String cause);

	@DefaultMessage("No se pudo guardar la declaraci\u00F3n. Causa: \n {0}")
	String unableToSaveDeclaration(String cause);
	
	@DefaultMessage("No se pudo reabrir la declaraci\u00F3n. Causa: \n {0}")
	String unableToReopenDeclaration(String message);	

	@DefaultMessage("No se pudo marcar la declaraci\u00F3n como enviada . Causa: \n {0}")
	String unableToMarkAsSentDeclaration(String message);	

	@DefaultMessage("No se pudo finalizar la declaraci\u00F3n. Causa: \n {0}")
	String unableToFinishDeclaration(String message);	

	@DefaultMessage("No se pudo borrar la declaraci\u00F3n. Causa: \n {0}")
	String unableToDeleteDeclaration(String message);

	@DefaultMessage("No se pudo inicializar la declaraci\u00F3n. Causa: \n {0}")
	String unableToInitializeDeclaration(String message);

	@DefaultMessage("No se pudo encontrar la declaraci\u00F3n seleccionada.")
	String unableToFindDeclaration();

	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindDetail(String field);

	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindMod190Detail(String field);

	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindMod193Detail(String field);


	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindMod184Detail(String field);

	@DefaultMessage("No existe un perceptor con c\u00F3digo {0}")
	String unableToFindPerceptor(Integer id);

	@DefaultMessage("No se pueden leer los par\u00E1metros fiscales. {0}")
	String unableToReadFiscalParameters(String message);

	@DefaultMessage("El valor del ejercicio es incorrecto")
	String unableToParseYear();

	@DefaultMessage("El dato \"{0}\" es obligatorio")
	String requiredField(String field);

	// ---------------------------------------------------------Button Messages
	@DefaultMessage("Desglosar")
	String breakdown();
	
	@DefaultMessage("Nuevo")
	String newAction();

	@DefaultMessage("Nuevo {0}")
	String newSomething(String message);

	@DefaultMessage("Guardar")
	String saveAction();

	@DefaultMessage("Borrar")
	String deleteAction();
	
	@DefaultMessage("Duplicar")
	String duplicate();

	@DefaultMessage("Seleccionar")
	String selectAction();
	
	@DefaultMessage("Auditor\u00eda")
	String audit();

	@DefaultMessage("No hay informaci\u00F3n acerca de la creaci\u00F3n")
	String emptyCreatedBy();
	
	@DefaultMessage("Creado por \"{0}\" el dia \"{1,date,medium}\" a las  \"{1,time,medium}\"") 
	String createdBy(@Select String user,Date date);
	
	@DefaultMessage("No hay informaci\u00F3n acerca de la modificaci\u00F3n")
	String emptyModifiedBy();
	
	@DefaultMessage("Modificado por \"{0}\" el dia \"{1,date,medium}\" a las  \"{1,time,medium}\"")
	String modifiedBy(@Select String user,Date date);

	@DefaultMessage("{0} - {1,date,medium} {1,time,medium}")
	String auditBy(@Select String user,Date date);

	@DefaultMessage("\u00BFContinuar con el borrado?")
	String confirmDeleteAction();

	@DefaultMessage("\u00BFDeshacer el borrado?")
	String confirmRestoreAction();

	@DefaultMessage("Si continua se borrar\u00E1 la declaraci\u00F3n completa.\n \u00BFContinuar con el borrado?")
	String confirmDeclarationDeleteAction();

	@DefaultMessage("Si continua se borrar\u00E1n los todos los datos de la actividad.\n \u00BFContinuar con el borrado?")
	String newEpigrapSelected();

	@DefaultMessage("La actividad seleccionada es diferente. Si continua se borrar\u00E1n los datos de la actividad introducidos.\n \u00BFContinuar?")
	String epigrapChanged();

	@DefaultMessage("Confirme si desea cancelar la confecci\u00F3n de la declaraci\u00F3n.")
	String confirmDeclarationCancelAction();

	@DefaultMessage("Restaurar")
	String restoreAction();

	@DefaultMessage("Buscar")
	String searchAction();

	@DefaultMessage("Cancelar")
	String cancelAction();
	
	@DefaultMessage("Continuar")
	String continueAction();
	
	@DefaultMessage("Ir")
	String goAction();

	@DefaultMessage("Aceptar")
	String accept();

 	@DefaultMessage("Ocultar")
 	String hide();
 	
 	@DefaultMessage("Vista previa")
	String preview();

	@DefaultMessage("C\u00F3digo")
	String code();

	@DefaultMessage("Generar fichero")
	String generateFile();
	
	@DefaultMessage("Fichero")
	String generateFileAbr();
	
	@DefaultMessage("Imprimir certificado")
	String printCertificate();

	@DefaultMessage("Certificado")
	String certificate();

	@DefaultMessage("Borrador")
	String draft();

	@DefaultMessage("Impresi\u00F3n del borrador")
	String draftPrint();

	@DefaultMessage("La impresi\u00F3n del borrador se realiza a partir de los datos guardados. Aseg\u00FArese de haber guardado la declaraci\u00F3n.")	
	String draftPrintNote();
	
	@DefaultMessage("La impresi\u00F3n se realiza a partir de los datos guardados. Aseg\u00FArese de haber guardado la declaraci\u00F3n.")	
	String printNote();

	@DefaultMessage("Generaci\u00F3n de fichero")
	String fileGeneration();
	@DefaultMessage("Se va a proceder a la generaci\u00F3n de un fichero con los datos de la declaraci\u00F3n, para su "
			+ "presentaci\u00F3n en Hacienda. Aseg\u00FArese de haber guardado la declaraci\u00F3n. "
			+ "El fichero se genera a partir de los datos guardados.")
	String fileGenerationNote();

	@DefaultMessage("Ver detalle")
	String seeDetail();

	@DefaultMessage("Imprimir via AEAT")
	String printViaAeat();

	@DefaultMessage("Validar / Imprimir via AEAT")
	String validatePrintViaAeat();

	@DefaultMessage("Validar / Borrador / Predeclaraci\u00F3n")
	String predeclaration();

	@DefaultMessage("Imprimir")
	String print();

	@DefaultMessage("Finalizado")
	String finished();

	@DefaultMessage("Pendiente")
	String pending();
	
	@DefaultMessage("Aprobado")
	String approved();
	
	@DefaultMessage("Rechazado")
	String refused();
	
	@DefaultMessage("Servido")
	String served();
	
	@DefaultMessage("Cerrado")
	String closed();
	
	@DefaultMessage("Bloqueado")
	String blocked();

	@DefaultMessage("Facturado")
	String invoiced();

	@DefaultMessage("Error")
	String error();

	@DefaultMessage("Problemas")
	String problems();

	@DefaultMessage("Aviso")
	String warning();

	@DefaultMessage("Procesando su orden. Por favor, espere")
	String processing();

	@DefaultMessage("SI")
	String yes();

	@DefaultMessage("NO")
	String no();

	// ---------------------------------------------------------Common Messages
	@DefaultMessage("Declaraci\u00F3n")
	String declaration();
	
	@DefaultMessage("A\u00F1o")
	String year();
	
	@DefaultMessage("Periodo")
	String period();

	@DefaultMessage("Concepto")
	String concept();

	@DefaultMessage("Cuenta bancaria")
	String bankAccount();

	@DefaultMessage("Desde")
	String from();

	@DefaultMessage("Hasta")
	String until();

	@DefaultMessage("Fecha")
	String date();

	@DefaultMessage("Fecha IVA")
	String taxDate();

	@DefaultMessage("No se han encontrado datos")
	String noData();

	@DefaultMessage("{0} - {1} de {2}")
	String exactPagerData(int start, int end, int count);

	@DefaultMessage("{0} - {1} de \u00BF?")
	String unexactPagerData(int start, int end);

	@DefaultMessage("Tipo")
	String type();
	
	@DefaultMessage("Empresa")
	String enterprise();

	@DefaultMessage("Datos de empresa")
	String enterpriseData();

	@DefaultMessage("Modelos fiscales")
	String fiscalModels();

	@DefaultMessage("Administraci\u00F3n")
	String administration();

	@DefaultMessage("Ejercicio")
	String fiscalYear();

	@DefaultMessage("Sustitutiva")
	String replacement();
	
	@DefaultMessage("Sustit.")
	String replacementAbbr();

	@DefaultMessage("Pendiente de pago")
	String pendingPayment();

	@DefaultMessage("Declaraci\u00F3n sustituva por rectificaci\u00F3n de cuotas en caso de concurso de acreedores ( art. 80.Tres LIVA)")
	String replacementDueInsolvencyState();

	@DefaultMessage("Num. Declaraci\u00F3n")
	String receipt();

	@DefaultMessage("N. Decl. Sustituida")
	String replacedReceipt();

	@DefaultMessage("N\u00BA Decl. Anterior")
	String previousDeclaration();

	@DefaultMessage("Complementaria")
	String complementary();

	@DefaultMessage("Complem.")
	String complementaryAbbr();

	@DefaultMessage("C\u00E1lculo por diferencia")
	String diffCalculation();

	@DefaultMessage("Sin actividad")
	String withoutActivity();

	@DefaultMessage("N. justificante anterior")
	String complementaryReceipt();
	
	@DefaultMessage("N\u00FAmero de justificante identificativo de la autoliquidaci\u00F3n anterior.")
	String previousReceipt();

	@DefaultMessage("Confidencial")
	String confidential();

	@DefaultMessage("Identificaci\u00F3n")
	String identification();

	@DefaultMessage("Datos identificativos")
	String identificationDate();

	@DefaultMessage("Informaci\u00F3n")
	String information();

	@DefaultMessage("Informaci\u00F3n Tributaria")
	String fiscalInformation();

	@DefaultMessage("Serie")
	String series();
	
	@DefaultMessage("N\u00FAmero")
	String number();

	@DefaultMessage("Documento")
	String document();

	@DefaultMessage("N.I.F.")
	String nif();

	@DefaultMessage("Nombre")
	String name();

	@DefaultMessage("Alias / Nombre comercial")
	String alias();
	
	@DefaultMessage("Nacionalidad")
	String nationality();

	@DefaultMessage("\u00C1mbito")
	String scope();

	
	@DefaultMessage("Nombre, raz\u00F3n social o denominaci\u00F3n")
	String nameCompanyName();

	@DefaultMessage("Apellidos y Nombre, raz\u00F3n social o denominaci\u00F3n")
	String companyName();

	@DefaultMessage("Raz\u00F3n social")
	String enterpriseName();

	@DefaultMessage("Apellidos")
	String surname();

	@DefaultMessage("Comentarios")
	String comments();

	@DefaultMessage("Estado")
	String status();
	
	@DefaultMessage("Estado Vto.")
	String financeStatus();

	@DefaultMessage("Descripci\u00F3n")
	String description();

	@DefaultMessage("Centro de trabajo")
	String workplace();

	@DefaultMessage("Forma de pago")
	String payMethod();

	@DefaultMessage("Fecha de Vto.")
	String dueDate();

//	@DefaultMessage("Contabilizar pago")
//	String payAccount();

	@DefaultMessage("Tel\u00E9fono")
	String phone();
	
	@DefaultMessage("M\u00F3vil")
	String cellular();

	@DefaultMessage("Fax")
	String fax();
	
	@DefaultMessage("eMail")
	String email();
	
	@DefaultMessage("Web")
	String web();

	@DefaultMessage("Datos del inmueble")
	String buildingData();

	@DefaultMessage("Situaci\u00F3n del inmueble")
	String buildingLocation();

	@DefaultMessage("Referencia catastral")
	String cadasdralReference();

	@DefaultMessage("Tipo v\u00EDa")
	String streetType();

	@DefaultMessage("Nombre de la v\u00EDa p\u00FAblica")
	String streetName();

	@DefaultMessage("Tipo de num.")
	String streetNumberType();

	@DefaultMessage("N\u00FAmero")
	String streetNumber();

	@DefaultMessage("Calif. N\u00FAm.")
	String streetNumberSuffix();

	@DefaultMessage("Bloq.")
	String streetBlock();

	@DefaultMessage("Port.")
	String streetHall();

	@DefaultMessage("Esc.")
	String streetStair();

	@DefaultMessage("Piso")
	String streetFloor();

	@DefaultMessage("Prta.")
	String streetDoor();

	@DefaultMessage("Complemento.")
	String streetComplement();

	@DefaultMessage("Localidad o poblaci\u00F3n")
	String city();

	@DefaultMessage("Municipio")
	String town();

	@DefaultMessage("C\u00F3digo de municipio")
	String townCode();

	@DefaultMessage("Cod.Postal")
	String zip();

	@DefaultMessage("Tipo de gr\u00E1fico")
	String graphicType();
	
	@DefaultMessage("Base imponible")
	String taxableBase();
	
	@DefaultMessage("Base Imp.")
	String taxableBaseAbr();

	@DefaultMessage("Cuota")
	String quota();

	@DefaultMessage("% Ded.")
	String dedPercent();

	@DefaultMessage("Cuota Deduc.")
	String dedQuota();

	@DefaultMessage("Cta. Ajustes")
	String adjAccount();

	@DefaultMessage("Total")
	String total();

	@DefaultMessage("Totales")
	String totals();

	@DefaultMessage("Porcentaje")
	String percent();

	@DefaultMessage("Porc.")
	String percentAbbr();

	@DefaultMessage("Cantidad")
	String quantity();

	@DefaultMessage("Cantidades")
	String quantities();

	@DefaultMessage("Importe")
	String amount();

	@DefaultMessage("Unidad")
	String unit();

	@DefaultMessage("Factor")
	String factor();

	@DefaultMessage("Actividad")
	String activity();

	@DefaultMessage("Actividades a las que se refiere la declaraci\u00F3n")
	String activities();

	@DefaultMessage("Modulos. Actividades empresariales")
	String moduleActivities();

	@DefaultMessage("(de mayor a menor importacia por vol\u00FAmen de operaciones)")
	String activitiesNote();

	@DefaultMessage("Territorio Com\u00FAn")
	String commonTerritory();

	@DefaultMessage("Araba/\u00C1lava")
	String alava();

	@DefaultMessage("Gipuzkoa")
	String gipuzkoa();

	@DefaultMessage("Bizkaia")
	String bizkaia();

	@DefaultMessage("Navarra")
	String navarra();

	@DefaultMessage("Actividad principal")
	String mainActivity();

	@DefaultMessage("Otras actividades")
	String otherActivities();

	@DefaultMessage("Selecci\u00F3n de actividad")
	String activitySelection();

	@DefaultMessage("Descripci\u00F3n actividad")
	String activityDescription();

	@DefaultMessage("Ep\u00EDgrafe")
	String epigraph();

	@DefaultMessage("Marque si ha efectuado operaciones por las que tenga la "
			+ "obligaci\u00F3n de presentar la declaraci\u00F3n anual "
			+ "de operaciones con terceras personas.")
	String mod347Check();

	@DefaultMessage("DECLARACI\u00D3N DE SUJETO PASIVO INCLU\u00CDDO "
			+ "EN AUTOLIQUIDACIONES CONJUNTAS")
	String mergedDeclarationLabel();
	
 	@DefaultMessage("Estad\u00EDsticas")
	String statistics();


	// -------------------------------------------------------------- Model 190

	@DefaultMessage("Nueva renta")
	String newIncome();

	@DefaultMessage("Nuevo socio")
	String newPartner();

	@DefaultMessage("Nuevo perceptor")
	String newPerceptor();

	@DefaultMessage("Datos del declarante")
	String deponentData();

	@DefaultMessage("Declarante")
	String deponent();

	@DefaultMessage("Declarantes")
	String deponents();

	@DefaultMessage("Datos de la declaraci\u00F3n")
	String declarationData();

	@DefaultMessage("Persona y tel\u00E9fono de contacto")
	String contactData();

	@DefaultMessage("Persona de contacto")
	String contactPerson();

	@DefaultMessage("Tel\u00E9fono de contacto")
	String contactPhone();

	@DefaultMessage("M\u00F3vil de contacto")
	String contactCellular();

	@DefaultMessage("Correo de contacto")
	String contactMail();

	@DefaultMessage("N\u00FAmero total de percepciones relacionadas en la declaraci\u00F3n")
	String receiverCountTotal();

	@DefaultMessage("Datos de la percepci\u00F3n")
	String perceptionData();

	@DefaultMessage("Importe total de las percepciones relacionadas")
	String receiptTotal();

	@DefaultMessage("Importe total de las retenciones e ingresos a cuenta relacionados")
	String retentionTotal();

	@DefaultMessage("Relaci\u00F3n de Perceptores")
	String receiverList();

	@DefaultMessage("NIF Perceptor")
	String receiverDocument();

	@DefaultMessage("NIF Repr.")
	String representativeDocument();

	@DefaultMessage("Apellidos y nombre o denominaci\u00F3n")
	String fullName();

	@DefaultMessage("Ej.Dev.")
	String accrualYear();

	@DefaultMessage("Provincia")
	String province();

	@DefaultMessage("Pa\u00EDs")
	String country();

	@DefaultMessage("Ceu.Mel.")
	String ceutaMelillaAbbrv();

	@DefaultMessage("Rentas Obtenidas en Ceuta o Melilla")
	String ceutaMelilla();

	@DefaultMessage("Clave")
	String key();

	@DefaultMessage("Subclave")
	String subkey();

	@DefaultMessage("Dinerarias")
	String money();

	@DefaultMessage("Percepciones \u00CDntegras")
	String perception();
	
	@DefaultMessage("Percepci\u00F3n \u00CDntegras / Valoraci\u00F3n")
	String perceptionValoration();
	
	@DefaultMessage("Ret. aplic./ Ingr. a cta.")
	String retentionIncome();
	
	@DefaultMessage("Retenciones")
	String retention();

	@DefaultMessage("En especie")
	String inKind();

	@DefaultMessage("Valoraci\u00F3n")
	String inKindPerception();

	@DefaultMessage("Ingr. a cta. efectuados")
	String inKindDeposit();

	@DefaultMessage("Ingr. a cta. repercutidos")
	String inKindOutputDeposit();

	@DefaultMessage("Datos adicionales (s\u00F3lo en percepciones de las claves A, B.01, B.02, C o D)")
	String additionalData190();

	@DefaultMessage("Datos adicionales")
	String additionalData();

	@DefaultMessage("A\u00F1o de nacimiento")
	String birthYear();

	@DefaultMessage("Situaci\u00F3n familiar")
	String familySituation();

	@DefaultMessage("NIF C\u00F3nyuge")
	String spouseDocument();

	@DefaultMessage("Discapacidad")
	String disability();

	@DefaultMessage("Contrato o relaci\u00F3n")
	String contract();

	@DefaultMessage("Prolongaci\u00F3n activ.laboral")
	String workActivityExtension();

	@DefaultMessage("Movilidad geogr\u00E1fica. Aceptaci\u00F3n 2014.")
	String geographicMobility2014();

	@DefaultMessage("Movilidad geogr\u00E1fica")
	String geographicMobility();

	@DefaultMessage("Reducciones aplicables")
	String applicableReduction();

	@DefaultMessage("Gastos deducibles")
	String deducibleExpense();

	@DefaultMessage("Pensiones compensatorias")
	String compensatoryPension();

	@DefaultMessage("Anualidades por alimentos")
	String foodAnnuality();
	
	@DefaultMessage("Realiza pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual.")
	String regularHomePayments(); 

	@DefaultMessage("Comunicaci\u00F3n pr\u00E9stamos vivienda habitual")
	String homeLoanCommunnication();

	@DefaultMessage("Hijos o descendientes comunicados por el perceptor")
	String descendant();

	@DefaultMessage("Menores de 3 a\u00F1os")
	String lessThan3();

	@DefaultMessage("Por Ent.")
	String byInteger();

	@DefaultMessage("Resto")
	String remainder();

	@DefaultMessage("C\u00F3mputo de los tres primeros")
	String first3Calculation();

	@DefaultMessage("Hijos o descendientes con discapacidad")
	String disabilityDescendant();

	@DefaultMessage("\u2265 33\u0025 y \u003C 65\u0025")
	String moreThan33lessThan65();

	@DefaultMessage("Movilidad reducida")
	String reducedMovilitiy();

	@DefaultMessage("\u2265 65\u0025")
	String moreThan65();

	@DefaultMessage("\u003C 75 a\u00F1os")
	String lessThan75();

	@DefaultMessage("\u2265 75 a\u00F1os")
	String greatherThan75();

	@DefaultMessage("Ascendientes comunicados por el perceptor")
	String ascendant();

	@DefaultMessage("Ascendientes con discapacidad")
	String disabilityAscendant();

	@DefaultMessage("1\u00BA")
	String first();

	@DefaultMessage("2\u00BA")
	String second();

	@DefaultMessage("3\u00BA")
	String third();

	// -------------------------------------------------------------- Model 390

	@DefaultMessage("Sujeto Pasivo")
	String pasiveSubject();

	@DefaultMessage("Sujeto Pasivo y Devengo")
	String pasiveSubjectAndAccrual();

	@DefaultMessage("Devengo")
	String accrual();

	@DefaultMessage("Datos estad\u00EDsticos")
	String stadisticalData();

	@DefaultMessage("Datos del representante")
	String representativeData();

	@DefaultMessage("Datos de los representantes legales")
	String legalRepresentativeData();
	
	@DefaultMessage("Datos del Secretario del Consejo o persona que cumple las funciones en el \u00F3rgano que sustituye a dicho Consejo. Declarante o representante.")
	String secretaryData();

	@DefaultMessage("PERSONAS F\u00CDSICAS Y ENTIDADES SIN PERSONALIDAD JUR\u00CDDICA")
	String nonLegalEntities();

	@DefaultMessage("PERSONAS JUR\u00CDDICAS")
	String legalEntities();

	@DefaultMessage("Fecha poder")
	String registrationDate();

	@DefaultMessage("Notar\u00EDa")
	String notary();
	
	@DefaultMessage("Fecha Notar\u00EDa")
	String notaryDate();

	@DefaultMessage("Operaciones realizadas en R\u00E9gimen general")
	String generalRegimeOperations();

	@DefaultMessage("R\u00E9gimen general")
	String generalRegime();

	@DefaultMessage("IVA DEVENGADO")
	String outputVat();

	@DefaultMessage("IVA DEDUCIBLE")
	String inputVat();

	@DefaultMessage("R\u00E9gimen simplificado")
	String simplifiedRegime();

	@DefaultMessage("Operaciones realizadas en R\u00E9gimen simplificado")
	String simplifiedRegimeOperations();

	@DefaultMessage("N\u00BA Unidades de m\u00F3dulo")
	String moduleUnits();

	@DefaultMessage("M\u00F3dulo")
	String module();

	@DefaultMessage("Cuota devengada operaciones corrientes")
	String page6C();

	@DefaultMessage("Cuotas soportadas operaciones corrientes")
	String page6D();
	
	@DefaultMessage("Cuo. Sop. op. corr.")
	String page6DAbbr();

	@DefaultMessage("\u00CDndice corrector")
	String page6E();

	@DefaultMessage("\u00CDndice corrector de actividades de temporada")
	String tempIndex();

	@DefaultMessage("Porcentaje de ingreso a cuenta")
	String incomePercent();
	
	@DefaultMessage("Ingreso a cuenta")
	String income();

	@DefaultMessage("RESULTADO")
	String page6F();

	@DefaultMessage("Porcentaje cuota m\u00EDnima")
	String page6G();

	@DefaultMessage("Devoluci\u00F3n cuotas soportadas otros paises")
	String page6H();

	@DefaultMessage("Cuota m\u00EDnima")
	String page6I();

	@DefaultMessage("Cuota derivada r\u00E9gimen simplificado")
	String page6J();

	@DefaultMessage("Cuota derivada")
	String derQuota();

	@DefaultMessage("Actividades agr\u00EDcolas, ganaderas y forestales")
	String farmerActivity();

	@DefaultMessage("Actividades en r\u00E9gimen simplificado")
	String simplifieedActivities();

	@DefaultMessage("Premios")
	String prizes();

	@DefaultMessage("Retribuciones en especie y otras")
	String inKindAndOthers();

	@DefaultMessage("Volumen Ingresos")
	String f02Msg();

	@DefaultMessage("\u00CDndice Cuota")
	String f03Msg();

	@DefaultMessage("Cuota Devengada")
	String f04Msg();

	@DefaultMessage("Cuotas soportadas")
	String f05Msg();

	@DefaultMessage("Suma de cuotas derivadas r\u00E9gimen simplificado (Actividades no agr\u00EDcolas)")
	String box74Msg();

	@DefaultMessage("Suma de cuotas derivadas r\u00E9gimen simplificado (Actividades agr\u00EDcolas)")
	String box75Msg();

	@DefaultMessage("IVA devengado en adquisiciones intracomunitarias de bienes")
	String box76Msg();

	@DefaultMessage("IVA devengado por inversi\u00F3n de sujeto pasivo (adquisiciones intracomunitarias de servicios y otros supuestos)")
	String box77Msg();

	@DefaultMessage("IVA devengado en entregas de activos fijos")
	String box78Msg();

	@DefaultMessage("TOTAL CUOTA RESULTANTE")
	String box79Msg();

	@DefaultMessage("IVA soportado en adquisici\u00F3n de activos fijos")
	String box80Msg();

	@DefaultMessage("Resultado del r\u00E9gimen simplificado")
	String box83Msg();

	@DefaultMessage("Resultado liquidaci\u00F3n anual")
	String annualLiquidationResult();
	
	@DefaultMessage("Regularizaci\u00F3n cuotas art. 80.Cinco.5\u00BA LIVA")
	String regQuotaArt80();

	@DefaultMessage("IVA a la importaci\u00F3n liquidado por la Aduana (s\u00F3lo sujetos pasivos con opci\u00F3n de diferimiento")
	String importIVACustoms();

	@DefaultMessage("Suma de Resultados")
	String resultSum();

	@DefaultMessage("Compensaci\u00F3n de cuotas del ejercicio anterior")
	String previousYearCompensation();

	@DefaultMessage("Resultado de la liquidaci\u00F3n")
	String liquidationResult();

	@DefaultMessage("Tributaci\u00F3n por raz\u00F3n de Territorio")
	String taxByTerritory();

	@DefaultMessage("Resultado atribuible al territorio com\u00FAn")
	String commonTerritoryResult();

	@DefaultMessage("Compensaci\u00F3n de cuotas del ejercicio anterior atribuible a territorio com\u00FAn")
	String commonTerritoryQuotaCompensation();

	@DefaultMessage("Resultado de la declaraci\u00F3n anual atribuible a territorio com\u00FAn")
	String commonTerritoryDeclarationResult();

	@DefaultMessage("Resultado de  las liquidaciones")
	String liquidationsResult();

	@DefaultMessage("Per\u00EDodos que no tributan en R\u00E9gimen especial del grupo de entidades")
	String commonRegimePeriods();

	@DefaultMessage("Total resultados a ingresar en las autoliquidaciones de IVA del ejercicio")
	String depositDeclarationsResult();

	@DefaultMessage("Total devoluciones mensuales de IVA solicitadas por sujetos pasivos inscritos en el Registro de devoluci\u00F3n mensual")
	String paybacksTotal();

	@DefaultMessage("Total devoluciones solicitadas por cuotas soportadas en la adquisici\u00F3n de elementos de transporte (Art. 30 bis RIVA)")
	String paybackTransportTotal();

	@DefaultMessage("Si el resultado de la autoliquidaci\u00F3n del \u00FAltimo periodo es a compensar o a devolver consigne su importe:")
	String lastDeclarationResult();

	@DefaultMessage("A compensar")
	String toCompensate();

	@DefaultMessage("A devolver")
	String toPayback();

	@DefaultMessage("Per\u00EDodos que tributan en R\u00E9gimen especial del grupo de entidades")
	String entityGroupRegimePeriods();

	@DefaultMessage("Total resultados positivos autoliquidaciones del ejercicio (modelo 322)")
	String mod322PositiveResults();

	@DefaultMessage("Total resultados negativos autoliquidaciones del ejercicio (modelo 322)")
	String mod322NegativeResults();

	@DefaultMessage("Vol\u00FAmen de operaciones")
	String operationsVolume();

	@DefaultMessage("Vol\u00FAmen ingresos")
	String incomeVolume();

	@DefaultMessage("Exclusivamente para aquellos sujetos pasivos acogidos al r\u00E9gimen "
			+ "especial del criterio de caja y para aquellos que sean destinatarios de "
			+ "operaciones afectadas por el mismo")
	String accrualRegimeOperations();

	@DefaultMessage("Operaciones en r\u00E9gimen general")
	String box99Msg();

	@DefaultMessage("Operaciones a las que habi\u00E9ndoles aplicado el r\u00E9gimen especial "
			+ "del criterio de caja hubieran resultado devengadas conforme a la regla general "
			+ "de devengo contenida en el art.75 LIVA")
	String box653Msg();

	@DefaultMessage("Entregas intracomunitarias exentas")
	String box103Msg();

	@DefaultMessage("Exportaciones y otras operaciones exentas con derecho a deducci\u00F3n")
	String box104Msg();

	@DefaultMessage("Operaciones exentas sin derecho a deducci\u00F3n")
	String box105Msg();

	@DefaultMessage("Operaciones no sujetas por reglas de localizaci\u00F3n o con inversi\u00F3n del sujeto pasivo")
	String box110Msg();

	@DefaultMessage("Entregas de bienes objeto de instalaci\u00F3n o montaje en otros Estados miembros")
	String box112Msg();

	@DefaultMessage("Operaciones en r\u00E9gimen simplificado")
	String box100Msg();

	@DefaultMessage("Operaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca")
	String box101Msg();

	@DefaultMessage("Operaciones realizadas por sujetos pasivos acogidos al r\u00E9gimen especial del recargo de equivalencia")
	String box102Msg();

	@DefaultMessage("Operaciones en R\u00E9gimen especial de bienes usados, objetos de arte, antig√ºedades y objetos de colecci\u00F3n")
	String box227Msg();

	@DefaultMessage("Operaciones en r\u00E9gimen especial de Agencias de Viajes")
	String box228Msg();

	@DefaultMessage("Entregas de bienes inmuebles y operaciones financieras no habituales")
	String box106Msg();

	@DefaultMessage("Entregas de bienes de inversi\u00F3n")
	String box107Msg();

	@DefaultMessage("Total volumen de operaciones (Art. 121 Ley IVA)")
	String box108Msg();

	@DefaultMessage("Operaciones espec\u00EDficas")
	String specificOperations();

	@DefaultMessage("Adquisiciones interiores exentas")
	String box230Msg();

	@DefaultMessage("Adquisiciones intracomunitarias exentas")
	String box109Msg();

	@DefaultMessage("Importaciones exentas")
	String box231Msg();

	@DefaultMessage("Bases imponibles del IVA soportado no deducible")
	String box232Msg();

	@DefaultMessage("Operaciones sujetas y no exentas que originan el derecho a la devoluci\u00F3n mensual")
	String box111Msg();

	@DefaultMessage("Entregas interiores de bienes devengadas por inversi\u00F3n del sujeto pasivo como consecuencia de operaciones triangulares")
	String box113Msg();

	@DefaultMessage("Servicios localizados en el territorio de aplicaci\u00F3n del impuesto por inversi\u00F3n de sujeto pasivo")
	String box523Msg();
	
	@DefaultMessage("Importes de las entregas de bienes y prestaciones de servicios a las que"
			+ "habi\u00E9ndoles sido aplicado el r\u00E9gimen especial del criterio de caja "
			+ "hubieran	resultado devengadas conforme a la regla general de devengo contenida "
			+ "en el art. 75 LIVA")
	String accrualRegimeOutputMsg();
	
	@DefaultMessage("Importe de las adquisiciones de bienes y servicios a las que sea de"
			+ "aplicaci\u00F3n o afecte el r\u00E9gimen especial del criterio de caja "
			+ "conforme a la regla general de devengo contenida en el art. 75 LIVA")
	String accrualRegimeInputMsg();
	
	@DefaultMessage("Prorrata")
	String prorrata();
	
	@DefaultMessage("Porcentaje de prorrata")
	String prorrataPercent();
	
	@DefaultMessage("Ha modificado el porcentaje de prorata. El modelo se inicializar\u00E1 a partir de los datos de facturas. \u00BFDesea continuar?.")
	String prorateChanged();

	@DefaultMessage("Actividades con reg\u00EDmenes de deducci\u00F3n diferenciados")
	String difActivitiesRegime();

	@DefaultMessage("Registro de devoluci\u00F3n mensual en alg\u00FAn per\u00EDodo del ejercicio")
	String taxRefund();

	@DefaultMessage("\u00BFLas declaraciones del \u00FAltimo periodo de liquidaci\u00F3n del ejercicio corresponden a declaraciones concursales?")
	String insolvencyDeclarations();
	
	@DefaultMessage("Concurso de acreedores")
	String insolvencyState();

	@DefaultMessage("Preconsursal")
	String preInsolvencyState();

	@DefaultMessage("Postconsursal")
	String postInsolvencyState();

	@DefaultMessage("\u00BFHa sido declarado en concurso de acreeedores en este ejercicio?")
	String insolvencyStateThisYear();
	
	@DefaultMessage("\u00BFLas autoliquidaciones del \u00FAltimo periodo de liquidaci\u00F3n corresponden a declaraciones concursales?")
	String insolvencyStateLastPeriod();
	
	@DefaultMessage("Criterio de caja")
	String cashAccrualRegime();

	@DefaultMessage("\u00BFHa optado por el r\u00E9gimen especial del criterio de caja (art. 163 undecies LIVA)?")
	String accrualRegime();
	
	@DefaultMessage("\u00BFHa sido destinatario de operaciones a las que se aplique el r\u00E9gimen especial del criterio de caja?")
	String accrualRegimeTarget();

	@DefaultMessage("R\u00E9gimen especial del grupo de entidades en "
			+ "alg\u00FAn per\u00EDodo del ejercicio")
	String specialGroupRegime();

	@DefaultMessage("Grupo")
	String group();

	@DefaultMessage("N\u00BA Grupo")
	String groupNumber();

	@DefaultMessage("Dependiente")
	String groupDependent();

	@DefaultMessage("Tipo r\u00E9gimen especial aplicable: Art. 163 sexies.cinco")
	String groupRegimeType();

	@DefaultMessage("NIF de la sociedad representante/dominante (incluida en el grupo fiscal)")
	String groupDocument();
	
	@DefaultMessage("N\u00BA identificaci\u00F3n de la sociedad dominante (en el caso de grupos constituidos s\u00F3lo por entidades dependientes)")
	String dominantIdentificationNumber();

	@DefaultMessage("\u00BFLa autoliquidaci\u00F3n del \u00FAltimo "
			+ "per\u00EDodo corresponde al r\u00E9gimen especial del "
			+ "grupo de entidades?")
	String groupDeclarations();

	@DefaultMessage("S\u00F3lo para sujetos pasivos que tributen exclusivamente "
			+ "a la Administraci\u00F3n del Estado. Si tributa a varias "
			+ "Administraciones (Pa\u00EDs Vasco o Navarra) no rellene este apartado.")
	String page7HelpText();

	@DefaultMessage("S\u00F3lo para sujetos pasivos que tributan a varias Administraciones")
	String page8HelpText();
	
	@DefaultMessage("Datos de secretario, representantes y administradores")
	String administratorPage();

	@DefaultMessage("Relaci\u00F3n de administradores")
	String administratorList();

	@DefaultMessage("Participaciones")
	String participations();

	@DefaultMessage("Participaciones directas de la declarante en otras sociedades y de otras personas o entidades en la declarante a la fecha de cierre del per\u00EDodo declarado")
	String participationsTitle();

	@DefaultMessage("Participaciones de la declarante en otras sociedades")
	String participationsOut();

	@DefaultMessage("Participaciones de personas o entidades en la declarante")
	String participationsIn();

	@DefaultMessage("Participaciones directas de la declarante en otras sociedades y de otras personas o entidades en la declarante a la fecha de cierre del per\u00EDodo declarado")
	String participationsDesc();

	@DefaultMessage("Balance: Activo")
	String balanceActivo();

	@DefaultMessage("Balance: Patrimonio Neto y Pasivo")
	String balancePasivo();

	@DefaultMessage("Balance: Patr. Neto y Pasivo")
	String balancePasivoAbbrv();

	@DefaultMessage("Cuenta de p\u00E9rdidas y ganancias")
	String pyg();

	@DefaultMessage("ECPN. Estado de ingresos y gastos reconocidos en el ejercicio")
	String patrimonioIngresos();

	@DefaultMessage("ECPN. Estado de ingresos y gastos")
	String patrimonioIngresosAbbrv();

	@DefaultMessage("ECPN. Estado total de cambios en el patrimonio neto")
	String patrimonioCambios();

	@DefaultMessage("Declaraci\u00F3n relativa al periodo impositivo comprendido desde el ")
	String periodLabel();

	@DefaultMessage("al")
	String to();

	@DefaultMessage("Tipo de ejercicio")
	String periodType();

	@DefaultMessage("C.N.A.E. actividad principal")
	String mainActivityCNAE();

	@DefaultMessage("CNAE Act. Prin.")
	String mainActivityCNAEAbbr();

	@DefaultMessage("Caracteres de la declaraci\u00F3n")
	String declarationCharacters();
	
	@DefaultMessage("La declaraci\u00F3n para el car\u00E1cter [{0}] no se encuentra disponible")
	String unsupportedCharacter(String character);

	@DefaultMessage("Tipo de declaraci\u00F3n")
	String declarationType();
	
	@DefaultMessage("Reg\u00EDmenes aplicables")
	String availableRegimes();

	@DefaultMessage("Otros caracteres")
	String otherCharacters();
	
	@DefaultMessage("Balance")
	String balanceSheet();

	@DefaultMessage("E.C.P.N.")
	String ecpn();

	@DefaultMessage("P\u00E9rdidas y Ganancias")
	String profitAndLoss();

	@DefaultMessage("Beneficio")
	String profit();

	@DefaultMessage("Domicilio fiscal")
	String fiscalAddress();
	
	@DefaultMessage("Direcci\u00F3n")
	String address();

	@DefaultMessage("Valor nominal")
	String nominalValue();

	@DefaultMessage("Nuevo administrador")
	String newAdministrator();

	@DefaultMessage("Participaciones de importe igual o superior al 5% del capital o al 1% si se trata de valores que coticen en un mercado secundario organizado.")
	String partMsg1();

	@DefaultMessage("Datos de la participada")
	String partMsg2();

	@DefaultMessage("Datos en los registros de la declarante")
	String partMsg3();

	@DefaultMessage("Porcentaje de participaci\u00F3n")
	String partMsg4();

	@DefaultMessage("Valor nominal total de la participaci\u00F3n")
	String partMsg5();

	@DefaultMessage("Valor en libros (en el activo de la declarante) de la participaci\u00F3n")
	String partMsg6();

	@DefaultMessage("Ingresos por Dividendos recibidos en el ejercicio declarado")
	String partMsg7();

	@DefaultMessage("Correcciones valorativas por deterioro y cambios en el valor razonable")
	String partMsg8();

	@DefaultMessage("a) Correcci\u00F3n de valor incluida en p\u00E9rdidas y ganancias del ejercicio")
	String partMsg9();

	@DefaultMessage("b) Reversi\u00F3n de p\u00E9rdidas por deterioro de valores (D.T. 41a LIS)")
	String partMsg10();
	
	@DefaultMessage("b) Reversi\u00F3n de p\u00E9rdidas por deterioro de valores (D.T. 16a LIS)")
	String partMsg101();

	@DefaultMessage("c) Efecto de la correcci\u00F3n valorativa en la BI del ejercicio")
	String partMsg11();
	
	@DefaultMessage("c) Eliminaci\u00F3n del deterioro contable incluido en P y G (art. 13.2b) LIS)")
	String partMsg111();

	@DefaultMessage("d) Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio")
	String partMsg12();
	
	@DefaultMessage("d) Efecto de la correcci\u00F3n valorativa en la BI del ejercicio")
	String partMsg121();
	
	@DefaultMessage("e) Saldo de correcciones fiscales (art. 12.3 RDL 4/2004) pendientes a fin de ejercicio")
	String partMsg122();

	@DefaultMessage("Datos adicionales de la participada:")
	String partMsg13();

	@DefaultMessage("Capital")
	String partMsg14();

	@DefaultMessage("Reservas y otras partidas de fondos propios")
	String partMsg15();

	@DefaultMessage("Otras partidas del patrimonio neto")
	String partMsg16();

	@DefaultMessage("Resultado del \u00FAltimo ejercicio")
	String partMsg17();

	@DefaultMessage("Capital Escriturado (N, A, P))")
	String ecpnMsg1();

	@DefaultMessage("Capital (No exigido) (N, A, P)")
	String ecpnMsg2();

	@DefaultMessage("Prima de emisi\u00F3n (N, A, P)")
	String ecpnMsg3();

	@DefaultMessage("Reservas (N, A, P)")
	String ecpnMsg4();

	@DefaultMessage("(Acciones y partic. en patr. propias) (N, A, P)")
	String ecpnMsg5();

	@DefaultMessage("Resultados de ejercicios anteriores (N, A, P)")
	String ecpnMsg6();

	@DefaultMessage("Otras aportaciones de socios (N, A, P)")
	String ecpnMsg7();

	@DefaultMessage("Resultado del ejercicio (N, A, P)")
	String ecpnMsg8();

	@DefaultMessage("(Dividendo a cuenta) (N, A, P)")
	String ecpnMsg9();

	@DefaultMessage("Otros instrumentos de patrimonio neto (N, A)")
	String ecpnMsg10();

	@DefaultMessage("Ajustes por cambios de valor (N, A)")
	String ecpnMsg11();

	@DefaultMessage("Ajustes en patrimonio neto (P)")
	String ecpnMsg12();

	@DefaultMessage("Subv. donac. y legados recibidos (N, A, P)")
	String ecpnMsg13();

	@DefaultMessage("TOTAL (N, A, P)")
	String ecpnMsg14();
	

	@DefaultMessage("SALDO, FINAL DEL EJERCICIO ANTERIOR (N, A, P)")
	String ecpnMsg15();

	@DefaultMessage("Ajustes por cambio de criterio de los ejercicios anteriores (N, A, P)")
	String ecpnMsg16();

	@DefaultMessage("Ajustes por errores de los ejercicios anteriores (N, A, P)")
	String ecpnMsg17();

	@DefaultMessage("SALDO AJUSTADO, INICIO DEL EJERCICIO (N, A, P)")
	String ecpnMsg18();

	@DefaultMessage("Total ingresos y gastos reconocidos (N, A)")
	String ecpnMsg19();

	@DefaultMessage("Resultado de la cuenta de p\u00E9rdidas y ganancias (P)")
	String ecpnMsg20();

	@DefaultMessage("Ingresos y gastos reconocidos en patrimonio neto (P)")
	String ecpnMsg21();

	@DefaultMessage("Ingresos fiscales a distribuir en varios ejercicios (P)")
	String ecpnMsg22();

	@DefaultMessage("Otros ingresos y gastos reconocidos en patrimonio neto (P)")
	String ecpnMsg23();

	@DefaultMessage("Operaciones con socios o propietarios (N, A, P)")
	String ecpnMsg24();

	@DefaultMessage("Aumentos de capital (N, A, P)")
	String ecpnMsg25();

	@DefaultMessage("(-) Reducciones de capital (N, A, P)")
	String ecpnMsg26();

	@DefaultMessage("Conversi\u00F3n de pasivos financ. en patr. neto (conv. de obligac. condonaciones de deudas) (N)")
	String ecpnMsg27();

	@DefaultMessage("(-) Distribuci\u00F3n de dividendos (N) ")
	String ecpnMsg28();

	@DefaultMessage("Operaciones con acciones o participaciones propias (netas) (N)")
	String ecpnMsg29();

	@DefaultMessage("Incremento (reducci\u00F3n) de patr. neto resultante de una combinaci\u00F3n de negocios (N)")
	String ecpnMsg30();

	@DefaultMessage("Otras operaciones con socios o propietarios (N, A, P)")
	String ecpnMsg31();

	@DefaultMessage("Otras variaciones del patrimonio neto (N, A, P)")
	String ecpnMsg32();

	@DefaultMessage("Movimiento de la reserva de revalorizaci\u00F3n (N, A, P)")
	String ecpnMsg33();

	@DefaultMessage("Otras variaciones (N, A, P)")
	String ecpnMsg34();

	@DefaultMessage("SALDO, FINAL DEL EJERCICIO (N, A, P)")
	String ecpnMsg35();

	
	@DefaultMessage("1.- Ejercicio econ\u00F3mico de 12 meses de duraci\u00F3n, que coincida con el a\u00F1o natural.")
	String periodType1();
	
	@DefaultMessage("2.- Ejercicio econ\u00F3mico de 12 meses de duraci\u00F3n, que no coincida con el a\u00F1o natural.")
	String periodType2();
	
	@DefaultMessage("3.- Ejercicio econ\u00F3mico de duraci\u00F3n inferior a 12 meses.")
	String periodType3();
		
	@DefaultMessage("Liquidaci\u00F3n")
	String liquidacion();
	
	@DefaultMessage("Resultado de la cuenta de p\u00E9rdidas y ganancias")
	String liquidation1Label1();

	@DefaultMessage("Detalle de las correcciones a la cuenta de p\u00E9rdidas y ganancias.")
	String liquidation1Label2();
	
	@DefaultMessage("Liquidaci\u00F3n (I)")
	String liquidacionI();
	@DefaultMessage("Liquidaci\u00F3n (II)")
	String liquidacionII();
	@DefaultMessage("Liquidaci\u00F3n (III)")
	String liquidacionIII();
	@DefaultMessage("Liquidaci\u00F3n (IV)")
	String liquidacionIV();
	@DefaultMessage("Liquidaci\u00F3n (V)")
	String liquidacionV();

	@DefaultMessage("Tipo Correcci\u00F3n")
	String correctionType();

	@DefaultMessage("Desde Ejercicio")
	String fromYear();

	@DefaultMessage("Hasta Ejercicio")
	String toYear();

	@DefaultMessage("Aumento")
	String increase();
	
	@DefaultMessage("Disminuci\u00F3n")
	String decrease();
	
	@DefaultMessage("Correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	String corrections();
	
	@DefaultMessage("Correcciones del ejericicio")
	String yearCorrections();

	@DefaultMessage("Saldo pendiente a fin de ejercicio")
	String pendingCorrections();
	
	@DefaultMessage("Correcciones fiscales")
	String fiscalCorrections();

	@DefaultMessage("Bonificaciones y deducciones por doble imposici\u00F3n. Cuota \u00EDntegra ajustada positiva")
	String bonus();
	
	@DefaultMessage("Otras deducciones. Cuota l\u00EDquida positiva")
	String otherDeductions();

	@DefaultMessage("Deuda tributaria a ingresar")
	String fiscalDebt();
	
	@DefaultMessage("Cuota del ejercicio a ingresar o a devolver")
	String yearQuota();
	
	@DefaultMessage("Pagos fraccionados. Cuota diferencial")
	String splittedPayments();
	
	@DefaultMessage("L\u00EDquido a ingresar o a devolver")
	String netQuota();

	@DefaultMessage("Estado")
	String state();
	
	@DefaultMessage("D. Forales / Navarra")
	String forales();
	
	@DefaultMessage("Tributaci\u00F3n conjunta al Estado, Pa\u00EDs Vasco y Navarra")
	String combinedTaxation();
	@DefaultMessage("Tributaci\u00F3n conjunta")
	String combinedTaxationAbbrv();
	
	@DefaultMessage("Aplicaci\u00F3n del concierto econ\u00F3mico con la Comunidad Aut\u00F3noma del Pa\u00EDs Vasco y del Convenio Econ\u00F3mico entre el Estado y la Comunidad Foral de Navarra")
	String combinedTaxation1();

	@DefaultMessage("C\u00E1lculo de los porcentajes de tributaci\u00F3n a cada una de las Administraciones")
	String combinedTaxation2();
	
	@DefaultMessage("Determinaci\u00F3n del l\u00EDquido a ingresar o a devolver a cada una de las Administraciones")
	String combinedTaxation3();
	
	
	@DefaultMessage("Aplicaci\u00F3n de resultados")
	String incomeDistribution();
	
	@DefaultMessage("Presentaci\u00F3n de documentaci\u00F3n previa en la sede electr\u00F3nica (consigne el N\u00FAmero de Referencia de Sociedades (NRS))")
	String documentsElectronicSede();
	
	@DefaultMessage("Documentaci\u00F3n presentada por el Anexo III (Ajustes y deducciones)")
	String nrsAnexoIII();
	@DefaultMessage("Documentaci\u00F3n presentada por el Anexo IV (Personal investigador)")
	String nrsAnexoIV();
	@DefaultMessage("Documento normalizado presentada por el Anexo V")
	String nrsAnexoV();
	@DefaultMessage("N\u00FAmero de justificante identificativo de la declaraci\u00F3n informativa de ayudas R\u00E9gimen Econ\u00F3mico y Fiscal de Canarias") 
	String justCanarias();
	@DefaultMessage("N\u00FAmero de justificante identificativo autoliquidaci\u00F3n de la prestaci\u00F3n patrimonial por conversi\u00F3n de activos (DA 13a LIS)")
	String justActivos();
	
	@DefaultMessage("Limitaci\u00F3n en la deducibilidad de gastos financieros.")
	String deducibleLimitation();

	@DefaultMessage("Limitaci\u00F3n deduc. gastos financieros.")
	String deducibleLimitationAbbrv();

	@DefaultMessage("L\u00EDmite art. 16.5, o 83 LIS")
	String limitMsg1();

	@DefaultMessage("L\u00EDmite art. 16.1 y 16.2 LIS")
	String limitMsg2();
	
	@DefaultMessage("R\u00E9gimen especial Canarias")
	String page17();
	
	@DefaultMessage("R\u00E9gimen especial de la reserva para inversiones en Canarias (Ley 19/1994)")
	String canariasRegime();

	@DefaultMessage("Dotaci\u00F3n / Reversi\u00F3n / Conversi\u00F3n")
	String page18();

	@DefaultMessage("Limitaci\u00F3n en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir")
	String deducibleLimitationPending();
	
	@DefaultMessage("Pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado")
	String pendingAddinngs();
	
	@DefaultMessage("Dotaciones pendientes de integraci\u00F3n a principio del per\u00EDodo/generado en el propio per\u00EDodo")
	String dot1();
	
	@DefaultMessage("Dotaciones pendientes de integraci\u00F3n a principio del per\u00EDodo")
	String dot3();

	@DefaultMessage("Dotaciones pendientes de integraci\u00F3n en per\u00EDodos futuros")
	String dot2();
	
	@DefaultMessage("Que no han cumplido condiciones de deducibilidad fiscal")
	String dot11();
	
	@DefaultMessage("Que han cumplido condiciones de deducibilidad fiscal pero no integradas por aplicaci\u00F3n l\u00EDmite")
	String dot12();
	
	@DefaultMessage("Dotaciones integradas en esta liquidaci\u00F3n")	
	String dot22();
	
	@DefaultMessage("Dotaciones aplicadas por la conversi\u00F3n de los activos por impuesto diferido")	
	String dot23();	
	
	@DefaultMessage("Importe total AID pendientes de aplicaci\u00F3n a principio del per\u00EDodo (DT 33a.1 y 6 a) LIS)")
	String dot30();
	@DefaultMessage("AID aplicados en el per\u00EDodo por integraci\u00F3n dotaciones en la liquidaci\u00F3n)")
	String dot31();
	@DefaultMessage("AID convertidos en cr\u00E9dito exigible en el per\u00EDodo")
	String dot32();
	@DefaultMessage("Con prestaci\u00F3n patrimonial (DT 33a.6 c) LIS)")
	String dot33();
	@DefaultMessage("Sin prestaci\u00F3n patrimonial (DT 33a.6 d) LIS)")
	String dot34();
	@DefaultMessage("Sin prestaci\u00F3n patrimonial. Minoraci\u00F3n prestaci\u00F3n por exceso cuota otros per\u00EDodos iniciados a partir de 2016 (DT 33a.4 y 6 d) LIS)")
	String dot35();
	@DefaultMessage("Importe total AID pendientes (DT 33a.1 y 6 a) LIS)(*)")
	String dot36();
	
	@DefaultMessage("AID pendientes de aplicaci\u00F3n a principio del per\u00EDodo/generados en el propio per\u00EDodo")
	String dot4_1();
	
	@DefaultMessage("AID pendientes de aplicaci\u00F3n en per\u00EDodos futuros")
	String dot4_2();
	
	@DefaultMessage("Importe total AID (art. 130.6 a) LIS) pendientes de aplicaci\u00F3n a principio "
			+ "del per\u00EDodo/generados en el propio per\u00EDodo")
	String dot40();
	@DefaultMessage("Cuota l\u00EDquida positiva")
	String dot41();
	@DefaultMessage("Con derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130.6 b) LIS)")
	String dot42();
	@DefaultMessage("Con derecho conversi\u00F3n en cr\u00E9dito exigible por exceso cuota otros per\u00EDodos (art. 130.1 y 6 b) LIS)")
	String dot43();
	@DefaultMessage("Sin derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130.6 c) LIS)")
	String dot44();
	@DefaultMessage("AID aplicados en el per\u00EDodo (por integraci\u00F3n dotaciones en la liquidaci\u00F3n)")
	String dot45();
	@DefaultMessage("AID convertidos en cr\u00E9dito exigible en el per\u00EDodo")
	String dot46();
	@DefaultMessage("Con derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130.6 b) LIS)")
	String dot47();
	@DefaultMessage("Con derecho a conversi\u00F3n en cr\u00E9dito exigible por exceso cuota otros per\u00EDodos (art. 130.1 y 6 b) LIS)")
	String dot48();
	@DefaultMessage("Sin derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130.6 c)LIS)")
	String dot49();

	@DefaultMessage("Exceso cuota l\u00EDquida positiva pendiente a principio del per\u00EDodo/generado en el propio per\u00EDodo")
	String dot50();
	@DefaultMessage("Exceso cuota l\u00EDquida positiva aplicado en per\u00EDodos impositivos de 2008 a 2015 (DT 33a.4 LIS)")
	String dot51();
	@DefaultMessage("Exceso cuota l\u00EDquida positiva aplicado en per\u00EDodos impositivos iniciados a partir de 2016 (art. 130.1 p\u00E1rrafo 2o LIS)")
	String dot52();
	@DefaultMessage("Exceso cuota l\u00EDquida positiva pendiente de aplicaci\u00F3n en per\u00EDodos futuros")
	String dot53();

	@DefaultMessage("N\u00FAmero de per\u00EDodo impositivo")	
	String numPer();

	@DefaultMessage("Dotaciones por deterioro de cr\u00E9ditos u otros activos derivados de las posibles insolvencias de los deudores no vinculados con el contribuyemte y otras del art. 11.12 LIS. conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la adm\u00F3n. tributaria (ART. 130 LIS)")
	String damageAmount();

	@DefaultMessage("Conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria")
	String damageAmount1();
	
	@DefaultMessage("Dotaciones por deterioro de cr\u00E9ditos u otros activos derivados "
		+ "de las posibles insolvencias de los deudores no vinculados con el contribuyente "
		+ "y otras del art. 11.12 LIS con posibilidad de conversi\u00F3n en cr\u00E9dito exigible (**).")
	String damageAmount2();
	
	@DefaultMessage("Reversi\u00F3n de las p\u00E9rdidas por deterioro de valores representativos de "
			+ "la participaci\u00F3n en el capital o en los fondos"
			+"propios de entidades pendientes de reversi\u00F3n (DT 16a LIS).")
	String damageAmount3();
	
	@DefaultMessage("Activos por impuesto diferido (AID). DT 33a y DA 13a LIS")
	String damageAmount4();
	
	@DefaultMessage("Activos por impuesto diferido (AID). Art. 130 LIS")
	String damageAmount5();

	@DefaultMessage("Exceso cuota l\u00EDquida positiva (art. 130.1 y DT 33a.4 LIS)")
	String damageAmount6();
	
	@DefaultMessage("C\u00E1lculo autom\u00E1tico")
	String authomaticCalculation();
	
	@DefaultMessage("Validar")
	String validateAction();
	
	@DefaultMessage("Calcular")
	String calculateAction();

	@DefaultMessage("Mensaje")
	String errorMessage();
	
	@DefaultMessage("P\u00E1gina")
	String page();

	@DefaultMessage("Casilla")
	String box();

	@DefaultMessage("Mensaje")
	String message();
	
	@DefaultMessage("Detalle de la compensaci\u00F3n de bases imponibles negativas")
	String compensationDetail();
	
	@DefaultMessage("Pendiente de aplicaci\u00F3n a principio de ejercicio")
	String previousPending();
	
	@DefaultMessage("Aplicado en esta liquidaci\u00F3n")
	String current();
	
	@DefaultMessage("Pendiente de aplicaci\u00F3n en periodos futuros")
	String futurePending();
	
	@DefaultMessage("Deducciones doble imposici\u00F3n interna")
	String doubleContributionNational();
	@DefaultMessage("Deducciones doble imposici\u00F3n interna ejerc. anteriores")
	String doubleContributionNationalPrevious();
	@DefaultMessage("Deducciones doble imposici\u00F3n interna 2013")
	String doubleContributionNational2013();
	@DefaultMessage("Deducciones doble imposici\u00F3n interna 2014")
	String doubleContributionNational2014();
	@DefaultMessage("Deducciones doble imposici\u00F3n interna 2015")
	String doubleContributionNational2015();
	@DefaultMessage("Deducciones doble imposici\u00F3n interna 2016")
	String doubleContributionNational2016();
	
	@DefaultMessage("Deducci\u00F3n pendiente/generada")
	String pendingDeduction();
	
	@DefaultMessage("Tipo gravamen")
	String taxType();
	
	@DefaultMessage("2013 deducci\u00F3n pendiente")
	String pendingDeduction2013();
	@DefaultMessage("2014 deducci\u00F3n pendiente")
	String pendingDeduction2014();
	@DefaultMessage("2015 deducci\u00F3n pendiente")
	String pendingDeduction2015();
	@DefaultMessage("2016 deducci\u00F3n pendiente")
	String pendingDeduction2016();
	
	@DefaultMessage("Deducci\u00F3n aplicada en esta liquidaci\u00F3n")
	String appliedDeduction();
	
	@DefaultMessage("Deducci\u00F3n pendiente per\u00EDodos futuros")
	String futureDeduction();
	
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional ejerc. anteriores")
	String doubleContributionInternationalPrevious();
	
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional 2013")
	String doubleContributionInternational2013();
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional 2014")
	String doubleContributionInternational2014();
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional 2015")
	String doubleContributionInternational2015();
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional 2016")
	String doubleContributionInternational2016();

	@DefaultMessage("Base de deducci\u00F3n")
	String deductionTaxablebase();

	@DefaultMessage("Deducci\u00F3n generada")
	String generatedDeduction();
	
	@DefaultMessage("Deducci\u00F3n reducida")
	String reducedDeduction();
	
	@DefaultMessage("Importe deducible en cuota")
	String quotableAmount();
	
	@DefaultMessage("Pendiente por insuficiencia de cuota")
	String pendingDueToQuota();
	
	@DefaultMessage("Importe pendiente")
	String pendingAmount();
	
	@DefaultMessage("Personal fijo")
	String fixedPersonal();

	@DefaultMessage("Personal no fijo")
	String nonFixedPersonal();

	@DefaultMessage("La validaci\u00F3n no ha generado ning\u00FAn mensaje.")
	String noValidationMessages();
	
	
	@DefaultMessage("R\u00E9gimen de cooperativas. Determinaci\u00F3n de la base imponible")
	String cooperativeRegime();
	
	@DefaultMessage("Resultados cooperativos")
	String cooperativeResult();

	@DefaultMessage("Resultados extracooperativos")
	String extraCooperativeResult();

	@DefaultMessage("Bases de reparto")
	String distributionBases();
	
	@DefaultMessage("Aplicaci\u00F3n")
	String aplication();
	
	@DefaultMessage("Aplicaci\u00F3n a")
	String aplicationTo();
	
	@DefaultMessage("Exportar datos contables")
	String aeatAccountingFile();
	@DefaultMessage("Exp.Dat.Ctb.")
	String aeatAccountingFileAbr();

	@DefaultMessage("Datos de Grupo Fiscal")
	String fiscalGroupLabel();

	@DefaultMessage("Numero de Grupo Fiscal")
	String fiscalGroup();

	@DefaultMessage("NIF de la sociedad dominante")
	String dominantDocument();
	
	@DefaultMessage("Documento de ingreso o Devoluci\u00F3n")
	String idDocument();

	@DefaultMessage("Devoluci\u00F3n")
	String payBack();

	@DefaultMessage("Ingreso")
	String deposit();

	@DefaultMessage("Cuota cero")
	String zeroQuota();

	@DefaultMessage("Renuncia a la devoluci\u00F3n")
	String payBackRefuse();
	
	@DefaultMessage("Devoluci\u00F3n por transferencia")
	String payBackTransfer();
	
	@DefaultMessage("Anotaci\u00F3n de devoluci\u00F3n por en CCT")
	String payBackCCT();

	@DefaultMessage("Anotaci\u00F3n de ingreso en CCT")
	String payCCT();

	@DefaultMessage("Adeudo en cuenta")
	String payInAccount();

	@DefaultMessage("Forma de pago")
	String paymentType();
	
	@DefaultMessage("En efectivo")
	String cash();
	
	@DefaultMessage("Domicialici\u00F3n")
	String directDebit();
	
	@DefaultMessage("I.B.A.N.")
	String iban();

	@DefaultMessage("Fecha I.R.N.R.")
	String irnrDate();

	@DefaultMessage("Importe total de las operaciones")
	String operationsAmount();

	@DefaultMessage("Importe de las operaciones con derecho a deducci\u00F3n")
	String operationsAmountWithRight();
	
	@DefaultMessage("Datos de la operaci\u00F3n")
	String operationData();
	
	@DefaultMessage("Naturaleza")
	String nature();
	
	@DefaultMessage("Pago a un intermediario")
	String intermediaryPayment();
	
	@DefaultMessage("Clave c\u00F3digo")
	String keyCode();
	
	@DefaultMessage("C\u00F3digo emisor")
	String issuingCode();
	
	@DefaultMessage("C\u00F3digo cuenta valores / N\u00FAmero operaci\u00F3n pr\u00E9stamo")
	String ccv();
	
	@DefaultMessage("Pago")
	String payment();
	
	@DefaultMessage("Tipo C\u00F3digo")
	String codeType();
	
	@DefaultMessage("Importe de percepciones/remuneraci\u00F3n al prestamista")
	String lenderAmount();
	
	@DefaultMessage("Reducciones")
	String reductions();
	
	@DefaultMessage("Base de retenciones e ingresos a cuenta")
	String retentionBase();
	
	@DefaultMessage("Trabajo")
	String work();

	@DefaultMessage("Actividades profesionales")
	String professionalActivities();

	@DefaultMessage("Retenciones e ingresos a cuenta")
	String retentionAccount();
	
	@DefaultMessage("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad signos, \u00EDndices o m\u00F3dulos")
	String modulesActivitiesYields();

	@DefaultMessage("Ret. e ingr. cta.")
	String retentionAccountShort();
	
	@DefaultMessage("Fecha de inicio del pr\u00E9stamo")
	String loanStart();
	
	@DefaultMessage("Fecha de Vto. del pr\u00E9stamo")
	String loanDueStart();
	
	@DefaultMessage("Compensaciones")
	String compensations();

	@DefaultMessage("Penalizaciones")
	String penalizations();

	@DefaultMessage("Garant\u00EDas")
	String guarantee();

	@DefaultMessage("Rentas obtenidas por la entidad")
	String entityIncomes();
	
	@DefaultMessage("Socios, herederos, comuneros o part\u00EDcipes")
	String entityPartners();
	
	
	@DefaultMessage("R\u00E9gimen de determinaci\u00F3n de rendimientos")
	String regime();
	
	@DefaultMessage("Tipo de actividad")
	String activityType();
	
	@DefaultMessage("NIF persona o entidad cesionaria")
	String granteeDocument();
	
	@DefaultMessage("Denominaci\u00F3n/Raz\u00F3n social de la entidad cesionaria")
	String granteeName();
	
	@DefaultMessage("Fecha adquisici\u00F3n acci\u00F3n/participaci\u00F3n")
	String adqDate();
	
	@DefaultMessage("Valor liquidativo / Ajustes: Aumentos")
	String adjustIncrease();
	
	@DefaultMessage("Valor adquisici\u00F3n / Ajustes: Disminuciones") 
	String adjustDecrease();
	
	@DefaultMessage("Resultado contable")
	String accountingResult();
	
	@DefaultMessage("Gastos")
	String expenses();
	
	@DefaultMessage("Renta atribuible / Rend. Neto atribuible")
	String netYieldExt();
	
	@DefaultMessage("Porc. Reducci\u00F3n")
	String reductionPercent();
	
	@DefaultMessage("Renta atrib. con drcho. deducci\u00F3n")
	String deductionRightRent();
	
	@DefaultMessage("Ganancias / P\u00E9rdidas")
	String profitLoss();
	
	@DefaultMessage("Base de la deducci\u00F3n / Importe")
	String deductionBase();
	
	@DefaultMessage("Base de la deducci\u00F3n")
	String deductionBaseAbbrv();
	
	@DefaultMessage("Retenciones e ingresos a cuenta")
	String retentionAccountDeposit();
	
	@DefaultMessage("Reducci\u00F3n")
	String reduction();
	@DefaultMessage("Porcentaje de participaci\u00F3n")
	String partPercent();
	@DefaultMessage("N\u00FAmero d\u00EDas miembro")
	String memberDays();
	@DefaultMessage("Miembro a 31 diciembre")
	String memberEndOfYear();
	@DefaultMessage("Clave tipo de part\u00EDcipe")
	String partType();
	
	@DefaultMessage("Entidades en r\u00E9gimen de atribuci\u00F3n de rentas constituidas en Espa\u00F1a.")
	String localEntities();
	@DefaultMessage("Tipo de entidad")
	String entityType();
	@DefaultMessage("Entidades en r\u00E9gimen de atribuci\u00F3n de rentas constituidas en el extranjero")
	String foreignEntities();
	@DefaultMessage("Objeto")
	String object();
	@DefaultMessage("Porcentaje de renta atribuible a miembros residentes")
	String residentPercent();
	@DefaultMessage("Tributaci\u00F3n en r\u00E9gimen del impuesto sobre sociedades")
	String isTax();
	@DefaultMessage("Importe neto de la cifra de negocios")
	String netAmount();
	@DefaultMessage("NIF del representante")
	String lrDocument();
	@DefaultMessage("Apellidos y nombre o raz\u00F3n social del representante")
	String lrName();
	
	@DefaultMessage("Valor incorrecto en el campo \"Ep\u00EDgrafe\"")
	String incorrectEpigraph();
	
	@DefaultMessage("Listado de facturas")
	String invoiceReport();

	@DefaultMessage("Filtro")
	String filter();

	@DefaultMessage("Fecha de emisi\u00F3n")
	String issueDate();
	
	@DefaultMessage("Fecha de Entrega")
	String deliveryDate();
	
	@DefaultMessage("I.R.P.F.")
	String irpf();

	@DefaultMessage("I.R.P.F. en especie")
	String inKindIrpf();
	
	@DefaultMessage("Recargo equivalencia")
	String surcharge();

	@DefaultMessage("% R.E.")
	String surchargePercent();

	@DefaultMessage("Cuota R.E.")
	String surchargeQuota();

	@DefaultMessage("Aplicar retenci\u00F3n")
	String withholding();
	
	@DefaultMessage("R\u00E9gimen agri., gan. y pesca.")
	String withholdingFarmer();
	
	@DefaultMessage("R\u00E9gimen de criterio de caja")
	String vatAccrualPayment();

	@DefaultMessage("R\u00E9g. crit. caja")
	String vatAccrualPaymentAbbr();

	@DefaultMessage("Tipo de transacci\u00F3n")
	String transactionType();

	@DefaultMessage("Transacci\u00F3n")
	String transaction();

	@DefaultMessage("Servicio")
	String service();

	@DefaultMessage("Tipo de factura")
	String invoiceType();

	@DefaultMessage("N\u00BA de factura")
	String invoiceNumber();

	@DefaultMessage("Total factura")
	String invoiceTotal();

	@DefaultMessage("Ventas")
	String sales();
	
	@DefaultMessage("Compras")
	String purchases();
	
	@DefaultMessage("Gastos no deducibles")
	String undeductibleExpenses();
	
	@DefaultMessage("Exportar")
	String export();
	
	@DefaultMessage("Cambio Empresa")
	String enterpriseChange();
	
	@DefaultMessage("Descarga")
	String download();

	@DefaultMessage("Facturas")
	String invoices();

	@DefaultMessage("Factura")
	String invoice();
	
	@DefaultMessage("Titular")
	String titular();

	@DefaultMessage("Tipo de titular")
	String titularType();
	
	@DefaultMessage("Presupuestos")
	String offers();
	
	@DefaultMessage("Pedidos de compra")
	String purchaseOrders();
	
	@DefaultMessage("Pedidos de venta")
	String saleOrders();
	
	@DefaultMessage("Albaranes de compra")
	String incomes();
	
	@DefaultMessage("Albaranes de venta")
	String deliveries();
	
	@DefaultMessage("[Comience a escribir para buscar empresas]")
	String startTyping();

	@DefaultMessage("Max. Personas")
	String maxPerson();

	@DefaultMessage("Lim. Exceso")
	String maxImport();

	@DefaultMessage("Porc. IVA")
	String vatPercent();
	
	@DefaultMessage("Cuota IVA")
	String vatQuota();

	@DefaultMessage("Cuenta IVA")
	String vatAccount();

	@DefaultMessage("IVA Sop.")
	String inputVatAccount();

	@DefaultMessage("IVA Rep.")
	String outputVatAccount();

	@DefaultMessage("Recibidas")
	String inputInvoices();

	@DefaultMessage("Emitidas")
	String outputInvoices();

	@DefaultMessage("Informaci\u00F3n adicional del ep\u00EDgrafe")
	String epigraphAdditionalInfo();

	@DefaultMessage("M\u00F3dulos.")
	String modules();

	@DefaultMessage("IRPF. M\u00F3dulos. Estimaci\u00F3n objetiva.")
	String irpfModules();

	@DefaultMessage("IVA. M\u00F3dulos. R\u00E9gimen simplificado.")
	String vatModules();
	
	@DefaultMessage("Click para cambiar el ep\u00EDgrafe.")
	String pushToChange();
	
	@DefaultMessage("I. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales.")
	String mod131Activities();
	
	@DefaultMessage("Rendimiento neto")
	String netYield();
	
	@DefaultMessage("Porc. aplicable")
	String appliedPercent();
	
	@DefaultMessage("Resultado")
	String result();
	
	@DefaultMessage("Suma de rendimientos netos")
	String netYieldSum();
	
	@DefaultMessage("Pago fraccionado previo del trimestre: Suma de resultados")
	String mod131ResultSum();

	@DefaultMessage("Reabrir")
	String reopen();
	
	@DefaultMessage("Finalizar")
	String finish();
	
	@DefaultMessage("Marcar como presentado")
	String markAsSent();
	
	@DefaultMessage("Cert. Retenciones")
	String generate10T();
	
	@DefaultMessage("Fecha de inicio del per\u00EDodo impositivo")
	String yearInitialDate();

	@DefaultMessage("A) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.2 LIS")
	String mod202Compute1();

	@DefaultMessage("B) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.3 LIS")
	String mod202Compute2();

	@DefaultMessage("Correcciones al resultado contable")
	String mod202Compute21();

	@DefaultMessage("B.1) Caso general (empresas con porcentaje \u00FAnico)")
	String mod202Compute3();

	@DefaultMessage("B.2) Casos espec\u00EDficos (empresas con m\u00E1s de un porcentaje)")
	String mod202Compute4();
	
	@DefaultMessage("Correcciones al resultado contable - por Impuesto sobre Sociedades")
	String mod202Correction1();
	
	@DefaultMessage("30% gastos amortiz (exc.  emp. reducidas)")
	String mod202Correction2();
	
	@DefaultMessage("Resto correcciones al resultado contable, excepto comp.")
	String mod202Correction3();
	
	@DefaultMessage("TOTAL")
	String mod202Correction4();

	@DefaultMessage("Reserva de nivelaci\u00F3n (art. 105 LIS) (Solo entidades del art. 101 LIS)")
	String mod202Correction5();

	@DefaultMessage("Reserva de nivelaci\u00F3n (105 LIS) convertido en cuotas")
	String mod202Correction6();
	
	@DefaultMessage("A) Art\u00EDculo LIS 40.2 LIS")
	String calculation0();
	
	@DefaultMessage("B.1) Casos Generales. Porcentaje \u00FAnico. (Art\u00EDculo 40.3 LIS.)")
	String calculation1();
	
	@DefaultMessage("B.2) Casos espec\u00EDficos. M\u00E1s de un porcentaje. (Art\u00EDculo 40.3 LIS.)")
	String calculation2();
	
	@DefaultMessage("I. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales.")
	String mod131Header1();
	
	@DefaultMessage("II. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales, sin posibilidad de determinar ninguno de los datos-base a efectos del pago fraccionado.")
	String mod131Header2();
	
	@DefaultMessage("III. Actividades agr\u00EDcolas, ganaderas y forestales, en estimaci\u00F3n objetiva.")
	String mod131Header3();
	
	@DefaultMessage("IV. Total liquidaci\u00F3n.")
	String mod131Header4();
	
	@DefaultMessage("Rendimientos dinerarios")
	String moneyYield();

	@DefaultMessage("Rendimientos en especie")
	String inKindYield();
	
	@DefaultMessage("Contrapartidas dinerarias o en especie")
	String moneyInKindReturn();
	
	@DefaultMessage("N\u00BA de perceptores")
	String receivers();

	@DefaultMessage("Percepciones")
	String perceptions();

	@DefaultMessage("Rellene la p\u00E1gina de \"Identificaci\u00F3n\" y pulse \"Continuar\" para completar el modelo.")
	String mustInitialzeMod200();
	
	@DefaultMessage("La p\u00E1gina no procede para su declaraci\u00F3n.")
	String pageNotAvailable();

	@DefaultMessage("Ejercicio de generaci\u00F3n")
	String liquiMsg1();
	
	@DefaultMessage("Importe generado. Pendiente de aplicaci\u00F3n a principio del ejercicio")
	String liquiMsg2();
	
	@DefaultMessage("Por l\u00EDmite 16.5 y 83 LIS")
	String liquiMsg21();

	@DefaultMessage("Aplicado en esta liquidaci\u00F3n")
	String liquiMsg3();
	
	@DefaultMessage("Importe aplicado")
	String liquiMsg31();

	@DefaultMessage("Pendiente de aplicaci\u00F3n en periodos futuros")
	String liquiMsg4();
	
	@DefaultMessage("Conversi\u00F3n")
	String liquiMsg5();
	
	@DefaultMessage("Importar Fich. Ej. Ant.")
	String importLastYear();

	@DefaultMessage("Importar Fich. 2013")
	String import2013();

	@DefaultMessage("Fichero oficial del Ejercicio 2013")
	String officialFile2013();
	
	@DefaultMessage("Importar Datos Contables")
	String importAccounting();
	
	@DefaultMessage("Imp.Dat.Ctb.")
	String importAccountingAbr();

	@DefaultMessage("Guardar la declaraci\u00F3n inclu\u00EDda en el modelo importado.")
	String saveImportedModel();

	@DefaultMessage("Fichero")
	String file();
	
 	@DefaultMessage("Limpiar")
	String clean();
	
	@DefaultMessage("Importar")
	String importAction();
	
    @DefaultMessage("Se est\u00E1 enviando un archivo al servidor.\n Int\u00E9ntelo mas tarde.")
    String uploaderActiveUpload();

    @DefaultMessage("Este archivo ya fu\u00E9 enviado.")
    String uploaderAlreadyDone();

    @DefaultMessage("Parece que esta aplicaci\u00F3n est\u00E1 configurada para usar GAE-Blobstore.\nSin embargo el servidor ha retornado un error al crear la URL.\nRecuerda que para utilizar blobstore debes activar la opci\u00F3n de facturaci\u00F3n en GAE.")
    String uploaderBlobstoreError();

    @DefaultMessage("Seleccione un fichero para enviar ...")
    String uploaderBrowse();

    @DefaultMessage("S\u00F3lo est\u00E1 permitido enviar estos tipos de archivo:\n")
    String uploaderInvalidExtension();

    @DefaultMessage("Enviar")
    String uploaderSend();

    @DefaultMessage("El servidor ha enviado una respuesta incorrecta.\n Compruebe que la aplicaci\u00F3n en el servidor est\u00E1 bien configurada.")
    String uploaderServerError();
    
    @DefaultMessage("Unable to auto submit the form, it seems your browser has security issues with this feature.\n Developer Info: If you are using jsupload and you do not need cross-domain, try a version compiled with the standard linker?")
    String submitError();
    
    @DefaultMessage("Ha sido imposible conectar con el servidor de la aplicaci\u00F3n.")
    String uploaderServerUnavailable();

    @DefaultMessage("Se ha sobrepasado el tiempo de espera al enviar el archivo.\n Es posible que su navegador no env\u00EDe correctamente archivos,\n o quiz\u00E1s ocurri\u00F3 un error en el servidor\nPor favor int\u00E9ntelo mas tarde")
    String uploaderTimeout();
    
    @DefaultMessage("Error uploading the file, the server response has a format which can not be parsed by the application.\n.")
    String uploaderBadServerResponse();

    @DefaultMessage("Additional information: it seems that you are using blobstore, so in order to upload large files check that your application is billing enabled.")
    String uploaderBlobstoreBilling();

    @DefaultMessage("Error you have typed an invalid file name, please select a valid one.")
    String uploaderInvalidPathError();
    
    @DefaultMessage("En espera")
    String uploadStatusQueued();
    
    @DefaultMessage("Enviando ...")
    String uploadStatusInProgress();
    
    @DefaultMessage("Finalizado")
    String uploadStatusSuccess();
    
    @DefaultMessage("Error")
    String uploadStatusError();
    
    @DefaultMessage("Cancelando ...")
    String uploadStatusCanceling();
    
    @DefaultMessage("Cancelado")
    String uploadStatusCanceled();
    
    @DefaultMessage("Borrado")
    String uploadStatusDeleted();

    @DefaultMessage("Enviando formulario ...")
    String uploadStatusSubmitting();
    
    @DefaultMessage("Cancelar")
    String uploadLabelCancel();
   
    //-------------------- D2 CUENTAS ANUALES - MEMORIA NORMALIZADA
    
 	@DefaultMessage("Memoria")
 	String memory();
     
 	@DefaultMessage("Memorias Predefinidas")
 	String digitalDepositFreeText();
 	
 	@DefaultMessage("Cuentas Anuales")
 	String digitalDeposit();
 	
 	@DefaultMessage("Apartado 3: Aplicaci\u00F3n de resultados")
 	String memory3_2Title();
 	
 	@DefaultMessage("Apartado 5: Inmovilizado material, intangible e inversiones inmobiliarias")
 	String memory5_2Title();
 	
 	@DefaultMessage("Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio actual")
 	String memory5_2Table1();
 	
 	@DefaultMessage("Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio anterior")
 	String memory5_2Table2();
 	
 	@DefaultMessage("Arrendamientos financieros y otras operaciones de naturaleza similar sobre activos no corrientes")
 	String memory5_2Table3();
 	
 	@DefaultMessage("Apartado 6: Activos financieros")
 	String memory6_2Title();
 	
 	@DefaultMessage("Activos financieros a largo plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.")
 	String memory6_2Table1();
 	
 	@DefaultMessage("Activos financieros a corto plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.")
 	String memory6_2Table2();
 	
 	@DefaultMessage("Traspasos o reclasificaciones de activos financieros")
 	String memory6_2Table3();
 	
 	@DefaultMessage("Correcciones por deterioro del valor originadas por el riesgo de cr\u00e9dito")
 	String memory6_2Table4();
 	
 	@DefaultMessage("Correcciones por deterioro del valor originadas por el riesgo de cr\u00e9dito")
 	String memory6_2Table5();
 	
 	@DefaultMessage("Correcciones valorativas por deterioro registradas en las distintas participaciones")
 	String memory6_2Table6();
 	
 	@DefaultMessage("Apartado 7: Pasivos Financieros")
 	String memory7_2Title();
 	
 	@DefaultMessage("Pasivos financieros a largo plazo")
 	String memory7_2Table1();
 	
 	@DefaultMessage("Pasivos financieros a corto plazos")
 	String memory7_2Table2();

 	@DefaultMessage("Vencimiento de las deudas al cierre del ejercicio 2014")
 	String memory7_2Table3();

 	@DefaultMessage("Lineas de descuento y p\u00f3lizas al cierre del ejercicio 2014")
 	String memory7_2Table4();

 	@DefaultMessage("Deudas con entidades de cr\u00e9dito")
 	String memory7_2Header1();

 	@DefaultMessage("Obligaciones y otros valores negociables")
 	String memory7_2Header2();

 	@DefaultMessage("Derivados y otros")
 	String memory7_2Header3();

 	@DefaultMessage("Uno")
 	String memory7_2One();

 	@DefaultMessage("Dos")
 	String memory7_2Two();

 	@DefaultMessage("Tres")
 	String memory7_2Three();

 	@DefaultMessage("Cuatro")
 	String memory7_2Four();

 	@DefaultMessage("Cinco")
 	String memory7_2Five();

 	@DefaultMessage("M\u00e1s de 5")
 	String memory7_2MoreFive();

 	@DefaultMessage("L\u00edmite concedido")
 	String memory7_2Header4();

 	@DefaultMessage("Dispuesto")
 	String memory7_2Header5();

 	@DefaultMessage("Disponible")
 	String memory7_2Header6();
 	
 	@DefaultMessage("Apartado 10: Ingresos y gastos")
 	String memory10_2Title();
 	
 	@DefaultMessage("Detalle de la cuenta de p\u00e9rdidas y ganancias")
 	String memory10_2Table();
 	
 	@DefaultMessage("Apartado 11: Subvenciones, donaciones y legados")
 	String memory11_2Title();
 	
 	@DefaultMessage("Subvenciones, donaciones y legados recibidos, otorgados por terceros distintos de los socios.")
 	String memory11_2Table1();
 	
 	@DefaultMessage("Subvenciones, donaciones y legados recogidos en el patrimonio neto del balance, otorgados por terceros distintos a los socios: an\u00E1lisis del movimiento")
 	String memory11_2Table2();
 	
 	@DefaultMessage("Entidad Dominante")
 	String memory12_2Header1();
 	
 	@DefaultMessage("Otras empresas del grupo")
 	String memory12_2Header2();
 	
 	@DefaultMessage("Empresas dependientes")
 	String memory12_2Header2_2016();
 	
 	@DefaultMessage("Negocios conjuntos en los que la empresa sea uno de los participantes")
 	String memory12_2Header3();
 	
 	@DefaultMessage("Empresas Asociadas")
 	String memory12_2Header4();
 	
 	@DefaultMessage("Empresas con control conjunto o influencia significativa sobre la empresa")
 	String memory12_2Header5();
 	
 	@DefaultMessage("Personal clave de la direcci\u00F3n de la empresa o de la entidad dominante")
 	String memory12_2Header6();
 	
 	@DefaultMessage("Miembros de los \u00F3rganos de administraci\u00F3n y personal clave de la direcci\u00F3n de la empresa")
 	String memory12_2Header6_2016();
 	
 	@DefaultMessage("Otras partes vinculadas")
 	String memory12_2Header7();
 	
 	@DefaultMessage("Apartado 12.1")
 	String memory12_2Title1();
 	
 	@DefaultMessage("Apartado 12.2")
 	String memory12_2Title2();
 	
 	@DefaultMessage("Apartado 12.3")
 	String memory12_2Title3();
 	
 	@DefaultMessage("Apartado 12.4")
 	String memory12_2Title4();
 	
 	@DefaultMessage("Apartado 12.5")
 	String memory12_2Title5();

 	
 	@DefaultMessage("Apartado 12: Operaciones con partes vinculadas")
 	String memory12_2Title();
 	
 	@DefaultMessage("Operaciones con partes vinculadas en el ejercicio 2014")
 	String memory12_2Table1();
 	
 	@DefaultMessage("Operaciones con partes vinculadas en el ejercicio 2013")
 	String memory12_2Table2();
 	
 	@DefaultMessage("Operaciones con partes vinculadas en el ejercicio {0}")
 	String memory12_1_2X(Integer year);
 	
 	@DefaultMessage("Saldos pendientes con partes vinculadas en el ejercicio 2014")
 	String memory12_2Table3();
 	
 	@DefaultMessage("Saldos pendientes con partes vinculadas en el ejercicio 2013")
 	String memory12_2Table4();
 	
 	@DefaultMessage("Saldos pendientes con partes vinculadas en el ejercicio {0}")
 	String memory12_3_4X(Integer year);
 	
 	@DefaultMessage("Importes recibidos por el personal de alta direcci\u00f3n")
 	String memory12_2Table5();
 	
 	@DefaultMessage("Importes recibidos por los miembros de los \u00f3rganos de administraci\u00F3n")
 	String memory12_2Table6();
 	
 	@DefaultMessage("Apartado 13: Otra Informaci\u00f3n")
 	String memory13_2Title();
 	
 	@DefaultMessage("N\u00famero medio de personas empleadas en el curso del ejercicio, por categor\u00EDas (adaptadas a la CNO-11)")
 	String memory13_2Table();
 	
 	@DefaultMessage("Apartado 14: Informaci\u00f3n sobre el medio ambiente")
 	String memory14_2Title1();
 	
 	@DefaultMessage("Apartado 14: Informaci\u00f3n sobre derechos de emisi\u00f3n de gases de efecto invernadero")
 	String memory14_2Title2();
 	
 	@DefaultMessage("DESCRIPCI\u00D3N DEL CONCEPTO")
 	String memory14_2Table1();
 	
 	@DefaultMessage("Movimiento durante el ejercicio")
 	String memory14_2Table2();
 	
 	@DefaultMessage("DERECHOS DE EMISI\u00D3N DE GASES DE EFECTO INVERNADERO")
 	String memory14_2Table2header1();
 	
 	@DefaultMessage("CONCEPTO")
 	String memory14_2Table2header2();
 	
 	@DefaultMessage("A) ACTIVOS DE NATURALEZA MEDIOAMBIENTAL")
 	String memory14_2Row1();
 	
 	@DefaultMessage("3.Correcciones valorativas por deterioro")
 	String memory14_2Row2();
 	
 	@DefaultMessage("C)Riesgos cubiertos por las provisiones para actuaciones medioambientales")
 	String memory14_2Row3();
 	
 	@DefaultMessage("1.Provisi\u00F3n para actuaciones medioambientales, inclu\u00EDdas en provisiones")
 	String memory14_2Row4();
 	
 	@DefaultMessage("Otra Informaci\u00f3n")
 	String memory14_2Table3();
 	
 	@DefaultMessage("Apartado 15: Informaci\u00f3n sobre los aplazamientos de pago efectuados a proveedores")
 	String memory15_2Title();
 	
 	@DefaultMessage("%")
 	String memory15_2Header1();
 	
 	@DefaultMessage("Pagos realizados y pendientes de pago en la fecha de cierre del Balance")
 	String memory15_2Table();
 	
 	@DefaultMessage("PAGOS DEL EJERCICIO")
 	String memory15_2Row1();
 	
 	@DefaultMessage("Datos generales de identificaci\u00f3n")
 	String header1Title();
 	
 	@DefaultMessage("Identificaci\u00f3n de la empresa")
 	String header1Table1();
 	
 	@DefaultMessage("Actividad")
 	String header1Table2();
 	
 	@DefaultMessage("Personal asalariado")
 	String header1Table3();
 	
 	@DefaultMessage("Presentaci\u00f3n de Cuentas")
 	String header1Table4();
 	
 	@DefaultMessage("Unidades")
 	String header1Table5();
 	
 	@DefaultMessage("Balance de situaci\u00f3n")
 	String header2Title();
 	
 	@DefaultMessage("Cuenta de p\u00E9rdidas y ganancias")
 	String header3Title();
 	
 	@DefaultMessage("Declaraci\u00f3n medioambiental")
 	String header5Title();

 	@DefaultMessage("Modelo de autocartera")
	String footer1Title();

 	@DefaultMessage("P\u00E1gina A1")
	String autocarteraModelA1();

 	@DefaultMessage("P\u00E1gina A1.1")
	String autocarteraModelA11();

 	@DefaultMessage("P\u00E1gina A2")
	String autocarteraModelA2();

 	@DefaultMessage("P\u00E1gina A3")
	String autocarteraModelA3();

 	@DefaultMessage("P\u00E1gina A4")
	String autocarteraModelA4();

 	@DefaultMessage("P\u00E1gina A5")
	String autocarteraModelA5();

 	@DefaultMessage("P\u00E1gina A6")
	String autocarteraModelA6();

 	@DefaultMessage("P\u00E1gina A7")
	String autocarteraModelA7();

	@DefaultMessage("La sociedad no ha realizado durante el presente ejercicio operaci\u00f3n alguna sobre acciones / participaciones propias")
	String footer1Label1();
 	
 	@DefaultMessage("Saldo al cierre del ejercicio precedente")
	String footer1Label2();
	
	@DefaultMessage("Acciones / participaciones")
	String footer1Label3();
	
	@DefaultMessage("Saldo al cierre del ejercicio")
	String footer1Label4();

	@DefaultMessage("Transcripci\u00f3n de acuerdos de Juntas generales, del \u00faltimo o anteriores ejercicios, autorizando negocios sobre acciones o participaciones propias realizados en el \u00faltimo ejercicio cerrado.")
	String footer1Label5();
	
 	@DefaultMessage("Relaci\u00f3n de acciones o participaciones adquiridas al amparo de los art\u00edculos 140, 144 y 146 de la Ley de SOciedades de Capital, durante el ejercicio.")
	String footer1Label6();
	
	@DefaultMessage("Relaci\u00f3n de acciones o participaciones adquiridas por los mismo t\u00edtulos, enajenadas o amortizadas durante el presente ejercicio")
	String footer1Label7();
 	
	@DefaultMessage("Negocios que han implicado la aceptaci\u00f3n en garant\u00eda de acciones propias, con las excepciones legales (art\u00edculo 149 de la Ley de Sociedades de Capital).")
	String footer1Label8();
	
	@DefaultMessage("Negocios que han implicado la asistencia finanaciera para la adquisicin de acciones propias salvo las excepciones legales (art\u00edculo 150 de la Ley de Sociedades de Capital).")
	String footer1Label9();
	
 	@DefaultMessage("Supuestos de infracci\u00f3n de las normas sobre participaciones rec\u00edprocas de capital (art\u00edculo 151 y siguiente de la Ley de Sociedades de Capital).")
	String footer1Label10();
 	
 	@DefaultMessage("Instancia de Presentaci\u00f3n")
	String footer2Title();

	@DefaultMessage("SOLICITUD DE PRESENTACI\u00d3N EN EL REGISTRO MERCANTIL DE ")
	String footer2Label1();

	@DefaultMessage("IDENTIFICACI\u00d3N DE LA ENTIDAD QUE PRESENTA LAS CUENTAS A DEP\u00d3SITO")
	String footer2Label2();
	
	@DefaultMessage("Denominaci\u00f3n de la entidad")
	String footer2Label3();
	
	@DefaultMessage("Tomo")
	String footer2Label4();
	
	@DefaultMessage("Folio")
	String footer2Label5();
	
	@DefaultMessage("N\u00BA Hoja registral")
	String footer2Label6();
	
	@DefaultMessage("Fecha de cierre ejercicio social")
	String footer2Label7();
	
	@DefaultMessage("IDENTIFICACI\u00d3N DE LOS DOCUMENTOS CONTABLES CUYO DEP\u00d3SITO SE SOLICITA")
	String footer2Label8();
	
	@DefaultMessage("Estado cambios patrimonio neto")
	String footer2Label9();
	
	@DefaultMessage("Estado de Flujos de efectivo")
	String footer2Label10();
	
	@DefaultMessage("Hoja de Identificaci\u00f3n de la sociedad")
	String footer2Label11();
	
	@DefaultMessage("Declaraci\u00f3n medioambiental")
	String footer2Label12();
	
	@DefaultMessage("Informe de gestion")
	String footer2Label13();
	
	@DefaultMessage("Informe de Auditor\u00eda")
	String footer2Label14();

	@DefaultMessage("Anuncios de convocatoria")
	String footer2Label15();
	
	@DefaultMessage("Certificado SICAV")
	String footer2Label16();
	
	@DefaultMessage("Certificaci\u00f3n acuerdo")
	String footer2Label17();
	
	@DefaultMessage("Otros Documentos")
	String footer2Label18();
	
	@DefaultMessage("N\u00BA")
	String footer2Label19();
	
	@DefaultMessage("IDENTIFICACI\u00d3N DEL PRESENTANTE QUE HACE LA SOLICITUD")
	String footer2Label20();
 	
	@DefaultMessage("DNI")
	String dni();
	
	@DefaultMessage("Nombre y Apellidos")
	String nameAndSurname();
	
	@DefaultMessage("Domicilio")
	String domicilio();
	
	@DefaultMessage("Ciudad")
	String ciudad();

	@DefaultMessage("Correo electronico")
	String mail();
	
	@DefaultMessage("Normal")
	String normal();
	
	@DefaultMessage("Abreviado")
	String abreviate();
	
	@DefaultMessage("PYME")
	String pyme();
	
 	@DefaultMessage("Certificaci\u00f3n de huella digital")
 	String footer3Title();
 	
 	@DefaultMessage("Nombre de las personas que expiden la certificaci\u00f3n")
 	String footer3Label();

 	@DefaultMessage("Notas de la memoria")
 	String memoryNotes();
 	 	
 	@DefaultMessage("(Debe)/ Haber")
	String debitCredit();

 	@DefaultMessage("Debe")
	String debit();

 	@DefaultMessage("Haber")
	String credit();

 	@DefaultMessage("Contrapartida")
	String balancingAccount();

 	//-------------------- AON GWT TEMPLATES - Consumption
 	
 	@DefaultMessage("Limpiar")
	String cleanTemplates();
 	
 	@DefaultMessage("Informe Agregado de Control de Consumos")
	String aggregateConsumptionTemplates();
	
 	@DefaultMessage("Control de Consumos")
	String consumptionTemplates();
 	
 	
 	@DefaultMessage("Informe Agregado de Control de Errores en Recuentos")
	String aggregateCountErrorTemplates();
 	
 	@DefaultMessage("Control de Errores en Recuentos")
	String countErrorTemplates();
 	
 	@DefaultMessage("B\u00FAsqueda")
	String searchTemplates();
 	
 	@DefaultMessage("Introduzca los par\u00E1metros de b\u00FAsqueda:")
	String search2Templates();
 	
 	@DefaultMessage("Almac\u00E9n")
	String warehouseTemplates();
 	
 	@DefaultMessage("Hotel")
	String hotelTemplates();
 	
 	@DefaultMessage("Seleccionados")
	String selectedTemplates();
 	
 	@DefaultMessage("Listado")
	String listTemplates();
 	
 	@DefaultMessage("Asientos contables")
	String accountEntries();
 	
 	@DefaultMessage("Cuenta contable")
 	String account();
 	
 	@DefaultMessage("Cta. Ctb.")
 	String accountAbr();
 	
 	@DefaultMessage("Cuenta Contable no encontrada")
 	String accountNotFound();

 	@DefaultMessage("Error inesperado: [{0}]")
 	String unexpectedError(String msg);
 
    @DefaultMessage("Existen {0} asientos de n\u00F3minas en el periodo seleccionado. [VER]")
    @AlternateMessage({"=1", "Existe un asiento de n\u00F3minas en el periodo seleccionado. [VER]"})
    String salaryEntryErrorMsg(@PluralCount int count);
    
    @DefaultMessage("No se pudo comprobar la existencia de asientos")
    String accountEntryReadError();
    
    @DefaultMessage("Error al recuperar las cuentas bancarias de la empresa.")
    String registryBankReadError();

	@DefaultMessage("\u00BFDesea continuar con la generaci\u00F3n de asientos?")
	String generateAccountEntry();
	
	@DefaultMessage("\u00BFDesea cambiar el concepto en todas las l\u00EDneas del asiento?")
	String changeConcept();

	@DefaultMessage("\u00BFDesea cambiar el n\u00FAmero de documento en todas las l\u00EDneas del asiento?")
	String changeDocument();

	@DefaultMessage("Alg\u00FAn dato de la l\u00EDnea, se ha modificado manualmente. \u00BFDesea recalcular la l\u00EDnea?")
	String manualChangeConfirm();

	@DefaultMessage("Asientos generados")
	String generatedAccountEntries();

	@DefaultMessage("No se gener\u00F3 ning\u00FAn asiento.")
	String noGeneratedAccountEntries();

	@DefaultMessage("Fecha de asiento")
	String accountEntryDate();
	
	@DefaultMessage("N\u00BA de diario")
	String journal();
	
	@DefaultMessage("Libro diario")
	String journalBook();

    @DefaultMessage("----------")
	@AlternateMessage({
		"OPENING", "Apertura",
		"CLOSING", "Cierre",
	    "OPERATING", "Explotaci\u00F3n",
	    "MANUAL", "Manual",
	    "SALES_INVOICE", "Factura de Venta",
	    "PURCHASE_INVOICE", "Factura de Compra",
	    "EXPENSE_INVOICE", "Factura de Gastos",
	    "INVESTMENT_INVOICE", "Factura de Inversi\u00F3n",
	    "EXPENSES", "Gastos sin IVA",
	    "SALARY", "N\u00F3minas",
	    "TAX", "Impuestos",
	    "LOAN", "Pr\u00E9stamos",
	    "LEASING", "Leasing",
	    "PAYMENT", "Pago",
	    "COLLECTION", "Cobro",
	    "STOCK_VARIATION", "Variaci\u00F3n de Existencias",
	    "AMORTIZATION", "Amortizaci\u00F3n",
	    "SOCIAL_INSURANCE", "Seg. Social",
	    "LOAN_FEE", "Cuotas Pr\u00E9stamos",
	    "LEASING_FEE", "Cuotas Leasing",
	    "RETURNED_PAYMENT", "Devoluci\u00F3n de Pago",
	    "RETURNED_COLLECTION", "Devoluci\u00F3n de Cobro",
	    "SOCIAL_INSURANCE_ADJUST", "Ajuste Seg. Social",
	    "FINANCE", "Tesorer\u00EDa"})
    String accountEntryType(@Select AccountEntryType type);

    @DefaultMessage("Tipo asiento")
    String accountEntryTypeLabel();

    @DefaultMessage("----------")
	@AlternateMessage({
		 "ACTIVE","Activo"
		,"INACTIVE","Inactivo"
		,"OPENING","Apertura"
		,"OPERATING","Explotaci\u00F3n"
		,"CLOSED","Cerrado"})
    String accountPeriodStatus(@Select AccountPeriodStatus status);

    @DefaultMessage("----------")
	@AlternateMessage({
		"GROUP1", "Actividades empresariales sujetas al I.A.E.",
		"GROUP2", "Actividades profesionales sujetas al I.A.E.",
		"GROUP3", "Actividades art\u00EDsticas sujetas al I.A.E.",
		"GROUP4", "Arrendadores de locales de negocios",
		"GROUP5", "wo agr\u00EDcolas, ganaderas o pesqueras, no sujetas al I.A.E.",
		"GROUP6", "Otras actividades no sujetas al I.A.E.", 
		"GROUP7", "Sujetos pasivos sin actividad" })
	String activityGroup(@Select ActivityGroup activityGroup);

 	@DefaultMessage("Hist\u00F3rico de sesi\u00F3n")
	String sessionLog();

 	@DefaultMessage("Extracto de cuenta")
	String accountStatetement();

 	@DefaultMessage("Prev. y saldos del asiento")
	String accountBalances();
 	
 	@DefaultMessage("No ha sido posible encontrar el asiento. Puede que el asiento haya sido borrado.\n \u00BFDesea recurperarlo de todas formas?")
	String recoverEntry();
 	
 	@DefaultMessage("No ha sido posible encontrar un ejercicio contable activo.")
	String noActiveAccountPeriod();
 	
 	@DefaultMessage("No definido")
	String undefined();
	
 	@DefaultMessage("Facturaci\u00F3n anual")
	String yearInvoicing();
	
 	@DefaultMessage("Facturaci\u00F3n mensual")
	String monthInvoicing();

 	@DefaultMessage("Categor\u00EDas de productos")
	String productCategories();
 	
 	@DefaultMessage("Categor\u00EDa")
	String category();
 	
 	@DefaultMessage("Categor\u00EDas")
	String categories();

 	@DefaultMessage("IVA deducible en operaciones interiores")
	String internOpVatDeduction();
 	
 	@DefaultMessage("IVA deducible en operaciones importaciones")
	String importVatDeduction();
 	
 	@DefaultMessage("IVA deducible en adquisiciones intracomunitarias")
	String intracommunityAdqVatDeduction();
 	
 	@DefaultMessage("Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca")
	String agricultureCompensation();
 	
 	@DefaultMessage("Rectificaci\u00F3n de deducciones")
	String deductionRectification();
	
 	@DefaultMessage("Regularizaci\u00F3n de bienes de inversi\u00F3n")
	String investAssetRegularization();

 	@DefaultMessage("Suma de deducciones")
	String deductionSum();
	
	@DefaultMessage("Bienes y servicios corriente")
	String commonAsset();

	@DefaultMessage("Bienes de inversi\u00F3n")
	String investAsset();

	@DefaultMessage("Inversi\u00F3n")
	String investment();

	@DefaultMessage("Bien afecto")
	String actInvestAsset();

	@DefaultMessage("Nueva declaraci\u00F3n")
	String newDeclaration();

	@DefaultMessage("Detalle de gastos")
	String expenseDetail();
	
	@DefaultMessage("Gastos de personal")
	String staffExpenses();
	
	@DefaultMessage("Adquisici\u00F3n a terceros de bienes y servicios")
	String assetAcquisition();
	
	@DefaultMessage("Tributos fiscalmente deducibles y gastos financieros")
	String taxDeduction();
	
	@DefaultMessage("Otros gastos fiscalmente deducibles")
	String otherTaxDeduction();
	
	@DefaultMessage("Acreedor")
	String creditor();

	@DefaultMessage("Acreedor no encontrado")
	String creditorNotFound();

	@DefaultMessage("Si el contribuyente es discapacitado en grado igual o superior al 33 por 100, marque X")
	String irpfActivityDis();

	@DefaultMessage("Comunidad, Sociedad civil o similar: porcentaje de participaci\u00F3n")
	String irpfActivityCom();

	@DefaultMessage("Actividad de temporada: n\u00BA de d\u00EDas de ejercicio en el a\u00F1o anterior")
	String irpfActivityTem();

	@DefaultMessage("Nuevas actividades: A\u00F1o de inicio")
	String irpfActivityNue();

	@DefaultMessage("\u00BFDeducci\u00F3n por rentas obtenidas en Ceuta y Melilla?")
	String irpfActivityCeu();

	@DefaultMessage("\u00BFEjerce la actividad en un solo local o sin \u00E9l?")
	String irpfActivityLoc();

	@DefaultMessage("Indique el n\u00FAmero de veh\u00EDculos afectos a la actividad")
	String irpfActivityVeh();

	@DefaultMessage("\u00BFLa capacidad de carga del veh\u00EDculo es superior a 1000 Kg.?")
	String irpfActivityCap();
	
	@DefaultMessage("\u00BFLa actividad se realiza con tractocamiones y el titular carece de semirremolques?")
	String irpfActivityTns();
	
	@DefaultMessage("\u00BFLa actividad se realiza con un \u00FAnico tractocami\u00F3n y sin semirremolques?")
	String irpfActivityTss();

	@DefaultMessage("Municipio donde se ejerce la actividad:")
	String irpfActivityMun();

	@DefaultMessage("N\u00BA de empleados al inicio del ejercicio (o en la fecha de inicio de la actividad)")
	String irpfActivityEmp();

	@DefaultMessage("Si realiza la actividad en LORCA, seleccione lo que proceda")
	String irpfActivityLor();

	@DefaultMessage("N\u00BA de bateas y de barcos auxiliares de la empresa")
	String irpfActivityBat();

	@DefaultMessage("Si para el c\u00E1lculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar")
	String irpfActivityPrc();

	@DefaultMessage("Rendimiento neto previo")
	String irpfActivityRnp();

	@DefaultMessage("Incentivos al empleo")
	String irpfActivityIem();

	@DefaultMessage("Incentivos a la inversi\u00F3n")
	String irpfActivityIin();

	@DefaultMessage("Rendimiento neto minorado")
	String irpfActivityRnm();

	@DefaultMessage("1. \u00CDndice Corrector. Especiales")
	String irpfActivityIc1();

	@DefaultMessage("2. \u00CDndice Corrector. Empresas de peque\u00F1a dimensi\u00F3n")
	String irpfActivityIc2();

	@DefaultMessage("3. \u00CDndice Corrector. De temporada")
	String irpfActivityIc3();

	@DefaultMessage("4. \u00CDndice Corrector. De exceso")
	String irpfActivityIc4();

	@DefaultMessage("5. \u00CDndice Corrector. De inicio de nueva actividad")
	String irpfActivityIc5();

	@DefaultMessage("Rendimiento a efectos de pagos fraccionados")
	String irpfActivityRpf();

	@DefaultMessage("Reducci\u00F3n para actividades econ\u00F3micas realizadas en el t\u00E9rmino municipal de Lorca")
	String irpfActivityRlo();

	@DefaultMessage("Rendimiento a efectos de pagos fraccionados despu\u00E9s de la reducci\u00F3n")
	String irpfActivityRdr();

	@DefaultMessage("D\u00EDas de ejercicio de la actividad")
	String irpfActivityDia();

	@DefaultMessage("D\u00EDas de ejercicio de la actividad en el trimestre")
	String irpfActivityDiaTrim();

	@DefaultMessage("Rendimiento neto de la actividad a efectos del pago fraccionado")
	String irpfActivityNet();

	@DefaultMessage("Porcentaje aplicable")
	String irpfActivityPor();

	@DefaultMessage("Resultado de aplicar el porcentaje correspondiente a cada actividad")
	String irpfActivityRes();
	
	@DefaultMessage("Borrar todos los filtros")
	String statFilterDelete();

	@DefaultMessage("D\u00EDas")
	String days();

	@DefaultMessage("Nueva Cuenta")
	String newAccount();

	@DefaultMessage("Contabilizar pago")
	String recordPayment();
	
	@DefaultMessage("Contabilizar")
	String record();

	@DefaultMessage("Vencimiento")
	String finance();

	@DefaultMessage("Vto.")
	String financeAbbr();

	@DefaultMessage("Rectificativas")
	String rectified();
	
	@DefaultMessage("Crear factura rectificativa")
	String rectifyInvoice();
	
	@DefaultMessage("Fecha factura rectificativa")
	String rectifyInvoiceDate();

	@DefaultMessage("Motivo")
	String reason();

	@DefaultMessage("Saldar vencimientos pendientes")
	String settleFinances();
	
	@DefaultMessage("No se ha encontrado la factura.")
	String invoiceNotFound();
	
	@DefaultMessage("Ver fact. rect.")
	String seeRectInvoiceAbbr();

	@DefaultMessage("Manual")
	String manual();
	
	@DefaultMessage("N\u00F3mina")
	String salary();

	@DefaultMessage("Remuneraciones monetaria")
	String moneySalary();
	
	@DefaultMessage("Remuneraciones en especie")
	String inKindSalary();
	
	@DefaultMessage("Dietas")
	String allowances();

	@DefaultMessage("Indemnizaciones")
	String salaryCompensations();

	@DefaultMessage("Total devengado")
	String totalAccrued();

	@DefaultMessage("Seguridad Social empleado")
	String employeeSocialInsurance();

	@DefaultMessage("Seguridad Social empresa")
	String companySocialInsurance();

	@DefaultMessage("Salario neto")
	String netSalary();

	@DefaultMessage("Anticipo")
	String advance();

	@DefaultMessage("Embargo")
	String seize();
	
	@DefaultMessage("Otras deducciones.")
	String salaryOtherDeductions();
	
	@DefaultMessage("Importar desde n\u00F3minas")
	String importSalaryAction();
	
	@DefaultMessage("Comercial")
	String seller();
	
	@DefaultMessage("Cliente")
	String customer();
	
	@DefaultMessage("Empresa de Transporte")
	String carrier();
	
	@DefaultMessage("Tesorer\u00EDa")
	String treasury();

	@DefaultMessage("Cant. cercanas")
	String nearbyNumbers();

	@DefaultMessage("Selecci\u00F3n de cobros / pagos")
	String financeSelection();

	@DefaultMessage("Cobros / pagos seleccionados")
	String financeSelected();

	@DefaultMessage("El Vencimiento tiene movimientos posteriores")
	String noLastTracking();
	
	@DefaultMessage("El Vencimiento est\u00E1 vinculado a una remesa")
	String fromFBatch();

	@DefaultMessage("No se puede modificar")
	String unableToUpdate();
	
	@DefaultMessage("Criterio de cobros y pagos")
	String vatAccrualPayment184();

	@DefaultMessage("Prov. ded. y gastos de dif. jus.")
	String expenses184();

	@DefaultMessage("Clave del declarado")
	String declaredKey();
	
	@DefaultMessage("Porc. Titularidad del inmueble")
	String assetPercent();

	@DefaultMessage("Existen vencimientos pagados/remesados/saldados.")
	String hasPaidFinances();

	@DefaultMessage("Factura creada desde \"gesti\u00F3n\"")
	String managmentInvoice();
	
	@DefaultMessage("El valor no coincide con el c\u00E1lculo. Deber\u00EDa ser: {0,number}")
	String editedValue(double value);
	
	@DefaultMessage("El valor no coincide con el c\u00E1lculo y supera el margen de aviso. Deber\u00EDa ser: {0,number} ( diferencia {1,number} )")
	String editedValueWarning(double value,double gap);
	
	@DefaultMessage("Existen {0,number} registros con ese documento.")
	@AlternateMessage({"=1", "Existe un registro con ese documento"})
	String existingRegistryWarning(@PluralCount int registryCount);

	@DefaultMessage("Invertir saldos entre debe y haber")
	String invertData();
	
	@DefaultMessage("Aviso: Se generar\u00E1 un apunte manual")
	String manualEntryGeneration();

	@DefaultMessage("El ejericio de la declaraci\u00F3n no est\u00E1 soportado")
	String noModuleForYear();
	
	@DefaultMessage("A) Porcentaje de imputaci\u00F3n de bases imponibles y dem\u00E1s conceptos liquidatorios")
	String ute1();
	
	@DefaultMessage("B) Modelo de informaci\u00F3n (art\u00EDculo 46 RIS)")
	String ute2();
	
	@DefaultMessage("6.- Deducci\u00F3n para evitar la doble imposici\u00F3n:")
	String ute31();

	@DefaultMessage("8.- Base de las deducciones para incentivar la realizaci\u00F3n de determinadas actividades:")
	String ute4();

	@DefaultMessage("10.- Dividendos y participaciones en benefi cios distribuidos con cargo a reservas:")
	String ute5();

	@DefaultMessage("11.- Relaci\u00F3n de socios existentes a la fecha de cierre del per\u00EDodo impositivo, en orden decreciente de grado de participaci\u00F3n, con sus datos identifi cativos y grado de participaci\u00F3n en dicha fecha:")
	String ute6();

	@DefaultMessage("Informaci\u00F3n de detalle de EP o UTE que operen en el extranjero y por participaci\u00F3n en f\u00F3rmula de colaboraci\u00F3n an\u00E1loga a UTE")
	String utefor();

	@DefaultMessage("Pa\u00EDs de residencia fiscal")
	String utefor1();

	@DefaultMessage("Volumen de operaciones")
	String utefor2();
	
	@DefaultMessage("Beneficio o p\u00E9rdida que obtienen en el per\u00EDodo impositivo")
	String utefor3();
	
	@DefaultMessage("Suma de ajustes al resultado contable desde el inicio de la actividad")
	String utefor4();
	
	@DefaultMessage("Suma de deducciones por DI internac (art. 31 LIS) acreditada en periodos anteriores")
	String utefor5();
	
	@DefaultMessage("Comunicaci\u00F3n del importe neto de la cifra de negocios")
	String bussinessAmount();

	@DefaultMessage("Grupos de sociedades, art. 42 c\u00F3digo de comercio, incluidas entidades de cr\u00E9dito y aseguradoras")
	String bussinessAmount1();
	
	@DefaultMessage("No residentes con m\u00E1s de un establecimiento permanente")
	String bussinessAmount2();
	
	@DefaultMessage("Entidades de cr\u00E9dito, aseguradoras e instituciones de inversi\u00F3n colectiva")
	String bussinessAmount3();
	
	@DefaultMessage("NIF de las entidades del grupo")
	String bussinessAmount11();

	@DefaultMessage("NIF de los establecimientos permanentes en caso de entidad titular")
	String bussinessAmount12();
	
	@DefaultMessage("Importe de la dotaci\u00F3n")
	String canCol1();
	
	@DefaultMessage("Inversiones previstas letras A, B, B bis y D (1o) art. 27.4 Ley 19/1994")
	String canCol2();
	
	@DefaultMessage("Inversiones previstas letras C y D (2o a 6o) art. 27.4 Ley 19/1994")
	String canCol3();
	
	@DefaultMessage("Pendiente de materializar")
	String canCol4();

	@DefaultMessage("Importar declaraciones antiguas")
	String transferModels();
	

}

