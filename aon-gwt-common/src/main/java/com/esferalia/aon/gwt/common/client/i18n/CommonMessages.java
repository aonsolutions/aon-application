package com.esferalia.aon.gwt.common.client.i18n;

import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.google.gwt.i18n.client.Messages;

public interface CommonMessages extends Messages {
	// ¡ --> \u00C1 · --> \u00E1
	// … --> \u00C9 È --> \u00E9
	// Õ --> \u00CD Ì --> \u00ED
	// ” --> \u00D3 Û --> \u00F3
	// ⁄ --> \u00DA ˙ --> \u00FA
	// — --> \u00D1 Ò --> \u00F1
	// ∫ --> \u00AA ™ --> \u00BA
	// ø --> \u00BF
	
	// ----------------------------------------------------------------- Format
	@DefaultMessage("#,##0")
	String integerPattern();

	@DefaultMessage("#,##0.00")
	String decimalPattern();

	@DefaultMessage("ESP")
	String currencyCode();

	// ---------------------------------------------------------------- Modulos
	@DefaultMessage("Modelo 190")
	String mod190();

	@DefaultMessage("Modelo 180")
	String mod180();

	@DefaultMessage("Modelo 390")
	String mod390();

	@DefaultMessage("Modelo 200")
	String mod200();

	@DefaultMessage("Impuesto sobre Sociedades.")
	String mod200Desc();

	@DefaultMessage("Impuesto sobre la Renta de no Residentes.")
	String mod200Desc2();

	@DefaultMessage("(establecimientos permanentes y entidades en r\u00E9gimen de atribuci\u00F3n de rentas constituidas en el extranjero con presencia en territorio espa\u00F1ol)")
	String mod200Desc3();

	// ---------------------------------------------------------------- Errores
	@DefaultMessage("Valor num\u00E9rico no correcto ({0}) ")
	String numericValueError(String value);

	@DefaultMessage("No se pudieron leer las declaraciones del modelo 190. Causa: \n {0}")
	String unableToReadMod190(String cause);

	@DefaultMessage("No se pudo guardar la declaraci\u00F3n del modelo 190. Causa: \n {0}")
	String unableToSaveMod190(String cause);

	@DefaultMessage("No se pudo borrar la declaraci\u00F3n del modelo 190. Causa: \n {0}")
	String unableToDeleteMod190(String message);

	@DefaultMessage("No se pudo encontrar la declaraci\u00F3n seleccionada.")
	String unableToFindMod190();

	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindMod190Detail(String field);

	@DefaultMessage("No se pudieron leer las declaraciones del modelo 180. Causa: \n {0}")
	String unableToReadMod180(String cause);

	@DefaultMessage("No se pudo guardar la declaraci\u00F3n del modelo 180. Causa: \n {0}")
	String unableToSaveMod180(String cause);

	@DefaultMessage("No se pudo borrar la declaraci\u00F3n del modelo 180. Causa: \n {0}")
	String unableToDeleteMod180(String message);

	@DefaultMessage("No se pudo encontrar la declaraci\u00F3n seleccionada.")
	String unableToFindMod180();

	@DefaultMessage("No se pudo encontrar el perceptor seleccionado. Causa: \n {0}")
	String unableToFindMod180Detail(String field);

	@DefaultMessage("No existe un perceptor con c\u00F3digo {0}")
	String unableToFindPerceptor(Integer id);

	@DefaultMessage("No se pueden leer los par\u00E1metros fiscales. {0}")
	String unableToReadFiscalParameters(String message);

	@DefaultMessage("El valor del ejercicio es incorrecto")
	String unableToParseYear();

	@DefaultMessage("El dato \"{0}\" es obligatorio")
	String requiredField(String field);

	// ---------------------------------------------------------Button Messages
	@DefaultMessage("Nuevo")
	String newAction();

	@DefaultMessage("Guardar")
	String saveAction();

	@DefaultMessage("Borrar")
	String deleteAction();

	@DefaultMessage("\u00BFContinuar con el borrado?")
	String confirmDeleteAction();

	@DefaultMessage("Si continua se borrar\u00E1 la declaraci\u00F3n completa. \u00BFContinuar con el borrado?")
	String confirmDeclarationDeleteAction();

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

	@DefaultMessage("C\u00F3digo")
	String code();

	@DefaultMessage("Generar fichero")
	String generateFile();

	@DefaultMessage("Imprimir via AEAT")
	String printViaAeat();

	@DefaultMessage("Error")
	String error();

	@DefaultMessage("Aviso")
	String warning();

	@DefaultMessage("Procesando su orden. Por favor, espere")
	String processing();

	@DefaultMessage("SI")
	String yes();

	@DefaultMessage("NO")
	String no();

	// ---------------------------------------------------------Common Messages
	@DefaultMessage("Desde")
	String from();

	@DefaultMessage("Hasta")
	String until();

	@DefaultMessage("Fecha")
	String date();

	@DefaultMessage("No se han encontrado datos")
	String noData();

	@DefaultMessage("{0} - {1} de {2}")
	String exactPagerData(int start, int end, int count);

	@DefaultMessage("{0} - {1} de \u00BF?")
	String unexactPagerData(int start, int end);

	@DefaultMessage("Empresa")
	String enterprise();

	@DefaultMessage("Administraci\u00F3n")
	String administration();

	@DefaultMessage("Ejercicio")
	String fiscalYear();

	@DefaultMessage("Sustitutiva")
	String replacement();

	@DefaultMessage("Num. Declaraci\u00F3n")
	String receipt();

	@DefaultMessage("N. Decl. Sustituida")
	String replacedReceipt();

	@DefaultMessage("Decl. complementaria")
	String complementary();

	@DefaultMessage("N. justificante anterior")
	String complementaryReceipt();

	@DefaultMessage("Confidencial")
	String confidential();

	@DefaultMessage("Identificaci\u00F3n")
	String identification();

	@DefaultMessage("N\u00FAmero")
	String number();

	@DefaultMessage("Documento")
	String document();

	@DefaultMessage("N.I.F.")
	String nif();

	@DefaultMessage("Nombre")
	String name();

	@DefaultMessage("Apellidos y Nombre, raz\u00F3n social o denominaci\u00F3n")
	String companyName();

	@DefaultMessage("Apellidos")
	String surname();

	@DefaultMessage("Comentarios")
	String comments();

	@DefaultMessage("Estado")
	String status();

	@DefaultMessage("Descripci\u00F3n")
	String description();

	@DefaultMessage("Tel\u00E9fono")
	String phone();

	@DefaultMessage("Tipo v\u00EDa")
	String streetType();

	@DefaultMessage("Nombre de la v\u00EDa p\u00FAblica")
	String streetName();

	@DefaultMessage("N\u00FAmero")
	String streetNumber();

	@DefaultMessage("Esc.")
	String streetStair();

	@DefaultMessage("Piso")
	String streetFloor();

	@DefaultMessage("Prta.")
	String streetDoor();

	@DefaultMessage("Municipio")
	String town();

	@DefaultMessage("Cod.Postal")
	String zip();

	@DefaultMessage("Base imponible")
	String taxableBase();

	@DefaultMessage("Cuota")
	String quota();

	@DefaultMessage("Total")
	String total();

	@DefaultMessage("Porcentaje")
	String percent();

	@DefaultMessage("Importe")
	String amount();

	@DefaultMessage("Actividad")
	String activity();

	@DefaultMessage("Actividades a las que se refiere la declaraci\u00F3n")
	String activities();

	@DefaultMessage("(de mayor a menor importacia por vol\u00FAmen de operaciones)")
	String activitiesNote();

	@DefaultMessage("Territorio Com\u00FAn")
	String commonTerritory();

	@DefaultMessage("\u00C1lava")
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

	// -------------------------------------------------------------- Model 190

	@DefaultMessage("Nuevo perceptor")
	String newPerceptor();

	@DefaultMessage("Datos del declarante")
	String deponentData();

	@DefaultMessage("Datos de la declaraci\u00F3n")
	String declarationData();

	@DefaultMessage("Persona y tel\u00E9fono de contacto")
	String contactData();

	@DefaultMessage("Persona de contacto")
	String contactPerson();

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

	@DefaultMessage("IVA DEVENGADO")
	String outputVat();

	@DefaultMessage("R\u00E9gimen Simplificado")
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

	@DefaultMessage("\u00CDndice corrector")
	String page6E();

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

	@DefaultMessage("Actividades agr\u00EDcolas, ganaderas y forestales")
	String farmerActivity();

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

	@DefaultMessage("Regularizaci\u00F3n de bienes de inversi\u00F3n")
	String box81Msg();

	@DefaultMessage("Suma de deducciones")
	String box82Msg();

	@DefaultMessage("Resultado del r\u00E9gimen simplificado")
	String box83Msg();

	@DefaultMessage("Resultado liquidaci\u00F3n anual")
	String annualLiquidationResult();

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

	@DefaultMessage("Operaciones en r\u00E9gimen general")
	String box99Msg();

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

	@DefaultMessage("Prorratas")
	String prorrata();

	@DefaultMessage("Actividades con reg\u00EDmenes de deducci\u00F3n diferenciados")
	String difActivitiesRegime();

	@DefaultMessage("Registro de devoluci\u00F3n mensual en alg\u00FAn per\u00EDodo del ejercicio")
	String taxRefund();

	@DefaultMessage("\u00BFLas declaraciones del \u00FAltimo periodo de liquidaci\u00F3n del ejercicio corresponden a declaraciones concursales?")
	String insolvencyDeclarations();

	@DefaultMessage("R\u00E9gimen especial del grupo de entidades en "
			+ "alg\u00FAn per\u00EDodo del ejercicio")
	String specialGroupRegime();

	@DefaultMessage("N\u00BA Grupo")
	String groupNumber();

	@DefaultMessage("Dependiente")
	String groupDependent();

	@DefaultMessage("Tipo r\u00E9gimen especial aplicable: Art. 163 sexies.cinco")
	String groupRegimeType();

	@DefaultMessage("NIF entidad dominante")
	String groupDocument();

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

	// -----------------------------------------------------------Enum Messages
	@DefaultMessage("----------")
	@AlternateMessage({ "ALAVA", "Araba/Alava", "BIZKAIA", "Bizkaia",
			"GIPUZKOA", "Gipuzkoa", "NAVARRA", "Navarra", "COMMON_TERRITORY",
			"Territorio Com\u00FAn" })
	String administrationName(@Select Administration administration);

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

	@DefaultMessage("Cuenta de p\u00E9rdidas y ganancias")
	String pyg();

	@DefaultMessage("ECPN. Estado de ingresos y gastos reconocidos en el ejercicio")
	String patrimonioIngresos();

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

	@DefaultMessage("Caracteres de la declaraci\u00F3n")
	String declarationCharacters();

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

	@DefaultMessage("Domicilio fiscal")
	String fiscalAddress();

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

	@DefaultMessage("c) Efecto de la correcci\u00F3n valorativa en la BI del ejercicio")
	String partMsg11();

	@DefaultMessage("d) Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio")
	String partMsg12();

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

	@DefaultMessage("Capital Escriturado")
	String ecpnMsg1();

	@DefaultMessage("Capital (No exigido)")
	String ecpnMsg2();

	@DefaultMessage("Prima de emisi\u00F3n")
	String ecpnMsg3();

	@DefaultMessage("Reservas")
	String ecpnMsg4();

	@DefaultMessage("(Acciones y partic. en patr. propias)")
	String ecpnMsg5();

	@DefaultMessage("Resultados de ejercicios anteriores")
	String ecpnMsg6();

	@DefaultMessage("Otras aportaciones de socios")
	String ecpnMsg7();

	@DefaultMessage("Resultado del ejercicio")
	String ecpnMsg8();

	@DefaultMessage("(Dividendo a cuenta)")
	String ecpnMsg9();

	@DefaultMessage("Otros instrumentos de patrimonio neto")
	String ecpnMsg10();

	@DefaultMessage("Ajustes por cambios de valor")
	String ecpnMsg11();

	@DefaultMessage("Ajustes en patrimonio neto")
	String ecpnMsg12();

	@DefaultMessage("Subv. donac. y legados recibidos")
	String ecpnMsg13();

	@DefaultMessage("TOTAL")
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
	@DefaultMessage("Liquidaci\u00F3n (I)")
	String liquidacionI();
	@DefaultMessage("Liquidaci\u00F3n (II)")
	String liquidacionII();
	@DefaultMessage("Liquidaci\u00F3n (III)")
	String liquidacionIII();
	@DefaultMessage("Liquidaci\u00F3n (IV)")
	String liquidacionIV();

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
	
	@DefaultMessage("Bonificaciones y deducciones por doble imposici\u00F3n. Cuota \u00EDntegra ajustada positiva")
	String bonus();
	
	@DefaultMessage("Otras deducciones. Cuota l\u00EDquida positiva")
	String otherDeductions();
	
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
	
	@DefaultMessage("Aplicaci\u00F3n de resultados")
	String incomeDistribution();
	
	@DefaultMessage("Limitaci\u00F3n en la deducibilidad de gastos financieros.")
	String deducibleLimitation();
	
	@DefaultMessage("Limitaci\u00F3n en la deducibilidad de gastos financieros. Art. 20 LIS")
	String deducibleLimitationArt();
	
	@DefaultMessage("Limitaci\u00F3n en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir")
	String deducibleLimitationPending();
	
	@DefaultMessage("Pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado")
	String pendingAddinngs();
	
	@DefaultMessage("Dotaciones por deterioro de cr\u00E9ditos u otros activos derivados de las posibles insolvencias de los deudores no vinculados con el sujeto pasivo (art. 19.13 lis) y conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la adm\u00F3n. tributaria (D.A. 22a LIS)")
	String damageAmount();
	
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
	
	@DefaultMessage("Deducci\u00F3n pendiente/generada")
	String pendingDeduction();
	
	@DefaultMessage("Tipo gravamen")
	String taxType();
	
	@DefaultMessage("2013 deducci\u00F3n pendiente")
	String pendingDeduction2013();
	
	@DefaultMessage("Deducci\u00F3n aplicada en esta liquidaci\u00F3n")
	String appliedDeduction();
	
	@DefaultMessage("Deducci\u00F3n pendiente per\u00EDodos futuros")
	String futureDeduction();
	
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional ejerc. anteriores")
	String doubleContributionInternationalPrevious();
	
	@DefaultMessage("Deducciones doble imposici\u00F3n internacional 2013")
	String doubleContributionInternational2013();
	
	@DefaultMessage("Deducci\u00F3n generada")
	String generatedDeduction();
	
	@DefaultMessage("Deducci\u00F3n reducida")
	String reducedDeduction();
	
	@DefaultMessage("Importe deducible en cuota")
	String quotableAmount();
	
	@DefaultMessage("Pendiente por insuficiencia de cuota")
	String pendingDueToQuota();
	
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
	
	@DefaultMessage("Exportar datos contables")
	String aeatAccountingFile();

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
	
	@DefaultMessage("Forma de pago")
	String paymentType();
	
	@DefaultMessage("En efectivo")
	String cash();
	
	@DefaultMessage("Domicialici√≥n")
	String directDebit();
	
	@DefaultMessage("I.B.A.N.")
	String iban();

	@DefaultMessage("Fecha I.R.N.R.")
	String irnrDate();

	
}
