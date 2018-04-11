package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Modules2018 {

	public enum FarmerIRPF {
		 A01 (0.13,0.23,"Agr\u00EDcola dedicada a la obtenci\u00F3n de remolacha azucarera y ganadera de explotaci\u00F3n de ganado porcino de carne, de ganado bovino de carne, de ganado ovino de carne, de ganado caprino de carne, avicultura y cunicultura.")
		,A02 (0.13,0.23,"Forestal con un \"per\u00EDodo medio de corta\" superior a 30 a\u00F1os.")
		,A03 (0.12,0.33,"Ganadera de explotaci\u00F3n de ganado bovino de leche.")
		,A04 (0.26,0.36,"Agr\u00EDcola dedicada a la obtenci\u00F3n de cereales, c\u00EDtricos, frutos secos, hort\u00EDcultura, leguminosas, uva para vino de mesa sin denominaci\u00F3n de origen, productos del olivo y hongos para el consumo humano y ganadera de explotaci\u00F3n de ganado porcino de cr\u00EDa, de ganado bovino de cr\u00EDa, de ganado ovino de leche, de ganado caprino de leche y apicultura.")
		,A05 (0.26,0.36,"Forestal con un \"per\u00EDodo medio de corta\" igual o inferior a 30 a\u00F1os.")
		,A06 (0.32,0.42,"Agr\u00EDcola dedicada a la obtenci\u00F3n de arroz, uva para vino de mesa con denominaci\u00F3n de origen, y oleaginosas, ganadera de explotaci\u00F3n de ganado bovino de leche y otras actividades ganaderas no comprendidas expresamente en otros apartados y forestal dedicada a la extracci\u00F3n de resina.")
		,A07 (0.37,0.47,"Agr\u00EDcola dedicada a la obtenci\u00F3n de ra\u00EDces, tub\u00E9rculos, forrajes, arroz, algod\u00F3n, frutos no c\u00EDtricos, tabaco y otros productos agr\u00EDcolas no comprendidos expresamente en otros apartados.")
		,A08 (0.42,0.52,"Agr\u00EDcola dedicada a la obtenci\u00F3n de plantas textiles y uva de mesa, actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales y servicios de cr\u00EDa, guarda y engorde de aves.")
		,A09 (0.56,0.00,"Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales y servicios de cr\u00EDa, guarda y engorde de ganado, excepto aves.")
		;
		private double indiceRendimientoNeto;
		private double indiceRendimientoNetoBis;
		private String description;
		
		private FarmerIRPF(double indiceRendimientoNeto, double indiceRendimientoNetoBis, String description) {
			this.indiceRendimientoNeto =indiceRendimientoNeto;
			this.indiceRendimientoNetoBis = indiceRendimientoNetoBis;
			this.description = description;
		}
		public double getIndiceRendimientoNeto() {
			return indiceRendimientoNeto;
		}
		public double getIndiceRendimientoNetoBis() {
			return indiceRendimientoNetoBis;
		}
		public String getDescription() {
			return description;
		}
	}
	
	public enum FarmerIVA {
		 A01 ("01",0.10,	  	12.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de carne y avicultura de carne.")
		,A02 ("02",0.04,	     2.00, "Ganadera de explotaci\u00F3n intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche.")
		,A03 ("03",0.10,	  	24.00, "Ganadera de explotaci\u00F3n intensiva de ganado bovino de carne y cunicultura.")
		,A04 ("04",0.10,	  	32.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de cr\u00EDa, bovino de cr\u00EDa y otras intensivas o extensivas no comprendidas expresamente en otros apartados.")
		,A05 ("05",0.10,	  	40.00, "Ganadera de explotaci\u00F3n intensiva de ganado ovino y caprino de carne.")
		,A06 ("06",0.06625,		40.00, "Servicios de cr\u00EDa, guarda y engorde de aves.")
		,A07 ("07",0.070,  		48.00, "Apicultura.")
		,A08 ("08",0.10,	    48.00, "Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales que est\u00E9n excluidos del r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido, y servicios de cr\u00EDa, guarda y engorde de ganado, excepto aves.")
		,A09 ("09",0.21,   	  	80.00, "Actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales no incluidas en el r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido.")
		,A10 ("10",0.04,         2.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de productos agr\u00EDcolas no comprendidas en los apartados siguientes.")
		,A11 ("11",0.07625,	  	28.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de forrajes.")
		,A12 ("12",0.21,	  	44.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de plantas textiles y tabaco.")
		,A13 ("13",0.21,	  	44.00, "Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en r\u00E9gimen de aparcer\u00EDa.")
		,A14 ("14",0.070,  		28.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de queso.")
		,A15 ("15",0.2675, 		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino de mesa.")
		,A16 ("16",0.2675, 		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino con denominaci\u00F3n de origen.")
		,A17 ("17",0.19625,		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de otros productos distintos a los anteriores.")
		;

		private String code;
		private double indiceRendimientoNeto;
		private double porcentaje;
		private String description;
		
		private FarmerIVA(String code,double indiceRendimientoNeto, double porcentaje, String description) {
			this.code = code;
			this.indiceRendimientoNeto =indiceRendimientoNeto;
			this.porcentaje = porcentaje;
			this.description = description;
		}
		public String getCode() {
			return code;
		}
		public double getIndiceRendimientoNeto() {
			return indiceRendimientoNeto;
		}
		public double getPorcentaje() {
			return porcentaje;
		} 
		public String getDescription() {
			return description;
		}
		public static FarmerIVA safeValueOf( String code ) {
			if (AonStringUtils.isBlank(code)) {
				return null;
			}
			for (FarmerIVA farmerIVA : FarmerIVA.values()) {
				if (AonStringUtils.equals(farmerIVA.getCode(), code)) {
					return farmerIVA;
				}
			}
			return null;
		}
	}
	
	public enum Epigraph implements Serializable, IEpigraph {
		  E____("---","Producci\u00F3n de mejill\u00F3n en batea"
				,0.0
				,0
				,5
				,40000.00
				,new Module[] {
					 new Module(1, ModuleInfo.M01, "Persona", 5500.00)
					,new Module(2, ModuleInfo.M02, "Persona",7500.00)
					,new Module(3, ModuleInfo.M61, "Batea", 6700.00) }
				 ,null 		 
				 )
		 ,E_419_1 ("419.1","Industrias del pan y de la boller\u00EDa."
				,6.0
				,20
				,6
				,41602.30
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",6248.22)
					,new Module(2,ModuleInfo.M02,"Persona",14530.89)
					,new Module(3,ModuleInfo.M09,"Metro cua.",49.13)
					,new Module(4,ModuleInfo.M14,"100dm.cua.",629.86)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1693.54)
					,new Module(2,ModuleInfo.M09,"Metro cua.",7.09)
					,new Module(3,ModuleInfo.M14,"100dm.cua.",30.47)}
				)
		,E_419_2 ("419.2","Industrias de la boller\u00EDa, pasteler\u00EDa y galletas."
				,9.0
				,30
				,6
				,33760.53
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",6657.63)
					,new Module(2,ModuleInfo.M02,"Persona",13485.32)
					,new Module(3,ModuleInfo.M09,"Metro cua.",45.35)
					,new Module(4,ModuleInfo.M14,"100dm.cua.",541.68)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3064.67)
					,new Module(2,ModuleInfo.M09,"Metro cua.",8.86)
					,new Module(3,ModuleInfo.M14,"100dm.cua.",59.34)}
				)
		,E_419_3 ("419.3","Industrias de elaboraci\u00F3n de masas fritas."
				,10.0
				,32
				,6
				,19670.55
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4238.96)
					,new Module(2,ModuleInfo.M02,"Persona",12301.18)
					,new Module(3,ModuleInfo.M09,"Metro cua.",25.82)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2550.92)
					,new Module(2,ModuleInfo.M09,"Metro cua.",10.63)}
				)
		,E_423_9 ("423.9","Elaboraci\u00F3n de patatas fritas, palomitas de ma\u00EDz y similares."
				,10.0
				,32
				,6
				,19670.55
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4390.12)
					,new Module(2,ModuleInfo.M02,"Persona",12723.18)
					,new Module(3,ModuleInfo.M09,"Metro cua.",26.45)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2550.92)
					,new Module(2,ModuleInfo.M09,"Metro cua.",10.63)}
				)
		,E_641 ("641","Comercio al por menor de frutas, verduras, hortalizas y tub\u00E9rculos."
				,0.0
				,0
				,5
				,16867.67
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2387.18)
					,new Module(2,ModuleInfo.M02,"Persona",10581.66)
					,new Module(3,ModuleInfo.M10,"Metro cua.",57.94)
					,new Module(4,ModuleInfo.M11,"Metro cua.",88.18)
					,new Module(5,ModuleInfo.M13,"Kilogramo",1.01)}
				,null
				)
		,E_642_1 ("642.1","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados.."
				,10.0
				,32
				,5
				,21635.71
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2355.68)
					,new Module(2,ModuleInfo.M02,"Persona",10991.07)
					,new Module(3,ModuleInfo.M10,"Metro cua.",35.90)
					,new Module(4,ModuleInfo.M11,"Metro cua.",81.88)
					,new Module(5,ModuleInfo.M03,"100 Kwh",39.05)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1753.76)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.21)}
				)
		,E_642_2 ("642.2","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados.."
				,10.0
				,32
				,5
				,21635.71
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2355.68)
					,new Module(2,ModuleInfo.M02,"Persona",10991.07)
					,new Module(3,ModuleInfo.M10,"Metro cua.",35.90)
					,new Module(4,ModuleInfo.M11,"Metro cua.",81.88)
					,new Module(5,ModuleInfo.M03,"100 Kwh",39.05)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1753.76)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.21)}
				)
		,E_642_3 ("642.3","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados."
				,10.0
				,32
				,5
				,21635.71
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2355.68)
					,new Module(2,ModuleInfo.M02,"Persona",10991.07)
					,new Module(3,ModuleInfo.M10,"Metro cua.",35.90)
					,new Module(4,ModuleInfo.M11,"Metro cua.",81.88)
					,new Module(5,ModuleInfo.M03,"100 Kwh",39.05)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1753.76)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.21)}
				)
		,E_642_4 ("642.4","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados."
				,10.0
				,0
				,5
				,21635.71
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2355.68)
					,new Module(2,ModuleInfo.M02,"Persona",10991.07)
					,new Module(3,ModuleInfo.M10,"Metro cua.",35.90)
					,new Module(4,ModuleInfo.M11,"Metro cua.",81.88)
					,new Module(5,ModuleInfo.M03,"100 Kwh",39.05)}
				,null
				)
		,E_642_5 ("642.5","Comercio al por menor de huevos, aves, conejos de granja, caza; y de productos derivados de los mismos"
				,10.0
				,32
				,4
				,20136.65
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3382.36)
					,new Module(2,ModuleInfo.M02,"Persona",11337.49)
					,new Module(3,ModuleInfo.M03,"100 Kwh",25.19)
					,new Module(4,ModuleInfo.M10,"Metro cua.",27.08)
					,new Module(5,ModuleInfo.M11,"Metro cua.",58.57)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",673.16)
					,new Module(2,ModuleInfo.M09,"Metro cua.",13.29)
					,new Module(3,ModuleInfo.M57,"Pieza",62.89)}
				)
		,E_642_6 ("642.6","Comercio al por menor, en casquer\u00EDas, de v\u00EDsceras y despojos procedentes de animales de abasto, frescos y congelados."
				,0.0
				,0
				,5
				,16237.81
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2254.90)
					,new Module(2,ModuleInfo.M02,"Persona",11098.14)
					,new Module(3,ModuleInfo.M10,"Metro cua.",27.71)
					,new Module(4,ModuleInfo.M11,"Metro cua.",69.28)
					,new Module(5,ModuleInfo.M03,"100 Kwh",35.90)}
				,null
				)
		,E_643_1 ("643.1","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0.0
				,0
				,5
				,24551.97
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3823.25)
					,new Module(2,ModuleInfo.M02,"Persona",13296.36)
					,new Module(3,ModuleInfo.M10,"Metro cua.",36.53)
					,new Module(4,ModuleInfo.M11,"Metro cua.",113.37)
					,new Module(5,ModuleInfo.M03,"100 Kwh",28.98)}
				,null
				)
		,E_643_2 ("643.2","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0.0
				,0
				,5
				,24551.97
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3823.25)
					,new Module(2,ModuleInfo.M02,"Persona",13296.36)
					,new Module(3,ModuleInfo.M10,"Metro cua.",36.53)
					,new Module(4,ModuleInfo.M11,"Metro cua.",113.37)
					,new Module(5,ModuleInfo.M03,"100 Kwh",28.98)}
				,null
				)
		,E_644_1 ("644.1","Comercio al por menor de pan, pasteler\u00EDa, confiter\u00EDa y similares y de leche y productos l\u00E1cteos."
				,6.0
				,20
				,6
				,43605.26
				,new Module[]{
					 new Module(1,ModuleInfo.M15,"Persona",6248.22)
					,new Module(2,ModuleInfo.M16,"Persona",1058.17)
					,new Module(3,ModuleInfo.M02,"Persona",14530.89)
					,new Module(4,ModuleInfo.M17,"Metro cua.",49.13)
					,new Module(5,ModuleInfo.M18,"Metro cua.",34.01)
					,new Module(6,ModuleInfo.M19,"Metro cua.",125.97)
					,new Module(7,ModuleInfo.M14,"100dm.cua.",629.86)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2090.35)
					,new Module(2,ModuleInfo.M09,"Metro cua.",8.86)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",40.75)
					,new Module(4,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_644_2 ("644.2","Despachos de pan, panes especiales y boller\u00EDa."
				,6.0
				,20
				,6
				,42925.01
				,new Module[]{
					 new Module(1,ModuleInfo.M15,"Persona",6134.85)
					,new Module(2,ModuleInfo.M16,"Persona",1039.27)
					,new Module(3,ModuleInfo.M02,"Persona",14266.34)
					,new Module(4,ModuleInfo.M17,"Metro cua.",48.50)
					,new Module(5,ModuleInfo.M18,"Metro cua.",33.38)
					,new Module(6,ModuleInfo.M19,"Metro cua.",125.97)
					,new Module(7,ModuleInfo.M14,"100dm.cua.",629.86)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2090.35)
					,new Module(2,ModuleInfo.M09,"Metro cua.",8.86)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",40.75)
					,new Module(4,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_644_3 ("644.3","Comercio al por menor de productos de pasteler\u00EDa, boller\u00EDa y confiter\u00EDa."
				,9.0
				,30
				,6
				,33760.53
				,new Module[]{
					 new Module(1,ModuleInfo.M15,"Persona",6367.89)
					,new Module(2,ModuleInfo.M16,"Persona",1014.08)
					,new Module(3,ModuleInfo.M02,"Persona",12912.15)
					,new Module(4,ModuleInfo.M17,"Metro cua.",43.46)
					,new Module(5,ModuleInfo.M18,"Metro cua.",34.01)
					,new Module(6,ModuleInfo.M19,"Metro cua.",113.37)
					,new Module(7,ModuleInfo.M14,"100dm.cua.",522.78)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3064.67)
					,new Module(2,ModuleInfo.M09,"Metro cua.",8.86)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",59.34)
					,new Module(4,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_644_6 ("644.6","Comercio al por menor de masas fritas, con o sin coberturas o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparados de chocolate y bebidas refrescantes."
				,10.0
				,32
				,6
				,19670.55
				,new Module[]{
					 new Module(1,ModuleInfo.M15,"Persona",6852.88)
					,new Module(2,ModuleInfo.M16,"Persona",2254.90)
					,new Module(3,ModuleInfo.M02,"Persona",13214.47)
					,new Module(4,ModuleInfo.M17,"Metro cua.",27.71)
					,new Module(5,ModuleInfo.M18,"Metro cua.",21.41)
					,new Module(6,ModuleInfo.M19,"Metro cua.",36.53)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2550.92)
					,new Module(2,ModuleInfo.M09,"Metro cua.",10.63)
					,new Module(3,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_647_1 ("647.1","Comercio al por menor de cualquier clase de productos alimenticios y de bebidas en establecimientos con vendedor."
				,0.0
				,75
				,5
				,15822.10
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1026.67)
					,new Module(2,ModuleInfo.M02,"Persona",10839.90)
					,new Module(3,ModuleInfo.M10,"Metro cua.",20.15)
					,new Module(4,ModuleInfo.M11,"Metro cua.",68.65)
					,new Module(5,ModuleInfo.M03,"100 Kwh",8.81)}
				,new Module[]{
						new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_647_2 ("647.2","Comercio al por menor de cualquier clase de productos alimenticios y bebidas en r\u00E9gimen de autoservicio o mixto en establecimientos cuya sala de ventas tenga una superficie inferior a 400 metros cuadrados."
				,0.0
				,75
				,4
				,25219.62
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1788.80)
					,new Module(2,ModuleInfo.M02,"Persona",10827.31)
					,new Module(3,ModuleInfo.M09,"Metro cua.",23.31)
					,new Module(4,ModuleInfo.M03,"100 Kwh",32.75)}
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_647_3 ("647.3","Comercio al por menor de cualquier clase de productos alimenticios y bebidas en r\u00E9gimen de autoservicio o mixto en establecimientos cuya sala de ventas tenga una superficie inferior a 400 metros cuadrados."
				,0.0
				,75
				,4
				,25219.62
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1788.80)
					,new Module(2,ModuleInfo.M02,"Persona",10827.31)
					,new Module(3,ModuleInfo.M09,"Metro cua.",23.31)
					,new Module(4,ModuleInfo.M03,"100 Kwh",32.75)}
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_651_1 ("651.1","Comercio al por menor de productos textiles, confecciones para el hogar, alfombras y similares y art\u00EDculos de tapicer\u00EDa."
				,0.0
				,0
				,4
				,23638.67
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Personas",3010.74)
					,new Module(2,ModuleInfo.M02,"Personas",13812.85)
					,new Module(3,ModuleInfo.M03,"100 Kwh",38.42)
					,new Module(4,ModuleInfo.M10,"Metro cua.",35.28)
					,new Module(5,ModuleInfo.M11,"Metro cua.",107.07)}
				,null
				)
		,E_651_2 ("651.2","Comercio al por menor de toda clase de prendas para el vestido y tocado."
				,0.0
				,0
				,5
				,24848.00
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2569.83)
					,new Module(2,ModuleInfo.M02,"Persona",13995.51)
					,new Module(3,ModuleInfo.M09,"Metro cua.",49.13)
					,new Module(4,ModuleInfo.M03,"100 Kwh",56.69)}
				,null
				)
		,E_651_3 ("651.3","Comercio al por menor de lencer\u00EDa, corseter\u00EDa y prendas especiales."
				,0.0
				,0
				,3
				,19626.46
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2198.21)
					,new Module(2,ModuleInfo.M02,"Persona",11998.85)
					,new Module(3,ModuleInfo.M09,"Metro cua.",47.87)
					,new Module(4,ModuleInfo.M03,"100 Kwh",75.58)}
				,null
				)
		,E_651_4 ("651.4","Comercio al por menor de art\u00EDculos de mercer\u00EDa y paqueter\u00EDa."
				,0.0
				,0
				,4
				,14862.05
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1902.18)
					,new Module(2,ModuleInfo.M02,"Persona",10291.93)
					,new Module(3,ModuleInfo.M09,"Metro cua.",28.98)
					,new Module(4,ModuleInfo.M03,"100 Kwh",58.57)}
				,null
				)
		,E_651_5 ("651.5","Comercio al por menor de lencer\u00EDa, corseter\u00EDa y prendas especiales."
				,0.0
				,0
				,3
				,19626.46
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2198.21)
					,new Module(2,ModuleInfo.M02,"Persona",11998.85)
					,new Module(3,ModuleInfo.M09,"Metro cua.",47.87)
					,new Module(4,ModuleInfo.M03,"100 Kwh",75.58)}
				,null
				)
		,E_651_6 ("651.6","Comercio al por menor de calzado, art\u00EDculos de piel e imitaci\u00F3n o productos sustitutivos, cinturones, carteras, bolsos, maletas y art\u00EDculos de viaje en general."
				,0.0
				,0
				,5
				,24306.32
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3130.41)
					,new Module(2,ModuleInfo.M02,"Persona",13453.83)
					,new Module(3,ModuleInfo.M09,"Metro cua.",27.71)
					,new Module(4,ModuleInfo.M03,"100 Kwh",52.27)}
				,null
				)
		,E_652_2 ("652.2","Comercio al por menor de productos de droguer\u00EDa, perfumer\u00EDa y cosm\u00E9tica, limpieza, pinturas, barnices, disolventes, papeles y otros productos para la decoraci\u00F3n y de productos qu\u00EDmicos, y de art\u00EDculos para la higiene y el aseo personal."
				,0.0
				,75
				,4
				,25333.00
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Personas",3722.48)
					,new Module(2,ModuleInfo.M02,"Personas",12786.18)
					,new Module(3,ModuleInfo.M03,"100 Kwh",31.49)
					,new Module(4,ModuleInfo.M10,"Metro cua.",18.27)
					,new Module(5,ModuleInfo.M11,"Metro cua.",55.43)}
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_652_3 ("652.3","Comercio al por menor de productos de droguer\u00EDa, perfumer\u00EDa y cosm\u00E9tica, limpieza, pinturas, barnices, disolventes, papeles y otros productos para la decoraci\u00F3n y de productos qu\u00EDmicos, y de art\u00EDculos para la higiene y el aseo personal."
				,0.0
				,75
				,4
				,25333.00
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Personas",3722.48)
					,new Module(2,ModuleInfo.M02,"Personas",12786.18)
					,new Module(3,ModuleInfo.M03,"100 Kwh",31.49)
					,new Module(4,ModuleInfo.M10,"Metro cua.",18.27)
					,new Module(5,ModuleInfo.M11,"Metro cua.",55.43)}
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_653_1 ("653.1","Comercio al por menor de muebles."
				,0.0
				,0
				,4
				,30718.31
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4075.20)
					,new Module(2,ModuleInfo.M02,"Persona",16200.02)
					,new Module(3,ModuleInfo.M03,"100 Kwh",50.39)
					,new Module(4,ModuleInfo.M09,"Metro cua.",16.38)}
				,null
				)
		,E_653_2 ("653.2","Comercio al por menor de material y aparatos el\u00E9ctricos, electr\u00F3nicos, electrodom\u00E9sticos y otros aparatos de uso dom\u00E9stico accionados por otro tipo de energ\u00EDa distinta de la el\u00E9ctrica, as\u00ED como muebles de cocina"
				,15.0
				,48
				,3
				,26189.61
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2884.77)
					,new Module(2,ModuleInfo.M02,"Persona",14656.86)
					,new Module(3,ModuleInfo.M03,"100 Kwh",100.78)
					,new Module(4,ModuleInfo.M10,"Metro cua.",36.53)
					,new Module(5,ModuleInfo.M11,"Metro cua.",113.37)}
				,new Module[]{
					 new Module(1,ModuleInfo.M27,"Persona",5819.91)
					,new Module(2,ModuleInfo.M28,"Metro cua.",5.71)}
				)
		,E_653_3 ("653.3","Comercio al por menor de art\u00EDculos de menaje, ferreter\u00EDa, adorno, regalo, o reclamo (incluyendo bisuter\u00EDa y peque\u00F1os electrodom\u00E9sticos)."
				,0.0
				,0
				,4
				,24470.09
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Personas",3709.88)
					,new Module(2,ModuleInfo.M02,"Personas",15116.66)
					,new Module(3,ModuleInfo.M03,"100 Kwh",51.02)
					,new Module(4,ModuleInfo.M09,"Metro cua.",21.41)}
				,null
				)
		,E_653_4 ("653.4","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,4.0
				,13
				,3
				,26454.15
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3061.12)
					,new Module(2,ModuleInfo.M02,"Persona",16527.55)
					,new Module(3,ModuleInfo.M03,"100 Kwh",94.48)
					,new Module(4,ModuleInfo.M09,"Metro cua.",8.81)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",12317.71)
					,new Module(2,ModuleInfo.M03,"100 Kwh",239.74)
					,new Module(3,ModuleInfo.M09,"Metro cua.",9.10)}
				)
		,E_653_5 ("653.5","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,4.0
				,13
				,3
				,26454.15
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3061.12)
					,new Module(2,ModuleInfo.M02,"Persona",16527.55)
					,new Module(3,ModuleInfo.M03,"100 Kwh",94.48)
					,new Module(4,ModuleInfo.M09,"Metro cua.",8.81)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",12317.71)
					,new Module(2,ModuleInfo.M03,"100 Kwh",239.74)
					,new Module(3,ModuleInfo.M09,"Metro cua.",9.10)}
				)
		,E_653_9 ("653.9","Comercio al por menor de otros art\u00EDculos para el equipamiento del hogar n.c.o.p."
				,0.0
				,0
				,3
				,32765.35
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4950.71)
					,new Module(2,ModuleInfo.M02,"Persona",20061.07)
					,new Module(3,ModuleInfo.M03,"100 Kwh",81.88)
					,new Module(4,ModuleInfo.M09,"Metro cua.",39.68)}
				,null
				)
		,E_654_2 ("654.2","Comercio al por menor de accesorios y piezas de recambio para veh\u00EDculos terrestres."
				,4.0
				,13
				,4
				,32815.74
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3098.92)
					,new Module(2,ModuleInfo.M02,"Persona",17018.84)
					,new Module(3,ModuleInfo.M03,"100 Kwh",201.55)
					,new Module(4,ModuleInfo.M24,"CVF",617.26)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",15558.35)
					,new Module(2,ModuleInfo.M03,"100 Kwh",272.80)
					,new Module(3,ModuleInfo.M24,"CVF",620.03)}
				)
		,E_654_5 ("654.5","Comercio al por menor de toda clase de maquinaria (excepto aparatos del hogar, de oficina, m\u00E9dicos, ortop\u00E9dicos, \u00F3pticos y fotogr\u00E1ficos)."
				,4.0
				,13
				,3
				,31367.06
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",9422.71)
					,new Module(2,ModuleInfo.M02,"Persona",18858.03)
					,new Module(3,ModuleInfo.M03,"100 Kwh",39.05)
					,new Module(4,ModuleInfo.M24,"CVF",132.27)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",18021.89)
					,new Module(2,ModuleInfo.M03,"100 Kwh",74.41)
					,new Module(3,ModuleInfo.M24,"CVF",82.66)}
				)
		,E_654_6 ("654.6","Comercio al por menor de cubiertas, bandas o bandajes y c\u00E1maras de aire para toda clase de veh\u00EDculos, excepto las actividades de comercio al por mayor de los art\u00EDculos citados."
				,4.0
				,13
				,4
				,26970.63
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2746.19)
					,new Module(2,ModuleInfo.M02,"Persona",14222.25)
					,new Module(3,ModuleInfo.M03,"100 Kwh",119.67)
					,new Module(4,ModuleInfo.M24,"CVF",377.92)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",14152.98)
					,new Module(2,ModuleInfo.M03,"100 Kwh",206.67)
					,new Module(3,ModuleInfo.M24,"CVF",372.02)}
				)
		,E_659_2 ("659.2","Comercio al por menor de muebles de oficina y de m\u00E1quinas y equipos de oficina."
				,0.0
				,0
				,4
				,30718.31
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4157.08)
					,new Module(2,ModuleInfo.M02,"Persona",16521.25)
					,new Module(3,ModuleInfo.M03,"100 Kwh",56.69)
					,new Module(4,ModuleInfo.M09,"Metro cua.",17.01)}
				,null
				)

		,E_659_3 ("659.3","Comercio al por menor de aparatos e instrumentos m\u00E9dicos, ortop\u00E9dicos, \u00F3pticos y fotogr\u00E1ficos."
				,4.0
				,13
				,3
				,35524.14
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",7174.12)
					,new Module(2,ModuleInfo.M02,"Persona",19273.74)
					,new Module(3,ModuleInfo.M03,"100 Kwh",119.67)
					,new Module(4,ModuleInfo.M24,"CVF",1070.76)}
				,new Module[]{
					 new Module(1,ModuleInfo.M62,"Persona",14533.25)
					,new Module(2,ModuleInfo.M09,"Metro cua.",30.59)}
				)			
		,E_659_4A("659.4","Comercio al por menor de libros, peri\u00F3dicos, art\u00EDculos de papeler\u00EDa y escritorio y art\u00EDculos de dibujo y bellas artes, excepto en quioscos situados en la v\u00EDa p\u00FAblica."
				,100.0
				,75
				,3
				,25207.02
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4648.37)
					,new Module(2,ModuleInfo.M02,"Persona",17176.30)
					,new Module(3,ModuleInfo.M03,"100 Kwh",57.94)
					,new Module(4,ModuleInfo.M09,"Metro cua.",30.86)
					,new Module(5,ModuleInfo.M24,"CVF",535.38)}
				,new Module[]{
					 new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_659_4B("659.4","Comercio al por menor de prensa, revistas y libros en quioscos situados en la v\u00EDa p\u00FAblica."
				,100.0
				,75
				,2
				,28860.22
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3476.83)
					,new Module(2,ModuleInfo.M02,"Persona",17220.39)
					,new Module(3,ModuleInfo.M03,"100 Kwh",403.11)
					,new Module(4,ModuleInfo.M09,"Metro cua.",844.02)}
				,new Module[]{
					 new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_659_6 ("659.6","Comercio al por menor de juguetes, art\u00EDculos de deporte, prendas deportivas de vestido, calzado y tocado, armas, cartucher\u00EDa y art\u00EDculos de pirotecnia."
				,0.0
				,0
				,3
				,24948.78
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2916.26)
					,new Module(2,ModuleInfo.M02,"Persona",13258.56)
					,new Module(3,ModuleInfo.M03,"100 Kwh",138.57)
					,new Module(4,ModuleInfo.M09,"Metro cua.",32.75)}
				,null
				)
		,E_659_7 ("659.7","Comercio al por menor de semillas, abonos, flores y plantas y peque\u00F1os animales."
				,0.0
				,0
				,4
				,23978.80
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4988.50)
					,new Module(2,ModuleInfo.M02,"Persona",16124.43)
					,new Module(3,ModuleInfo.M24,"CVF",258.24)}
				,null
				)
		,E_662_2 ("662.2","Comercio al por menor de toda clase de art\u00EDculos, incluyendo alimentaci\u00F3n y bebidas, en establecimientos distintos de los especificados en el grupo 661 y en el ep\u00EDgrafe 662.1."
				,0.0
				,75
				,3
				,16395.27
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4868.82)
					,new Module(2,ModuleInfo.M02,"Persona",9429.01)
					,new Module(3,ModuleInfo.M03,"100 Kwh",28.98)
					,new Module(4,ModuleInfo.M09,"Metro cua.",37.79)}
				,new Module[]{
					 new Module(1,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_663_1 ("663.1","Comercio al por menor fuera de un establecimiento comercial permanente de productos alimenticios, incluso bebidas y helados."
				,6.0
				,20
				,2
				,14379.72
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1398.29)
					,new Module(2,ModuleInfo.M02,"Persona",13989.21)
					,new Module(3,ModuleInfo.M24,"CVF",113.37)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",5881.32)
					,new Module(2,ModuleInfo.M24,"CVF",43.40)}
				)
		
		,E_663_2 ("663.2","Comercio al por menor fuera de un establecimiento comercial permanente de art\u00EDculos textiles y de confecci\u00F3n."
				,0.0
				,0
				,2
				,19059.58
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2991.84)
					,new Module(2,ModuleInfo.M02,"Persona",13982.91)
					,new Module(3,ModuleInfo.M24,"CVF",239.34)}
				,null
				)
		,E_663_3 ("663.3","Comercio al por menor fuera de un establecimiento comercial permanente de calzado, pieles y art\u00EDculos de cuero."
				,0.0
				,0
				,2
				,17081.82
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2613.92)
					,new Module(2,ModuleInfo.M02,"Persona",11186.32)
					,new Module(3,ModuleInfo.M24,"CVF",151.16)}
				,null
				)
		,E_663_4 ("663.4","Comercio al por menor fuera de un establecimiento comercial permanente de art\u00EDculos de droguer\u00EDa y cosm\u00E9ticos y de productos qu\u00EDmicos en general."
				,0.0
				,0
				,2
				,16886.56
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3678.39)
					,new Module(2,ModuleInfo.M02,"Persona",12641.30)
					,new Module(3,ModuleInfo.M24,"CVF",113.37)}
				,null
				)
		,E_663_9 ("663.9","Comercio al por menor fuera de un establecimiento comercial permanente de otras clases de mercanc\u00EDas n.c.o.p."
				,0.0
				,0
				,2
				,18354.14
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",5448.29)
					,new Module(2,ModuleInfo.M02,"Persona",10537.57)
					,new Module(3,ModuleInfo.M24,"CVF",283.44)}
				,null
				)
		,E_671_4 ("671.4","Restaurantes de dos tenedores."
				,4.0
				,13
				,10
				,51617.08
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3709.88)
					,new Module(2,ModuleInfo.M02,"Persona",17434.55)
					,new Module(3,ModuleInfo.M08,"Kw cont.",201.55)
					,new Module(4,ModuleInfo.M04,"Mesa",585.77)
					,new Module(5,ModuleInfo.M06,"Maquina A",1077.06)
					,new Module(6,ModuleInfo.M07,"Maquina B",3810.65)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2993.81)
					,new Module(2,ModuleInfo.M08,"Kw cont.",150.57)
					,new Module(3,ModuleInfo.M04,"Mesa",168.29)
					,new Module(4,ModuleInfo.M06,"Maquina A",239.15)
					,new Module(5,ModuleInfo.M07,"Maquina B",841.46)
					,new Module(6,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_671_5 ("671.5","Restaurantes de un tenedor."
				,6.0
				,20
				,10
				,38081.38
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3602.80)
					,new Module(2,ModuleInfo.M02,"Persona",16174.82)
					,new Module(3,ModuleInfo.M08,"Kw cont.",125.97)
					,new Module(4,ModuleInfo.M04,"Mesa",220.45)
					,new Module(5,ModuleInfo.M06,"Maquina A",1077.06)
					,new Module(6,ModuleInfo.M07,"Maquina B",3810.65)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2400.36)
					,new Module(2,ModuleInfo.M08,"Kw cont.",70.86)
					,new Module(3,ModuleInfo.M04,"Mesa",124.00)
					,new Module(4,ModuleInfo.M06,"Maquina A",239.15)
					,new Module(5,ModuleInfo.M07,"Maquina B",841.46)
					,new Module(6,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_672_1 ("672.1","Cafeter\u00EDas."
				,4.0
				,13
				,8
				,39070.26
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1448.68)
					,new Module(2,ModuleInfo.M02,"Persona",13743.56)
					,new Module(3,ModuleInfo.M08,"Kw cont.",478.69)
					,new Module(4,ModuleInfo.M04,"Mesa",377.92)
					,new Module(5,ModuleInfo.M06,"Maquina A",957.39)
					,new Module(6,ModuleInfo.M07,"Maquina B",3747.67)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2356.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",124.00)
					,new Module(3,ModuleInfo.M04,"Mesa",70.86)
					,new Module(4,ModuleInfo.M06,"Maquina A",221.43)
					,new Module(5,ModuleInfo.M07,"Maquina B",832.60)
					,new Module(6,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_672_2 ("672.2","Cafeter\u00EDas."
				,4.0
				,13
				,8
				,39070.26
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1448.68)
					,new Module(2,ModuleInfo.M02,"Persona",13743.56)
					,new Module(3,ModuleInfo.M08,"Kw cont.",478.69)
					,new Module(4,ModuleInfo.M04,"Mesa",377.92)
					,new Module(5,ModuleInfo.M06,"Maquina A",957.39)
					,new Module(6,ModuleInfo.M07,"Maquina B",3747.67)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2356.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",124.00)
					,new Module(3,ModuleInfo.M04,"Mesa",70.86)
					,new Module(4,ModuleInfo.M06,"Maquina A",221.43)
					,new Module(5,ModuleInfo.M07,"Maquina B",832.60)
					,new Module(6,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_672_3 ("672.3","Cafeter\u00EDas."
				,4.0
				,13
				,8
				,39070.26
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1448.68)
					,new Module(2,ModuleInfo.M02,"Persona",13743.56)
					,new Module(3,ModuleInfo.M08,"Kw cont.",478.69)
					,new Module(4,ModuleInfo.M04,"Mesa",377.92)
					,new Module(5,ModuleInfo.M06,"Maquina A",957.39)
					,new Module(6,ModuleInfo.M07,"Maquina B",3747.67)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2356.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",124.00)
					,new Module(3,ModuleInfo.M04,"Mesa",70.86)
					,new Module(4,ModuleInfo.M06,"Maquina A",221.43)
					,new Module(5,ModuleInfo.M07,"Maquina B",832.60)
					,new Module(6,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_673_1 ("673.1","Caf\u00E9s y bares de categor\u00EDa especial."
				,2.0
				,6
				,8
				,30586.03
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4056.30)
					,new Module(2,ModuleInfo.M02,"Persona",15538.66)
					,new Module(3,ModuleInfo.M08,"Kw cont.",321.23)
					,new Module(4,ModuleInfo.M04,"Mesa",233.04)
					,new Module(5,ModuleInfo.M05,"Metro",371.62)
					,new Module(6,ModuleInfo.M06,"Maquina A",957.39)
					,new Module(7,ModuleInfo.M07,"Maquina B",2903.66)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3294.97)
					,new Module(2,ModuleInfo.M08,"Kw cont.",69.09)
					,new Module(3,ModuleInfo.M04,"Mesa",60.23)
					,new Module(4,ModuleInfo.M05,"Metro",77.95)
					,new Module(5,ModuleInfo.M06,"Maquina A",221.43)
					,new Module(6,ModuleInfo.M07,"Maquina B",655.45)
					,new Module(7,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_673_2 ("673.2","Otros caf\u00E9s y bares."
				,2.0
				,6
				,8
				,19084.78
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1643.93)
					,new Module(2,ModuleInfo.M02,"Persona",11413.08)
					,new Module(3,ModuleInfo.M08,"Kw cont.",94.48)
					,new Module(4,ModuleInfo.M04,"Mesa",119.67)
					,new Module(5,ModuleInfo.M05,"Metro",163.76)
					,new Module(6,ModuleInfo.M06,"Maquina A",806.23)
					,new Module(7,ModuleInfo.M07,"Maquina B",2947.75)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2577.52)
					,new Module(2,ModuleInfo.M08,"Kw cont.",47.83)
					,new Module(3,ModuleInfo.M04,"Mesa",56.69)
					,new Module(4,ModuleInfo.M05,"Metros",62.89)
					,new Module(5,ModuleInfo.M06,"Maquina A",177.15)
					,new Module(6,ModuleInfo.M07,"Maquina B",655.45)
					,new Module(7,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_675   ("675"	 ,"Servicios en quioscos, cajones, barracas u otros locales an\u00E1logos."
				,1.0
				,3
				,3
				,16596.83
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2802.88)
					,new Module(2,ModuleInfo.M02,"Persona",14461.60)
					,new Module(3,ModuleInfo.M08,"Kw cont.",107.07)
					,new Module(4,ModuleInfo.M09,"Metro cua.",26.45)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",4357.86)
					,new Module(2,ModuleInfo.M08,"Kw cont.",50.48)
					,new Module(3,ModuleInfo.M09,"Metro cua.",4.06)
					,new Module(4,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_676   ("676"	 ,"Servicios en chocolater\u00EDas, helader\u00EDas y horchater\u00EDas."
				,6.0
				,20
				,3
				,25528.25
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2418.67)
					,new Module(2,ModuleInfo.M02,"Persona",20016.97)
					,new Module(3,ModuleInfo.M08,"Kw cont.",541.68)
					,new Module(4,ModuleInfo.M04,"Mesa",220.45)
					,new Module(5,ModuleInfo.M06,"Maquina A",806.23)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3817.55)
					,new Module(2,ModuleInfo.M08,"Kw cont.",141.72)
					,new Module(3,ModuleInfo.M04,"Mesa",46.05)
					,new Module(4,ModuleInfo.M06,"Maquina A",177.15)
					,new Module(5,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_681   ("681"	 ,"Servicio de hospedaje en hoteles y moteles de una o dos estrellas."
				,6.0
				,20
				,10
				,61512.19
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",6223.02)
					,new Module(2,ModuleInfo.M02,"Persona",20438.98)
					,new Module(3,ModuleInfo.M20,"Plaza",371.62)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2604.09)
					,new Module(2,ModuleInfo.M20,"Plaza",53.14)
					,new Module(3,ModuleInfo.M58,"Euro",0.21)}
				)
		,E_682   ("682"	 ,"Servicio de hospedaje en hostales y pensiones."
				,6.0
				,20
				,8
				,32840.94
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",5145.96)
					,new Module(2,ModuleInfo.M02,"Persona",17541.62)
					,new Module(3,ModuleInfo.M20,"Plaza",270.85)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2533.22)
					,new Module(2,ModuleInfo.M20,"Plaza",55.80)}
				)
		,E_683   ("683"	 ,"Servicio de hospedaje en fondas y casas de hu\u00E9spedes."
				,9.0
				,30
				,8
				,16256.70
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4478.31)
					,new Module(2,ModuleInfo.M02,"Persona",14587.57)
					,new Module(3,ModuleInfo.M20,"Plaza",132.27)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1762.62)
					,new Module(2,ModuleInfo.M20,"Plaza",29.22)}
				)
		,E_691_1 ("691.1","Reparaci\u00F3n de art\u00EDculos el\u00E9ctricos para el hogar."
				,15.0
				,48
				,3
				,21585.33
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4314.54)
					,new Module(2,ModuleInfo.M02,"Persona",15538.66)
					,new Module(3,ModuleInfo.M09,"Metro cua.",17.01)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",5819.91)
					,new Module(2,ModuleInfo.M09,"Metro cua.",5.71)}
				)
		,E_691_2 ("691.2","Reparaci\u00F3n de veh\u00EDculos autom\u00F3viles, bicicletas y otros veh\u00EDculos."
				,9.0
				,30
				,5
				,33729.04
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4157.08)
					,new Module(2,ModuleInfo.M02,"Persona",17094.42)
					,new Module(3,ModuleInfo.M09,"Metro cua.",27.08)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",8556.27)
					,new Module(2,ModuleInfo.M09,"Metro cua.",16.54)}
				)
		,E_691_9A("691.9","Reparaci\u00F3n de calzado."
				,15.0
				,48
				,2
				,16552.74
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1845.50)
					,new Module(2,ModuleInfo.M02,"Persona",10014.78)
					,new Module(3,ModuleInfo.M03,"100 Kwh",125.97)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3000.89)
					,new Module(2,ModuleInfo.M03,"100 Kwh",36.37)}
				)
		,E_691_9B("691.9","Reparaci\u00F3n de otros bienes de consumo n.c.o.p. (excepto reparaci\u00F3n de calzado, restauraci\u00F3n de obras de arte, muebles, antig\u00FCedades e instrumentos musicales)."
				,15.0
				,48
				,2
				,24803.91
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4094.10)
					,new Module(2,ModuleInfo.M02,"Persona",16187.42)
					,new Module(3,ModuleInfo.M09,"Metro cua.",45.35)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",5381.76)
					,new Module(2,ModuleInfo.M09,"Metro cua.",13.23)}
				)
		
		,E_692   ("692"	 ,"Reparaci\u00F3n de maquinaria industrial."
				,9.0
				,30
				,2
				,30352.99
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4409.02)
					,new Module(2,ModuleInfo.M02,"Persona",18146.29)
					,new Module(3,ModuleInfo.M09,"Metro cua.",94.48)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",9721.90)
					,new Module(2,ModuleInfo.M09,"Metro cua.",53.75)}
				)
		,E_699   ("699"	 ,"Otras reparaciones n.c.o.p."
				,10.0
				,32
				,2
				,23607.18
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3703.58)
					,new Module(2,ModuleInfo.M02,"Persona",15230.03)
					,new Module(3,ModuleInfo.M09,"Metro cua.",88.18)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",7671.69)
					,new Module(2,ModuleInfo.M09,"Metro cua.",46.31)}
				)
		,E_721_1 ("721.1","Transporte urbano colectivo y de viajeros por carretera."
				,1.0
				,1
				,5
				,35196.62
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2981.02)
					,new Module(2,ModuleInfo.M02,"Persona",16016.97)
					,new Module(3,ModuleInfo.M21,"Asiento",121.40)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1700.62)
					,new Module(2,ModuleInfo.M21,"Asiento",79.72)}
				)
		,E_721_2 ("721.2","Transporte por autotaxis."
				,5.0
				,10
				,3
				,9999999.99
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1346.27)
					,new Module(2,ModuleInfo.M02,"Persona",7656.89)
					,new Module(3,ModuleInfo.M12,"1000 Km",45.08)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",903.46)
					,new Module(2,ModuleInfo.M12,"1000 Km",8.78)}
				)
		,E_721_3 ("721.3","Transporte urbano colectivo y de viajeros por carretera."
				,1.0
				,1
				,5
				,35196.62
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2981.02)
					,new Module(2,ModuleInfo.M02,"Persona",16016.97)
					,new Module(3,ModuleInfo.M21,"Asiento",121.40)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1700.62)
					,new Module(2,ModuleInfo.M21,"Asiento",79.72)}
				)
		,E_722A  ("722"	 ,"Transporte de mercanc\u00EDas por carretera, expto residuos"
				,5.0
				,10
				,5
				,33640.86
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2728.59)
					,new Module(2,ModuleInfo.M02,"Persona",10090.99)
					,new Module(3,ModuleInfo.M23,"Tonelada",126.21)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",4149.99)
					,new Module(2,ModuleInfo.M23,"Tonelada",388.55)}
				)
		,E_722B  ("722"	 ,"Transporte de residuos por carretera."
				,5.0
				,1
				,5
				,33640.86
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2728.59)
					,new Module(2,ModuleInfo.M02,"Persona",10090.99)
					,new Module(3,ModuleInfo.M23,"Tonelada",126.21)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1948.64)
					,new Module(2,ModuleInfo.M23,"Tonelada",181.58)}
				)

		,E_751_5 ("751.5","Engrase y lavado de veh\u00EDculos."
				,9.0
				,30
				,5
				,28280.74
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4667.27)
					,new Module(2,ModuleInfo.M02,"Persona",19191.86)
					,new Module(3,ModuleInfo.M09,"Metro cua.",30.23)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",8556.27)
					,new Module(2,ModuleInfo.M09,"Metro cua.",16.54)}
				)
		,E_757   ("757"	 ,"Servicios de mudanzas."
				,5.0
				,10
				,4
				,33640.86
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2566.32)
					,new Module(2,ModuleInfo.M02,"Persona",10175.13)
					,new Module(3,ModuleInfo.M23,"Tonelada",48.08)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",5712.43)
					,new Module(2,ModuleInfo.M23,"Tonelada",256.27)}
				)
		,E_849_5 ("849.5","Transporte de mensajer\u00EDa y recader\u00EDa, cuando la actividad se realice exclusivamente con medios de transporte propios."
				,5.0
				,10
				,5
				,33640.86
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",2728.59)
					,new Module(2,ModuleInfo.M02,"Persona",10090.99)
					,new Module(3,ModuleInfo.M23,"Tonelada",126.21)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",4149.99)
					,new Module(2,ModuleInfo.M23,"Tonelada",388.55)}
				)
		,E_933_1 ("933.1","Ense\u00F1anza de conducci\u00F3n de veh\u00EDculos terrestres, acu\u00E1ticos, aeron\u00E1uticos, etc."
				,15.0
				,48
				,4
				,47233.25
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3067.42)
					,new Module(2,ModuleInfo.M02,"Persona",20596.45)
					,new Module(3,ModuleInfo.M25,"veh\u00EDculo",774.72)
					,new Module(4,ModuleInfo.M24,"CVF",258.24)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3000.89)
					,new Module(2,ModuleInfo.M25,"veh\u00EDculo",256.27)
					,new Module(3,ModuleInfo.M24,"CVF",107.47)}
				)
		,E_933_9 ("933.9","Otras actividades de ense\u00F1anza, tales como idiomas, corte y confecci\u00F3n, mecanograf\u00EDa, taquigraf\u00EDa, preparaci\u00F3n de ex\u00E1menes y oposiciones y similares n.c.o.p."
				,15.0
				,48
				,5
				,33697.55
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1253.49)
					,new Module(2,ModuleInfo.M02,"Persona",15727.62)
					,new Module(3,ModuleInfo.M09,"Metro cua.",62.36)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2413.95)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.64)}
				)
		,E_967_2 ("967.2","Escuelas y servicios de perfeccionamiento del deporte."
				,2.0
				,3
				,3
				,37067.30
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",7035.55)
					,new Module(2,ModuleInfo.M02,"Persona",14215.95)
					,new Module(3,ModuleInfo.M09,"Metro cua.",34.01)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3441.10)
					,new Module(2,ModuleInfo.M09,"Metro cua.",5.58)}
				)
		,E_971_1 ("971.1","Tinte, limpieza en seco, lavado y planchado de ropas hechas y de prendas y art\u00EDculos del hogar usados."
				,15.0
				,48
				,4
				,37224.77
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4553.90)
					,new Module(2,ModuleInfo.M02,"Persona",16773.19)
					,new Module(3,ModuleInfo.M03,"100 Kwh",45.98)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3844.13)
					,new Module(2,ModuleInfo.M03,"100 Kwh",14.06)}
				)
		,E_972_1 ("972.1","Servicios de peluquer\u00EDa de se\u00F1ora y caballero."
				,5.0
				,13
				,6
				,18051.81
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",3161.90)
					,new Module(2,ModuleInfo.M02,"Persona",9649.47)
					,new Module(3,ModuleInfo.M09,"Metro cua.",94.48)
					,new Module(4,ModuleInfo.M03,"100 Kwh",81.88)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2562.75)
					,new Module(2,ModuleInfo.M09,"Metro cua.",41.33)
					,new Module(3,ModuleInfo.M03,"100 Kwh",17.48)}
				)
		,E_972_2 ("972.2","Salones e institutos de belleza."
				,10.0
				,32
				,6
				,26945.44
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",1788.80)
					,new Module(2,ModuleInfo.M02,"Persona",14896.21)
					,new Module(3,ModuleInfo.M09,"Metro cua.",88.18)
					,new Module(4,ModuleInfo.M03,"100 Kwh",55.43)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2562.75)
					,new Module(2,ModuleInfo.M09,"Metro cua.",41.33)
					,new Module(3,ModuleInfo.M03,"100 Kwh",17.36)}
				)
		,E_973_3 ("973.3","Servicios de copias de documentos con m\u00E1quinas fotocopiadoras."
				,9.0
				,30
				,4
				,24192.95
				,new Module[]{
					 new Module(1,ModuleInfo.M01,"Persona",4125.59)
					,new Module(2,ModuleInfo.M02,"Persona",17044.03)
					,new Module(3,ModuleInfo.M08,"Kw cont.",541.68)}
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",13136.13)
					,new Module(2,ModuleInfo.M08,"Kw cont.",239.74)}
				)
				;
		private String epigraph;
		private String description;
		private Module[] irpfModules;
		private Module[] vatModules;
		private double vatPorc;
		private double porcMin;
		private double limPers;
		private double limExceso;
		
		private Epigraph(String epigraph, String description
				,double vatPorc,double porcMin,double limPers
				,double limExceso
				,Module[] irpfModules, Module[] ivaModules) {
			this.epigraph = epigraph;
			this.description = description;
			this.vatPorc = vatPorc; 
			this.porcMin=porcMin;
			this.limPers=limPers;
			this.limExceso=limExceso;
			this.irpfModules=irpfModules;
			this.vatModules=ivaModules;
		}
		public String getEpigraph() {
			return epigraph;
		}
		public String getDescription() {
			return description;
		}
		public double getVatPorc() {
			return vatPorc;
		}
		public double getPorcMin() {
			return porcMin;
		}
		public double getLimPers() {
			return limPers;
		}
		public double getLimExceso() {
			return limExceso;
		}
		public Module[] getIRPFModules() {
			return irpfModules;
		}
		public Module[] getVATModules() {
			return vatModules;
		}
		public boolean hasVATModules() {
			return vatModules != null;
		}
		public boolean hasIRPFModules() {
			return irpfModules != null;
		}
		
		public static Epigraph getEpigraph(String code) {
			for (Epigraph epi: Epigraph.values()) {
				if (AonStringUtils.equals(epi.getEpigraph(), code)) {
					return epi;
				}
			}
			return null;
		}
		public String getFullDescription() {
			return epigraph  + " - " + description;
		}
	}
	
	public static void main(String[] args) {
		for (Epigraph e : Epigraph.values()){
			System.out.print("\"");
			System.out.print(e.getEpigraph());
			System.out.print("\",\"");
			if (e.hasIRPFModules()) System.out.print("IRPF");
			System.out.print("\",\"");
			if (e.hasVATModules()) System.out.print("IVA");
			System.out.print("\"");
			System.out.println();
		}
	}	
	
}
