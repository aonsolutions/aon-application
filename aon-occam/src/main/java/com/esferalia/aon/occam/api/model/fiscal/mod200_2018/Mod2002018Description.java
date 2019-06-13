package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.util.EnumMap;

public class Mod2002018Description {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
		
	public static EnumMap<Mod2002018Key,String> DESCRIPTION_MAP = new EnumMap<Mod2002018Key,String>(Mod2002018Key.class);

	static {		
		DESCRIPTION_MAP.put(Mod2002018Key.X0000, "Tipo de ejercicio");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0001,"Entidad sin \u00E1nimo de lucro acogida r\u00E9gimen fiscal T\u00EDtulo II Ley 49/2002");
		DESCRIPTION_MAP.put(Mod2002018Key.C0002,"Entidad parcialmente exenta");
		DESCRIPTION_MAP.put(Mod2002018Key.C0003,"Sociedad de inversi\u00F3n de capital variable o fondo de inversi\u00F3n de car\u00E1cter financiero");
		DESCRIPTION_MAP.put(Mod2002018Key.C0004,"Sociedad de inversi\u00F3n inmobiliaria o fondo de inversi\u00F3n inmobiliaria");
		DESCRIPTION_MAP.put(Mod2002018Key.C0005,"Comunidades titulares de montes vecinales en mano com\u00FAn");
		DESCRIPTION_MAP.put(Mod2002018Key.C0011,"Entidad de tenencia de valores extranjeros");
		DESCRIPTION_MAP.put(Mod2002018Key.C0013,"Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico espa\u00F1ola o Uni\u00F3n temporal de empresas");
		DESCRIPTION_MAP.put(Mod2002018Key.C0014,"Agrupaci\u00F3n europea de inter\u00E9s econ\u00F3mico");
		DESCRIPTION_MAP.put(Mod2002018Key.C0017,"Cooperativa protegida");
		DESCRIPTION_MAP.put(Mod2002018Key.C0018,"Cooperativa especialmente protegida"); 
		DESCRIPTION_MAP.put(Mod2002018Key.C0019,"Resto cooperativas");
		DESCRIPTION_MAP.put(Mod2002018Key.C0021,"Establecimiento permanente"); 
		DESCRIPTION_MAP.put(Mod2002018Key.C0023,"Gran empresa");
		DESCRIPTION_MAP.put(Mod2002018Key.C0024,"Entidad de cr\u00E9dito"); 
		DESCRIPTION_MAP.put(Mod2002018Key.C0025,"Entidad aseguradora");
		DESCRIPTION_MAP.put(Mod2002018Key.C0031,"Entidades de capital-riesgo");
		DESCRIPTION_MAP.put(Mod2002018Key.C0032,"Sociedad de desarrollo industrial regional");
		DESCRIPTION_MAP.put(Mod2002018Key.C0036,"Sociedad de garant\u00EDa rec\u00EDproca o de reafianzamiento.");
		DESCRIPTION_MAP.put(Mod2002018Key.C0048,"Fondo de pensiones R.D.L. 1/2002, 00048 de 29 de noviembre");
		DESCRIPTION_MAP.put(Mod2002018Key.C0058,"Mutua de seguros o Mutualidad de previsi\u00F3n social");
		DESCRIPTION_MAP.put(Mod2002018Key.C0060,"Fondos o activos de titulizaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.C0066,"Entidad patrimonial");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0006,"Incentivos entidad de reducida dimensi\u00F3n (Cap. XI, T\u00EDt. VII LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0015,"Entidad ZEC");
		DESCRIPTION_MAP.put(Mod2002018Key.C0015G,"Entidad ZEC y pertenece a grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0022,"R\u00E9gimen entid. navieras en funci\u00F3n del tonelaje");
		DESCRIPTION_MAP.put(Mod2002018Key.C0028,"Tribut. conjunta Estado/Diput. Cdad. Forales");		
		DESCRIPTION_MAP.put(Mod2002018Key.C0047,"Entidades sometidas a la normativa foral");		
		DESCRIPTION_MAP.put(Mod2002018Key.C0035,"Aplicaci\u00F3n r\u00E9g. especial fusiones, escisiones, aportaciones y canjes valores (Cap.VII, T\u00EDt.VII)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0049,"Reg\u00EDmenes especiales de normativa foral");
		DESCRIPTION_MAP.put(Mod2002018Key.C0029,"R\u00E9gimen especial Canarias");
		DESCRIPTION_MAP.put(Mod2002018Key.C0033,"R\u00E9gimen especial miner\u00EDa");
		DESCRIPTION_MAP.put(Mod2002018Key.C0034,"R\u00E9gimen especial hidrocarburos");		
		DESCRIPTION_MAP.put(Mod2002018Key.C0038,"Entidad dedicada al arrend. de viviendas");
		DESCRIPTION_MAP.put(Mod2002018Key.C0046,"Entidad en r\u00E9g. de atribuci\u00F3n de rentas constitu\u00EDda en el extranjero con presencia en territorio espa\u00F1ol");
		DESCRIPTION_MAP.put(Mod2002018Key.C0012,"SOCIMI");
		DESCRIPTION_MAP.put(Mod2002018Key.C0064,"R\u00E9gimen fiscal entrada SOCIMI");
		DESCRIPTION_MAP.put(Mod2002018Key.C0057,"R\u00E9gimen fiscal salida SOCIMI");
		DESCRIPTION_MAP.put(Mod2002018Key.C0020,"Otros reg\u00EDmenes especiales");
		DESCRIPTION_MAP.put(Mod2002018Key.C0062,"R\u00E9g. fiscal de operaciones de aportaci\u00F3n de activos a sociedades para la gesti\u00F3n de activos (Ley 8/2012)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0007,"Inclusi\u00F3n en base imp. rentas positivas art. 100.8 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.C0009,"Sociedad dominante de grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0010,"Sociedad dependiente de grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0016,"Opci\u00F3n art. 46.2 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.C0026,"Entidad inactiva");
		DESCRIPTION_MAP.put(Mod2002018Key.C0027,"Base imponible negativa o cero");
		DESCRIPTION_MAP.put(Mod2002018Key.C0030,"Transmisi\u00F3n elementos patrimoniales arts. 27.2.d) y 77.1 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.C0039,"Entidad que forma parte de un grupo mercantil (art. 42 del C\u00F3d. Comercio)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0043,"Obligaci\u00F3n informaci\u00F3n DT 5a RIS");
		DESCRIPTION_MAP.put(Mod2002018Key.C0045,"Inversiones anticipadas-reserva inversiones en Canarias (art. 27.11 Ley 19/1994)");		 
		DESCRIPTION_MAP.put(Mod2002018Key.C0063,"Tipo gravamen reducido para entidades de nueva creaci\u00F3n (DT 22a LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0071,"Tipo gravamen reducido para entidades de nueva creaci\u00F3n (art. 29.1 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0070,"Compensaci\u00F3n bases imponibles negativas para entidades de nueva creaci\u00F3n (art. 23.3 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0059,"Opciones arts. 39.2 y 39.3 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.C0065,"Bonificaci\u00F3n personal investigador (R.D. 475/2014)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0067,"Opci\u00F3n r\u00E9gimen transitorio de la reducci\u00F3n de ingresos procedentes de determinados activos intangibles (DT 20\u00AA LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0072,"Extinci\u00F3n de entidad");
		DESCRIPTION_MAP.put(Mod2002018Key.C0073,"Opci\u00F3n del 0,7% de la cuota \u00EDntegra para fines sociales (DA 103\u00AA Ley 6/2018)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0050,"Balance. Normal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0051,"Balance. Abreviado");
		DESCRIPTION_MAP.put(Mod2002018Key.C0052,"Balance. PYMES");
		DESCRIPTION_MAP.put(Mod2002018Key.C0075,"ECPN. Normal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0076,"ECPN. Abreviado (voluntario)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0077,"ECPN. PYMES (voluntario)");
		DESCRIPTION_MAP.put(Mod2002018Key.C0053,"P\u00E9rdidas y ganancias. Normal");
		DESCRIPTION_MAP.put(Mod2002018Key.C0054,"P\u00E9rdidas y ganancias. Abreviado");
		DESCRIPTION_MAP.put(Mod2002018Key.C0055,"P\u00E9rdidas y ganancias. PYMES");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0061,"Entidades que sin ser Instituciones de inversi\u00F3n colectiva utilicen los estados de cuentas aplicables a estas");
		
		DESCRIPTION_MAP.put(Mod2002018Key.C0041,"Personal fijo");
		DESCRIPTION_MAP.put(Mod2002018Key.C0042,"Personal no fijo");
	}
	
	// PARTICIPACIONES
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.P1501,"Valor nominal total de la participaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.P1502,"Valor en libros (en el activo de la declarante) de la participaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.P1503,"Ingresos por Dividendos recibidos en el ejercicio declarado");
		DESCRIPTION_MAP.put(Mod2002018Key.P1504,"a) Correcci\u00F3n de valor incluida en p\u00E9rdidas y ganancias del periodo");
		DESCRIPTION_MAP.put(Mod2002018Key.P1505,"b) Reversi\u00F3n de p\u00E9rdidas por deterioro de valores (D.T. 16a LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.P1506,"c) Eliminaci\u00F3n del deterioro contable incluido en P y G (art. 13.2b) LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.P1809,"d) Eliminaci\u00F3n del deterioro de valores repr. de partic. en el capital o fondos propios (art. 15 k) LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.P1810,"e) Ajuste por la disminuci\u00F3n de valor originada por criterio de valor razonable (art. 15 l) LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.P1507,"f) Efecto de la correcci\u00F3n valorativa en la BI del ejercicio");
		DESCRIPTION_MAP.put(Mod2002018Key.P1508,"g) Saldo de correcciones fiscales (art. 12.3 RDL 4/2004) pendientes a fin de ejercicio");
	}

	static {
		DESCRIPTION_MAP.put(Mod2002018Key.BA101,"ACTIVO NO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA102,"Inmovilizado intangible (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA103,"Desarrollo (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA104,"Concesiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA105,"Patentes, licencias, marcas y similares (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA106,"Fondo de comercio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA107,"Aplicaciones inform\u00E1ticas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA108,"Investigaci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA700,"Propiedad intelectual (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA109,"Otro inmovilizado intangible (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA110,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA111,"Inmovilizado material (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA112,"Terrenos y construcciones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA113,"Instalaciones t\u00E9cnicas y otro inmovilizado material (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA114,"Inmovilizado en curso y anticipos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA115,"Inversiones inmobiliarias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA116,"Terrenos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA117,"Construcciones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA118,"Inversiones en empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA119,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA120,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA121,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA122,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA123,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA124,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA125,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA126,"Inversiones financieras a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA127,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA128,"Cr\u00E9ditos a terceros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA129,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA130,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA131,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA132,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA133,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA134,"Activos por impuesto diferido (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA135,"Deudores comerciales no corrientes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA136,"ACTIVO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA137,"Activos no corrientes mantenidos para la venta (N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA138,"Existencias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA139,"Comerciales (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA140,"Materias primas y otros aprovisionamientos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA141,"Productos en curso (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA142,"De ciclo largo de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA143,"De ciclo corto de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA144,"Productos terminados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA145,"De ciclo largo de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA146,"De ciclo corto de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA147,"Subproductos, residuos y materiales recuperados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA148,"Anticipos a proveedores (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA701,"Derechos de emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA149,"Deudores comerciales y otras cuentas a cobrar (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA150,"Clientes por ventas y prestaciones de servicios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA151,"Clientes por ventas y prestaciones de servicios a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA152,"Clientes por ventas y prestaciones de servicios a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA153,"Clientes empresas del grupo y asociadas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA154,"Deudores varios (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA155,"Personal (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA156,"Activos por impuesto corriente (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA157,"Otros cr\u00E9ditos con las Administraciones p\u00FAblicas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA158,"Accionistas (socios) por desembolsos exigidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA159,"Otros deudores (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA160,"Inversiones en empresas del grupo y asociadas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA161,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA162,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA163,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA164,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA165,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA166,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA167,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA168,"Inversiones financieras a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA169,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA170,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA171,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA172,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA173,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA174,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA175,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA176,"Periodificaciones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA177,"Efectivo y otros activos l\u00EDquidos equivalentes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA178,"Tesorer\u00EDa (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA179,"Otros activos l\u00EDquidos equivalentes (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BA180,"TOTAL ACTIVO (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.BP185,"PATRIMONIO NETO (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP186,"Fondos propios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP187,"Capital (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP188,"Capital escriturado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP189,"(Capital no exigido) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP190,"Prima de emisi\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP191,"Reservas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP192,"Legal y estatutarias (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP193,"Otras reservas (N, A, P))");
		DESCRIPTION_MAP.put(Mod2002018Key.BP702,"Reserva de revalorizaci\u00F3n (Ley 16/2012 de 27 de diciembre) (N)");		
		DESCRIPTION_MAP.put(Mod2002018Key.BP1001,"Reserva de capitalizaci\u00F3n (N,A,P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP1002,"Reserva de nivelaci\u00F3n (N,A,P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP194,"(Acciones y participaciones en patrimonio propias) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP195,"Resultados de ejercicios anteriores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP196,"Remanente (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP197,"(Resultados negativos de ejercicios anteriores) (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP198,"Otras aportaciones	 de socios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP199,"Resultado del ejercicio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP200,"(Dividendo a cuenta) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP201,"Otros instrumentos de patrimonio neto (N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP202,"Ajustes por cambio de valor (N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP203,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP204,"Operaciones de cobertura (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP205,"Activos no corrientes y pasivos vinculados, mantenidos para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP206,"Diferencia de conversi\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP207,"Otros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP208,"Ajustes en patrimonio neto (P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP209,"Subvenciones, donaciones y legados recibidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP210,"PASIVO NO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP211,"Provisiones a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP212,"Obligaciones por prestaciones a largo plazo al personal (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP213,"Actuaciones medioambientales (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP214,"Provisiones por reestructuraci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP215,"Otras provisiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP216,"Deudas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP217,"Obligaciones y otros valores negociables (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP218,"Deudas con entidades de cr\u00E9dito (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP219,"Acreedores por arrendamiento financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP220,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP221,"Otros pasivos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP222,"Otras deudas a largo plazo (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP223,"Deudas con empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP224,"Pasivos por impuesto diferido (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP225,"Periodificaciones a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP226,"Acreedores comerciales no corrientes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP227,"Deuda con caracter\u00EDsticas especiales a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP228,"PASIVO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP229,"Pasivos vinculados con activos no corr. mantenidos para la venta(N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP230,"Provisiones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP703,"Provisiones por derechos de emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP704,"Otras provisiones (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP231,"Deudas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP232,"Obligaciones y otros valores negociables (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP233,"Deudas con entidades de cr\u00E9dito (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP234,"Acreedores por arrendamiento financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP235,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP236,"Otros pasivos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP237,"Otras deudas a corto plazo (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP238,"Deudas con empresas del grupo y asociadas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP239,"Acreedores comerciales y otras cuentas a pagar (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP240,"Proveedores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP241,"Proveedores a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP242,"Proveedores a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP243,"Proveedores, empresas del grupo y asociadas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP244,"Acreedores varios (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP245,"Personal (remuneraciones pendientes de pago) (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP246,"Pasivos por impuesto corriente (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP247,"Otras deudas con las Administraciones p\u00FAblicas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP248,"Anticipos de clientes (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP249,"Otros acreedores (A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP250,"Periodificaciones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP251,"Deuda con caracter\u00EDsticas especiales a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.BP252,"TOTAL PATRIMONIO NETO Y PASIVO (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.PG255,"Importe neto de la cifra de negocios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG256,"Ventas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG257,"Prestaciones de servicios (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG711,"Ingresos de car\u00E1cter financiero de las entidades concesionarias de infraestructuras p\u00FAblicas (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG705,"Ingresos de car\u00E1cter financiero de las sociedades holding (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG706,"De participaciones en instrumentos de patrimonio (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG707,"De valores negociables y otros instrumentos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG708,"Resto (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG258,"Variaci\u00F3n de existencias de productos terminados y en curso de fabricaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG259,"Trabajos realizados por la empresa para su activo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG260,"Aprovisionamientos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG261,"Consumo de mercader\u00EDas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG760,"Compras de mercader\u00EDas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG761,"Variaci\u00F3n de existencias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG262,"Consumo de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG762,"Compras de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG763,"Variaci\u00F3n de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG263,"Trabajos realizados por otras empresas (N, A, P))");
		DESCRIPTION_MAP.put(Mod2002018Key.PG264,"Deterioro de mercader\u00EDas, materias primas y otros aprovisionamientos (N, A, P))");
		DESCRIPTION_MAP.put(Mod2002018Key.PG265,"Otros ingresos de explotaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG266,"Ingresos accesorios y otros de gesti\u00F3n corriente (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG267,"Ingresos por arrendamientos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG268,"Resto (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG269,"Subvenciones de explotaci\u00F3n incorporadas al resultado del ejercicio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG270,"Gastos de personal (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG271,"Sueldos y salarios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG273,"Indemnizaciones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG274,"Seguridad Social a cargo de la empresa (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG275,"Retribuciones a largo plazo mediante sistemas de aportaciones o prestaci\u00F3n definida (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG276,"Retribuciones mediante instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG277,"Otros gastos sociales (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG278,"Provisiones (N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG279,"Otros gastos de explotaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG280,"Servicios exteriores (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG281,"Tributos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG282,"P\u00E9rdidas, deterioro y variaci\u00F3n de provisiones por operaciones comerciales (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG283,"Otros gastos de gesti\u00F3n corriente (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG709,"Gastos por emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG284,"Amortizaci\u00F3n del inmovilizado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG285,"Imputaci\u00F3n de subvenciones de inmovilizado no financiero y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG286,"Excesos de provisiones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG287,"Deterioro y resultado por enajenaciones del inmovilizado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG288,"Deterioro y p\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG289,"Deterioros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG290,"Reversi\u00F3n de deterioros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG291,"Resultados por enajenaciones y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG292,"Beneficios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG293,"P\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG710,"Deterioro y resultados por enajenaciones del inmovilizado de las sociedades holding (N, A, P))");
		DESCRIPTION_MAP.put(Mod2002018Key.PG294,"Diferencia negativa de combinaciones de negocio (N, A)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG295,"Otros resultados (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG296,"RESULTADO DE EXPLOTACI\u00D3N (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG297,"Ingresos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG298,"De participaciones en instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG299,"En empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG300,"En terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG301,"De valores negociables y otros instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG302,"De empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG303,"De terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG304,"Imputaci\u00F3n de subvenciones, donaciones y legados de car\u00E1cter financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG305,"Gastos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG306,"Por deudas con empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG307,"Por deudas con terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG308,"Por actualizaci\u00F3n de provisiones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG309,"Variaci\u00F3n del valor razonable en instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG310,"Cartera de negociaci\u00F3n y otros (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG311,"Imputaci\u00F3n al resultado del ejercicio por activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG312,"Diferencias de cambio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG313,"Deterioro y resultado por enajenaci\u00F3n de instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG314,"Deterioros y p\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG315,"Deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG316,"Deterioros, Otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG317,"Reversi\u00F3n de deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG318,"Reversi\u00F3n de deterioros, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG319,"Resultados por enajenaci\u00F3n y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG320,"Beneficios, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG321,"Beneficios, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG322,"P\u00E9rdidas, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG323,"P\u00E9rdidas, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG329,"Otros ingresos y gastos de car\u00E1cter financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG330,"Incorporaci\u00F3n al activo de gastos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG331,"Ingresos financieros derivados de convenios de acreedores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG332,"Resto de ingresos y gastos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG324,"RESULTADO FINANCIERO (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG325,"RESULTADO ANTES DE IMPUESTOS (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG326,"Impuestos sobre beneficios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG327,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS (N, A, P) (2)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG328,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.PG500,"RESULTADO DE LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.T0500,"RESULTADO DE LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0336,"Por valoraci\u00F3n de instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0337,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0338,"Otros ingresos/gastos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0339,"Por coberturas de flujos de efectivo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0340,"Subvenciones, donaciones y legados recibidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0341,"Por ganancias y p\u00E9rdidas actuariales y otros ajustes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0342,"Por activos no corrientes y pasivos vinculados, mantenidos pra la venta (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0343,"Diferencias de conversi\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0344,"Efecto impositivo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0345,"Total ingresos y gastos imputados directamente en el patrimonio neto (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0346,"Por valoraci\u00F3n de instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0347,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0348,"Otros ingresos/gastos (N)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0349,"Por coberturas de flujo de efectivo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0350,"Subvenciones, donaciones y legados recibidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0351,"Por activos no corrientes y pasivos vinculados, mantenidos para la venta (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0352,"Diferencias de conversi\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0353,"Efecto impositivo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0354,"Total transferencias a la cuenta de p\u00E9rdidas y ganancias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002018Key.T0355,"TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.LQ500, "Resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ301, "Correcciones por Impuesto sobre Sociedades.");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ302, "Correcciones por Impuesto sobre Sociedades. Disminuciones.");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ501, "Resultado de la cuenta de p\u00E9rdidas y ganancias antes de Impuesto sobre Sociedades");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1230,"Correcciones al resultado contable al considerar los requisitos o calificaciones contables referidos al grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1231,"Correcciones al resultado contable al considerar los requisitos o calificaciones contables referidos al grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002018Key.I0417, "Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002018Key.D0418, "Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.LQ578, "Base imponible de actividades o rentas que tributen en r\u00E9gimen general");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ579, "Base imponible derivada de la aplicaci\u00F3n del r\u00E9gimen especial");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ0N1, "N\u00BA de buques a los que se aplica el r\u00E9gimen");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ630, "Base imponible resultante de aplicar la escala del apartado 1 del art. 114 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ631, "Importe de rentas generadas en transmisiones de buques (reserva, diferencia entre la amortizaci\u00F3n fiscal y la contable)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ632, "Compensaci\u00F3n de bases imponibles negativas de per\u00EDodos anteriores (a compensar \u00FAnicamente con la casilla 631)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1029,"Base imponible individual a integrar por las entidades que forman parte del grupo");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1030,"Eliminaciones e incorporaciones correspondientes a la entidad");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1031,"Integraci\u00F3n individual de las dotaciones del art. 11.12 LIS");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ550, "Base imponible antes de la aplicaci\u00F3n de la reserva de capitalizaci\u00F3n y compensaci\u00F3n de bases imponibles negativas");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ550TG, "SOCIMIS: Parte de la base imponible del per\u00EDodo impositivo que tributa al tipo general (antes de compensaci\u00F3n de bases imponibles negativas)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ550T0, "SOCIMIS: Parte de la base imponible del per\u00EDodo impositivo que tributa al tipo del 0% (antes de compensaci\u00F3n de bases imponibles negativas)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1032,"Reserva de capitalizaci\u00F3n");		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ547, "Compensaci\u00F3n de bases imponibles negativas de per\u00EDodos anteriores");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ552, "Base imponible");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1033,"Reserva de nivelaci\u00F3n (Aumentos)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1034,"Reserva de nivelaci\u00F3n (Disminuciones)");		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1330,"Base imponible despu\u00E9s de la reserva de nivelaci\u00F3n");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ553, "Resultados cooperativos");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ554, "Resultados extracooperativos");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ555, "Socios residentes");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ556, "Socios no residentes");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ559, "Base imponible a tipo de gravamen especial");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ520, "Parte de la base imponible del periodo impositivo que tributa al tipos general");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ521, "Parte de la base imponible del periodo impositivo que tributa al tipo del 0%");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ545, "Rentas correspondientes a quitas por acuerdo con acreedores no vinculados (art. 26.1 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1509,"Rentas correspondientes a la reversi\u00F3n de deterioros (DT 16\u00AA.8 LIS)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ558, "Tipo de gravamen");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ560, "Cuota \u00EDntegra previa");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ210, "Aumentos: P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2) LIS a los que se refiere el art. 11.12 LIS (convertida en cuota)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ480, "Disminuciones: P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2) LIS a los que se refiere el art. 11.12 LIS (convertida en cuota)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ408, "Aumentos: Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1037,"Disminuciones: Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ593, "Disminuciones: Rentas corresp. a quitas por acuerdo con acreedores no vinculados cooperativas (a nivel cuota) (D.A. 8\u00AA Ley 20/1990)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1510,"Disminuciones: Rentas correspondientes a la reversi\u00F3n de deterioros cooperativas (a nivel cuota) (DT 16\u00AA.8 LIS)");		
		DESCRIPTION_MAP.put(Mod2002018Key.LQ561, "Compensaci\u00F3n de cuotas por p\u00E9rdidas de cooperativas");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1285,"Aumentos: Reserva de nivelaci\u00F3n convertido en cuotas (s\u00F3lo entidades del art. 101 LIS)");	
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1286,"Disminuciones: Reserva de nivelaci\u00F3n convertido en cuotas (s\u00F3lo entidades del art. 101 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1331,"Cuota \u00EDntegra previa despu\u00E9s de la reserva de nivelaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ562, "Cuota \u00EDntegra");
		DESCRIPTION_MAP.put(Mod2002018Key.LQ1038,"Incremento por incumplimiento reserva de nivelaci\u00F3n (art. 105.6 LIS)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.BN567, "Bonificaci\u00F3n por rentas obtenidas en Ceuta y Melilla (art. 33 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN568, "Bonificaciones por prestaci\u00F3n de servicios (art. 34 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN563, "Bonificaci\u00F3n rendimientos por ventas bienes corporales producidos en Canarias (art. 26 Ley 19/1994)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN566, "Bonificaciones Sociedades Cooperativas (Ley 20/1990)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN576, "Bonificaciones entidades dedicadas al arrendamiento de viviendas (Cap\u00EDtulo III T\u00EDtulo VII LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN569, "Otras bonificaciones");
		

		DESCRIPTION_MAP.put(Mod2002018Key.BN570, "DI interna de per\u00EDodos anteriores aplicada en el ejercicio (art. 30 RDL 4/2004)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1344,"DI interna de per\u00EDodos anteriores aplicada en el ejercicio (DT 23\u00AA.1 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1280,"DI interna generada y aplicada en el ejercicio (DT 23\u00AA.1 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN572, "DI internacional de per\u00EDodos anteriores aplicada en el ejercicio (art. 31 y 32 RDL 4/2004)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN571, "DI internacional de per\u00EDodos anteriores aplicada en el ejercicio (art. 31 y 32 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN573, "DI internacional generada y aplicada en el ejercicio actual (arts. 31 y 32 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN575, "Transparencia fiscal internacional (art. 100.11 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN577, "DI interna intersocietaria al 5/10% (cooperativas)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN581, "Bonificaciones empresas navieras en Canarias (art. 76 Ley 19/1994)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN582, "Cuota \u00EDntegra ajustada positiva");
		
		DESCRIPTION_MAP.put(Mod2002018Key.BN583, "Apoyo fiscal a la inversi\u00F3n y otras deducciones");
		DESCRIPTION_MAP.put(Mod2002018Key.BN585, "Deducci\u00F3n DT 24\u00AA.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95");
		DESCRIPTION_MAP.put(Mod2002018Key.BN584, "Deducciones DT 24\u00AA.1 LIS y DT 8\u00AA RDL 4/2004");
		DESCRIPTION_MAP.put(Mod2002018Key.BN588, "Deducciones con l\u00EDmite del Cap\u00EDtulo IV T\u00EDtulo VI RDL 4/2004 y LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1039,"Deducciones por producciones cinematogr\u00E1ficas extranjeras (art. 36.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN565, "Deducci\u00F3n donaciones a entidades sin fines de lucro (Ley 49/2002)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN590, "Deducciones Inversi\u00F3n Canarias");
		DESCRIPTION_MAP.put(Mod2002018Key.BN399, "Deducciones espec\u00EDficas de las entidades sometidas a normativa foral");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1040,"Deducci\u00F3n por reversi\u00F3n de medidas temporales DT 37\u00AA.1 LIS");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1041,"Deducci\u00F3n por reversi\u00F3n de medidas temporales DT 37\u00AA.2 LIS");
				
		DESCRIPTION_MAP.put(Mod2002018Key.BN082, "Deducciones sin l\u00EDmite I + D + i");
		DESCRIPTION_MAP.put(Mod2002018Key.BN592, "Cuota l\u00EDquida positiva");
		
		DESCRIPTION_MAP.put(Mod2002018Key.BN1785,"Retenciones por rendimientos del capital mobiliario");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1787,"Retenciones por arrendamientos de inmuebles urbanos");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1789,"Retenciones por rendimientos del capital mobiliario atribuidas por entidades en atribuci\u00F3n de rentas");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1791,"Retenciones por arrendamientos de inmuebles urbanos atribuidas por entidades en atribuci\u00F3n de rentas");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1793,"Retenciones por otros conceptos diferentes a los rendimientos del capital mobiliario o a los arrendamientos de inmuebles urbanos atribu\u00EDdas por entidades en atribuci\u00F3n de rentas");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1795,"Retenciones e ingresos a cuenta participaciones IIC");
		DESCRIPTION_MAP.put(Mod2002018Key.BN597, "Retenciones sobre los premios de determinadas loter\u00EDas y apuestas");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1798,"Retenciones por otros conceptos NO inclu\u00EDdos en las casillas anteriores");
		DESCRIPTION_MAP.put(Mod2002018Key.BN083, "Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 44.2 RDL 4/2004 y DT 24\u00AA.7 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1042,"Abono de deducciones por producciones extranjeras (art. 36.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN599, "Cuota del ejercicio a ingresar o a devolver");
		DESCRIPTION_MAP.put(Mod2002018Key.BN600, "Cuota del ejercicio a ingresar o a devolver");
		
		DESCRIPTION_MAP.put(Mod2002018Key.BN601, "1er pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN602, "1er pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN603, "2o pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN604, "2o pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN605, "3er pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN606, "3er pago fraccionado");
		DESCRIPTION_MAP.put(Mod2002018Key.BN611, "Cuota diferencial");
		DESCRIPTION_MAP.put(Mod2002018Key.BN612, "Cuota diferencial");
		DESCRIPTION_MAP.put(Mod2002018Key.BN615, "Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores");
		DESCRIPTION_MAP.put(Mod2002018Key.BN616, "Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores");
		DESCRIPTION_MAP.put(Mod2002018Key.BN633, "Incremento por incumplimiento de requisitos SOCIMI");
		DESCRIPTION_MAP.put(Mod2002018Key.BN642, "Incremento por incumplimiento de requisitos SOCIMI");
		DESCRIPTION_MAP.put(Mod2002018Key.BN617, "Intereses de demora");
		DESCRIPTION_MAP.put(Mod2002018Key.BN618, "Intereses de demora");
		DESCRIPTION_MAP.put(Mod2002018Key.BN619, "Importe ingreso / devoluci\u00F3n efectuada de la declaraci\u00F3n originaria");
		DESCRIPTION_MAP.put(Mod2002018Key.BN620, "Importe ingreso / devoluci\u00F3n efectuada de la declaraci\u00F3n originaria");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1234B,"Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 39.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN083, "Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 39.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1332,"Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 39.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1200,"Abono de deducciones por producciones cinematogr\u00E1ficas extranjeras (art. 39.3 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1042,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1333,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS)");
		
		DESCRIPTION_MAP.put(Mod2002018Key.BN621, "L\u00EDquido a ingresar o a devolver");
		DESCRIPTION_MAP.put(Mod2002018Key.BN622, "L\u00EDquido a ingresar o a devolver");
		DESCRIPTION_MAP.put(Mod2002018Key.LM150, "Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1020,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.BN1043,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM506, "Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");		
		DESCRIPTION_MAP.put(Mod2002018Key.BN1021,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");		
		DESCRIPTION_MAP.put(Mod2002018Key.BN1044,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)");
	}
	
	static {
		// Aplicacion de resultados - Base de reparto 
		DESCRIPTION_MAP.put(Mod2002018Key.ID650,"P\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002018Key.ID651,"Remanente");
		DESCRIPTION_MAP.put(Mod2002018Key.ID652,"Reservas");
		DESCRIPTION_MAP.put(Mod2002018Key.ID653,"Total");
		
		// Aplicacion de resultados - Aplicacion
		DESCRIPTION_MAP.put(Mod2002018Key.ID654,"A reservas");
		DESCRIPTION_MAP.put(Mod2002018Key.ID1270,"Reservas de capitalizaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.ID1271,"Reservas de nivelaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002018Key.ID1522,"Otras reservas");		
		DESCRIPTION_MAP.put(Mod2002018Key.ID655,"Intereses aportaciones al capital (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002018Key.ID656,"A dividendos");
		DESCRIPTION_MAP.put(Mod2002018Key.ID658,"A dotaci\u00F3n O.S. (Cajas de ahorro)");
		DESCRIPTION_MAP.put(Mod2002018Key.ID659,"A F.R.O. y dotaciones voluntarias al F.E.P. (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002018Key.ID660,"A retornos cooperativos (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002018Key.ID662,"Part\u00EDcipes (IIC)");
		DESCRIPTION_MAP.put(Mod2002018Key.ID664,"A remanente y otros");
		DESCRIPTION_MAP.put(Mod2002018Key.ID665,"A compensaci\u00F3n de p\u00E9rdidas de ejercicios anteriores");
		DESCRIPTION_MAP.put(Mod2002018Key.ID666,"Total");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.LM1240,"a) Gastos financieros del per\u00EDodo impositivo derivados de deudas por adquisici\u00F3n de particip. afectados por el art. 16.5 y/o 83 LIS (sin signo)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1241,"b) L\u00EDmite adicional a la deducci\u00F3n de gastos financieros (art. 16.5 y/o 83 LIS) (sin signo)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1242,"c1) Gastos financieros del per\u00EDodo impositivo deducibles tras aplicaci\u00F3n l\u00EDmite art. 16.5 y/o 83 LIS (<= [b], [a=c1+c2], >= 0)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1243,"c2) Gastos financieros del per\u00EDodo impositivo no deducibles tras aplicaci\u00F3n l\u00EDmite art. 16.5 y/o 83 LIS (=[a- c1], >= 0)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1244,"d) Gastos financieros pendientes de deducir en periodos anteriores afectados por art. 16.5 y/o 83 LIS, deducibles tras este l\u00EDmite ([b>=c1+d], >= 0]");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1245,"e) Gastos financieros del periodo impositivo no afectados por art. 16.5, 67 b) y/o 83 LIS (sin signo)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1246,"f) Gastos financieros del per\u00EDodo impositivo (= [c1+e])");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1247,"g) Ingresos financieros del per\u00EDodo impositivo derivados de la cesi\u00F3n a terceros de capitales propios");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1248,"h) Gastos financieros netos del per\u00EDodo impositivo (= [f-g])");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1249,"i) L\u00EDmite a la deducci\u00F3n de gastos financieros netos (= 30%* [i1-i2-i3-i4+i5], m\u00EDnimo 1 mill\u00F3n de euros si gasto financiero neto >= 1 mill\u00F3n)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1250,"i1) Resultado de explotaci\u00F3n (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1251,"i2) Amortizaci\u00F3n del inmovilizado (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1252,"i3) Imputaci\u00F3n de subvenciones de inmovilizado no financiero y otras (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1253,"i4) Deterioro y resultado por enajenaciones del inmovilizado (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1254,"i5) Ingresos financieros de participaciones en instrumentos de patrimonio (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1255,"j) Adici\u00F3n por l\u00EDmite beneficio operativo no aplicado en los cinco ejercicios anteriores");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1256,"k1) Gastos financieros netos del per\u00EDodo impositivo deducibles (<= [i+j], [h=k1+k2], >= 0)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1257,"k2) Gastos financieros netos del per\u00EDodo impositivo no deducibles (=[h - k1], <= [h - i], >= 0)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1258,"l) Gastos financieros pendientes de deducir en periodos impositivos anteriores afectados por art. 16.5, y/o 83 LIS deducibles tras aplicar los 2 l\u00EDmites (<= [d], >= 0)");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1259,"m) Gastos financieros netos pendientes de deducir de periodos impositivos anteriores no afectados por art. 16.5 y/o 83 LIS aplicados");
		DESCRIPTION_MAP.put(Mod2002018Key.LM1260,"Total gastos financieros del per\u00EDodo impositivo no deducibles (= [c2+k2])");
		
		DESCRIPTION_MAP.put(Mod2002018Key.LM393,"Importe del cr\u00E9dito exigible");
		DESCRIPTION_MAP.put(Mod2002018Key.LM150,"Opciones: Abono.");
		DESCRIPTION_MAP.put(Mod2002018Key.LM506,"Opciones: Compensaci\u00F3n,");
		
	}
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.CN987,"Importe neto de la cifra de negocios del conjunto de las entidades del grupo");
		DESCRIPTION_MAP.put(Mod2002018Key.CN988,"Importe neto cifra de negocios del conjunto de establecimientos permanentes de la misma persona f\u00EDsica o entidad titular");
		DESCRIPTION_MAP.put(Mod2002018Key.CNEST,"N\u00FAmero de establecimientos permanentes a trav\u00E9s de los que opera, en caso de persona f\u00EDsica titular");
		DESCRIPTION_MAP.put(Mod2002018Key.CN989,"Las entidades que hayan marcado la clave de caracteres de la declaraci\u00F3n [00003], [00004], [00024] \u00F3 [00025] deber\u00E1n consignar a continuaci\u00F3n el importe neto de la cifra de negocios en el ejercicio 2017");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.UT060,"Indique el porcentaje de imputaci\u00F3n de bases imponibles y "
				+ "dem\u00E1s conceptos liquidatorios a las personas o entidades que ostenten los derechos econ\u00F3micos "
				+ "inherentes a la cualidad de socio que sean contribuyentes por el IRPF o del Impuesto sobre "
				+ "Sociedades o socios o empresas miembros residentes en territorio espa\u00F1ol o no residentes "
				+ "con establecimiento permanente");
		DESCRIPTION_MAP.put(Mod2002018Key.UT500 ,"1.- Resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002018Key.UT1227,"2.- Gastos financieros netos no deducidos por la entidad");
		DESCRIPTION_MAP.put(Mod2002018Key.UT1228,"3.- Reserva de capitalizaci\u00F3n no aplicada por la entidad");
		DESCRIPTION_MAP.put(Mod2002018Key.UT552 ,"4.- Base imponible");
		DESCRIPTION_MAP.put(Mod2002018Key.UT1330,"5.- Base imponible minorada o incrementada, en su caso, en las cantidades derivadas de la aplicaci\u00F3n de la reserva de nivelaci\u00F3n (entidades de reducida dimensi\u00F3n)");
		DESCRIPTION_MAP.put(Mod2002018Key.UTC01	,"7.- Base de las bonificaciones");
		DESCRIPTION_MAP.put(Mod2002018Key.UTC02 ,"a) Base total (excepto base de deducci\u00F3n por inversiones en elementos del inmovilizado material nuevos)");
		DESCRIPTION_MAP.put(Mod2002018Key.UTC03 ,"b) Base de deducci\u00F3n por inversiones en elementos del inmovilizado material nuevos");
		DESCRIPTION_MAP.put(Mod2002018Key.UT062 ,"9.- Retenciones e ingresos a cuenta");
		DESCRIPTION_MAP.put(Mod2002018Key.UTC04 ,"a) De ejercicios en los que la sociedad no haya tributado en el r\u00E9gimen especial");
		DESCRIPTION_MAP.put(Mod2002018Key.UTC05 ,"b) De ejercicios en los que la sociedad haya tributado en el r\u00E9gimen especial");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002018Key.TR050,"Volumen de las operaciones realizadas en:Volumen total de las operaciones realizadas por la entidad en el ejercicio (incluidas las operaciones realizadas en el extranjero)");
		DESCRIPTION_MAP.put(Mod2002018Key.TR051,"Volumen de las operaciones realizadas en el extranjero durante el ejercicio");
		DESCRIPTION_MAP.put(Mod2002018Key.TR052,"ARABA");
		DESCRIPTION_MAP.put(Mod2002018Key.TR053,"GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002018Key.TR054,"BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002018Key.TR055,"NAVARRA");
		DESCRIPTION_MAP.put(Mod2002018Key.TR056,"Territorio com\u00FAn");
		DESCRIPTION_MAP.put(Mod2002018Key.TR626,"Diputaci\u00F3n Foral de ARABA: 052 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002018Key.TR627,"Diputaci\u00F3n Foral de GIPUZKOA: 053 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002018Key.TR628,"Diputaci\u00F3n Foral de BIZKAIA: 054 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002018Key.TR629,"Diputaci\u00F3n Foral de NAVARRA: 055 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002018Key.TR625,"Administraci\u00F3n del Estado: 056 / ( 050 - 051 ) x 100");
		
		DESCRIPTION_MAP.put(Mod2002018Key.POR51,"Suma de  porcentajes de participaci\u00F3n de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado");
		DESCRIPTION_MAP.put(Mod2002018Key.PORES,"Suma de porcentajes de participaciones en situaciones especiales");
	}
}
