package com.esferalia.aon.occam.jooq.test.mod200;


import java.io.IOException;
import java.sql.SQLException;
import java.util.EnumMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Behaviour;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.ValidationMessage2016;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import junit.framework.Assert;


public class Mod200Test {

	private static AONContext ctx;
	
	// 	[*] Aquí debes poner el nombre del dominio de tu base de datos
	private static String DOMAIN_NAME = "proorgan-masdemar.ecastellano.dev";
	// 	[*] Aquí debes poner el ID del dominio de tu base de datos
	private static int DOMAIN_ID = 804;
	// 	[*] Aquí debes poner el login de un usuario con permisos.
	private static String LOGIN = "luis";
	
	private static Mod2002016 mod200;
	
	// 	[*] Antes de comenzar el test, se carga el driver JDBC y se conecta a la base de datos.
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( com.mysql.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}
	
	// 	[*] JUNIT instancia esta clase cada vez que se ejecuta un método marcado con @Test
	//		Este método se ejecuta antes de generar la instancia.
	@Before
	public void beforeInstance() throws ClassNotFoundException, SQLException, AonConnectionException {
		System.out.println( "\n-- FIRST OF ALL\n" );
		mod200 = createNormal();
	}
	
	@Test
	public void testBalanceActivoNormal() throws IOException {
		System.out.println( "\n-- BALANCE: ACTIVO - NORMAL\n" );
		// BALANCE: ACTIVO - NORMAL
		
		
		// [*] Damos un valor aleatorio a todas las casillas que permiten introducción manual.
		System.out.println( "\n\t-- random initialize\n" );
		
		// [*] El mapa "expected" simula los datos de pantalla.
		EnumMap<Mod2002016Key,Double> expected = new EnumMap<Mod2002016Key,Double>(Mod2002016Key.class);
		for (Mod2002016Key key : Mod2002016Constants.BALANCE_ACTIVE_KEYS) {
			Boolean[] behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key);
			if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
				expected.put(key, valueChanged(mod200,key));		
			}
		}
		
		// [*] Se calcula el modelo, las casillas que son calculadas, deben toman valor.
		calculate(mod200);
		
		// BALANCE: ACTIVO - NORMAL - ACTIVO NO CORRIENTE
		double BA102 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BA103)+expected.get(Mod2002016Key.BA104)+expected.get(Mod2002016Key.BA105)
			+expected.get(Mod2002016Key.BA106)+expected.get(Mod2002016Key.BA107)+expected.get(Mod2002016Key.BA108)
			+expected.get(Mod2002016Key.BA700)+expected.get(Mod2002016Key.BA109));
		double BA111 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA112)+expected.get(Mod2002016Key.BA113)+expected.get(Mod2002016Key.BA114) );
		double BA115 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA116)+expected.get(Mod2002016Key.BA117));
		double BA118 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA119)+expected.get(Mod2002016Key.BA120)+expected.get(Mod2002016Key.BA121)
			+expected.get(Mod2002016Key.BA122)+expected.get(Mod2002016Key.BA123)+expected.get(Mod2002016Key.BA124));
		double BA126 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA127)+expected.get(Mod2002016Key.BA128)+expected.get(Mod2002016Key.BA129)
			+expected.get(Mod2002016Key.BA130)+expected.get(Mod2002016Key.BA131)+expected.get(Mod2002016Key.BA132));
		double BA101 = AonMathUtils.round(BA102+BA111+BA115+BA118+BA126
			 +expected.get(Mod2002016Key.BA134)+expected.get(Mod2002016Key.BA135));
		
		// BALANCE: ACTIVO - NORMAL - ACTIVO CORRIENTE
		double BA141 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA142)+expected.get(Mod2002016Key.BA143));
		double BA144 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA145)+expected.get(Mod2002016Key.BA146));
		double BA138 = AonMathUtils.round(
			expected.get(Mod2002016Key.BA139)+expected.get(Mod2002016Key.BA140)
			+BA141+BA144
			+expected.get(Mod2002016Key.BA147)+expected.get(Mod2002016Key.BA148)+expected.get(Mod2002016Key.BA701));
		double BA150 = AonMathUtils.round( 
			 expected.get(Mod2002016Key.BA151)+expected.get(Mod2002016Key.BA152));
		double BA149 = AonMathUtils.round(BA150
			+expected.get(Mod2002016Key.BA153)+expected.get(Mod2002016Key.BA154)+expected.get(Mod2002016Key.BA155)
			+expected.get(Mod2002016Key.BA156)+expected.get(Mod2002016Key.BA157)+expected.get(Mod2002016Key.BA158));
		double BA160 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BA161)+expected.get(Mod2002016Key.BA162)+expected.get(Mod2002016Key.BA163)
			+expected.get(Mod2002016Key.BA164)+expected.get(Mod2002016Key.BA165)+expected.get(Mod2002016Key.BA166));
		double BA168 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BA169)+expected.get(Mod2002016Key.BA170)+expected.get(Mod2002016Key.BA171)
			+expected.get(Mod2002016Key.BA172)+expected.get(Mod2002016Key.BA173)+expected.get(Mod2002016Key.BA174));
		double BA177 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BA178)+expected.get(Mod2002016Key.BA179));
		double BA136 = AonMathUtils.round(
			expected.get(Mod2002016Key.BA137)+BA138+BA149+BA160+BA168+expected.get(Mod2002016Key.BA176)+BA177);

		// BALANCE: ACTIVO - NORMAL - TOTAL ACTIVO
		double BA180 = AonMathUtils.round(BA101+BA136);
					
		System.out.println(AonStringUtils.repeat("=", 132));		
		System.out.println(AonStringUtils.leftPad("CALC", 9)
				 + " "  +AonStringUtils.leftPad("EXPECTED", 10)
				 + "  " +AonStringUtils.rightPad("DESCRIPTION", 110)
				);		
		System.out.println(AonStringUtils.repeat("=", 132));
		
		// [*] A partir de aquí vienen las comprobaciones.
		// 		Se toman los valores de expected, se realiza la 
		//		operación pertinente, que debe coincidoir con la casilla correspondiente del modelo. 
		//

		assertEquals(mod200,Mod2002016Key.BA102, BA102);
		assertEquals(mod200,Mod2002016Key.BA111, BA111);
		assertEquals(mod200,Mod2002016Key.BA115, BA115);
		assertEquals(mod200,Mod2002016Key.BA118, BA118);
		assertEquals(mod200,Mod2002016Key.BA126, BA126);
		assertEquals(mod200,Mod2002016Key.BA101, BA101 );
		assertEquals(mod200,Mod2002016Key.BA141, BA141);
		assertEquals(mod200,Mod2002016Key.BA144, BA144);
		assertEquals(mod200,Mod2002016Key.BA138, BA138);
		assertEquals(mod200,Mod2002016Key.BA149, BA149);
		assertEquals(mod200,Mod2002016Key.BA150, BA150);
		assertEquals(mod200,Mod2002016Key.BA160, BA160);
		assertEquals(mod200,Mod2002016Key.BA168, BA168);
		assertEquals(mod200,Mod2002016Key.BA177, BA177);
		assertEquals(mod200,Mod2002016Key.BA136, BA136);
		assertEquals(mod200,Mod2002016Key.BA180, BA180);
	}
	
	@Test
	public void testBalancePasivoNormal() throws IOException {
		System.out.println( "\n-- BALANCE: PATRIMONIO NETO Y PASIVO - NORMAL\n" );
		// BALANCE: PATRIMONIO NETO Y PASIVO - NORMAL
		System.out.println( "\n\t-- random initialize\n" );
		EnumMap<Mod2002016Key,Double> expected = new EnumMap<Mod2002016Key,Double>(Mod2002016Key.class);
		for (Mod2002016Key key : Mod2002016Constants.BALANCE_PASIVE_KEYS) {
			Boolean[] behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key);
			if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
				expected.put(key, valueChanged(mod200,key));		
			}
		}
		calculate(mod200);

		// BALANCE: PATRIMONIO NETO 
		double BP187 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP188)+expected.get(Mod2002016Key.BP189));
		double BP191 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP192)+expected.get(Mod2002016Key.BP193)+expected.get(Mod2002016Key.BP702)
			+expected.get(Mod2002016Key.BP1001)+expected.get(Mod2002016Key.BP1002));
		double BP195 = AonMathUtils.round(expected.get(Mod2002016Key.BP196)+expected.get(Mod2002016Key.BP197));
		double BP186 = AonMathUtils.round(
				+BP187+expected.get(Mod2002016Key.BP190)+BP191+expected.get(Mod2002016Key.BP194)+BP195
				+expected.get(Mod2002016Key.BP198)+expected.get(Mod2002016Key.BP199)
				+expected.get(Mod2002016Key.BP200)+expected.get(Mod2002016Key.BP201));
		double BP202 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP203)+expected.get(Mod2002016Key.BP204)+expected.get(Mod2002016Key.BP205)
			+expected.get(Mod2002016Key.BP206)+expected.get(Mod2002016Key.BP207));
		double BP185 = AonMathUtils.round(+BP186+BP202+expected.get(Mod2002016Key.BP209));
		
		// BALANCE: PASIVO NO CORRIENTE
		double BP211 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP212)+expected.get(Mod2002016Key.BP213)
			+expected.get(Mod2002016Key.BP214)+expected.get(Mod2002016Key.BP215));
		double BP216 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP217)+expected.get(Mod2002016Key.BP218)
			+expected.get(Mod2002016Key.BP219)+expected.get(Mod2002016Key.BP220)
			+expected.get(Mod2002016Key.BP221));
		double BP210 = AonMathUtils.round(
			 BP211+BP216
			+expected.get(Mod2002016Key.BP223)+expected.get(Mod2002016Key.BP224)
			+expected.get(Mod2002016Key.BP225)+expected.get(Mod2002016Key.BP226)
			+expected.get(Mod2002016Key.BP227)
				);
		// BALANCE: PASIVO CORRIENTE
		double BP230 = AonMathUtils.round(
			 expected.get(Mod2002016Key.BP703)+expected.get(Mod2002016Key.BP704));
		double BP231 = AonMathUtils.round(
			  expected.get(Mod2002016Key.BP232)+expected.get(Mod2002016Key.BP233)
			 +expected.get(Mod2002016Key.BP234)+expected.get(Mod2002016Key.BP235)
			 +expected.get(Mod2002016Key.BP236));
		double BP240 = AonMathUtils.round(
			  expected.get(Mod2002016Key.BP241)+expected.get(Mod2002016Key.BP242));
		double BP239 = AonMathUtils.round(
			  BP240+expected.get(Mod2002016Key.BP243)
			 +expected.get(Mod2002016Key.BP244)+expected.get(Mod2002016Key.BP245)
			 +expected.get(Mod2002016Key.BP246)+expected.get(Mod2002016Key.BP247)
			 +expected.get(Mod2002016Key.BP248));
		double BP228 = AonMathUtils.round(
				expected.get(Mod2002016Key.BP229)+BP230+BP231
				+expected.get(Mod2002016Key.BP238)+BP239
				+expected.get(Mod2002016Key.BP250)+expected.get(Mod2002016Key.BP251)
				);
		double BP252 = AonMathUtils.round(BP185+BP210+BP228);
				
				
		System.out.println(AonStringUtils.repeat("=", 132));		
		System.out.println(AonStringUtils.leftPad("CALC", 9)
				 + " "  +AonStringUtils.leftPad("EXPECTED", 10)
				 + "  " +AonStringUtils.rightPad("DESCRIPTION", 110)
				);		
		assertEquals(mod200,Mod2002016Key.BP187, BP187);
		assertEquals(mod200,Mod2002016Key.BP191, BP191);
		assertEquals(mod200,Mod2002016Key.BP195, BP195);
		assertEquals(mod200,Mod2002016Key.BP186, BP186);
		assertEquals(mod200,Mod2002016Key.BP202, BP202);
		assertEquals(mod200,Mod2002016Key.BP185, BP185);
		assertEquals(mod200,Mod2002016Key.BP211, BP211);
		assertEquals(mod200,Mod2002016Key.BP216, BP216);
		assertEquals(mod200,Mod2002016Key.BP210, BP210);
		assertEquals(mod200,Mod2002016Key.BP230, BP230);
		assertEquals(mod200,Mod2002016Key.BP231, BP231);
		assertEquals(mod200,Mod2002016Key.BP240, BP240);
		assertEquals(mod200,Mod2002016Key.BP239, BP239);
		assertEquals(mod200,Mod2002016Key.BP228, BP228);
		assertEquals(mod200,Mod2002016Key.BP252, BP252);
	}

	@Test
	public void testPyGNormal() throws IOException {
		System.out.println( "\n-- CUENTA DE PERDIDAS Y GANANCIAS - NORMAL\n" );
		// BALANCE: PATRIMONIO NETO Y PASIVO - NORMAL
		System.out.println( "\n\t-- random initialize\n" );
		EnumMap<Mod2002016Key,Double> expected = new EnumMap<Mod2002016Key,Double>(Mod2002016Key.class);
		for (Mod2002016Key key : Mod2002016Constants.PYG_KEYS) {
			Boolean[] behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key);
			if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
				expected.put(key, valueChanged(mod200,key));		
			}
		}
		calculate(mod200);
		// CUENTA DE PERDIDAS Y GANANCIAS
		double PG705 = AonMathUtils.round(expected.get(Mod2002016Key.PG706)
			+expected.get(Mod2002016Key.PG707)+expected.get(Mod2002016Key.PG708));
		double PG255 = AonMathUtils.round(
			expected.get(Mod2002016Key.PG256)+expected.get(Mod2002016Key.PG257)+PG705);
		double PG261 = AonMathUtils.round(expected.get(Mod2002016Key.PG760)+expected.get(Mod2002016Key.PG761));
		double PG262 = AonMathUtils.round(expected.get(Mod2002016Key.PG762)+expected.get(Mod2002016Key.PG763));
		double PG260 = AonMathUtils.round(PG261+PG262
			+expected.get(Mod2002016Key.PG263)+expected.get(Mod2002016Key.PG264));
		double PG266 = AonMathUtils.round(expected.get(Mod2002016Key.PG267)+expected.get(Mod2002016Key.PG268));
		double PG265 = AonMathUtils.round(PG266+expected.get(Mod2002016Key.PG269));
		double PG270 = AonMathUtils.round(expected.get(Mod2002016Key.PG271)
			+expected.get(Mod2002016Key.PG273)+expected.get(Mod2002016Key.PG274)+expected.get(Mod2002016Key.PG275)
			+expected.get(Mod2002016Key.PG276)+expected.get(Mod2002016Key.PG277)+expected.get(Mod2002016Key.PG278));
		double PG279 = AonMathUtils.round(expected.get(Mod2002016Key.PG280)
			+expected.get(Mod2002016Key.PG281)+expected.get(Mod2002016Key.PG282)+expected.get(Mod2002016Key.PG283)
			+expected.get(Mod2002016Key.PG709));
		double PG288 = AonMathUtils.round(expected.get(Mod2002016Key.PG289)+expected.get(Mod2002016Key.PG290));
		double PG291 = AonMathUtils.round(expected.get(Mod2002016Key.PG292)+expected.get(Mod2002016Key.PG293));
		double PG287 = AonMathUtils.round(PG288+PG291+expected.get(Mod2002016Key.PG710));
		double PG296 = AonMathUtils.round(PG255+expected.get(Mod2002016Key.PG258)+expected.get(Mod2002016Key.PG259)
			+PG260+PG265+PG270+PG279+expected.get(Mod2002016Key.PG284)+expected.get(Mod2002016Key.PG285)
			+expected.get(Mod2002016Key.PG286)+PG287+expected.get(Mod2002016Key.PG294)+expected.get(Mod2002016Key.PG295));
		double PG298 = AonMathUtils.round(expected.get(Mod2002016Key.PG299)+expected.get(Mod2002016Key.PG300));
		double PG301 = AonMathUtils.round(expected.get(Mod2002016Key.PG302)+expected.get(Mod2002016Key.PG303));
		double PG297 = AonMathUtils.round(PG298+PG301+expected.get(Mod2002016Key.PG304));
		double PG305 = AonMathUtils.round(expected.get(Mod2002016Key.PG306)+expected.get(Mod2002016Key.PG307)
			+expected.get(Mod2002016Key.PG308));
		double PG309 = AonMathUtils.round(expected.get(Mod2002016Key.PG310)+expected.get(Mod2002016Key.PG311));
		double PG314 = AonMathUtils.round(expected.get(Mod2002016Key.PG315)+expected.get(Mod2002016Key.PG316)
			+expected.get(Mod2002016Key.PG317)+expected.get(Mod2002016Key.PG318));
		double PG319 = AonMathUtils.round(expected.get(Mod2002016Key.PG320)+expected.get(Mod2002016Key.PG321)
			+expected.get(Mod2002016Key.PG322)+expected.get(Mod2002016Key.PG323));
		double PG313 = AonMathUtils.round(PG314+PG319);
		double PG329 = AonMathUtils.round(expected.get(Mod2002016Key.PG330)+expected.get(Mod2002016Key.PG331)+expected.get(Mod2002016Key.PG332));
		double PG324 = AonMathUtils.round(PG297+PG305+PG309+expected.get(Mod2002016Key.PG312)+PG313+PG329);
		double PG325 = AonMathUtils.round(PG324+PG296);
		double PG327 = AonMathUtils.round(PG325+expected.get(Mod2002016Key.PG326));
		double PG500 = AonMathUtils.round(PG327+expected.get(Mod2002016Key.PG328));
		
		System.out.println(AonStringUtils.repeat("=", 132));		
		System.out.println(AonStringUtils.leftPad("CALC", 9)
				 + " "  +AonStringUtils.leftPad("EXPECTED", 10)
				 + "  " +AonStringUtils.rightPad("DESCRIPTION", 110)
				);
		
		assertEquals(mod200,Mod2002016Key.PG705, PG705);
		assertEquals(mod200,Mod2002016Key.PG255, PG255);
		assertEquals(mod200,Mod2002016Key.PG261, PG261);
		assertEquals(mod200,Mod2002016Key.PG262, PG262);
		assertEquals(mod200,Mod2002016Key.PG260, PG260);
		assertEquals(mod200,Mod2002016Key.PG266, PG266);
		assertEquals(mod200,Mod2002016Key.PG265, PG265);
		assertEquals(mod200,Mod2002016Key.PG270, PG270);
		assertEquals(mod200,Mod2002016Key.PG279, PG279);
		assertEquals(mod200,Mod2002016Key.PG288, PG288);
		assertEquals(mod200,Mod2002016Key.PG291, PG291);
		assertEquals(mod200,Mod2002016Key.PG287, PG287);
		assertEquals(mod200,Mod2002016Key.PG296, PG296);
		assertEquals(mod200,Mod2002016Key.PG298, PG298);
		assertEquals(mod200,Mod2002016Key.PG301, PG301);
		assertEquals(mod200,Mod2002016Key.PG297, PG297);
		assertEquals(mod200,Mod2002016Key.PG305, PG305);
		assertEquals(mod200,Mod2002016Key.PG309, PG309);
		assertEquals(mod200,Mod2002016Key.PG314, PG314);
		assertEquals(mod200,Mod2002016Key.PG319, PG319);
		assertEquals(mod200,Mod2002016Key.PG313, PG313);
		assertEquals(mod200,Mod2002016Key.PG329, PG329);
		assertEquals(mod200,Mod2002016Key.PG324, PG324);
		assertEquals(mod200,Mod2002016Key.PG325, PG325);
		assertEquals(mod200,Mod2002016Key.PG327, PG327);
		assertEquals(mod200,Mod2002016Key.PG500, PG500);
	}	
	
	
	@Test
	public void testValidation() throws IOException {
		// [*] se realiza una validación como último paso.
		validate(mod200);
	}

	private void assertEquals(Mod2002016 mod200, Mod2002016Key key, double expected) {
		double keyValue = AonMathUtils.round( mod200.getVariable(key).getValue());
		System.out.println(
				 AonStringUtils.leftPad(AonNumberUtils.toString(keyValue), 9)
				+" (" + AonStringUtils.leftPad(AonNumberUtils.toString(expected), 9) + ")" 
				+" " + key.toString()
				+" " + AonStringUtils.abbreviate( key.getDescription(),100));
		Assert.assertEquals(key.getDescription(), expected, keyValue);
	}

	private void calculate(Mod2002016 mod200) {
		System.out.println( "\n\t-- calculate\n" );
		FISCAL.calculateMod2002016(mod200);
	}
	
	private void validate(Mod2002016 mod200) {
		System.out.println( "\n-- validate\n" );
		FISCAL.validateMod2002016(mod200);
		if (mod200.getMessages() != null && mod200.getMessages().size() > 0 ) {
			System.out.println(
					 AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 101)
					 +AonStringUtils.repeat("=", 51)
					 +AonStringUtils.CR_LF
					 +AonStringUtils.center("BOX", 6)
					 +AonStringUtils.center("PAG", 6)
					 +AonStringUtils.rightPad("MENSAJE", 101)
					 +AonStringUtils.right("EXPRESION", 51)
					 +AonStringUtils.CR_LF
					 +AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 101)
					 +AonStringUtils.repeat("=", 51)
			);
			for (ValidationMessage2016 msg : mod200.getMessages()) {
				System.out.println(
						 AonStringUtils.center(msg.getKey()!=null?msg.getKey().getCode():"", 6)
						+AonStringUtils.center(AonNumberUtils.toString(msg.getPage()), 6)
						+AonStringUtils.rightPad(AonStringUtils.abbreviate(msg.getMessage(), 100),101)
						+AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.trim(msg.getExpression()), 50),51)
						);
			}
			System.out.println(
					 AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 6)
					 +AonStringUtils.repeat("=", 101)
					 +AonStringUtils.repeat("=", 51)
			);
		} else {
			System.out.println( "Sin mensajes de validacion");
		}
		
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

	private double valueChanged(Mod2002016 mod200,Mod2002016Key key) {
		double value = AonNumberUtils.todouble( AonRandomStringUtils.randomNumeric(5));
		value = (AonMathUtils.isZero(value))?value:AonMathUtils.round( value  / 100 );
		DoubleVariable2016 oldVar = mod200.getKey(key);
		if (oldVar == null) {
			oldVar = new DoubleVariable2016(key);
		}
		DoubleVariable2016 newVar = oldVar.clone();
		newVar.setValue( value );
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		System.out.println(AonStringUtils.leftPad(AonNumberUtils.toString(value), 9)
				+" " + key.toString()
				+" " + key.getDescription());
		return value;
	}
	
	
	// [*] Se crea un modelo 200 - 2016. Se inicializa los tipos de balances.
	//	   Se llama al método initializeMod2002016 para que cree la casillas adecuadas. 
	private static Mod2002016 createNormal() {
		System.out.println( "\n-- create\n" );
		Mod2002016 mod200 = FISCAL.createMod2002016(DOMAIN_NAME, DOMAIN_ID, LOGIN, 2016);
		mod200.setBalanceType(BalanceType.NORMAL);
		mod200.setPygType(BalanceType.NORMAL);

		System.out.println( "\n-- initialize\n" );
		mod200 = FISCAL.initializeMod2002016(DOMAIN_NAME, DOMAIN_ID, LOGIN, mod200);
		return mod200;
	}
	
}
