package com.esferalia.aon.occam.impl.jooq.dao.mod200_2015;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.*;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.ValidationMessage2015;

public class Mod2002015Validation {
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
	private static final int PAGE08 = 8;
	private static final int PAGE09 = 9;
	private static final int PAGE10 = 10;
	private static final int PAGE12 = 12;
	private static final int PAGE13 = 13;

	private static final String EMPTY_BALANCE_MSG = "No se han cumplimentado datos en el Balance (Activo, patrimonio neto y pasivo).";

	private static final String EQUAL_MSG = "\"{0}\" debe igual que \"{1}\".";
	private static final String EQUAL_EXP = "round({0}) == round({1})";

	private static final String EQUAL_GREATER_MSG = "\"{0}\" debe ser mayor o igual que \"{1}\".";
	private static final String EQUAL_GREATER_EXP = "round({0}) >= round({1})";
	
	private static final String EQUAL_LESS_MSG = "\"{0}\" debe ser menor o igual que \"{1}\".";
	private static final String EQUAL_LESS_EXP = "round({0}) <= round({1})";

	private static final String EQUAL_LESS_FACTOR_MSG = "\"{0}\" debe ser menor o igual que el {2} por \"{1}\".";
	private static final String EQUAL_LESS_FACTOR_EXP = "round({0}) <= round({1} * {2})";

	private static final String MUST_EQUAL_MSG = "\"{0}\" y \"{1}\" deben ser iguales.";
	private static final String MUST_EQUAL_EXP = "round({0}) == round({1})";
	
	private static final String CHECK_SIGN_MSG = "Verifique el signo de la clave: \"{0}\"";
	private static final String MUST_NEGATIVE_EXP = "round({0}) <= 0.0";
	private static final String MUST_POSITIVE_EXP = "round({0}) >= 0.0";
	
	private static final String INV_BOX_MSG = "Casilla \"{0}\" no v\u00E1lida sin el caracter \"{1}\".";
	private static final String INV_BOX_EXP = "round({0}) == 0.0 || (round({0}) > 0.0 && {1})";
	
	private static final String INCOMPATIBLE_MSG = "Casilla \"{0}\" incompatible con \"{1}\"";
	
	private static final String MSG_581 = "Si existen deducciones por doble imposici\u00F3n pendientes "
			+ "de aplicar no podr\u00E1n aplicarse ni las bonificaciones del art\u00EDculo 76 de la Ley "
			+ "19/1994, ni las deducciones por inversiones";
	

	public static List<ValidationMessage2015> VALIDATION_EXPRESSION_LIST = new LinkedList<ValidationMessage2015>();

	//	**************************************************************************************
	//	**************************************************************************************
	//								LA CONDICIÓN DEBE CUMPLIRSE.
	//	**************************************************************************************
	//	**************************************************************************************
	
	static {	// PAGE 03	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE03,BA180
				,MessageFormat.format(MUST_EQUAL_MSG,BA180.getDescription(),BP252.getDescription())
				,MessageFormat.format(MUST_EQUAL_EXP,BA180.toString(),BP252.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE03,BA180
				,EMPTY_BALANCE_MSG
				,"round(BA180) != 0.0 && round(BP252) != 0.0 && round(BP187) != 0.0"));
	}

	
	static { // PAGE 04	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE04,BP189
				,MessageFormat.format(CHECK_SIGN_MSG,BP189.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP189.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE04,BP194
				,MessageFormat.format(CHECK_SIGN_MSG,BP194.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP194.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE04,BP197
				,MessageFormat.format(CHECK_SIGN_MSG,BP197.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP197.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE04,BP200
				,MessageFormat.format(CHECK_SIGN_MSG,BP200.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP200.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE04,BP199
				,MessageFormat.format(MUST_EQUAL_MSG,BP199.getDescription(),LQ500.getDescription())
				//,"(C0003 || C0004 || C0024 || C0025 || C0036 || C0061)? true : round(BP199) == round(LQ500)"));
				,"(C0003 || C0004 || C0024 || C0025 || C0036)? true : round(BP199) == round(LQ500)"));
	}

	
	static { // PAGE 07	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC534
				,MessageFormat.format(CHECK_SIGN_MSG,TC534.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC535
				,MessageFormat.format(CHECK_SIGN_MSG,TC535.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC535.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC536
				,MessageFormat.format(CHECK_SIGN_MSG,TC536.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC536.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC537
				,MessageFormat.format(CHECK_SIGN_MSG,TC537.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC537.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC538
				,MessageFormat.format(CHECK_SIGN_MSG,TC538.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC538.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC539
				,MessageFormat.format(CHECK_SIGN_MSG,TC539.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC539.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC540
				,MessageFormat.format(CHECK_SIGN_MSG,TC540.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC540.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC541
				,MessageFormat.format(CHECK_SIGN_MSG,TC541.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC541.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC542
				,MessageFormat.format(CHECK_SIGN_MSG,TC542.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC542.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC543
				,MessageFormat.format(CHECK_SIGN_MSG,TC543.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC543.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC544
				,MessageFormat.format(CHECK_SIGN_MSG,TC544.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC544.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC545
				,MessageFormat.format(CHECK_SIGN_MSG,TC545.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC545.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC546
				,MessageFormat.format(CHECK_SIGN_MSG,TC546.toString())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC546.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC562
				,MessageFormat.format(CHECK_SIGN_MSG,TC562.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC562.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC563
				,MessageFormat.format(CHECK_SIGN_MSG,TC563.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC563.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC564
				,MessageFormat.format(CHECK_SIGN_MSG,TC564.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC564.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC565
				,MessageFormat.format(CHECK_SIGN_MSG,TC565.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC565.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC566
				,MessageFormat.format(CHECK_SIGN_MSG,TC566.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC566.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC567
				,MessageFormat.format(CHECK_SIGN_MSG,TC567.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC567.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC568
				,MessageFormat.format(CHECK_SIGN_MSG,TC568.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC568.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC569
				,MessageFormat.format(CHECK_SIGN_MSG,TC569.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC569.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC570
				,MessageFormat.format(CHECK_SIGN_MSG,TC570.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC570.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC571
				,MessageFormat.format(CHECK_SIGN_MSG,TC571.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC571.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC572
				,MessageFormat.format(CHECK_SIGN_MSG,TC572.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC572.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE07,TC574
				,MessageFormat.format(CHECK_SIGN_MSG,TC574.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC574.toString())));
	}

	
	static { // PAGE 08
		
		for (Mod2002015CorrectionKey ck : Mod2002015CorrectionKey.values()) {
			Mod2002015Key key = ck.getIncrease();
			if (key != null) {
				VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,key
						,MessageFormat.format(CHECK_SIGN_MSG,key.getDescription())
						,MessageFormat.format(MUST_POSITIVE_EXP,key.toString())));
			}
			key = ck.getDecrease();
			if (key != null) {
				VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,key
						,MessageFormat.format(CHECK_SIGN_MSG,key.getDescription())
						,MessageFormat.format(MUST_POSITIVE_EXP,key.toString())));
			}
		}
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0417
				,MessageFormat.format(CHECK_SIGN_MSG,I0417.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,I0417.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0418
				,MessageFormat.format(CHECK_SIGN_MSG,D0418.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,D0418.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0391
				,MessageFormat.format(INV_BOX_MSG,I0391.getDescription(),C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0391.toString(),C0001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0392
				,MessageFormat.format(INV_BOX_MSG,D0392.getDescription(),C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0392.toString(),C0001.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0389
//				,MessageFormat.format(INV_BOX_MSG,D0392.getDescription(),C0002.getDescription())
//				,MessageFormat.format(INV_BOX_EXP,D0392.toString(),C0002.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0390
//				,MessageFormat.format(INV_BOX_MSG,D0390.getDescription(),C0002.getDescription())
//				,MessageFormat.format(INV_BOX_EXP,D0390.toString(),C0002.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0371
				,MessageFormat.format(INV_BOX_MSG,I0371.getDescription(),C0003.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0371.toString(),C0003.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0311
				,MessageFormat.format(INV_BOX_MSG,I0311.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0311.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0313
				,MessageFormat.format(INV_BOX_MSG,I0313.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0313.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0323
				,MessageFormat.format(INV_BOX_MSG,I0323.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0323.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0312
				,MessageFormat.format(INV_BOX_MSG,D0312.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0312.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0314
				,MessageFormat.format(INV_BOX_MSG,D0314.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0314.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0324
				,MessageFormat.format(INV_BOX_MSG,D0324.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0324.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0387
				,MessageFormat.format(INV_BOX_MSG,I0387.getDescription(),C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0387.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0388
				,MessageFormat.format(INV_BOX_MSG,D0388.getDescription(),C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0388.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0396
				,MessageFormat.format(INV_BOX_MSG,D0396.getDescription(),C0005.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0396.toString(),C0005.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0385
				,MessageFormat.format(INV_BOX_MSG,I0385.getDescription(),C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0385.toString(),C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0386
				,MessageFormat.format(INV_BOX_MSG,D0386.getDescription(),C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0386.toString(),C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0397
				,MessageFormat.format(INV_BOX_MSG,I0397.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0397.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0398
				,MessageFormat.format(INV_BOX_MSG,D0398.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0398.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0373
				,MessageFormat.format(INV_BOX_MSG,I0373.getDescription(),C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0373.toString(),C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0374
				,MessageFormat.format(INV_BOX_MSG,D0374.getDescription(),C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0374.toString(),C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0403
				,MessageFormat.format(INV_BOX_MSG,I0403.getDescription(),C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0403.toString(),C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0404
				,MessageFormat.format(INV_BOX_MSG,D0404.getDescription(),C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0404.toString(),C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0383
				,MessageFormat.format(INV_BOX_MSG,I0383.getDescription(),C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0383.toString(),C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0384
				,MessageFormat.format(INV_BOX_MSG,D0384.getDescription(),C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0384.toString(),C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0368
				,MessageFormat.format(INV_BOX_MSG,D0368.getDescription(),C0036.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0368.toString(),C0036.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0409
				,MessageFormat.format(INV_BOX_MSG,I0409.getDescription(),C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0409.toString(),C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0410
				,MessageFormat.format(INV_BOX_MSG,D0410.getDescription(),C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0410.toString(),C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0411
				,MessageFormat.format(INV_BOX_MSG,I0411.getDescription(),C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0411.toString(),C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0412
				,MessageFormat.format(INV_BOX_MSG,D0412.getDescription(),C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0412.toString(),C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0400
			,MessageFormat.format(INV_BOX_MSG,D0400.getDescription(),
				  C0017.getDescription()+"\" ni \"" 
				+ C0018.getDescription()+"\" \"ni \"" 
				+ C0019.getDescription())
			,"round(D0400) == 0.0 || (round(D0400) > 0.0 && (C0017 || C0018 || C0019))"));

//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0379
//			,MessageFormat.format(INV_BOX_MSG,I0379.getDescription(),
//				C0020.getDescription()+"\" ni \"" 
//				+ C0035.getDescription()+"\" \"ni \"" 
//				+ C0037.getDescription())
//			,"round(I0379) == 0.0 || (round(I0379) > 0.0 && (C0020 || C0035 || C0037))"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0380
//			,MessageFormat.format(INV_BOX_MSG,D0380.getDescription(),
//				C0020.getDescription()+"\" ni \"" 
//				+ C0035.getDescription()+"\" \"ni \"" 
//				+ C0037.getDescription())
//			,"round(D0380) == 0.0 || (round(D0380) > 0.0 && (C0020 || C0035 || C0037))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0377
			,MessageFormat.format(INV_BOX_MSG,I0377.getDescription(),
				C0031.getDescription()+"\" \"ni \"" + C0032.getDescription())
			,"round(I0377) == 0.0 || (round(I0377) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0378
			,MessageFormat.format(INV_BOX_MSG,D0378.getDescription(),
				C0031.getDescription()+"\" \"ni \"" + C0032.getDescription())
			,"round(D0378) == 0.0 || (round(D0378) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0381
			,MessageFormat.format(INV_BOX_MSG,I0381.getDescription(),
				C0033.getDescription()+"\" \"ni \"" + C0034.getDescription())
			,"round(I0381) == 0.0 || (round(I0381) > 0.0 && (C0033 || C0034))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0382
			,MessageFormat.format(INV_BOX_MSG,D0382.getDescription(),
				C0033.getDescription()+"\" \"ni \"" + C0034.getDescription())
			,"round(D0382) == 0.0 || (round(D0382) > 0.0 && (C0033 || C0034))"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,LQ301
				,MessageFormat.format(INCOMPATIBLE_MSG,LQ301.getDescription(),PG326.getDescription())
				,"!(round(PG326) >= 0.0 && round(LQ301) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,LQ301
				,MessageFormat.format(MUST_EQUAL_MSG,LQ301.getDescription(),PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ301) != round(PG326 * -1))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,LQ302
				,MessageFormat.format(MUST_EQUAL_MSG,LQ302.getDescription(),PG326.getDescription())
				,"!(round(PG326) >= 0.0 && round(LQ302) != round(PG326))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,LQ302
				,MessageFormat.format(INCOMPATIBLE_MSG,LQ302.getDescription(),PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ302) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,LQ302
				,"No pueden tener contenido simult\u00E1neamente las dos casillas correspondientes a aumentos y disminuciones del Impuesto sobre Sociedades."
				,"!(round(LQ301) > 0 && round(LQ302) > 0)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0504
//				,"Confirme la procedencia del ajuste consignado en las claves 504 y/o 505"
//				,"(C0006 || C0056) && (round(I0504) != 0 || round(D0505) != 0)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0369
//				,MessageFormat.format(INCOMPATIBLE_MSG,I0369.getDescription()
//					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
//				,"round(I0369) != 0.0?(C0013 || C0014):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0370
//				,MessageFormat.format(INCOMPATIBLE_MSG,D0370.getDescription()
//					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
//				,"round(D0370) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,I0256
				,MessageFormat.format(INCOMPATIBLE_MSG,I0256.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(I0256) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0278
				,MessageFormat.format(INCOMPATIBLE_MSG,D0278.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(D0278) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0400
				, "S\u00F3lo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes despu\u00E9s de deducir las p\u00E9rdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0050"
							+ "?((round(PG500) + (round(BP197)<0?round(BP197):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0400
				, "S\u00F3lo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes despu\u00E9s de deducir las p\u00E9rdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0051"
							+ "?((round(PG500) + (round(BP195)<0?round(BP195):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE08,D0404
				,"Revise importe disminuciones RIC"
				,"round(D0404) == 0.0 || round(D0404) >= round((LQ650 * 0.90))"));
	}
	
	static {	// PAGE 09
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ578
				,MessageFormat.format(INV_BOX_MSG,LQ578.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,LQ578.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ579
				,MessageFormat.format(INV_BOX_MSG,LQ579.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,LQ579.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ631
				,MessageFormat.format(EQUAL_GREATER_MSG,LQ631.getDescription(),LQ632.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ631.toString(),LQ632.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ552
//				,LQ552.getDescription() + " debe ser cero con el caracter \"" + C0027.getDescription() + "\" marcado"
//				,"C0027?LQ552<=0:LQ552>0"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_MSG,LQ641.getDescription(),LQ640.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ641.toString(),LQ640.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_MSG,LQ644.getDescription(),LQ643.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ644.toString(),LQ643.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_MSG,LQ647.getDescription(),LQ646.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ647.toString(),LQ646.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_MSG,LQ650.getDescription(),LQ649.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ650.toString(),LQ649.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_MSG,LQ653.getDescription(),LQ652.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ653.toString(),LQ652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_MSG,LQ656.getDescription(),LQ655.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ656.toString(),LQ655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_MSG,LQ659.getDescription(),LQ658.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ659.toString(),LQ658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_MSG,LQ662.getDescription(),LQ661.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ662.toString(),LQ661.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_MSG,LQ665.getDescription(),LQ664.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ665.toString(),LQ664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_MSG,LQ668.getDescription(),LQ667.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ668.toString(),LQ667.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_MSG,LQ747.getDescription(),LQ743.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ747.toString(),LQ743.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_MSG,LQ276.getDescription(),LQ275.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ276.toString(),LQ275.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_MSG,LQ609.getDescription(),LQ608.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ609.toString(),LQ608.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_MSG,LQ705.getDescription(),LQ704.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ705.toString(),LQ704.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_MSG,LQ014.getDescription(),LQ013.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ014.toString(),LQ013.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_MSG,LQ726.getDescription(),LQ725.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ726.toString(),LQ725.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_MSG,LQ535.getDescription(),LQ534.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ535.toString(),LQ534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ675
				,MessageFormat.format(EQUAL_LESS_MSG,LQ675.getDescription(),LQ607.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ675.toString(),LQ607.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ547
				,MessageFormat.format(EQUAL_LESS_MSG,LQ547.getDescription(),LQ670.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ547.toString(),LQ670.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ641.getDescription(),LQ640.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ641.toString(),LQ640.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ644.getDescription(),LQ643.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ644.toString(),LQ643.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ647.getDescription(),LQ646.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ647.toString(),LQ646.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ650.getDescription(),LQ649.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ650.toString(),LQ649.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ653.getDescription(),LQ652.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ653.toString(),LQ652.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ656.getDescription(),LQ655.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ656.toString(),LQ655.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ659.getDescription(),LQ658.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ659.toString(),LQ658.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ662.getDescription(),LQ661.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ662.toString(),LQ661.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ665.getDescription(),LQ664.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ665.toString(),LQ664.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ668.getDescription(),LQ667.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ668.toString(),LQ667.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ747.getDescription(),LQ743.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ747.toString(),LQ743.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ276.getDescription(),LQ275.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ276.toString(),LQ275.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ609.getDescription(),LQ608.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ609.toString(),LQ608.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ705.getDescription(),LQ704.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ705.toString(),LQ704.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ014.getDescription(),LQ013.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ014.toString(),LQ013.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ726.getDescription(),LQ725.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ726.toString(),LQ725.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ535.getDescription(),LQ534.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ535.toString(),LQ534.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ553
				,"La suma de las casillas \""+LQ553.getDescription()+"\" y \""+LQ554.getDescription()+"\" debe ser igual que \""+LQ552.getDescription()+"\""	
				,"(C0017 || C0018 || C0019)?LQ552 == (LQ553 + LQ554):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ559
				,"La casilla \""+LQ559.getDescription()+"\" debe ser mayor igual que cero y menor o igual que la casilla \""+LQ552.getDescription()+"\""
				,"C0015?(0.0 <= round(LQ559) && round(LQ559) <= round(LQ552)):true"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ674
				,MessageFormat.format(EQUAL_GREATER_MSG,"673","674")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ673.toString(),LQ674.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ677
				,MessageFormat.format(EQUAL_GREATER_MSG,"676","677")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ676.toString(),LQ677.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ680
				,MessageFormat.format(EQUAL_GREATER_MSG,"679","680")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ679.toString(),LQ680.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ683
				,MessageFormat.format(EQUAL_GREATER_MSG,"682","683")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ682.toString(),LQ683.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ686
				,MessageFormat.format(EQUAL_GREATER_MSG,"685","686")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ685.toString(),LQ686.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ689
				,MessageFormat.format(EQUAL_GREATER_MSG,"688","689")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ688.toString(),LQ689.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ692
				,MessageFormat.format(EQUAL_GREATER_MSG,"691","692")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ691.toString(),LQ692.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ099
				,MessageFormat.format(EQUAL_GREATER_MSG,"059","099")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ059.toString(),LQ099.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ018
				,MessageFormat.format(EQUAL_GREATER_MSG,"017","018")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ017.toString(),LQ018.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ773
				,MessageFormat.format(EQUAL_GREATER_MSG,"772","773")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ772.toString(),LQ773.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ908
				,MessageFormat.format(EQUAL_GREATER_MSG,"907","908")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ907.toString(),LQ908.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ911
				,MessageFormat.format(EQUAL_GREATER_MSG,"910","911")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ910.toString(),LQ911.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ936
				,MessageFormat.format(EQUAL_GREATER_MSG,"935","936")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ935.toString(),LQ936.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ515
				,MessageFormat.format(EQUAL_GREATER_MSG,"587","515")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ587.toString(),LQ515.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ280
				,MessageFormat.format(EQUAL_GREATER_MSG,"279","280")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ279.toString(),LQ280.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE09,LQ624
				,MessageFormat.format(EQUAL_GREATER_MSG,"623","624")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ623.toString(),LQ624.toString())));
	}
	
	static {// PAGE 10
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN567
				,MessageFormat.format(EQUAL_GREATER_MSG,BN567.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN567.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN568
				,MessageFormat.format(EQUAL_GREATER_MSG,BN568.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN568.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN563
				,MessageFormat.format(EQUAL_GREATER_MSG,BN563.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN563.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN566
				,MessageFormat.format(EQUAL_GREATER_MSG,BN566.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN566.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN576
				,MessageFormat.format(EQUAL_GREATER_MSG,BN576.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN576.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN569
				,MessageFormat.format(EQUAL_GREATER_MSG,BN569.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN569.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN570
				,MessageFormat.format(EQUAL_GREATER_MSG,BN570.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN570.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN571
				,MessageFormat.format(EQUAL_GREATER_MSG,BN571.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN571.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN572
				,MessageFormat.format(EQUAL_GREATER_MSG,BN572.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN572.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN573
				,MessageFormat.format(EQUAL_GREATER_MSG,BN573.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN573.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN575
				,MessageFormat.format(EQUAL_GREATER_MSG,BN575.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN575.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN577
				,MessageFormat.format(EQUAL_GREATER_MSG,BN577.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN577.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN581
				,MessageFormat.format(EQUAL_GREATER_MSG,BN581.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN581.toString(),"0")));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN582
				,MessageFormat.format(EQUAL_LESS_MSG,BN582.getDescription(),LQ562.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN582.toString(),LQ562.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN563
				,MessageFormat.format(EQUAL_GREATER_MSG,BN563.getDescription(),C0029.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN563.toString(),C0029.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN566
				,BN566.getDescription() + " no procede. Caracter '" + C0017.getDescription() + "' o '"+C0018.getDescription()+"' no marcado." 
				,"(BN566 == 0.0) || (BN566 > 0 && (C0017 || C0018))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN576
				,MessageFormat.format(EQUAL_GREATER_MSG,BN576.getDescription(),C0038.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN576.toString(),C0038.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN575
				,MessageFormat.format(EQUAL_GREATER_MSG,BN575.getDescription(),C0007.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN575.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN581
				,MessageFormat.format(EQUAL_GREATER_MSG,BN581.getDescription(),C0015.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN581.toString(),C0015.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN581,MSG_581
				,"BN581 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN583,MSG_581
				,"BN583 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN585,MSG_581
				,"BN585 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN584,MSG_581
				,"BN584 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN588,MSG_581
				,"BN588 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN590,MSG_581
				,"BN590 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN082,MSG_581
				,"BN082 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));

//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN697
//				,MessageFormat.format(EQUAL_LESS_MSG,BN697.getDescription(),BN696.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,BN697.toString(),BN696.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN847
				,MessageFormat.format(EQUAL_LESS_MSG,BN847.getDescription(),BN846.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN847.toString(),BN846.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN283
				,MessageFormat.format(EQUAL_LESS_MSG,BN283.getDescription(),BN282.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN283.toString(),BN282.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN703
				,MessageFormat.format(EQUAL_LESS_MSG,BN703.getDescription(),BN702.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN703.toString(),BN702.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN187
				,MessageFormat.format(EQUAL_LESS_MSG,BN187.getDescription(),BN071.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN187.toString(),BN071.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN026
				,MessageFormat.format(EQUAL_LESS_MSG,BN026.getDescription(),BN025.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN026.toString(),BN025.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN715
				,MessageFormat.format(EQUAL_LESS_MSG,BN715.getDescription(),BN714.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN715.toString(),BN714.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN737
				,MessageFormat.format(EQUAL_LESS_MSG,BN737.getDescription(),BN736.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN737.toString(),BN736.toString())));
	
		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN712
//				,MessageFormat.format(EQUAL_LESS_MSG,BN712.getDescription(),BN711.getDescription()) 
//				,MessageFormat.format(EQUAL_LESS_EXP,BN712.toString(),BN711.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN638
				,MessageFormat.format(EQUAL_LESS_MSG,BN638.getDescription(),BN637.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN638.toString(),BN637.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN894
				,MessageFormat.format(EQUAL_LESS_MSG,BN894.getDescription(),BN849.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN894.toString(),BN849.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN286
				,MessageFormat.format(EQUAL_LESS_MSG,BN286.getDescription(),BN285.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN286.toString(),BN285.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN826
				,MessageFormat.format(EQUAL_LESS_MSG,BN826.getDescription(),BN825.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN826.toString(),BN825.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN002
				,MessageFormat.format(EQUAL_LESS_MSG,BN002.getDescription(),BN001.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN002.toString(),BN001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN029
				,MessageFormat.format(EQUAL_LESS_MSG,BN029.getDescription(),BN028.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN029.toString(),BN028.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN718
				,MessageFormat.format(EQUAL_LESS_MSG,BN718.getDescription(),BN717.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN718.toString(),BN717.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN723
				,MessageFormat.format(EQUAL_LESS_MSG,BN723.getDescription(),BN722.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN723.toString(),BN722.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN741
				,MessageFormat.format(EQUAL_LESS_MSG,BN741.getDescription(),BN740.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN741.toString(),BN740.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN136
				,MessageFormat.format(EQUAL_LESS_MSG,BN136.getDescription(),BN135.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN136.toString(),BN135.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN585
				,MessageFormat.format(EQUAL_LESS_MSG,BN585.getDescription(),BN582.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN585.toString(),BN582.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE10,BN592
				,MessageFormat.format(EQUAL_GREATER_MSG,BN592.getDescription(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN592.toString(),"0")));
	}

	static {	// PAGE 11
	}

	static {	// PAGE 12
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID653
				,MessageFormat.format(EQUAL_MSG,ID653.getDescription() + " (653)",ID666.getDescription()+ "(666)")
				,MessageFormat.format(EQUAL_EXP,ID653.toString(),ID666.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID650
				,MessageFormat.format(CHECK_SIGN_MSG,ID650.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID650.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID651
				,MessageFormat.format(CHECK_SIGN_MSG,ID651.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID651.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID652
				,MessageFormat.format(CHECK_SIGN_MSG,ID652.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID666
				,MessageFormat.format(CHECK_SIGN_MSG,ID666.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID666.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID654
				,MessageFormat.format(CHECK_SIGN_MSG,ID654.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID654.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID655
				,MessageFormat.format(CHECK_SIGN_MSG,ID655.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID656
				,MessageFormat.format(CHECK_SIGN_MSG,ID656.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID656.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID658
				,MessageFormat.format(CHECK_SIGN_MSG,ID658.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID659
				,MessageFormat.format(CHECK_SIGN_MSG,ID659.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID659.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID660
				,MessageFormat.format(CHECK_SIGN_MSG,ID660.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID660.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID662
				,MessageFormat.format(CHECK_SIGN_MSG,ID662.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID662.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID664
				,MessageFormat.format(CHECK_SIGN_MSG,ID664.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE12,ID665
				,MessageFormat.format(CHECK_SIGN_MSG,ID665.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID665.toString())));
	}

	static { // PAGE 13 		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM259
//				,"La casilla 259 debe ser menor o igual que la suma de las casillas 043 y 049" 
//				,"LM259 <= (LM043+LM049)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM254
//				,MessageFormat.format(EQUAL_LESS_MSG,LM254.getDescription(),LM253.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM254.toString(),LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255
//				,MessageFormat.format(EQUAL_LESS_MSG,LM255.getDescription(),LM253.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM255.toString(),LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM260
//				,MessageFormat.format(EQUAL_LESS_MSG,LM260.getDescription(),LM259.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM260.toString(),LM259.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM049
//				,MessageFormat.format(CHECK_SIGN_MSG,LM049.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM049.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM249
//				,MessageFormat.format(CHECK_SIGN_MSG,LM249.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM249.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM252
//				,MessageFormat.format(CHECK_SIGN_MSG,LM252.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM252.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM253
//				,MessageFormat.format(CHECK_SIGN_MSG,LM253.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM254
//				,MessageFormat.format(CHECK_SIGN_MSG,LM254.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM254.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255
//				,MessageFormat.format(CHECK_SIGN_MSG,LM255.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM255.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM258
//				,MessageFormat.format(CHECK_SIGN_MSG,LM258.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM258.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM259
//				,MessageFormat.format(CHECK_SIGN_MSG,LM259.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM259.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM260
//				,MessageFormat.format(CHECK_SIGN_MSG,LM260.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM260.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM175 
//				,MessageFormat.format(EQUAL_MSG,LM175.getDescription() + " (175)",PG296.getDescription()+ "(PYG - 296)")
//				,MessageFormat.format(EQUAL_EXP,LM175.toString(),PG296.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM176 
//				,MessageFormat.format(EQUAL_MSG,LM176.getDescription() + " (176)",PG284.getDescription()+ "(PYG - 284)")
//				,MessageFormat.format(EQUAL_EXP,LM176.toString(),PG284.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM177 
//				,MessageFormat.format(EQUAL_MSG,LM177.getDescription() + " (177)",PG285.getDescription()+ "(PYG - 285)")
//				,MessageFormat.format(EQUAL_EXP,LM177.toString(),PG285.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM178 
//				,MessageFormat.format(EQUAL_MSG,LM178.getDescription() + " (178)",PG287.getDescription()+ "(PYG - 287)")
//				,MessageFormat.format(EQUAL_EXP,LM178.toString(),PG287.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM179 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM179.getDescription() + " (179)",PG298.getDescription()+ "(PYG - 298)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM179.toString(),PG298.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM254
//				,"\"" + LM254.getDescription() +"\" debe ser menor o igual que la suma de \"" 
//				+ LM043.getDescription() + "\" y \""
//				+ LM049.getDescription() + "\"" 
//				,"LM254<=LM043+LM049"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM254
//				,"Si el importe de la casilla 254 es menor que la suma de los importes de las "
//				+ "casillas 043 + 049, dicho importe debe ser igual al de la casilla 253" 
//				,"LM254<(LM043+LM049)?LM254==LM253:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255
//				,"El importe de la casilla 255 debe ser igual a la diferencia "
//				+ "de las casillas 253 menos 254" 
//				,"LM255==(LM253-LM254)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255
//				,"Si el importe de la casilla 255 es mayor que cero, la casilla 258 debe ser cero" 
//				,"LM255>0?LM258==0:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255
//				,"Si el importe de la casilla 043 es mayor que la suma de las casillas 254 y 258, la casilla 049 debe ser cero" 
//				,"(LM254+LM258)<=LM043?LM049==0:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM255 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM255.getDescription() 
//				+ " (255)",Mod2002014CorrectionKey.C0040.getDescription()+ " (Correcciones Contables - 363)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM255.toString(),I0363.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM258 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM258.getDescription() 
//				+ " (258)",Mod2002014CorrectionKey.C0040.getDescription()+ "(Correcciones Contables - 364)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM258.toString(),D0364.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM970
//				,MessageFormat.format(EQUAL_LESS_MSG,LM970.getDescription(),LM969.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM970.toString(),LM969.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM262
//				,MessageFormat.format(EQUAL_LESS_MSG,LM262.getDescription(),LM261.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM262.toString(),LM261.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM265
//				,MessageFormat.format(EQUAL_LESS_MSG,LM265.getDescription(),LM264.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM265.toString(),LM264.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM266
//				,"Compruebe los gastos financieros pendientes de deducir. " 
//				+MessageFormat.format(MUST_EQUAL_MSG,LM266.getDescription(),LM255.getDescription())
//				,MessageFormat.format(MUST_EQUAL_EXP,LM266.toString(),LM255.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM258
//				,"Compruebe los gastos financieros pendientes de deducir de per\u00EDodos "
//				+ "anteriores aplicados en esta liquidaci\u00F3n, claves 258, 970 y 262" 
//				,"LM258==(LM970+LM262)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM522
//				,MessageFormat.format(EQUAL_LESS_MSG,LM522.getDescription(),LM503.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM522.toString(),LM503.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM271
//				,MessageFormat.format(EQUAL_LESS_MSG,LM271.getDescription(),LM270.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM271.toString(),LM270.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM274
//				,MessageFormat.format(EQUAL_LESS_MSG,LM274.getDescription(),LM273.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM274.toString(),LM273.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2015(PAGE13,LM957
//				,"Compruebe el importe pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado"
//				,"LM957==(LM043+LM049)-LM254-LM258"));
	}

	
}
