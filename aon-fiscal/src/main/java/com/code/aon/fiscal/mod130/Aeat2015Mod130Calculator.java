package com.code.aon.fiscal.mod130;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2015Mod130Calculator extends FiscalModelDetailCalculator implements IMod130Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2015 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod130 mod130) throws AonException {
		calculateDetails(mod130.getDetails());
		double c01 = mod130.getDetail( Mod130Key.C01 ).getAmount();
		double c02 = mod130.getDetail( Mod130Key.C02 ).getAmount();

		double c03 = CommonUtil.round(c01 - c02);
		mod130.getDetail( Mod130Key.C03 ).setAmount( c03 );
		
		// Casilla 04. Consigne el resultado de aplicar el porcentaje del 20 por 100 sobre 
		// el importe positivo de la casilla 03. En caso de que el importe de la casilla 
		// 03 sea negativo, consigne el número cero en la casilla 04.
		double c04 =  c03>0?CommonUtil.round(c03 * 20 / 100):0;
		mod130.getDetail( Mod130Key.C04 ).setAmount( c04 );
		
		double c05 = mod130.getDetail( Mod130Key.C05 ).getAmount();
		double c06 = mod130.getDetail( Mod130Key.C06 ).getAmount();
		
		// Casilla 07. Consigne el resultado de efectuar la operación indicada en el impreso. 
		// Si se obtuviera una cantidad negativa, consígnela precedida del signo menos (-).
		double c07 =  CommonUtil.round(c04 - c05 - c06);
		
		mod130.getDetail( Mod130Key.C07 ).setAmount( c07 );
		double c08 = mod130.getDetail( Mod130Key.C08 ).getAmount();
		
		// Casilla 09. Se hará constar en esta casilla la cantidad resultante de aplicar el 
		// porcentaje del 2 por 100 sobre el importe reflejado en la casilla 08 anterior.
		// Tratándose de actividades agrícolas, ganaderas, forestales o pesqueras con derecho 
		// a la deducción por rentas obtenidas en Ceuta o Melilla a que se refiere el artículo 
		// 68.4 de la Ley del Impuesto, el porcentaje aplicable será el 1 por 100. En caso de 
		// que, además, se desarrollen otras actividades agrícolas, ganaderas, forestales o 
		// pesqueras que no tengan derecho a dicha deducción, el porcentaje aplicable respecto 
		// de estas últimas será el 2 por 100.
		double c09 =  CommonUtil.round(c08 * 2 / 100);
		mod130.getDetail( Mod130Key.C09 ).setAmount( c09 );
		double c10 = mod130.getDetail( Mod130Key.C10 ).getAmount();

		// Casilla 11. Consigne en esta casilla el resultado de efectuar la operación 
		// indicada en el impreso de declaración. De obtenerse una cantidad negativa, 
		// consígnela precedida del signo menos (-).
		double c11 =  CommonUtil.round(c08 + c09 + c10);
		mod130.getDetail( Mod130Key.C11 ).setAmount( c11 );
		
		// Casilla 12. En esta casilla se reflejará el resultado de sumar, teniendo 
		// en cuenta sus respectivos signos, los importes consignados en las casillas 
		// 07 y 11. De obtenerse una cantidad negativa, consigne el número cero (0).
		// No obstante, el contribuyente podrá consignar en esta casilla una cantidad 
		// superior a la que resulte de dicha suma, como consecuencia de la aplicación 
		// de porcentajes superiores a los indicados, de conformidad con lo establecido 
		// en el artículo 110.4 del Reglamento del Impuesto.
		double c12 =  CommonUtil.round(c07 + c11);
		c12 =  c12>0?c12:0;
		mod130.getDetail( Mod130Key.C12 ).setAmount( c12 );
		
		FiscalModelDetail detC13 = mod130.getDetail( Mod130Key.C131 );
		double c13 = 0;
		if (detC13 != null) {
			c13 = detC13.getAmount();
		}
		
		// Casilla 14. Se anotará en esta casilla el resultado de efectuar la operación 
		// indicada en el impreso. De obtenerse una cantidad negativa, se hará constar 
		// con signo menos (-).
		double c14 =  CommonUtil.round(c12 - c13);
		
		mod130.getDetail( Mod130Key.C14 ).setAmount( c14 );
		double c15 = mod130.getDetail( Mod130Key.C15 ).getAmount();
		double c16 = mod130.getDetail( Mod130Key.C16 ).getAmount();
		if (c14>0) {
			// TODO
		} else {
			c15 = 0;
			c16 = 0;
		}
		mod130.getDetail( Mod130Key.C15 ).setAmount( c15 );
		mod130.getDetail( Mod130Key.C16 ).setAmount( c16 );
		
		double c17 =  CommonUtil.round(c14 -c15 - c16);
		mod130.getDetail( Mod130Key.C17 ).setAmount( c17 );
		
		double c18 =  mod130.getDetail( Mod130Key.C18 ).getAmount();
		double c19 =  CommonUtil.round(c17 -c18);
		mod130.getDetail( Mod130Key.C19 ).setAmount( c19 );
		
	}

	@Override
	public double getResult(Mod130 mod130) {
		return mod130.getDetail( Mod130Key.C19 ).getAmount( );
	}

}
