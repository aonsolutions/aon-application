package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.util.EnumMap;

public class Mod2002015Description {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	public static EnumMap<Mod2002015Key,String> DESCRIPTION_MAP = new EnumMap<Mod2002015Key,String>(Mod2002015Key.class);

	static {		
		DESCRIPTION_MAP.put(Mod2002015Key.X0000, "tipo de ejercicio");
		
		DESCRIPTION_MAP.put(Mod2002015Key.C0001,"Entidad sin \u00E1nimo de lucro acogida r\u00E9gimen fiscal T\u00EDtulo II Ley 49/2002");
		DESCRIPTION_MAP.put(Mod2002015Key.C0002,"Entidad parcialmente exenta");
		DESCRIPTION_MAP.put(Mod2002015Key.C0003,"Sociedad de inversi\u00F3n de capital variable o fondo de inversi\u00F3n de car\u00E1cter financiero");
		DESCRIPTION_MAP.put(Mod2002015Key.C0004,"Sociedad de inversi\u00F3n inmobiliaria o fondo de inversi\u00F3n inmobiliaria");
		DESCRIPTION_MAP.put(Mod2002015Key.C0005,"Comunidades titulares de montes vecinales en mano com\u00FAn");
		DESCRIPTION_MAP.put(Mod2002015Key.C0011,"Entidad de tenencia de valores extranjeros");
		DESCRIPTION_MAP.put(Mod2002015Key.C0013,"Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico espa\u00F1ola o Uni\u00F3n temporal de empresas");
		DESCRIPTION_MAP.put(Mod2002015Key.C0014,"Agrupaci\u00F3n europea de inter\u00E9s econ\u00F3mico");
		DESCRIPTION_MAP.put(Mod2002015Key.C0017,"Cooperativa protegida");
		DESCRIPTION_MAP.put(Mod2002015Key.C0018,"Cooperativa especialmente protegida"); 
		DESCRIPTION_MAP.put(Mod2002015Key.C0019,"Resto cooperativas");
		DESCRIPTION_MAP.put(Mod2002015Key.C0021,"Establecimiento permanente"); 
		DESCRIPTION_MAP.put(Mod2002015Key.C0023,"Gran empresa");
		DESCRIPTION_MAP.put(Mod2002015Key.C0024,"Entidad de cr\u00E9dito"); 
		DESCRIPTION_MAP.put(Mod2002015Key.C0025,"Entidad aseguradora");
		DESCRIPTION_MAP.put(Mod2002015Key.C0031,"Entidades de capital-riesgo");
		DESCRIPTION_MAP.put(Mod2002015Key.C0032,"Sociedad de desarrollo industrial regional");
		DESCRIPTION_MAP.put(Mod2002015Key.C0036,"Fondo de pensiones Real Decreto Legislativo 1/2002, de 29 de noviembre");
		DESCRIPTION_MAP.put(Mod2002015Key.C0048,"Sociedad de garant\u00EDa rec\u00EDproca o de reafianzamiento");
		DESCRIPTION_MAP.put(Mod2002015Key.C0058,"Mutua de seguros o Mutualidad de previsi\u00F3n social");
		DESCRIPTION_MAP.put(Mod2002015Key.C0060,"Fondos o activos de titulizaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002015Key.C0066,"Entidad patrimonial");
		
		DESCRIPTION_MAP.put(Mod2002015Key.C0006,"Incentivos entidad de reducida dimensi\u00F3n (Cap. XI, T\u00EDt. VII LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0015,"Entidad ZEC");
		DESCRIPTION_MAP.put(Mod2002015Key.C0022,"R\u00E9gimen entid. navieras en funci\u00F3n del tonelaje");
		DESCRIPTION_MAP.put(Mod2002015Key.C0028,"Tribut. conjunta Estado/Diput. Cdad. Forales");		
		DESCRIPTION_MAP.put(Mod2002015Key.C0047,"Entidades sometidas a la normativa foral");		
		DESCRIPTION_MAP.put(Mod2002015Key.C0035,"Aplicaci\u00F3n r\u00E9g. especial fusiones, escisiones, aportaciones y canjes valores (Cap.VII, T\u00EDt.VII)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0049,"Reg\u00EDmenes especiales de normativa foral");
		DESCRIPTION_MAP.put(Mod2002015Key.C0029,"R\u00E9gimen especial Canarias");
		DESCRIPTION_MAP.put(Mod2002015Key.C0033,"R\u00E9gimen especial miner\u00EDa");
		DESCRIPTION_MAP.put(Mod2002015Key.C0034,"R\u00E9gimen especial hidrocarburos");		
		DESCRIPTION_MAP.put(Mod2002015Key.C0038,"Entidad dedicada al arrend. de viviendas");
		DESCRIPTION_MAP.put(Mod2002015Key.C0046,"Entidad en r\u00E9g. de atribuci\u00F3n de rentas constitu\u00EDda en el extranjero con presencia en territorio espa\u00F1ol");
		DESCRIPTION_MAP.put(Mod2002015Key.C0012,"SOCIMI");
		DESCRIPTION_MAP.put(Mod2002015Key.C0064,"R\u00E9gimen fiscal entrada SOCIMI");
		DESCRIPTION_MAP.put(Mod2002015Key.C0057,"R\u00E9gimen fiscal salida SOCIMI");
		DESCRIPTION_MAP.put(Mod2002015Key.C0020,"Otros reg\u00EDmenes especiales");
		DESCRIPTION_MAP.put(Mod2002015Key.C0062,"R\u00E9g. fiscal de operaciones de aportaci\u00F3n de activos a sociedades para la gesti\u00F3n de activos (Ley 8/2012)");
		
		DESCRIPTION_MAP.put(Mod2002015Key.C0056,"Tipo gravamen reducido mant. o creaci\u00F3n empleo (DT 22 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0007,"Inclusi\u00F3n en base imp. rentas positivas art. 100.8 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0009,"Sociedad dominante de grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002015Key.C0010,"Sociedad dependiente de grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002015Key.C0016,"Opci\u00F3n art. 46.2 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0026,"Entidad inactiva");
		DESCRIPTION_MAP.put(Mod2002015Key.C0027,"Base imponible negativa o cero");
		DESCRIPTION_MAP.put(Mod2002015Key.C0030,"Transmisi\u00F3n elementos patrimoniales arts. 27.2.d) y 77.1 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0039,"Entidad que forma parte de un grupo mercantil (art. 42 del C\u00F3d. Comercio)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0067,"Obligaci\u00F3n informaci\u00F3n art. 14 RIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0068,"Obligaci\u00F3n informaci\u00F3n art. 15 RIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0043,"Obligaci\u00F3n informacion art.16 RIS importe CN>=45 mill. euros");
		DESCRIPTION_MAP.put(Mod2002015Key.C0069,"Obligaci\u00F3n informacion art.16 RIS importe CN<45 mill. euros");		
		DESCRIPTION_MAP.put(Mod2002015Key.C0045,"Inversiones anticipadas-reserva inversiones en Canarias (art. 27.11 Ley 19/1994)");		 
		DESCRIPTION_MAP.put(Mod2002015Key.C0063,"Tipo gravamen reducido para entidades de nueva creaci\u00F3n (DT 22a LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0071,"Tipo gravamen reducido para entidades de nueva creaci\u00F3n (art. 29.1 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.C0059,"Opci\u00F3n art. 39.2 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.C0065,"Bonificaci\u00F3n personal investigador (R.D. 475/2014)");
		
		DESCRIPTION_MAP.put(Mod2002015Key.C0050,"Balance y ECPN. Normal");
		DESCRIPTION_MAP.put(Mod2002015Key.C0051,"Balance y ECPN. Abreviado");
		DESCRIPTION_MAP.put(Mod2002015Key.C0052,"Balance y ECPN. PYMES");
		DESCRIPTION_MAP.put(Mod2002015Key.C0053,"Balance y ECPN. Normal");
		DESCRIPTION_MAP.put(Mod2002015Key.C0054,"Balance y ECPN. Abreviado");
		DESCRIPTION_MAP.put(Mod2002015Key.C0055,"Balance y ECPN. PYMES");
		DESCRIPTION_MAP.put(Mod2002015Key.C0061,"Estados de cuentas de Instituciones de inversi\u00F3n colectiva");
		
		DESCRIPTION_MAP.put(Mod2002015Key.C0041,"Personal fijo");
		DESCRIPTION_MAP.put(Mod2002015Key.C0042,"Personal no fijo");
		
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.BA101,"ACTIVO NO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA102,"Inmovilizado intangible (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA103,"Desarrollo (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA104,"Concesiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA105,"Patentes, licencias, marcas y similares (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA106,"Fondo de comercio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA107,"Aplicaciones inform\u00E1ticas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA108,"Investigaci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA700,"Propiedad intelectual (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA701,"Derechos de emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA109,"Otro inmovilizado intangible (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA110,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA111,"Inmovilizado material (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA112,"Terrenos y construcciones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA113,"Instalaciones t\u00E9cnicas y otro inmovilizado material (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA114,"Inmovilizado en curso y anticipos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA115,"Inversiones inmobiliarias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA116,"Terrenos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA117,"Construcciones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA118,"Inversiones en empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA119,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA120,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA121,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA122,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA123,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA124,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA125,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA126,"Inversiones financieras a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA127,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA128,"Cr\u00E9ditos a terceros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA129,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA130,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA131,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA132,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA133,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA134,"Activos por impuesto diferido (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA135,"Deudores comerciales no corrientes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA136,"ACTIVO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA137,"Activos no corrientes mantenidos para la venta (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA138,"Existencias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA139,"Comerciales (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA140,"Materias primas y otros aprovisionamientos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA141,"Productos en curso (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA142,"De ciclo largo de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA143,"De ciclo corto de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA144,"Productos terminados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA145,"De ciclo largo de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA146,"De ciclo corto de producci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA147,"Subproductos, residuos y materiales recuperados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA148,"Anticipos a proveedores (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA149,"Deudores comerciales y otras cuentas a cobrar (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA150,"Clientes por ventas y prestaciones de servicios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA151,"Clientes por ventas y prestaciones de servicios a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA152,"Clientes por ventas y prestaciones de servicios a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA153,"Clientes empresas del grupo y asociadas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA154,"Deudores varios (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA155,"Personal (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA156,"Activos por impuesto corriente (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA157,"Otros cr\u00E9ditos con las Administraciones p\u00FAblicas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA158,"Accionistas (socios) por desembolsos exigidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA159,"Otros deudores (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA160,"Inversiones en empresas del grupo y asociadas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA161,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA162,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA163,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA164,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA165,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA166,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA167,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA168,"Inversiones financieras a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA169,"Instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA170,"Cr\u00E9ditos a empresas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA171,"Valores representativos de deuda (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA172,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA173,"Otros activos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA174,"Otras inversiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA175,"Resto (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA176,"Periodificaciones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA177,"Efectivo y otros activos l\u00EDquidos equivalentes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA178,"Tesorer\u00EDa (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA179,"Otros activos l\u00EDquidos equivalentes (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BA180,"TOTAL ACTIVO (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.BP185,"PATRIMONIO NETO (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP186,"Fondos propios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP187,"Capital (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP188,"Capital escriturado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP189,"(Capital no exigido) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP190,"Prima de emisi\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP191,"Reservas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP192,"Legal y estatutarias (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP193,"Otras reservas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP702,"Reserva de revalorizaci\u00F3n (Ley 16/2012 de 27 de diciembre) (N)");		
		DESCRIPTION_MAP.put(Mod2002015Key.BP1001,"Reserva de capitalizaci\u00F3n (N,A,P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP1002,"Reserva de nivelaci\u00F3n (N,A,P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP194,"(Acciones y participaciones en patrimonio propias) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP195,"Resultados de ejercicios anteriores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP196,"Remanente (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP197,"(Resultados negativos de ejercicios anteriores) (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP198,"Otras aportaciones	 de socios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP199,"Resultado del ejercicio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP200,"(Dividendo a cuenta) (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP201,"Otros instrumentos de patrimonio neto (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP202,"Ajustes por cambio de valor (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP203,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP204,"Operaciones de cobertura (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP205,"Activos no corrientes y pasivos vinculados, mantenidos para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP206,"Diferencia de conversi\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP207,"Otros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP208,"Ajustes en patrimonio neto (P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP209,"Subvenciones, donaciones y legados recibidos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP210,"PASIVO NO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP211,"Provisiones a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP212,"Obligaciones por prestaciones a largo plazo al personal (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP213,"Actuaciones medioambientales (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP214,"Provisiones por reestructuraci\u00F3n (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP215,"Otras provisiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP216,"Deudas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP217,"Obligaciones y otros valores negociables (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP218,"Deudas con entidades de cr\u00E9dito (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP219,"Acreedores por arrendamiento financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP220,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP221,"Otros pasivos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP222,"Otras deudas a largo plazo (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP223,"Deudas con empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP224,"Pasivos por impuesto diferido (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP225,"Periodificaciones a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP226,"Acreedores comerciales no corrientes (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP227,"Deuda con caracter\u00EDsticas especiales a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP228,"PASIVO CORRIENTE (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP229,"Pasivos vinculados con activos no corr. mantenidos para la venta(N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP230,"Provisiones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP703,"Provisiones por derechos de emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP704,"Otras provisiones (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP231,"Deudas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP232,"Obligaciones y otros valores negociables (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP233,"Deudas con entidades de cr\u00E9dito (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP234,"Acreedores por arrendamiento financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP235,"Derivados (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP236,"Otros pasivos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP237,"Otras deudas a corto plazo (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP238,"Deudas con empresas del grupo y asociadas a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP239,"Acreedores comerciales y otras cuentas a pagar (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP240,"Proveedores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP241,"Proveedores a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP242,"Proveedores a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP243,"Proveedores, empresas del grupo y asociadas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP244,"Acreedores varios (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP245,"Personal (remuneraciones pendientes de pago) (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP246,"Pasivos por impuesto corriente (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP247,"Otras deudas con las Administraciones p\u00FAblicas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP248,"Anticipos de clientes (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP249,"Otros acreedores (A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP250,"Periodificaciones a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP251,"Deuda con caracter\u00EDsticas especiales a corto plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.BP252,"TOTAL PATRIMONIO NETO Y PASIVO (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.PG255,"Importe neto de la cifra de negocios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG256,"Ventas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG257,"Prestaciones de servicios (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG705,"Ingresos de car\u00E1cter financiero de las sociedades holding (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG706,"De participaciones en instrumentos de patrimonio (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG707,"De valores negociables y otros instrumentos financieros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG708,"Resto (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG258,"Variaci\u00F3n de existencias de productos terminados y en curso de fabricaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG259,"Trabajos realizados por la empresa para su activo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG260,"Aprovisionamientos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG261,"Consumo de mercader\u00EDas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG760,"Compras de mercader\u00EDas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG761,"Variaci\u00F3n de existencias (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG262,"Consumo de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG762,"Compras de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG763,"Variaci\u00F3n de materias primas y otras materias consumibles (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG263,"Trabajos realizados por otras empresas (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG264,"Deterioro de mercader\u00EDas, materias primas y otros aprovisionamientos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG265,"Otros ingresos de explotaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG266,"Ingresos accesorios y otros de gesti\u00F3n corriente (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG267,"Ingresos por arrendamientos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG268,"Resto (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG269,"Subvenciones de explotaci\u00F3n incorporadas al resultado del ejercicio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG270,"Gastos de personal (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG271,"Sueldos y salarios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG273,"Indemnizaciones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG274,"Seguridad Social a cargo de la empresa (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG275,"Retribuciones a largo plazo mediante sistemas de aportaciones o prestaci\u00F3n definida (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG276,"Retribuciones mediante instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG277,"Otros gastos sociales (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG278,"Provisiones (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG279,"Otros gastos de explotaci\u00F3n (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG280,"Servicios exteriores (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG281,"Tributos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG282,"P\u00E9rdidas, deterioro y variaci\u00F3n de provisiones por operaciones comerciales (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG283,"Otros gastos de gesti\u00F3n corriente (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG709,"Gastos por emisi\u00F3n de gases de efecto invernadero (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG284,"Amortizaci\u00F3n del inmovilizado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG285,"Imputaci\u00F3n de subvenciones de inmovilizado no financiero y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG286,"Excesos de provisiones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG287,"Deterioro y resultado por enajenaciones del inmovilizado (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG288,"Deterioro y p\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG289,"Deterioros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG290,"Reversi\u00F3n de deterioros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG291,"Resultados por enajenaciones y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG292,"Beneficios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG293,"P\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG710,"Deterioro y resultados por enajenaciones del inmovilizado de las sociedades holding (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG294,"Diferencia negativa de combinaciones de negocio (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG295,"Otros resultados (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG296,"RESULTADO DE EXPLOTACI\u00D3N (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG297,"Ingresos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG298,"De participaciones en instrumentos de patrimonio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG299,"En empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG300,"En terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG301,"De valores negociables y otros instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG302,"De empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG303,"De terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG304,"Imputaci\u00F3n de subvenciones, donaciones y legados de car\u00E1cter financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG305,"Gastos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG306,"Por deudas con empresas del grupo y asociadas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG307,"Por deudas con terceros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG308,"Por actualizaci\u00F3n de provisiones (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG309,"Variaci\u00F3n del valor razonable en instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG310,"Cartera de negociaci\u00F3n y otros (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG311,"Imputaci\u00F3n al resultado del ejercicio por activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG312,"Diferencias de cambio (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG313,"Deterioro y resultado por enajenaci\u00F3n de instrumentos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG314,"Deterioros y p\u00E9rdidas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG315,"Deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG316,"Deterioros, Otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG317,"Reversi\u00F3n de deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG318,"Reversi\u00F3n de deterioros, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG319,"Resultados por enajenaci\u00F3n y otras (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG320,"Beneficios, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG321,"Beneficios, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG322,"P\u00E9rdidas, empresas del grupo y asociadas a largo plazo (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG323,"P\u00E9rdidas, otras empresas (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG329,"Otros ingresos y gastos de car\u00E1cter financiero (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG330,"Incorporaci\u00F3n al activo de gastos financieros (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG331,"Ingresos financieros derivados de convenios de acreedores (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG332,"Resto de ingresos y gastos (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG324,"RESULTADO FINANCIERO (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG325,"RESULTADO ANTES DE IMPUESTOS (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG326,"Impuestos sobre beneficios (N, A, P)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG327,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS (N, A, P) (2)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG328,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.PG500,"RESULTADO DE LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS (N, A, P)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.T0500,"RESULTADO DE LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0336,"Por valoraci\u00F3n de instrumentos financieros (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0337,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0338,"Otros ingresos/gastos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0339,"Por coberturas de flujos de efectivo (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0340,"Subvenciones, donaciones y legados recibidos (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0341,"Por ganancias y p\u00E9rdidas actuariales y otros ajustes (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0342,"Por activos no corrientes y pasivos vinculados, mantenidos pra la venta (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0343,"Diferencias de conversi\u00F3n (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0344,"Efecto impositivo (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0345,"Total ingresos y gastos imputados directamente en el patrimonio neto (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0346,"Por valoraci\u00F3n de instrumentos financieros (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0347,"Activos financieros disponibles para la venta (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0348,"Otros ingresos/gastos (N)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0349,"Por coberturas de flujo de efectivo (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0350,"Subvenciones, donaciones y legados recibidos (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0351,"Por activos no corrientes y pasivos vinculados, mantenidos para la venta (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0352,"Diferencias de conversi\u00F3n (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0353,"Efecto impositivo (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0354,"Total transferencias a la cuenta de p\u00E9rdidas y ganancias (N, A)");
		DESCRIPTION_MAP.put(Mod2002015Key.T0355,"TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (N, A)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.LQ500,"Resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ301,"Correcciones por Impuesto sobre Sociedades. Aumentos.");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ302,"Correcciones por Impuesto sobre Sociedades. Disminuciones.");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ501,"Resultado de la cuenta de p\u00E9rdidas y ganancias antes de Impuesto sobre Sociedades");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1230,"Correcciones al resultado contable al considerar los requisitos o calificaciones contables referidos al grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1231,"Correcciones al resultado contable al considerar los requisitos o calificaciones contables referidos al grupo fiscal");
		DESCRIPTION_MAP.put(Mod2002015Key.I0417,"Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002015Key.D0418,"Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.LQ578,"Base imponible de actividades o rentas que tributen en r\u00E9gimen general");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ579,"Base imponible derivada de la aplicaci\u00F3n del r\u00E9gimen especial");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ0N1,"N\u00BA de buques a los que se aplica el r\u00E9gimen");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ630,"Base imponible resultante de aplicar la escala del apartado 1 del art. 114 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ631,"Importe de rentas generadas en transmisiones de buques (reserva, diferencia entre la amortizaci\u00F3n fiscal y la contable)");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ632,"Compensaci\u00F3n de bases imponibles negativas de per\u00EDodos anteriores (a compensar \u00FAnicamente con la casilla 631)");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1029,"Base imponible individual a integrar por las entidades que forman parte del grupo");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1030,"Eliminaciones e incorporaciones correspondientes a la entidad");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1031,"Integraci\u00F3n individual de las dotaciones del art. 11.12 LIS");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ550,"Base imponible antes de la compensaci\u00F3n de bases imponibles negativas");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1032,"Reserva de capitalizaci\u00F3n");		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ547,"Compensaci\u00F3n de bases imponibles negativas de per\u00EDodos anteriores");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ552,"Base imponible");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1033,"Reserva de nivelaci\u00F3n (Aumentos)");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1034,"Reserva de nivelaci\u00F3n (Disminuciones)");		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1330,"Base imponible despu\u00E9s de la reserva de nivelaci\u00F3n");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ553,"Resultados cooperativos");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ554,"Resultados extracooperativos");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ555,"Socios residentes");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ556,"Socios no residentes");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ559,"Base imponible a tipo de gravamen especial: actividades sector industrial");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1035,"Base imponible a tipo de gravamen especial: resto de actividades");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ520,"Parte de la base imponible del periodo impositivo que tributa al tipos general");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ521,"Parte de la base imponible del periodo impositivo que tributa al tipo del 0%");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ545,"Rentas correspondientes a quitas por acuerdo con acreedores no vinculados (art. 2. Segundo. Dos Ley 16/2013, de 29 de octubre)");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ593,"Rentas correspondientes a quitas por acuerdo con acreedores no vinculados de cooperativas (a nivel de cuota)");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ558,"Tipo de gravamen");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LQ560,"Cuota \u00EDntegra previa");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ210,"P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2) LIS a los que se refi ere el art. 11.12 LIS (convertida en cuota). Aumentos");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ480,"P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2) LIS a los que se refi ere el art. 11.12 LIS (convertida en cuota). Disminuciones");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ408 ,"Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS). Aumentos");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1037,"Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS). Disminuciones");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ561 ,"Compensaci\u00F3n de cuotas por p\u00E9rdidas de cooperativas");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1285,"Reserva de nivelaci\u00F3n convertido en cuotas (s\u00F3lo entidades del art. 101 LIS). Aumentos");	
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1286,"Reserva de nivelaci\u00F3n convertido en cuotas (s\u00F3lo entidades del art. 101 LIS). Disminuciones");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1331,"Cuota \u00EDntegra previa despu\u00E9s de la reserva de nivelaci\u00F3n");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ562 ,"Cuota \u00EDntegra");
		DESCRIPTION_MAP.put(Mod2002015Key.LQ1038,"Incremento por incumplimiento reserva de nivelaci\u00F3n (art. 105.6 LIS)");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.BN567,"Bonificaci\u00F3n por rentas obtenidas en Ceuta y Melilla (art. 33 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN568,"Bonificaciones por prestaci\u00F3n de servicios (art. 34 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN563,"Bonificaci\u00F3n rendimientos por ventas bienes corporales producidos en Canarias (art. 26 Ley 19/1994)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN566,"Bonificaciones Sociedades Cooperativas (Ley 20/1990)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN576,"Bonificaciones entidades dedicadas al arrendamiento de viviendas (Cap\u00EDtulo III T\u00EDtulo VII LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN569,"Otras bonificaciones");
		

		DESCRIPTION_MAP.put(Mod2002015Key.BN570,"DI interna de per\u00EDodos anteriores aplicada en el ejercicio (art. 30 RDL 4/2004)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN572,"DI internacional de per\u00EDodos anteriores aplicada en el ejercicio (art. 31 y 32 RDL 4/2004)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN571,"DI internacional de per\u00EDodos anteriores aplicada en el ejercicio (art. 31 y 32 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN573,"DI internacional generada y aplicada en el ejercicio actual (arts. 31 y 32 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN575,"Transparencia fi scal internacional (art. 100.11 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN577,"DI interna intersocietaria al 5/10% (cooperativas)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN581,"Bonificaciones empresas navieras en Canarias (art. 76 Ley 19/1994)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN582,"Cuota \u00EDntegra ajustada positiva");
		
		DESCRIPTION_MAP.put(Mod2002015Key.BN583,"Apoyo fiscal a la inversi\u00F3n y otras deducciones");
		DESCRIPTION_MAP.put(Mod2002015Key.BN585,"Deducci\u00F3n DT 24\u00AA.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95");
		DESCRIPTION_MAP.put(Mod2002015Key.BN584,"Deducciones DT 24\u00AA.1 LIS y DT 8\u00AA RDL 4/2004");
		DESCRIPTION_MAP.put(Mod2002015Key.BN588,"Deducciones con l\u00EDmite del Cap\u00EDtulo IV T\u00EDtulo VI RDL 4/2004 y LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1039,"Deducciones por producciones cinematogr\u00E1fi cas extranjeras (art. 36.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN565,"Deducci\u00F3n donaciones a entidades sin fines de lucro (Ley 49/2002)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN590,"Deducciones Inversi\u00F3n Canarias (Ley 20/1991)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN399,"Deducciones espec\u00EDficas de las entidades sometidas a normativa foral");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1040,"Deducci\u00F3n por reversi\u00F3n de medidas temporales DT 37\u00AA.1 LIS");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1041,"Deducci\u00F3n por reversi\u00F3n de medidas temporales DT 37\u00AA.2 LIS");
				
		DESCRIPTION_MAP.put(Mod2002015Key.BN082,"Deducciones sin l\u00EDmite I + D + i");
		DESCRIPTION_MAP.put(Mod2002015Key.BN592,"Cuota l\u00EDquida positiva");
		
		DESCRIPTION_MAP.put(Mod2002015Key.BN595,"Retenciones e ingresos a cuenta / pagos a cuenta participaciones I.I.C.");
		DESCRIPTION_MAP.put(Mod2002015Key.BN596,"Retenciones e ingresos a cuenta / pagos a cuenta participaciones I.I.C. imputados por agrupaciones de inter\u00E9s econ\u00F3mico y uniones temporales de empresas");
		DESCRIPTION_MAP.put(Mod2002015Key.BN597,"Retenciones sobre los premios de determinadas loter\u00EDas y apuestas");
		DESCRIPTION_MAP.put(Mod2002015Key.BN083,"Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 44.2 RDL 4/2004 y DT 24\u00AA.7 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1042,"Abono de deducciones por producciones extranjeras (art. 36.2 LIS)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN599,"Cuota del ejercicio a ingresar o a devolver (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN600,"Cuota del ejercicio a ingresar o a devolver (D. Forales / Navarra)");
		
		DESCRIPTION_MAP.put(Mod2002015Key.BN601,"1er pago fraccionado (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN602,"1er pago fraccionado (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN603,"2o pago fraccionado (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN604,"2o pago fraccionado (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN605,"3er pago fraccionado (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN606,"3er pago fraccionado (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN611,"Cuota diferencial (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN612,"Cuota diferencial (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN615,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN616,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN633,"Incremento por incumplimiento de requisitos SOCIMI (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN642,"Incremento por incumplimiento de requisitos SOCIMI (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN617,"Intereses de demora (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN618,"Intereses de demora (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN619,"Importe ingreso / devoluci\u00F3n efectuada de la declaraci\u00F3n originaria (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN620,"Importe ingreso / devoluci\u00F3n efectuada de la declaraci\u00F3n originaria (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1200,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS). TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1042,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS). (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1333,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS). (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN621,"L\u00EDquido a ingresar o a devolver (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN622,"L\u00EDquido a ingresar o a devolver (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1020,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS) (Estado)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1043,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS) (D. Forales / Navarra)");
		DESCRIPTION_MAP.put(Mod2002015Key.BN1021,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS) (Estado)");		
		DESCRIPTION_MAP.put(Mod2002015Key.BN1044,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS) (D. Forales / Navarra)");
	}
	
	static {
		// Aplicación de resultados - Base de reparto 
		DESCRIPTION_MAP.put(Mod2002015Key.ID650,"P\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(Mod2002015Key.ID651,"Remanente");
		DESCRIPTION_MAP.put(Mod2002015Key.ID652,"Reservas");
		DESCRIPTION_MAP.put(Mod2002015Key.ID653,"Total");
		
		// Aplicación de resultados - Aplicación
		DESCRIPTION_MAP.put(Mod2002015Key.ID654,"A reservas");
		DESCRIPTION_MAP.put(Mod2002015Key.ID655,"Intereses aportaciones al capital (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002015Key.ID656,"A dividendos");
		DESCRIPTION_MAP.put(Mod2002015Key.ID658,"A dotaci\u00F3n O.S. (Cajas de ahorro)");
		DESCRIPTION_MAP.put(Mod2002015Key.ID659,"A F.R.O. y dotaciones voluntarias al F.E.P. (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002015Key.ID660,"A retornos cooperativos (Cooperativas)");
		DESCRIPTION_MAP.put(Mod2002015Key.ID662,"Part\u00EDcipes (IIC)");
		DESCRIPTION_MAP.put(Mod2002015Key.ID664,"A remanente y otros");
		DESCRIPTION_MAP.put(Mod2002015Key.ID665,"A compensaci\u00F3n de p\u00E9rdidas de ejercicios anteriores");
		DESCRIPTION_MAP.put(Mod2002015Key.ID666,"Total");
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.LM1240,"a) Gastos financieros del per\u00EDodo impositivo derivados de deudas por adquisici\u00F3n de particip. afectados por el art. 16.5 y/o 83 LIS (sin signo)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1241,"b) L\u00EDmite adicional a la deducci\u00F3n de gastos financieros (art. 16.5 y/o 83 LIS) (sin signo)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1242,"c1) Gastos financieros del per\u00EDodo impositivo deducibles tras aplicaci\u00F3n l\u00EDmite art. 16.5 y/o 83 LIS (<= [b], [a=c1+c2], >= 0)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1243,"c2) Gastos financieros del per\u00EDodo impositivo no deducibles tras aplicaci\u00F3n l\u00EDmite art. 16.5 y/o 83 LIS (=[a- c1], >= 0)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1244,"d) Gastos financieros pendientes de deducir en periodos anteriores afectados por art. 16.5 y/o 83 LIS, deducibles tras este l\u00EDmite ([b>=c1+d], >= 0]");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1245,"e) Gastos financieros del periodo impositivo no afectados por art. 16.5, 67 b) y/o 83 LIS (sin signo)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1246,"f) Gastos financieros del per\u00EDodo impositivo (= [c1+e])");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1247,"g) Ingresos financieros del per\u00EDodo impositivo derivados de la cesi\u00F3n a terceros de capitales propios");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1248,"h) Gastos financieros netos del per\u00EDodo impositivo (= [f-g])");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1249,"i) L\u00EDmite a la deducci\u00F3n de gastos financieros netos (= 30%* [i1-i2-i3-i4+i5], m\u00EDnimo 1 mill\u00F3n de euros si gasto financiero neto >= 1 mill\u00F3n)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1250,"i1) Resultado de explotaci\u00F3n (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1251,"i2) Amortizaci\u00F3n del inmovilizado (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1252,"i3) Imputaci\u00F3n de subvenciones de inmovilizado no fi nanciero y otras (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1253,"i4) Deterioro y resultado por enajenaciones del inmovilizado (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1254,"i5) Ingresos financieros de participaciones en instrumentos de patrimonio (signo igual a Cuenta de P\u00E9rd. y Gan.)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1255,"j) Adici\u00F3n por l\u00EDmite beneficio operativo no aplicado en los cinco ejercicios anteriores");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1256,"k1) Gastos financieros netos del per\u00EDodo impositivo deducibles (<= [i+j], [h=k1+k2], >= 0)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1257,"k2) Gastos financieros netos del per\u00EDodo impositivo no deducibles (=[h - k1], <= [h - i], >= 0)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1258,"l) Gastos financieros pendientes de deducir en periodos impositivos anteriores afectados por art. 16.5, y/o 83 LIS deducibles tras aplicar los 2 l\u00EDmites (<= [d], >= 0)");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1259,"m) Gastos financieros netos pendientes de deducir de periodos impositivos anteriores no afectados por art. 16.5 y/o 83 LIS aplicados");
		DESCRIPTION_MAP.put(Mod2002015Key.LM1260,"Total gastos financieros del per\u00EDodo impositivo no deducibles (= [c2+k2])");
		
		DESCRIPTION_MAP.put(Mod2002015Key.LM393,"Importe del cr\u00E9dito exigible");
		DESCRIPTION_MAP.put(Mod2002015Key.LM150,"Opciones: Abono.");
		DESCRIPTION_MAP.put(Mod2002015Key.LM506,"Opciones: Compensaci\u00F3n,");
		
	}
	
	static {
		DESCRIPTION_MAP.put(Mod2002015Key.TR050,"Volumen de las operaciones realizadas en:Volumen total de las operaciones realizadas por la entidad en el ejercicio (incluidas las operaciones realizadas en el extranjero)");
		DESCRIPTION_MAP.put(Mod2002015Key.TR051,"Volumen de las operaciones realizadas en el extranjero durante el ejercicio");
		DESCRIPTION_MAP.put(Mod2002015Key.TR052,"ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR053,"GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR054,"BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR055,"NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR056,"Territorio com\u00FAn");
		DESCRIPTION_MAP.put(Mod2002015Key.TR626,"Diputaci\u00F3n Foral de ARABA: 052 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002015Key.TR627,"Diputaci\u00F3n Foral de GIPUZKOA: 053 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002015Key.TR628,"Diputaci\u00F3n Foral de BIZKAIA: 054 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002015Key.TR629,"Diputaci\u00F3n Foral de NAVARRA: 055 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002015Key.TR625,"Administraci\u00F3n del Estado: 056 / ( 050 - 051 ) x 100");
		DESCRIPTION_MAP.put(Mod2002015Key.TR420,"Cuota del ejercicio a ingresar o a devolver. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR421,"Cuota del ejercicio a ingresar o a devolver. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR426,"Cuota del ejercicio a ingresar o a devolver. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR427,"Cuota del ejercicio a ingresar o a devolver. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR600,"Cuota del ejercicio a ingresar o a devolver. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR402,"1\u00BA Pago fraccionados 1\u00BA. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR442,"1\u00BA Pago fraccionados 1\u00BA. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR443,"1\u00BA Pago fraccionados 1\u00BA. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR444,"1\u00BA Pago fraccionados 1\u00BA. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR602,"1\u00BA Pago fraccionados 1\u00BA. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR445,"2\u00BA Pago fraccionados 2\u00BA. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR446,"2\u00BA Pago fraccionados 2\u00BA. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR447,"2\u00BA Pago fraccionados 2\u00BA. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR448,"2\u00BA Pago fraccionados 2\u00BA. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR604,"2\u00BA Pago fraccionados 2\u00BA. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR449,"3\u00BA Pago fraccionados 3\u00BA. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR451,"3\u00BA Pago fraccionados 3\u00BA. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR450,"3\u00BA Pago fraccionados 3\u00BA. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR465,"3\u00BA Pago fraccionados 3\u00BA. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR606,"3\u00BA Pago fraccionados 3\u00BA. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR474,"Cuota diferencial. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR475,"Cuota diferencial. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR476,"Cuota diferencial. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR477,"Cuota diferencial. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR612,"Cuota diferencial. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR482,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores.ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR483,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores.GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR484,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores.BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR485,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores.NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR616,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores.TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR913,"Incremento por incumplimiento de requisitos SOCIMI. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR914,"Incremento por incumplimiento de requisitos SOCIMI. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR915,"Incremento por incumplimiento de requisitos SOCIMI. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR916,"Incremento por incumplimiento de requisitos SOCIMI. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR642,"Incremento por incumplimiento de requisitos SOCIMI. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR486,"Intereses de demora. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR487,"Intereses de demora. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR488,"Intereses de demora. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR489,"Intereses de demora. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR618,"Intereses de demora. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR490,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR491,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR492,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR493,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR620,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria. TOTAL");
		DESCRIPTION_MAP.put(Mod2002015Key.TR494,"L\u00EDquido a ingresar o a devolver. ARABA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR495,"L\u00EDquido a ingresar o a devolver. GIPUZKOA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR496,"L\u00EDquido a ingresar o a devolver. BIZKAIA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR497,"L\u00EDquido a ingresar o a devolver. NAVARRA");
		DESCRIPTION_MAP.put(Mod2002015Key.TR622,"L\u00EDquido a ingresar o a devolver. TOTAL");

		DESCRIPTION_MAP.put(Mod2002015Key.POR51,"Suma de  porcentajes de participaci\u00F3n de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado");
		DESCRIPTION_MAP.put(Mod2002015Key.PORES,"Suma de porcentajes de participaciones en situaciones especiales");
	}
}
