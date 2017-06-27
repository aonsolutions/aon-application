package com.esferalia.aon.occam.impl.jooq.dao.mod200_2016;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.*;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.ValidationMessage2016;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002016Validation {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	private static final int PAGE00 = 0;
	private static final int PAGE01 = 1;
	private static final int PAGE02 = 2;
	private static final int PAGE03 = 3;
	private static final int PAGE04 = 4;
	private static final int PAGE07 = 7;
	private static final int PAGE08 = 8;
	private static final int PAGE09 = 9;
	private static final int PAGE10 = 10;
	private static final int PAGE14 = 14;
	
	private static final String MUST_EQUAL_MSG = "[{0}] {1} y [{2}] {3} deben ser iguales.";
	private static final String MUST_GREATHER_MSG = "[{0}] {1} debe ser mayor que [{2}] {3} ";
	private static final String MUST_LESS_MSG = "[{0}] {1} debe ser menor que [{2}] {3} ";
	private static final String CHECK_SIGN_MSG = "Verifique el signo de la clave: [{0}] {1}";
	private static final String MUST_POSITIVE_MSG = "El valor de la clave: [{0}] {1} debe ser un n\u00FAmero positivo" ;
	private static final String COOP_MSG =  "Las sociedades cooperativas tienen unas casillas espec\u00EDficas que se aplican a nivel de cuota, la [00210], [00480], [00408] y [01037]";
	private static final String GRP_MSG =  "las sociedades integrantes de grupos fiscales no pueden marcar esta clave";
	private static final String INV_BOX_MSG = "Casilla [{0}] {1} no v\u00E1lida sin el caracter [{2}] {3}.";
	
	@FunctionalInterface
	private static interface IValidator {
		boolean validate(Mod2002016 mod);
	}
	private static enum Type {
		 DOC   ( mod -> !AonDocumentUtil.isValid(mod.getEnterpriseDocument()),new ValidationMessage2016(PAGE00,"NIF de la declaraci\u00F3n incorrecto."))
		,CNAE_1( mod -> AonStringUtils.isEmpty(mod.getCnae()),new ValidationMessage2016(PAGE00,"Rellene el CNAE de la empresa."))
		,CNAE_2( mod -> !AonStringUtils.isEmpty(mod.getCnae()) && CNAE2009.valueOfCode(mod.getCnae()) == null,new ValidationMessage2016(PAGE00,"CNAE de la empresa, no v\u00E1lido."))
		,COMP_1( mod -> mod.isComplementary() && AonStringUtils.isBlank(mod.getComplementaryReceipt() )
			,new ValidationMessage2016(PAGE00,"Si marca Decl. Complementaria, debe indicar un n. de justificante anterior."))
		,COMP_2( mod -> mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt()) && mod.getComplementaryReceipt().length() != 13 
			,new ValidationMessage2016(PAGE00,"El n. de justificante anterior debe tener 13 caracteres."))
		,COMP_3( mod -> mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt()) 
				  && (!mod.getComplementaryReceipt().startsWith("200") && !mod.getComplementaryReceipt().startsWith("206"))
			,new ValidationMessage2016(PAGE00,"El n. de justificante anterior debe empezar por 200 o 206."))
		,COMP_4( mod -> !mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt())
			, new ValidationMessage2016(PAGE00,"Si no marca Decl. Complementaria, no debe indicar un n. de justificante anterior."))
		,SECR_1( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) && mod.getSecretary() == null
			, new ValidationMessage2016(PAGE01,"Para personas jur\u00EDdicas, debe rellenar los datos del secretario"))
		,SECR_2( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& !AonDocumentUtil.isValid(mod.getSecretary().getDocument())
			, new ValidationMessage2016(PAGE01,"NIF del secretario incorrecto."))
		,SECR_3( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& AonStringUtils.isBlank(mod.getSecretary().getName())
			, new ValidationMessage2016(PAGE01,"Falta nombre del secretario."))
		,SECR_4( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null && AonStringUtils.isNotBlank(mod.getSecretary().getName()) 
					&& mod.getSecretary().getName().length() > 25
			, new ValidationMessage2016(PAGE01,"Longitud excedida en el nombre del secretario. Debe limitarse a 25 caracteres."))
		,SECR_5( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& mod.getSecretary().getIrnr() == null 
					&& (mod.isChecked(C0021) || mod.isChecked(C0046))  
			, new ValidationMessage2016(PAGE01,"Falta fecha IRNR."))
		
		// ------------------------------------------------------------------------
		// -------------------------- BALANCE: ACTIVO ----------------------------- 		
		// ------------------------------------------------------------------------
		,V_BA180_1( mod -> isNotEqual(mod,BA180,BP252),new ValidationMessage2016(PAGE03,BA180,mustEqualMsg( BA180, BP252 )))
		,V_BA180_2( mod -> isZero(mod,BA180) && isZero(mod,BP252) && isZero(mod,BP187)
			 ,new ValidationMessage2016(PAGE03,BA180,"Advertencia: Los totales de los balances son cero (Activo, patrimonio neto y pasivo)." ))
		
		// ------------------------------------------------------------------------
		// --------------- BALANCE: PATRIMONIO NETO Y PASIVO ----------------------
		// ------------------------------------------------------------------------
		,V_BP189_1( mod -> isPositive(mod,BP189)		,new ValidationMessage2016(PAGE04,BP189,checkSignMsg(BP189)))
		,V_BP194_1( mod -> isPositive(mod,BP194)		,new ValidationMessage2016(PAGE04,BP194,checkSignMsg(BP194)))
		,V_BP197  ( mod -> isPositive(mod,BP197)		,new ValidationMessage2016(PAGE04,BP197,checkSignMsg(BP197)))
		,V_BP199_1( mod -> isNotEqual(mod,BP199,LQ500)	,new ValidationMessage2016(PAGE04,BP199,mustEqualMsg(BP199, LQ500)))  
		,V_BP200_1( mod -> isPositive(mod,BP200)		,new ValidationMessage2016(PAGE04,BP200,checkSignMsg(BP200)))
		
		// ------------------------------------------------------------------------
		// --------- ECPN. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO ----------
		// ------------------------------------------------------------------------
		,V_TC632  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP188,TC632)
			, new ValidationMessage2016(PAGE07, TC632,mustEqualMsg(TC632,BP188))) 
		,V_TC633  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP189,TC633)
			, new ValidationMessage2016(PAGE07, TC633,mustEqualMsg(TC633,BP189)))
		,V_TC634  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP190,TC634)
			, new ValidationMessage2016(PAGE07, TC634,mustEqualMsg(TC634,BP190)))
		,V_TC635  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP191,TC635)
			, new ValidationMessage2016(PAGE07, TC635,mustEqualMsg(TC635,BP191)))
		,V_TC636  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP194,TC636)
			, new ValidationMessage2016(PAGE07, TC637,mustEqualMsg(TC636,BP194)))
		,V_TC637  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP195,TC637)
			, new ValidationMessage2016(PAGE07, TC637,mustEqualMsg(TC637,BP195)))
		,V_TC639  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP199,TC639)
			, new ValidationMessage2016(PAGE07, TC639,mustEqualMsg(TC639,BP199)))
		,V_TC640  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP200,TC640)
			, new ValidationMessage2016(PAGE07, TC640,mustEqualMsg(TC640,BP200)))
		,V_TC641  ( mod -> isECPNFilled(mod) && isNotBalancePymes(mod) && isNotEqual(mod,BP201,TC641)
			, new ValidationMessage2016(PAGE07, TC642,mustEqualMsg(TC641,BP201))) 
		,V_TC642  ( mod -> isECPNFilled(mod) && isNotBalancePymes(mod) && isNotEqual(mod,BP202,TC642)
			, new ValidationMessage2016(PAGE07, TC642,mustEqualMsg(TC642,BP202))) 
		,V_TC638  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP198,TC638)
			, new ValidationMessage2016(PAGE07, TC638,mustEqualMsg(TC638,BP198)))
		,V_TC643  ( mod -> isECPNFilled(mod) && isBalancePymes(mod) && isNotEqual(mod,BP208,LQ643)
			, new ValidationMessage2016(PAGE07, TC643,mustEqualMsg(TC643,BP208))) 
		,V_TC644  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP209,TC644)
			, new ValidationMessage2016(PAGE07, TC644,mustEqualMsg(TC644,BP209)))
		,V_TC645  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP185,TC645)
			, new ValidationMessage2016(PAGE07, TC645,mustEqualMsg(TC645,BP185)))
		
		,V_TC534  ( mod -> isPositive(mod,TC534),new ValidationMessage2016(PAGE07,TC534, checkSignMsg(TC534)))
		,V_TC535  ( mod -> isPositive(mod,TC535),new ValidationMessage2016(PAGE07,TC535, checkSignMsg(TC535)))
		,V_TC536  ( mod -> isPositive(mod,TC536),new ValidationMessage2016(PAGE07,TC536, checkSignMsg(TC536)))
		,V_TC537  ( mod -> isPositive(mod,TC537),new ValidationMessage2016(PAGE07,TC537, checkSignMsg(TC537)))
		,V_TC538  ( mod -> isPositive(mod,TC538),new ValidationMessage2016(PAGE07,TC538, checkSignMsg(TC538)))
		,V_TC539  ( mod -> isPositive(mod,TC539),new ValidationMessage2016(PAGE07,TC539, checkSignMsg(TC539)))
		,V_TC540  ( mod -> isPositive(mod,TC540),new ValidationMessage2016(PAGE07,TC540, checkSignMsg(TC540)))
		,V_TC541  ( mod -> isPositive(mod,TC541),new ValidationMessage2016(PAGE07,TC541, checkSignMsg(TC541)))
		,V_TC542  ( mod -> isPositive(mod,TC542),new ValidationMessage2016(PAGE07,TC542, checkSignMsg(TC542)))
		,V_TC543  ( mod -> isPositive(mod,TC543),new ValidationMessage2016(PAGE07,TC543, checkSignMsg(TC543)))
		,V_TC544  ( mod -> isPositive(mod,TC544),new ValidationMessage2016(PAGE07,TC544, checkSignMsg(TC544)))
		,V_TC545  ( mod -> isPositive(mod,TC545),new ValidationMessage2016(PAGE07,TC545, checkSignMsg(TC545)))
		,V_TC546  ( mod -> isPositive(mod,TC546),new ValidationMessage2016(PAGE07,TC546, checkSignMsg(TC546)))
		,V_TC562  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC562),new ValidationMessage2016(PAGE07,TC562, checkSignMsg(TC562))) 
		,V_TC563  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC563),new ValidationMessage2016(PAGE07,TC563, checkSignMsg(TC563)))
		,V_TC564  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC564),new ValidationMessage2016(PAGE07,TC564, checkSignMsg(TC564)))
		,V_TC565  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC565),new ValidationMessage2016(PAGE07,TC565, checkSignMsg(TC565)))
		,V_TC566  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC566),new ValidationMessage2016(PAGE07,TC566, checkSignMsg(TC566)))
		,V_TC567  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC567),new ValidationMessage2016(PAGE07,TC567, checkSignMsg(TC567)))
		,V_TC568  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC568),new ValidationMessage2016(PAGE07,TC568, checkSignMsg(TC568)))
		,V_TC569  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC569),new ValidationMessage2016(PAGE07,TC569, checkSignMsg(TC569)))
		,V_TC570  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC570),new ValidationMessage2016(PAGE07,TC570, checkSignMsg(TC570)))
		,V_TC571  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC571),new ValidationMessage2016(PAGE07,TC571, checkSignMsg(TC571)))
		,V_TC572  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC572),new ValidationMessage2016(PAGE07,TC572, checkSignMsg(TC572)))
		,V_TC574  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC574),new ValidationMessage2016(PAGE07,TC574, checkSignMsg(TC574)))
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (I) ----------------------------
		// ------------------------------------------------------------------------
		,V_LQ1230 ( mod -> isNegative(mod,LQ1230),new ValidationMessage2016(PAGE08,LQ1230, mustPositiveMsg(LQ1230)))
		,V_LQ1231 ( mod -> isNegative(mod,LQ1231),new ValidationMessage2016(PAGE08,LQ1231, mustPositiveMsg(LQ1231)))
		
		,V_I0355  ( mod -> isNegative(mod,I0355),new ValidationMessage2016(PAGE08,I0355, mustPositiveMsg(I0355)))
		,V_I0357  ( mod -> isNegative(mod,I0357),new ValidationMessage2016(PAGE08,I0357, mustPositiveMsg(I0357)))
		,V_I0359  ( mod -> isNegative(mod,I0359),new ValidationMessage2016(PAGE08,I0359, mustPositiveMsg(I0359)))
		,V_I0225  ( mod -> isNegative(mod,I0225),new ValidationMessage2016(PAGE08,I0225, mustPositiveMsg(I0225)))
		,V_I1514  ( mod -> isNegative(mod,I1514),new ValidationMessage2016(PAGE08,I1514, mustPositiveMsg(I1514)))
		,V_I0361  ( mod -> isNegative(mod,I0361),new ValidationMessage2016(PAGE08,I0361, mustPositiveMsg(I0361)))
		,V_I0303  ( mod -> isNegative(mod,I0303),new ValidationMessage2016(PAGE08,I0303, mustPositiveMsg(I0303)))
		,V_I1005  ( mod -> isNegative(mod,I1005),new ValidationMessage2016(PAGE08,I1005, mustPositiveMsg(I1005)))
		,V_I0305  ( mod -> isNegative(mod,I0305),new ValidationMessage2016(PAGE08,I0305, mustPositiveMsg(I0305)))
		,V_I0307  ( mod -> isNegative(mod,I0307),new ValidationMessage2016(PAGE08,I0307, mustPositiveMsg(I0307)))
		,V_I1003  ( mod -> isNegative(mod,I1003),new ValidationMessage2016(PAGE08,I1003, mustPositiveMsg(I1003)))
		,V_I0309  ( mod -> isNegative(mod,I0309),new ValidationMessage2016(PAGE08,I0309, mustPositiveMsg(I0309)))
		,V_I0514  ( mod -> isNegative(mod,I0514),new ValidationMessage2016(PAGE08,I0514, mustPositiveMsg(I0514)))
		,V_I0516  ( mod -> isNegative(mod,I0516),new ValidationMessage2016(PAGE08,I0516, mustPositiveMsg(I0516)))
		,V_I0321  ( mod -> isNegative(mod,I0321),new ValidationMessage2016(PAGE08,I0321, mustPositiveMsg(I0321)))
		,V_I0415  ( mod -> isNegative(mod,I0415),new ValidationMessage2016(PAGE08,I0415, mustPositiveMsg(I0415)))
		,V_I0331  ( mod -> isNegative(mod,I0331),new ValidationMessage2016(PAGE08,I0331, mustPositiveMsg(I0331)))
		,V_I0325  ( mod -> isNegative(mod,I0325),new ValidationMessage2016(PAGE08,I0325, mustPositiveMsg(I0325)))
		,V_I1518  ( mod -> isNegative(mod,I1518),new ValidationMessage2016(PAGE08,I1518, mustPositiveMsg(I1518)))
		,V_I0333  ( mod -> isNegative(mod,I0333),new ValidationMessage2016(PAGE08,I0333, mustPositiveMsg(I0333)))
		,V_I0327  ( mod -> isNegative(mod,I0327),new ValidationMessage2016(PAGE08,I0327, mustPositiveMsg(I0327)))
		,V_I0416  ( mod -> isNegative(mod,I0416),new ValidationMessage2016(PAGE08,I0416, mustPositiveMsg(I0416)))
		,V_I0335  ( mod -> isNegative(mod,I0335),new ValidationMessage2016(PAGE08,I0335, mustPositiveMsg(I0335)))
		,V_I0337  ( mod -> isNegative(mod,I0337),new ValidationMessage2016(PAGE08,I0337, mustPositiveMsg(I0337)))
		,V_I0339  ( mod -> isNegative(mod,I0339),new ValidationMessage2016(PAGE08,I0339, mustPositiveMsg(I0339)))
		,V_I0341  ( mod -> isNegative(mod,I0341),new ValidationMessage2016(PAGE08,I0341, mustPositiveMsg(I0341)))
		,V_I0508  ( mod -> isNegative(mod,I0508),new ValidationMessage2016(PAGE08,I0508, mustPositiveMsg(I0508)))
		,V_I1009  ( mod -> isNegative(mod,I1009),new ValidationMessage2016(PAGE08,I1009, mustPositiveMsg(I1009)))
		,V_I0343  ( mod -> isNegative(mod,I0343),new ValidationMessage2016(PAGE08,I0343, mustPositiveMsg(I0343)))
		,V_I0363  ( mod -> isNegative(mod,I0363),new ValidationMessage2016(PAGE08,I0363, mustPositiveMsg(I0363)))
		,V_I0345  ( mod -> isNegative(mod,I0345),new ValidationMessage2016(PAGE08,I0345, mustPositiveMsg(I0345)))
		,V_I0371  ( mod -> isNegative(mod,I0371),new ValidationMessage2016(PAGE08,I0371, mustPositiveMsg(I0371)))
		,V_I0347  ( mod -> isNegative(mod,I0347),new ValidationMessage2016(PAGE08,I0347, mustPositiveMsg(I0347)))
		,V_I1011  ( mod -> isNegative(mod,I1011),new ValidationMessage2016(PAGE08,I1011, mustPositiveMsg(I1011)))
		,V_I1013  ( mod -> isNegative(mod,I1013),new ValidationMessage2016(PAGE08,I1013, mustPositiveMsg(I1013)))
		,V_I1015  ( mod -> isNegative(mod,I1015),new ValidationMessage2016(PAGE08,I1015, mustPositiveMsg(I1015)))
		,V_I0369  ( mod -> isNegative(mod,I0369),new ValidationMessage2016(PAGE08,I0369, mustPositiveMsg(I0369)))
		,V_I0256  ( mod -> isNegative(mod,I0256),new ValidationMessage2016(PAGE08,I0256, mustPositiveMsg(I0256)))
		,V_I0373  ( mod -> isNegative(mod,I0373),new ValidationMessage2016(PAGE08,I0373, mustPositiveMsg(I0373)))
		,V_I0340  ( mod -> isNegative(mod,I0340),new ValidationMessage2016(PAGE08,I0340, mustPositiveMsg(I0340)))
		,V_I0351  ( mod -> isNegative(mod,I0351),new ValidationMessage2016(PAGE08,I0351, mustPositiveMsg(I0351)))
		,V_I0375  ( mod -> isNegative(mod,I0375),new ValidationMessage2016(PAGE08,I0375, mustPositiveMsg(I0375)))
		,V_I1320  ( mod -> isNegative(mod,I1320),new ValidationMessage2016(PAGE08,I1320, mustPositiveMsg(I1320)))
		,V_I0184  ( mod -> isNegative(mod,I0184),new ValidationMessage2016(PAGE08,I0184, mustPositiveMsg(I0184)))
		,V_I1022  ( mod -> isNegative(mod,I1022),new ValidationMessage2016(PAGE08,I1022, mustPositiveMsg(I1022)))
		,V_I1018  ( mod -> isNegative(mod,I1018),new ValidationMessage2016(PAGE08,I1018, mustPositiveMsg(I1018)))
		,V_I1275  ( mod -> isNegative(mod,I1275),new ValidationMessage2016(PAGE08,I1275, mustPositiveMsg(I1275)))
		,V_I0377  ( mod -> isNegative(mod,I0377),new ValidationMessage2016(PAGE08,I0377, mustPositiveMsg(I0377)))
		,V_I0379  ( mod -> isNegative(mod,I0379),new ValidationMessage2016(PAGE08,I0379, mustPositiveMsg(I0379)))
		,V_I0381  ( mod -> isNegative(mod,I0381),new ValidationMessage2016(PAGE08,I0381, mustPositiveMsg(I0381)))
		,V_I0383  ( mod -> isNegative(mod,I0383),new ValidationMessage2016(PAGE08,I0383, mustPositiveMsg(I0383)))
		,V_I0387  ( mod -> isNegative(mod,I0387),new ValidationMessage2016(PAGE08,I0387, mustPositiveMsg(I0387)))
		,V_I0311  ( mod -> isNegative(mod,I0311),new ValidationMessage2016(PAGE08,I0311, mustPositiveMsg(I0311)))
		,V_I0313  ( mod -> isNegative(mod,I0313),new ValidationMessage2016(PAGE08,I0313, mustPositiveMsg(I0313)))
		,V_I0323  ( mod -> isNegative(mod,I0323),new ValidationMessage2016(PAGE08,I0323, mustPositiveMsg(I0323)))
		,V_I0317  ( mod -> isNegative(mod,I0317),new ValidationMessage2016(PAGE08,I0317, mustPositiveMsg(I0317)))
		,V_I0385  ( mod -> isNegative(mod,I0385),new ValidationMessage2016(PAGE08,I0385, mustPositiveMsg(I0385)))
		,V_I0389  ( mod -> isNegative(mod,I0389),new ValidationMessage2016(PAGE08,I0389, mustPositiveMsg(I0389)))
		,V_I0397  ( mod -> isNegative(mod,I0397),new ValidationMessage2016(PAGE08,I0397, mustPositiveMsg(I0397)))
		,V_I0250  ( mod -> isNegative(mod,I0250),new ValidationMessage2016(PAGE08,I0250, mustPositiveMsg(I0250)))
		,V_I0391  ( mod -> isNegative(mod,I0391),new ValidationMessage2016(PAGE08,I0391, mustPositiveMsg(I0391)))
		,V_I0403  ( mod -> isNegative(mod,I0403),new ValidationMessage2016(PAGE08,I0403, mustPositiveMsg(I0403)))
		,V_I0518  ( mod -> isNegative(mod,I0518),new ValidationMessage2016(PAGE08,I0518, mustPositiveMsg(I0518)))
		,V_I0510  ( mod -> isNegative(mod,I0510),new ValidationMessage2016(PAGE08,I0510, mustPositiveMsg(I0510)))
		,V_I0329  ( mod -> isNegative(mod,I0329),new ValidationMessage2016(PAGE08,I0329, mustPositiveMsg(I0329)))
		,V_I0365  ( mod -> isNegative(mod,I0365),new ValidationMessage2016(PAGE08,I0365, mustPositiveMsg(I0365)))
		,V_I0409  ( mod -> isNegative(mod,I0409),new ValidationMessage2016(PAGE08,I0409, mustPositiveMsg(I0409)))
		,V_I0411  ( mod -> isNegative(mod,I0411),new ValidationMessage2016(PAGE08,I0411, mustPositiveMsg(I0411)))
		,V_I1027  ( mod -> isNegative(mod,I1027),new ValidationMessage2016(PAGE08,I1027, mustPositiveMsg(I1027)))
		,V_I0413  ( mod -> isNegative(mod,I0413),new ValidationMessage2016(PAGE08,I0413, mustPositiveMsg(I0413)))
		,V_I0417  ( mod -> isNegative(mod,I0417),new ValidationMessage2016(PAGE08,I0417, mustPositiveMsg(I0417)))		
		,V_D0356  ( mod -> isNegative(mod,D0356),new ValidationMessage2016(PAGE08,D0356, mustPositiveMsg(D0356)))
		,V_D0358  ( mod -> isNegative(mod,D0358),new ValidationMessage2016(PAGE08,D0358, mustPositiveMsg(D0358)))
		,V_D0360  ( mod -> isNegative(mod,D0360),new ValidationMessage2016(PAGE08,D0360, mustPositiveMsg(D0360)))
		,V_D0226  ( mod -> isNegative(mod,D0226),new ValidationMessage2016(PAGE08,D0226, mustPositiveMsg(D0226)))
		,V_D0272  ( mod -> isNegative(mod,D0272),new ValidationMessage2016(PAGE08,D0272, mustPositiveMsg(D0272)))
		,V_D0362  ( mod -> isNegative(mod,D0362),new ValidationMessage2016(PAGE08,D0362, mustPositiveMsg(D0362)))
		,V_D0304  ( mod -> isNegative(mod,D0304),new ValidationMessage2016(PAGE08,D0304, mustPositiveMsg(D0304)))
		,V_D0505  ( mod -> isNegative(mod,D0505),new ValidationMessage2016(PAGE08,D0505, mustPositiveMsg(D0505)))
		,V_D1006  ( mod -> isNegative(mod,D1006),new ValidationMessage2016(PAGE08,D1006, mustPositiveMsg(D1006)))
		,V_D0306  ( mod -> isNegative(mod,D0306),new ValidationMessage2016(PAGE08,D0306, mustPositiveMsg(D0306)))
		,V_D0308  ( mod -> isNegative(mod,D0308),new ValidationMessage2016(PAGE08,D0308, mustPositiveMsg(D0308)))
		,V_D1004  ( mod -> isNegative(mod,D1004),new ValidationMessage2016(PAGE08,D1004, mustPositiveMsg(D1004)))
		,V_D0310  ( mod -> isNegative(mod,D0310),new ValidationMessage2016(PAGE08,D0310, mustPositiveMsg(D0310)))
		,V_D0509  ( mod -> isNegative(mod,D0509),new ValidationMessage2016(PAGE08,D0509, mustPositiveMsg(D0509)))
		,V_D0551  ( mod -> isNegative(mod,D0551),new ValidationMessage2016(PAGE08,D0551, mustPositiveMsg(D0551)))
		,V_D0322  ( mod -> isNegative(mod,D0322),new ValidationMessage2016(PAGE08,D0322, mustPositiveMsg(D0322)))
		,V_D0211  ( mod -> isNegative(mod,D0211),new ValidationMessage2016(PAGE08,D0211, mustPositiveMsg(D0211)))
		,V_D0332  ( mod -> isNegative(mod,D0332),new ValidationMessage2016(PAGE08,D0332, mustPositiveMsg(D0332)))
		,V_D0326  ( mod -> isNegative(mod,D0326),new ValidationMessage2016(PAGE08,D0326, mustPositiveMsg(D0326)))
		,V_D0394  ( mod -> isNegative(mod,D0394),new ValidationMessage2016(PAGE08,D0394, mustPositiveMsg(D0394)))
		,V_D0334  ( mod -> isNegative(mod,D0334),new ValidationMessage2016(PAGE08,D0334, mustPositiveMsg(D0334)))
		,V_D0328  ( mod -> isNegative(mod,D0328),new ValidationMessage2016(PAGE08,D0328, mustPositiveMsg(D0328)))
		,V_D0543  ( mod -> isNegative(mod,D0543),new ValidationMessage2016(PAGE08,D0543, mustPositiveMsg(D0543)))
		,V_D0336  ( mod -> isNegative(mod,D0336),new ValidationMessage2016(PAGE08,D0336, mustPositiveMsg(D0336)))
		,V_D0338  ( mod -> isNegative(mod,D0338),new ValidationMessage2016(PAGE08,D0338, mustPositiveMsg(D0338)))
		,V_D0368  ( mod -> isNegative(mod,D0368),new ValidationMessage2016(PAGE08,D0368, mustPositiveMsg(D0368)))
		,V_D0342  ( mod -> isNegative(mod,D0342),new ValidationMessage2016(PAGE08,D0342, mustPositiveMsg(D0342)))
		,V_D1010  ( mod -> isNegative(mod,D1010),new ValidationMessage2016(PAGE08,D1010, mustPositiveMsg(D1010)))
		,V_D0364  ( mod -> isNegative(mod,D0364),new ValidationMessage2016(PAGE08,D0364, mustPositiveMsg(D0364)))
		,V_D0346  ( mod -> isNegative(mod,D0346),new ValidationMessage2016(PAGE08,D0346, mustPositiveMsg(D0346)))
		,V_D0348  ( mod -> isNegative(mod,D0348),new ValidationMessage2016(PAGE08,D0348, mustPositiveMsg(D0348)))
		,V_D1012  ( mod -> isNegative(mod,D1012),new ValidationMessage2016(PAGE08,D1012, mustPositiveMsg(D1012)))
		,V_D1014  ( mod -> isNegative(mod,D1014),new ValidationMessage2016(PAGE08,D1014, mustPositiveMsg(D1014)))
		,V_D1016  ( mod -> isNegative(mod,D1016),new ValidationMessage2016(PAGE08,D1016, mustPositiveMsg(D1016)))
		,V_D0370  ( mod -> isNegative(mod,D0370),new ValidationMessage2016(PAGE08,D0370, mustPositiveMsg(D0370)))
		,V_D0278  ( mod -> isNegative(mod,D0278),new ValidationMessage2016(PAGE08,D0278, mustPositiveMsg(D0278)))
		,V_D0372  ( mod -> isNegative(mod,D0372),new ValidationMessage2016(PAGE08,D0372, mustPositiveMsg(D0372)))
		,V_D0374  ( mod -> isNegative(mod,D0374),new ValidationMessage2016(PAGE08,D0374, mustPositiveMsg(D0374)))
		,V_D1589  ( mod -> isNegative(mod,D1589),new ValidationMessage2016(PAGE08,D1589, mustPositiveMsg(D1589)))
		,V_D0376  ( mod -> isNegative(mod,D0376),new ValidationMessage2016(PAGE08,D0376, mustPositiveMsg(D0376)))
		,V_D1321  ( mod -> isNegative(mod,D1321),new ValidationMessage2016(PAGE08,D1321, mustPositiveMsg(D1321)))
		,V_D0544  ( mod -> isNegative(mod,D0544),new ValidationMessage2016(PAGE08,D0544, mustPositiveMsg(D0544)))
		,V_D1023  ( mod -> isNegative(mod,D1023),new ValidationMessage2016(PAGE08,D1023, mustPositiveMsg(D1023)))
		,V_D1019  ( mod -> isNegative(mod,D1019),new ValidationMessage2016(PAGE08,D1019, mustPositiveMsg(D1019)))
		,V_D1276  ( mod -> isNegative(mod,D1276),new ValidationMessage2016(PAGE08,D1276, mustPositiveMsg(D1276)))
		,V_D0378  ( mod -> isNegative(mod,D0378),new ValidationMessage2016(PAGE08,D0378, mustPositiveMsg(D0378)))
		,V_D0380  ( mod -> isNegative(mod,D0380),new ValidationMessage2016(PAGE08,D0380, mustPositiveMsg(D0380)))
		,V_D0382  ( mod -> isNegative(mod,D0382),new ValidationMessage2016(PAGE08,D0382, mustPositiveMsg(D0382)))
		,V_D0384  ( mod -> isNegative(mod,D0384),new ValidationMessage2016(PAGE08,D0384, mustPositiveMsg(D0384)))
		,V_D0388  ( mod -> isNegative(mod,D0388),new ValidationMessage2016(PAGE08,D0388, mustPositiveMsg(D0388)))
		,V_D0312  ( mod -> isNegative(mod,D0312),new ValidationMessage2016(PAGE08,D0312, mustPositiveMsg(D0312)))
		,V_D0314  ( mod -> isNegative(mod,D0314),new ValidationMessage2016(PAGE08,D0314, mustPositiveMsg(D0314)))
		,V_D0324  ( mod -> isNegative(mod,D0324),new ValidationMessage2016(PAGE08,D0324, mustPositiveMsg(D0324)))
		,V_D0318  ( mod -> isNegative(mod,D0318),new ValidationMessage2016(PAGE08,D0318, mustPositiveMsg(D0318)))
		,V_D0386  ( mod -> isNegative(mod,D0386),new ValidationMessage2016(PAGE08,D0386, mustPositiveMsg(D0386)))
		,V_D0390  ( mod -> isNegative(mod,D0390),new ValidationMessage2016(PAGE08,D0390, mustPositiveMsg(D0390)))
		,V_D0396  ( mod -> isNegative(mod,D0396),new ValidationMessage2016(PAGE08,D0396, mustPositiveMsg(D0396)))
		,V_D0398  ( mod -> isNegative(mod,D0398),new ValidationMessage2016(PAGE08,D0398, mustPositiveMsg(D0398)))
		,V_D0251  ( mod -> isNegative(mod,D0251),new ValidationMessage2016(PAGE08,D0251, mustPositiveMsg(D0251)))
		,V_D0392  ( mod -> isNegative(mod,D0392),new ValidationMessage2016(PAGE08,D0392, mustPositiveMsg(D0392)))
		,V_D0400  ( mod -> isNegative(mod,D0400),new ValidationMessage2016(PAGE08,D0400, mustPositiveMsg(D0400)))
		,V_D0404  ( mod -> isNegative(mod,D0404),new ValidationMessage2016(PAGE08,D0404, mustPositiveMsg(D0404)))
		,V_D0519  ( mod -> isNegative(mod,D0519),new ValidationMessage2016(PAGE08,D0519, mustPositiveMsg(D0519)))
		,V_D0512  ( mod -> isNegative(mod,D0512),new ValidationMessage2016(PAGE08,D0512, mustPositiveMsg(D0512)))
		,V_D0330  ( mod -> isNegative(mod,D0330),new ValidationMessage2016(PAGE08,D0330, mustPositiveMsg(D0330)))
		,V_D1026  ( mod -> isNegative(mod,D1026),new ValidationMessage2016(PAGE08,D1026, mustPositiveMsg(D1026)))
		,V_D0410  ( mod -> isNegative(mod,D0410),new ValidationMessage2016(PAGE08,D0410, mustPositiveMsg(D0410)))
		,V_D0412  ( mod -> isNegative(mod,D0412),new ValidationMessage2016(PAGE08,D0412, mustPositiveMsg(D0412)))
		,V_D1028  ( mod -> isNegative(mod,D1028),new ValidationMessage2016(PAGE08,D1028, mustPositiveMsg(D1028)))
		,V_D0414  ( mod -> isNegative(mod,D0414),new ValidationMessage2016(PAGE08,D0414, mustPositiveMsg(D0414)))
		,V_D0418  ( mod -> isNegative(mod,D0418),new ValidationMessage2016(PAGE08,D0418, mustPositiveMsg(D0418)))

		,V_I0415_1  ( mod -> isCooperativa(mod) && isNotZero(mod, I0415),new ValidationMessage2016(PAGE08,I0415, COOP_MSG))
		,V_D0211_1  ( mod -> isCooperativa(mod) && isNotZero(mod, D0211),new ValidationMessage2016(PAGE08,D0211, COOP_MSG))
		,V_I0416_1  ( mod -> isCooperativa(mod) && isNotZero(mod, I0416),new ValidationMessage2016(PAGE08,I0416, COOP_MSG))
		,V_D0543_1  ( mod -> isCooperativa(mod) && isNotZero(mod, D0543),new ValidationMessage2016(PAGE08,D0543, COOP_MSG))
		
		,V_I0415_2  ( mod -> isGrupo(mod) && isNotZero(mod, I0415),new ValidationMessage2016(PAGE08,I0415, GRP_MSG))
		,V_D0211_2  ( mod -> isGrupo(mod) && isNotZero(mod, D0211),new ValidationMessage2016(PAGE08,D0211, GRP_MSG))
		,V_I0416_2  ( mod -> isGrupo(mod) && isNotZero(mod, I0416),new ValidationMessage2016(PAGE08,I0416, GRP_MSG))
		,V_D0543_2  ( mod -> isGrupo(mod) && isNotZero(mod, D0543),new ValidationMessage2016(PAGE08,D0543, GRP_MSG))
		
		,V_D0370_2  ( mod -> isGreatherThan(mod,D0370,P1503),new ValidationMessage2016(PAGE08,D0370, 
				  "Confirme el importe total consignado en las claves de ingresos por dividendos declarados en la p\u00E1gina 3 [01503]"
				+ " y el importe declarado en las correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias de la clave [00370]"))
		
		,V_I0391_3  ( mod -> mod.isNotChecked(C0001) && isNotZero(mod,I0391), new ValidationMessage2016(PAGE08,I0391,incompatibleCharacterMsg(I0391,C0001))) 
		,V_D0392_3  ( mod -> mod.isNotChecked(C0001) && isNotZero(mod,D0392), new ValidationMessage2016(PAGE08,D0392,incompatibleCharacterMsg(D0392,C0001)))
		,V_I0389_3  ( mod -> mod.isNotChecked(C0002) && isNotZero(mod,I0389), new ValidationMessage2016(PAGE08,I0389,incompatibleCharacterMsg(I0389,C0002)))
		,V_D0390_3  ( mod -> mod.isNotChecked(C0002) && isNotZero(mod,D0390), new ValidationMessage2016(PAGE08,D0390,incompatibleCharacterMsg(D0390,C0002)))
		,V_I0371_3  ( mod -> mod.isNotChecked(C0003) && isNotZero(mod,I0371), new ValidationMessage2016(PAGE08,I0371,incompatibleCharacterMsg(I0371,C0003)))
		,V_I0311_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0311), new ValidationMessage2016(PAGE08,I0311,incompatibleCharacterMsg(I0311,C0006)))
		,V_D0312_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0312), new ValidationMessage2016(PAGE08,D0312,incompatibleCharacterMsg(D0312,C0006)))
		,V_I0313_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0313), new ValidationMessage2016(PAGE08,I0313,incompatibleCharacterMsg(I0313,C0006)))
		,V_D0314_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0314), new ValidationMessage2016(PAGE08,D0314,incompatibleCharacterMsg(D0314,C0006)))
		,V_I0323_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0323), new ValidationMessage2016(PAGE08,I0323,incompatibleCharacterMsg(I0323,C0006)))
		,V_D0324_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0324), new ValidationMessage2016(PAGE08,D0324,incompatibleCharacterMsg(D0324,C0006)))
		,V_I0387_3  ( mod -> mod.isNotChecked(C0007) && isNotZero(mod,I0387), new ValidationMessage2016(PAGE08,I0387,incompatibleCharacterMsg(I0387,C0007)))
		,V_D0388_3  ( mod -> mod.isNotChecked(C0007) && isNotZero(mod,D0388), new ValidationMessage2016(PAGE08,D0388,incompatibleCharacterMsg(D0388,C0007)))
		,V_D0396_3  ( mod -> mod.isNotChecked(C0005) && isNotZero(mod,D0396), new ValidationMessage2016(PAGE08,D0396,incompatibleCharacterMsg(D0396,C0005)))
		,V_I0385_3  ( mod -> mod.isNotChecked(C0011) && isNotZero(mod,I0385), new ValidationMessage2016(PAGE08,I0385,incompatibleCharacterMsg(I0385,C0011)))
		,V_D0386_3  ( mod -> mod.isNotChecked(C0011) && isNotZero(mod,D0386), new ValidationMessage2016(PAGE08,D0386,incompatibleCharacterMsg(D0386,C0011)))
		,V_I0309_3  ( mod -> mod.isNotChecked(C0020) && isNotZero(mod,I0309), new ValidationMessage2016(PAGE08,I0309,incompatibleCharacterMsg(I0309,C0020)))
		,V_D0310_3  ( mod -> mod.isNotChecked(C0020) && isNotZero(mod,D0310), new ValidationMessage2016(PAGE08,D0310,incompatibleCharacterMsg(D0310,C0020)))
		,V_I0397_3  ( mod -> mod.isNotChecked(C0022) && isNotZero(mod,I0397), new ValidationMessage2016(PAGE08,I0397,incompatibleCharacterMsg(I0397,C0022)))
		,V_D0398_3  ( mod -> mod.isNotChecked(C0022) && isNotZero(mod,D0398), new ValidationMessage2016(PAGE08,D0398,incompatibleCharacterMsg(D0398,C0022)))
		,V_I0403_3  ( mod -> mod.isNotChecked(C0029) && isNotZero(mod,I0403), new ValidationMessage2016(PAGE08,I0403,incompatibleCharacterMsg(I0403,C0029)))
		,V_D0404_3  ( mod -> mod.isNotChecked(C0029) && isNotZero(mod,D0404), new ValidationMessage2016(PAGE08,D0404,incompatibleCharacterMsg(D0404,C0029)))
		,V_I0383_3  ( mod -> mod.isNotChecked(C0034) && isNotZero(mod,I0383), new ValidationMessage2016(PAGE08,I0383,incompatibleCharacterMsg(I0383,C0034)))
		,V_D0384_3  ( mod -> mod.isNotChecked(C0034) && isNotZero(mod,D0384), new ValidationMessage2016(PAGE08,D0384,incompatibleCharacterMsg(D0384,C0034)))
		,V_I0409_3  ( mod -> mod.isNotChecked(C0046) && isNotZero(mod,I0409), new ValidationMessage2016(PAGE08,I0409,incompatibleCharacterMsg(I0409,C0046)))
		,V_D0410_3  ( mod -> mod.isNotChecked(C0046) && isNotZero(mod,D0410), new ValidationMessage2016(PAGE08,D0410,incompatibleCharacterMsg(D0410,C0046)))
		,V_I0411_3  ( mod -> mod.isNotChecked(C0047) && isNotZero(mod,I0411), new ValidationMessage2016(PAGE08,I0411,incompatibleCharacterMsg(I0411,C0047)))
		,V_D0412_3  ( mod -> mod.isNotChecked(C0047) && isNotZero(mod,D0412), new ValidationMessage2016(PAGE08,D0412,incompatibleCharacterMsg(D0412,C0047)))

		,V_D0404_4  ( mod -> isNotZero(mod,D0404) && (getValue(mod,D0404) >= (getValue(mod,LQ650) * 0.90)) , new ValidationMessage2016(PAGE08,D0404,"Revise importe disminuciones RIC"))
		
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (II) ----------------------------
		// ------------------------------------------------------------------------
		
		// -- Entidades navieras en regimen  de tributación en función del tonelaje
		,V_LQ631_1  ( mod -> isLessThan(mod,LQ631,LQ632), new ValidationMessage2016(PAGE09,LQ631,mustGreatherMsg(LQ631,LQ632)))

		// -- Reserva de capitalizacion
		,V_LQ1140_1  ( mod -> isLessThan(mod,BP1001,LQ1140), new ValidationMessage2016(PAGE09,LQ1140,mustGreatherMsg(BP1001,LQ1140)))
		
		// -- Compensación de bases imponibles negativas de períodos anteriores
		,V_LQ640_1  ( mod -> isGreatherThan(mod,  LQ641  ,LQ640  ), new ValidationMessage2016(PAGE09,LQ641  ,mustLessMsg(LQ641  ,LQ640 )))    
		,V_LQ643_1  ( mod -> isGreatherThan(mod,  LQ644  ,LQ643  ), new ValidationMessage2016(PAGE09,LQ644  ,mustLessMsg(LQ644  ,LQ643 )))
		,V_LQ646_1  ( mod -> isGreatherThan(mod,  LQ647  ,LQ646  ), new ValidationMessage2016(PAGE09,LQ647  ,mustLessMsg(LQ647  ,LQ646 )))
		,V_LQ649_1  ( mod -> isGreatherThan(mod,  LQ650  ,LQ649  ), new ValidationMessage2016(PAGE09,LQ650  ,mustLessMsg(LQ650  ,LQ649 )))
		,V_LQ652_1  ( mod -> isGreatherThan(mod,  LQ653  ,LQ652  ), new ValidationMessage2016(PAGE09,LQ653  ,mustLessMsg(LQ653  ,LQ652 )))
		,V_LQ655_1  ( mod -> isGreatherThan(mod,  LQ656  ,LQ655  ), new ValidationMessage2016(PAGE09,LQ656  ,mustLessMsg(LQ656  ,LQ655 )))
		,V_LQ658_1  ( mod -> isGreatherThan(mod,  LQ659  ,LQ658  ), new ValidationMessage2016(PAGE09,LQ659  ,mustLessMsg(LQ659  ,LQ658 )))
		,V_LQ661_1  ( mod -> isGreatherThan(mod,  LQ662  ,LQ661  ), new ValidationMessage2016(PAGE09,LQ662  ,mustLessMsg(LQ662  ,LQ661 )))
		,V_LQ664_1  ( mod -> isGreatherThan(mod,  LQ665  ,LQ664  ), new ValidationMessage2016(PAGE09,LQ665  ,mustLessMsg(LQ665  ,LQ664 )))
		,V_LQ667_1  ( mod -> isGreatherThan(mod,  LQ668  ,LQ667  ), new ValidationMessage2016(PAGE09,LQ668  ,mustLessMsg(LQ668  ,LQ667 )))
		,V_LQ743_1  ( mod -> isGreatherThan(mod,  LQ747  ,LQ743  ), new ValidationMessage2016(PAGE09,LQ747  ,mustLessMsg(LQ747  ,LQ743 )))
		,V_LQ275_1  ( mod -> isGreatherThan(mod,  LQ276  ,LQ275  ), new ValidationMessage2016(PAGE09,LQ276  ,mustLessMsg(LQ276  ,LQ275 )))
		,V_LQ608_1  ( mod -> isGreatherThan(mod,  LQ609  ,LQ608  ), new ValidationMessage2016(PAGE09,LQ609  ,mustLessMsg(LQ609  ,LQ608 )))
		,V_LQ704_1  ( mod -> isGreatherThan(mod,  LQ705  ,LQ704  ), new ValidationMessage2016(PAGE09,LQ705  ,mustLessMsg(LQ705  ,LQ704 )))
		,V_LQ013_1  ( mod -> isGreatherThan(mod,  LQ014  ,LQ013  ), new ValidationMessage2016(PAGE09,LQ014  ,mustLessMsg(LQ014  ,LQ013 )))
		,V_LQ725_1  ( mod -> isGreatherThan(mod,  LQ726  ,LQ725  ), new ValidationMessage2016(PAGE09,LQ726  ,mustLessMsg(LQ726  ,LQ725 )))
		,V_LQ534_1  ( mod -> isGreatherThan(mod,  LQ535  ,LQ534  ), new ValidationMessage2016(PAGE09,LQ535  ,mustLessMsg(LQ535  ,LQ534 )))
		,V_LQ607_1  ( mod -> isGreatherThan(mod,  LQ675  ,LQ607  ), new ValidationMessage2016(PAGE09,LQ675  ,mustLessMsg(LQ675  ,LQ607 )))
		,V_LQ1045_1 ( mod -> isGreatherThan(mod,  LQ1046 ,LQ1045 ), new ValidationMessage2016(PAGE09,LQ1046 ,mustLessMsg(LQ1046 ,LQ1045)))
		,V_LQ1519_1 ( mod -> isGreatherThan(mod,  LQ1520 ,LQ1519 ), new ValidationMessage2016(PAGE09,LQ1520 ,mustLessMsg(LQ1520 ,LQ1519)))
		,V_LQ670_1  ( mod -> isGreatherThan(mod,  LQ547  ,LQ670  ), new ValidationMessage2016(PAGE09,LQ547  ,mustLessMsg(LQ547  ,LQ670 )))
		
		// -- Sólo sociedades cooperativas
		,V_LQ553_1  ( mod -> isCooperativa(mod)  && !AonMathUtils.equals(getValue(mod,LQ552),(getValue(mod,LQ553)+getValue(mod,LQ554))), 
				new ValidationMessage2016(PAGE09,LQ553,"La suma de las casillas \""+LQ553.getDescription()+"\" y \""+LQ554.getDescription()
														+"\" debe ser igual que \""+LQ552.getDescription()+"\""))

		// -- Rentas que no limitan la compensación de bases imponibles y cuotas negativas
		,V_LQ545  ( mod -> isNegative(mod,LQ545) ,new ValidationMessage2016(PAGE09,LQ545 , mustPositiveMsg(LQ545)))
		,V_LQ593  ( mod -> isNegative(mod,LQ593) ,new ValidationMessage2016(PAGE08,LQ593 , mustPositiveMsg(LQ593)))
		,V_LQ1509 ( mod -> isNegative(mod,LQ1509),new ValidationMessage2016(PAGE08,LQ1509, mustPositiveMsg(LQ1509)))
		,V_LQ1510 ( mod -> isNegative(mod,LQ1510),new ValidationMessage2016(PAGE08,LQ1510, mustPositiveMsg(LQ1510)))
		;		
		private IValidator validator;
		private ValidationMessage2016 message;
		private Type( IValidator validator,ValidationMessage2016 message){
			this.validator = validator;
			this.message = message;
		}
		public boolean validate(Mod2002016 mod) {
			return (this.validator.validate(mod));
		}
		public ValidationMessage2016 getMessage() {
			return message;
		}
	}
	public static void validate(Mod2002016 mod) {
		validateAdministrators(mod);
		validateParticipationsIn(mod);
		validateParticipationsOut(mod);
		validateRepresentatives(mod);
		for ( Type type : Type.values()) {
			if (type.validate(mod)) {
				mod.getMessages().add(type.getMessage());
			}
		}
	}
	
	private static void validateRepresentatives(Mod2002016 mod200) {
		if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getRepresentatives() == null || mod200.getRepresentatives().size() == 0 ) {
				mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Para personas jur\u00EDdicas, debe rellenar al menos un representante."));
			} else {
				for (int i = 0; i < mod200.getRepresentatives().size(); i++ ) {
					LegalRepresentative lr = mod200.getRepresentatives().get(i); 
					if (!AonDocumentUtil.isValid(lr.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE01,"NIF del representante legal n\u00BA "+(i+1) +" incorrecto ["+lr.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(lr.getName())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Falta nombre del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					}
					if (AonStringUtils.isEmpty(lr.getNotary())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Falta el dato de la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					} else if (lr.getNotary().length() > 20) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Longitud excedida en la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]. Debe limitarse a 20 caracteres."));	
					}
					if (lr.getNotaryDate() == null) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Falta el dato fecha de la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateAdministrators(Mod2002016 mod200) {
		if (mod200.getAdministrators() == null || mod200.getAdministrators().size() == 0 ) {
			mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Debe rellenar al menos un administrador."));
		} else {
			for (int i = 0; i < mod200.getAdministrators().size(); i++ ) {
				CompanyAdministrator ca = mod200.getAdministrators().get(i); 
				if (!AonDocumentUtil.isValid(ca.getDocument())) {
					mod200.getMessages().add(new ValidationMessage2016(PAGE01,"NIF del administrador n\u00BA "+(i+1) +" incorrecto ["+ca.getDocument()+"]"));		
				}
				if (AonStringUtils.isEmpty(ca.getName())) {
					mod200.getMessages().add(new ValidationMessage2016(PAGE01,"Falta nombre del administrador n\u00BA "+(i+1) +". ["+ca.getDocument()+"]"));
				}
			}
		}
	}

	private static void validateParticipationsIn(Mod2002016 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())
			&& !AonDocumentUtil.isCulturalAssociation(mod200.getEnterpriseDocument())) {
			 LinkedList<CompanyParticipation> participations = mod200.getParticipationsIn();
			if (participations == null || participations.size() == 0) {
				mod200.getMessages().add(new ValidationMessage2016(PAGE02,"Para personas jur\u00EDdicas, debe rellenar los datos de participaci\u00F3n en la declarante"));
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"NIF de la participaci\u00F3n en la declarante n\u00BA "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"Falta nombre de la participaci\u00F3n en la declarante n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"Porcentaje no correcto en la participaci\u00F3n en la declarante n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateParticipationsOut(Mod2002016 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			 LinkedList<CompanyParticipation> participations = mod200.getParticipationsOut();
			if (participations == null || participations.size() == 0) {
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"NIF de la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"Falta nombre de la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						mod200.getMessages().add(new ValidationMessage2016(PAGE02,"Porcentaje no correcto en la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	
	// ***************************************************************************************************************
	// ***************************************************************************************************************
	// **********************************************************************************************************
	// ***************************************************************************************************************
	// ***************************************************************************************************************
	// ***************************************************************************************************************

	private static final String EQUAL_GREATER_MSG = "\"{0}\" debe ser mayor o igual que \"{1}\".";
	private static final String EQUAL_GREATER_EXP = "round({0}) >= round({1})";
	
	private static final String EQUAL_LESS_MSG = "\"{0}\" debe ser menor o igual que \"{1}\".";
	private static final String EQUAL_LESS_EXP = "round({0}) <= round({1})";

//	private static final String EQUAL_LESS_FACTOR_MSG = "\"{0}\" debe ser menor o igual que el {2} por \"{1}\".";
//	private static final String EQUAL_LESS_FACTOR_EXP = "round({0}) <= round({1} * {2})";

	private static final String MUST_POSITIVE_EXP = "round({0}) >= 0.0";
	
//  private static final String EQUAL_MSG = "\"{0}\" debe igual que \"{1}\".";
//  private static final String EQUAL_EXP = "round({0}) == round({1})";
//	private static final String MUST_EQUAL_MSG = "\"{0}\" y \"{1}\" deben ser iguales.";
//	private static final String CHECK_SIGN_MSG = "Verifique el signo de la clave: \"{0}\"";
//	private static final String MUST_NEGATIVE_EXP = "round({0}) <= 0.0";
//	private static final String INV_BOX_EXP = "round({0}) == 0.0 || (round({0}) > 0.0 && {1})";
//	private static final String INCOMPATIBLE_MSG = "Casilla \"{0}\" incompatible con \"{1}\"";
//	private static final String MSG_581 = "Si existen deducciones por doble imposici\u00F3n pendientes "
//			+ "de aplicar no podr\u00E1n aplicarse ni las bonificaciones del art\u00EDculo 76 de la Ley "
//			+ "19/1994, ni las deducciones por inversiones";
	

	public static List<ValidationMessage2016> VALIDATION_EXPRESSION_LIST = new LinkedList<ValidationMessage2016>();

	//	**************************************************************************************
	//	**************************************************************************************
	//								LA CONDICIÓN DEBE CUMPLIRSE.
	//	**************************************************************************************
	//	**************************************************************************************
	
	
	static {	// PAGE 09
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage(PAGE09,LQ552
//				,LQ552.getDescription() + " debe ser cero con el caracter \"" + C0027.getDescription() + "\" marcado"
//				,"C0027?LQ552<=0:LQ552>0"));
		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ1140
//				,MessageFormat.format(MUST_EQUAL_MSG,"Reserva de capitalizaci\u00F3n dotada en el ejercicio",BP1001.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LQ1140.toString(),BP1001.toString())));

/*
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_MSG,LQ641.getDescription(),LQ640.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ641.toString(),LQ640.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_MSG,LQ644.getDescription(),LQ643.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ644.toString(),LQ643.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_MSG,LQ647.getDescription(),LQ646.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ647.toString(),LQ646.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_MSG,LQ650.getDescription(),LQ649.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ650.toString(),LQ649.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_MSG,LQ653.getDescription(),LQ652.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ653.toString(),LQ652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_MSG,LQ656.getDescription(),LQ655.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ656.toString(),LQ655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_MSG,LQ659.getDescription(),LQ658.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ659.toString(),LQ658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_MSG,LQ662.getDescription(),LQ661.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ662.toString(),LQ661.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_MSG,LQ665.getDescription(),LQ664.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ665.toString(),LQ664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_MSG,LQ668.getDescription(),LQ667.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ668.toString(),LQ667.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_MSG,LQ747.getDescription(),LQ743.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ747.toString(),LQ743.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_MSG,LQ276.getDescription(),LQ275.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ276.toString(),LQ275.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_MSG,LQ609.getDescription(),LQ608.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ609.toString(),LQ608.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_MSG,LQ705.getDescription(),LQ704.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ705.toString(),LQ704.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_MSG,LQ014.getDescription(),LQ013.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ014.toString(),LQ013.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_MSG,LQ726.getDescription(),LQ725.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ726.toString(),LQ725.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_MSG,LQ535.getDescription(),LQ534.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ535.toString(),LQ534.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ675
				,MessageFormat.format(EQUAL_LESS_MSG,LQ675.getDescription(),LQ607.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ675.toString(),LQ607.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ1046
				,MessageFormat.format(EQUAL_LESS_MSG,LQ1046.getDescription(),LQ1045.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ1046.toString(),LQ1045.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ547
				,MessageFormat.format(EQUAL_LESS_MSG,LQ547.getDescription(),LQ670.getDescription())
				,"C0034?true:"+MessageFormat.format(EQUAL_LESS_EXP,LQ547.toString(),LQ670.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ641
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ641.getDescription(),LQ640.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ641.toString(),LQ640.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ644
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ644.getDescription(),LQ643.getDescription(),"0.25")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ644.toString(),LQ643.toString(),"0.25")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ647
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ647.getDescription(),LQ646.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ647.toString(),LQ646.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ650
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ650.getDescription(),LQ649.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ650.toString(),LQ649.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ653
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ653.getDescription(),LQ652.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ653.toString(),LQ652.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ656
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ656.getDescription(),LQ655.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ656.toString(),LQ655.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ659
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ659.getDescription(),LQ658.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ659.toString(),LQ658.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ662
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ662.getDescription(),LQ661.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ662.toString(),LQ661.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ665
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ665.getDescription(),LQ664.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ665.toString(),LQ664.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ668
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ668.getDescription(),LQ667.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ668.toString(),LQ667.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ747
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ747.getDescription(),LQ743.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ747.toString(),LQ743.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ276
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ276.getDescription(),LQ275.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ276.toString(),LQ275.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ609
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ609.getDescription(),LQ608.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ609.toString(),LQ608.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ705
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ705.getDescription(),LQ704.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ705.toString(),LQ704.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ014
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ014.getDescription(),LQ013.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ014.toString(),LQ013.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ726
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ726.getDescription(),LQ725.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ726.toString(),LQ725.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ535
				,MessageFormat.format(EQUAL_LESS_FACTOR_MSG,LQ535.getDescription(),LQ534.getDescription(),"0.50")
				,"C0034?"+MessageFormat.format(EQUAL_LESS_FACTOR_EXP,LQ535.toString(),LQ534.toString(),"0.50")+":true"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ559
				,"La casilla \""+LQ559.getDescription()+"\" debe ser mayor igual que cero y menor o igual que la casilla \""+LQ552.getDescription()+"\""
				,"C0015?(0.0 <= round(LQ559) && round(LQ559) <= round(LQ552)):true"));
*/
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ674
				,MessageFormat.format(EQUAL_GREATER_MSG,"673","674")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ673.toString(),LQ674.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ677
				,MessageFormat.format(EQUAL_GREATER_MSG,"676","677")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ676.toString(),LQ677.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ680
				,MessageFormat.format(EQUAL_GREATER_MSG,"679","680")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ679.toString(),LQ680.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ683
				,MessageFormat.format(EQUAL_GREATER_MSG,"682","683")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ682.toString(),LQ683.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ686
				,MessageFormat.format(EQUAL_GREATER_MSG,"685","686")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ685.toString(),LQ686.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ689
				,MessageFormat.format(EQUAL_GREATER_MSG,"688","689")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ688.toString(),LQ689.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ692
				,MessageFormat.format(EQUAL_GREATER_MSG,"691","692")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ691.toString(),LQ692.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ099
				,MessageFormat.format(EQUAL_GREATER_MSG,"059","099")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ059.toString(),LQ099.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ018
				,MessageFormat.format(EQUAL_GREATER_MSG,"017","018")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ017.toString(),LQ018.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ773
				,MessageFormat.format(EQUAL_GREATER_MSG,"772","773")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ772.toString(),LQ773.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ908
				,MessageFormat.format(EQUAL_GREATER_MSG,"907","908")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ907.toString(),LQ908.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ911
				,MessageFormat.format(EQUAL_GREATER_MSG,"910","911")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ910.toString(),LQ911.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ936
				,MessageFormat.format(EQUAL_GREATER_MSG,"935","936")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ935.toString(),LQ936.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ515
				,MessageFormat.format(EQUAL_GREATER_MSG,"587","515")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ587.toString(),LQ515.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ280
				,MessageFormat.format(EQUAL_GREATER_MSG,"279","280")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ279.toString(),LQ280.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE09,LQ624
				,MessageFormat.format(EQUAL_GREATER_MSG,"623","624")
				,MessageFormat.format(EQUAL_GREATER_EXP,LQ623.toString(),LQ624.toString())));
	}
	
	static {// PAGE 10
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN565
				,"No puede aplicarse la clave 565 mientras existan saldos pendientes "
				+ "de aplicaci\u00F3n de deducciones por doble imposici\u00F3n o de deducciones "
				+ "del cap\u00EDtulo IV T\u00EDtulo VI de la Ley del Impuesto."
				,"BN565 == 0 || (BN565 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0 && BN832 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN567
				,"La clave 567 no puede superar el 50% de la clave 562"
				,"BN567 <= (LQ562 * 50 /100)"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN567
				,MessageFormat.format(EQUAL_GREATER_MSG,BN567.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN567.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN568
				,MessageFormat.format(EQUAL_GREATER_MSG,BN568.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN568.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN563
				,MessageFormat.format(EQUAL_GREATER_MSG,BN563.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN563.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN566
				,MessageFormat.format(EQUAL_GREATER_MSG,BN566.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN566.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN576
				,MessageFormat.format(EQUAL_GREATER_MSG,BN576.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN576.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN569
				,MessageFormat.format(EQUAL_GREATER_MSG,BN569.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN569.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN570
				,MessageFormat.format(EQUAL_GREATER_MSG,BN570.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN570.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN571
				,MessageFormat.format(EQUAL_GREATER_MSG,BN571.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN571.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN572
				,MessageFormat.format(EQUAL_GREATER_MSG,BN572.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN572.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN573
				,MessageFormat.format(EQUAL_GREATER_MSG,BN573.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN573.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN575
				,MessageFormat.format(EQUAL_GREATER_MSG,BN575.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN575.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN577
				,MessageFormat.format(EQUAL_GREATER_MSG,BN577.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN577.toString(),"0")));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN581
				,MessageFormat.format(EQUAL_GREATER_MSG,BN581.toString(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN581.toString(),"0")));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN582
				,MessageFormat.format(EQUAL_LESS_MSG,BN582.getDescription(),LQ562.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN582.toString(),LQ562.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN581
				,"No puede aplicarse la clave 581 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN581 == 0 || (BN581 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN583
				,"No puede aplicarse la clave 583 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN583 == 0 || (BN583 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN585
				,"No puede aplicarse la clave 585 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN585 == 0 || (BN585 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN584
				,"No puede aplicarse la clave 584 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN584 == 0 || (BN584 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN588
				,"No puede aplicarse la clave 588 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN588 == 0 || (BN588 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN590
				,"No puede aplicarse la clave 590 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN590 == 0 || (BN590 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN082
				,"No puede aplicarse la clave 082 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."
				,"BN082 == 0 || (BN082 != 0 && (BN118 == 0 && BN133 == 0 && BN162 == 0 && BN174 == 0))"));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN563
				,MessageFormat.format(EQUAL_GREATER_MSG,BN563.getDescription(),C0029.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN563.toString(),C0029.toString())));
		
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN566
				,BN566.getDescription() + " no procede. Caracter '" + C0017.getDescription() + "' o '"+C0018.getDescription()+"' no marcado." 
				,"(BN566 == 0.0) || (BN566 > 0 && (C0017 || C0018))"));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN576
				,MessageFormat.format(EQUAL_GREATER_MSG,BN576.getDescription(),C0038.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN576.toString(),C0038.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN575
				,MessageFormat.format(EQUAL_GREATER_MSG,BN575.getDescription(),C0007.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN575.toString(),C0007.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN581
				,MessageFormat.format(EQUAL_GREATER_MSG,BN581.getDescription(),C0015.getDescription())
				,MessageFormat.format(EQUAL_GREATER_EXP,BN581.toString(),C0015.toString())));
		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN581,MSG_581
//				,"BN581 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN583,MSG_581
//				,"BN583 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN585,MSG_581
//				,"BN585 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN584,MSG_581
//				,"BN584 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN588,MSG_581
//				,"BN588 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN590,MSG_581
//				,"BN590 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN082,MSG_581
//				,"BN082 != 0?(BN118 > 0 || BN133 > 0 || BN162 > 0 || BN174 > 0):true"));

//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN697
//				,MessageFormat.format(EQUAL_LESS_MSG,BN697.getDescription(),BN696.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,BN697.toString(),BN696.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN847
				,MessageFormat.format(EQUAL_LESS_MSG,BN847.getDescription(),BN846.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN847.toString(),BN846.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN283
				,MessageFormat.format(EQUAL_LESS_MSG,BN283.getDescription(),BN282.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN283.toString(),BN282.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN703
				,MessageFormat.format(EQUAL_LESS_MSG,BN703.getDescription(),BN702.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN703.toString(),BN702.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN187
				,MessageFormat.format(EQUAL_LESS_MSG,BN187.getDescription(),BN071.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN187.toString(),BN071.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN026
				,MessageFormat.format(EQUAL_LESS_MSG,BN026.getDescription(),BN025.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN026.toString(),BN025.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN715
				,MessageFormat.format(EQUAL_LESS_MSG,BN715.getDescription(),BN714.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN715.toString(),BN714.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN737
				,MessageFormat.format(EQUAL_LESS_MSG,BN737.getDescription(),BN736.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN737.toString(),BN736.toString())));
	
		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN712
//				,MessageFormat.format(EQUAL_LESS_MSG,BN712.getDescription(),BN711.getDescription()) 
//				,MessageFormat.format(EQUAL_LESS_EXP,BN712.toString(),BN711.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN638
				,MessageFormat.format(EQUAL_LESS_MSG,BN638.getDescription(),BN637.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN638.toString(),BN637.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN894
				,MessageFormat.format(EQUAL_LESS_MSG,BN894.getDescription(),BN849.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN894.toString(),BN849.toString()))); 
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN286
				,MessageFormat.format(EQUAL_LESS_MSG,BN286.getDescription(),BN285.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN286.toString(),BN285.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN826
				,MessageFormat.format(EQUAL_LESS_MSG,BN826.getDescription(),BN825.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN826.toString(),BN825.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN002
				,MessageFormat.format(EQUAL_LESS_MSG,BN002.getDescription(),BN001.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN002.toString(),BN001.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN029
				,MessageFormat.format(EQUAL_LESS_MSG,BN029.getDescription(),BN028.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN029.toString(),BN028.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN718
				,MessageFormat.format(EQUAL_LESS_MSG,BN718.getDescription(),BN717.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN718.toString(),BN717.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN723
				,MessageFormat.format(EQUAL_LESS_MSG,BN723.getDescription(),BN722.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN723.toString(),BN722.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN741
				,MessageFormat.format(EQUAL_LESS_MSG,BN741.getDescription(),BN740.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN741.toString(),BN740.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN136
				,MessageFormat.format(EQUAL_LESS_MSG,BN136.getDescription(),BN135.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN136.toString(),BN135.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN585
				,MessageFormat.format(EQUAL_LESS_MSG,BN585.getDescription(),BN582.getDescription())
				,MessageFormat.format(EQUAL_LESS_EXP,BN585.toString(),BN582.toString())));

		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE10,BN592
				,MessageFormat.format(EQUAL_GREATER_MSG,BN592.getDescription(),"0")
				,MessageFormat.format(EQUAL_GREATER_EXP,BN592.toString(),"0")));
	}

	static {	// PAGE 11
	}

	static {	// PAGE 12
		//VALIDATION_EXPRESSION_LIST.add(new EqualValidationMessage2016(PAGE14,ID653,ID666));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID650
				,MessageFormat.format(CHECK_SIGN_MSG,ID650.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID650.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID651
				,MessageFormat.format(CHECK_SIGN_MSG,ID651.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID651.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID652
				,MessageFormat.format(CHECK_SIGN_MSG,ID652.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID652.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID666
				,MessageFormat.format(CHECK_SIGN_MSG,ID666.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID666.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID654
				,MessageFormat.format(CHECK_SIGN_MSG,ID654.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID654.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID655
				,MessageFormat.format(CHECK_SIGN_MSG,ID655.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID655.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID656
				,MessageFormat.format(CHECK_SIGN_MSG,ID656.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID656.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID658
				,MessageFormat.format(CHECK_SIGN_MSG,ID658.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID658.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID659
				,MessageFormat.format(CHECK_SIGN_MSG,ID659.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID659.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID660
				,MessageFormat.format(CHECK_SIGN_MSG,ID660.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID660.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID662
				,MessageFormat.format(CHECK_SIGN_MSG,ID662.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID662.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID664
				,MessageFormat.format(CHECK_SIGN_MSG,ID664.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID664.toString())));
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE14,ID665
				,MessageFormat.format(CHECK_SIGN_MSG,ID665.getDescription())
				,MessageFormat.format(MUST_POSITIVE_EXP,ID665.toString())));
	}
	
	
	static {
		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE08,D0364
				,"Compruebe la correspondencia entre el importe declarado en "
				+ "la clave 364 y los de las claves 1258 y 1259 de la p\u00E1gina 14."
				,"D0364 == round(LM1258 + LM1259)"));
	}

	static { // PAGE 13 		
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM259
//				,"La casilla 259 debe ser menor o igual que la suma de las casillas 043 y 049" 
//				,"LM259 <= (LM043+LM049)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM254
//				,MessageFormat.format(EQUAL_LESS_MSG,LM254.getDescription(),LM253.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM254.toString(),LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255
//				,MessageFormat.format(EQUAL_LESS_MSG,LM255.getDescription(),LM253.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM255.toString(),LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM260
//				,MessageFormat.format(EQUAL_LESS_MSG,LM260.getDescription(),LM259.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM260.toString(),LM259.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM049
//				,MessageFormat.format(CHECK_SIGN_MSG,LM049.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM049.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM249
//				,MessageFormat.format(CHECK_SIGN_MSG,LM249.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM249.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM252
//				,MessageFormat.format(CHECK_SIGN_MSG,LM252.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM252.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM253
//				,MessageFormat.format(CHECK_SIGN_MSG,LM253.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM253.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM254
//				,MessageFormat.format(CHECK_SIGN_MSG,LM254.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM254.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255
//				,MessageFormat.format(CHECK_SIGN_MSG,LM255.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM255.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM258
//				,MessageFormat.format(CHECK_SIGN_MSG,LM258.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM258.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM259
//				,MessageFormat.format(CHECK_SIGN_MSG,LM259.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM259.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM260
//				,MessageFormat.format(CHECK_SIGN_MSG,LM260.getDescription())
//				,MessageFormat.format(MUST_POSITIVE_EXP,LM260.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM175 
//				,MessageFormat.format(EQUAL_MSG,LM175.getDescription() + " (175)",PG296.getDescription()+ "(PYG - 296)")
//				,MessageFormat.format(EQUAL_EXP,LM175.toString(),PG296.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM176 
//				,MessageFormat.format(EQUAL_MSG,LM176.getDescription() + " (176)",PG284.getDescription()+ "(PYG - 284)")
//				,MessageFormat.format(EQUAL_EXP,LM176.toString(),PG284.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM177 
//				,MessageFormat.format(EQUAL_MSG,LM177.getDescription() + " (177)",PG285.getDescription()+ "(PYG - 285)")
//				,MessageFormat.format(EQUAL_EXP,LM177.toString(),PG285.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM178 
//				,MessageFormat.format(EQUAL_MSG,LM178.getDescription() + " (178)",PG287.getDescription()+ "(PYG - 287)")
//				,MessageFormat.format(EQUAL_EXP,LM178.toString(),PG287.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM179 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM179.getDescription() + " (179)",PG298.getDescription()+ "(PYG - 298)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM179.toString(),PG298.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM254
//				,"\"" + LM254.getDescription() +"\" debe ser menor o igual que la suma de \"" 
//				+ LM043.getDescription() + "\" y \""
//				+ LM049.getDescription() + "\"" 
//				,"LM254<=LM043+LM049"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM254
//				,"Si el importe de la casilla 254 es menor que la suma de los importes de las "
//				+ "casillas 043 + 049, dicho importe debe ser igual al de la casilla 253" 
//				,"LM254<(LM043+LM049)?LM254==LM253:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255
//				,"El importe de la casilla 255 debe ser igual a la diferencia "
//				+ "de las casillas 253 menos 254" 
//				,"LM255==(LM253-LM254)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255
//				,"Si el importe de la casilla 255 es mayor que cero, la casilla 258 debe ser cero" 
//				,"LM255>0?LM258==0:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255
//				,"Si el importe de la casilla 043 es mayor que la suma de las casillas 254 y 258, la casilla 049 debe ser cero" 
//				,"(LM254+LM258)<=LM043?LM049==0:true"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM255 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM255.getDescription() 
//				+ " (255)",Mod2002014CorrectionKey.C0040.getDescription()+ " (Correcciones Contables - 363)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM255.toString(),I0363.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM258 
//				,MessageFormat.format(EQUAL_LESS_MSG,LM258.getDescription() 
//				+ " (258)",Mod2002014CorrectionKey.C0040.getDescription()+ "(Correcciones Contables - 364)")
//				,MessageFormat.format(EQUAL_LESS_EXP,LM258.toString(),D0364.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM970
//				,MessageFormat.format(EQUAL_LESS_MSG,LM970.getDescription(),LM969.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM970.toString(),LM969.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM262
//				,MessageFormat.format(EQUAL_LESS_MSG,LM262.getDescription(),LM261.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM262.toString(),LM261.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM265
//				,MessageFormat.format(EQUAL_LESS_MSG,LM265.getDescription(),LM264.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM265.toString(),LM264.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM266
//				,"Compruebe los gastos financieros pendientes de deducir. " 
//				+MessageFormat.format(MUST_EQUAL_MSG,LM266.getDescription(),LM255.getDescription())
//				,MessageFormat.format(MUST_EQUAL_EXP,LM266.toString(),LM255.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM258
//				,"Compruebe los gastos financieros pendientes de deducir de per\u00EDodos "
//				+ "anteriores aplicados en esta liquidaci\u00F3n, claves 258, 970 y 262" 
//				,"LM258==(LM970+LM262)"));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM522
//				,MessageFormat.format(EQUAL_LESS_MSG,LM522.getDescription(),LM503.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM522.toString(),LM503.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM271
//				,MessageFormat.format(EQUAL_LESS_MSG,LM271.getDescription(),LM270.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM271.toString(),LM270.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM274
//				,MessageFormat.format(EQUAL_LESS_MSG,LM274.getDescription(),LM273.getDescription())
//				,MessageFormat.format(EQUAL_LESS_EXP,LM274.toString(),LM273.toString())));
//		VALIDATION_EXPRESSION_LIST.add(new ValidationMessage2016(PAGE13,LM957
//				,"Compruebe el importe pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado"
//				,"LM957==(LM043+LM049)-LM254-LM258"));
	}
	
	private static double getValue(Mod2002016 mod, Mod2002016Key key) {
		if (mod.getDraftMap().containsKey(key)){
			DoubleVariable2016 dv = mod.getDraftMap().get(key);
			if (dv != null) {
				Object o = dv.getValue();
				if (o != null && o instanceof Double) {
					return (Double) o;
				}
			}
		}
		return mod.getDoubleValue(key);
	}
	private static boolean isPositive(Mod2002016 mod, Mod2002016Key key) {
		return AonMathUtils.isGreatherThanZero( getValue(mod,key) );
	}
	private static boolean isNegative(Mod2002016 mod, Mod2002016Key key) {
		return AonMathUtils.isLessThanZero( getValue(mod,key) );
	}
	private static boolean isZero(Mod2002016 mod, Mod2002016Key key) {
		return AonMathUtils.isZero( getValue(mod,key) );
	}
	private static boolean isNotZero(Mod2002016 mod, Mod2002016Key key) {
		return !AonMathUtils.isZero( getValue(mod,key) );
	}
	private static boolean isEqual(Mod2002016 mod, Mod2002016Key key1, Mod2002016Key key2) {
		return AonMathUtils.equals( getValue(mod,key1), getValue(mod,key2) );
	}
	private static boolean isNotEqual(Mod2002016 mod, Mod2002016Key key1, Mod2002016Key key2) {
		return !isEqual(mod, key1, key2);
	}
	private static boolean isGreatherThan(Mod2002016 mod, Mod2002016Key key1, Mod2002016Key key2) {
		return getValue(mod,key1) > getValue(mod,key2);
	}	
	private static boolean isLessThan(Mod2002016 mod, Mod2002016Key key1, Mod2002016Key key2) {
		return getValue(mod,key1) < getValue(mod,key2);
	}	
	private static boolean isBalanceNormal(Mod2002016 mod) {
		return (mod.getBalanceType() == BalanceType.NORMAL); 
	}
	private static boolean isBalancePymes(Mod2002016 mod) {
		return (mod.getBalanceType() == BalanceType.PYMES); 
	}
	private static boolean isNotBalancePymes(Mod2002016 mod) {
		return !isBalancePymes(mod);		 
	}
	private static boolean isCooperativa(Mod2002016 mod) {
		return mod.isChecked(C0017) || mod.isChecked(C0018) || mod.isChecked(C0019);		 
	}
	private static boolean isGrupo(Mod2002016 mod) {
		return mod.isChecked(C0009) || mod.isChecked(C0010);		 
	}
	private static boolean isECPNFilled(Mod2002016 mod) {
		return isNotZero(mod, TC645) && isNotZero(mod, T0355);
	}	
	private static String checkSignMsg( Mod2002016Key key) {
		String desc = key.getDescription();
		return MessageFormat.format(CHECK_SIGN_MSG, key.getCode(), AonStringUtils.isBlank(desc)?"": ("- \"" + desc + "\""));
	}
	private static String mustPositiveMsg( Mod2002016Key key) {
		String desc = key.getDescription();
		return MessageFormat.format(MUST_POSITIVE_MSG, key.getCode(), AonStringUtils.isBlank(desc)?"": ("- \"" + desc + "\""));
	}
	
	
	private static String mustEqualMsg( Mod2002016Key key1, Mod2002016Key key2 ) {
		return format(MUST_EQUAL_MSG,key1,key2); 
	}
	private static String mustGreatherMsg( Mod2002016Key key1, Mod2002016Key key2 ) {
		return format(MUST_GREATHER_MSG,key1,key2); 
	}
	private static String mustLessMsg( Mod2002016Key key1, Mod2002016Key key2 ) {
		return format(MUST_LESS_MSG,key1,key2); 
	}
	private static String incompatibleCharacterMsg( Mod2002016Key key1,Mod2002016Key key2) {
		return format(INV_BOX_MSG,key1,key2); 
	}
	private static String format(String pattern, Mod2002016Key key1,Mod2002016Key key2) {
		String desc1 = key1.getDescription();
		String desc2 = key2.getDescription();  
		return MessageFormat.format(pattern,key1.getCode()
			,AonStringUtils.isBlank(desc1)?"": ("- \"" + desc1 + "\"")
			,key2.getCode()
			,AonStringUtils.isBlank(desc2)?"": ("\"" + desc2 + "\"")
			); 
	}
}
