package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.util.LinkedList;
import java.util.List;

public class Mod200ConstantsValidation {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	private static final int PAGE03 = 3;
	private static final int PAGE04 = 4;
	private static final int PAGE07 = 7;

	private static final String ERROR001 = "'"+Mod200Key.BA180.getDescription()+"' y '"
			+Mod200Key.BP252.getDescription()+"' deben ser iguales.";
	private static final String ERROR002 = "No se han cumplimentado datos en el "
			+ "Balance (Activo, patrimonio neto y pasivo).";
	private static final String ERROR003 =
			"Verifique el signo de la clave: ";
	private static final String ERROR005 =
			"Las claves '" + Mod200Key.BP199.getDescription() + "' y '"
			+ Mod200Key.LQ500.getDescription() + "' deben ser iguales.";
	
	/*	
	private static final int PAGE08 = 8;
	private static final int PAGE09 = 9;
	private static final int PAGE10 = 10;

	private static final String MSG1 = "Correcciones al resultado de la cuenta de "
			+ "p\u00E9rdidas y ganancias no compatible con determinados caracteres de la declaraci\u00F3n";
	private static final String MSG2 = "Las casillas 301 y 302 (Correcciones por Impuesto sobre "
			+ "Sociedades. Aumentos o Disminuciones) no son v\u00E1lidas con lo indicado en la "
			+ "casilla 326 (PyG - Impuesto sobre beneficios)";
	private static final String MSG3 = "La compensaci\u00F3n aplicada en la presente liquidaci\u00F3n, es "
			+ "mayor que lo definido como pendiente de ejercicio anteriores.";
	private static final String MSG4 = "No admite valores negativos.";
	private static final String MSG5 = "Si existen deducciones por doble imposici\u00F3n pendientes de "
			+ "aplicar no podr\u00E1n aplicarse ni las bonificaciones del art\u00EDculo 76 de la Ley "
			+ "19/1994, ni las deducciones por inversiones";
	private static final String MSG6 = "\"Deducción aplicada en esta liquidación\" mayor que \"Deducción pendiente\"";
*/	
	public static List<ValidationMessage> VALIDATION_EXPRESSION_LIST = new LinkedList<ValidationMessage>();
	
	// La condición debe cumplirse.
	static {	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,Mod200Key.BA180
				,ERROR001
				,"round(BA180) == round(BP252)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,Mod200Key.BA180
				,ERROR002
				,"round(BA180) != 0.0 && round(BP252) != 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP189
				,ERROR003 + Mod200Key.BP189.getDescription()
				,"round(BP189) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP194
				,ERROR003 + Mod200Key.BP194.getDescription()
				,"round(BP194) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP197
				,ERROR003 + Mod200Key.BP197.getDescription()
				,"round(BP197) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP200
				,ERROR003 + Mod200Key.BP200.getDescription()
				,"round(BP200) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP199
				,ERROR005
				,"(C0003 || C0004 || C0024 || C0025 || C0036 || C0061)? true "
					+ ": round(BP199) == round(LQ500)"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC534
				,ERROR003 + Mod200Key.TC534.toString()
				,"round(TC534) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC535
				,ERROR003 + Mod200Key.TC535.toString()
				,"round(TC535) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC536
				,ERROR003 + Mod200Key.TC536.toString()
				,"round(TC536) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC537
				,ERROR003 + Mod200Key.TC537.toString()
				,"round(TC537) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC538
				,ERROR003 + Mod200Key.TC538.toString()
				,"round(TC538) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC539
				,ERROR003 + Mod200Key.TC539.toString()
				,"round(TC539) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC540
				,ERROR003 + Mod200Key.TC540.toString()
				,"round(TC540) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC541
				,ERROR003 + Mod200Key.TC541.toString()
				,"round(TC541) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC542
				,ERROR003 + Mod200Key.TC542.toString()
				,"round(TC542) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC543
				,ERROR003 + Mod200Key.TC543.toString()
				,"round(TC543) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC544
				,ERROR003 + Mod200Key.TC544.toString()
				,"round(TC544) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC545
				,ERROR003 + Mod200Key.TC545.toString()
				,"round(TC545) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC546
				,ERROR003 + Mod200Key.TC546.toString()
				,"round(TC546) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC562
				,ERROR003 + Mod200Key.TC562.toString()
				,"round(TC562) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC563
				,ERROR003 + Mod200Key.TC563.toString()
				,"round(TC563) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC564
				,ERROR003 + Mod200Key.TC564.toString()
				,"round(TC564) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC565
				,ERROR003 + Mod200Key.TC565.toString()
				,"round(TC565) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC566
				,ERROR003 + Mod200Key.TC566.toString()
				,"round(TC566) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC567
				,ERROR003 + Mod200Key.TC567.toString()
				,"round(TC567) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC568
				,ERROR003 + Mod200Key.TC568.toString()
				,"round(TC568) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC569
				,ERROR003 + Mod200Key.TC569.toString()
				,"round(TC569) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC570
				,ERROR003 + Mod200Key.TC570.toString()
				,"round(TC570) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC571
				,ERROR003 + Mod200Key.TC571.toString()
				,"round(TC571) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC572
				,ERROR003 + Mod200Key.TC572.toString()
				,"round(TC572) <= 0.0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC574
				,ERROR003 + Mod200Key.TC574.toString()
				,"round(TC574) <= 0.0"));
	}
/*	
	static {
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0417
				,"En el sumatorio de la columna de aumentos no se admiten signos negativos."
				,"I0417 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0418
				,"En el sumatorio de la columna de disminuciones no se admiten signos negativos."
				,"D0418 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0391
				,MSG1 + "('"+Mod200Key.I0391.getDescription()+"' sin '"+ Mod200Key.C0001.getDescription()+"')"
				,"I0391 > 0 && !C0001"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0392
				,MSG1 + "('"+Mod200Key.D0392.getDescription()+"' sin '"+ Mod200Key.C0001.getDescription()+"')"
				,"D0392 > 0 && !C0001"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0389
				,MSG1 + "('"+Mod200Key.I0389.getDescription()+"' sin '"+ Mod200Key.C0002.getDescription()+"')"
				,"I0389 > 0 && !C0002"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0390
				,MSG1 + "('"+Mod200Key.D0390.getDescription()+"' sin '"+ Mod200Key.C0002.getDescription()+"')"
				,"D0390 > 0 && !C0002"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0371
				,MSG1 + "('"+Mod200Key.I0371.getDescription()+"' sin '"+ Mod200Key.C0003.getDescription()+"')"
				,"I0371 > 0 && !C0003"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0311
				,MSG1 + "('"+Mod200Key.I0311.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"I0311 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0313
				,MSG1 + "('"+Mod200Key.I0313.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"I0313 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0323
				,MSG1 + "('"+Mod200Key.I0323.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"I0323 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0312
				,MSG1 + "('"+Mod200Key.D0312.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"D0312 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0314
				,MSG1 + "('"+Mod200Key.D0314.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"D0314 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0324
				,MSG1 + "('"+Mod200Key.D0324.getDescription()+"' sin '"+ Mod200Key.C0006.getDescription()+"')"
				,"D0324 > 0 && !C0006"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0387
				,MSG1 + "('"+Mod200Key.I0387.getDescription()+"' sin '"+ Mod200Key.C0007.getDescription()+"')"
				,"I0387 > 0 && !C0007"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0388
				,MSG1 + "('"+Mod200Key.D0388.getDescription()+"' sin '"+ Mod200Key.C0007.getDescription()+"')"
				,"D0388 > 0 && !C0007"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0396
				,MSG1 + "('"+Mod200Key.D0396.getDescription()+"' sin '"+ Mod200Key.C0005.getDescription()+"')"
				,"D0396 > 0 && !C0005"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0385
				,MSG1 + "('"+Mod200Key.I0385.getDescription()+"' sin '"+ Mod200Key.C0011.getDescription()+"')"
				,"I0385 > 0 && !C0011"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0386
				,MSG1 + "('"+Mod200Key.D0386.getDescription()+"' sin '"+ Mod200Key.C0011.getDescription()+"')"
				,"D0386 > 0 && !C0011"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0400
				,MSG1 + "('"+Mod200Key.D0400.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0017.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0018.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0019.getDescription()+"'" 
				+")"
				,"D0400 > 0 && !(C0017 || C0018 || C0019)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0379
				,MSG1 + "('"+Mod200Key.I0379.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0020.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0025.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0037.getDescription()+"'" 
				+")"
				,"I0379 > 0 && !(C0020 || C0035 || C0037)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0380
				,MSG1 + "('"+Mod200Key.D0380.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0020.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0025.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0037.getDescription()+"'" 
				+")"
				,"D0380 > 0 && !(C0020 || C0035 || C0037)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0397
				,MSG1 + "('"+Mod200Key.I0397.getDescription()+"' sin '"+ Mod200Key.C0022.getDescription()+"')"
				,"I0397 > 0 && !C0022"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0398
				,MSG1 + "('"+Mod200Key.D0398.getDescription()+"' sin '"+ Mod200Key.C0022.getDescription()+"')"
				,"D0398 > 0 && !C0022"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0373
				,MSG1 + "('"+Mod200Key.I0373.getDescription()+"' sin '"+ Mod200Key.C0024.getDescription()+"')"
				,"I0373 > 0 && !C0024"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0374
				,MSG1 + "('"+Mod200Key.D0374.getDescription()+"' sin '"+ Mod200Key.C0024.getDescription()+"')"
				,"D0374 > 0 && !C0024"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0403
				,MSG1 + "('"+Mod200Key.I0403.getDescription()+"' sin '"+ Mod200Key.C0029.getDescription()+"')"
				,"I0403 > 0 && !C0029"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0404
				,MSG1 + "('"+Mod200Key.D0404.getDescription()+"' sin '"+ Mod200Key.C0029.getDescription()+"')"
				,"D0404 > 0 && !C0029"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0377
				,MSG1 + "('"+Mod200Key.I0377.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0031.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0032.getDescription()+"'" 
				+")"
				,"I0377 > 0 && !(C0031 || C0032)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0378
				,MSG1 + "('"+Mod200Key.D0378.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0031.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0032.getDescription()+"'" 
				,"D0378 > 0 && !(C0031 || C0032)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0381
				,MSG1 + "('"+Mod200Key.I0381.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0033.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0034.getDescription()+"'" 
				+")"
				,"I0381 > 0 && !(C0033 || C0034)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0382
				,MSG1 + "('"+Mod200Key.D0382.getDescription()+"' sin " 
						+ "'" + Mod200Key.C0033.getDescription()+"'" 
						+ "ni '" + Mod200Key.C0034.getDescription()+"'" 
				+")"
				,"D0382 > 0 && !(C0033 || C0034)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0383
				,MSG1 + "('"+Mod200Key.I0383.getDescription()+"' sin '"+ Mod200Key.C0034.getDescription()+"')"
				,"I0383 > 0 && !C0034"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0384
				,MSG1 + "('"+Mod200Key.D0384.getDescription()+"' sin '"+ Mod200Key.C0034.getDescription()+"')"
				,"D0384 > 0 && !C0034"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0368
				,MSG1 + "('"+Mod200Key.D0368.getDescription()+"' sin '"+ Mod200Key.C0036.getDescription()+"')"
				,"D0368 > 0 && !C0036"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0409
				,MSG1 + "('"+Mod200Key.I0409.getDescription()+"' sin '"+ Mod200Key.C0046.getDescription()+"')"
				,"I0409 > 0 && !C0046"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0410
				,MSG1 + "('"+Mod200Key.D0410.getDescription()+"' sin '"+ Mod200Key.C0046.getDescription()+"')"
				,"D0410 > 0 && !C0046"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0411
				,MSG1 + "('"+Mod200Key.I0411.getDescription()+"' sin '"+ Mod200Key.C0047.getDescription()+"')"
				,"I0411 > 0 && !C0047"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0412
				,MSG1 + "('"+Mod200Key.D0412.getDescription()+"' sin '"+ Mod200Key.C0047.getDescription()+"')"
				,"D0412 > 0 && !C0047"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
				,MSG2
				,"(PG326 >= 0 && LQ301 != 0)"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ302
				,MSG2
				,"(PG326 >= 0 && LQ302 != PG326)"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ302
				,MSG2
				,"(PG326 < 0 && LQ302 != 0)"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
				,MSG2
				,"(PG326 < 0 && LQ301 != PG326)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ302
				,"No pueden tener contenido simult\u00E1neamente las dos casillas correspondientes a aumentos y disminuciones del Impuesto sobre Sociedades."
				,"(LQ301 > 0 && LQ302 > 0)"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ641
				,MSG3
				, "LQ641 > LQ640")); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ644
				,MSG3
				,"LQ644 > LQ643"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ647
				,MSG3
				,"LQ647 > LQ646"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ650
				,MSG3
				,"LQ650 > LQ649"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ653
				,MSG3
				,"LQ653 > LQ652"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ656
				,MSG3
				,"LQ656 > LQ655"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ659
				,MSG3
				,"LQ659 > LQ658"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ662
				,MSG3
				,"LQ662 > LQ661"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ665
				,MSG3
				,"LQ665 > LQ664"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ668
				,MSG3
				,"LQ668 > LQ667"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ747
				,MSG3
				,"LQ747 > LQ743"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ276
				,MSG3
				,"LQ276 > LQ275"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ609
				,MSG3
				,"LQ609 > LQ608"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ705
				,MSG3
				,"LQ705 > LQ704"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ014
				,MSG3
				,"LQ014 > LQ013"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ726
				,MSG3
				,"LQ726 > LQ725"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ535
				,MSG3
				,"LQ535 > LQ534"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ547
				,MSG3
				,"LQ547 > LQ670"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN567
				,MSG4
				,"BN567 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN568
				,MSG4
				,"BN568 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN563
				,MSG4
				,"BN563 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN566
				,MSG4
				,"BN566 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN576
				,MSG4
				,"BN576 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN569
				,MSG4
				,"BN569 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN570
				,MSG4
				,"BN570 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN571
				,MSG4
				,"BN571 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN572
				,MSG4
				,"BN572 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN573
				,MSG4
				,"BN573 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN575
				,MSG4
				,"BN575 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN577
				,MSG4
				,"BN577 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN581
				,MSG4
				,"BN581 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN582
				,"'"+Mod200Key.BN582.getDescription() +  "' mayor que '" + Mod200Key.LQ562.getDescription()+"'"
				,"BN582>LQ562"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN567
				, Mod200Key.BN567.getDescription() + " mayor que el 50 por 100 de " + Mod200Key.LQ562.getDescription()
				,"BN567 > (LQ562 / 2)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN563
				,Mod200Key.BN563.getDescription() + " no procede. Caracter '" + Mod200Key.C0029.getDescription() + "' no marcado." 
				,"BN563 > 0 && !C0029"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN566
				,Mod200Key.BN566.getDescription() + " no procede. Caracter '" 
				+ Mod200Key.C0017.getDescription() + "' o '"+Mod200Key.C0018.getDescription()+"' no marcado." 
				,"BN566 > 0 && !(C0017 || C0018)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN576
				,Mod200Key.BN576.getDescription() + " no procede. Caracter '" 
				+ Mod200Key.C0038.getDescription() +"' no marcado." 
				,"BN576 > 0 && !C0038"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN575
				,Mod200Key.BN575.getDescription() + " no procede. Caracter '" 
				+ Mod200Key.C0007.getDescription() +"' no marcado." 
				,"BN575 > 0 && !C0007"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN581
				,Mod200Key.BN581.getDescription() + " no procede. Caracter '" 
				+ Mod200Key.C0015.getDescription() +"' no marcado." 
				,"BN581 > 0 && !C0015"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN581
				,MSG5
				,"BN581 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN583
				,MSG5
				,"BN583 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN585
				,MSG5
				,"BN585 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN584
				,MSG5
				,"BN584 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN588
				,MSG5
				,"BN588 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN590
				,MSG5
				,"BN590 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN082
				,MSG5
				,"BN082 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN565
				,"No puede aplicarse la clave [565] mientras existan saldos pendientes "
				+ "de aplicación de deducciones por doble imposición o de deducciones "
				+ "del capítulo IV Título VI de la Ley del Impuesto"
				,"(BN565 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0))"
				+ "|| (BN565 != 0 && BN832 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN585
				,"Deducción art. 42 L.I.S. Y 36 TER LEY 43/95 (Clave 585)"
				,"BN585 < BN582"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN592
				,"Cuota líquida menor que cero"
				,"BN592 < 0"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN697
				,MSG6	
				,"BN697 > BN696")); 
			
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN847
				,MSG6	
				,"BN847 > BN846"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN283
				,MSG6	
				,"BN283 > BN282"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN703
				,MSG6	
				,"BN703 > BN702"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN187
				,MSG6	
				,"BN187 > BN071"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN026
				,MSG6	
				,"BN026 > BN025"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN715
				,MSG6	
				,"BN715 > BN714"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,Mod200Key.BN737
				,MSG6	
				,"BN737 > BN736"));
	}
*/
}
