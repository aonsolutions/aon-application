package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import java.util.EnumMap;

public class D2DepositDescription {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	// AÑO ACTUAL --> @      	Ej: 2014 
	// AÑO ANTERIOR --> #		Ej: 2013 
	// 2 AÑOS ANTES --> ¬ 		Ej: 2012 
	
	public static EnumMap<D2DepositKey,String> DESCRIPTION_MAP = new EnumMap<D2DepositKey,String>(D2DepositKey.class);
	public static EnumMap<D2DepositHeaderKey,String> DESCRIPTION_MAP_HEADER = new EnumMap<D2DepositHeaderKey,String>(D2DepositHeaderKey.class);
	
	static {
		DESCRIPTION_MAP.put(D2DepositKey.MA391000,"Saldo de la cuenta de p\u00e9rdidas y ganancias");
		DESCRIPTION_MAP.put(D2DepositKey.MA391001,"Remanente");
		DESCRIPTION_MAP.put(D2DepositKey.MA391002,"Reservas voluntarias");
		DESCRIPTION_MAP.put(D2DepositKey.MA391003,"Otras reservas de libre disposici\u00f3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA391004,"TOTAL BASES DE REPARTO = TOTAL APLICACI\u00d3N");

		DESCRIPTION_MAP.put(D2DepositKey.MA391005,"Reserva Legal");
		DESCRIPTION_MAP.put(D2DepositKey.MA391006, "Reserva por fondo de comercio");
		DESCRIPTION_MAP.put(D2DepositKey.MA391007,"Reservas especiales");
		DESCRIPTION_MAP.put(D2DepositKey.MA391008, "Reservas voluntarias");
		DESCRIPTION_MAP.put(D2DepositKey.MA391009,"Dividendos");
		DESCRIPTION_MAP.put(D2DepositKey.MA391010,"Remanente y otros"); 
		DESCRIPTION_MAP.put(D2DepositKey.MA391011,"Compensaci\u00f3n de p\u00e9rdidas de ejercicios anteriores");
		DESCRIPTION_MAP.put(D2DepositKey.MA391012,"TOTAL APLICAC\u00d3N = TOTAL BASES DE REPARTO");
		DESCRIPTION_MAP.put(D2DepositKey.MA394705,"Per\u00edodo medio de pago a proveedores (d\u00edas)");
	}
	
	static {
		DESCRIPTION_MAP.put(D2DepositKey.MA592001,"A) SALDO INICIAL BRUTO, EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592011,"(+) Entradas");
		DESCRIPTION_MAP.put(D2DepositKey.MA592141,"(+) Correcciones de valor por actualizaci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA592021,"(-) Salidas");
		DESCRIPTION_MAP.put(D2DepositKey.MA592031,"B) SALDO FINAL BRUTO, EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592041,"C) AMORTIZACI\u00D3N ACUMULADA, SALDO INICIAL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592051,"(+) Dotaci\u00F3n a la amortizaci\u00F3n del ejercicio");
		DESCRIPTION_MAP.put(D2DepositKey.MA592151,"(+) Aumento de la amortizaci\u00F3n acumulada por efecto de la actualizaci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA592061,"(+) Aumentos por adquisiciones o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA592071,"(-) Disminuciones por salidas, bajas o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA592081,"D) AMORTIZACI\u00D3N ACUMULADA, SALDO FINAL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592091,"E) CORRECCIONES DE VALOR POR DETERIORO, SALDO INICIAL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592101,"(+) Correcciones valorativas por deterioro reconocidas en el periodo");
		DESCRIPTION_MAP.put(D2DepositKey.MA592111,"(-) Reversi\u00F3n de correcciones valorativas por deteriodo");
		DESCRIPTION_MAP.put(D2DepositKey.MA592121,"(-) Disminuciones por salidas, bajas o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA592131,"F) CORRECCIONES DE VALOR POR DETERIORO, SALDO FINAL EJERCICIO @ ");
		
		DESCRIPTION_MAP.put(D2DepositKey.MA5920019,"A) SALDO INICIAL BRUTO, EJERCICIO # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920119,"(+) Entradas");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921419,"(+) Correcciones de valor por actualizaci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920219,"(-) Salidas");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920319,"B) SALDO FINAL BRUTO, EJERCICIO # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920419,"C) AMORTIZACI\u00D3N ACUMULADA, SALDO INICIAL EJERCICIO # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920519,"(+) Dotaci\u00F3n a la amortizaci\u00F3n del ejercicio");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921519,"(+) Aumento de la amortizaci\u00F3n acumulada por efecto de la actualizaci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920619,"(+) Aumentos por adquisiciones o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920719,"(-) Disminuciones por salidas, bajas o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920819,"D) AMORTIZACI\u00D3N ACUMULADA, SALDO FINAL EJERCICIO # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA5920919,"E) CORRECCIONES DE VALOR POR DETERIORO, SALDO INICIAL EJERCICIO # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921019,"(+) Correcciones valorativas por deterioro reconocidas en el periodo");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921119,"(-) Reversi\u00F3n de correcciones valorativas por deteriodo");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921219,"(-) Disminuciones por salidas, bajas o traspasos");
		DESCRIPTION_MAP.put(D2DepositKey.MA5921319,"F) CORRECCIONES DE VALOR POR DETERIORO, SALDO FINAL EJERCICIO # ");
	
		DESCRIPTION_MAP.put(D2DepositKey.MA592200,"Coste del bien en origen");
		DESCRIPTION_MAP.put(D2DepositKey.MA592201,"Cuotas satisfechas");
		DESCRIPTION_MAP.put(D2DepositKey.MA592202,"Ejercicios anteriores");
		DESCRIPTION_MAP.put(D2DepositKey.MA592203,"Ejercicio @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592204,"Importe cuotas pendientes @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA592205,"Valor de la opci\u00f3n de compra");
	}
	
	static {
		DESCRIPTION_MAP.put(D2DepositKey.MA6193001,"Activos a valor razonable con cambios en p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193011,"Inversiones mantenidas hasta el vencimiento");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193021,"Pr\u00E9stamos y partidas a cobrar");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193031,"Activos disponibles para la venta");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193041,"Derivados de cobertura");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193051,"TOTAL");

		DESCRIPTION_MAP.put(D2DepositKey.MA6193101,"Activos a valor razonable con cambios en p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193111,"Inversiones mantenidas hasta el vencimiento");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193121,"Pr\u00E9stamos y partidas a cobrar");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193131,"Activos disponibles para la venta");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193141,"Derivados de cobertura");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193151,"TOTAL");

		///////////////////////////////***PYMES***///////////////////////////////
		DESCRIPTION_MAP.put(D2DepositKey.MP6193061,"Activos financieros mantenidos para negociar");
		DESCRIPTION_MAP.put(D2DepositKey.MP6193071,"Activos financieros a coste amortizado");
		DESCRIPTION_MAP.put(D2DepositKey.MP6193081,"Activos financieros a coste");		
		
		DESCRIPTION_MAP.put(D2DepositKey.MP6193161,"Activos financieros mantenidos para negociar");
		DESCRIPTION_MAP.put(D2DepositKey.MP6193171,"Activos financieros a coste amortizado");
		DESCRIPTION_MAP.put(D2DepositKey.MP6193181,"Activos financieros a coste");		

		//////////////////////////////////////////////////////////////
		
		DESCRIPTION_MAP.put(D2DepositKey.MA6193201,"Inversiones mantenidas hasta el vencimiento");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193211,"Activos financieros mantenidos para negociar");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193221,"Otros activos financieros a valor razonable con cambios en la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193231,"Inversi\u00F3n en el patrimonio de empresas del grupo, multigrupo y asociados");
		DESCRIPTION_MAP.put(D2DepositKey.MA6193241,"Activos financieros disponibles para la venta");
		
		
		DESCRIPTION_MAP.put(D2DepositKey.MA62933019,"P\u00E9rdida por deterioro al inicio del ejercicio # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA62933119,"(+)Correci\u00F3n valorativo por deterioro");
		DESCRIPTION_MAP.put(D2DepositKey.MA62933219,"(-) Reversi\u00F3n del deterioro");
		DESCRIPTION_MAP.put(D2DepositKey.MA62933319,"(-) Salidas y reducciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA62933419,"(+/-) Traspasos y otras variaciones (combinaciones de negocio,etc.)");
		DESCRIPTION_MAP.put(D2DepositKey.MA62933519,"P\u00E9rdida por deterioro al final del ejercicio # ");
		//TODO
		DESCRIPTION_MAP.put(D2DepositKey.MA6293301,"P\u00E9rdida por deterioro al inicio del ejercicio @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293311,"(+)Correcci\u00F3n valorativo por deterioro");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293321,"(-) Reversi\u00F3n del deterioro");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293331,"(-) Salidas y reducciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293341,"(+/-) Traspasos y otras variaciones (combinaciones de negocio,etc.)");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293351,"P\u00E9rdida por deterioro al final del ejercicio @ ");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA62934019,"Valor razonable al inicio del ejercicio # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA62934119,"Variaciones del valor razonable registradas en p\u00E9rdidas y ganancias en el ejercicio # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA62934219,"Variaciones del valor razonable registradas en patrimonio neto en el ejercicio # ");
		DESCRIPTION_MAP.put(D2DepositKey.MA62934319,"Valor razonable al final del ejercicio # ");
		//TODO
		DESCRIPTION_MAP.put(D2DepositKey.MA6293401,"Valor razonable al inicio del ejercicio @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293411,"Variaciones del valor razonable registradas en p\u00E9rdidas y ganancias en el ejercicio @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293421,"Variaciones del valor razonable registradas en patrimonio neto en el ejercicio @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA6293431,"Valor razonable al final del ejercicio @ ");

		DESCRIPTION_MAP.put(D2DepositKey.MA62935059,"Empresas del grupo");
		DESCRIPTION_MAP.put(D2DepositKey.MA62935159,"Empresas multigrupo");
		DESCRIPTION_MAP.put(D2DepositKey.MA62935259,"Empresas asociadas	");
		DESCRIPTION_MAP.put(D2DepositKey.MA62935359,"Total");

	}
	
	static{
		DESCRIPTION_MAP.put(D2DepositKey.MA794001,"D\u00E9bitos y partidas a pagar");
		DESCRIPTION_MAP.put(D2DepositKey.MA794011,"Pasivos a valor razonable con cambios en p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP.put(D2DepositKey.MA794021,"Otros");

		DESCRIPTION_MAP.put(D2DepositKey.MA794031,"TOTAL");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794101,"D\u00E9bitos y partidas a pagar");

		DESCRIPTION_MAP.put(D2DepositKey.MA794111,"Pasivos a valor razonable con cambios en p\u00E9rdidas y ganancias");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794121,"Otros");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794131,"TOTAL");
		
		/////////////////////////////**** PYMES***** ///////////////////////////////
		DESCRIPTION_MAP.put(D2DepositKey.MP794041,"Pasivos financieros a coste amortizado");
		DESCRIPTION_MAP.put(D2DepositKey.MP794051,"Pasivos financieros mantenidos para negociar");
		
		DESCRIPTION_MAP.put(D2DepositKey.MP794141,"Pasivos financieros a coste amortizado");
		DESCRIPTION_MAP.put(D2DepositKey.MP794151,"Pasivos financieros mantenidos para negociar");

		////////////////////////////////////////////////////////////////////////////
		
		DESCRIPTION_MAP.put(D2DepositKey.MA794201,"Deudas con entidades de cr\u00E9dito");
	
		
		DESCRIPTION_MAP.put(D2DepositKey.MA794211,"Acreedores por arrendamiento financiero");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794221,"Otras deudas");
	
		
		DESCRIPTION_MAP.put(D2DepositKey.MA794231,"Deudas con empresas del grupo y asociadas");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794241,"Acreedores comerciales no corrientes");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794251,"Acreedores comerciales y otras cuentas a pagar");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794261,"Proveedores");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794271,"Otros acreedores");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794281,"Deuda con caracter\u00EDsticas especiales");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794291,"TOTAL");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA794301,"Entidades de cr\u00E9dito");	
		DESCRIPTION_MAP.put(D2DepositKey.MA794311,"Total p\u00F3lizas de cr\u00E9dito");

	}
	
	static{
		DESCRIPTION_MAP.put(D2DepositKey.MA1095000,"1.Consumo de mercader\u00EDas");
		DESCRIPTION_MAP.put(D2DepositKey.MA1095001,"a)Compras, netas de devoluciones y cualquier descuento, de las cuales:");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095002,"-nacionales");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095003,"-adquisiciones intracomunitarias");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095004,"-importaciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095005,"b) Variaci\u00F3n de existencias");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095006,"2.Consumo de materias primas y otras materias consumibles");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095007,"a)Compras, netas de devoluciones y cualquier descuento, de las cuales:");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095008,"-nacionales");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095009,"-adquisiciones intracomunitarias");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095010,"-importaciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095011,"b) Variaci\u00F3n de existencias");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095012,"3.Cargas sociales");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095013,"a) Seguridad social a cargo de la empresa");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095014,"b) Aportaciones y dotaciones para pensiones");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095015,"c) Otras cargas sociales");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095016,"4.Otros gastos de explotaci\u00F3n");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095017,"a) P\u00E9rdidas y deterioro de operaciones comerciales");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095018,"b) Resto de gastos de explotaci\u00F3n");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095019,"5.Venta de bienes y prestaci\u00F3n de servicios producidos por permuta de bienes no monetarios y servicios");

		DESCRIPTION_MAP.put(D2DepositKey.MA1095020,"6.Resultados originados fuera de la actividad normal de la empresa incluidos en 'Otros resultados'");

	}
	
	static{
		DESCRIPTION_MAP.put(D2DepositKey.MA1196000,"Que aparecen en el balance");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196001,"Imputados en la cuenta de p\u00E9rdidas y ganancias");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196002,"Deudas a largo plazo transformables en subvenciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196010,"Saldo al inicio del ejercicio");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196011,"(+) Importes recibidos");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196012,"(+) Conversi\u00F3n de deudas a largo plazo en subvenciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196013,"(-) Subvenciones traspasadas a resultados del ejercicio");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196014,"(-) Importes devueltos");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196015,"(-) Otros movimientos");

		DESCRIPTION_MAP.put(D2DepositKey.MA1196016,"Saldo al cierre del ejercicio");
		
		//************** PYMES **********************
		DESCRIPTION_MAP.put(D2DepositKey.MP1196017,"(+) Aumentos");
		DESCRIPTION_MAP.put(D2DepositKey.MP1196018,"(+) Disminuciones");
		//*******************************************
	}
	
	static{
		DESCRIPTION_MAP.put(D2DepositKey.MA1398000,"Directores generales y presidentes ejecutivos");// 2015 Altos directivos

		DESCRIPTION_MAP.put(D2DepositKey.MA1398001,"Resto de directores y gerentes"); // 2015 Resto de personal directivo

		DESCRIPTION_MAP.put(D2DepositKey.MA1398002,"T\u00E9cnicos y profesionales cient\u00EDficos e intelectuales y profesionales de apoyo");
				// 2015 T\u00E9cnicos y profesionales cient\u00EDficos e intelectuales y de apoyo
		DESCRIPTION_MAP.put(D2DepositKey.MA1398003,"Empleados contables, administrativos y otros empleados de oficina");
				// 2015 Empleados de tipo administrativo
		DESCRIPTION_MAP.put(D2DepositKey.MA1398004,"Comerciales, vendedores y similares");
		
		DESCRIPTION_MAP.put(D2DepositKey.MA1398005,"Resto de personal cualificado");

		DESCRIPTION_MAP.put(D2DepositKey.MA1398006,"Ocupaciones elementales");
				// 2015 Trabajadores no cualificados
		DESCRIPTION_MAP.put(D2DepositKey.MA1398007,"Total empleado medio");

	}
	
	
	static{
		DESCRIPTION_MAP.put(D2DepositKey.MA15947001,"Dentro del plazo m\u00E1ximo legal");
		DESCRIPTION_MAP.put(D2DepositKey.MA15947011,"Resto excedido");
		DESCRIPTION_MAP.put(D2DepositKey.MA15947021,"TOTAL (1+2)");
		DESCRIPTION_MAP.put(D2DepositKey.MA15947041,"Aplazamientos que a la fecha de cierre sobrepasan el plazo m\u00E1ximo legal");
		DESCRIPTION_MAP.put(D2DepositKey.MA1594705,"Per\u00EDodo medio de pago a proveedores");		
	}
	
	static{ // Apartado 12
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97001,"Ventas de activos corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97011,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97021,"Ventas de activos no corrientes, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97031,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97041,"Compras de activos corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97051,"Compras de activos no corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97061,"Prestaci\u00F3n de servicios, de la cual:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97071,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97081,"Recepci\u00F3n de servicios");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97091,"Contratos de arrendamiento financieros, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97101,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97111,"Transferencia de investigaci\u00F3n y desarrollo, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97121,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97131,"Ingresos por intereses cobrados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97141,"Ingresos por intereses devengados pero no cobrados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97151,"Gastos por intereses pagados");	
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97161,"Gastos por intereses devengados pero no pagados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97171,"Gastos consecuencia de deudas incobrables o de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97181,"Dividendos y otros beneficios distribuidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97191,"Garant\u00EDas y avales recibidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A97201,"Garant\u00EDas y avales prestados");
	
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970019,"Ventas de activos corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970119,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970219,"Ventas de activos no corrientes, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970319,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970419,"Compras de activos corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970519,"Compras de activos no corrientes");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970619,"Prestaci\u00F3n de servicios, de la cual:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970719,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970819,"Recepci\u00F3n de servicios");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A970919,"Contratos de arrendamiento financieros, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971019,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971119,"Transferencia de investigaci\u00F3n y desarrollo, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971219,"Beneficios(+) / P\u00E9rdidas(-)");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971319,"Ingresos por intereses cobrados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971419,"Ingresos por intereses devengados pero no cobrados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971519,"Gastos por intereses pagados");	
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971619,"Gastos por intereses devengados pero no pagados");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971719,"Gastos consecuencia de deudas incobrables o de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971819,"Dividendos y otros beneficios distribuidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A971919,"Garant\u00EDas y avales recibidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12A972019,"Garant\u00EDas y avales prestados");
		
		
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97301,"A) ACTIVO NO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97311,"1.Invesiones financieras a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97321,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97331,"B) ACTIVO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97341,"1.Deudores comerciales y otras deudas a cobrar");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97351,"a)Clientes por ventas y prestaci\u00F3n de servicios a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97361,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97371,"b)Clientes por ventas y prestaci\u00F3n de servicios a corto plazo. de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97381,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97391,"Accionistas (socios) por desembolsos exigidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97401,"d)Otros deudores, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97411,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97421,"2.Inversiones financieras a corto plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97431,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97441,"C)PASIVO NO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97451,"1.Deudas a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97461,"a)Deudas con entidades de cr\u00E9dito");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97471,"b)Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97481,"c)Otras deudas a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97491,"2.Deuda con caracter\u00EDsticas especiales a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97501,"D)PASIVO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97511,"1.Deudas a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97521,"a)Deudas con entidades de cr\u00E9dito");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97531,"b)Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97541,"c)Otras deudas a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97551,"2.Acreedores comerciales y otras cuentas a pagar");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97561,"a)Proveedores");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97571,"b)Otros Acreedores");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B97581,"3.Deuda con caracter\u00EDsticas especiales a corto plazo");
		
		
		
		//////////////////////////////////////////********PYMES*******//////////////////////////////////////////
		
		DESCRIPTION_MAP.put(D2DepositKey.MP12B97591,"a)Clientes por ventas y prestaci\u00F3n de servicios a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MP12B97601,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a largo plazo");		
		
		DESCRIPTION_MAP.put(D2DepositKey.MP12B975919,"a)Clientes por ventas y prestaci\u00F3n de servicios a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MP12B976019,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a largo plazo");		

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973019,"A) ACTIVO NO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973119,"1.Invesiones financieras a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973219,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973319,"B) ACTIVO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973419,"1.Deudores comerciales y otras deudas a cobrar");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973519,"a)Clientes por ventas y prestaci\u00F3n de servicios a largo plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973619,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973719,"b)CLientes por ventas y prestaci\u00F3n de servicios a corto plazo. de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973819,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B973919,"Accionistas (socios) por desembolsos exigidos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974019,"d)Otros deudores, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974119,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974219,"2.Inversiones financieras a corto plazo, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974319,"-Correcciones valorativas por cr\u00E9ditos de dudoso cobro");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974419,"C)PASIVO NO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974519,"1.Deudas a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974619,"a)Deudas con entidades de cr\u00E9dito");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974719,"b)Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974819,"c)Otras deudas a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B974919,"2.Deuda con caracter\u00EDsticas especiales a largo plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975019,"D)PASIVO CORRIENTE");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975119,"1.Deudas a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975219,"a)Deudas con entidades de cr\u00E9dito");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975319,"b)Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975419,"c)Otras deudas a corto plazo");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975519,"2.Acreedores comerciales y otras cuentas a pagar");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975619,"a)Proveedores");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975719,"b)Otros Acreedores");
		DESCRIPTION_MAP.put(D2DepositKey.MA12B975819,"3.Deuda con caracter\u00EDsticas especiales a corto plazo");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97700,"1.Sueldos, dietas y otras remuneraciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97701,"2.Obligaciones contra\u00EDdas en materia de pensiones de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97702,"a)Obligaciones con miembros antiguos de la alta direcci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97703,"b)Obligaciones con miembros actuales de la alta direcci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97704,"3.Primas de seguro de vida pagadas, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97705,"a)Primas pagadas a miembros antiguos de la alta direcci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97706,"b)Primas pagadas a miembros actuales de la alta direcci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97707,"4.Indemnizaciones por cese");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97708,"5.Pagos basados en instrumentos de patrimonio");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97709,"6.Anticipos y cr\u00E9ditos concedidos, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97710,"a)Importes devueltos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97711,"b)Obligaciones asumidas por cuenta de ellos a t\u00EDtulo de garant\u00EDa");

		
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97720,"1.Sueldos, dietas y otras remuneraciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97721,"2.Obligaciones contra\u00EDdas en materia de pensiones de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97722,"a)Obligaciones con miembros antiguos del \u00F3rgano de administraci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97723,"b)Obligaciones con miembros actuales del \u00F3rgano de administraci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97724,"3.Primas de seguro de vida pagadas, de las cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97725,"a)Primas pagadas a miembros antiguos del \u00F3rgano de administraci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97726,"b)Primas pagadas a miembros actuales del \u00F3rgano de administraci\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97727,"4.Indemnizaciones por cese");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97728,"5.Pagos basados en instrumentos de patrimonio");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97729,"6.Anticipos y creditos concedidos, de los cuales:");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97730,"a)Importes devueltos");
		DESCRIPTION_MAP.put(D2DepositKey.MA12C97731,"b)Obligaciones asumidas por cuenta de ellos a t\u00EDtulo de garant\u00EDa");
		
	}
	
	static{ // APARTADO 14
		
		DESCRIPTION_MAP.put(D2DepositKey.MA14199000,"1.Valor contable");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199001,"2.Amortizaci\u00F3n acumulada");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199002,"3.1.Reconocidas en el ejercicio");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199003,"3.2.Acumuladas");
	
		DESCRIPTION_MAP.put(D2DepositKey.MA14199004,"B) Gastos incurridos para la mejora y protecci\u00F3n del medio ambiente");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199005,"Saldo al inicio del ejercicio");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199006,"(+) Dotaciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199007,"(+) Aplicaciones");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199008,"(+/-) Otros ajustes realizados (combinaciones de negocios, etc) de los cuales");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199009,"(+/-) Combinaciones de negocios");
		
		DESCRIPTION_MAP.put(D2DepositKey.MA14199010,"(+/-) Variaciones por cambios de valoraci\u00F3n (inclu\u00EDdas modificaciones en el tipo de descuento)");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199011,"(+/-) Excesos");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199012,"Saldo al cierre del ejercicio");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199013,"2.Derechos de reembolso reconocidos en el activo");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199014,"D) INVERSIONES DEL EJERCICIO POR RAZONES MEDIOAMBIENTALES");

		DESCRIPTION_MAP.put(D2DepositKey.MA14199015,"E) COMPENSACIONES A RECIBIR DE TERCEROS");

		

		DESCRIPTION_MAP.put(D2DepositKey.MA14294600,"A) IMPORTE (BRUTO) AL INICIO DEL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294601,"(+) Entradas o adquisiciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294602,"(-) Enajenaciones y otras bajas");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294603,"B) IMPORTE (BRUTO) AL CIERRE DEL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294604,"C) CORRECCIONES DE VALOR POR DETERIORO AL INICIO DEL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294605,"(+) Dotaciones");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294606,"(-) Aplicaci\u00F3n y bajas");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294607,"D) CORRECCIONES DE VALOR POR DETERIORO AL CIERRE DEL EJERCICIO @ ");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294608,"E) GASTOS DEL EJERCICIO @  POR EMISI\u00D3N DE GASES DE EFECTO INVERNADERO");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294610,"(+) Por derechos de emisi\u00F3n transferidos a la cuenta de haberes de las empresas del registro nacional de derechos de emisi\u00F3n, imputados a las emisiones en el año");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294611,"(+) Por restantes derechos de emisi\u00F3n, adquiridos o generados, que figuran en el balance, imputados a las emisiones del año");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294612,"(+) Cuant\u00EDa que procede por deficits de derechos de emisi\u00F3n");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294613,"F) SUBVENCIONES RECIBIDAS EN EL EJERCICIO @ , POR DERECHOS DE EMISI\u00D3N DE GASES DE EFECTO INVERNADERO");
		DESCRIPTION_MAP.put(D2DepositKey.MA14294614,"Importe de las subvenciones imputadas a resultados como ingresos de ejercicio @ ");
	}
	
	static { 

		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831010 ,"Constituci\u00F3n de sociedades u otras personas jur\u00EDdicas");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831020 ,"Direcci\u00F3n, secretar\u00EDa y/o asesor\u00EDa externa de una sociedad");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831030 ,"Socio de una asocici\u00F3n o similar");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831040 ,"Facilitar direcci\u00F3n postal, fiscal, social o similar a una persona jur\u00EDdica");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831050 ,"Funciones fiduciarias en un fideicomiso");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831060 ,"Funciones de accionistas por cuenta ajena");
	}
	
	static { 

		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831011 ,"Constituci\u00F3n de sociedades u otras personas jur\u00EDdicas");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831021 ,"Direcci\u00F3n, secretar\u00EDa y/o asesor\u00EDa externa de una sociedad");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831031 ,"Socio de una asocici\u00F3n o similar");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831041 ,"Facilitar direcci\u00F3n postal, fiscal, social o similar a una persona jur\u00EDdica");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831051 ,"Funciones fiduciarias en un fideicomiso");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.SRP831061 ,"Funciones de accionistas por cuenta ajena");
	}
	
	static {
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.IMA8099020 ,"Emisiones Alcance 1 (TnC02)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.IMA8099030 ,"Emisiones Alcance 2 (TnC02)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.IMA8099040 ,"Emisiones Alcance 3 (TnC02)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.IMA8099050 ,"Consumo de energia dentro de la organizacion (Kwh)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.IMA8099060 ,"Consumo de agua (m3)");
	}
	
	static { 

		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111000 ,"A) ACTIVO NO CORRIENTE");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111100,"I. Inmovilizado Intangible");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111200,"II. Inmovilizado material");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111300,"III. Inversiones inmobiliarias");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111400,"IV. Inversiones en empresas del grupo y asociadas a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111500,"V. Inversiones financieras a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111600,"VI. Activos por impuesto diferido");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA111700 ,"VII. Deudores comerciales no corrientes");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112000 ,"B) ACTIVO CORRIENTE");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112100,"I. Activos no corrientes mantenidos para la venta");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112200,"II. Existencias");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112300 ,"III. Deudores comerciales y otras cuentas a cobrar");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112380 ,"1. Clientes por ventas y prestaciones de servicios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112381 ,"a) Clientes por ventas y prestaciones de servicios a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112382 ,"b) Clientes por ventas y prestaciones de servicios a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112370 ,"2. Accionistas (socios) por desembolsos exigidos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112390 ,"3. Otros deudores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112400,"IV. Inversiones en empresas del grupo y asociadas a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112500 ,"V. Inversiones financieras a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112600,"VI. Periodificaciones a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA112700,"VII. Efectivo y otros activos liquidos equivalentes");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA110000 ,"TOTAL ACTIVO (A + B)");
		
		
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2120000,"A) PATRIMONIO NETO");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121000,"A-1) Fondos propios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121100,"I. Capital");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121110,"1. Capital escriturado");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121120,"2. (Capital no exigido)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121200,"II. Prima de emisi\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121300,"III. Reservas");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121350,"1. Reserva de capitalizaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121360,"2. Otras reservas");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121400,"IV. (Acciones y participaciones en patrimonio propias)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121500,"V. Resultados de ejercicios anteriores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121600,"VI. Otras aportaciones de socios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121700,"VII. Resultado del ejercicio");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121800,"VIII. (Dividendo a cuenta)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2121900,"IX. Otros instrumentos de patrimonio");
		
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BP2122000,"A-2). Ajustes en patrimonio neto");
		
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2122000,"A-2) Ajustes por cambios de valor");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2123000,"A-3) Subvenciones, donaciones y legados recibidos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131000,"B) PASIVO NO CORRIENTE");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131100,"I. Provisiones a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131200,"II. Deudas a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131220,"1. Deudas con entidades de credito");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131230,"2. Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131290,"3. Otras deudas a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131300,"III. Deudas con empresas del grupo y asodiadas a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131400,"IV. Pasivo por impuesto diferido");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131500,"V. Periodificaciones a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131600,"VI. Acreedores comerciales no corrientes");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2131700,"VII. Deuda con caracteristicas especiales a largo plazo");
		
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232000,"C) PASIVO CORRIENTE");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232100,"I. Pasivos vinculados con activos no corrientes mantenidos para la venta");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232200,"II. Provisiones a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232300,"III. Deudas a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232320,"1. Deudas con entidades de cr\u00E9dito");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232330,"2. Acreedores por arrendamiento financiero");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232390,"3. Otras deudas a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232400,"IV. Deudas con empresas del grupo y asociadas a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232500,"V. Acreedores comerciales y otras cuentas a pagar");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232580,"1. Proveedores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232581,"a) Proveedores a largo plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232582,"b) Proveedores a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232590,"2. Otros acreedores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232600,"VI. Periodificaciones a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2232700,"VII. Deuda con caracteristicas especiales a corto plazo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.BA2230000,"TOTAL PATRIMONIO NETO Y PASIVO (A + B + C)");
	}
	
	static{
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40100,"1. Importe neto de la cifra de negocios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40200,"2. Variaci\u00F3n de existencias de productos terminados y en curso de fabricaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40300,"3. Trabajos realizados por la empresa para su activo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40400,"4. Aprovisionamientos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40500,"5. Otros ingresos de explotaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40600,"6. Gastos de personal");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40700,"7. Otros gastos de explotaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40800,"8. Amortizaci\u00F3n del inmovilizado");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA40900,"9. Imputaci\u00F3n de subvenciones de inmovilizado no financiero y otras");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41000,"10. Excesos de provisiones");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41100,"11. Deterioro y resultado por enajenaciones del inmovilizado");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41200,"12. Diferencia negativa de combinaciones de negocio");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41300,"13. Otros resultados");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA49100,"A) RESULTADO DE EXPLOTACI\u00D3N (1+2+3+4+5+6+7+8+9+10+11+12+13)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41400,"14. Ingresos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41430,"a) Imputaci\u00F3n de subvenciones, donaciones y legados de caracter financiero");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41490,"b) Otros ingresos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41500,"15. Gastos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41600,"16. Variaci\u00F3n de valor razonable en instrumentos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41700,"17. Diferencias de cambio");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41800,"18. Deterioro y resultado por enajenaciones de instrumentos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA42100,"19. Otros ingresos y gastos de caracter financiero");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA42110,"a) Incorporaci\u00F3n al activo de gastos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA42120,"b) Ingresos financieros derivados de convenios de acreedores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA42130,"c) Resto de ingresos y gastos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA49200,"B) RESULTADO FINANCIERO (14 + 15 + 16 + 17 + 18 + 19)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA49300,"C) RESULTADO ANTES DE IMPUESTOS (A + B)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA41900,"Impuesto sobre beneficios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PA49500,"D) RESULTADO DEL EJERCICIO (C + 20)");
	}
	
	static{
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA159100,"A) RESULTADO DE LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150010,"I. Por valoraci\u00F3n de instrumentos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150020,"II. Por coberturas de flujos de efectivo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150030,"III. Subvenciones, donaciones y legados recibidos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150040,"IV. Por ganancias y p\u00E9rdidas actuariales y otros ajustes");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150050,"V. Por activos no corrientes y pasivos vinculados, mantenidos para la venta");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150060,"VI. Diferencias de conversi\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150070,"VII. Efecto impositivo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA159200,"B) TOTAL INGRESOS Y GASTOS IMPUTADOS DIRECTAMENTE EN EL PATRIMONIO NETO (I+II+III+IV+V+VI+VII)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150080,"VIII. Por valoraci\u00F3n de instrumentos financieros");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150090,"IX. Por coberturas de flujos de efectivo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150100,"X. Subvenciones, donaciones y legados recibidos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150110,"XI. Por activos no corrientes y pasivos vinculados, mantenidos para la venta");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150120,"XII. Diferencias de conversi\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA150130,"XIII. Efecto impositivo");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA159300,"C) TOTAL TRANSFERENCIAS A LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS (VIII+IX+X+XI+XII+XIII)");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA159400,"TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (A + B +C)");
	
	}
	
	static{
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2511019,"A) SALDO, FINAL DEL EJERCICIO ¬ ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2512019,"I. Ajustes por cambios de criterio del ejercicio ¬  y anteriores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2513019,"II. Ajustes por errores del ejercicio ¬  y anteriores");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2514019,"B) SALDO AJUSTADO, INICIO DEL EJERCICIO # ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP1528019,"I. Resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP1530019,"II. Ingresos y gastos reconocidos en patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP1527019,"1. Ingresos fiscales a distribuir en varios ejercicios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP1529019,"2. Otros ingresos y gastos reconocidos en patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2516019,"II. Operaciones con socios o propietarios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2517019,"1. Aumentos de capital");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2518019,"2. (-) Reducciones de capital");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2526019,"3. Otras operaciones con socios o propietarios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2524019,"III. Otras variaciones del patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2531019,"1. Movimiento de la reserva de revalorizaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2532019,"2. Otras variaciones");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251101,"C) SALDO, FINAL DEL EJERCICIO # ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251201,"I. Ajustes por cambios de criterio en el ejercicio # ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251301,"II. Ajustes por errores del ejercicio # ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251401,"D) SALDO AJUSTADO, INICIO DEL EJERCICIO @  ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP152801,"I. Resultado de la cuenta de p\u00E9rdidas y ganancias");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP153001,"II. Ingresos y gastos reconocidos en patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP152701,"1. Ingresos fiscales a distribuir en varios ejercicios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNP152901,"2. Otros ingresos y gastos reconocidos en patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251601,"II. Operaciones con socios o propietarios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251701,"1. Aumentos de capital");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251801,"2. (-) Reducciones de capital");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA252601,"3. Otras operaciones con socios o propietarios");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA252401,"III. Otras variaciones del patrimonio neto");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA253101,"1. Movimiento de la reserva de revalorizaci\u00F3n");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA253201,"2. Otras variaciones ");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA252501,"E) SALDO, FINAL DEL EJERCICIO @ ");
		
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA251501,"I. Total ingresos y gastos reconocidos");
		DESCRIPTION_MAP_HEADER.put(D2DepositHeaderKey.PNA2515019,"I. Total ingresos y gastos reconocidos");
		
	}
	
	public static void main(String[] args) {
		
		for (D2DepositHeaderKey key : D2DepositDescription.DESCRIPTION_MAP_HEADER.keySet())
			System.out.println("----> " + D2DepositDescription.DESCRIPTION_MAP_HEADER.get(key));
		
	}
}
