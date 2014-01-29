package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.ActivityGroup;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Administration;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Province;
import com.google.gwt.i18n.client.Messages;

public interface FiscalMessages extends Messages {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

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

	@DefaultMessage("Restaurar")
	String restoreAction();

	@DefaultMessage("Buscar")
	String searchAction();

	@DefaultMessage("Cancelar")
	String cancelAction();

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

	@DefaultMessage("Confidencial")
	String confidential();

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

	@DefaultMessage("PERSONAS F\u00CDSICAS Y ENTIDADES SIN PERSONALIDAD JUR\u00CDDICA")
	String nonLegalEntities();
	
	@DefaultMessage("PERSONAS JUR\u00CDDICAS")
	String legalEntities();
	
	@DefaultMessage("Fecha poder")
	String registrationDate();
	
	@DefaultMessage("Notar\u00EDa")
	String notary();
	
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
	
	@DefaultMessage("Operaciones en R\u00E9gimen especial de bienes usados, objetos de arte, antigüedades y objetos de colecci\u00F3n")
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

	@DefaultMessage("----------")
	@AlternateMessage({
			"GROUP1",
			"Actividades empresariales sujetas al I.A.E.",
			"GROUP2",
			"Actividades profesionales sujetas al I.A.E.",
			"GROUP3",
			"Actividades art\u00EDsticas sujetas al I.A.E.",
			"GROUP4",
			"Arrendadores de locales de negocios",
			"GROUP5",
			"Actividades agr\u00EDcolas, ganaderas o pesqueras, no sujetas al I.A.E.",
			"GROUP6", "Otras actividades no sujetas al I.A.E.", "GROUP7",
			"Sujetos pasivos sin actividad" })
	String activityGroup(@Select ActivityGroup activityGroup);
	
	
	
	
	@DefaultMessage("----------")
	@AlternateMessage({
		 "K00_04","R\u00E9gimen ordinario"
		,"K00_08",""
		,"K00_10",""
		,"K00_18",""
		,"K00_21",""
		,"K01_04","Operaciones intragrupo"
		,"K01_08",""
		,"K01_10",""
		,"K01_18",""
		,"K01_21",""
		,"K02_04","R\u00E9gimen especial de bienes usados, objetos de arte, antigüedades y objetos de colecci\u00F3n"
		,"K02_08",""
		,"K02_10",""
		,"K02_18",""
		,"K02_21",""
		,"K03_18","R\u00E9gimen especial de agencias de viaje"
		,"K03_21",""
		,"K04_04","Adquisiciones intracomunitarias de bienes"
		,"K04_08",""
		,"K04_10",""
		,"K04_18",""
		,"K04_21",""
		,"K05_04","Adquisiciones intracomunitarias de servicios"
		,"K05_08",""
		,"K05_10",""
		,"K05_18",""
		,"K05_21",""
		,"K06","IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
		,"K07","Modificaci\u00F3n de bases y cuotas"
		,"K08","Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
		,"K09","Total bases y cuotas IVA"
		,"K10_05","Recargo de equivalencia"
		,"K10_1",""
		,"K10_14",""
		,"K10_4",""
		,"K10_52",""
		,"K10_175",""
		,"K11","Modificaci\u00F3n recargo equivalencia"
		,"K12","Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
		,"K13","Total cuotas IVA y recargo de equivalencia"
		,"K14_04","IVA deducible en operaciones interiores de bienes y servicios corrientes"
		,"K14_07",""
		,"K14_08",""
		,"K14_10",""
		,"K14_16",""
		,"K14_18",""
		,"K14_21",""
		,"K15","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
		,"K16_04","IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
		,"K16_07",""
		,"K16_08",""
		,"K16_10",""
		,"K16_16",""
		,"K16_18",""
		,"K16_21",""
		,"K17","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
		,"K18_04","IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
		,"K18_07",""
		,"K18_08",""
		,"K18_10",""
		,"K18_16",""
		,"K18_18",""
		,"K18_21",""
		,"K19","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
		,"K20_04","IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
		,"K20_07",""
		,"K20_08",""
		,"K20_10",""
		,"K20_16",""
		,"K20_18",""
		,"K20_21",""
		,"K21","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
		,"K22_04","IVA deducible en importaciones de bienes corrientes"
		,"K22_07",""
		,"K22_08",""
		,"K22_10",""
		,"K22_16",""
		,"K22_18",""
		,"K22_21",""
		,"K23","Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
		,"K24_04","IVA deducible en importaciones de bienes de inversi\u00F3n"
		,"K24_07",""
		,"K24_08",""
		,"K24_10",""
		,"K24_16",""
		,"K24_18",""
		,"K24_21",""
		,"K25","Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n"
		,"K26_04","IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
		,"K26_07",""
		,"K26_08",""
		,"K26_10",""
		,"K26_16",""
		,"K26_18",""
		,"K26_21",""
		,"K27","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes" 
		,"K28_04","IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"K28_07",""
		,"K28_08",""
		,"K28_10",""
		,"K28_16",""
		,"K28_18",""
		,"K28_21",""
		,"K29","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"K30_04","IVA deducible en adquisiciones intracomunitarias de servicios"
		,"K30_07",""
		,"K30_08",""
		,"K30_10",""
		,"K30_16",""
		,"K30_18",""
		,"K30_21",""
		,"K31","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
		,"K32","Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
		,"K33","Rectificaci\u00F3n de deducciones"
		,"K34","Regularizaci\u00F3n de bienes de inversi\u00F3n"
		,"K35","Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
		,"K36","Suma de deducciones"
		,"K37","Resultado r\u00E9gimen general"
	})
	
	String mod390DetailKey(@Select Mod390DetailKey key);
	
}
