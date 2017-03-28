package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Map;

import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod130MVELContext extends ModelMVELContext implements Map<String, Object> {

	public static final double C16_MAX_VALUE = 660.14;

	// ********************************************************** CASILLA 16
	/**
	 * <b>Casilla 16</b><br/><br/>
	 * Si en la casilla 14 se hubiera obtenido una cantidad
	 * positiva y el contribuyente está realizando pagos por préstamos
	 * destinados a la adquisición o rehabilitación de su vivienda habitual, se
	 * hará constar, en su caso, en la casilla 16 el importe de la deducción a
	 * que se refiere el artículo 110.3.d) del Reglamento del Impuesto. <br/><br/>
	 * Si únicamente se hubiese cumplimentado el apartado I de este modelo, dicha
	 * deducción está constituida por el importe resultante de aplicar el
	 * porcentaje del 2 por 100 sobre la cantidad consignada en la casilla 03,
	 * con el límite máximo de 660,14 euros para cada trimestre. <br/><br/>
	 * Si solamente se hubiese cumplimentado el apartado II, la deducción está 
	 * constituida por el importe resultante de aplicar el porcentaje del 2 por 
	 * 100 sobre la cantidad consignada en la casilla 08. En este caso, el límite 
	 * máximo de deducción por este concepto será de 660,14 euros anuales. <br/><br/>  
	 * En cualquier caso, deberá tenerse en cuenta que el importe consignado en la 
	 * casilla 16 no podrá ser superior a la diferencia positiva entre las casillas 
	 * 14 y 15 anteriores.
	 * 
	 * <b>Atención:</b> esta deducción no resultará aplicable si los pagos realizados 
	 * se destinan a la construcción o ampliación de la vivienda habitual o a cuentas 
	 * vivienda, ni tampoco en los siguientes supuestos:<ul>
	 * <li> Cuando el contribuyente realice simultáneamente actividades agrícolas, 
	 * ganaderas, forestales o pesqueras y actividades distintas de éstas.</li>
	 * <li>Cuando el contribuyente también esté obligado a presentar el modelo 131 para 
	 * declarar el pago fraccionado correspondiente a las actividades económicas en 
	 * estimación objetiva que realice, como sucede, entre otros, en el supuesto a que 
	 * se refiere el segundo párrafo del artículo 35 del Reglamento del Impuesto.</li>
	 * <li>Cuando, además de realizar actividades económicas, el contribuyente perciba 
	 * rendimientos del trabajo y haya presentado al pagador de los mismos el modelo 145 
	 * comunicando la circunstancia de que está realizando pagos por préstamos destinados 
	 * a la adquisición o rehabilitación de su vivienda habitual.</li>
	 * <li>Tratándose de actividades distintas de las agrícolas, ganaderas, forestales o 
	 * pesqueras, cuando los ingresos íntegros anuales previsibles correspondientes a las 
	 * mismas sean iguales o superiores a la cantidad de 33.007,20 euros. A estos efectos, 
	 * se considerarán como ingresos íntegros anuales previsibles los que resulten de 
	 * elevar al año los ingresos íntegros correspondientes al primer trimestre del ejercicio 
	 * o, en su caso, al primer trimestre de inicio de la actividad.</li>
	 * <li>En las actividades agrícolas, ganaderas, forestales o pesqueras, cuando el 
	 * volumen de ingresos anuales previsibles correspondiente a las mismas sea igual o 
	 * superior a la cantidad de 33.007,20 euros. A estos efectos, se considerará como volumen 
	 * de ingresos anuales previsibles el que resulte de elevar al año el volumen de ingresos 
	 * del primer trimestre del ejercicio o, en su caso, del primer trimestre de inicio de 
	 * la actividad, excluidas en todo caso las subvenciones de capital y las indemnizaciones.</li>
	 * </ul>
	 * 
	 * @return el valor
	 * @throws AonCoreException
	 */
	public double computeC16() throws AonCoreException {
		Double p2 = (Double) get(Mod130Key.P2.toString());
		if (p2 != null && p2 == 1) {
			double c03 = (Double) get(Mod130Key.C03.toString());
			double c08 = (Double) get(Mod130Key.C08.toString());
			if (isNotZero(c03) && isNotZero(c08)) {
				return 0.0;
			}
			double cXX = c08 > c03 ? c08 : c03;
			cXX = round(cXX * 2 / 100);
			double c14 = (Double) get(Mod130Key.C14.toString());
			double c15 = (Double) get(Mod130Key.C15.toString());
			cXX = (cXX > (c14 - c15)?(c14 - c15):cXX);
			return cXX > C16_MAX_VALUE ? C16_MAX_VALUE : cXX;
		}
		return 0.0;
	}


}
