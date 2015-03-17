package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.watson.util.IntegerStringPair;

public enum FiscalActivityInfoKey implements Serializable{

	A01 {
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"-")
				,IntegerStringPair.of(1,"Segundo Trimestre")
				,IntegerStringPair.of(2,"Tercer Trimestre")
				,IntegerStringPair.of(3,"Cuarto Trimestre")
				};
		
		@Override
		public String getDescription() {
			return "Trimestre de inicio de la actividad.";
		}

		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A02 {
		@Override
		public String getDescription() {
			return "Comunidad, Sociedad Civil o Similar. Porcentaje de participaci\u00F3n.";
		}
	}
	, A03{

		@Override
		public String getDescription() {
			return "Actividad de Temporada. n\u00AA de dias de ejercicio en el a\u00F1o anterior.";
		}
	} 
	, A04{

		@Override
		public String getDescription() {
			return "Nuevas actividades iniciadas a partir del 1 de enero del a\u00F1o anterior. A\u00F1o de inicio.";
		}
	} 
	, A05{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};

		@Override
		public String getDescription() {
			return "Deducci\u00F3n por rentas obtenidas en Ceuta y Melilla.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	} 
	, B06{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(1,"Una batea y ning\u00FAn barco.")
				,IntegerStringPair.of(2,"Una batea y un barco de menos de 15 TRB.")
				,IntegerStringPair.of(3,"Una batea y un barco de 15 a 30 TRB")
				,IntegerStringPair.of(4,"Una batea y un barco de m\u00E1s de 30 TRB")
				,IntegerStringPair.of(5,"Dos bateas y ning\u00FAn barco")
				,IntegerStringPair.of(6,"Dos bateas y un barco de menos de 15 TRB.")
				,IntegerStringPair.of(7,"Otros: numero de bateas, barcos o TRB distintos de los anteriores.")
				};
		
		@Override
		public String getDescription() {
			return "N\u00FAmero de bateas y de barcos auxiliares de la empresa.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A06{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};
		
		@Override
		public String getDescription() {
			return "Ejerce la actividad en un s\u00F3lo local o sin \u00E9l.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A07{

		@Override
		public String getDescription() {
			return "N\u00FAmero de veh\u00EDculos afectos de la actividad.";
		}
	} 
	, A08{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};
		
		@Override
		public String getDescription() {
			return "Capacidad de carga del veh\u00EDculo superior a 1000 Kg.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A09{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				IntegerStringPair.of(1,"Hasta 2.000 habitantes.")
				,IntegerStringPair.of(2,"Desde 2.001 hasta 5.000 habitantes.")
				,IntegerStringPair.of(3,"Desde 5.001 hasta 10.000 habitantes.")
				,IntegerStringPair.of(4,"Desde 10.001 hasta 50.000 habitantes.")
				,IntegerStringPair.of(5,"Desde 50.001 hasta 100.000 habitantes.")
				,IntegerStringPair.of(6,"M\u00E1s de 100.000 habitantes.")
				,IntegerStringPair.of(7,"Madrid o Barcelona.")
				};
		
		@Override
		public String getDescription() {
			return "Municipio donde se ejerce la actividad.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A10{

		@Override
		public String getDescription() {
			return "N\u00FAmero de empleados al inicio de ejercicio (o al inicio de la actividad).";
		}
	} 
	
	, C10{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};
		
		@Override
		public String getDescription() {
			return "Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, C11{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};
		
		@Override
		public String getDescription() {
			return "Indique si la actividad se realiza con un \u00FAnico tractocami\u00F3n y sin semirremolques.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A11{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"-")
				,IntegerStringPair.of(1,"Actividad relizada exclusivamente en Lorca.")
				,IntegerStringPair.of(2,"Actividad realizada en Lorca y otros municipios.")
				};
		
		@Override
		public String getDescription() {
			return "Si en 2014 realiza la actividad en LORCA, seleccione:";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, A13{
		private IntegerStringPair[] CHOICES = new IntegerStringPair[]{
				 IntegerStringPair.of(0,"NO")
				,IntegerStringPair.of(1,"SI")
				};
		
		@Override
		public String getDescription() {
			return "Indique si el titular es discapacitado en grado igual o superior al 33%";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
	}
	, M01 {
		@Override 
		public String getDescription() { 
			return "PERSONAL ASALARIADO";
		}
	}
	, M011 {
		@Override
		public String getDescription() {
			return "Mayores de 19 a\u00F1os";
		}
	}
	, M012 {
		@Override
		public String getDescription() {
			return "Menores de 19 a\u00F1os y trabajadores con contratos de aprendizaje o formaci\u00F3n, que no sean discapacitados.";
		}
	}
	, M013 {
		@Override
		public String getDescription() {
			return "Discapacitados con grado de minusval\u00EDa igual o superior al 33 por 100";
		}
	}
	, M014 {
		@Override
		public String getDescription() {
			return "Horas anuales";
		}
	}
	, M02 {
		@Override
		public String getDescription() {
			return "PERSONAL NO ASALARIADO";
		}
	}
	, M021 {
		@Override
		public String getDescription() {
			return "Horas anuales del titular. (m\u00E1ximo 1.800 horas)";
		}
	}
	, M022 {
		@Override
		public String getDescription() {
			return "Horas anuales del c\u00F3nyuge. (m\u00E1ximo 1.800 horas)";
		}
	}
	, M023 {
		@Override
		public String getDescription() {
			return "Indique si el c\u00F3nyuge es discapacitado en grado igual o superior al 33%";
		}
	}
	, M024 {
		@Override
		public String getDescription() {
			return "Horas anuales de los hijos menores de 18 a\u00F1os.";
		}
	}
	, M025 {
		@Override
		public String getDescription() {
			return "Horas anuales de los hijos menores de 18 a\u00F1os con discapacidad en grado igual o superior al 33%";
		}
	}
	, M03 {
		@Override
		public String getDescription() {
			return "CONSUMO DE ENERGIA EL\u00C9CTRICA";
		}
	}
	, M04 {
		@Override
		public String getDescription() {
			return "MESAS";
		}
	}
	, M05 {
		@Override
		public String getDescription() {
			return "LONGITUD DE BARRA";
		}
	}
	, M06 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINAS TIPO A";
		}
	}
	, M07 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINAS TIPO B";
		}
	}
	, M08 {
		@Override
		public String getDescription() {
			return "POTENCIA EL\u00C9CTRICA";
		}
	}
	, M09 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL LOCAL";
		}
	}
	, M10 {
		@Override
		public String getDescription() {
			return "SUPERFICIE LOCAL INDEPENDIENTE";
		}
	}
	, M11 {
		@Override
		public String getDescription() {
			return "SUPERFICIE LOCAL NO INDEPENDIENTE";
		}
	}
	, M12 {
		@Override
		public String getDescription() {
			return "DISTANCIA RECORRIDA";
		}
	}
	, M13 {
		@Override
		public String getDescription() {
			return "CARGA DE ELEMENTOS DE TRANSPORTE";
		}
	}
	, M14 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL HORNO";
		}
	}
	, M15 {
		@Override
		public String getDescription() {
			return "PERSONAL ASALARIADO DE FABRICACI\u00D3N";
		}
	}
	, M16 {
		@Override
		public String getDescription() {
			return "RESTO PERSONAL ASALARIADO";
		}
	}
	, M17 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL LOCAL DE FABRICACI\u00D3N";
		}
	}
	, M18 {
		@Override
		public String getDescription() {
			return "RESTO SUPERFICIE DEL LOCAL INDEPENDIENTE";
		}
	}
	, M19 {
		@Override
		public String getDescription() {
			return "RESTO SUPERFICIE DEL LOCAL NO INDEPENDIENTE";
		}
	}
	, M20 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE PLAZAS";
		}
	}
	, M21 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE ASIENTOS";
		}
	}
	, M22 {
		@Override
		public String getDescription() {
			return "NO SE USA";
		}
	}
	, M23 {
		@Override
		public String getDescription() {
			return "CARGA VEH\u00CDCULOS (TM)";
		}
	}
	, M24 {
		@Override
		public String getDescription() {
			return "POTENCIA FISCAL DEL VEH\u00CDCULO";
		}
	}
	, M25 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE VEH\u00CDCULOS";
		}
	}
	, M26 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO";
		}
	}
	, M27 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO DE REPARACI\u00D3N";
		}
	}
	, M28 {
		@Override
		public String getDescription() {
			return "SUPERFICIE TALLER DE REPARACI\u00D3N";
		}
	}
	, M29 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE CARGA DE VEH\u00CDCULOS";
		}
	}
	, M30 {
		@Override
		public String getDescription() {
			return "POTENCIA INSTALADA";
		}
	}
	, M31 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DEL HORNO";
		}
	}
	, M32 {
		@Override
		public String getDescription() {
			return "VOLUMEN DE LOS HORNOS";
		}
	}
	, M33 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE PRODUCCI\u00D3N DE LOS HORNOS";
		}
	}
	, M34 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE MOLIDO";
		}
	}
	, M35 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE PRENSADO";
		}
	}
	, M36 {
		@Override
		public String getDescription() {
			return "CAPACIDAD CUBAS DE COAGULACI\u00D3N";
		}
	}
	, M37 {
		@Override
		public String getDescription() {
			return "VOLUMEN DE BOMBOS DE TUESTE";
		}
	}
	, M38 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE DEP\u00D3SITOS Y CUBAS";
		}
	}
	, M39 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE TELARES";
		}
	}
	, M40 {
		@Override
		public String getDescription() {
			return "MAQUINARIA";
		}
	}
	, M41 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS";
		}
	}
	, M42 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE LOS BOMBOS";
		}
	}
	, M43 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS DE COSER";
		}
	}
	, M44 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS DE ENCUADERNAR";
		}
	}
	, M45 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINA DE REVELAR";
		}
	}
	, M46 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE ALOJAMIENTO";
		}
	}
	, M47 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL RECINTO";
		}
	}
	, M48 {
		@Override
		public String getDescription() {
			return "CAPACIDAD EN PLAZAS";
		}
	}
	, M49 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL VIVERO";
		}
	}
	, M50 {
		@Override
		public String getDescription() {
			return "AFORO DEL LOCAL";
		}
	}
	, M51 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE MESAS Y APARATOS DE JUEGO";
		}
	}
	, M52 {
		@Override
		public String getDescription() {
			return "\u00CDNDICE DE RENDIMIENTO NETO";
		}
	}
	, M53 {
		@Override
		public String getDescription() {
			return "ID. DE CUOTA DEVENGADA POR OPER.CORRIENTES";
		}
	}
	, M54 {
		@Override
		public String getDescription() {
			return "IMPORTE DE LAS COMISIONES POR LOTERIAS";
		}
	}
	, M55 {
		@Override
		public String getDescription() {
			return "PORCENTAJE CUOTA DEVENGADA OP. CORRIENTES";
		}
	}
	, M56 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO ASADO DE POLLOS";
		}
	}
	, M57 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DEL ASADOR";
		}
	}
	, M58 {
		@Override
		public String getDescription() {
			return "IMPORTE TOTAL DE LAS COMISIONES";
		}
	}
	, M59 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO (RESIDUOS)";
		}
	}
	, M60 {
		@Override
		public String getDescription() {
			return "CARGA VEH\u00CDCULOS (RESIDUOS)";
		}
	}
	, M61 {
		@Override
		public String getDescription() {
			return "BATEAS";
		}
	}	
	,I01 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO NETO PREVIO";
		}
	}
	,I02{
		@Override
		public String getDescription() {
			return "Incentivos al empleo";
		}
	}
	,I03 {
		@Override
		public String getDescription() {
			return "Incentivos a la inversión";
		}
	}
	,I04 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO NETO MINORADO";
		}
	}
	,I05 {
		@Override
		public String getDescription() {
			return "ÍNDICES CORRECTORES";
		}
	}

	,I06 {
		@Override
		public String getDescription() {
			return "\t\t1. Especiales";
		}
	}
	,I07 {
		@Override
		public String getDescription() {
			return "\t\t2. Empresas de peque\u00F1a dimensi\u00F3n";
		}
	}

	,I08{
		@Override
		public String getDescription() {
			return "\t\t3. De temporada";
		}
	}
	,I09 {
		@Override
		public String getDescription() {
			return "\t\t4. De exceso";
		}
	}
	,I10 {
		@Override
		public String getDescription() {
			return "\t\t5. De inicio de nueva actividad";
		}
	}
	,I11 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)";
		}
	}
	,I12{
		@Override
		public String getDescription() {
			return "Reducción para actividades económicas realizadas en el término municipal de Lorca";
		}
	}
	,I13 {
		@Override
		public String getDescription() {
			return "Rendimientos a efectos de pagos fraccionados después de la reducción";
		}
	}
	,I14 {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	}
	,I15 {
		@Override
		public String getDescription() {
			return "Resultado. Pago Trimestral de I.R.P.F.";
		}
	}
	,V01 {
		@Override
		public String getDescription() {
			return "Índice corrector de temporada";
		}
	} 
	,V02 {
		@Override
		public String getDescription() {
			return "Cuota anual devengada por operaciones corrientes";
		}
	} 
	,V03 {
		@Override
		public String getDescription() {
			return "Reducción aplicable por actividades económicas realizadas en el término municipal de Lorca";
		}
	} 
	,V04 {
		@Override
		public String getDescription() {
			return "Cuota anual por operaciones corrientes después de las reducciones anteriores";
		}
	} 
	,V05 {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
	} 
	,V06 {
		@Override
		public String getDescription() {
			return "INGRESO A CUENTA POR OPERACIONES CORRIENTES PREVIO";
		}
	} 
	;
	

	public String getKey() {
		return toString();
	}
	public IntegerStringPair[] getOptions() {
		return null;
	}
	public int getFromYear() {
		return 2012;
	}
	public int getToYear() {
		return 9999;
	}
	public boolean isChoice() {
		return getOptions() != null && getOptions().length > 0;
	}
	
	public abstract String getDescription();
	
	public static FiscalActivityInfoKey safeValueOf(String value) {
		if (value == null) return null;
		return FiscalActivityInfoKey.valueOf(value);
	}
}
/*

M01 { @Override public String getDescription() {}
}
("M01",FiscalActivityInfoType.MODULE,2012,9999,true,Double.class,false,false,false,"0",null,null),
M011 ("M011",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
M012 ("M012",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
M013 ("M013",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
M014 ("M014",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"1800",FiscalActivityInfoKey.M01,null),
M02 ("M02",FiscalActivityInfoType.MODULE,2012,9999,true,Double.class,false,false,false,"0",null,null),
M021 ("M021",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
M022 ("M022",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
M023 ("M023",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02
	,new FiscalActivityInfoKeyEntry[] {
		new FiscalActivityInfoKeyEntry(0,"NO"),
		new FiscalActivityInfoKeyEntry(1,"SI")
	}),
M024("M024",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
M025("M025",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
M03 ("M03",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M04 ("M04",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M05 ("M05",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M06 ("M06",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M07 ("M07",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M08 ("M08",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M09 ("M09",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M10 ("M10",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M11 ("M11",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M12 ("M12",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M13 ("M13",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M14 ("M14",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M15 ("M15",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M16 ("M16",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M17 ("M17",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M18 ("M18",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M19 ("M19",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M20 ("M20",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M21 ("M21",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M22 ("M22",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M23 ("M23",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M24 ("M24",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M25 ("M25",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M26 ("M26",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M27 ("M27",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M28 ("M28",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M29 ("M29",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M30 ("M30",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M31 ("M31",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M32 ("M32",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M33 ("M33",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M34 ("M34",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M35 ("M35",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M36 ("M36",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M37 ("M37",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M38 ("M38",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M39 ("M39",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M40 ("M40",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M41 ("M41",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M42 ("M42",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M43 ("M43",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M44 ("M44",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M45 ("M45",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M46 ("M46",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M47 ("M47",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M48 ("M48",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M49 ("M49",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M50 ("M50",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M51 ("M51",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M52 ("M52",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M53 ("M53",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M54 ("M54",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M55 ("M55",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M56 ("M56",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M57 ("M57",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M58 ("M58",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M59 ("M59",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M60 ("M60",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
M61 ("M61",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
*/
