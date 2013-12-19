package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.fiscal.client.FiscalEnum.Administration;
import com.esferalia.aon.gwt.fiscal.client.FiscalEnum.Province;
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

	//  ----------------------------------------------------------------- Format
	@DefaultMessage("#,##0")
	String integerPattern();
	@DefaultMessage("#,##0.00")
	String decimalPattern();
	@DefaultMessage("ESP")
	String currencyCode();
	
	//  ---------------------------------------------------------------- Modulos
	@DefaultMessage("Modelo 190")
	String mod190();
	
	@DefaultMessage("Modelo 180")
	String mod180();
	//  ---------------------------------------------------------------- Errores
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

	//  ---------------------------------------------------------Button Messages
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

	//  ---------------------------------------------------------Common Messages
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
	
	@DefaultMessage("Tel\u00E9fono")
	String phone();

	@DefaultMessage("Total")
	String total();
	
	@DefaultMessage("Porcentaje")
	String percent();
	
	//  -------------------------------------------------------------- Model 190
	
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

	@DefaultMessage("Cómputo de los tres primeros")
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
	
	//  -----------------------------------------------------------Enum Messages	
	@DefaultMessage("----------")
	@AlternateMessage({ "ALAVA", "Araba/Alava", 
		"BIZKAIA", "Bizkaia", 
		"GIPUZKOA", "Gipuzkoa",
		"NAVARRA", "Navarra" ,
		"COMMON_TERRITORY", "Territorio Com\u00FAn"})
	String administrationName(@Select Administration administration);

	@DefaultMessage("----------")
	@AlternateMessage({ 
		"DESCONOCIDO","Desconocido",
		"ARABA","Araba/\u00C1lava",
		"ALBACETE","Albacete",
		"ALICANTE","Alicante",
		"ALMERIA","Almer\u00EDa",
		"ASTURIAS","Asturias",
		"AVILA","\u00C1vila",
		"BADAJOZ","Badajoz",
		"BARCELONA","Barcelona",
		"BIZKAIA","Bizkaia",
		"BURGOS","Burgos",
		"CACERES","C\u00E1ceres",
		"CADIZ","C\u00E1diz",
		"CANTABRIA","Cantabria",
		"CASTELLON","Castell\u00F3n",
		"CEUTA","Ceuta",
		"CIUDAD_REAL","Ciudad Real",
		"CORDOBA","C\u00F3rdoba",
		"A_CORUNA","Coru\u00F1a, A",
		"CUENCA","Cuenca",
		"GIPUZKOA","Gipuzkoa",
		"GIRONA","Girona",
		"GRANADA","Granada",
		"GUADALAJARA","Guadalajara",
		"HUELVA","Huelva",
		"HUESCA","Huesca",
		"ILLES_BALEARS","Illes Balears",
		"JAEN","Jaen",
		"LEON","Le\u00F3n",
		"LLEIDA","Lleida",
		"LUGO","Lugo",
		"MADRID","Madrid",
		"MALAGA","M\u00E1laga",
		"MELILLA","Melilla",
		"MURCIA","Murcia",
		"NAVARRA","Navarra",
		"OURENSE","Ourense",
		"PALENCIA","Palencia",
		"LAS_PALMAS","Palmas, Las",
		"PONTEVEDRA","Pontevedra",
		"LA_RIOJA","Rioja, La",
		"SALAMANCA","Salamanca",
		"TENERIFE","S.C. Tenerife",
		"SEGOVIA","Segovia",
		"SEVILLA","Sevilla",
		"SORIA","Soria",
		"TARRAGONA","Tarragona",
		"TERUEL","Teruel",
		"TOLEDO","Toledo",
		"VALENCIA","Valencia",
		"VALLADOLID","Valladolid",
		"ZAMORA","Zamora",
		"ZARAGOZA","Zaragoza",
		
		"NO_RESIDENTE","No residente"
		})
	String provinceName(@Select Province province);
	
}
