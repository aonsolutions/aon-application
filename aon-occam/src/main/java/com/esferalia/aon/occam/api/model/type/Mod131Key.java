package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;

public enum Mod131Key implements Serializable {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	ACH1("131-ACH1") {
		@Override
		public String getDescription() {
			return "I. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(0).getNetYield();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(0).getPercent();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(0).getResult();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
		}
	},
	AC21("131-AC21") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.getActivity(1).setEpigraph(fm.getMap().get(getValue()).getDescription());
		}
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(1).getNetYield();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(1).getPercent();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(1).getResult();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(2).getNetYield();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(2).getPercent();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(2).getResult();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(3).getNetYield();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(3).getPercent();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(3).getResult();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(4).getNetYield();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(4).getPercent();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getActivity(4).getResult();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC01();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC01(value);
		}
		@Override
		public String getBox() {
			return "01";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC02();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC02(value);
		}
		@Override
		public String getBox() {
			return "02";
		}
	},
	H2("131-H2") {
		@Override
		public String getDescription() {
			return "II. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales, sin posibilidad de determinar ninguno de los datos-base a efectos del pago fraccionado.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC03();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC03(value);
		}
		@Override
		public String getBox() {
			return "03";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC04();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC04(value);
		}
		@Override
		public String getBox() {
			return "04";
		}
	},
	H3("131-H3") {
		@Override
		public String getDescription() {
			return "III. Actividades agr\u00EDcolas, ganaderas y forestales, en estimaci\u00F3n objetiva.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC05();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC05(value);
		}
		@Override
		public String getBox() {
			return "05";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC06();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC06(value);
		}
		@Override
		public String getBox() {
			return "06";
		}
	},
	H4("131-H4") {
		@Override
		public String getDescription() {
			return "IV. Total liquidaci\u00F3n.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
		}
		@Override
		public double getValue(Mod131 mod131) {
			return 0;
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC07();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC07(value);
		}
		@Override
		public String getBox() {
			return "07";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC08();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC08(value);
		}
		@Override
		public String getBox() {
			return "08";
		}
	},
	C09("131-09") {
		@Override
		public String getDescription() {
			return "09 - A deducir. Minoraci\u00F3n  por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 80 bis de la ley de Impuesto.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC09(fm.getMap().get(getValue()).getAmount());
		}
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC09();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC09(value);
		}
		@Override
		public String getBox() {
			return "09";
		}
	},
	C091("131-091") {
		@Override
		public String getDescription() {
			return "09 - A deducir. Minoraci\u00F3n por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 110.3 C) del reglamento del impuesto.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC09(fm.getMap().get(getValue()).getAmount());
		}
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC09();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC09(value);
		}
		@Override
		public String getBox() {
			return "09";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC10();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC10(value);
		}
		@Override
		public String getBox() {
			return "10";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC11();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC11(value);
		}
		@Override
		public String getBox() {
			return "11";
		}
	},
	C12("131-12") {
		@Override
		public String getDescription() {
			return "12 - Por destinar cantidades al pago de pr\u00E9stamos por adquisici\u00F3n o rehabilitaci\u00F3n de vivienda habitual.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC12(fm.getMap().get(getValue()).getAmount());
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC12(value);
		}
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC12();
		}
		@Override
		public String getBox() {
			return "12";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC13();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC13(value);
		}
		@Override
		public String getBox() {
			return "13";
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
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC14();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC14(value);
		}
		@Override
		public String getBox() {
			return "14";
		}
	},
	C15("131-15") {
		@Override
		public String getDescription() {
			return "15 - Resultado de la declaraci\u00F3n.";
		}
		@Override
		public void fill(FiscalModel fm, Mod131 mod131) {
			mod131.setC15(fm.getMap().get(getValue()).getAmount());
		}
		@Override
		public double getValue(Mod131 mod131) {
			return mod131.getC15();
		}
		@Override
		public void setValue(Mod131 mod131, Double value) {
			mod131.setC15(value);
		}
		@Override
		public String getBox() {
			return "15";
		}
	};
	
	
	private String value;
	
	private Mod131Key(String value) {
    	this.value = value;
    }
	
	public abstract String getDescription();
	public abstract void fill(FiscalModel fm, Mod131 mod131);
	public abstract double getValue(Mod131 mod131);
	public abstract void setValue(Mod131 mod131, Double value);
	
	public String getBox() {
		return null;
	}

	public String getValue() {
		return value;
	}

//	public String getBox() {
//		return null;
//	}
	
}