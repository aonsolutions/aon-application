package com.esferalia.aon.gwt.fiscal.server.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.*;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200CorrectionKey;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.ValidationMessage;

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
	private static final int PAGE08 = 8;
	private static final int PAGE09 = 9;

	private static final String EMPTY_BALANCE_MSG = "No se han cumplimentado datos en el Balance (Activo, patrimonio neto y pasivo).";

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

	public static List<ValidationMessage> VALIDATION_EXPRESSION_LIST = new LinkedList<ValidationMessage>();

	//	**************************************************************************************
	//	**************************************************************************************
	//								LA CONDICIÓN DEBE CUMPLIRSE.
	//	**************************************************************************************
	//	**************************************************************************************
	
	static {	// PAGE 03	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,BA180
				,MessageFormat.format(MUST_EQUAL_MSG,BA180.getDescription(),BP252.getDescription())
				,MessageFormat.format(MUST_EQUAL_EXP,BA180.toString(),BP252.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,BA180
				,EMPTY_BALANCE_MSG
				,"round(BA180) != 0.0 && round(BP252) != 0.0"));
	}

	
	static { // PAGE 04	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,BP189
				,MessageFormat.format(CHECK_SIGN_MSG,BP189.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP189.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,BP194
				,MessageFormat.format(CHECK_SIGN_MSG,BP194.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP194.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,BP197
				,MessageFormat.format(CHECK_SIGN_MSG,BP197.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP197.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,BP200
				,MessageFormat.format(CHECK_SIGN_MSG,BP200.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,BP200.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,BP199
				,MessageFormat.format(MUST_EQUAL_MSG,BP199.getDescription(),LQ500.getDescription())
				,"(C0003 || C0004 || C0024 || C0025 || C0036 || C0061)? true : round(BP199) == round(LQ500)"));
	}

	
	static { // PAGE 07	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC534
				,MessageFormat.format(CHECK_SIGN_MSG,TC534.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC535
				,MessageFormat.format(CHECK_SIGN_MSG,TC535.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC535.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC536
				,MessageFormat.format(CHECK_SIGN_MSG,TC536.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC536.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC537
				,MessageFormat.format(CHECK_SIGN_MSG,TC537.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC537.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC538
				,MessageFormat.format(CHECK_SIGN_MSG,TC538.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC538.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC539
				,MessageFormat.format(CHECK_SIGN_MSG,TC539.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC539.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC540
				,MessageFormat.format(CHECK_SIGN_MSG,TC540.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC540.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC541
				,MessageFormat.format(CHECK_SIGN_MSG,TC541.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC541.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC542
				,MessageFormat.format(CHECK_SIGN_MSG,TC542.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC542.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC543
				,MessageFormat.format(CHECK_SIGN_MSG,TC543.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC543.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC544
				,MessageFormat.format(CHECK_SIGN_MSG,TC544.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC544.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC545
				,MessageFormat.format(CHECK_SIGN_MSG,TC545.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC545.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC546
				,MessageFormat.format(CHECK_SIGN_MSG,TC546.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC546.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC562
				,MessageFormat.format(CHECK_SIGN_MSG,TC562.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC562.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC563
				,MessageFormat.format(CHECK_SIGN_MSG,TC563.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC563.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC564
				,MessageFormat.format(CHECK_SIGN_MSG,TC564.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC564.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC565
				,MessageFormat.format(CHECK_SIGN_MSG,TC565.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC565.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC566
				,MessageFormat.format(CHECK_SIGN_MSG,TC566.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC566.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC567
				,MessageFormat.format(CHECK_SIGN_MSG,TC567.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC567.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC568
				,MessageFormat.format(CHECK_SIGN_MSG,TC568.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC568.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC569
				,MessageFormat.format(CHECK_SIGN_MSG,TC569.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC569.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC570
				,MessageFormat.format(CHECK_SIGN_MSG,TC570.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC570.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC571
				,MessageFormat.format(CHECK_SIGN_MSG,TC571.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC571.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC572
				,MessageFormat.format(CHECK_SIGN_MSG,TC572.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC572.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,TC574
				,MessageFormat.format(CHECK_SIGN_MSG,TC574.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,TC574.toString())));
	}

	
	static { // PAGE 08
		
		for (Mod200CorrectionKey ck : Mod200CorrectionKey.values()) {
			Mod200Key key = ck.getIncrease();
			if (key != null) {
				VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,key
						,MessageFormat.format(CHECK_SIGN_MSG,key.getDescription())
						,MessageFormat.format(MUST_POSITIVE_EXP,key.toString())));
			}
			key = ck.getDecrease();
			if (key != null) {
				VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,key
						,MessageFormat.format(CHECK_SIGN_MSG,key.getDescription())
						,MessageFormat.format(MUST_POSITIVE_EXP,key.toString())));
			}
		}
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0417
				,MessageFormat.format(CHECK_SIGN_MSG,I0417.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,I0417.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0418
				,MessageFormat.format(CHECK_SIGN_MSG,D0418.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,D0418.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0391
				,MessageFormat.format(INV_BOX_MSG,I0391.getDescription(),C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0391.toString(),C0001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0392
				,MessageFormat.format(INV_BOX_MSG,D0392.getDescription(),C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0392.toString(),C0001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0389
				,MessageFormat.format(INV_BOX_MSG,D0392.getDescription(),C0002.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0392.toString(),C0002.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0390
				,MessageFormat.format(INV_BOX_MSG,D0390.getDescription(),C0002.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0390.toString(),C0002.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0371
				,MessageFormat.format(INV_BOX_MSG,I0371.getDescription(),C0003.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0371.toString(),C0003.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0311
				,MessageFormat.format(INV_BOX_MSG,I0311.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0311.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0313
				,MessageFormat.format(INV_BOX_MSG,I0313.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0313.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0323
				,MessageFormat.format(INV_BOX_MSG,I0323.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0323.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0312
				,MessageFormat.format(INV_BOX_MSG,D0312.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0312.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0314
				,MessageFormat.format(INV_BOX_MSG,D0314.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0314.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0324
				,MessageFormat.format(INV_BOX_MSG,D0324.getDescription(),C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0324.toString(),C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0387
				,MessageFormat.format(INV_BOX_MSG,I0387.getDescription(),C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0387.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0388
				,MessageFormat.format(INV_BOX_MSG,D0388.getDescription(),C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0388.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0396
				,MessageFormat.format(INV_BOX_MSG,D0396.getDescription(),C0005.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0396.toString(),C0005.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0385
				,MessageFormat.format(INV_BOX_MSG,I0385.getDescription(),C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0385.toString(),C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0386
				,MessageFormat.format(INV_BOX_MSG,D0386.getDescription(),C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0386.toString(),C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0397
				,MessageFormat.format(INV_BOX_MSG,I0397.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0397.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0398
				,MessageFormat.format(INV_BOX_MSG,D0398.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0398.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0373
				,MessageFormat.format(INV_BOX_MSG,I0373.getDescription(),C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0373.toString(),C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0374
				,MessageFormat.format(INV_BOX_MSG,D0374.getDescription(),C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0374.toString(),C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0403
				,MessageFormat.format(INV_BOX_MSG,I0403.getDescription(),C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0403.toString(),C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0404
				,MessageFormat.format(INV_BOX_MSG,D0404.getDescription(),C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0404.toString(),C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0383
				,MessageFormat.format(INV_BOX_MSG,I0383.getDescription(),C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0383.toString(),C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0384
				,MessageFormat.format(INV_BOX_MSG,D0384.getDescription(),C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0384.toString(),C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0368
				,MessageFormat.format(INV_BOX_MSG,D0368.getDescription(),C0036.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0368.toString(),C0036.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0409
				,MessageFormat.format(INV_BOX_MSG,I0409.getDescription(),C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0409.toString(),C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0410
				,MessageFormat.format(INV_BOX_MSG,D0410.getDescription(),C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0410.toString(),C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0411
				,MessageFormat.format(INV_BOX_MSG,I0411.getDescription(),C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,I0411.toString(),C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0412
				,MessageFormat.format(INV_BOX_MSG,D0412.getDescription(),C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,D0412.toString(),C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0400
			,MessageFormat.format(INV_BOX_MSG,D0400.getDescription(),
				  C0017.getDescription()+"\" ni \"" 
				+ C0018.getDescription()+"\" \"ni \"" 
				+ C0019.getDescription())
			,"round(D0400) == 0.0 || (round(D0400) > 0.0 && (C0017 || C0018 || C0019))"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0379
			,MessageFormat.format(INV_BOX_MSG,I0379.getDescription(),
				C0020.getDescription()+"\" ni \"" 
				+ C0035.getDescription()+"\" \"ni \"" 
				+ C0037.getDescription())
			,"round(I0379) == 0.0 || (round(I0379) > 0.0 && (C0020 || C0035 || C0037))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0380
			,MessageFormat.format(INV_BOX_MSG,D0380.getDescription(),
				C0020.getDescription()+"\" ni \"" 
				+ C0035.getDescription()+"\" \"ni \"" 
				+ C0037.getDescription())
			,"round(D0380) == 0.0 || (round(D0380) > 0.0 && (C0020 || C0035 || C0037))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0377
			,MessageFormat.format(INV_BOX_MSG,I0377.getDescription(),
				C0031.getDescription()+"\" \"ni \"" + C0032.getDescription())
			,"round(I0377) == 0.0 || (round(I0377) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0378
			,MessageFormat.format(INV_BOX_MSG,D0378.getDescription(),
				C0031.getDescription()+"\" \"ni \"" + C0032.getDescription())
			,"round(D0378) == 0.0 || (round(D0378) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0381
			,MessageFormat.format(INV_BOX_MSG,I0381.getDescription(),
				C0033.getDescription()+"\" \"ni \"" + C0034.getDescription())
			,"round(I0381) == 0.0 || (round(I0381) > 0.0 && (C0033 || C0034))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0382
			,MessageFormat.format(INV_BOX_MSG,D0382.getDescription(),
				C0033.getDescription()+"\" \"ni \"" + C0034.getDescription())
			,"round(D0382) == 0.0 || (round(D0382) > 0.0 && (C0033 || C0034))"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,LQ301
			,MessageFormat.format(INCOMPATIBLE_MSG,LQ301.getDescription(),PG326.getDescription())
			,"!(round(PG326) >= 0.0 && round(LQ301) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,LQ301
				,MessageFormat.format(MUST_EQUAL_MSG,LQ302.getDescription(),PG326.getDescription())
				,"!(round(PG326) >= 0.0 && round(LQ302) != round(PG326))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,LQ301
				,MessageFormat.format(INCOMPATIBLE_MSG,LQ302.getDescription(),PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ302) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,LQ301
				,MessageFormat.format(MUST_EQUAL_MSG,LQ301.getDescription(),PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ301) != round(PG326))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,LQ302
				,"No pueden tener contenido simult\u00E1neamente las dos casillas correspondientes a aumentos y disminuciones del Impuesto sobre Sociedades."
				,"!(round(LQ301) > 0 && round(LQ302) > 0)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0504
//				,"Confirme la procedencia del ajuste consignado en las claves 504 y/o 505"
//				,"(C0006 || C0056) && (round(I0504) != 0 || round(D0505) != 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0369
				,MessageFormat.format(INCOMPATIBLE_MSG,I0369.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(I0369) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0370
				,MessageFormat.format(INCOMPATIBLE_MSG,D0370.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(D0370) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,I0256
				,MessageFormat.format(INCOMPATIBLE_MSG,I0256.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(I0256) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0278
				,MessageFormat.format(INCOMPATIBLE_MSG,D0278.getDescription()
					,C0013.getDescription() +"\" y/o \"" + C0014.getDescription())
				,"round(D0278) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0400
				, "Sólo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes después de deducir las pérdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0050"
							+ "?((round(PG500) + (round(BP197)<0?round(BP197):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0400
				, "Sólo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes después de deducir las pérdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0051"
							+ "?((round(PG500) + (round(BP195)<0?round(BP195):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,D0404
				,"Revise importe disminuciones RIC"
				,"round(D0404) == 0.0 || round(D0404) >= round((LQ650 * 0.90))"));
	}
	
	static {	// PAGE 09
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ578
				,MessageFormat.format(INV_BOX_MSG,LQ578.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,LQ578.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ579
				,MessageFormat.format(INV_BOX_MSG,LQ579.getDescription(),C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,LQ579.toString(),C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ631
				,MessageFormat.format(EQUAL_GREATER_MSG,LQ631.getDescription(),LQ632.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ631.toString(),LQ632.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ552
				,LQ552.getDescription() + " debe ser cero con el caracter \"" + C0027.getDescription() + "\" marcado"
				,"C0027?LQ552==0:true"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_MSG,LQ641.getDescription(),LQ640.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ641.toString(),LQ640.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_MSG,LQ644.getDescription(),LQ643.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ644.toString(),LQ643.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_MSG,LQ647.getDescription(),LQ646.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ647.toString(),LQ646.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_MSG,LQ650.getDescription(),LQ649.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ650.toString(),LQ649.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_MSG,LQ653.getDescription(),LQ652.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ653.toString(),LQ652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_MSG,LQ656.getDescription(),LQ655.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ656.toString(),LQ655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_MSG,LQ659.getDescription(),LQ658.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ659.toString(),LQ658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_MSG,LQ662.getDescription(),LQ661.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ662.toString(),LQ661.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_MSG,LQ665.getDescription(),LQ664.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ665.toString(),LQ664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_MSG,LQ668.getDescription(),LQ667.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ668.toString(),LQ667.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_MSG,LQ747.getDescription(),LQ743.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ747.toString(),LQ743.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_MSG,LQ276.getDescription(),LQ275.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ276.toString(),LQ275.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_MSG,LQ609.getDescription(),LQ608.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ609.toString(),LQ608.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_MSG,LQ705.getDescription(),LQ704.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ705.toString(),LQ704.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_MSG,LQ014.getDescription(),LQ013.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ014.toString(),LQ013.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_MSG,LQ726.getDescription(),LQ725.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ726.toString(),LQ725.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_MSG,LQ535.getDescription(),LQ534.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ535.toString(),LQ534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ547
				,MessageFormat.format(EQUAL_LESS_MSG,LQ547.getDescription(),LQ670.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ547.toString(),LQ670.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ641.getDescription(),LQ640.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ641.toString(),LQ640.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ644.getDescription(),LQ643.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ644.toString(),LQ643.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ647.getDescription(),LQ646.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ647.toString(),LQ646.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ650.getDescription(),LQ649.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ650.toString(),LQ649.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ653.getDescription(),LQ652.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ653.toString(),LQ652.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ656.getDescription(),LQ655.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ656.toString(),LQ655.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ659.getDescription(),LQ658.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ659.toString(),LQ658.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ662.getDescription(),LQ661.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ662.toString(),LQ661.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ665.getDescription(),LQ664.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ665.toString(),LQ664.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ668.getDescription(),LQ667.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ668.toString(),LQ667.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ747.getDescription(),LQ743.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ747.toString(),LQ743.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ276.getDescription(),LQ275.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ276.toString(),LQ275.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ609.getDescription(),LQ608.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ609.toString(),LQ608.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ705.getDescription(),LQ704.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ705.toString(),LQ704.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ014.getDescription(),LQ013.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ014.toString(),LQ013.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ726.getDescription(),LQ725.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ726.toString(),LQ725.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ535.getDescription(),LQ534.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ535.toString(),LQ534.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ553
				,"La suma de las casillas \""+LQ553.getDescription()+"\" y \""+LQ554.getDescription()+"\" debe ser igual que \""+LQ552.getDescription()+"\""	
				,"(C0017 || C0018 || C0019)?LQ552 == (LQ553 + LQ554):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ559
				,"La casilla \""+LQ559.getDescription()+"\" debe ser mayor igual que cero y menor o igual que la casilla \""+LQ552.getDescription()+"\""
				,"(C0015?0.0 <= round(LQ559) && round(LQ559) <= round(LQ552):true"));
		
	}
	
	
/*	
	static {
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN567
				,MSG4
				,"BN567 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN568
				,MSG4
				,"BN568 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN563
				,MSG4
				,"BN563 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN566
				,MSG4
				,"BN566 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN576
				,MSG4
				,"BN576 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN569
				,MSG4
				,"BN569 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN570
				,MSG4
				,"BN570 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN571
				,MSG4
				,"BN571 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN572
				,MSG4
				,"BN572 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN573
				,MSG4
				,"BN573 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN575
				,MSG4
				,"BN575 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN577
				,MSG4
				,"BN577 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN581
				,MSG4
				,"BN581 < 0"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN582
				,"'"+BN582.getDescription() +  "' mayor que '" + LQ562.getDescription()+"'"
				,"BN582>LQ562"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN567
				, BN567.getDescription() + " mayor que el 50 por 100 de " + LQ562.getDescription()
				,"BN567 > (LQ562 / 2)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN563
				,BN563.getDescription() + " no procede. Caracter '" + C0029.getDescription() + "' no marcado." 
				,"BN563 > 0 && !C0029"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN566
				,BN566.getDescription() + " no procede. Caracter '" 
				+ C0017.getDescription() + "' o '"+C0018.getDescription()+"' no marcado." 
				,"BN566 > 0 && !(C0017 || C0018)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN576
				,BN576.getDescription() + " no procede. Caracter '" 
				+ C0038.getDescription() +"' no marcado." 
				,"BN576 > 0 && !C0038"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN575
				,BN575.getDescription() + " no procede. Caracter '" 
				+ C0007.getDescription() +"' no marcado." 
				,"BN575 > 0 && !C0007"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN581
				,BN581.getDescription() + " no procede. Caracter '" 
				+ C0015.getDescription() +"' no marcado." 
				,"BN581 > 0 && !C0015"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN581
				,MSG5
				,"BN581 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN583
				,MSG5
				,"BN583 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN585
				,MSG5
				,"BN585 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN584
				,MSG5
				,"BN584 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN588
				,MSG5
				,"BN588 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN590
				,MSG5
				,"BN590 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN082
				,MSG5
				,"BN082 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN565
				,"No puede aplicarse la clave [565] mientras existan saldos pendientes "
				+ "de aplicación de deducciones por doble imposición o de deducciones "
				+ "del capítulo IV Título VI de la Ley del Impuesto"
				,"(BN565 != 0 && (BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0))"
				+ "|| (BN565 != 0 && BN832 > 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN585
				,"Deducción art. 42 L.I.S. Y 36 TER LEY 43/95 (Clave 585)"
				,"BN585 < BN582"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN592
				,"Cuota líquida menor que cero"
				,"BN592 < 0"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN697
				,MSG6	
				,"BN697 > BN696")); 
			
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN847
				,MSG6	
				,"BN847 > BN846"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN283
				,MSG6	
				,"BN283 > BN282"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN703
				,MSG6	
				,"BN703 > BN702"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN187
				,MSG6	
				,"BN187 > BN071"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN026
				,MSG6	
				,"BN026 > BN025"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN715
				,MSG6	
				,"BN715 > BN714"));
				
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE10,BN737
				,MSG6	
				,"BN737 > BN736"));
	}
*/
	public static void main(String[] args) {
		for (ValidationMessage vm : VALIDATION_EXPRESSION_LIST) {
			System.out.println(vm.getExpression());
		}
	}
}
