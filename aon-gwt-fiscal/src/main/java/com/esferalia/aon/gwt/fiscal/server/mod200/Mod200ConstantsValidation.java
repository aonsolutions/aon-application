package com.esferalia.aon.gwt.fiscal.server.mod200;

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
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,Mod200Key.BA180
				,MessageFormat.format(MUST_EQUAL_MSG,Mod200Key.BA180.getDescription(),Mod200Key.BP252.getDescription())
				,MessageFormat.format(MUST_EQUAL_EXP,Mod200Key.BA180.toString(),Mod200Key.BP252.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE03,Mod200Key.BA180
				,EMPTY_BALANCE_MSG
				,"round(BA180) != 0.0 && round(BP252) != 0.0"));
	}

	
	static { // PAGE 04	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP189
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.BP189.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.BP189.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP194
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.BP194.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.BP194.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP197
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.BP197.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.BP197.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP200
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.BP200.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.BP200.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE04,Mod200Key.BP199
				,MessageFormat.format(MUST_EQUAL_MSG,Mod200Key.BP199.getDescription(),Mod200Key.LQ500.getDescription())
				,"(C0003 || C0004 || C0024 || C0025 || C0036 || C0061)? true : round(BP199) == round(LQ500)"));
	}

	
	static { // PAGE 07	
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC534
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC534.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC535
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC535.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC535.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC536
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC536.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC536.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC537
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC537.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC537.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC538
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC538.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC538.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC539
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC539.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC539.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC540
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC540.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC540.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC541
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC541.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC541.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC542
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC542.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC542.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC543
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC543.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC543.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC544
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC544.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC544.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC545
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC545.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC545.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC546
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC546.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC546.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC562
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC562.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC562.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC563
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC563.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC563.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC564
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC564.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC564.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC565
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC565.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC565.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC566
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC566.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC566.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC567
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC567.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC567.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC568
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC568.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC568.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC569
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC569.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC569.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC570
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC570.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC570.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC571
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC571.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC571.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC572
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC572.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC572.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE07,Mod200Key.TC574
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.TC574.getDescription())
				,MessageFormat.format(MUST_NEGATIVE_EXP,Mod200Key.TC574.toString())));
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
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0417
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.I0417.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,Mod200Key.I0417.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0418
				,MessageFormat.format(CHECK_SIGN_MSG,Mod200Key.D0418.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,Mod200Key.D0418.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0391
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0391.getDescription(),Mod200Key.C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0391.toString(),Mod200Key.C0001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0392
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0392.getDescription(),Mod200Key.C0001.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0392.toString(),Mod200Key.C0001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0389
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0392.getDescription(),Mod200Key.C0002.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0392.toString(),Mod200Key.C0002.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0390
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0390.getDescription(),Mod200Key.C0002.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0390.toString(),Mod200Key.C0002.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0371
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0371.getDescription(),Mod200Key.C0003.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0371.toString(),Mod200Key.C0003.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0311
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0311.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0311.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0313
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0313.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0313.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0323
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0323.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0323.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0312
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0312.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0312.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0314
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0314.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0314.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0324
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0324.getDescription(),Mod200Key.C0006.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0324.toString(),Mod200Key.C0006.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0387
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0387.getDescription(),Mod200Key.C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0387.toString(),Mod200Key.C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0388
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0388.getDescription(),Mod200Key.C0007.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0388.toString(),Mod200Key.C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0396
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0396.getDescription(),Mod200Key.C0005.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0396.toString(),Mod200Key.C0005.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0385
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0385.getDescription(),Mod200Key.C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0385.toString(),Mod200Key.C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0386
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0386.getDescription(),Mod200Key.C0011.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0386.toString(),Mod200Key.C0011.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0397
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0397.getDescription(),Mod200Key.C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0397.toString(),Mod200Key.C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0398
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0398.getDescription(),Mod200Key.C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0398.toString(),Mod200Key.C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0373
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0373.getDescription(),Mod200Key.C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0373.toString(),Mod200Key.C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0374
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0374.getDescription(),Mod200Key.C0024.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0374.toString(),Mod200Key.C0024.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0403
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0403.getDescription(),Mod200Key.C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0403.toString(),Mod200Key.C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0404
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0404.getDescription(),Mod200Key.C0029.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0404.toString(),Mod200Key.C0029.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0383
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0383.getDescription(),Mod200Key.C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0383.toString(),Mod200Key.C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0384
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0384.getDescription(),Mod200Key.C0034.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0384.toString(),Mod200Key.C0034.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0368
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0368.getDescription(),Mod200Key.C0036.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0368.toString(),Mod200Key.C0036.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0409
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0409.getDescription(),Mod200Key.C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0409.toString(),Mod200Key.C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0410
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0410.getDescription(),Mod200Key.C0046.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0410.toString(),Mod200Key.C0046.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0411
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0411.getDescription(),Mod200Key.C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.I0411.toString(),Mod200Key.C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0412
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0412.getDescription(),Mod200Key.C0047.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.D0412.toString(),Mod200Key.C0047.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0400
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0400.getDescription(),
				  Mod200Key.C0017.getDescription()+"\" ni \"" 
				+ Mod200Key.C0018.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0019.getDescription())
			,"round(D0400) == 0.0 || (round(D0400) > 0.0 && (C0017 || C0018 || C0019))"));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0379
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0379.getDescription(),
				Mod200Key.C0020.getDescription()+"\" ni \"" 
				+ Mod200Key.C0035.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0037.getDescription())
			,"round(I0379) == 0.0 || (round(I0379) > 0.0 && (C0020 || C0035 || C0037))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0380
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0380.getDescription(),
				Mod200Key.C0020.getDescription()+"\" ni \"" 
				+ Mod200Key.C0035.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0037.getDescription())
			,"round(D0380) == 0.0 || (round(D0380) > 0.0 && (C0020 || C0035 || C0037))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0377
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0377.getDescription(),
				Mod200Key.C0031.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0032.getDescription())
			,"round(I0377) == 0.0 || (round(I0377) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0378
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0378.getDescription(),
				Mod200Key.C0031.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0032.getDescription())
			,"round(D0378) == 0.0 || (round(D0378) > 0.0 && (C0031 || C0032))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0381
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.I0381.getDescription(),
				Mod200Key.C0033.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0034.getDescription())
			,"round(I0381) == 0.0 || (round(I0381) > 0.0 && (C0033 || C0034))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0382
			,MessageFormat.format(INV_BOX_MSG,Mod200Key.D0382.getDescription(),
				Mod200Key.C0033.getDescription()+"\" \"ni \"" 
				+ Mod200Key.C0034.getDescription())
			,"round(D0382) == 0.0 || (round(D0382) > 0.0 && (C0033 || C0034))"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
			,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.LQ301.getDescription(),Mod200Key.PG326.getDescription())
			,"!(round(PG326) >= 0.0 && round(LQ301) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
				,MessageFormat.format(MUST_EQUAL_MSG,Mod200Key.LQ302.getDescription(),Mod200Key.PG326.getDescription())
				,"!(round(PG326) >= 0.0 && round(LQ302) != round(PG326))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
				,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.LQ302.getDescription(),Mod200Key.PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ302) != 0.0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ301
				,MessageFormat.format(MUST_EQUAL_MSG,Mod200Key.LQ301.getDescription(),Mod200Key.PG326.getDescription())
				,"!(round(PG326) < 0.0 && round(LQ301) != round(PG326))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.LQ302
				,"No pueden tener contenido simult\u00E1neamente las dos casillas correspondientes a aumentos y disminuciones del Impuesto sobre Sociedades."
				,"!(round(LQ301) > 0 && round(LQ302) > 0)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0504
//				,"Confirme la procedencia del ajuste consignado en las claves 504 y/o 505"
//				,"(C0006 || C0056) && (round(I0504) != 0 || round(D0505) != 0)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0369
				,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.I0369.getDescription()
					,Mod200Key.C0013.getDescription() +"\" y/o \"" + Mod200Key.C0014.getDescription())
				,"round(I0369) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0370
				,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.D0370.getDescription()
					,Mod200Key.C0013.getDescription() +"\" y/o \"" + Mod200Key.C0014.getDescription())
				,"round(D0370) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.I0256
				,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.I0256.getDescription()
					,Mod200Key.C0013.getDescription() +"\" y/o \"" + Mod200Key.C0014.getDescription())
				,"round(I0256) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0278
				,MessageFormat.format(INCOMPATIBLE_MSG,Mod200Key.D0278.getDescription()
					,Mod200Key.C0013.getDescription() +"\" y/o \"" + Mod200Key.C0014.getDescription())
				,"round(D0278) != 0.0?(C0013 || C0014):true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0400
				, "Sólo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes después de deducir las pérdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0050"
							+ "?((round(PG500) + (round(BP197)<0?round(BP197):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0400
				, "Sólo se puede dotar Fondo de Reserva Obligatorio si los resultados del ejercicio "
				+ "han sido excedentes después de deducir las pérdidas de ejercicios anteriores"
				,"round(D0400) != 0.0"
						+ "?(C0051"
							+ "?((round(PG500) + (round(BP195)<0?round(BP195):0.0) - round(LQ326)) < 0)"
							+ ":true)"
						+ ":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE08,Mod200Key.D0404
				,"Revise importe disminuciones RIC"
				,"round(D0404) == 0.0 || round(D0404) >= round((LQ650 * 0.90))"));
	}
	
	static {	// PAGE 09
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ578
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.LQ578.getDescription(),Mod200Key.C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.LQ578.toString(),Mod200Key.C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ579
				,MessageFormat.format(INV_BOX_MSG,Mod200Key.LQ579.getDescription(),Mod200Key.C0022.getDescription())
				,MessageFormat.format(INV_BOX_EXP,Mod200Key.LQ579.toString(),Mod200Key.C0022.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ631
				,MessageFormat.format(EQUAL_GREATER_MSG,Mod200Key.LQ631.getDescription(),Mod200Key.LQ632.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,Mod200Key.LQ631.toString(),Mod200Key.LQ632.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ552
				,Mod200Key.LQ552.getDescription() + " debe ser cero con el caracter \"" + Mod200Key.C0027.getDescription() + "\" marcado"
				,"C0027?LQ552==0:true"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ641
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ641.getDescription(),Mod200Key.LQ640.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ641.toString(),Mod200Key.LQ640.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ644
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ644.getDescription(),Mod200Key.LQ643.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ644.toString(),Mod200Key.LQ643.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ647
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ647.getDescription(),Mod200Key.LQ646.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ647.toString(),Mod200Key.LQ646.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ650
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ650.getDescription(),Mod200Key.LQ649.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ650.toString(),Mod200Key.LQ649.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ653
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ653.getDescription(),Mod200Key.LQ652.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ653.toString(),Mod200Key.LQ652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ656
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ656.getDescription(),Mod200Key.LQ655.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ656.toString(),Mod200Key.LQ655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ659
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ659.getDescription(),Mod200Key.LQ658.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ659.toString(),Mod200Key.LQ658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ662
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ662.getDescription(),Mod200Key.LQ661.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ662.toString(),Mod200Key.LQ661.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ665
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ665.getDescription(),Mod200Key.LQ664.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ665.toString(),Mod200Key.LQ664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ668
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ668.getDescription(),Mod200Key.LQ667.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ668.toString(),Mod200Key.LQ667.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ747
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ747.getDescription(),Mod200Key.LQ743.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ747.toString(),Mod200Key.LQ743.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ276
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ276.getDescription(),Mod200Key.LQ275.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ276.toString(),Mod200Key.LQ275.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ609
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ609.getDescription(),Mod200Key.LQ608.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ609.toString(),Mod200Key.LQ608.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ705
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ705.getDescription(),Mod200Key.LQ704.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ705.toString(),Mod200Key.LQ704.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ014
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ014.getDescription(),Mod200Key.LQ013.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ014.toString(),Mod200Key.LQ013.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ726
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ726.getDescription(),Mod200Key.LQ725.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ726.toString(),Mod200Key.LQ725.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ535
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ535.getDescription(),Mod200Key.LQ534.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ535.toString(),Mod200Key.LQ534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ547
				,MessageFormat.format(EQUAL_LESS_MSG,Mod200Key.LQ547.getDescription(),Mod200Key.LQ670.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,Mod200Key.LQ547.toString(),Mod200Key.LQ670.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ641
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ641.getDescription(),Mod200Key.LQ640.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ641.toString(),Mod200Key.LQ640.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ644
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ644.getDescription(),Mod200Key.LQ643.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ644.toString(),Mod200Key.LQ643.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ647
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ647.getDescription(),Mod200Key.LQ646.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ647.toString(),Mod200Key.LQ646.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ650
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ650.getDescription(),Mod200Key.LQ649.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ650.toString(),Mod200Key.LQ649.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ653
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ653.getDescription(),Mod200Key.LQ652.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ653.toString(),Mod200Key.LQ652.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ656
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ656.getDescription(),Mod200Key.LQ655.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ656.toString(),Mod200Key.LQ655.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ659
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ659.getDescription(),Mod200Key.LQ658.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ659.toString(),Mod200Key.LQ658.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ662
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ662.getDescription(),Mod200Key.LQ661.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ662.toString(),Mod200Key.LQ661.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ665
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ665.getDescription(),Mod200Key.LQ664.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ665.toString(),Mod200Key.LQ664.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ668
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ668.getDescription(),Mod200Key.LQ667.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ668.toString(),Mod200Key.LQ667.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ747
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ747.getDescription(),Mod200Key.LQ743.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ747.toString(),Mod200Key.LQ743.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ276
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ276.getDescription(),Mod200Key.LQ275.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ276.toString(),Mod200Key.LQ275.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ609
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ609.getDescription(),Mod200Key.LQ608.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ609.toString(),Mod200Key.LQ608.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ705
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ705.getDescription(),Mod200Key.LQ704.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ705.toString(),Mod200Key.LQ704.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ014
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ014.getDescription(),Mod200Key.LQ013.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ014.toString(),Mod200Key.LQ013.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ726
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ726.getDescription(),Mod200Key.LQ725.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ726.toString(),Mod200Key.LQ725.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ535
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,Mod200Key.LQ535.getDescription(),Mod200Key.LQ534.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,Mod200Key.LQ535.toString(),Mod200Key.LQ534.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,Mod200Key.LQ553
				,"La suma de las casillas \""+Mod200Key.LQ553.getDescription()+"\" y \""+Mod200Key.LQ554.getDescription()+"\" debe ser igual que \""+Mod200Key.LQ552.getDescription()+"\""	
				,"(C0017 || C0018 || C0019)?LQ552 == (LQ553 + LQ554):true"));				
	}
	
	
/*	
	static {
				
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
	public static void main(String[] args) {
		for (ValidationMessage vm : VALIDATION_EXPRESSION_LIST) {
			System.out.println(vm.getExpression());
		}
	}
}
