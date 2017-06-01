package com.esferalia.aon.occam.jooq.test.mod200;


import java.io.IOException;
import java.sql.SQLException;
import java.util.EnumMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Behaviour;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ547Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.ValidationMessage2016;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod200Page13Test {

	private static AONContext ctx;
	
	// 	[*] Aquí debes poner el nombre del dominio de tu base de datos
	private static String DOMAIN_NAME = "felixlocal-dsi.aonsolutions.dev";
	// 	[*] Aquí debes poner el ID del dominio de tu base de datos
	private static int DOMAIN_ID = 5208;
	// 	[*] Aquí debes poner el login de un usuario con permisos.
	private static String LOGIN = "felix";
	
	private static Mod2002016 mod200;
	
	// 	[*] Antes de comenzar el test, se carga el driver JDBC y se conecta a la base de datos.
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}

	// Al finalizar todos los test se liberan recursos inicializados en BeforeClass
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
	// 	[*] JUNIT instancia esta clase cada vez que se ejecuta un método marcado con @Test
	//		Este método se ejecuta antes de generar la instancia.
	@Before
	public void beforeInstance() throws ClassNotFoundException, SQLException, AonConnectionException {
		System.out.println( "\n-- FIRST OF ALL\n" );
		mod200 = createNormal();		
	}

	// Despues de cada test se realiza una validación del modelo, para comprobar que mensajes reporta
	@After
	public void testValidation() throws IOException {
		validate(mod200);
	}
	
	// Correcciones al resultado contable
	@Test 
	public void testCorrecciones() throws IOException {
		
		System.out.println( "\n-- CORRECCIONES AL RESULTADO CONTABLE --\n" );
		
		// [*] Damos un valor aleatorio a todas las casillas que permiten introducción manual.
		System.out.println( "\n\t-- random initialize\n" );
		
		// [*] El mapa "expected" simula los datos introducidos en pantalla.
		
		// Cumplimentamos las casillas de correcciones contables 
		EnumMap<Mod2002016Key,Double> expected = new EnumMap<Mod2002016Key,Double>(Mod2002016Key.class);
		for (Mod2002016CorrectionKey key : Mod2002016CorrectionKey.values()  ) {
			
			Boolean[] behaviour;
			
			// Casilla aumento
			if (key.isIncreaseEnabled())
			{
				behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key.getIncrease().toString());
				if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
					expected.put(key.getIncrease(), valueChanged(mod200,key.getIncrease()));		
				}
			}
			
			// Casilla disminucion
			if (key.isDecreaseEnabled())
			{
				behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key.getDecrease().toString());
				if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
					expected.put(key.getDecrease(), valueChanged(mod200,key.getDecrease()));		
				}
			}
			
			// Mostrar la linea de correcciones en consola
			System.out.println(					
					AonStringUtils.rightPad(key.isIncreaseEnabled()?"[" + key.getIncrease().toString() + "]":"", 7) +
					AonStringUtils.leftPad(key.isIncreaseEnabled()?AonNumberUtils.toString(expected.get(key.getIncrease())):"", 8)+"   "+
					AonStringUtils.rightPad(key.isDecreaseEnabled()?"[" + key.getDecrease().toString() + "]":"", 7) +
					AonStringUtils.leftPad(key.isDecreaseEnabled()?AonNumberUtils.toString(expected.get(key.getDecrease())):"", 8)+
			        "  -  " + key.getDescription()
					);			
		}
		
		// [*] Se calcula el modelo, las casillas que son calculadas, deben toman valor.
		calculate(mod200);

		// Poner las casillas calculadas segun el valor esperado, para compararlas despues
		
		// Total Aumentos
		double I0417 = AonMathUtils.round(
								expected.get(Mod2002016Key.I0355)+
								expected.get(Mod2002016Key.I0357)+
								expected.get(Mod2002016Key.I0359)+
								expected.get(Mod2002016Key.I0225)+
								expected.get(Mod2002016Key.I1514)+
								expected.get(Mod2002016Key.I0361)+
								expected.get(Mod2002016Key.I0303)+
								expected.get(Mod2002016Key.I1005)+
								expected.get(Mod2002016Key.I0305)+
								expected.get(Mod2002016Key.I0307)+
								expected.get(Mod2002016Key.I1003)+
								expected.get(Mod2002016Key.I0309)+
								expected.get(Mod2002016Key.I0514)+
								expected.get(Mod2002016Key.I0516)+
								expected.get(Mod2002016Key.I0321)+
								expected.get(Mod2002016Key.I0415)+
								expected.get(Mod2002016Key.I0331)+
								expected.get(Mod2002016Key.I0325)+
								expected.get(Mod2002016Key.I1518)+
								expected.get(Mod2002016Key.I0333)+
								expected.get(Mod2002016Key.I0327)+
								expected.get(Mod2002016Key.I0416)+
								expected.get(Mod2002016Key.I0335)+
								expected.get(Mod2002016Key.I0337)+
								expected.get(Mod2002016Key.I0339)+
								expected.get(Mod2002016Key.I0341)+
								expected.get(Mod2002016Key.I0508)+
								expected.get(Mod2002016Key.I1009)+
								expected.get(Mod2002016Key.I0343)+
								expected.get(Mod2002016Key.I0363)+
								expected.get(Mod2002016Key.I0345)+
								expected.get(Mod2002016Key.I0371)+
								expected.get(Mod2002016Key.I0347)+
								expected.get(Mod2002016Key.I1011)+
								expected.get(Mod2002016Key.I1013)+
								expected.get(Mod2002016Key.I1015)+
								expected.get(Mod2002016Key.I0369)+
								expected.get(Mod2002016Key.I0256)+
								expected.get(Mod2002016Key.I0373)+
								expected.get(Mod2002016Key.I0340)+
								expected.get(Mod2002016Key.I0351)+
								expected.get(Mod2002016Key.I0375)+
								expected.get(Mod2002016Key.I1320)+
								expected.get(Mod2002016Key.I0184)+
								expected.get(Mod2002016Key.I1022)+
								expected.get(Mod2002016Key.I1018)+
								expected.get(Mod2002016Key.I1275)+
								expected.get(Mod2002016Key.I0377)+
								expected.get(Mod2002016Key.I0379)+
								expected.get(Mod2002016Key.I0381)+
								expected.get(Mod2002016Key.I0383)+
								expected.get(Mod2002016Key.I0387)+
								expected.get(Mod2002016Key.I0311)+
								expected.get(Mod2002016Key.I0313)+
								expected.get(Mod2002016Key.I0323)+
								expected.get(Mod2002016Key.I0317)+
								expected.get(Mod2002016Key.I0385)+
								expected.get(Mod2002016Key.I0389)+
								expected.get(Mod2002016Key.I0397)+
								expected.get(Mod2002016Key.I0250)+
								expected.get(Mod2002016Key.I0391)+
								expected.get(Mod2002016Key.I0403)+
								expected.get(Mod2002016Key.I0518)+
								expected.get(Mod2002016Key.I0510)+
								expected.get(Mod2002016Key.I0329)+
								expected.get(Mod2002016Key.I0365)+
								expected.get(Mod2002016Key.I0409)+
								expected.get(Mod2002016Key.I0411)+
								expected.get(Mod2002016Key.I1027)+								
								expected.get(Mod2002016Key.I0413));

		// Total Disminuciones
		double D0418 = AonMathUtils.round(		
								expected.get(Mod2002016Key.D0356)+
								expected.get(Mod2002016Key.D0358)+
								expected.get(Mod2002016Key.D0360)+
								expected.get(Mod2002016Key.D0226)+
								expected.get(Mod2002016Key.D0272)+
								expected.get(Mod2002016Key.D0362)+
								expected.get(Mod2002016Key.D0304)+
								expected.get(Mod2002016Key.D0505)+
								expected.get(Mod2002016Key.D1006)+
								expected.get(Mod2002016Key.D0306)+
								expected.get(Mod2002016Key.D0308)+
								expected.get(Mod2002016Key.D1004)+
								expected.get(Mod2002016Key.D0310)+
								expected.get(Mod2002016Key.D0509)+
								expected.get(Mod2002016Key.D0551)+
								expected.get(Mod2002016Key.D0322)+
								expected.get(Mod2002016Key.D0211)+
								expected.get(Mod2002016Key.D0332)+
								expected.get(Mod2002016Key.D0326)+
								expected.get(Mod2002016Key.D0394)+
								expected.get(Mod2002016Key.D0334)+
								expected.get(Mod2002016Key.D0328)+
								expected.get(Mod2002016Key.D0543)+
								expected.get(Mod2002016Key.D0336)+
								expected.get(Mod2002016Key.D0338)+
								expected.get(Mod2002016Key.D0368)+
								expected.get(Mod2002016Key.D0342)+
								expected.get(Mod2002016Key.D1010)+
								expected.get(Mod2002016Key.D0364)+
								expected.get(Mod2002016Key.D0346)+
								expected.get(Mod2002016Key.D0348)+
								expected.get(Mod2002016Key.D1012)+
								expected.get(Mod2002016Key.D1014)+
								expected.get(Mod2002016Key.D1016)+
								expected.get(Mod2002016Key.D0370)+
								expected.get(Mod2002016Key.D0278)+
								expected.get(Mod2002016Key.D0372)+
								expected.get(Mod2002016Key.D0374)+
								expected.get(Mod2002016Key.D1589)+
								expected.get(Mod2002016Key.D0376)+
								expected.get(Mod2002016Key.D1321)+
								expected.get(Mod2002016Key.D0544)+
								expected.get(Mod2002016Key.D1023)+
								expected.get(Mod2002016Key.D1019)+
								expected.get(Mod2002016Key.D1276)+
								expected.get(Mod2002016Key.D0378)+
								expected.get(Mod2002016Key.D0380)+
								expected.get(Mod2002016Key.D0382)+
								expected.get(Mod2002016Key.D0384)+
								expected.get(Mod2002016Key.D0388)+
								expected.get(Mod2002016Key.D0312)+
								expected.get(Mod2002016Key.D0314)+
								expected.get(Mod2002016Key.D0324)+
								expected.get(Mod2002016Key.D0318)+
								expected.get(Mod2002016Key.D0386)+
								expected.get(Mod2002016Key.D0390)+
								expected.get(Mod2002016Key.D0396)+
								expected.get(Mod2002016Key.D0398)+
								expected.get(Mod2002016Key.D0251)+
								expected.get(Mod2002016Key.D0392)+
								expected.get(Mod2002016Key.D0400)+
								expected.get(Mod2002016Key.D0404)+
								expected.get(Mod2002016Key.D0519)+
								expected.get(Mod2002016Key.D0512)+
								expected.get(Mod2002016Key.D0330)+
								expected.get(Mod2002016Key.D1026)+
								expected.get(Mod2002016Key.D0410)+
								expected.get(Mod2002016Key.D0412)+
								expected.get(Mod2002016Key.D1028)+
								expected.get(Mod2002016Key.D0414));
		
		// Base imponible antes de reserva y compensacion bases negativas (no hay datos en entidades navieras ni consolidacion fiscal, ni correcciones referidas al grupo fiscal)
		// para ver que está teniendo en cuenta los aumentos y disminuciones  
		// 550 = 501 + 1230 - 1231 + 417 - 418
		double LQ550 = AonMathUtils.round(
				 mod200.getVariable(Mod2002016Key.LQ501).getValue()+
				 I0417-
				 D0418);
		
		// [*] A partir de aquí vienen las comprobaciones.
		// 		Se toman los valores de expected, se realiza la 
		//		operación pertinente, que debe coincidir con la casilla correspondiente del modelo. 
		//
		assertEquals(mod200,Mod2002016Key.I0417, I0417);
		assertEquals(mod200,Mod2002016Key.D0418, D0418);		
		assertEquals(mod200,Mod2002016Key.LQ550, LQ550);
		
	}
	
	// Desglose casilla 547 - Detalle de la compensación de bases imponibles negativas
	@Test
	public void testDesgloseCasilla547() throws IOException {
		
		System.out.println( "\n-- DETALLE DE LA COMPENSACIÓN DE BASES IMPONIBLES NEGATIVAS --\n" );
		
		// [*] Damos un valor aleatorio a todas las casillas que permiten introducción manual.
		System.out.println( "\n\t-- random initialize\n" );
		
		// [*] El mapa "expected" simula los datos introducidos en pantalla.
		
		// Cumplimentamos las casillas de correcciones contables 
		EnumMap<Mod2002016Key,Double> expected = new EnumMap<Mod2002016Key,Double>(Mod2002016Key.class);
		for (Mod2002016LQ547Key key : Mod2002016LQ547Key.values()  ) {
			
			Boolean[] behaviour;
			
			// Casilla Pendiente de Aplicacion a principio del periodo
			behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key.getPreviousPendind().toString());
			if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
				expected.put(key.getPreviousPendind(), valueChanged(mod200,key.getPreviousPendind()));		
			}
			
			// Casilla Aplicado en esta declaración
			if (key.getCurrent()!=null) {
				behaviour = Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP.get(key.getCurrent().toString());
				if (  behaviour == null || (behaviour != null && !behaviour[1]) ) {
					expected.put(key.getCurrent(), valueChanged(mod200,key.getCurrent()));		
				}
			}
			
			// Mostrar la linea de correcciones en consola
			System.out.println(					
					AonStringUtils.rightPad("[" + key.getPreviousPendind().toString() + "]", 8) +
					AonStringUtils.leftPad(AonNumberUtils.toString(expected.get(key.getPreviousPendind())), 8)+"   "+
					AonStringUtils.rightPad(key.getCurrent()==null?"":"[" + key.getCurrent().toString() + "]", 8) +
					AonStringUtils.leftPad(key.getCurrent()==null?"":AonNumberUtils.toString(expected.get(key.getCurrent())), 8)+
			        "  -  " + key.getDescription()
					);			
		}
		
		// [*] Se calcula el modelo, las casillas que son calculadas, deben toman valor.
		calculate(mod200);
		
		// Poner las casillas calculadas segun el valor esperado, para compararlas despues
		
		// Total Pendiente al inicio
		double LQ670 = AonMathUtils.round(
								expected.get(Mod2002016Key.LQ640 )+
								expected.get(Mod2002016Key.LQ643 )+
								expected.get(Mod2002016Key.LQ646 )+
								expected.get(Mod2002016Key.LQ649 )+
								expected.get(Mod2002016Key.LQ652 )+
								expected.get(Mod2002016Key.LQ655 )+
								expected.get(Mod2002016Key.LQ658 )+
								expected.get(Mod2002016Key.LQ661 )+
								expected.get(Mod2002016Key.LQ664 )+
								expected.get(Mod2002016Key.LQ667 )+
								expected.get(Mod2002016Key.LQ743 )+
								expected.get(Mod2002016Key.LQ275 )+
								expected.get(Mod2002016Key.LQ608 )+
								expected.get(Mod2002016Key.LQ704 )+
								expected.get(Mod2002016Key.LQ013 )+
								expected.get(Mod2002016Key.LQ725 )+
								expected.get(Mod2002016Key.LQ534 )+
								expected.get(Mod2002016Key.LQ607 )+
								expected.get(Mod2002016Key.LQ1045)+
								expected.get(Mod2002016Key.LQ1519));
		
        // Total Aplicado en esta liquidación
		double LQ547 = AonMathUtils.round(
								expected.get(Mod2002016Key.LQ641 )+
								expected.get(Mod2002016Key.LQ644 )+
								expected.get(Mod2002016Key.LQ647 )+
								expected.get(Mod2002016Key.LQ650 )+
								expected.get(Mod2002016Key.LQ653 )+
								expected.get(Mod2002016Key.LQ656 )+
								expected.get(Mod2002016Key.LQ659 )+
								expected.get(Mod2002016Key.LQ662 )+
								expected.get(Mod2002016Key.LQ665 )+
								expected.get(Mod2002016Key.LQ668 )+
								expected.get(Mod2002016Key.LQ747 )+
								expected.get(Mod2002016Key.LQ276 )+
								expected.get(Mod2002016Key.LQ609 )+
								expected.get(Mod2002016Key.LQ705 )+
								expected.get(Mod2002016Key.LQ014 )+
								expected.get(Mod2002016Key.LQ726 )+
								expected.get(Mod2002016Key.LQ535 )+
								expected.get(Mod2002016Key.LQ675 )+
								expected.get(Mod2002016Key.LQ1046)+
								expected.get(Mod2002016Key.LQ1520));
	      
		// Pendiente futuro
		double LQ671 = AonMathUtils.round(
								expected.get(Mod2002016Key.LQ640 )-expected.get(Mod2002016Key.LQ641 )+ 
								expected.get(Mod2002016Key.LQ643 )-expected.get(Mod2002016Key.LQ644 )+ 
								expected.get(Mod2002016Key.LQ646 )-expected.get(Mod2002016Key.LQ647 )+ 
								expected.get(Mod2002016Key.LQ649 )-expected.get(Mod2002016Key.LQ650 )+ 
								expected.get(Mod2002016Key.LQ652 )-expected.get(Mod2002016Key.LQ653 )+ 
								expected.get(Mod2002016Key.LQ655 )-expected.get(Mod2002016Key.LQ656 )+ 
								expected.get(Mod2002016Key.LQ658 )-expected.get(Mod2002016Key.LQ659 )+ 
								expected.get(Mod2002016Key.LQ661 )-expected.get(Mod2002016Key.LQ662 )+ 
								expected.get(Mod2002016Key.LQ664 )-expected.get(Mod2002016Key.LQ665 )+ 
								expected.get(Mod2002016Key.LQ667 )-expected.get(Mod2002016Key.LQ668 )+ 
								expected.get(Mod2002016Key.LQ743 )-expected.get(Mod2002016Key.LQ747 )+ 
								expected.get(Mod2002016Key.LQ275 )-expected.get(Mod2002016Key.LQ276 )+ 
								expected.get(Mod2002016Key.LQ608 )-expected.get(Mod2002016Key.LQ609 )+ 
								expected.get(Mod2002016Key.LQ704 )-expected.get(Mod2002016Key.LQ705 )+ 
								expected.get(Mod2002016Key.LQ013 )-expected.get(Mod2002016Key.LQ014 )+ 
								expected.get(Mod2002016Key.LQ725 )-expected.get(Mod2002016Key.LQ726 )+ 
								expected.get(Mod2002016Key.LQ534 )-expected.get(Mod2002016Key.LQ535 )+ 
								expected.get(Mod2002016Key.LQ607 )-expected.get(Mod2002016Key.LQ675 )+ 
								expected.get(Mod2002016Key.LQ1045)-expected.get(Mod2002016Key.LQ1046)+ 
								expected.get(Mod2002016Key.LQ1519)-expected.get(Mod2002016Key.LQ1520));
	
		// [*] A partir de aquí vienen las comprobaciones.
		// 		Se toman los valores de expected, se realiza la 
		//		operación pertinente, que debe coincidir con la casilla correspondiente del modelo. 
		//
		for (Mod2002016LQ547Key key : Mod2002016LQ547Key.values()  ) {  // Diferencia pendiente inicio - aplicado para cada linea
			if (key.getCurrent() != null)
				assertEquals(mod200,key.getFuturePendind(), AonMathUtils.round(expected.get(key.getPreviousPendind())-expected.get(key.getCurrent())) );
		}
		assertEquals(mod200,Mod2002016Key.LQ670, LQ670);  // Total pendiente inicio
		assertEquals(mod200,Mod2002016Key.LQ547, LQ547);  // Total aplicado en esta liquidacion
		assertEquals(mod200,Mod2002016Key.LQ671, LQ671);  // Total pendiente futuro
		
	}
	
	// --- METODOS AUXILIARES ---

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
						 AonStringUtils.rightPad((msg.getKey()==null?"null":msg.getKey().toString()), 6)
						+AonStringUtils.leftPad(AonNumberUtils.toString(msg.getPage()), 4)
						+"  "+AonStringUtils.rightPad(AonStringUtils.abbreviate(msg.getMessage(), 100),101)
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
//		System.out.println(AonStringUtils.leftPad(AonNumberUtils.toString(value), 9)
//				+" " + key.toString()
//				+" " + key.getDescription());
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
