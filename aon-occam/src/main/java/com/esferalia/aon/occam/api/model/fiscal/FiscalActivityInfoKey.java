package com.esferalia.aon.occam.api.model.fiscal;

import static com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType.INFO;
import static com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType.IRPF_INFO;
import static com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType.MODULE;
import static com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType.VAT_INFO;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.IntegerStringPair;

public enum FiscalActivityInfoKey implements Serializable {

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

		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
	}
	, A02 {
		@Override
		public String getDescription() {
			return "Comunidad, Sociedad Civil o Similar. Porcentaje de participaci\u00F3n.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
		@Override
		public String getDefaultValue() {
			return AonStringUtils.EMPTY;
		}
	}
	, A03{

		@Override
		public String getDescription() {
			return "Actividad de Temporada. n\u00AA de dias de ejercicio en el a\u00F1o anterior.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
		@Override
		public String getDefaultValue() {
			return AonStringUtils.EMPTY;
		}
	} 
	, A04{

		@Override
		public String getDescription() {
			return "Nuevas actividades iniciadas a partir del 1 de enero del a\u00F1o anterior. A\u00F1o de inicio.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
		@Override
		public String getDefaultValue() {
			return AonStringUtils.EMPTY;
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
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
				,IntegerStringPair.of(7,"Otros: Distintos de los anteriores.")
				};
		
		@Override
		public String getDescription() {
			return "N\u00FAmero de bateas y de barcos auxiliares de la empresa.";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& AonStringUtils.equals(fa.getEpigraph(),Epigraph.E____.getEpigraph());
		}
		@Override
		public String getDefaultValue() {
			return AonStringUtils.ONE;
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& (!AonStringUtils.equals( Epigraph.E_721_1.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_2.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_3.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_722A.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_757.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E____.getEpigraph(), fa.getEpigraph()));
		}
	}
	, A07{

		@Override
		public String getDescription() {
			return "N\u00FAmero de veh\u00EDculos afectos de la actividad.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& (!AonStringUtils.equals( Epigraph.E_721_2.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E____.getEpigraph(), fa.getEpigraph()));
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& (!AonStringUtils.equals( Epigraph.E_721_1.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_2.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_3.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_722A.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_757.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E____.getEpigraph(), fa.getEpigraph()));
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& (!AonStringUtils.equals( Epigraph.E_721_1.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_3.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_722A.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_757.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E____.getEpigraph(), fa.getEpigraph()));
		}
		@Override
		public String getDefaultValue() {
			return AonStringUtils.SIX;
		}
	}
	, A10{

		@Override
		public String getDescription() {
			return "N\u00FAmero de empleados al inicio de ejercicio (o al inicio de la actividad).";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& (!AonStringUtils.equals( Epigraph.E_721_1.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_2.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_721_3.getEpigraph(), fa.getEpigraph())
				&& !AonStringUtils.equals( Epigraph.E_722A.getEpigraph(), fa.getEpigraph())
					&& !AonStringUtils.equals( Epigraph.E_757.getEpigraph(), fa.getEpigraph())
					&& !AonStringUtils.equals( Epigraph.E____.getEpigraph(), fa.getEpigraph()));
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& AonStringUtils.equals(fa.getEpigraph(),Epigraph.E_722A.getEpigraph())
				|| AonStringUtils.equals(fa.getEpigraph(),Epigraph.E_757.getEpigraph());
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}

		@Override
		public boolean accept(Epigraph epigraph,FiscalActivity fa) {
			return super.accept(epigraph,fa) 
				&& AonStringUtils.equals(fa.getEpigraph(),Epigraph.E_722A.getEpigraph())
				|| AonStringUtils.equals(fa.getEpigraph(),Epigraph.E_757.getEpigraph());
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
			return "Si realiza la actividad en LORCA, seleccione:";
		}
		
		@Override
		public IntegerStringPair[] getOptions() {
			return CHOICES;
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
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
		@Override
		public FiscalActivityInfoKeyType getType() {
			return INFO;
		}
	}
	, M01 {
		@Override 
		public String getDescription() { 
			return "PERSONAL ASALARIADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
		@Override
		public FiscalActivityInfoKey[] getDetailKeys() {
			return new FiscalActivityInfoKey[]{
				 FiscalActivityInfoKey.M011,FiscalActivityInfoKey.M012
				,FiscalActivityInfoKey.M013,FiscalActivityInfoKey.M014};
		}
	}
	, M011 {
		@Override
		public String getDescription() {
			return "Mayores de 19 a\u00F1os";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M012 {
		@Override
		public String getDescription() {
			return "Menores de 19 a\u00F1os y trabajadores con contratos de aprendizaje o formaci\u00F3n, que no sean discapacitados.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M013 {
		@Override
		public String getDescription() {
			return "Discapacitados con grado de minusval\u00EDa igual o superior al 33 por 100";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M014 {
		@Override
		public String getDescription() {
			return "Horas anuales";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
		@Override
		public String getDefaultValue() {
			return "1800";
		}
		
	}
	, M02 {
		@Override
		public String getDescription() {
			return "PERSONAL NO ASALARIADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
		@Override
		public FiscalActivityInfoKey[] getDetailKeys() {
			return new FiscalActivityInfoKey[]{FiscalActivityInfoKey.M021
				,FiscalActivityInfoKey.M022,FiscalActivityInfoKey.M023
				,FiscalActivityInfoKey.M024,FiscalActivityInfoKey.M025};
		}
	}
	, M021 {
		@Override
		public String getDescription() {
			return "Horas anuales del titular. (m\u00E1ximo 1.800 horas)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M022 {
		@Override
		public String getDescription() {
			return "Horas anuales del c\u00F3nyuge. (m\u00E1ximo 1.800 horas)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M023 {
		@Override
		public String getDescription() {
			return "Indique si el c\u00F3nyuge es discapacitado en grado igual o superior al 33%";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M024 {
		@Override
		public String getDescription() {
			return "Horas anuales de los hijos menores de 18 a\u00F1os.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M025 {
		@Override
		public String getDescription() {
			return "Horas anuales de los hijos menores de 18 a\u00F1os con discapacidad en grado igual o superior al 33%";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M03 {
		@Override
		public String getDescription() {
			return "CONSUMO DE ENERGIA EL\u00C9CTRICA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M04 {
		@Override
		public String getDescription() {
			return "MESAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M05 {
		@Override
		public String getDescription() {
			return "LONGITUD DE BARRA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M06 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINAS TIPO A";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M07 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINAS TIPO B";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M08 {
		@Override
		public String getDescription() {
			return "POTENCIA EL\u00C9CTRICA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M09 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL LOCAL";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M10 {
		@Override
		public String getDescription() {
			return "SUPERFICIE LOCAL INDEPENDIENTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M11 {
		@Override
		public String getDescription() {
			return "SUPERFICIE LOCAL NO INDEPENDIENTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M12 {
		@Override
		public String getDescription() {
			return "DISTANCIA RECORRIDA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M13 {
		@Override
		public String getDescription() {
			return "CARGA DE ELEMENTOS DE TRANSPORTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M14 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL HORNO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M15 {
		@Override
		public String getDescription() {
			return "PERSONAL ASALARIADO DE FABRICACI\u00D3N";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M16 {
		@Override
		public String getDescription() {
			return "RESTO PERSONAL ASALARIADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M17 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL LOCAL DE FABRICACI\u00D3N";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M18 {
		@Override
		public String getDescription() {
			return "RESTO SUPERFICIE DEL LOCAL INDEPENDIENTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M19 {
		@Override
		public String getDescription() {
			return "RESTO SUPERFICIE DEL LOCAL NO INDEPENDIENTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M20 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE PLAZAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M21 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE ASIENTOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M22 {
		@Override
		public String getDescription() {
			return "NO SE USA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M23 {
		@Override
		public String getDescription() {
			return "CARGA VEH\u00CDCULOS (TM)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M24 {
		@Override
		public String getDescription() {
			return "POTENCIA FISCAL DEL VEH\u00CDCULO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M25 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE VEH\u00CDCULOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M26 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M27 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO DE REPARACI\u00D3N";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M28 {
		@Override
		public String getDescription() {
			return "SUPERFICIE TALLER DE REPARACI\u00D3N";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M29 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE CARGA DE VEH\u00CDCULOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M30 {
		@Override
		public String getDescription() {
			return "POTENCIA INSTALADA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M31 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DEL HORNO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M32 {
		@Override
		public String getDescription() {
			return "VOLUMEN DE LOS HORNOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M33 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE PRODUCCI\u00D3N DE LOS HORNOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M34 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE MOLIDO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M35 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE PRENSADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M36 {
		@Override
		public String getDescription() {
			return "CAPACIDAD CUBAS DE COAGULACI\u00D3N";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M37 {
		@Override
		public String getDescription() {
			return "VOLUMEN DE BOMBOS DE TUESTE";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M38 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE DEP\u00D3SITOS Y CUBAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M39 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE TELARES";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M40 {
		@Override
		public String getDescription() {
			return "MAQUINARIA";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M41 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M42 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE LOS BOMBOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M43 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS DE COSER";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M44 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE M\u00C1QUINAS DE ENCUADERNAR";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M45 {
		@Override
		public String getDescription() {
			return "M\u00C1QUINA DE REVELAR";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M46 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DE ALOJAMIENTO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M47 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL RECINTO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M48 {
		@Override
		public String getDescription() {
			return "CAPACIDAD EN PLAZAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M49 {
		@Override
		public String getDescription() {
			return "SUPERFICIE DEL VIVERO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M50 {
		@Override
		public String getDescription() {
			return "AFORO DEL LOCAL";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M51 {
		@Override
		public String getDescription() {
			return "N\u00DAMERO DE MESAS Y APARATOS DE JUEGO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M52 {
		@Override
		public String getDescription() {
			return "\u00CDNDICE DE RENDIMIENTO NETO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M53 {
		@Override
		public String getDescription() {
			return "ID. DE CUOTA DEVENGADA POR OPER.CORRIENTES";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M54 {
		@Override
		public String getDescription() {
			return "IMPORTE DE LAS COMISIONES POR LOTERIAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M55 {
		@Override
		public String getDescription() {
			return "PORCENTAJE CUOTA DEVENGADA OP. CORRIENTES";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M56 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO ASADO DE POLLOS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M57 {
		@Override
		public String getDescription() {
			return "CAPACIDAD DEL ASADOR";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M58 {
		@Override
		public String getDescription() {
			return "IMPORTE TOTAL DE LAS COMISIONES";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M59 {
		@Override
		public String getDescription() {
			return "PERSONAL EMPLEADO (RESIDUOS)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M60 {
		@Override
		public String getDescription() {
			return "CARGA VEH\u00CDCULOS (RESIDUOS)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M61 {
		@Override
		public String getDescription() {
			return "BATEAS";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	, M62 {
		@Override
		public String getDescription() {
			return "PERSONAL RECOGIDA MATERIAL FOTOGR\u00E1FICO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return MODULE;
		}
	}
	
	
	
	,I01 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO NETO PREVIO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I02{
		@Override
		public String getDescription() {
			return "Incentivos al empleo";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I03 {
		@Override
		public String getDescription() {
			return "Incentivos a la inversi\u00F3n";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I04 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO NETO MINORADO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,
	@Deprecated
	I05 {
		@Override
		public String getDescription() {
			return "\u00CDNDICES CORRECTORES";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
		@Override
		public boolean accept(Epigraph epigraph, FiscalActivity fa) {
			return false;
		}
	}
		

	,I06 {
		@Override
		public String getDescription() {
			return "1. \u00CDndice corrector. Especiales";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I07 {
		@Override
		public String getDescription() {
			return "2. \u00CDndice corrector. Empresas de peque\u00F1a dimensi\u00F3n";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}

	,I08{
		@Override
		public String getDescription() {
			return "3. \u00CDndice corrector. De temporada";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I09 {
		@Override
		public String getDescription() {
			return "4. \u00CDndice corrector. De exceso";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I10 {
		@Override
		public String getDescription() {
			return "5. \u00CDndice corrector. De inicio de nueva actividad";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I11 {
		@Override
		public String getDescription() {
			return "RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I12{
		@Override
		public String getDescription() {
			return "Reducci\u00F3n para actividades econ\u00F3micas realizadas en el t\u00E9rmino municipal de Lorca";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I13 {
		@Override
		public String getDescription() {
			return "Rendimientos a efectos de pagos fraccionados despu\u00E9s de la reducci\u00F3n";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I14 {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,I15 {
		@Override
		public String getDescription() {
			return "Resultado. Pago Trimestral de I.R.P.F.";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return IRPF_INFO;
		}
	}
	,V01 {
		@Override
		public String getDescription() {
			return "\u00CDndice corrector de temporada";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
		}
	} 
	,V02 {
		@Override
		public String getDescription() {
			return "Cuota anual devengada por operaciones corrientes";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
		}
	} 
	,V03 {
		@Override
		public String getDescription() {
			return "Reducci\u00F3n aplicable por actividades econ\u00F3micas realizadas en el t\u00E9rmino municipal de Lorca";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
		}
	} 
	,V04 {
		@Override
		public String getDescription() {
			return "Cuota anual por operaciones corrientes despu\u00E9s de las reducciones anteriores";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
		}
	} 
	,V05 {
		@Override
		public String getDescription() {
			return "Porcentaje aplicable";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
		}
	} 
	,V06 {
		@Override
		public String getDescription() {
			return "INGRESO A CUENTA POR OPERACIONES CORRIENTES PREVIO";
		}
		@Override
		public FiscalActivityInfoKeyType getType() {
			return VAT_INFO;
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
	public boolean accept(Epigraph epigraph,FiscalActivity fa) {
		return  getType() == FiscalActivityInfoKeyType.INFO 
			|| (epigraph.hasIRPFModules() && getType() == FiscalActivityInfoKeyType.IRPF_INFO)
			|| (epigraph.hasIRPFModules() && getType() == FiscalActivityInfoKeyType.IRPF_MODULE)
			|| (epigraph.hasVATModules() && getType() == FiscalActivityInfoKeyType.VAT_INFO)
			|| (epigraph.hasVATModules() && getType() == FiscalActivityInfoKeyType.VAT_MODULE);
	}
	public String getDefaultValue() {
		return AonStringUtils.ZERO;
	}
	public FiscalActivityInfoKey[] getDetailKeys() {
		return null;
	}
	public boolean hasDetails() {
		return getDetailKeys() != null;
	}
	
	public abstract String getDescription();
	public abstract FiscalActivityInfoKeyType getType();

	
	public static FiscalActivityInfoKey safeValueOf(String value) {
		if (value == null) return null;
		return FiscalActivityInfoKey.valueOf(value);
	}
	
}