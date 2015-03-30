package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Mod131Key implements Serializable {

	@Deprecated
	ACH1("131-ACH1") {
		@Override
		public String getDescription() {
			return "I. Actividades económicas en estimación objetiva distintas de las agrícolas, ganaderas y forestales.";
		}
	},
	AC11("131-AC11") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
	},
	AC12("131-AC12") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
	},
	AC13("131-AC13") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	},
	AC14("131-AC14") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
	},
	AC21("131-AC21") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
	},
	AC22("131-AC22") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
	},
	AC23("131-AC23") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	},
	AC24("131-AC24") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
	},
	AC31("131-AC31") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
	},
	AC32("131-AC32") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
	},
	AC33("131-AC33") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	},
	AC34("131-AC34") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
	},
	AC41("131-AC41") {
		@Override
		public String getDescription() {
			return "Actividad (epigrafe IAE)";
		}
	},
	AC42("131-AC42") {
		@Override
		public String getDescription() {
			return "Rendimiento neto de la actividad a efectos del pago fraccionado";
		}
	},
	AC43("131-AC43") {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	},
	AC44("131-AC44") {
		@Override
		public String getDescription() {
			return "Resultado de aplicar el porcentaje correspondiente a cada actividad";
		}
	},
	@Deprecated
	H2("131-H2") {
		@Override
		public String getDescription() {
			return "II. Actividades económicas en estimación objetiva distintas de las agrícolas, ganaderas y forestales, sin posibilidad de determinar ninguno de los datos-base a efectos del pago fraccionado.";
		}
	},
	C03("131-03") {
		@Override
		public String getDescription() {
			return "03 - Volumen de ventas o  ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)";
		}
	},
	C04("131-04") {
		@Override
		public String getDescription() {
			return "04 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 03.";
		}
	},
	@Deprecated
	H3("131-H3") {
		@Override
		public String getDescription() {
			return "III. Actividades agrícolas, ganaderas y forestales, en estimación objetiva.";
		}
	},
	C05("131-05") {
		@Override
		public String getDescription() {
			return "05 - Volumen de ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)";
		}
	},
	C06("131-06") {
		@Override
		public String getDescription() {
			return "06 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 05.";
		}
	},
	@Deprecated
	H4("131-H4") {
		@Override
		public String getDescription() {
			return "IV. Total liquidación.";
		}
	},
	C07("131-07") {
		@Override
		public String getDescription() {
			return "07 - Suma de los pagos fraccionados previos de trimestre (02 + 04 + 06) ";
		}
	},
	C08("131-08") {
		@Override
		public String getDescription() {
			return "08 - A deducir. Retenciones e ingresos a cuenta soportados correspondientes al trimestre. ";
		}
	},
	C09("131-09") {
		@Override
		public String getDescription() {
			return "09 - A deducir. Minoración  por aplicación de la deducción a que se refiere el artículo 80 bis de la ley de Impuesto.";
		}
	},
	C10("131-10") {
		@Override
		public String getDescription() {
			return "10 - Diferencia.";
		}
	},
	C11("131-11") {
		@Override
		public String getDescription() {
			return "11 - Resultados negativos de trimestres anteriores.  ";
		}
	},
	C12("131-12") {
		@Override
		public String getDescription() {
			return "12 - Por destinar cantidades al pago de préstamos por adquisición o rehabilitación de vivienda habitual.";
		}
	},
	C13("131-13") {
		@Override
		public String getDescription() {
			return "13 - Total.";
		}
	},
	C14("131-14") {
		@Override
		public String getDescription() {
			return "14 - A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo.";
		}
	},
	C15("131-15") {
		@Override
		public String getDescription() {
			return "15 - Resultado de la declaración.";
		}
	};
	
	//,AC51("131-AC51"){ 
	//,AC52("131-AC52"){ 
	//,AC53("131-AC53"){ 
//	,AC54("131-AC54"){ 
//	,AC01("131-AC01"){ @Override public String getDescription() { "01 - Suma de rendimientos netos"; }}
//	,AC02("131-AC02"){ @Override public String getDescription() { "02 - Pago fraccionado previo del trimestre. Suma de resultados."; }}
//		@Override public String getDescription() { 
//		@Override public String getDescription() {
	
	private String value;
	
	private Mod131Key(String value) {
    	this.value = value;
    }
	
	public abstract String getDescription();
    
	public String getValue() {
		return value;
	}
}