package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.ModelMVELContext;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod131MVELContext extends ModelMVELContext implements Map<String, Object> {
	
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");
	protected final AONContext ctx;
	protected final Mod131 mod131;
	protected final LinkedList<Mod131Key> keys = new LinkedList<>();
	private static final double LIM_C12_1 = 660.14;
	private static final double LIM_C12_2 = 33007.20;
	
	public Mod131MVELContext(final AONContext ctx, final Mod131 mod131) {
		this.ctx = ctx;
		this.mod131 = mod131;
		for (String keyValue : mod131.getMap().keySet()) {
			Mod131Key mod131Key = Mod131Key.getKey(keyValue);
			if (mod131Key != null) {
				FiscalModelDetail detail = mod131.getMap().get(keyValue);
				put(mod131Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
	}

	public String format(Double amount) {
		return DEC2.format(amount);
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public double computeC01() {
		return round(
			AonCollectionUtils.stream( mod131.getActivities() )
				.mapToDouble( act -> act.getRdr())
				.sum());
	}
	
	public double computeC02() {
		double c02Sum = round( 
			AonCollectionUtils.stream( mod131.getActivities() )
				.mapToDouble( act -> act.getRes())
				.sum());
		return AonMathUtils.isLessThanZero( c02Sum ) ? 0.0 : round(c02Sum); 
	}
	
	/**
	 * Casilla 11. Si en la casilla 10 anterior se hubiera obtenido una cantidad positiva, 
	 * se hará constar en la casilla 11 el importe (sin signo) de los resultados negativos 
	 * que, en su caso, se hubieran obtenido en la casilla 15 de cualquiera de las 
	 * autoliquidaciones anteriores, modelo 131, del mismo ejercicio y que no hubieran 
	 * sido deducidos anteriormente, teniendo en cuenta que en ningún caso podrá figurar 
	 * en la casilla 11 un importe superior a la cantidad positiva consignada en la casilla 10.
	 * 
	 * @return
	 */
	public double computeC11() {
		double c10 = mod131.getAmount(Mod131Key.C10);
		
		// Si la cantidad ha sido editado por pantalla
		if (AonMathUtils.isNotZero(mod131.getAdjustAmount(Mod131Key.C11))) {
			double c11 = mod131.getAmount(Mod131Key.C11);
			return c11>c10?c10:c11;	
		}
		
		// Si la cantidad NO ha sido editado por pantalla
		double ret = 0.0; 
		if (c10 > 0) {
			MutableDouble c11Sum = new MutableDouble(0.0);
			MutableDouble c15Sum = new MutableDouble(0.0);
			Mod131DAO.getPreviousModels(ctx, mod131)
				.forEach( fm -> {
					// Cantidades deducidas
					c11Sum.add( fm.getAmount(Mod131Key.C11) );
					// Resultado negativos
					double c15 = fm.getDeclarationResult();
					if ( AonMathUtils.isLessThanZero(c15) ) {
						c15Sum.add( fm.getDeclarationResult() );
				}
			});
			double total = AonMathUtils.round(c15Sum.doubleValue() - c11Sum.doubleValue());
			if (AonMathUtils.isLessThanZero(total)) {
				// Si el importe "total" es negativo. Quiere decir que hay importes deducibles 
				// por lo que en la presente declaación se podrá deducir.
				total = AonMathUtils.absRounded(total); 
			} else {
				// Si el importe "total" es positivo. Quiere decir que ya no hay nada por deducir.
				total = 0.0;
			}
			// La cantidad a deducir ("total") no podrá ser mayor qu la casilla 10.
			ret = total>c10?c10:total;
		}
		return ret;
	}

	public double computeC12() {
		double c12 = 0.0;
		// Si en la casilla 10 se hubiera obtenido una cantidad positiva y el contribuyente 
		// está realizando pagos por préstamos destinados a la adquisición o rehabilitación 
		// de su vivienda habitual por los que vaya a tener derecho a la deducción por 
		// inversión en vivienda habitual regulada en la disposición transitoria decimoctava 
		// de la Ley del Impuesto, se hará constar, en su caso, en la casilla 12 el importe 
		// de la deducción a que se refiere el artículo 110.3.d) del Reglamento del Impuesto.
		double p2 = mod131.getAmount(Mod131Key.P2);
		double c10 = mod131.getAmount(Mod131Key.C10);
		if (p2 > 0 && c10 > 0) {
			double c01 = mod131.getAmount(Mod131Key.C01);
			double c03 = mod131.getAmount(Mod131Key.C03);
			double c05 = mod131.getAmount(Mod131Key.C05);

			// Atención: esta deducción no resultará aplicable si los pagos 
			// realizados se destinan a la construcción o ampliación de la 
			// vivienda habitual, ni tampoco, de acuerdo con lo dispuesto 
			// en el art. 110.3.d), último párrafo Reglamento IRPF, en los 
			// siguientes supuestos:
			boolean apply = true;
			
			// Atención: esta deducción no resultará aplicable: Cuando el contribuyente 
			// realice simultáneamente actividades agrícolas, ganaderas o forestales y 
			// actividades distintas de éstas.
			if (AonMathUtils.isNotZero(c05) && AonMathUtils.isNotZero(c01 + c03) ) {
				apply = false;	
			}
			
			// Tratándose de actividades distintas de las agrícolas, ganaderas o forestales, cuando el conjunto 
			// de la suma de rendimientos netos a efectos del pago fraccionado consignada en la casilla 01 y 
			// del volumen de ventas o ingresos anuales previsibles correspondiente a las actividades comprendidas 
			// en el apartado II de este modelo, sea igual o superior a la cantidad de 33.007,20 euros. A estos 
			// efectos, se considerará como volumen de ventas o ingresos anuales previsibles el que resulte de 
			// elevar al año el volumen de ventas o ingresos del primer trimestre, excluidas las subvenciones 
			// de capital y las indemnizaciones.			
			if (apply && AonMathUtils.isZero(c05) && AonMathUtils.isNotZero(c01 + c03)) {
				if (AonMathUtils.isGreatherThan(c01, LIM_C12_2)) {
					apply = false;
				} else {
					double c03Year;
					if (mod131.getPeriod() == Period.T1) {
						c03Year = AonMathUtils.round(c03 * 4);
					} else {
						double c03T1 = Mod131DAO.getPreviousModels(ctx, mod131)
							.filter( fm -> fm.getPeriod() == Period.T1)
							.mapToDouble( fm -> fm.getAmount(Mod131Key.C03))
							.findFirst()
							.orElse(0.0);
						c03Year = AonMathUtils.round(c03T1 * 4);
					}
					if (AonMathUtils.isGreatherThan(c03Year, LIM_C12_2)) {
						apply = false;	
					}
				}	
			}
			
			// En el caso de actividades agrícolas, ganaderas o forestales, cuando el 
			// volumen de ingresos anuales previsibles correspondiente a las mismas, 
			// sea igual o superior a la cantidad de 33.007,20 euros. A estos efectos, 
			// se considerará como volumen de ingresos anuales previsibles el que 
			// resulte de elevar al año el volumen de ingresos del primer trimestre, 
			// excluidas las subvenciones de capital y las indemnizaciones.
			if (apply && AonMathUtils.isNotZero(c05) && AonMathUtils.isZero(c01 + c03)) {
				if (AonMathUtils.isGreatherThan(c05, LIM_C12_2)) {
					apply = false;
				} else {
					double c05Year;
					if (mod131.getPeriod() == Period.T1) {
						c05Year = AonMathUtils.round(c05 * 4);
					} else {
						double c05T1 = Mod131DAO.getPreviousModels(ctx, mod131)
							.filter( fm -> fm.getPeriod() == Period.T1)
							.mapToDouble( fm -> fm.getAmount(Mod131Key.C05))
							.findFirst()
							.orElse(0.0);
						c05Year = AonMathUtils.round(c05T1 * 4);
					}
					if (AonMathUtils.isGreatherThan(c05Year, LIM_C12_2)) {
						apply = false;	
					}
				}	
			}
			
			if (apply) {
				// Si solamente se hubiese cumplimentado el apartado "III", la deducción 
				// está constituida por el importe resultante de aplicar el porcentaje del 
				// 2 por 100 sobre la cantidad consignada en la casilla 05.
				if (c05 != 0) {
					c12 = c05 * 2 / 100;
				}
				// Si únicamente se hubiese cumplimentado el apartado "I" y/o el apartado "II" 
				// de este modelo, dicha deducción está constituida por la suma de los importes 
				// resultantes de aplicar el porcentaje del 0,5 por 100 sobre la cantidad 
				// consignada en la casilla 01 y el porcentaje del 2 por 100 sobre la cantidad 
				// consignada en la casilla 03.
				double c11 = mod131.getAmount(Mod131Key.C11);
				if (c01 != 0 || c03 != 0) {
					c12 = (c01 * 0.5 / 100) + (c03 * 2 / 100);
				}
				
				// En cualquier caso, deberá tenerse en cuenta, también, que el importe 
				// consignado en la casilla 12 no podrá ser superior a la diferencia 
				// positiva entre las casillas 10 y 11 anteriores, 
				double positiveDif = AonMathUtils.zeroIfNegative(c10 - c11); 
				c12 = c12 > positiveDif?positiveDif:c12;
				
				// y que la deducción por 
				// este concepto tiene como límite máximo la cantidad de 660,14 euros 
				// anuales, por lo que en la casilla 12 no podrá figurar en ningún caso 
				// un importe superior a dicha cantidad, sin que tampoco pueda superar 
				// la citada cantidad (660,14 euros anuales) el sumatorio resultante de 
				// los importes consignados en las casillas 12 de los cuatro modelos 131 
				// del mismo ejercicio.
				double c12Sum = Mod131DAO.getPreviousModels(ctx, mod131)
					.mapToDouble( fm -> fm.getAmount(Mod131Key.C12))
					.sum();
				if (c12Sum >= LIM_C12_1) {
					c12 = 0.0;
				}
				double c12Year = c12 + c12Sum;
				if ( c12Year > LIM_C12_1) {
					c12 = LIM_C12_1 - c12Sum;
				}
			}
		}
		return round(c12);
	}
	
	
}
