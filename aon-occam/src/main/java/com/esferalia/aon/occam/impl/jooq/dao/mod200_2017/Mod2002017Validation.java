package com.esferalia.aon.occam.impl.jooq.dao.mod200_2017;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key.*;

import java.text.MessageFormat;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.DoubleVariable2017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.ValidationMessage2017;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002017Validation {
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
	private static final int PAGE11 = 11;
	private static final int PAGE12 = 12;
	private static final int PAGE14 = 14;
	private static final int PAGE15 = 15;
	
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
		boolean validate(Mod2002017 mod);
	}
	private static enum Type {
		 DOC   ( mod -> !AonDocumentUtil.isValid(mod.getEnterpriseDocument()),new ValidationMessage2017(PAGE00,"NIF de la declaraci\u00F3n incorrecto."))
		,CNAE_1( mod -> AonStringUtils.isEmpty(mod.getCnae()),new ValidationMessage2017(PAGE00,"Rellene el CNAE de la empresa."))
		,CNAE_2( mod -> !AonStringUtils.isEmpty(mod.getCnae()) && CNAE2009.valueOfCode(mod.getCnae()) == null,new ValidationMessage2017(PAGE00,"CNAE de la empresa, no v\u00E1lido."))
		,COMP_1( mod -> mod.isComplementary() && AonStringUtils.isBlank(mod.getComplementaryReceipt() )
			,new ValidationMessage2017(PAGE00,"Si marca Decl. Complementaria, debe indicar un n. de justificante anterior."))
		,COMP_2( mod -> mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt()) && mod.getComplementaryReceipt().length() != 13 
			,new ValidationMessage2017(PAGE00,"El n. de justificante anterior debe tener 13 caracteres."))
		,COMP_3( mod -> mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt()) 
				  && (!mod.getComplementaryReceipt().startsWith("200") && !mod.getComplementaryReceipt().startsWith("206"))
			,new ValidationMessage2017(PAGE00,"El n. de justificante anterior debe empezar por 200 o 206."))
		,COMP_4( mod -> !mod.isComplementary() && AonStringUtils.isNotBlank(mod.getComplementaryReceipt())
			, new ValidationMessage2017(PAGE00,"Si no marca Decl. Complementaria, no debe indicar un n. de justificante anterior."))
		,SECR_1( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) && mod.getSecretary() == null
			, new ValidationMessage2017(PAGE01,"Para personas jur\u00EDdicas, debe rellenar los datos del secretario"))
		,SECR_2( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& !AonDocumentUtil.isValid(mod.getSecretary().getDocument())
			, new ValidationMessage2017(PAGE01,"NIF del secretario incorrecto."))
		,SECR_3( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& AonStringUtils.isBlank(mod.getSecretary().getName())
			, new ValidationMessage2017(PAGE01,"Falta nombre del secretario."))
		,SECR_4( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null && AonStringUtils.isNotBlank(mod.getSecretary().getName()) 
					&& mod.getSecretary().getName().length() > 25
			, new ValidationMessage2017(PAGE01,"Longitud excedida en el nombre del secretario. Debe limitarse a 25 caracteres."))
		,SECR_5( mod -> AonDocumentUtil.isEntity(mod.getEnterpriseDocument()) 
					&& mod.getSecretary() != null 
					&& mod.getSecretary().getIrnr() == null 
					&& (mod.isChecked(C0021) || mod.isChecked(C0046))  
			, new ValidationMessage2017(PAGE01,"Falta fecha IRNR."))
		
		// ------------------------------------------------------------------------
		// -------------------------- BALANCE: ACTIVO ----------------------------- 		
		// ------------------------------------------------------------------------
		,V_BA180_1( mod -> isNotEqual(mod,BA180,BP252),new ValidationMessage2017(PAGE03,BA180,mustEqualMsg( BA180, BP252 )))
		,V_BA180_2( mod -> isZero(mod,BA180) && isZero(mod,BP252) && isZero(mod,BP187)
			 ,new ValidationMessage2017(PAGE03,BA180,"Advertencia: Los totales de los balances son cero (Activo, patrimonio neto y pasivo)." ))
		
		// ------------------------------------------------------------------------
		// --------------- BALANCE: PATRIMONIO NETO Y PASIVO ----------------------
		// ------------------------------------------------------------------------
		,V_BP189_1( mod -> isPositive(mod,BP189)		,new ValidationMessage2017(PAGE04,BP189,checkSignMsg(BP189)))
		,V_BP194_1( mod -> isPositive(mod,BP194)		,new ValidationMessage2017(PAGE04,BP194,checkSignMsg(BP194)))
		,V_BP197  ( mod -> isPositive(mod,BP197)		,new ValidationMessage2017(PAGE04,BP197,checkSignMsg(BP197)))
		,V_BP199_1( mod -> isNotEqual(mod,BP199,LQ500)	,new ValidationMessage2017(PAGE04,BP199,mustEqualMsg(BP199, LQ500)))  
		,V_BP200_1( mod -> isPositive(mod,BP200)		,new ValidationMessage2017(PAGE04,BP200,checkSignMsg(BP200)))
		
		// ------------------------------------------------------------------------
		// --------- ECPN. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO ----------
		// ------------------------------------------------------------------------
		,V_TC632  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP188,TC632)
			, new ValidationMessage2017(PAGE07, TC632,mustEqualMsg(TC632,BP188))) 
		,V_TC633  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP189,TC633)
			, new ValidationMessage2017(PAGE07, TC633,mustEqualMsg(TC633,BP189)))
		,V_TC634  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP190,TC634)
			, new ValidationMessage2017(PAGE07, TC634,mustEqualMsg(TC634,BP190)))
		,V_TC635  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP191,TC635)
			, new ValidationMessage2017(PAGE07, TC635,mustEqualMsg(TC635,BP191)))
		,V_TC636  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP194,TC636)
			, new ValidationMessage2017(PAGE07, TC637,mustEqualMsg(TC636,BP194)))
		,V_TC637  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP195,TC637)
			, new ValidationMessage2017(PAGE07, TC637,mustEqualMsg(TC637,BP195)))
		,V_TC639  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP199,TC639)
			, new ValidationMessage2017(PAGE07, TC639,mustEqualMsg(TC639,BP199)))
		,V_TC640  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP200,TC640)
			, new ValidationMessage2017(PAGE07, TC640,mustEqualMsg(TC640,BP200)))
		,V_TC641  ( mod -> isECPNFilled(mod) && isNotBalancePymes(mod) && isNotEqual(mod,BP201,TC641)
			, new ValidationMessage2017(PAGE07, TC642,mustEqualMsg(TC641,BP201))) 
		,V_TC642  ( mod -> isECPNFilled(mod) && isNotBalancePymes(mod) && isNotEqual(mod,BP202,TC642)
			, new ValidationMessage2017(PAGE07, TC642,mustEqualMsg(TC642,BP202))) 
		,V_TC638  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP198,TC638)
			, new ValidationMessage2017(PAGE07, TC638,mustEqualMsg(TC638,BP198)))
		,V_TC643  ( mod -> isECPNFilled(mod) && isBalancePymes(mod) && isNotEqual(mod,BP208,LQ643)
			, new ValidationMessage2017(PAGE07, TC643,mustEqualMsg(TC643,BP208))) 
		,V_TC644  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP209,TC644)
			, new ValidationMessage2017(PAGE07, TC644,mustEqualMsg(TC644,BP209)))
		,V_TC645  ( mod -> isECPNFilled(mod) && isNotEqual(mod,BP185,TC645)
			, new ValidationMessage2017(PAGE07, TC645,mustEqualMsg(TC645,BP185)))
		
		,V_TC534  ( mod -> isPositive(mod,TC534),new ValidationMessage2017(PAGE07,TC534, checkSignMsg(TC534)))
		,V_TC535  ( mod -> isPositive(mod,TC535),new ValidationMessage2017(PAGE07,TC535, checkSignMsg(TC535)))
		,V_TC536  ( mod -> isPositive(mod,TC536),new ValidationMessage2017(PAGE07,TC536, checkSignMsg(TC536)))
		,V_TC537  ( mod -> isPositive(mod,TC537),new ValidationMessage2017(PAGE07,TC537, checkSignMsg(TC537)))
		,V_TC538  ( mod -> isPositive(mod,TC538),new ValidationMessage2017(PAGE07,TC538, checkSignMsg(TC538)))
		,V_TC539  ( mod -> isPositive(mod,TC539),new ValidationMessage2017(PAGE07,TC539, checkSignMsg(TC539)))
		,V_TC540  ( mod -> isPositive(mod,TC540),new ValidationMessage2017(PAGE07,TC540, checkSignMsg(TC540)))
		,V_TC541  ( mod -> isPositive(mod,TC541),new ValidationMessage2017(PAGE07,TC541, checkSignMsg(TC541)))
		,V_TC542  ( mod -> isPositive(mod,TC542),new ValidationMessage2017(PAGE07,TC542, checkSignMsg(TC542)))
		,V_TC543  ( mod -> isPositive(mod,TC543),new ValidationMessage2017(PAGE07,TC543, checkSignMsg(TC543)))
		,V_TC544  ( mod -> isPositive(mod,TC544),new ValidationMessage2017(PAGE07,TC544, checkSignMsg(TC544)))
		,V_TC545  ( mod -> isPositive(mod,TC545),new ValidationMessage2017(PAGE07,TC545, checkSignMsg(TC545)))
		,V_TC546  ( mod -> isPositive(mod,TC546),new ValidationMessage2017(PAGE07,TC546, checkSignMsg(TC546)))
		,V_TC562  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC562),new ValidationMessage2017(PAGE07,TC562, checkSignMsg(TC562))) 
		,V_TC563  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC563),new ValidationMessage2017(PAGE07,TC563, checkSignMsg(TC563)))
		,V_TC564  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC564),new ValidationMessage2017(PAGE07,TC564, checkSignMsg(TC564)))
		,V_TC565  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC565),new ValidationMessage2017(PAGE07,TC565, checkSignMsg(TC565)))
		,V_TC566  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC566),new ValidationMessage2017(PAGE07,TC566, checkSignMsg(TC566)))
		,V_TC567  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC567),new ValidationMessage2017(PAGE07,TC567, checkSignMsg(TC567)))
		,V_TC568  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC568),new ValidationMessage2017(PAGE07,TC568, checkSignMsg(TC568)))
		,V_TC569  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC569),new ValidationMessage2017(PAGE07,TC569, checkSignMsg(TC569)))
		,V_TC570  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC570),new ValidationMessage2017(PAGE07,TC570, checkSignMsg(TC570)))
		,V_TC571  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC571),new ValidationMessage2017(PAGE07,TC571, checkSignMsg(TC571)))
		,V_TC572  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC572),new ValidationMessage2017(PAGE07,TC572, checkSignMsg(TC572)))
		,V_TC574  ( mod -> isBalanceNormal(mod) && isPositive(mod,TC574),new ValidationMessage2017(PAGE07,TC574, checkSignMsg(TC574)))
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (I) ----------------------------
		// ------------------------------------------------------------------------
		,V_LQ301  ( mod -> isPositive(mod,PG258) && isNotZero(mod,LQ301),
				new ValidationMessage2017(PAGE08,LQ301, "La clave [00258] es positiva a la vez que la clave [00301] no es igual a cero."))
		,V_LQ301B ( mod -> isNegative(mod,PG258) && isNotZero(mod,LQ302),
				new ValidationMessage2017(PAGE08,LQ302, "La clave [00258] es negativa a la vez que la clave [00302] no es igual a cero."))
		,V_LQ302  ( mod -> isPositive(mod,PG258) && isNotEqual(mod,LQ302,PG258),
				new ValidationMessage2017(PAGE08,LQ302, "La clave [00258] es positiva a la vez que la clave [00302] no es igual a la clave [00258]."))
		
		,V_LQ1230 ( mod -> isNegative(mod,LQ1230),new ValidationMessage2017(PAGE08,LQ1230, mustPositiveMsg(LQ1230)))
		,V_LQ1231 ( mod -> isNegative(mod,LQ1231),new ValidationMessage2017(PAGE08,LQ1231, mustPositiveMsg(LQ1231)))
		
		,V_I0355  ( mod -> isNegative(mod,I0355),new ValidationMessage2017(PAGE08,I0355, mustPositiveMsg(I0355)))
		,V_I0357  ( mod -> isNegative(mod,I0357),new ValidationMessage2017(PAGE08,I0357, mustPositiveMsg(I0357)))
		,V_I0359  ( mod -> isNegative(mod,I0359),new ValidationMessage2017(PAGE08,I0359, mustPositiveMsg(I0359)))
		,V_I0225  ( mod -> isNegative(mod,I0225),new ValidationMessage2017(PAGE08,I0225, mustPositiveMsg(I0225)))
		,V_I1514  ( mod -> isNegative(mod,I1514),new ValidationMessage2017(PAGE08,I1514, mustPositiveMsg(I1514)))
		,V_I0361  ( mod -> isNegative(mod,I0361),new ValidationMessage2017(PAGE08,I0361, mustPositiveMsg(I0361)))
		,V_I0303  ( mod -> isNegative(mod,I0303),new ValidationMessage2017(PAGE08,I0303, mustPositiveMsg(I0303)))
		,V_I1005  ( mod -> isNegative(mod,I1005),new ValidationMessage2017(PAGE08,I1005, mustPositiveMsg(I1005)))
		,V_I0305  ( mod -> isNegative(mod,I0305),new ValidationMessage2017(PAGE08,I0305, mustPositiveMsg(I0305)))
		,V_I0307  ( mod -> isNegative(mod,I0307),new ValidationMessage2017(PAGE08,I0307, mustPositiveMsg(I0307)))
		,V_I1003  ( mod -> isNegative(mod,I1003),new ValidationMessage2017(PAGE08,I1003, mustPositiveMsg(I1003)))
		,V_I0309  ( mod -> isNegative(mod,I0309),new ValidationMessage2017(PAGE08,I0309, mustPositiveMsg(I0309)))
		,V_I0514  ( mod -> isNegative(mod,I0514),new ValidationMessage2017(PAGE08,I0514, mustPositiveMsg(I0514)))
		,V_I0516  ( mod -> isNegative(mod,I0516),new ValidationMessage2017(PAGE08,I0516, mustPositiveMsg(I0516)))
		,V_I0321  ( mod -> isNegative(mod,I0321),new ValidationMessage2017(PAGE08,I0321, mustPositiveMsg(I0321)))
		,V_I0415  ( mod -> isNegative(mod,I0415),new ValidationMessage2017(PAGE08,I0415, mustPositiveMsg(I0415)))
		,V_I0331  ( mod -> isNegative(mod,I0331),new ValidationMessage2017(PAGE08,I0331, mustPositiveMsg(I0331)))
		,V_I0325  ( mod -> isNegative(mod,I0325),new ValidationMessage2017(PAGE08,I0325, mustPositiveMsg(I0325)))
		,V_I1518  ( mod -> isNegative(mod,I1518),new ValidationMessage2017(PAGE08,I1518, mustPositiveMsg(I1518)))
		,V_I0333  ( mod -> isNegative(mod,I0333),new ValidationMessage2017(PAGE08,I0333, mustPositiveMsg(I0333)))
		,V_I0327  ( mod -> isNegative(mod,I0327),new ValidationMessage2017(PAGE08,I0327, mustPositiveMsg(I0327)))
		,V_I0416  ( mod -> isNegative(mod,I0416),new ValidationMessage2017(PAGE08,I0416, mustPositiveMsg(I0416)))
		,V_I0335  ( mod -> isNegative(mod,I0335),new ValidationMessage2017(PAGE08,I0335, mustPositiveMsg(I0335)))
		,V_I0337  ( mod -> isNegative(mod,I0337),new ValidationMessage2017(PAGE08,I0337, mustPositiveMsg(I0337)))
		,V_I1002  ( mod -> isNegative(mod,I1002),new ValidationMessage2017(PAGE08,I1002, mustPositiveMsg(I1002)))
		,V_I0339  ( mod -> isNegative(mod,I0339),new ValidationMessage2017(PAGE08,I0339, mustPositiveMsg(I0339)))
		,V_I0341  ( mod -> isNegative(mod,I0341),new ValidationMessage2017(PAGE08,I0341, mustPositiveMsg(I0341)))
		,V_I0508  ( mod -> isNegative(mod,I0508),new ValidationMessage2017(PAGE08,I0508, mustPositiveMsg(I0508)))
		,V_I1009  ( mod -> isNegative(mod,I1009),new ValidationMessage2017(PAGE08,I1009, mustPositiveMsg(I1009)))
		,V_I1807  ( mod -> isNegative(mod,I1807),new ValidationMessage2017(PAGE08,I1807, mustPositiveMsg(I1807)))
		,V_I1808  ( mod -> isNegative(mod,I1808),new ValidationMessage2017(PAGE08,I1808, mustPositiveMsg(I1808)))	
		,V_I0343  ( mod -> isNegative(mod,I0343),new ValidationMessage2017(PAGE08,I0343, mustPositiveMsg(I0343)))
		,V_I0363  ( mod -> isNegative(mod,I0363),new ValidationMessage2017(PAGE08,I0363, mustPositiveMsg(I0363)))
		,V_I0345  ( mod -> isNegative(mod,I0345),new ValidationMessage2017(PAGE08,I0345, mustPositiveMsg(I0345)))
		,V_I0371  ( mod -> isNegative(mod,I0371),new ValidationMessage2017(PAGE08,I0371, mustPositiveMsg(I0371)))
		,V_I0347  ( mod -> isNegative(mod,I0347),new ValidationMessage2017(PAGE08,I0347, mustPositiveMsg(I0347)))
		,V_I1011  ( mod -> isNegative(mod,I1011),new ValidationMessage2017(PAGE08,I1011, mustPositiveMsg(I1011)))
		,V_I1013  ( mod -> isNegative(mod,I1013),new ValidationMessage2017(PAGE08,I1013, mustPositiveMsg(I1013)))
		,V_I1015  ( mod -> isNegative(mod,I1015),new ValidationMessage2017(PAGE08,I1015, mustPositiveMsg(I1015)))
		,V_I0369  ( mod -> isNegative(mod,I0369),new ValidationMessage2017(PAGE08,I0369, mustPositiveMsg(I0369)))
		,V_I0256  ( mod -> isNegative(mod,I0256),new ValidationMessage2017(PAGE08,I0256, mustPositiveMsg(I0256)))
		,V_I0373  ( mod -> isNegative(mod,I0373),new ValidationMessage2017(PAGE08,I0373, mustPositiveMsg(I0373)))
		,V_I0340  ( mod -> isNegative(mod,I0340),new ValidationMessage2017(PAGE08,I0340, mustPositiveMsg(I0340)))
		,V_I0351  ( mod -> isNegative(mod,I0351),new ValidationMessage2017(PAGE08,I0351, mustPositiveMsg(I0351)))
		,V_I0375  ( mod -> isNegative(mod,I0375),new ValidationMessage2017(PAGE08,I0375, mustPositiveMsg(I0375)))
		,V_I1320  ( mod -> isNegative(mod,I1320),new ValidationMessage2017(PAGE08,I1320, mustPositiveMsg(I1320)))
		,V_I0184  ( mod -> isNegative(mod,I0184),new ValidationMessage2017(PAGE08,I0184, mustPositiveMsg(I0184)))
		,V_I1022  ( mod -> isNegative(mod,I1022),new ValidationMessage2017(PAGE08,I1022, mustPositiveMsg(I1022)))
		,V_I1018  ( mod -> isNegative(mod,I1018),new ValidationMessage2017(PAGE08,I1018, mustPositiveMsg(I1018)))
		,V_I1275  ( mod -> isNegative(mod,I1275),new ValidationMessage2017(PAGE08,I1275, mustPositiveMsg(I1275)))
		,V_I0377  ( mod -> isNegative(mod,I0377),new ValidationMessage2017(PAGE08,I0377, mustPositiveMsg(I0377)))
		,V_I0379  ( mod -> isNegative(mod,I0379),new ValidationMessage2017(PAGE08,I0379, mustPositiveMsg(I0379)))
		,V_I0381  ( mod -> isNegative(mod,I0381),new ValidationMessage2017(PAGE08,I0381, mustPositiveMsg(I0381)))
		,V_I0383  ( mod -> isNegative(mod,I0383),new ValidationMessage2017(PAGE08,I0383, mustPositiveMsg(I0383)))
		,V_I0387  ( mod -> isNegative(mod,I0387),new ValidationMessage2017(PAGE08,I0387, mustPositiveMsg(I0387)))
		,V_I0311  ( mod -> isNegative(mod,I0311),new ValidationMessage2017(PAGE08,I0311, mustPositiveMsg(I0311)))
		,V_I0313  ( mod -> isNegative(mod,I0313),new ValidationMessage2017(PAGE08,I0313, mustPositiveMsg(I0313)))
		,V_I0323  ( mod -> isNegative(mod,I0323),new ValidationMessage2017(PAGE08,I0323, mustPositiveMsg(I0323)))
		,V_I0317  ( mod -> isNegative(mod,I0317),new ValidationMessage2017(PAGE08,I0317, mustPositiveMsg(I0317)))
		,V_I0385  ( mod -> isNegative(mod,I0385),new ValidationMessage2017(PAGE08,I0385, mustPositiveMsg(I0385)))
		,V_I0389  ( mod -> isNegative(mod,I0389),new ValidationMessage2017(PAGE08,I0389, mustPositiveMsg(I0389)))
		,V_I0397  ( mod -> isNegative(mod,I0397),new ValidationMessage2017(PAGE08,I0397, mustPositiveMsg(I0397)))
		,V_I0250  ( mod -> isNegative(mod,I0250),new ValidationMessage2017(PAGE08,I0250, mustPositiveMsg(I0250)))
		,V_I0391  ( mod -> isNegative(mod,I0391),new ValidationMessage2017(PAGE08,I0391, mustPositiveMsg(I0391)))
		,V_I0403  ( mod -> isNegative(mod,I0403),new ValidationMessage2017(PAGE08,I0403, mustPositiveMsg(I0403)))
		,V_I0518  ( mod -> isNegative(mod,I0518),new ValidationMessage2017(PAGE08,I0518, mustPositiveMsg(I0518)))
		,V_I0510  ( mod -> isNegative(mod,I0510),new ValidationMessage2017(PAGE08,I0510, mustPositiveMsg(I0510)))
		,V_I0329  ( mod -> isNegative(mod,I0329),new ValidationMessage2017(PAGE08,I0329, mustPositiveMsg(I0329)))
		,V_I0365  ( mod -> isNegative(mod,I0365),new ValidationMessage2017(PAGE08,I0365, mustPositiveMsg(I0365)))
		,V_I0409  ( mod -> isNegative(mod,I0409),new ValidationMessage2017(PAGE08,I0409, mustPositiveMsg(I0409)))
		,V_I0411  ( mod -> isNegative(mod,I0411),new ValidationMessage2017(PAGE08,I0411, mustPositiveMsg(I0411)))
		,V_I1027  ( mod -> isNegative(mod,I1027),new ValidationMessage2017(PAGE08,I1027, mustPositiveMsg(I1027)))
		,V_I0413  ( mod -> isNegative(mod,I0413),new ValidationMessage2017(PAGE08,I0413, mustPositiveMsg(I0413)))
		,V_I0417  ( mod -> isNegative(mod,I0417),new ValidationMessage2017(PAGE08,I0417, mustPositiveMsg(I0417)))		
		,V_D0356  ( mod -> isNegative(mod,D0356),new ValidationMessage2017(PAGE08,D0356, mustPositiveMsg(D0356)))
		,V_D0358  ( mod -> isNegative(mod,D0358),new ValidationMessage2017(PAGE08,D0358, mustPositiveMsg(D0358)))
		,V_D0360  ( mod -> isNegative(mod,D0360),new ValidationMessage2017(PAGE08,D0360, mustPositiveMsg(D0360)))
		,V_D0226  ( mod -> isNegative(mod,D0226),new ValidationMessage2017(PAGE08,D0226, mustPositiveMsg(D0226)))
		,V_D0272  ( mod -> isNegative(mod,D0272),new ValidationMessage2017(PAGE08,D0272, mustPositiveMsg(D0272)))
		,V_D0362  ( mod -> isNegative(mod,D0362),new ValidationMessage2017(PAGE08,D0362, mustPositiveMsg(D0362)))
		,V_D0304  ( mod -> isNegative(mod,D0304),new ValidationMessage2017(PAGE08,D0304, mustPositiveMsg(D0304)))
		,V_D0505  ( mod -> isNegative(mod,D0505),new ValidationMessage2017(PAGE08,D0505, mustPositiveMsg(D0505)))
		,V_D1006  ( mod -> isNegative(mod,D1006),new ValidationMessage2017(PAGE08,D1006, mustPositiveMsg(D1006)))
		,V_D0306  ( mod -> isNegative(mod,D0306),new ValidationMessage2017(PAGE08,D0306, mustPositiveMsg(D0306)))
		,V_D0308  ( mod -> isNegative(mod,D0308),new ValidationMessage2017(PAGE08,D0308, mustPositiveMsg(D0308)))
		,V_D1004  ( mod -> isNegative(mod,D1004),new ValidationMessage2017(PAGE08,D1004, mustPositiveMsg(D1004)))
		,V_D0310  ( mod -> isNegative(mod,D0310),new ValidationMessage2017(PAGE08,D0310, mustPositiveMsg(D0310)))
		,V_D0509  ( mod -> isNegative(mod,D0509),new ValidationMessage2017(PAGE08,D0509, mustPositiveMsg(D0509)))
		,V_D0551  ( mod -> isNegative(mod,D0551),new ValidationMessage2017(PAGE08,D0551, mustPositiveMsg(D0551)))
		,V_D0322  ( mod -> isNegative(mod,D0322),new ValidationMessage2017(PAGE08,D0322, mustPositiveMsg(D0322)))
		,V_D0211  ( mod -> isNegative(mod,D0211),new ValidationMessage2017(PAGE08,D0211, mustPositiveMsg(D0211)))
		,V_D0332  ( mod -> isNegative(mod,D0332),new ValidationMessage2017(PAGE08,D0332, mustPositiveMsg(D0332)))
		,V_D0326  ( mod -> isNegative(mod,D0326),new ValidationMessage2017(PAGE08,D0326, mustPositiveMsg(D0326)))
		,V_D0394  ( mod -> isNegative(mod,D0394),new ValidationMessage2017(PAGE08,D0394, mustPositiveMsg(D0394)))
		,V_D0334  ( mod -> isNegative(mod,D0334),new ValidationMessage2017(PAGE08,D0334, mustPositiveMsg(D0334)))
		,V_D0328  ( mod -> isNegative(mod,D0328),new ValidationMessage2017(PAGE08,D0328, mustPositiveMsg(D0328)))
		,V_D0543  ( mod -> isNegative(mod,D0543),new ValidationMessage2017(PAGE08,D0543, mustPositiveMsg(D0543)))
		,V_D0336  ( mod -> isNegative(mod,D0336),new ValidationMessage2017(PAGE08,D0336, mustPositiveMsg(D0336)))
		,V_D0338  ( mod -> isNegative(mod,D0338),new ValidationMessage2017(PAGE08,D0338, mustPositiveMsg(D0338)))
		,V_D0368  ( mod -> isNegative(mod,D0368),new ValidationMessage2017(PAGE08,D0368, mustPositiveMsg(D0368)))
		,V_D0342  ( mod -> isNegative(mod,D0342),new ValidationMessage2017(PAGE08,D0342, mustPositiveMsg(D0342)))
		,V_D1010  ( mod -> isNegative(mod,D1010),new ValidationMessage2017(PAGE08,D1010, mustPositiveMsg(D1010)))
		,V_D0364  ( mod -> isNegative(mod,D0364),new ValidationMessage2017(PAGE08,D0364, mustPositiveMsg(D0364)))
		,V_D0346  ( mod -> isNegative(mod,D0346),new ValidationMessage2017(PAGE08,D0346, mustPositiveMsg(D0346)))
		,V_D0348  ( mod -> isNegative(mod,D0348),new ValidationMessage2017(PAGE08,D0348, mustPositiveMsg(D0348)))
		,V_D1012  ( mod -> isNegative(mod,D1012),new ValidationMessage2017(PAGE08,D1012, mustPositiveMsg(D1012)))
		,V_D1014  ( mod -> isNegative(mod,D1014),new ValidationMessage2017(PAGE08,D1014, mustPositiveMsg(D1014)))
		,V_D1016  ( mod -> isNegative(mod,D1016),new ValidationMessage2017(PAGE08,D1016, mustPositiveMsg(D1016)))
		,V_D0370  ( mod -> isNegative(mod,D0370),new ValidationMessage2017(PAGE08,D0370, mustPositiveMsg(D0370)))
		,V_D0278  ( mod -> isNegative(mod,D0278),new ValidationMessage2017(PAGE08,D0278, mustPositiveMsg(D0278)))
		,V_D0372  ( mod -> isNegative(mod,D0372),new ValidationMessage2017(PAGE08,D0372, mustPositiveMsg(D0372)))
		,V_D0374  ( mod -> isNegative(mod,D0374),new ValidationMessage2017(PAGE08,D0374, mustPositiveMsg(D0374)))
		,V_D1589  ( mod -> isNegative(mod,D1589),new ValidationMessage2017(PAGE08,D1589, mustPositiveMsg(D1589)))
		,V_D0376  ( mod -> isNegative(mod,D0376),new ValidationMessage2017(PAGE08,D0376, mustPositiveMsg(D0376)))
		,V_D1321  ( mod -> isNegative(mod,D1321),new ValidationMessage2017(PAGE08,D1321, mustPositiveMsg(D1321)))
		,V_D0544  ( mod -> isNegative(mod,D0544),new ValidationMessage2017(PAGE08,D0544, mustPositiveMsg(D0544)))
		,V_D1023  ( mod -> isNegative(mod,D1023),new ValidationMessage2017(PAGE08,D1023, mustPositiveMsg(D1023)))
		,V_D1019  ( mod -> isNegative(mod,D1019),new ValidationMessage2017(PAGE08,D1019, mustPositiveMsg(D1019)))
		,V_D1276  ( mod -> isNegative(mod,D1276),new ValidationMessage2017(PAGE08,D1276, mustPositiveMsg(D1276)))
		,V_D0378  ( mod -> isNegative(mod,D0378),new ValidationMessage2017(PAGE08,D0378, mustPositiveMsg(D0378)))
		,V_D0380  ( mod -> isNegative(mod,D0380),new ValidationMessage2017(PAGE08,D0380, mustPositiveMsg(D0380)))
		,V_D0382  ( mod -> isNegative(mod,D0382),new ValidationMessage2017(PAGE08,D0382, mustPositiveMsg(D0382)))
		,V_D0384  ( mod -> isNegative(mod,D0384),new ValidationMessage2017(PAGE08,D0384, mustPositiveMsg(D0384)))
		,V_D0388  ( mod -> isNegative(mod,D0388),new ValidationMessage2017(PAGE08,D0388, mustPositiveMsg(D0388)))
		,V_D0312  ( mod -> isNegative(mod,D0312),new ValidationMessage2017(PAGE08,D0312, mustPositiveMsg(D0312)))
		,V_D0314  ( mod -> isNegative(mod,D0314),new ValidationMessage2017(PAGE08,D0314, mustPositiveMsg(D0314)))
		,V_D0324  ( mod -> isNegative(mod,D0324),new ValidationMessage2017(PAGE08,D0324, mustPositiveMsg(D0324)))
		,V_D0318  ( mod -> isNegative(mod,D0318),new ValidationMessage2017(PAGE08,D0318, mustPositiveMsg(D0318)))
		,V_D0386  ( mod -> isNegative(mod,D0386),new ValidationMessage2017(PAGE08,D0386, mustPositiveMsg(D0386)))
		,V_D0390  ( mod -> isNegative(mod,D0390),new ValidationMessage2017(PAGE08,D0390, mustPositiveMsg(D0390)))
		,V_D0396  ( mod -> isNegative(mod,D0396),new ValidationMessage2017(PAGE08,D0396, mustPositiveMsg(D0396)))
		,V_D0398  ( mod -> isNegative(mod,D0398),new ValidationMessage2017(PAGE08,D0398, mustPositiveMsg(D0398)))
		,V_D0251  ( mod -> isNegative(mod,D0251),new ValidationMessage2017(PAGE08,D0251, mustPositiveMsg(D0251)))
		,V_D0392  ( mod -> isNegative(mod,D0392),new ValidationMessage2017(PAGE08,D0392, mustPositiveMsg(D0392)))
		,V_D0400  ( mod -> isNegative(mod,D0400),new ValidationMessage2017(PAGE08,D0400, mustPositiveMsg(D0400)))
		,V_D0404  ( mod -> isNegative(mod,D0404),new ValidationMessage2017(PAGE08,D0404, mustPositiveMsg(D0404)))
		,V_D0519  ( mod -> isNegative(mod,D0519),new ValidationMessage2017(PAGE08,D0519, mustPositiveMsg(D0519)))
		,V_D0512  ( mod -> isNegative(mod,D0512),new ValidationMessage2017(PAGE08,D0512, mustPositiveMsg(D0512)))
		,V_D0330  ( mod -> isNegative(mod,D0330),new ValidationMessage2017(PAGE08,D0330, mustPositiveMsg(D0330)))
		,V_D1026  ( mod -> isNegative(mod,D1026),new ValidationMessage2017(PAGE08,D1026, mustPositiveMsg(D1026)))
		,V_D0410  ( mod -> isNegative(mod,D0410),new ValidationMessage2017(PAGE08,D0410, mustPositiveMsg(D0410)))
		,V_D0412  ( mod -> isNegative(mod,D0412),new ValidationMessage2017(PAGE08,D0412, mustPositiveMsg(D0412)))
		,V_D1028  ( mod -> isNegative(mod,D1028),new ValidationMessage2017(PAGE08,D1028, mustPositiveMsg(D1028)))
		,V_D0414  ( mod -> isNegative(mod,D0414),new ValidationMessage2017(PAGE08,D0414, mustPositiveMsg(D0414)))
		,V_D0418  ( mod -> isNegative(mod,D0418),new ValidationMessage2017(PAGE08,D0418, mustPositiveMsg(D0418)))

		,V_I0415_1  ( mod -> isCooperativa(mod) && isNotZero(mod, I0415),new ValidationMessage2017(PAGE08,I0415, COOP_MSG))
		,V_D0211_1  ( mod -> isCooperativa(mod) && isNotZero(mod, D0211),new ValidationMessage2017(PAGE08,D0211, COOP_MSG))
		,V_I0416_1  ( mod -> isCooperativa(mod) && isNotZero(mod, I0416),new ValidationMessage2017(PAGE08,I0416, COOP_MSG))
		,V_D0543_1  ( mod -> isCooperativa(mod) && isNotZero(mod, D0543),new ValidationMessage2017(PAGE08,D0543, COOP_MSG))
		
		,V_I0415_2  ( mod -> isGrupo(mod) && isNotZero(mod, I0415),new ValidationMessage2017(PAGE08,I0415, GRP_MSG))
		,V_D0211_2  ( mod -> isGrupo(mod) && isNotZero(mod, D0211),new ValidationMessage2017(PAGE08,D0211, GRP_MSG))
		,V_I0416_2  ( mod -> isGrupo(mod) && isNotZero(mod, I0416),new ValidationMessage2017(PAGE08,I0416, GRP_MSG))
		,V_D0543_2  ( mod -> isGrupo(mod) && isNotZero(mod, D0543),new ValidationMessage2017(PAGE08,D0543, GRP_MSG))
		
		,V_D0370_2  ( mod -> isGreatherThan(mod,D0370,P1503),new ValidationMessage2017(PAGE08,D0370, 
				  "Confirme el importe total consignado en las claves de ingresos por dividendos declarados en la p\u00E1gina 3 [01503]"
				+ " y el importe declarado en las correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias de la clave [00370]"))
		
		,V_I0391_3  ( mod -> mod.isNotChecked(C0001) && isNotZero(mod,I0391), new ValidationMessage2017(PAGE08,I0391,incompatibleCharacterMsg(I0391,C0001))) 
		,V_D0392_3  ( mod -> mod.isNotChecked(C0001) && isNotZero(mod,D0392), new ValidationMessage2017(PAGE08,D0392,incompatibleCharacterMsg(D0392,C0001)))
		,V_I0389_3  ( mod -> mod.isNotChecked(C0002) && isNotZero(mod,I0389), new ValidationMessage2017(PAGE08,I0389,incompatibleCharacterMsg(I0389,C0002)))
		,V_D0390_3  ( mod -> mod.isNotChecked(C0002) && isNotZero(mod,D0390), new ValidationMessage2017(PAGE08,D0390,incompatibleCharacterMsg(D0390,C0002)))
		,V_I0371_3  ( mod -> mod.isNotChecked(C0003) && isNotZero(mod,I0371), new ValidationMessage2017(PAGE08,I0371,incompatibleCharacterMsg(I0371,C0003)))
		,V_I0311_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0311), new ValidationMessage2017(PAGE08,I0311,incompatibleCharacterMsg(I0311,C0006)))
		,V_D0312_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0312), new ValidationMessage2017(PAGE08,D0312,incompatibleCharacterMsg(D0312,C0006)))
		,V_I0313_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0313), new ValidationMessage2017(PAGE08,I0313,incompatibleCharacterMsg(I0313,C0006)))
		,V_D0314_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0314), new ValidationMessage2017(PAGE08,D0314,incompatibleCharacterMsg(D0314,C0006)))
		,V_I0323_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,I0323), new ValidationMessage2017(PAGE08,I0323,incompatibleCharacterMsg(I0323,C0006)))
		,V_D0324_3  ( mod -> mod.isNotChecked(C0006) && isNotZero(mod,D0324), new ValidationMessage2017(PAGE08,D0324,incompatibleCharacterMsg(D0324,C0006)))
		,V_I0387_3  ( mod -> mod.isNotChecked(C0007) && isNotZero(mod,I0387), new ValidationMessage2017(PAGE08,I0387,incompatibleCharacterMsg(I0387,C0007)))
		,V_D0388_3  ( mod -> mod.isNotChecked(C0007) && isNotZero(mod,D0388), new ValidationMessage2017(PAGE08,D0388,incompatibleCharacterMsg(D0388,C0007)))
		,V_D0396_3  ( mod -> mod.isNotChecked(C0005) && isNotZero(mod,D0396), new ValidationMessage2017(PAGE08,D0396,incompatibleCharacterMsg(D0396,C0005)))
		,V_I0385_3  ( mod -> mod.isNotChecked(C0011) && isNotZero(mod,I0385), new ValidationMessage2017(PAGE08,I0385,incompatibleCharacterMsg(I0385,C0011)))
		,V_D0386_3  ( mod -> mod.isNotChecked(C0011) && isNotZero(mod,D0386), new ValidationMessage2017(PAGE08,D0386,incompatibleCharacterMsg(D0386,C0011)))
		,V_I0309_3  ( mod -> mod.isNotChecked(C0020) && isNotZero(mod,I0309), new ValidationMessage2017(PAGE08,I0309,incompatibleCharacterMsg(I0309,C0020)))
		,V_D0310_3  ( mod -> mod.isNotChecked(C0020) && isNotZero(mod,D0310), new ValidationMessage2017(PAGE08,D0310,incompatibleCharacterMsg(D0310,C0020)))
		,V_I0397_3  ( mod -> mod.isNotChecked(C0022) && isNotZero(mod,I0397), new ValidationMessage2017(PAGE08,I0397,incompatibleCharacterMsg(I0397,C0022)))
		,V_D0398_3  ( mod -> mod.isNotChecked(C0022) && isNotZero(mod,D0398), new ValidationMessage2017(PAGE08,D0398,incompatibleCharacterMsg(D0398,C0022)))
		,V_I0403_3  ( mod -> mod.isNotChecked(C0029) && isNotZero(mod,I0403), new ValidationMessage2017(PAGE08,I0403,incompatibleCharacterMsg(I0403,C0029)))
		,V_D0404_3  ( mod -> mod.isNotChecked(C0029) && isNotZero(mod,D0404), new ValidationMessage2017(PAGE08,D0404,incompatibleCharacterMsg(D0404,C0029)))
		,V_I0383_3  ( mod -> mod.isNotChecked(C0034) && isNotZero(mod,I0383), new ValidationMessage2017(PAGE08,I0383,incompatibleCharacterMsg(I0383,C0034)))
		,V_D0384_3  ( mod -> mod.isNotChecked(C0034) && isNotZero(mod,D0384), new ValidationMessage2017(PAGE08,D0384,incompatibleCharacterMsg(D0384,C0034)))
		,V_I0409_3  ( mod -> mod.isNotChecked(C0046) && isNotZero(mod,I0409), new ValidationMessage2017(PAGE08,I0409,incompatibleCharacterMsg(I0409,C0046)))
		,V_D0410_3  ( mod -> mod.isNotChecked(C0046) && isNotZero(mod,D0410), new ValidationMessage2017(PAGE08,D0410,incompatibleCharacterMsg(D0410,C0046)))
		,V_I0411_3  ( mod -> mod.isNotChecked(C0047) && isNotZero(mod,I0411), new ValidationMessage2017(PAGE08,I0411,incompatibleCharacterMsg(I0411,C0047)))
		,V_D0412_3  ( mod -> mod.isNotChecked(C0047) && isNotZero(mod,D0412), new ValidationMessage2017(PAGE08,D0412,incompatibleCharacterMsg(D0412,C0047)))

		,V_D0404_4  ( mod -> isNotZero(mod,D0404) && (getValue(mod,D0404) >= (getValue(mod,LQ650) * 0.90)) , new ValidationMessage2017(PAGE08,D0404,"Revise importe disminuciones RIC"))	

		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (II) ----------------------------
		// ------------------------------------------------------------------------
		
		// -- Entidades navieras en regimen  de tributación en función del tonelaje
		,V_LQ631_1  ( mod -> isLessThan(mod,LQ631,LQ632), new ValidationMessage2017(PAGE09,LQ631,mustGreatherMsg(LQ631,LQ632)))

		// -- Reserva de capitalizacion
		,V_LQ1140_1  ( mod -> isLessThan(mod,BP1001,LQ1140), new ValidationMessage2017(PAGE09,LQ1140,mustGreatherMsg(BP1001,LQ1140)))
		
		// -- Compensación de bases imponibles negativas de períodos anteriores
		,V_LQ640_1  ( mod -> isGreatherThan(mod,  LQ641  ,LQ640  ), new ValidationMessage2017(PAGE09,LQ641  ,mustLessMsg(LQ641  ,LQ640 )))    
		,V_LQ643_1  ( mod -> isGreatherThan(mod,  LQ644  ,LQ643  ), new ValidationMessage2017(PAGE09,LQ644  ,mustLessMsg(LQ644  ,LQ643 )))
		,V_LQ646_1  ( mod -> isGreatherThan(mod,  LQ647  ,LQ646  ), new ValidationMessage2017(PAGE09,LQ647  ,mustLessMsg(LQ647  ,LQ646 )))
		,V_LQ649_1  ( mod -> isGreatherThan(mod,  LQ650  ,LQ649  ), new ValidationMessage2017(PAGE09,LQ650  ,mustLessMsg(LQ650  ,LQ649 )))
		,V_LQ652_1  ( mod -> isGreatherThan(mod,  LQ653  ,LQ652  ), new ValidationMessage2017(PAGE09,LQ653  ,mustLessMsg(LQ653  ,LQ652 )))
		,V_LQ655_1  ( mod -> isGreatherThan(mod,  LQ656  ,LQ655  ), new ValidationMessage2017(PAGE09,LQ656  ,mustLessMsg(LQ656  ,LQ655 )))
		,V_LQ658_1  ( mod -> isGreatherThan(mod,  LQ659  ,LQ658  ), new ValidationMessage2017(PAGE09,LQ659  ,mustLessMsg(LQ659  ,LQ658 )))
		,V_LQ661_1  ( mod -> isGreatherThan(mod,  LQ662  ,LQ661  ), new ValidationMessage2017(PAGE09,LQ662  ,mustLessMsg(LQ662  ,LQ661 )))
		,V_LQ664_1  ( mod -> isGreatherThan(mod,  LQ665  ,LQ664  ), new ValidationMessage2017(PAGE09,LQ665  ,mustLessMsg(LQ665  ,LQ664 )))
		,V_LQ667_1  ( mod -> isGreatherThan(mod,  LQ668  ,LQ667  ), new ValidationMessage2017(PAGE09,LQ668  ,mustLessMsg(LQ668  ,LQ667 )))
		,V_LQ743_1  ( mod -> isGreatherThan(mod,  LQ747  ,LQ743  ), new ValidationMessage2017(PAGE09,LQ747  ,mustLessMsg(LQ747  ,LQ743 )))
		,V_LQ275_1  ( mod -> isGreatherThan(mod,  LQ276  ,LQ275  ), new ValidationMessage2017(PAGE09,LQ276  ,mustLessMsg(LQ276  ,LQ275 )))
		,V_LQ608_1  ( mod -> isGreatherThan(mod,  LQ609  ,LQ608  ), new ValidationMessage2017(PAGE09,LQ609  ,mustLessMsg(LQ609  ,LQ608 )))
		,V_LQ704_1  ( mod -> isGreatherThan(mod,  LQ705  ,LQ704  ), new ValidationMessage2017(PAGE09,LQ705  ,mustLessMsg(LQ705  ,LQ704 )))
		,V_LQ013_1  ( mod -> isGreatherThan(mod,  LQ014  ,LQ013  ), new ValidationMessage2017(PAGE09,LQ014  ,mustLessMsg(LQ014  ,LQ013 )))
		,V_LQ725_1  ( mod -> isGreatherThan(mod,  LQ726  ,LQ725  ), new ValidationMessage2017(PAGE09,LQ726  ,mustLessMsg(LQ726  ,LQ725 )))
		,V_LQ534_1  ( mod -> isGreatherThan(mod,  LQ535  ,LQ534  ), new ValidationMessage2017(PAGE09,LQ535  ,mustLessMsg(LQ535  ,LQ534 )))
		,V_LQ607_1  ( mod -> isGreatherThan(mod,  LQ675  ,LQ607  ), new ValidationMessage2017(PAGE09,LQ675  ,mustLessMsg(LQ675  ,LQ607 )))
		,V_LQ1045_1 ( mod -> isGreatherThan(mod,  LQ1046 ,LQ1045 ), new ValidationMessage2017(PAGE09,LQ1046 ,mustLessMsg(LQ1046 ,LQ1045)))
		,V_LQ1519_1 ( mod -> isGreatherThan(mod,  LQ1520 ,LQ1519 ), new ValidationMessage2017(PAGE09,LQ1520 ,mustLessMsg(LQ1520 ,LQ1519)))
		,V_LQ670_1  ( mod -> isGreatherThan(mod,  LQ547  ,LQ670  ), new ValidationMessage2017(PAGE09,LQ547  ,mustLessMsg(LQ547  ,LQ670 )))
		
		// -- Sólo sociedades cooperativas
		,V_LQ553_1  ( mod -> isCooperativa(mod)  && !AonMathUtils.equals(getValue(mod,LQ552),(getValue(mod,LQ553)+getValue(mod,LQ554))), 
				new ValidationMessage2017(PAGE09,LQ553,"La suma de las casillas \""+LQ553.getDescription()+"\" y \""+LQ554.getDescription()
														+"\" debe ser igual que \""+LQ552.getDescription()+"\""))

		// Compensación de cuotas por pérdidas de cooperativas
		,V_LQ674_1  ( mod -> isGreatherThan(mod,LQ674 , LQ673 ), new ValidationMessage2017(PAGE09,LQ674  ,mustLessMsg(LQ674 ,LQ673)))
		,V_LQ677_1  ( mod -> isGreatherThan(mod,LQ677 , LQ676 ), new ValidationMessage2017(PAGE09,LQ677  ,mustLessMsg(LQ677 ,LQ676)))
		,V_LQ680_1  ( mod -> isGreatherThan(mod,LQ680 , LQ679 ), new ValidationMessage2017(PAGE09,LQ680  ,mustLessMsg(LQ680 ,LQ679)))
		,V_LQ683_1  ( mod -> isGreatherThan(mod,LQ683 , LQ682 ), new ValidationMessage2017(PAGE09,LQ683  ,mustLessMsg(LQ683 ,LQ682)))
		,V_LQ686_1  ( mod -> isGreatherThan(mod,LQ686 , LQ685 ), new ValidationMessage2017(PAGE09,LQ686  ,mustLessMsg(LQ686 ,LQ685)))
		,V_LQ689_1  ( mod -> isGreatherThan(mod,LQ689 , LQ688 ), new ValidationMessage2017(PAGE09,LQ689  ,mustLessMsg(LQ689 ,LQ688)))
		,V_LQ692_1  ( mod -> isGreatherThan(mod,LQ692 , LQ691 ), new ValidationMessage2017(PAGE09,LQ692  ,mustLessMsg(LQ692 ,LQ691)))
		,V_LQ624_1  ( mod -> isGreatherThan(mod,LQ624 , LQ623 ), new ValidationMessage2017(PAGE09,LQ624  ,mustLessMsg(LQ624 ,LQ623)))
		,V_LQ280_1  ( mod -> isGreatherThan(mod,LQ280 , LQ279 ), new ValidationMessage2017(PAGE09,LQ280  ,mustLessMsg(LQ280 ,LQ279)))
		,V_LQ515_1  ( mod -> isGreatherThan(mod,LQ515 , LQ587 ), new ValidationMessage2017(PAGE09,LQ515  ,mustLessMsg(LQ515 ,LQ587)))
		,V_LQ099_1  ( mod -> isGreatherThan(mod,LQ099 , LQ059 ), new ValidationMessage2017(PAGE09,LQ099  ,mustLessMsg(LQ099 ,LQ059)))
		,V_LQ018_1  ( mod -> isGreatherThan(mod,LQ018 , LQ017 ), new ValidationMessage2017(PAGE09,LQ018  ,mustLessMsg(LQ018 ,LQ017)))
		,V_LQ773_1  ( mod -> isGreatherThan(mod,LQ773 , LQ772 ), new ValidationMessage2017(PAGE09,LQ773  ,mustLessMsg(LQ773 ,LQ772)))
		,V_LQ908_1  ( mod -> isGreatherThan(mod,LQ908 , LQ907 ), new ValidationMessage2017(PAGE09,LQ908  ,mustLessMsg(LQ908 ,LQ907)))
		,V_LQ911_1  ( mod -> isGreatherThan(mod,LQ911 , LQ910 ), new ValidationMessage2017(PAGE09,LQ911  ,mustLessMsg(LQ911 ,LQ910)))
		,V_LQ936_1  ( mod -> isGreatherThan(mod,LQ936 , LQ935 ), new ValidationMessage2017(PAGE09,LQ936  ,mustLessMsg(LQ936 ,LQ935)))
		,V_LQ1512_1 ( mod -> isGreatherThan(mod,LQ1512, LQ1511), new ValidationMessage2017(PAGE09,LQ1512 ,mustLessMsg(LQ1512,LQ1511)))
		
		
		
		// -- Rentas que no limitan la compensación de bases imponibles y cuotas negativas
		,V_LQ545  ( mod -> isNegative(mod,LQ545) ,new ValidationMessage2017(PAGE09,LQ545 , mustPositiveMsg(LQ545)))
		,V_LQ593  ( mod -> isNegative(mod,LQ593) ,new ValidationMessage2017(PAGE08,LQ593 , mustPositiveMsg(LQ593)))
		,V_LQ1509 ( mod -> isNegative(mod,LQ1509),new ValidationMessage2017(PAGE08,LQ1509, mustPositiveMsg(LQ1509)))
		,V_LQ1510 ( mod -> isNegative(mod,LQ1510),new ValidationMessage2017(PAGE08,LQ1510, mustPositiveMsg(LQ1510)))
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (III) ----------------------------
		// ------------------------------------------------------------------------
		,V_BN567_1( mod -> isNegative(mod,BN567) ,new ValidationMessage2017(PAGE10,BN567  , mustPositiveMsg(BN567 )))
		,V_BN568  ( mod -> isNegative(mod,BN568) ,new ValidationMessage2017(PAGE10,BN568  , mustPositiveMsg(BN568 )))
		,V_BN563  ( mod -> isNegative(mod,BN563) ,new ValidationMessage2017(PAGE10,BN563  , mustPositiveMsg(BN563 )))
		,V_BN566  ( mod -> isNegative(mod,BN566) ,new ValidationMessage2017(PAGE10,BN566  , mustPositiveMsg(BN566 )))
		,V_BN576  ( mod -> isNegative(mod,BN576) ,new ValidationMessage2017(PAGE10,BN576  , mustPositiveMsg(BN576 )))
		,V_BN569  ( mod -> isNegative(mod,BN569) ,new ValidationMessage2017(PAGE10,BN569  , mustPositiveMsg(BN569 )))
		,V_BN570  ( mod -> isNegative(mod,BN570) ,new ValidationMessage2017(PAGE10,BN570  , mustPositiveMsg(BN570 )))
	 	,V_BN1344 ( mod -> isNegative(mod,BN1344),new ValidationMessage2017(PAGE10,BN1344 , mustPositiveMsg(BN1344)))
	 	,V_BN1280 ( mod -> isNegative(mod,BN1280),new ValidationMessage2017(PAGE10,BN1280 , mustPositiveMsg(BN1280)))
		,V_BN572  ( mod -> isNegative(mod,BN572) ,new ValidationMessage2017(PAGE10,BN572  , mustPositiveMsg(BN572 )))
		,V_BN571  ( mod -> isNegative(mod,BN571) ,new ValidationMessage2017(PAGE10,BN571  , mustPositiveMsg(BN571 )))
		,V_BN573  ( mod -> isNegative(mod,BN573) ,new ValidationMessage2017(PAGE10,BN573  , mustPositiveMsg(BN573 )))
		,V_BN575  ( mod -> isNegative(mod,BN575) ,new ValidationMessage2017(PAGE10,BN575  , mustPositiveMsg(BN575 )))
		,V_BN577  ( mod -> isNegative(mod,BN577) ,new ValidationMessage2017(PAGE10,BN577  , mustPositiveMsg(BN577 )))
		,V_BN581  ( mod -> isNegative(mod,BN581) ,new ValidationMessage2017(PAGE10,BN581  , mustPositiveMsg(BN581 )))
		,V_BN582_1( mod -> isNegative(mod,BN582) ,new ValidationMessage2017(PAGE10,BN582  , mustPositiveMsg(BN582 )))
		
		,V_BN582_2( mod -> getValue(mod,BN582) > (getValue(mod,LQ562)+getValue(mod,LQ1038))
			,new ValidationMessage2017(PAGE10,BN582,"La suma de las casillas \""+LQ562.getDescription()+"\" y \""+LQ1038.getDescription()
														+"\" debe ser mayor o igual que \""+BN582.getDescription()+"\""))
		
		,V_BN567_2( mod -> getValue(mod,BN567) > (getValue(mod,LQ562) * 50 /100)
			,new ValidationMessage2017(PAGE10,BN567  , "La clave [00567] no puede superar el 50% de la clave [00562]"))
		
		,V_BN563_2( mod -> mod.isNotChecked(C0029) && isNotZero(mod,BN563)
			,new ValidationMessage2017(PAGE10,BN563  , "La clave [00563] s\u00F3lo puede tener valor con el caracter [00029] marcado"))

		,V_BN566_2( mod -> isNotZero(mod,BN566) && (mod.isNotChecked(C0017) &&  mod.isNotChecked(C0018)),new ValidationMessage2017(PAGE10,BN566  , "La clave [00566] s\u00F3lo puede tener valor con el caracter [00017] \u00F3 [00018] marcado"))
		,V_BN576_2( mod -> mod.isNotChecked(C0038) && isNotZero(mod,BN576),new ValidationMessage2017(PAGE10,BN576  , "La clave [00576] s\u00F3lo puede tener valor con el caracter [00038] marcado"))
		,V_BN575_1( mod -> mod.isNotChecked(C0007) && isNotZero(mod,BN575),new ValidationMessage2017(PAGE10,BN575  , "La clave [00575] s\u00F3lo puede tener valor con el caracter [00007] marcado"))
		,V_BN581_1( mod -> mod.isNotChecked(C0015) && isNotZero(mod,BN581),new ValidationMessage2017(PAGE10,BN581  , "La clave [BN581] s\u00F3lo puede tener valor con el caracter [00015] marcado"))
		
		,V_BN581_2( mod -> (isNotZero(mod,BN581) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE10,BN581 ,"No puede aplicarse la clave 581 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		
		// -- DI interna de períodos anteriores aplicada en el ejercicio (art. 30 RDL 4/2004)
		,V_BN847_1  ( mod -> isGreatherThan(mod,  BN847,BN846), new ValidationMessage2017(PAGE10,BN847,mustLessMsg(BN847,BN846)))
		,V_BN283_1  ( mod -> isGreatherThan(mod,  BN283,BN282), new ValidationMessage2017(PAGE10,BN283,mustLessMsg(BN283,BN282)))
		,V_BN703_1  ( mod -> isGreatherThan(mod,  BN703,BN702), new ValidationMessage2017(PAGE10,BN703,mustLessMsg(BN703,BN702)))
		,V_BN187_1  ( mod -> isGreatherThan(mod,  BN187,BN071), new ValidationMessage2017(PAGE10,BN187,mustLessMsg(BN187,BN071)))
		,V_BN026_1  ( mod -> isGreatherThan(mod,  BN026,BN025), new ValidationMessage2017(PAGE10,BN026,mustLessMsg(BN026,BN025)))
		,V_BN715_1  ( mod -> isGreatherThan(mod,  BN715,BN714), new ValidationMessage2017(PAGE10,BN715,mustLessMsg(BN715,BN714)))
		,V_BN737_1  ( mod -> isGreatherThan(mod,  BN737,BN736), new ValidationMessage2017(PAGE10,BN737,mustLessMsg(BN737,BN736)))
		
		// -- DI interna de periodos anteriores aplicada en el ejercicio (DT 23.1 LIS)
		,V_BN120_1  ( mod -> isGreatherThan(mod,  BN120,BN119), new ValidationMessage2017(PAGE10,BN120,mustLessMsg(BN119,BN120)))
		,V_BN125_1  ( mod -> isGreatherThan(mod,  BN125,BN124), new ValidationMessage2017(PAGE10,BN125,mustLessMsg(BN124,BN125)))
		,V_BN1598_1 ( mod -> isGreatherThan(mod,  BN1598,BN1597), new ValidationMessage2017(PAGE10,BN1598,mustLessMsg(BN1597,BN1598)))
		
		// -- DI internacional de períodos anteriores aplicada en el ejercicio (art. 31 y 32 RDL 4/2004)
		,V_BN638_1  ( mod -> isGreatherThan(mod,  BN638,BN637), new ValidationMessage2017(PAGE10,BN638,mustLessMsg(BN638,BN637)))
		,V_BN894_1  ( mod -> isGreatherThan(mod,  BN894,BN849), new ValidationMessage2017(PAGE10,BN894,mustLessMsg(BN894,BN849)))
		,V_BN286_1  ( mod -> isGreatherThan(mod,  BN286,BN285), new ValidationMessage2017(PAGE10,BN286,mustLessMsg(BN286,BN285)))
		,V_BN826_1  ( mod -> isGreatherThan(mod,  BN826,BN825), new ValidationMessage2017(PAGE10,BN826,mustLessMsg(BN826,BN825)))
		,V_BN002_1  ( mod -> isGreatherThan(mod,  BN002,BN001), new ValidationMessage2017(PAGE10,BN002,mustLessMsg(BN002,BN001)))
		,V_BN029_1  ( mod -> isGreatherThan(mod,  BN029,BN028), new ValidationMessage2017(PAGE10,BN029,mustLessMsg(BN029,BN028)))
		,V_BN718_1  ( mod -> isGreatherThan(mod,  BN718,BN717), new ValidationMessage2017(PAGE10,BN718,mustLessMsg(BN718,BN717)))
		,V_BN723_1  ( mod -> isGreatherThan(mod,  BN723,BN722), new ValidationMessage2017(PAGE10,BN723,mustLessMsg(BN723,BN722)))
		,V_BN741_1  ( mod -> isGreatherThan(mod,  BN741,BN740), new ValidationMessage2017(PAGE10,BN741,mustLessMsg(BN741,BN740)))
		,V_BN136_1  ( mod -> isGreatherThan(mod,  BN136,BN135), new ValidationMessage2017(PAGE10,BN136,mustLessMsg(BN136,BN135)))

		// -- DI internacional de períodos anteriores aplicada en el ejercicio (art. 31 y 32 LIS)
		,V_BN1052_1  ( mod -> isGreatherThan(mod,  BN1052,BN1051), new ValidationMessage2017(PAGE10,BN1052 ,mustLessMsg(BN1052,BN1051)))
		,V_BN1351_1  ( mod -> isGreatherThan(mod,  BN1351,BN1350), new ValidationMessage2017(PAGE10,BN1351 ,mustLessMsg(BN1351,BN1350)))
		,V_BN1773_1  ( mod -> isGreatherThan(mod,  BN1773,BN1772), new ValidationMessage2017(PAGE10,BN1773 ,mustLessMsg(BN1773,BN1772)))

		// -- DI internacional generada y aplicada en el ejercicio actual (arts. 31 y 32 LIS)
		,V_BN165_1  ( mod -> isGreatherThan(mod,  BN165,BN163), new ValidationMessage2017(PAGE10,BN165,mustLessMsg(BN165, BN163)))
		,V_BN169_1  ( mod -> isGreatherThan(mod,  BN169,BN167), new ValidationMessage2017(PAGE10,BN169,mustLessMsg(BN169, BN167)))
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (IV) ---------------------------
		// ------------------------------------------------------------------------
		
		,V_BN583_2( mod -> (isNotZero(mod,BN583) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN583 ,"No puede aplicarse la clave 583 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN585_2( mod -> (isNotZero(mod,BN585) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN585 ,"No puede aplicarse la clave 585 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN584_2( mod -> (isNotZero(mod,BN584) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN584 ,"No puede aplicarse la clave 584 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN588_2( mod -> (isNotZero(mod,BN588) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN588 ,"No puede aplicarse la clave 588 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN590_2( mod -> (isNotZero(mod,BN590) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN590 ,"No puede aplicarse la clave 590 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN082_2( mod -> (isNotZero(mod,BN082) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN082 ,"No puede aplicarse la clave 082 mientras existan saldos pendientes de aplicaci\u00F3n de deducciones por doble imposici\u00F3n."))
		,V_BN565_2( mod -> (isNotZero(mod,BN565) && (isNotZero(mod,BN118 ) || isNotZero(mod,BN162 ) || isNotZero(mod,BN133 ) || isNotZero(mod,BN1345) || isNotZero(mod,BN1347) || isNotZero(mod,BN174 )))
			,new ValidationMessage2017(PAGE11,BN565,"No puede aplicarse la clave 565 mientras existan saldos pendientes "
					+ "de aplicaci\u00F3n de deducciones por doble imposici\u00F3n o de deducciones "
					+ "del cap\u00EDtulo IV T\u00EDtulo VI de la Ley del Impuesto."))

			// -- Deducción DT 24ª.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95
		,V_BN585_1  ( mod -> isGreatherThan(mod,  BN585,BN582), new ValidationMessage2017(PAGE11,BN585,mustLessMsg(BN585,BN582)))
		
		,V_BN836_1  ( mod -> isGreatherThan(mod,  BN836  ,BN835  ), new ValidationMessage2017(PAGE11,BN836  ,mustLessMsg(BN836 , BN835  )))
		,V_BN839_1  ( mod -> isGreatherThan(mod,  BN839  ,BN838  ), new ValidationMessage2017(PAGE11,BN839  ,mustLessMsg(BN839 , BN838  )))
		,V_BN933_1  ( mod -> isGreatherThan(mod,  BN933  ,BN932  ), new ValidationMessage2017(PAGE11,BN933  ,mustLessMsg(BN933 , BN932  )))
		,V_BN298_1  ( mod -> isGreatherThan(mod,  BN298  ,BN297  ), new ValidationMessage2017(PAGE11,BN298  ,mustLessMsg(BN298 , BN297  )))
		,V_BN091_1  ( mod -> isGreatherThan(mod,  BN091  ,BN090  ), new ValidationMessage2017(PAGE11,BN091  ,mustLessMsg(BN091 , BN090  )))
		,V_BN005_1  ( mod -> isGreatherThan(mod,  BN005  ,BN004  ), new ValidationMessage2017(PAGE11,BN005  ,mustLessMsg(BN005 , BN004  )))
		,V_BN032_1  ( mod -> isGreatherThan(mod,  BN032  ,BN031  ), new ValidationMessage2017(PAGE11,BN032  ,mustLessMsg(BN032 , BN031  )))
		,V_BN023_1  ( mod -> isGreatherThan(mod,  BN023  ,BN022  ), new ValidationMessage2017(PAGE11,BN023  ,mustLessMsg(BN023 , BN022  )))
		,V_BN041_1  ( mod -> isGreatherThan(mod,  BN041  ,BN040  ), new ValidationMessage2017(PAGE11,BN041  ,mustLessMsg(BN041 , BN040  )))
		,V_BN139_1  ( mod -> isGreatherThan(mod,  BN139  ,BN138  ), new ValidationMessage2017(PAGE11,BN139  ,mustLessMsg(BN139 , BN138  )))
		,V_BN142_1  ( mod -> isGreatherThan(mod,  BN142  ,BN141  ), new ValidationMessage2017(PAGE11,BN142  ,mustLessMsg(BN142 , BN141  )))
		,V_BN189_1  ( mod -> isGreatherThan(mod,  BN189  ,BN188  ), new ValidationMessage2017(PAGE11,BN189  ,mustLessMsg(BN189 , BN188  )))
		,V_BN804_1  ( mod -> isGreatherThan(mod,  BN804  ,BN803  ), new ValidationMessage2017(PAGE11,BN804  ,mustLessMsg(BN804 , BN803  )))
		,V_BN1056_1 ( mod -> isGreatherThan(mod,  BN1056 ,BN1055 ), new ValidationMessage2017(PAGE11,BN1056 ,mustLessMsg(BN1056, BN1055 )))
		,V_BN708_1  ( mod -> isGreatherThan(mod,  BN708  ,BN700  ), new ValidationMessage2017(PAGE11,BN708  ,mustLessMsg(BN708 , BN700  )))
		,V_BN1354_1 ( mod -> isGreatherThan(mod,  BN1354 ,BN1353 ), new ValidationMessage2017(PAGE11,BN1354 ,mustLessMsg(BN1354, BN1353 )))
		,V_BN1776_1 ( mod -> isGreatherThan(mod,  BN1776 ,BN1775 ), new ValidationMessage2017(PAGE11,BN1776 ,mustLessMsg(BN1776, BN1775 )))
		
		// -- Deducciones DT 24ª.1 LIS y DT 8ª RDL 4/2004
		,V_BN750 ( mod -> isGreatherThan(mod,  BN750 ,BN749 ), new ValidationMessage2017(PAGE11,BN750 ,mustLessMsg(BN750, BN749)))
		,V_BN753 ( mod -> isGreatherThan(mod,  BN753 ,BN752 ), new ValidationMessage2017(PAGE11,BN753 ,mustLessMsg(BN753, BN752)))
		,V_BN756 ( mod -> isGreatherThan(mod,  BN756 ,BN755 ), new ValidationMessage2017(PAGE11,BN756 ,mustLessMsg(BN756, BN755)))
		,V_BN759 ( mod -> isGreatherThan(mod,  BN759 ,BN758 ), new ValidationMessage2017(PAGE11,BN759 ,mustLessMsg(BN759, BN758)))
		,V_BN762 ( mod -> isGreatherThan(mod,  BN762 ,BN761 ), new ValidationMessage2017(PAGE11,BN762 ,mustLessMsg(BN762, BN761)))
		,V_BN745 ( mod -> isGreatherThan(mod,  BN745 ,BN744 ), new ValidationMessage2017(PAGE11,BN745 ,mustLessMsg(BN745, BN744)))
		,V_BN783 ( mod -> isGreatherThan(mod,  BN783 ,BN779 ), new ValidationMessage2017(PAGE11,BN783 ,mustLessMsg(BN783, BN779)))

		// Deducciones con límite del Capítulo IV Título VI RDL 4/2004 y LIS
		,V_BN775_1  ( mod -> isGreatherThan(mod,  BN775,BN774), new ValidationMessage2017(PAGE11,BN775,mustLessMsg(BN775, BN774)))
		,V_BN781_1  ( mod -> isGreatherThan(mod,  BN781,BN780), new ValidationMessage2017(PAGE11,BN781,mustLessMsg(BN781, BN780)))
		,V_BN787_1  ( mod -> isGreatherThan(mod,  BN787,BN786), new ValidationMessage2017(PAGE11,BN787,mustLessMsg(BN787, BN786)))
		,V_BN767_1  ( mod -> isGreatherThan(mod,  BN767,BN766), new ValidationMessage2017(PAGE11,BN767,mustLessMsg(BN767, BN766)))
		,V_BN896_1  ( mod -> isGreatherThan(mod,  BN896,BN198), new ValidationMessage2017(PAGE11,BN896,mustLessMsg(BN896, BN198)))
		,V_BN289_1  ( mod -> isGreatherThan(mod,  BN289,BN288), new ValidationMessage2017(PAGE11,BN289,mustLessMsg(BN289, BN288)))
		,V_BN467_1  ( mod -> isGreatherThan(mod,  BN467,BN466), new ValidationMessage2017(PAGE11,BN467,mustLessMsg(BN467, BN466)))
		,V_BN498_1  ( mod -> isGreatherThan(mod,  BN498,BN061), new ValidationMessage2017(PAGE11,BN498,mustLessMsg(BN498, BN061)))
		,V_BN473_1  ( mod -> isGreatherThan(mod,  BN473,BN472), new ValidationMessage2017(PAGE11,BN473,mustLessMsg(BN473, BN472)))
		,V_BN181_1  ( mod -> isGreatherThan(mod,  BN181,BN180), new ValidationMessage2017(PAGE11,BN181,mustLessMsg(BN181, BN180)))
		,V_BN532_1  ( mod -> isGreatherThan(mod,  BN532,BN531), new ValidationMessage2017(PAGE11,BN532,mustLessMsg(BN532, BN531)))
		,V_BN946_1  ( mod -> isGreatherThan(mod,  BN946,BN945), new ValidationMessage2017(PAGE11,BN946,mustLessMsg(BN946, BN945)))
		,V_BN961_1  ( mod -> isGreatherThan(mod,  BN961,BN960), new ValidationMessage2017(PAGE11,BN961,mustLessMsg(BN961, BN960)))
		,V_BN185_1  ( mod -> isGreatherThan(mod,  BN185,BN183), new ValidationMessage2017(PAGE11,BN185,mustLessMsg(BN185, BN183)))
		,V_BN967_1  ( mod -> isGreatherThan(mod,  BN967,BN966), new ValidationMessage2017(PAGE11,BN967,mustLessMsg(BN967, BN966)))
		,V_BN458_1  ( mod -> isGreatherThan(mod,  BN458,BN457), new ValidationMessage2017(PAGE11,BN458,mustLessMsg(BN458, BN457)))
		,V_BN461_1  ( mod -> isGreatherThan(mod,  BN461,BN460), new ValidationMessage2017(PAGE11,BN461,mustLessMsg(BN461, BN460)))
		,V_BN1064_1 ( mod -> isGreatherThan(mod,  BN1064,BN1063), new ValidationMessage2017(PAGE11,BN1064,mustLessMsg(BN1064, BN1063)))
		,V_BN1067_1 ( mod -> isGreatherThan(mod,  BN1067,BN1066), new ValidationMessage2017(PAGE11,BN1067,mustLessMsg(BN1067, BN1066)))
		,V_BN1070_1 ( mod -> isGreatherThan(mod,  BN1070,BN1069), new ValidationMessage2017(PAGE11,BN1070,mustLessMsg(BN1070, BN1069)))
		,V_BN814_1  ( mod -> isGreatherThan(mod,  BN814,BN813), new ValidationMessage2017(PAGE11,BN814,mustLessMsg(BN814, BN813)))
		,V_BN810_1  ( mod -> isGreatherThan(mod,  BN810,BN986), new ValidationMessage2017(PAGE11,BN810,mustLessMsg(BN810, BN986)))
		,V_BN591_1  ( mod -> isGreatherThan(mod,  BN591,BN557), new ValidationMessage2017(PAGE11,BN591,mustLessMsg(BN591, BN557)))
		,V_BN1615_1 ( mod -> isGreatherThan(mod,  BN1615,BN1614), new ValidationMessage2017(PAGE11,BN1615,mustLessMsg(BN1615, BN1614)))
		,V_BN1618_1 ( mod -> isGreatherThan(mod,  BN1618,BN1617), new ValidationMessage2017(PAGE11,BN1618,mustLessMsg(BN1618, BN1617)))
		,V_BN1621_1 ( mod -> isGreatherThan(mod,  BN1621,BN1620), new ValidationMessage2017(PAGE11,BN1621,mustLessMsg(BN1621, BN1620)))
		,V_BN1361_1 ( mod -> isGreatherThan(mod,  BN1361,BN1360), new ValidationMessage2017(PAGE11,BN1361,mustLessMsg(BN1361, BN1360)))
		,V_BN1364_1 ( mod -> isGreatherThan(mod,  BN1364,BN1363), new ValidationMessage2017(PAGE11,BN1364,mustLessMsg(BN1364, BN1363)))
		,V_BN1367_1 ( mod -> isGreatherThan(mod,  BN1367,BN1366), new ValidationMessage2017(PAGE11,BN1367,mustLessMsg(BN1367, BN1366)))
		,V_BN799_1  ( mod -> isGreatherThan(mod,  BN799,BN798), new ValidationMessage2017(PAGE11,BN799,mustLessMsg(BN799, BN798)))
		,V_BN698_1  ( mod -> isGreatherThan(mod,  BN698,BN096), new ValidationMessage2017(PAGE11,BN698,mustLessMsg(BN698, BN096)))
		,V_BN808_1  ( mod -> isGreatherThan(mod,  BN808,BN807), new ValidationMessage2017(PAGE11,BN808,mustLessMsg(BN808, BN807)))
		,V_BN1076_1 ( mod -> isGreatherThan(mod,  BN1076,BN1075), new ValidationMessage2017(PAGE11,BN1076,mustLessMsg(BN1076, BN1075)))
		,V_BN964_1  ( mod -> isGreatherThan(mod,  BN964,BN963), new ValidationMessage2017(PAGE11,BN964,mustLessMsg(BN964, BN963)))
		,V_BN502_1  ( mod -> isGreatherThan(mod,  BN502,BN931), new ValidationMessage2017(PAGE11,BN502,mustLessMsg(BN502, BN931)))
		,V_BN796_1  ( mod -> isGreatherThan(mod,  BN796,BN795), new ValidationMessage2017(PAGE11,BN796,mustLessMsg(BN796, BN795)))
		,V_BN888_1  ( mod -> isGreatherThan(mod,  BN888,BN549), new ValidationMessage2017(PAGE11,BN888,mustLessMsg(BN888, BN549)))
		,V_BN1370_1 ( mod -> isGreatherThan(mod,  BN1370,BN1369), new ValidationMessage2017(PAGE11,BN1370,mustLessMsg(BN1370, BN1369)))
		,V_BN439_1  ( mod -> isGreatherThan(mod,  BN439,BN438), new ValidationMessage2017(PAGE11,BN439,mustLessMsg(BN439, BN438)))
		,V_BN1082_1 ( mod -> isGreatherThan(mod,  BN1082,BN1081), new ValidationMessage2017(PAGE11,BN1082,mustLessMsg(BN1082, BN1081)))
		,V_BN1085_1 ( mod -> isGreatherThan(mod,  BN1085,BN1084), new ValidationMessage2017(PAGE11,BN1085,mustLessMsg(BN1085, BN1084)))
		,V_BN1088_1 ( mod -> isGreatherThan(mod,  BN1088,BN1087), new ValidationMessage2017(PAGE11,BN1088,mustLessMsg(BN1088, BN1087)))
		,V_BN1091_1 ( mod -> isGreatherThan(mod,  BN1091,BN1090), new ValidationMessage2017(PAGE11,BN1091,mustLessMsg(BN1091, BN1090)))
		,V_BN1094_1 ( mod -> isGreatherThan(mod,  BN1094,BN1093), new ValidationMessage2017(PAGE11,BN1094,mustLessMsg(BN1094, BN1093)))
		,V_BN1097_1 ( mod -> isGreatherThan(mod,  BN1097,BN1096), new ValidationMessage2017(PAGE11,BN1097,mustLessMsg(BN1097, BN1096)))
		,V_BN1100_1 ( mod -> isGreatherThan(mod,  BN1100,BN1099), new ValidationMessage2017(PAGE11,BN1100,mustLessMsg(BN1100, BN1099)))
		,V_BN1103_1 ( mod -> isGreatherThan(mod,  BN1103,BN1102), new ValidationMessage2017(PAGE11,BN1103,mustLessMsg(BN1103, BN1102)))
		,V_BN1106_1 ( mod -> isGreatherThan(mod,  BN1106,BN1105), new ValidationMessage2017(PAGE11,BN1106,mustLessMsg(BN1106, BN1105)))
		,V_BN1115_1 ( mod -> isGreatherThan(mod,  BN1115,BN1114), new ValidationMessage2017(PAGE11,BN1115,mustLessMsg(BN1115, BN1114)))
		,V_BN1118_1 ( mod -> isGreatherThan(mod,  BN1118,BN1117), new ValidationMessage2017(PAGE11,BN1118,mustLessMsg(BN1118, BN1117)))
		,V_BN1373_1 ( mod -> isGreatherThan(mod,  BN1373,BN1372), new ValidationMessage2017(PAGE11,BN1373,mustLessMsg(BN1373, BN1372)))
		,V_BN1376_1 ( mod -> isGreatherThan(mod,  BN1376,BN1375), new ValidationMessage2017(PAGE11,BN1376,mustLessMsg(BN1376, BN1375)))
		,V_BN1379_1 ( mod -> isGreatherThan(mod,  BN1379,BN1378), new ValidationMessage2017(PAGE11,BN1379,mustLessMsg(BN1379, BN1378)))
		,V_BN1382_1 ( mod -> isGreatherThan(mod,  BN1382,BN1381), new ValidationMessage2017(PAGE11,BN1382,mustLessMsg(BN1382, BN1381)))
		,V_BN1385_1 ( mod -> isGreatherThan(mod,  BN1385,BN1384), new ValidationMessage2017(PAGE11,BN1385,mustLessMsg(BN1385, BN1384)))
		,V_BN1388_1 ( mod -> isGreatherThan(mod,  BN1388,BN1387), new ValidationMessage2017(PAGE11,BN1388,mustLessMsg(BN1388, BN1387)))
		,V_BN1391_1 ( mod -> isGreatherThan(mod,  BN1391,BN1390), new ValidationMessage2017(PAGE11,BN1391,mustLessMsg(BN1391, BN1390)))
		,V_BN1394_1 ( mod -> isGreatherThan(mod,  BN1394,BN1393), new ValidationMessage2017(PAGE11,BN1394,mustLessMsg(BN1394, BN1393)))
		,V_BN1397_1 ( mod -> isGreatherThan(mod,  BN1397,BN1396), new ValidationMessage2017(PAGE11,BN1397,mustLessMsg(BN1397, BN1396)))
		,V_BN1400_1 ( mod -> isGreatherThan(mod,  BN1400,BN1399), new ValidationMessage2017(PAGE11,BN1400,mustLessMsg(BN1400, BN1399)))
		,V_BN1403_1 ( mod -> isGreatherThan(mod,  BN1403,BN1402), new ValidationMessage2017(PAGE11,BN1403,mustLessMsg(BN1403, BN1402)))
		,V_BN1406_1 ( mod -> isGreatherThan(mod,  BN1406,BN1405), new ValidationMessage2017(PAGE11,BN1406,mustLessMsg(BN1406, BN1405)))
		,V_BN1409_1 ( mod -> isGreatherThan(mod,  BN1409,BN1408), new ValidationMessage2017(PAGE11,BN1409,mustLessMsg(BN1409, BN1408)))
		,V_BN1412_1 ( mod -> isGreatherThan(mod,  BN1412,BN1411), new ValidationMessage2017(PAGE11,BN1412,mustLessMsg(BN1412, BN1411)))
		,V_BN1415_1 ( mod -> isGreatherThan(mod,  BN1415,BN1414), new ValidationMessage2017(PAGE11,BN1415,mustLessMsg(BN1415, BN1414)))
		,V_BN1418_1 ( mod -> isGreatherThan(mod,  BN1418,BN1417), new ValidationMessage2017(PAGE11,BN1418,mustLessMsg(BN1418, BN1417)))
		,V_BN1421_1 ( mod -> isGreatherThan(mod,  BN1421,BN1420), new ValidationMessage2017(PAGE11,BN1421,mustLessMsg(BN1421, BN1420)))
		,V_BN1424_1 ( mod -> isGreatherThan(mod,  BN1424,BN1423), new ValidationMessage2017(PAGE11,BN1424,mustLessMsg(BN1424, BN1423)))
		,V_BN1624_1 ( mod -> isGreatherThan(mod,  BN1624,BN1623), new ValidationMessage2017(PAGE11,BN1624,mustLessMsg(BN1624, BN1623)))
		,V_BN1627_1 ( mod -> isGreatherThan(mod,  BN1627,BN1626), new ValidationMessage2017(PAGE11,BN1627,mustLessMsg(BN1627, BN1626)))
		,V_BN1630_1 ( mod -> isGreatherThan(mod,  BN1630,BN1629), new ValidationMessage2017(PAGE11,BN1630,mustLessMsg(BN1630, BN1629)))
		,V_BN1633_1 ( mod -> isGreatherThan(mod,  BN1633,BN1632), new ValidationMessage2017(PAGE11,BN1633,mustLessMsg(BN1633, BN1632)))
		,V_BN1636_1 ( mod -> isGreatherThan(mod,  BN1636,BN1635), new ValidationMessage2017(PAGE11,BN1636,mustLessMsg(BN1636, BN1635)))
		,V_BN1639_1 ( mod -> isGreatherThan(mod,  BN1639,BN1638), new ValidationMessage2017(PAGE11,BN1639,mustLessMsg(BN1639, BN1638)))
		,V_BN1642_1 ( mod -> isGreatherThan(mod,  BN1642,BN1641), new ValidationMessage2017(PAGE11,BN1642,mustLessMsg(BN1642, BN1641)))
		,V_BN1645_1 ( mod -> isGreatherThan(mod,  BN1645,BN1644), new ValidationMessage2017(PAGE11,BN1645,mustLessMsg(BN1645, BN1644)))
		,V_BN1648_1 ( mod -> isGreatherThan(mod,  BN1648,BN1647), new ValidationMessage2017(PAGE11,BN1648,mustLessMsg(BN1648, BN1647)))
		,V_BN1651_1 ( mod -> isGreatherThan(mod,  BN1651,BN1650), new ValidationMessage2017(PAGE11,BN1651,mustLessMsg(BN1651, BN1650)))
		,V_BN1654_1 ( mod -> isGreatherThan(mod,  BN1654,BN1653), new ValidationMessage2017(PAGE11,BN1654,mustLessMsg(BN1654, BN1653)))
		,V_BN1657_1 ( mod -> isGreatherThan(mod,  BN1657,BN1656), new ValidationMessage2017(PAGE11,BN1657,mustLessMsg(BN1657, BN1656)))
		,V_BN1660_1 ( mod -> isGreatherThan(mod,  BN1660,BN1659), new ValidationMessage2017(PAGE11,BN1660,mustLessMsg(BN1660, BN1659)))
		,V_BN1663_1 ( mod -> isGreatherThan(mod,  BN1663,BN1662), new ValidationMessage2017(PAGE11,BN1663,mustLessMsg(BN1663, BN1662)))
		,V_BN1666_1 ( mod -> isGreatherThan(mod,  BN1666,BN1665), new ValidationMessage2017(PAGE11,BN1666,mustLessMsg(BN1666, BN1665)))
		,V_BN1669_1 ( mod -> isGreatherThan(mod,  BN1669,BN1668), new ValidationMessage2017(PAGE11,BN1669,mustLessMsg(BN1669, BN1668)))
		,V_BN1672_1 ( mod -> isGreatherThan(mod,  BN1672,BN1671), new ValidationMessage2017(PAGE11,BN1672,mustLessMsg(BN1672, BN1671)))
		,V_BN1675_1 ( mod -> isGreatherThan(mod,  BN1675,BN1674), new ValidationMessage2017(PAGE11,BN1675,mustLessMsg(BN1675, BN1674)))
		,V_BN1678_1 ( mod -> isGreatherThan(mod,  BN1678,BN1677), new ValidationMessage2017(PAGE11,BN1678,mustLessMsg(BN1678, BN1677)))
		,V_BN1681_1 ( mod -> isGreatherThan(mod,  BN1681,BN1680), new ValidationMessage2017(PAGE11,BN1681,mustLessMsg(BN1681, BN1680)))
		,V_BN1690_1 ( mod -> isGreatherThan(mod,  BN1690,BN1689), new ValidationMessage2017(PAGE11,BN1690,mustLessMsg(BN1690, BN1689)))
		,V_BN1693_1 ( mod -> isGreatherThan(mod,  BN1693,BN1692), new ValidationMessage2017(PAGE11,BN1693,mustLessMsg(BN1693, BN1692)))
		,V_BN1696_1 ( mod -> isGreatherThan(mod,  BN1696,BN1695), new ValidationMessage2017(PAGE11,BN1696,mustLessMsg(BN1696, BN1695)))
		,V_BN1699_1 ( mod -> isGreatherThan(mod,  BN1699,BN1698), new ValidationMessage2017(PAGE11,BN1699,mustLessMsg(BN1699, BN1698)))
		,V_BN1702_1 ( mod -> isGreatherThan(mod,  BN1702,BN1701), new ValidationMessage2017(PAGE11,BN1702,mustLessMsg(BN1702, BN1701)))
		,V_BN1705_1 ( mod -> isGreatherThan(mod,  BN1705,BN1704), new ValidationMessage2017(PAGE11,BN1705,mustLessMsg(BN1705, BN1704)))
		,V_BN1708_1 ( mod -> isGreatherThan(mod,  BN1708,BN1707), new ValidationMessage2017(PAGE11,BN1708,mustLessMsg(BN1708, BN1707)))
		,V_BN1801_1 ( mod -> isGreatherThan(mod,  BN1801,BN1800), new ValidationMessage2017(PAGE11,BN1801,mustLessMsg(BN1801, BN1800)))
		,V_BN1684_1 ( mod -> isGreatherThan(mod,  BN1684,BN1683), new ValidationMessage2017(PAGE11,BN1684,mustLessMsg(BN1684, BN1683)))
		,V_BN635_1  ( mod -> isGreatherThan(mod,  BN635,BN634), new ValidationMessage2017(PAGE11,BN635,mustLessMsg(BN635, BN634)))
		,V_BN829_1  ( mod -> isGreatherThan(mod,  BN829,BN828), new ValidationMessage2017(PAGE11,BN829,mustLessMsg(BN829, BN828)))		
		//  -- Deducción donaciones a entidades sin fines de lucro (Ley 49/2002)
		,V_BN295_1 ( mod -> isGreatherThan(mod,  BN295,BN294), new ValidationMessage2017(PAGE11,BN295,mustLessMsg(BN295, BN294)))
		,V_BN074_1 ( mod -> isGreatherThan(mod,  BN074,BN066), new ValidationMessage2017(PAGE11,BN074,mustLessMsg(BN074, BN066)))
		,V_BN009_1 ( mod -> isGreatherThan(mod,  BN009,BN008), new ValidationMessage2017(PAGE11,BN009,mustLessMsg(BN009, BN008)))
		,V_BN035_1 ( mod -> isGreatherThan(mod,  BN035,BN034), new ValidationMessage2017(PAGE11,BN035,mustLessMsg(BN035, BN034)))
		,V_BN202_1 ( mod -> isGreatherThan(mod,  BN202,BN201), new ValidationMessage2017(PAGE11,BN202,mustLessMsg(BN202, BN201)))
		,V_BN905_1 ( mod -> isGreatherThan(mod,  BN905,BN904), new ValidationMessage2017(PAGE11,BN905,mustLessMsg(BN905, BN904)))
		,V_BN991_1 ( mod -> isGreatherThan(mod,  BN991,BN990), new ValidationMessage2017(PAGE11,BN991,mustLessMsg(BN991, BN990)))
		,V_BN998_1 ( mod -> isGreatherThan(mod,  BN998,BN997), new ValidationMessage2017(PAGE11,BN998,mustLessMsg(BN998, BN997)))
		,V_BN247_1 ( mod -> isGreatherThan(mod,  BN247,BN246), new ValidationMessage2017(PAGE11,BN247,mustLessMsg(BN247, BN246)))
		,V_BN1435_1( mod -> isGreatherThan(mod,  BN1435,BN1434), new ValidationMessage2017(PAGE11,BN1435,mustLessMsg(BN1435, BN1434)))
		,V_BN1719_1( mod -> isGreatherThan(mod,  BN1719,BN1718), new ValidationMessage2017(PAGE11,BN1719,mustLessMsg(BN1719, BN1718)))
		
		,V_BN974_1 ( mod -> isGreatherThan(mod,  BN1718,BN974), new ValidationMessage2017(PAGE11,BN974,"El importe consignado en la casilla 00974 no puede ser menor a la deducción generada en el periodo actual por donaciones a entidades sin fines de lucro (casilla 01718)"))
		
		// -- Deducciones Inversión Canarias
		,V_BN855_1 ( mod -> isGreatherThan(mod,  BN855,BN854), new ValidationMessage2017(PAGE11,BN855,mustLessMsg(BN855, BN854)))
		,V_BN858_1 ( mod -> isGreatherThan(mod,  BN858,BN857), new ValidationMessage2017(PAGE11,BN858,mustLessMsg(BN858, BN857)))
		,V_BN861_1 ( mod -> isGreatherThan(mod,  BN861,BN860), new ValidationMessage2017(PAGE11,BN861,mustLessMsg(BN861, BN860)))
		,V_BN864_1 ( mod -> isGreatherThan(mod,  BN864,BN863), new ValidationMessage2017(PAGE11,BN864,mustLessMsg(BN864, BN863)))
		,V_BN884_1 ( mod -> isGreatherThan(mod,  BN884,BN883), new ValidationMessage2017(PAGE11,BN884,mustLessMsg(BN884, BN883)))
		,V_BN789_1 ( mod -> isGreatherThan(mod,  BN789,BN785), new ValidationMessage2017(PAGE11,BN789,mustLessMsg(BN789, BN785)))
		,V_BN1358_1( mod -> isGreatherThan(mod, BN1358,BN1357),new ValidationMessage2017(PAGE11,BN1358,mustLessMsg(BN1358, BN1357)))
		,V_BN1779_1( mod -> isGreatherThan(mod, BN1779,BN1778),new ValidationMessage2017(PAGE11,BN1779,mustLessMsg(BN1779, BN1778)))
		,V_BN853_1 ( mod -> isGreatherThan(mod,  BN853,BN852), new ValidationMessage2017(PAGE11,BN853,mustLessMsg(BN853, BN852)))
		
		,V_BN195_1 ( mod -> isGreatherThan(mod,  BN195,BN194), new ValidationMessage2017(PAGE11,BN195,mustLessMsg(BN195, BN194)))
		,V_BN869_1 ( mod -> isGreatherThan(mod,  BN869,BN868), new ValidationMessage2017(PAGE11,BN869,mustLessMsg(BN869, BN868)))
		,V_BN872_1 ( mod -> isGreatherThan(mod,  BN872,BN871), new ValidationMessage2017(PAGE11,BN872,mustLessMsg(BN872, BN871)))
		,V_BN875_1 ( mod -> isGreatherThan(mod,  BN875,BN874), new ValidationMessage2017(PAGE11,BN875,mustLessMsg(BN875, BN874)))
		,V_BN878_1 ( mod -> isGreatherThan(mod,  BN878,BN877), new ValidationMessage2017(PAGE11,BN878,mustLessMsg(BN878, BN877)))
		,V_BN881_1 ( mod -> isGreatherThan(mod,  BN881,BN880), new ValidationMessage2017(PAGE11,BN881,mustLessMsg(BN881, BN880)))
		,V_BN867_1 ( mod -> isGreatherThan(mod,  BN867,BN866), new ValidationMessage2017(PAGE11,BN867,mustLessMsg(BN867, BN866)))
		,V_BN940_1 ( mod -> isGreatherThan(mod,  BN940,BN939), new ValidationMessage2017(PAGE11,BN940,mustLessMsg(BN940, BN939)))
		,V_BN192_1 ( mod -> isGreatherThan(mod,  BN192,BN191), new ValidationMessage2017(PAGE11,BN192,mustLessMsg(BN192, BN191)))
		,V_BN614_1 ( mod -> isGreatherThan(mod,  BN614,BN613), new ValidationMessage2017(PAGE11,BN614,mustLessMsg(BN614, BN613)))
		,V_BN257_1 ( mod -> isGreatherThan(mod,  BN257,BN200), new ValidationMessage2017(PAGE11,BN257,mustLessMsg(BN257, BN200)))
		,V_BN038_1 ( mod -> isGreatherThan(mod,  BN038,BN037), new ValidationMessage2017(PAGE11,BN038,mustLessMsg(BN038, BN037)))
		,V_BN045_1 ( mod -> isGreatherThan(mod,  BN045,BN044), new ValidationMessage2017(PAGE11,BN045,mustLessMsg(BN045, BN044)))
		,V_BN529_1 ( mod -> isGreatherThan(mod,  BN529,BN528), new ValidationMessage2017(PAGE11,BN529,mustLessMsg(BN529, BN528)))
		,V_BN145_1 ( mod -> isGreatherThan(mod,  BN145,BN144), new ValidationMessage2017(PAGE11,BN145,mustLessMsg(BN145, BN144)))
		,V_BN148_1 ( mod -> isGreatherThan(mod,  BN148,BN147), new ValidationMessage2017(PAGE11,BN148,mustLessMsg(BN148, BN147)))
		,V_BN241_1 ( mod -> isGreatherThan(mod,  BN241,BN240), new ValidationMessage2017(PAGE11,BN241,mustLessMsg(BN241, BN240)))
		,V_BN1059_1(mod -> isGreatherThan(mod,  BN1059,BN1058), new ValidationMessage2017(PAGE11,BN1059,mustLessMsg(BN1059, BN1058)))
		,V_BN802_1 ( mod -> isGreatherThan(mod,  BN802,BN791), new ValidationMessage2017(PAGE11,BN802,mustLessMsg(BN802, BN791)))

		// -- Deducciones sin límite I + D + i
		,V_BN574_1 ( mod -> isGreatherThan(mod,  BN574,BN919), new ValidationMessage2017(PAGE11,BN574,mustLessMsg(BN574, BN919)))
		,V_BN977_1 ( mod -> isGreatherThan(mod,  BN977,BN976), new ValidationMessage2017(PAGE11,BN977,mustLessMsg(BN977, BN976)))
		,V_BN824_1 ( mod -> isGreatherThan(mod,  BN824,BN823), new ValidationMessage2017(PAGE11,BN824,mustLessMsg(BN824, BN823)))
		,V_BN850_1 ( mod -> isGreatherThan(mod,  BN850,BN233), new ValidationMessage2017(PAGE11,BN850,mustLessMsg(BN850, BN233)))
		,V_BN1125_1 ( mod -> isGreatherThan(mod,  BN1125,BN1124), new ValidationMessage2017(PAGE11,BN1125,mustLessMsg(BN1125, BN1124)))
		,V_BN1129_1 ( mod -> isGreatherThan(mod,  BN1129,BN1128), new ValidationMessage2017(PAGE11,BN1129,mustLessMsg(BN1129, BN1128)))
		,V_BN1428_1 ( mod -> isGreatherThan(mod,  BN1428,BN1427), new ValidationMessage2017(PAGE11,BN1428,mustLessMsg(BN1428, BN1427)))
		,V_BN1432_1 ( mod -> isGreatherThan(mod,  BN1432,BN1431), new ValidationMessage2017(PAGE11,BN1432,mustLessMsg(BN1432, BN1431)))
		,V_BN1712_1 ( mod -> isGreatherThan(mod,  BN1712,BN1711), new ValidationMessage2017(PAGE11,BN1712,mustLessMsg(BN1712, BN1711)))
		,V_BN1716_1 ( mod -> isGreatherThan(mod,  BN1716,BN1715), new ValidationMessage2017(PAGE11,BN1716,mustLessMsg(BN1716, BN1715)))

		// -- Deducción por reversión de medidas temporales DT 37ª.1 LIS
		,V_BN1437_1 ( mod -> isGreatherThan(mod,  BN1437,BN1167), new ValidationMessage2017(PAGE11,BN1437,mustLessMsg(BN1437, BN1167)))
		,V_BN1440_1 ( mod -> isGreatherThan(mod,  BN1440,BN1439), new ValidationMessage2017(PAGE11,BN1440,mustLessMsg(BN1440, BN1439)))
		,V_BN1444_1 ( mod -> isGreatherThan(mod,  BN1444,BN1443), new ValidationMessage2017(PAGE11,BN1444,mustLessMsg(BN1444, BN1443)))
		,V_BN1723_1 ( mod -> isGreatherThan(mod,  BN1723,BN1722), new ValidationMessage2017(PAGE11,BN1723,mustLessMsg(BN1723, BN1722)))
		
		// -- Deducción por reversión de medidas temporales DT 37ª.2 LIS
		,V_BN1446_1 ( mod -> isGreatherThan(mod,  BN1446,BN1179), new ValidationMessage2017(PAGE11,BN1446,mustLessMsg(BN1446, BN1179)))
		,V_BN1449_1 ( mod -> isGreatherThan(mod,  BN1449,BN1448), new ValidationMessage2017(PAGE11,BN1449,mustLessMsg(BN1449, BN1448)))
		,V_BN1453_1 ( mod -> isGreatherThan(mod,  BN1453,BN1452), new ValidationMessage2017(PAGE11,BN1453,mustLessMsg(BN1453, BN1452)))
		,V_BN1727_1 ( mod -> isGreatherThan(mod,  BN1727,BN1726), new ValidationMessage2017(PAGE11,BN1727,mustLessMsg(BN1727, BN1726)))

		,V_BN592_1( mod -> isNegative(mod,BN592) ,new ValidationMessage2017(PAGE11,BN592  , mustPositiveMsg(BN592)))
		
		
		// ------------------------------------------------------------------------
		// --------------------------- LIQUIDACIÓN (V) ----------------------------
		// ------------------------------------------------------------------------

		,V_BN1785_1( mod -> isNegative(mod,BN1785) ,new ValidationMessage2017(PAGE12,BN1785, mustPositiveMsg(BN1785)))
		,V_BN1786_1( mod -> isNegative(mod,BN1786) ,new ValidationMessage2017(PAGE12,BN1786, mustPositiveMsg(BN1786)))
		,V_BN1787_1( mod -> isNegative(mod,BN1787) ,new ValidationMessage2017(PAGE12,BN1787, mustPositiveMsg(BN1787)))
		,V_BN1788_1( mod -> isNegative(mod,BN1788) ,new ValidationMessage2017(PAGE12,BN1788, mustPositiveMsg(BN1788)))
		,V_BN1789_1( mod -> isNegative(mod,BN1789) ,new ValidationMessage2017(PAGE12,BN1789, mustPositiveMsg(BN1789)))
		,V_BN1790_1( mod -> isNegative(mod,BN1790) ,new ValidationMessage2017(PAGE12,BN1790, mustPositiveMsg(BN1790)))
		,V_BN1791_1( mod -> isNegative(mod,BN1791) ,new ValidationMessage2017(PAGE12,BN1791, mustPositiveMsg(BN1791)))
		,V_BN1792_1( mod -> isNegative(mod,BN1792) ,new ValidationMessage2017(PAGE12,BN1792, mustPositiveMsg(BN1792)))
		,V_BN1793_1( mod -> isNegative(mod,BN1793) ,new ValidationMessage2017(PAGE12,BN1793, mustPositiveMsg(BN1793)))
		,V_BN1794_1( mod -> isNegative(mod,BN1794) ,new ValidationMessage2017(PAGE12,BN1794, mustPositiveMsg(BN1794)))
		,V_BN1795_1( mod -> isNegative(mod,BN1795) ,new ValidationMessage2017(PAGE12,BN1795, mustPositiveMsg(BN1795)))
		,V_BN1796_1( mod -> isNegative(mod,BN1796) ,new ValidationMessage2017(PAGE12,BN1796, mustPositiveMsg(BN1796)))
		,V_BN597_1 ( mod -> isNegative(mod,BN597)  ,new ValidationMessage2017(PAGE12,BN597,  mustPositiveMsg(BN597)))
		,V_BN1797_1( mod -> isNegative(mod,BN1797) ,new ValidationMessage2017(PAGE12,BN1797, mustPositiveMsg(BN1797)))
		,V_BN1798_1( mod -> isNegative(mod,BN1798) ,new ValidationMessage2017(PAGE12,BN1798, mustPositiveMsg(BN1798)))
		,V_BN1799_1( mod -> isNegative(mod,BN1799) ,new ValidationMessage2017(PAGE12,BN1799, mustPositiveMsg(BN1799)))
		
		,V_BN615_1( mod -> isNegative(mod,BN615) ,new ValidationMessage2017(PAGE12,BN615, mustPositiveMsg(BN615)))
		,V_BN616_1( mod -> isNegative(mod,BN616) ,new ValidationMessage2017(PAGE12,BN616, mustPositiveMsg(BN616)))
		
		,V_BN633_1( mod -> isNegative(mod,BN633) ,new ValidationMessage2017(PAGE12,BN633, mustPositiveMsg(BN633)))
		,V_BN6hue42_1( mod -> isNegative(mod,BN642) ,new ValidationMessage2017(PAGE12,BN642, mustPositiveMsg(BN642)))
		
		,V_BN617_1( mod -> isNegative(mod,BN617) ,new ValidationMessage2017(PAGE12,BN617, mustPositiveMsg(BN617)))
		,V_BN618_1( mod -> isNegative(mod,BN618) ,new ValidationMessage2017(PAGE12,BN618, mustPositiveMsg(BN618)))
		
		,V_BN1042_1( mod -> isNegative(mod,BN1042) ,new ValidationMessage2017(PAGE12,BN1042, mustPositiveMsg(BN1042)))
		
		// ------------------------------------------------------------------------
		// ------------------- APLICACION DE RESULTADOS ---------------------------
		// ------------------------------------------------------------------------
		,V_ID650  ( mod -> isNegative(mod,ID650),new ValidationMessage2017(PAGE14,ID650, mustPositiveMsg(ID650)))
		,V_ID651  ( mod -> isNegative(mod,ID651),new ValidationMessage2017(PAGE14,ID651, mustPositiveMsg(ID651)))
		,V_ID652  ( mod -> isNegative(mod,ID652),new ValidationMessage2017(PAGE14,ID652, mustPositiveMsg(ID652)))
		,V_ID666  ( mod -> isNegative(mod,ID666),new ValidationMessage2017(PAGE14,ID666, mustPositiveMsg(ID666)))
		,V_ID654  ( mod -> isNegative(mod,ID654),new ValidationMessage2017(PAGE14,ID654, mustPositiveMsg(ID654)))
		,V_ID655  ( mod -> isNegative(mod,ID655),new ValidationMessage2017(PAGE14,ID655, mustPositiveMsg(ID655)))
		,V_ID656  ( mod -> isNegative(mod,ID656),new ValidationMessage2017(PAGE14,ID656, mustPositiveMsg(ID656)))
		,V_ID658  ( mod -> isNegative(mod,ID658),new ValidationMessage2017(PAGE14,ID658, mustPositiveMsg(ID658)))
		,V_ID659  ( mod -> isNegative(mod,ID659),new ValidationMessage2017(PAGE14,ID659, mustPositiveMsg(ID659)))
		,V_ID660  ( mod -> isNegative(mod,ID660),new ValidationMessage2017(PAGE14,ID660, mustPositiveMsg(ID660)))
		,V_ID662  ( mod -> isNegative(mod,ID662),new ValidationMessage2017(PAGE14,ID662, mustPositiveMsg(ID662)))
		,V_ID664  ( mod -> isNegative(mod,ID664),new ValidationMessage2017(PAGE14,ID664, mustPositiveMsg(ID664)))
		,V_ID665  ( mod -> isNegative(mod,ID665),new ValidationMessage2017(PAGE14,ID665, mustPositiveMsg(ID665)))

		// ------------------------------------------------------------------------
		// -------------- LIMITACIÓN DEDUC. GASTOS FINANCIEROS. -------------------
		// ------------------------------------------------------------------------
		,V_LM1241_1 ( mod -> (getValue(mod,  LM1241) < getValue(mod,LM1242)+getValue(mod,LM1244))
			, new ValidationMessage2017(PAGE15,LM1241,
			"La suma de las casillas [01242] \""+LM1242.getDescription()+"\" y [01244] \""+LM1244.getDescription()
					+"\" debe ser menor o igual que [01241] \""+LM1241.getDescription()+"\""))
		,V_LM1256_1 ( mod -> (getValue(mod,LM1249)+getValue(mod,LM1255) < getValue(mod,  LM1256))
			, new ValidationMessage2017(PAGE15,LM1256,
			"La suma de las casillas [01249] \""+LM1249.getDescription()+"\" y [01255] \""+LM1255.getDescription()
					+"\" debe ser menor o igual que [01256] \""+LM1256.getDescription()+"\""))
		,V_LM1243_1 ( mod -> isNotEqual(mod,LM1739, LM1243), new ValidationMessage2017(PAGE15,LM1739,mustEqualMsg( LM1243, LM1739 )))
		,V_LM1257_1 ( mod -> isNotEqual(mod,LM1740, LM1257), new ValidationMessage2017(PAGE15,LM1740,mustEqualMsg( LM1257, LM1740 )))
		,V_LM1256_2 ( mod -> isNotEqual(mod,LM1738, LM1256), new ValidationMessage2017(PAGE15,LM1738,mustEqualMsg( LM1256, LM1738 )))
		
		,V_LM1258_1 ( mod -> ((getValue(mod, LM1258) + (getValue(mod, LM1259))) != 
			(getValue(mod, LM1189) + getValue(mod, LM1194) + getValue(mod, LM1199) + getValue(mod, LM1204) + getValue(mod, LM1209) + getValue(mod, LM1464)))
			, new ValidationMessage2017(PAGE15,LM1258,"Compruebe los  gastos financieros pendientes de deducir de períodos anteriores aplicados en esta " +
					"liquidación, claves 01258, 01259, 01189, 01194, 01199, 01204, 01209 y/o 01464"))
		
		// ------------------------------------------------------------------------
		// -------------- RESERVA DE CAPITALIZACIÓN. ------------------------------
		// ------------------------------------------------------------------------
		,V_LM1751_1 ( mod -> isGreatherThan(mod, LM1751, LM1750)
			, new ValidationMessage2017(PAGE15,LM1750,mustLessMsg( LM1751, LM1750 )))
		
		;		
		private IValidator validator;
		private ValidationMessage2017 message;
		private Type( IValidator validator,ValidationMessage2017 message){
			this.validator = validator;
			this.message = message;
		}
		public boolean validate(Mod2002017 mod) {
			return (this.validator.validate(mod));
		}
		public ValidationMessage2017 getMessage() {
			return message;
		}
	}
	public static void validate(Mod2002017 mod) {
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
	
	private static void validateRepresentatives(Mod2002017 mod200) {
		if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getRepresentatives() == null || mod200.getRepresentatives().size() == 0 ) {
				mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Para personas jur\u00EDdicas, debe rellenar al menos un representante."));
			} else {
				for (int i = 0; i < mod200.getRepresentatives().size(); i++ ) {
					LegalRepresentative lr = mod200.getRepresentatives().get(i); 
					if (!AonDocumentUtil.isValid(lr.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE01,"NIF del representante legal n\u00BA "+(i+1) +" incorrecto ["+lr.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(lr.getName())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Falta nombre del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					}
					if (AonStringUtils.isEmpty(lr.getNotary())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Falta el dato de la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					} else if (lr.getNotary().length() > 20) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Longitud excedida en la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]. Debe limitarse a 20 caracteres."));	
					}
					if (lr.getNotaryDate() == null) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Falta el dato fecha de la notar\u00EDa del representante legal n\u00BA "+(i+1) +". ["+lr.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateAdministrators(Mod2002017 mod200) {
		if (mod200.getAdministrators() == null || mod200.getAdministrators().size() == 0 ) {
			mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Debe rellenar al menos un administrador."));
		} else {
			for (int i = 0; i < mod200.getAdministrators().size(); i++ ) {
				CompanyAdministrator ca = mod200.getAdministrators().get(i); 
				if (!AonDocumentUtil.isValid(ca.getDocument())) {
					mod200.getMessages().add(new ValidationMessage2017(PAGE01,"NIF del administrador n\u00BA "+(i+1) +" incorrecto ["+ca.getDocument()+"]"));		
				}
				if (AonStringUtils.isEmpty(ca.getName())) {
					mod200.getMessages().add(new ValidationMessage2017(PAGE01,"Falta nombre del administrador n\u00BA "+(i+1) +". ["+ca.getDocument()+"]"));
				}
			}
		}
	}

	private static void validateParticipationsIn(Mod2002017 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())
			&& !AonDocumentUtil.isCulturalAssociation(mod200.getEnterpriseDocument())) {
			 LinkedList<CompanyParticipation> participations = mod200.getParticipationsIn();
			if (participations == null || participations.size() == 0) {
				mod200.getMessages().add(new ValidationMessage2017(PAGE02,"Para personas jur\u00EDdicas, debe rellenar los datos de participaci\u00F3n en la declarante"));
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"NIF de la participaci\u00F3n en la declarante n\u00BA "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"Falta nombre de la participaci\u00F3n en la declarante n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"Porcentaje no correcto en la participaci\u00F3n en la declarante n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateParticipationsOut(Mod2002017 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			 LinkedList<CompanyParticipation> participations = mod200.getParticipationsOut();
			if (participations == null || participations.size() == 0) {
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"NIF de la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"Falta nombre de la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						mod200.getMessages().add(new ValidationMessage2017(PAGE02,"Porcentaje no correcto en la participaci\u00F3n de la declarante en otras n\u00BA "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static double getValue(Mod2002017 mod, Mod2002017Key key) {
		if (mod.getDraftMap().containsKey(key)){
			DoubleVariable2017 dv = mod.getDraftMap().get(key);
			if (dv != null) {
				Object o = dv.getValue();
				if (o != null && o instanceof Double) {
					return (Double) o;
				}
			}
		}
		return mod.getDoubleValue(key);
	}
	private static boolean isPositive(Mod2002017 mod, Mod2002017Key key) {
		return AonMathUtils.isGreatherThanZero( getValue(mod,key) );
	}
	private static boolean isNegative(Mod2002017 mod, Mod2002017Key key) {
		return AonMathUtils.isLessThanZero( getValue(mod,key) );
	}
	private static boolean isZero(Mod2002017 mod, Mod2002017Key key) {
		return AonMathUtils.isZero( getValue(mod,key) );
	}
	private static boolean isNotZero(Mod2002017 mod, Mod2002017Key key) {
		return !AonMathUtils.isZero( getValue(mod,key) );
	}
	private static boolean isEqual(Mod2002017 mod, Mod2002017Key key1, Mod2002017Key key2) {
		return AonMathUtils.equals( getValue(mod,key1), getValue(mod,key2) );
	}
	private static boolean isEqualNumber(Mod2002017 mod, Mod2002017Key key1, int number) {
		return AonMathUtils.equals( getValue(mod,key1), number );
	}
	private static boolean isNotEqual(Mod2002017 mod, Mod2002017Key key1, Mod2002017Key key2) {
		return !isEqual(mod, key1, key2);
	}
	private static boolean isGreatherThan(Mod2002017 mod, Mod2002017Key key1, Mod2002017Key key2) {
		return getValue(mod,key1) > getValue(mod,key2);
	}	
	private static boolean isLessThan(Mod2002017 mod, Mod2002017Key key1, Mod2002017Key key2) {
		return getValue(mod,key1) < getValue(mod,key2);
	}	
	private static boolean isBalanceNormal(Mod2002017 mod) {
		return (mod.getBalanceType() == BalanceType.NORMAL); 
	}
	private static boolean isBalancePymes(Mod2002017 mod) {
		return (mod.getBalanceType() == BalanceType.PYMES); 
	}
	private static boolean isNotBalancePymes(Mod2002017 mod) {
		return !isBalancePymes(mod);		 
	}
	private static boolean isCooperativa(Mod2002017 mod) {
		return mod.isChecked(C0017) || mod.isChecked(C0018) || mod.isChecked(C0019);		 
	}
	private static boolean isGrupo(Mod2002017 mod) {
		return mod.isChecked(C0009) || mod.isChecked(C0010);		 
	}
	private static boolean isECPNFilled(Mod2002017 mod) {
		return isNotZero(mod, TC645) && isNotZero(mod, T0355);
	}	
	private static String checkSignMsg( Mod2002017Key key) {
		String desc = key.getDescription();
		return MessageFormat.format(CHECK_SIGN_MSG, key.getCode(), AonStringUtils.isBlank(desc)?"": ("- \"" + desc + "\""));
	}
	private static String mustPositiveMsg( Mod2002017Key key) {
		String desc = key.getDescription();
		return MessageFormat.format(MUST_POSITIVE_MSG, key.getCode(), AonStringUtils.isBlank(desc)?"": ("- \"" + desc + "\""));
	}
	
	
	private static String mustEqualMsg( Mod2002017Key key1, Mod2002017Key key2 ) {
		return format(MUST_EQUAL_MSG,key1,key2); 
	}
	private static String mustGreatherMsg( Mod2002017Key key1, Mod2002017Key key2 ) {
		return format(MUST_GREATHER_MSG,key1,key2); 
	}
	private static String mustLessMsg( Mod2002017Key key1, Mod2002017Key key2 ) {
		return format(MUST_LESS_MSG,key1,key2); 
	}
	private static String incompatibleCharacterMsg( Mod2002017Key key1,Mod2002017Key key2) {
		return format(INV_BOX_MSG,key1,key2); 
	}
	private static String format(String pattern, Mod2002017Key key1,Mod2002017Key key2) {
		String desc1 = key1.getDescription();
		String desc2 = key2.getDescription();  
		return MessageFormat.format(pattern,key1.getCode()
			,AonStringUtils.isBlank(desc1)?"": ("- \"" + desc1 + "\"")
			,key2.getCode()
			,AonStringUtils.isBlank(desc2)?"": ("\"" + desc2 + "\"")
			); 
	}
	
}
