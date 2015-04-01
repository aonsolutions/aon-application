package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;

public enum Mod131Key implements Serializable {

	@Deprecated
	ACH1("131-ACH1") {
		@Override
		public String getDescription() {
			return "I. Actividades económicas en estimación objetiva distintas de las agrícolas, ganaderas y forestales.";
		}

		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
	},
	AC11("131-AC11") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}

		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(0).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
	},
	AC12("131-AC12") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(0).setNetYield(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC13("131-AC13") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(0).setPercent(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC14("131-AC14") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(0).setResult(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC21("131-AC21") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
//			mod131.getActivity(1).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
	},
	AC22("131-AC22") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(1).setNetYield(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC23("131-AC23") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(1).setPercent(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC24("131-AC24") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(1).setResult(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC31("131-AC31") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(2).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
	},
	AC32("131-AC32") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(2).setNetYield(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC33("131-AC33") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(2).setPercent(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC34("131-AC34") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(2).setResult(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC41("131-AC41") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(3).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
	},
	AC42("131-AC42") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(3).setNetYield(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC43("131-AC43") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(3).setPercent(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC44("131-AC44") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(3).setResult(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC51("131-AC51") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(4).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
	},
	AC52("131-AC52") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(4).setNetYield(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC53("131-AC53") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(4).setPercent(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC54("131-AC54") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(4).setResult(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC01("131-AC01"){ 
		@Override 
		public String getDescription() { 
			return "01 - Suma de rendimientos netos"; 
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC01(fm.getMap().get(getValue()).getAmount());
		}
	},
	AC02("131-AC02"){ 
		@Override 
		public String getDescription() { 
			return  "02 - Pago fraccionado previo del trimestre. Suma de resultados."; 
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC02(fm.getMap().get(getValue()).getAmount());
		}
	},
	@Deprecated
	H2("131-H2") {
		@Override
		public String getDescription() {
			return "II. Actividades económicas en estimación objetiva distintas de las agrícolas, ganaderas y forestales, sin posibilidad de determinar ninguno de los datos-base a efectos del pago fraccionado.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
	},
	C03("131-03") {
		@Override
		public String getDescription() {
			return "03 - Volumen de ventas o  ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC03(fm.getMap().get(getValue()).getAmount());
		}
	},
	C04("131-04") {
		@Override
		public String getDescription() {
			return "04 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 03.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC04(fm.getMap().get(getValue()).getAmount());
		}
	},
	@Deprecated
	H3("131-H3") {
		@Override
		public String getDescription() {
			return "III. Actividades agrícolas, ganaderas y forestales, en estimación objetiva.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
	},
	C05("131-05") {
		@Override
		public String getDescription() {
			return "05 - Volumen de ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC05(fm.getMap().get(getValue()).getAmount());
		}
	},
	C06("131-06") {
		@Override
		public String getDescription() {
			return "06 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 05.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC06(fm.getMap().get(getValue()).getAmount());
		}
	},
	@Deprecated
	H4("131-H4") {
		@Override
		public String getDescription() {
			return "IV. Total liquidación.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
	},
	C07("131-07") {
		@Override
		public String getDescription() {
			return "07 - Suma de los pagos fraccionados previos de trimestre (02 + 04 + 06) ";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC07(fm.getMap().get(getValue()).getAmount());
		}
	},
	C08("131-08") {
		@Override
		public String getDescription() {
			return "08 - A deducir. Retenciones e ingresos a cuenta soportados correspondientes al trimestre. ";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC08(fm.getMap().get(getValue()).getAmount());
		}
	},
	C09("131-09") {
		@Override
		public String getDescription() {
			return "09 - A deducir. Minoración  por aplicación de la deducción a que se refiere el artículo 80 bis de la ley de Impuesto.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC09(fm.getMap().get(getValue()).getAmount());
		}
	},
	C10("131-10") {
		@Override
		public String getDescription() {
			return "10 - Diferencia.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC10(fm.getMap().get(getValue()).getAmount());
		}
	},
	C11("131-11") {
		@Override
		public String getDescription() {
			return "11 - Resultados negativos de trimestres anteriores.  ";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC11(fm.getMap().get(getValue()).getAmount());
		}
	},
	C12("131-12") {
		@Override
		public String getDescription() {
			return "12 - Por destinar cantidades al pago de préstamos por adquisición o rehabilitación de vivienda habitual.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC12(fm.getMap().get(getValue()).getAmount());
		}
	},
	C13("131-13") {
		@Override
		public String getDescription() {
			return "13 - Total.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC13(fm.getMap().get(getValue()).getAmount());
		}
	},
	C14("131-14") {
		@Override
		public String getDescription() {
			return "14 - A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC14(fm.getMap().get(getValue()).getAmount());
		}
	},
	C15("131-15") {
		@Override
		public String getDescription() {
			return "15 - Resultado de la declaración.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC15(fm.getMap().get(getValue()).getAmount());
		}
	};
	
	
	private String value;
	
	private Mod131Key(String value) {
    	this.value = value;
    }
	
	public abstract String getDescription();
	public abstract void fill(FiscalModel fm, Mod131 mod131);
    
	public String getValue() {
		return value;
	}
	
}