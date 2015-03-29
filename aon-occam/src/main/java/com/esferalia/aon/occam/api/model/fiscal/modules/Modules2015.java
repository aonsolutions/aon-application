package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;

public class Modules2015 {
	
	public enum FarmerIRPF {
		 A01 (0.13,0.23,"Agr\u00EDcola dedicada a la obtenci\u00F3n de remolacha azucarera y ganadera de explotaci\u00F3n de ganado porcino de carne, de ganado bovino de carne, de ganado ovino de carne, de ganado caprino de carne, avicultura y cunicultura.")
		,A02 (0.13,0.23,"Forestal con un \"per\u00EDodo medio de corta\" superior a 30 a\u00F1os.")
		,A03 (0.26,0.36,"Agr\u00EDcola dedicada a la obtenci\u00F3n de cereales, c\u00EDtricos, frutos secos, hort\u00EDcultura, leguminosas, uva para vino de mesa sin denominaci\u00F3n de origen, productos del olivo y hongos para el consumo humano y ganadera de explotaci\u00F3n de ganado porcino de cr\u00EDa, de ganado bovino de cr\u00EDa, de ganado ovino de leche, de ganado caprino de leche y apicultura.")
		,A04 (0.26,0.36,"Forestal con un \"per\u00EDodo medio de corta\" igual o inferior a 30 a\u00F1os.")
		,A05 (0.32,0.42,"Agr\u00EDcola dedicada a la obtenci\u00F3n de uva para vino de mesa con denominaci\u00F3n de origen, y oleaginosas, ganadera de explotaci\u00F3n de ganado bovino de leche y otras actividades ganaderas no comprendidas expresamente en otros apartados y forestal dedicada a la extracci\u00F3n de resina.")
		,A06 (0.37,0.47,"Agr\u00EDcola dedicada a la obtenci\u00F3n de ra\u00EDces, tub\u00E9rculos, forrajes, arroz, algod\u00F3n, frutos no c\u00EDtricos, tabaco y otros productos agr\u00EDcolas no comprendidos expresamente en otros apartados.")
		,A07 (0.42,0.52,"Agr\u00EDcola dedicada a la obtenci\u00F3n de plantas textiles y uva de mesa, actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales y servicios de cr\u00EDa, guarda y engorde de aves.")
		,A08 (0.56,0.00,"Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales y servicios de cr\u00EDa, guarda y engorde de ganado, excepto aves.")
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
		 A01 (0.10,	  12.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de carne y avicultura de carne.")
		,A02 (0.04,	   2.00, "Ganadera de explotaci\u00F3n intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche.")
		,A03 (0.10,	  24.00, "Ganadera de explotaci\u00F3n intensiva de ganado bovino de carne y cunicultura.")
		,A04 (0.10,	  32.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de cr\u00EDa, bovino de cr\u00EDa y otras intensivas o extensivas no comprendidas expresamente en otros apartados.")
		,A05 (0.10,	  40.00, "Ganadera de explotaci\u00F3n intensiva de ganado ovino y caprino de carne.")
		,A06 (0.06625,40.00, "Servicios de cr\u00EDa, guarda y engorde de aves.")
		,A07 (0.070,  48.00, "Apicultura.")
		,A08 (0.10,	  48.00, "Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales que est\u00E9n excluidos del r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido, y servicios de cr\u00EDa, guarda y engorde de ganado, excepto aves.")
		,A09 (0.21,   80.00, "Actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales no incluidas en el r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido.")
		,A10 (0.04,    2.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de productos agr\u00EDcolas no comprendidas en los apartados siguientes.")
		,A11 (0.07625,28.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de forrajes.")
		,A12 (0.21,	  44.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de plantas textiles y tabaco.")
		,A13 (0.21,	  44.00, "Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en r\u00E9gimen de aparcer\u00EDa.")
		,A14 (0.070,  28.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de queso.")
		,A15 (0.2675, 80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino de mesa.")
		,A16 (0.2675, 80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino con denominaci\u00F3n de origen.")
		,A17 (0.19625,80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de otros productos distintos a los anteriores.")
		;

		private double indiceRendimientoNeto;
		private double porcentaje;
		private String description;
		
		private FarmerIVA(double indiceRendimientoNeto, double porcentaje, String description) {
			this.indiceRendimientoNeto =indiceRendimientoNeto;
			this.porcentaje = porcentaje;
			this.description = description;
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
	}
	
	public enum Epigraph implements Serializable {
		E____("---",
			  "Producci\u00F3n de mejill\u00F3n en batea",
			  0,
			  5,
			  40000.00,
			  new Module[] {new Module(1, FiscalActivityInfoKey.M01, "Persona", 5500.00),
						new Module(2, FiscalActivityInfoKey.M02, "Persona",7500.00),
						new Module(3, FiscalActivityInfoKey.M61,"Batea", 6700.00) },
			  null 		 
				 )
		, E_314(
				"314",
				"Carpinter\u00EDa met\u00E1lica y fabricaci\u00F3n de estructuras met\u00E1licas y calderer\u00EDa.",
				30,
				4,
				32475.62,
				new Module[] {new Module(1, FiscalActivityInfoKey.M01, "Persona", 3577.61),
						new Module(2, FiscalActivityInfoKey.M02, "Persona",17044.03),
						new Module(3, FiscalActivityInfoKey.M03,"100 Kwh", 61.10),
						new Module(4, FiscalActivityInfoKey.M24, "CVF", 170.06) },
				new Module[] {new Module(1, FiscalActivityInfoKey.M26, "Persona", 5968.73),
						new Module(2, FiscalActivityInfoKey.M03,"100 Kwh", 43.81),
						new Module(3, FiscalActivityInfoKey.M24, "CVF", 289.34) }		 
				 )
		, E_315(
				"315",
				"Carpinter\u00EDa met\u00E1lica y fabricaci\u00F3n de estructuras met\u00E1licas y calderer\u00EDa.",
				30,
				4,
				32475.62,
				new Module[] {new Module(1, FiscalActivityInfoKey.M01, "Persona", 3577.61),
						new Module(2, FiscalActivityInfoKey.M02, "Persona",17044.03),
						new Module(3, FiscalActivityInfoKey.M03,"100 Kwh", 61.10),
						new Module(4, FiscalActivityInfoKey.M24, "CVF", 170.06) },
				new Module[] {new Module(1, FiscalActivityInfoKey.M26, "Persona", 5968.73),
						new Module(2, FiscalActivityInfoKey.M03,"100 Kwh", 43.81),
						new Module(3, FiscalActivityInfoKey.M24, "CVF", 289.34) })
						
		,E_316_2 ("316.2","Fabricaci\u00F3n de art\u00EDculos de ferreter\u00EDa, cerrajer\u00EDa, torniller\u00EDa, derivados del alambre, menaje y otros art\u00EDculos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3678.39)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16351.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",62.98)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",125.97)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8010.65)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",42.16)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",28.10)}
				)				
		,E_316_3 ("316.3","Fabricaci\u00F3n de art\u00EDculos de ferreter\u00EDa, cerrajer\u00EDa, torniller\u00EDa, derivados del alambre, menaje y otros art\u00EDculos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3678.39)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16351.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",62.98)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",125.97)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8010.65)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",42.16)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",28.10)}
				)
		,E_316_4 ("316.4","Fabricaci\u00F3n de art\u00EDculos de ferreter\u00EDa, cerrajer\u00EDa, torniller\u00EDa, derivados del alambre, menaje y otros art\u00EDculos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3678.39)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16351.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",62.98)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",125.97)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8010.65)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",42.16)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",28.10)}
				)
		,E_316_9 ("316.9","Fabricaci\u00F3n de art\u00EDculos de ferreter\u00EDa, cerrajer\u00EDa, torniller\u00EDa, derivados del alambre, menaje y otros art\u00EDculos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3678.39)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16351.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",62.98)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",125.97)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8010.65)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",42.16)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",28.10)}
				)
		,E_419_1 ("419.1","Industrias del pan y de la boller\u00EDa."
				,20
				,6
				,41602.30
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6248.22)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14530.89)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",49.13)
					,new Module(4,FiscalActivityInfoKey.M14,"100dm.cua.",629.86)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1693.54)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",7.09)
					,new Module(3,FiscalActivityInfoKey.M14,"100dm.cua.",30.47)}
				)
		,E_419_2 ("419.2","Industrias de la boller\u00EDa, pasteler\u00EDa y galletas."
				,30
				,6
				,33760.53
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6657.63)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13485.32)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",45.35)
					,new Module(4,FiscalActivityInfoKey.M14,"100dm.cua.",541.68)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3064.67)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",8.86)
					,new Module(3,FiscalActivityInfoKey.M14,"100dm.cua.",59.34)}
				)
		,E_419_3 ("419.3","Industrias de elaboraci\u00F3n de masas fritas."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4238.96)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",12301.18)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",25.82)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2550.92)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",10.63)}
				)
		,E_423_9 ("423.9","Elaboraci\u00F3n de patatas fritas, palomitas de ma\u00EDz y similares."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4390.12)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",12723.18)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",26.45)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2550.92)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",10.63)}
				)
		,E_453A  ("453"  ,"Confecci\u00F3n en serie de prendas de vestir y sus complementos, excepto cuando su ejecuci\u00F3n se realice mayoritariamente por encargo a terceros."
				,32
				,5
				,38969.48
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3382.36)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13730.96)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",125.97)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",529.08)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5232.96)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",25.62)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",198.40)}
				)
		,E_453B  ("453"  ,"Confecci\u00F3n en serie de prendas de vestir y sus complementos, ejecutada directamente por la propia empresa, cuando se realice exclusivamente para terceros y por encargo."
				,32
				,5
				,29225.54
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2538.34)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10298.22)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",94.48)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",396.81)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3926.79)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",19.01)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",148.80)}
				)
		,E_463   ("463"  ,"Fabricaci\u00F3n en serie de piezas de carpinter\u00EDa, parqu\u00E9 y estructuras de madera para la construcci\u00F3n."
				,32
				,5
				,28463.40
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",4037.41)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",18404.53)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",62.98)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8126.38)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",13.23)}
				)
		,E_468   ("468"  ,"Industria del mueble de madera."
				,32
				,4
				,29534.17
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2947.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16300.79)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",49.76)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5522.30)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",28.10)}
				)
		,E_474_1 ("474.1","Impresi\u00F3n de textos o im\u00E1genes."
				,30
				,4
				,40418.16
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",5208.95)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",21534.93)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",484.99)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",680.25)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",7861.85)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",165.34)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",264.53)}
				)
		,E_501_3 ("501.3","Alba\u00F1iler\u00EDa y peque\u00F1os trabajos de construcci\u00F3n en general."
				,9
				,6
				,32078.80
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",3640.59)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",17988.83)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",46.60)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",201.55)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3401.25)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",23.03)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",76.18)}
				)
		,E_504_1 ("504.1","Instalaciones y montajes (excepto fontaner\u00EDa, fr\u00EDo, calor y acondicionamiento de aire)."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_504_2 ("504.2","Instalaciones de fontaner\u00EDa, fr\u00EDo, calor y acondicionamiento de aire."
				,20
				,4
				,33332.23
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",8238.58)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",22360.05)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",138.57)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",138.57)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4775.14)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",50.61)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",2.96)}
				)
		,E_504_3 ("504.3","Instalaciones de fontaner\u00EDa, fr\u00EDo, calor y acondicionamiento de aire."
				,20
				,4
				,33332.23
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",8238.58)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",22360.05)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",138.57)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",138.57)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4775.14)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",50.61)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",2.96)}
				)
		,E_504_4 ("504.4","Instalaci\u00F3n de pararrayos y similares. Montaje e instalaci\u00F3n de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalaci\u00F3n de aparatos elevadores de cualquier clase y tipo. Instalaciones telef\u00F3nicas, telegr\u00E1ficas, telegr\u00E1ficas sin hilos y de televisi\u00F3n, en edificios y construcciones de cualquier clase. Montajes met\u00E1licos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalaci\u00F3n o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_504_5 ("504.5","Instalaci\u00F3n de pararrayos y similares. Montaje e instalaci\u00F3n de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalaci\u00F3n de aparatos elevadores de cualquier clase y tipo. Instalaciones telef\u00F3nicas, telegr\u00E1ficas, telegr\u00E1ficas sin hilos y de televisi\u00F3n, en edificios y construcciones de cualquier clase. Montajes met\u00E1licos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalaci\u00F3n o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_504_6 ("504.6","Instalaci\u00F3n de pararrayos y similares. Montaje e instalaci\u00F3n de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalaci\u00F3n de aparatos elevadores de cualquier clase y tipo. Instalaciones telef\u00F3nicas, telegr\u00E1ficas, telegr\u00E1ficas sin hilos y de televisi\u00F3n, en edificios y construcciones de cualquier clase. Montajes met\u00E1licos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalaci\u00F3n o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_504_7 ("504.7","Instalaci\u00F3n de pararrayos y similares. Montaje e instalaci\u00F3n de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalaci\u00F3n de aparatos elevadores de cualquier clase y tipo. Instalaciones telef\u00F3nicas, telegr\u00E1ficas, telegr\u00E1ficas sin hilos y de televisi\u00F3n, en edificios y construcciones de cualquier clase. Montajes met\u00E1licos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalaci\u00F3n o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_504_8 ("504.8","Instalaci\u00F3n de pararrayos y similares. Montaje e instalaci\u00F3n de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalaci\u00F3n de aparatos elevadores de cualquier clase y tipo. Instalaciones telef\u00F3nicas, telegr\u00E1ficas, telegr\u00E1ficas sin hilos y de televisi\u00F3n, en edificios y construcciones de cualquier clase. Montajes met\u00E1licos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalaci\u00F3n o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6575.75)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20854.69)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",69.28)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4601.93)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",30.31)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",3.70)}
				)
		,E_505_1 ("505.1","Revestimientos, solados y pavimentos y colocaci\u00F3n de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4112.99)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20325.60)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",21.41)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",245.64)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2432.84)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.91)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",59.05)}
				)
		,E_505_2 ("505.2","Revestimientos, solados y pavimentos y colocaci\u00F3n de aislamientos."	,20
				,4
				,30038.06
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4112.99)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20325.60)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",21.41)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",245.64)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2432.84)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.91)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",59.05)}
				)
		,E_505_3 ("505.3","Revestimientos, solados y pavimentos y colocaci\u00F3n de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4112.99)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20325.60)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",21.41)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",245.64)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2432.84)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.91)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",59.05)}
				)
		,E_505_4 ("505.4","Revestimientos, solados y pavimentos y colocaci\u00F3n de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4112.99)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20325.60)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",21.41)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",245.64)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2432.84)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.91)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",59.05)}
				)
		,E_505_5 ("505.5","Carpinter\u00EDa y cerrajer\u00EDa."
				,20
				,4
				,28356.33
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6689.12)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",19154.07)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",144.87)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3354.01)
					,new Module(2,FiscalActivityInfoKey.M24,"CVF",16.93)}
				)
		,E_505_6 ("505.6","Pintura de cualquier tipo y clase y revestimiento con papel, tejidos o pl\u00E1sticos y terminaci\u00F3n y decoraci\u00F3n de edificios y locales."
				,30
				,3
				,26687.20
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6027.77)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17648.70)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",144.87)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2629.68)
					,new Module(2,FiscalActivityInfoKey.M24,"CVF",14.18)}
				)
		,E_505_7 ("505.7","Trabajos en yeso y escayola y decoraci\u00F3n de edificios y locales."
				,30
				,3
				,26687.20
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6027.77)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17648.70)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",144.87)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2629.68)
					,new Module(2,FiscalActivityInfoKey.M24,"CVF",14.18)}
				)
		,E_641 ("641","Comercio al por menor de frutas, verduras, hortalizas y tub\u00E9rculos."
				,0
				,5
				,16867.67
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2387.18)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10581.66)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",57.94)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",88.18)
					,new Module(5,FiscalActivityInfoKey.M13,"Kilogramo",1.01)}
				,null
				)
		,E_642_1 ("642.1","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados, salvo casquer\u00EDas."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2355.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10991.07)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",35.90)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",81.88)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",39.05)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1753.76)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",2.21)}
				)
		,E_642_2 ("642.2","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados, salvo casquer\u00EDas."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2355.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10991.07)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",35.90)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",81.88)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",39.05)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1753.76)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",2.21)}
				)
		,E_642_3 ("642.3","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados, salvo casquer\u00EDas."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2355.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10991.07)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",35.90)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",81.88)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",39.05)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1753.76)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",2.21)}
				)
		,E_642_4 ("642.4","Comercio al por menor de carne y despojos; de productos y derivados c\u00E1rnicos elaborados, salvo casquer\u00EDas."
				,0
				,5
				,21635.71
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2355.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10991.07)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",35.90)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",81.88)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",39.05)}
				,null
				)
		,E_642_5 ("642.5","Comerciantes minoristas matriculados en el ep\u00EDgrafe 642.5 por el asado de pollos."
				,32
				,4
				,20136.65
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3382.36)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11337.49)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",25.19)
					,new Module(4,FiscalActivityInfoKey.M10,"Metro cua.",27.08)
					,new Module(5,FiscalActivityInfoKey.M11,"Metro cua.",58.57)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",673.16)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",13.29)
					,new Module(3,FiscalActivityInfoKey.M57,"Pieza",62.89)}
				)
		,E_642_6 ("642.6","Comercio al por menor, en casquer\u00EDas, de v\u00EDsceras y despojos procedentes de animales de abasto, frescos y congelados."
				,0
				,5
				,16237.81
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2254.90)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11098.14)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",27.71)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",69.28)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",35.90)}
				,null
				)
		,E_643_1 ("643.1","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0
				,5
				,24551.97
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3823.25)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13296.36)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",36.53)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",113.37)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",28.98)}
				,null
				)
		,E_643_2 ("643.2","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0
				,5
				,24551.97
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3823.25)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13296.36)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",36.53)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",113.37)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",28.98)}
				,null
				)
		,E_644_1 ("644.1","Comercio al por menor de pan, pasteler\u00EDa, confiter\u00EDa y similares y de leche y productos l\u00E1cteos."
				,20
				,6
				,43605.26
				,new Module[]{new Module(1,FiscalActivityInfoKey.M15,"Persona",6248.22)
					,new Module(2,FiscalActivityInfoKey.M16,"Persona",1058.17)
					,new Module(3,FiscalActivityInfoKey.M02,"Persona",14530.89)
					,new Module(4,FiscalActivityInfoKey.M17,"Metro cua.",49.13)
					,new Module(5,FiscalActivityInfoKey.M18,"Metro cua.",34.01)
					,new Module(6,FiscalActivityInfoKey.M19,"Metro cua.",125.97)
					,new Module(7,FiscalActivityInfoKey.M14,"100dm.cua.",629.86)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2090.35)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",8.86)
					,new Module(3,FiscalActivityInfoKey.M14,"100 dmcua.",40.75)
					,new Module(4,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_644_2 ("644.2","Despachos de pan, panes especiales y boller\u00EDa."
				,20
				,6
				,42925.01
				,new Module[]{new Module(1,FiscalActivityInfoKey.M15,"Persona",6134.85)
					,new Module(2,FiscalActivityInfoKey.M16,"Persona",1039.27)
					,new Module(3,FiscalActivityInfoKey.M02,"Persona",14266.34)
					,new Module(4,FiscalActivityInfoKey.M17,"Metro cua.",48.50)
					,new Module(5,FiscalActivityInfoKey.M18,"Metro cua.",33.38)
					,new Module(6,FiscalActivityInfoKey.M19,"Metro cua.",125.97)
					,new Module(7,FiscalActivityInfoKey.M14,"100dm.cua.",629.86)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2090.35)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",8.86)
					,new Module(3,FiscalActivityInfoKey.M14,"100 dmcua.",40.75)
					,new Module(4,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_644_3 ("644.3","Comercio al por menor de productos de pasteler\u00EDa, boller\u00EDa y confiter\u00EDa."
				,30
				,6
				,33760.53
				,new Module[]{new Module(1,FiscalActivityInfoKey.M15,"Persona",6367.89)
					,new Module(2,FiscalActivityInfoKey.M16,"Persona",1014.08)
					,new Module(3,FiscalActivityInfoKey.M02,"Persona",12912.15)
					,new Module(4,FiscalActivityInfoKey.M17,"Metro cua.",43.46)
					,new Module(5,FiscalActivityInfoKey.M18,"Metro cua.",34.01)
					,new Module(6,FiscalActivityInfoKey.M19,"Metro cua.",113.37)
					,new Module(7,FiscalActivityInfoKey.M14,"100dm.cua.",522.78)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3064.67)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",8.86)
					,new Module(3,FiscalActivityInfoKey.M14,"100 dmcua.",59.34)
					,new Module(4,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_644_6 ("644.6","Comercio al por menor de masas fritas, con o sin coberturas o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparados de chocolate y bebidas refrescantes."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,FiscalActivityInfoKey.M15,"Persona",6852.88)
					,new Module(2,FiscalActivityInfoKey.M16,"Persona",2254.90)
					,new Module(3,FiscalActivityInfoKey.M02,"Persona",13214.47)
					,new Module(4,FiscalActivityInfoKey.M17,"Metro cua.",27.71)
					,new Module(5,FiscalActivityInfoKey.M18,"Metro cua.",21.41)
					,new Module(6,FiscalActivityInfoKey.M19,"Metro cua.",36.53)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2550.92)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",10.63)
					,new Module(3,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_647_1 ("647.1","Comerciantes minoristas matriculados en el ep\u00EDgrafe 647.1 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,5
				,15822.10
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1026.67)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10839.90)
					,new Module(3,FiscalActivityInfoKey.M10,"Metro cua.",20.15)
					,new Module(4,FiscalActivityInfoKey.M11,"Metro cua.",68.65)
					,new Module(5,FiscalActivityInfoKey.M03,"100 Kwh",8.81)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_647_2 ("647.2","Comerciantes minoristas matriculados en el ep\u00EDgrafe 647.2 y 3 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,4
				,25219.62
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1788.80)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10827.31)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",23.31)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",32.75)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_647_3 ("647.3","Comerciantes minoristas matriculados en el ep\u00EDgrafe 647.2 y 3 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,4
				,25219.62
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1788.80)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10827.31)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",23.31)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",32.75)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_651_1 ("651.1","Comercio al por menor de productos textiles, confecciones para el hogar, alfombras y similares y art\u00EDculos de tapicer\u00EDa."
				,0
				,4
				,23638.67
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",3010.74)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",13812.85)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",38.42)
					,new Module(4,FiscalActivityInfoKey.M10,"Metro cua.",35.28)
					,new Module(5,FiscalActivityInfoKey.M11,"Metro cua.",107.07)}
				,null
				)
		,E_651_2 ("651.2","Comercio al por menor de toda clase de prendas para el vestido y tocado."
				,0
				,5
				,24848.00
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2569.83)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13995.51)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",49.13)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",56.69)}
				,null
				)
		,E_651_3 ("651.3","Comercio al por menor de lencer\u00EDa, corseter\u00EDa y prendas especiales."
				,0
				,3
				,19626.46
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2198.21)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11998.85)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",47.87)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",75.58)}
				,null
				)
		,E_651_4 ("651.4","Comercio al por menor de art\u00EDculos de mercer\u00EDa y paqueter\u00EDa."
				,0
				,4
				,14862.05
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1902.18)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10291.93)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",28.98)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",58.57)}
				,null
				)
		,E_651_5 ("651.5","Comercio al por menor de lencer\u00EDa, corseter\u00EDa y prendas especiales."
				,0
				,3
				,19626.46
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2198.21)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11998.85)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",47.87)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",75.58)}
				,null
				)
		,E_651_6 ("651.6","Comercio al por menor de calzado, art\u00EDculos de piel e imitaci\u00F3n o productos sustitutivos, cinturones, carteras, bolsos, maletas y art\u00EDculos de viaje en general."
				,0
				,5
				,24306.32
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3130.41)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13453.83)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",27.71)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",52.27)}
				,null
				)
		,E_652_2 ("652.2","Comerciantes minoristas matriculados en el ep\u00EDgrafe 652.2 y 3 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,4
				,25333.00
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",3722.48)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",12786.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",31.49)
					,new Module(4,FiscalActivityInfoKey.M10,"Metro cua.",18.27)
					,new Module(5,FiscalActivityInfoKey.M11,"Metro cua.",55.43)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_652_3 ("652.3","Comerciantes minoristas matriculados en el ep\u00EDgrafe 652.2 y 3 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,4
				,25333.00
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",3722.48)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",12786.18)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",31.49)
					,new Module(4,FiscalActivityInfoKey.M10,"Metro cua.",18.27)
					,new Module(5,FiscalActivityInfoKey.M11,"Metro cua.",55.43)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_653_1 ("653.1","Comercio al por menor de muebles."
				,0
				,4
				,30718.31
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4075.20)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16200.02)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",50.39)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",16.38)}
				,null
				)
		,E_653_2 ("653.2","Comercio al por menor de material y aparatos el\u00E9ctricos, electr\u00F3nicos, electrodom\u00E9sticos y otros aparatos de uso dom\u00E9stico accionados por otro tipo de energ\u00EDa distinta de la el\u00E9ctrica, as\u00ED como muebles de cocina"
				,48
				,3
				,26189.61
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2884.77)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14656.86)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",100.78)
					,new Module(4,FiscalActivityInfoKey.M10,"Metro cua.",36.53)
					,new Module(5,FiscalActivityInfoKey.M11,"Metro cua.",113.37)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M27,"Persona",5819.91)
					,new Module(2,FiscalActivityInfoKey.M28,"Metro cua.",5.71)}
				)
		,E_653_3 ("653.3","Comercio al por menor de art\u00EDculos de menaje, ferreter\u00EDa, adorno, regalo, o reclamo (incluyendo bisuter\u00EDa y peque\u00F1os electrodom\u00E9sticos)."
				,0
				,4
				,24470.09
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Personas",3709.88)
					,new Module(2,FiscalActivityInfoKey.M02,"Personas",15116.66)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",51.02)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",21.41)}
				,null
				)
		,E_653_4 ("653.4","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,13
				,3
				,26454.15
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3061.12)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16527.55)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",94.48)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",8.81)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",12317.71)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",239.74)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",9.10)}
				)
		,E_653_5 ("653.5","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,13
				,3
				,26454.15
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3061.12)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16527.55)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",94.48)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",8.81)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",12317.71)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",239.74)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",9.10)}
				)
		,E_653_9 ("653.9","Comercio al por menor de otros art\u00EDculos para el equipamiento del hogar n.c.o.p."
				,0
				,3
				,32765.35
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4950.71)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20061.07)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",81.88)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",39.68)}
				,null
				)
		,E_654_2 ("654.2","Comercio al por menor de accesorios y piezas de recambio para veh\u00EDculos terrestres."
				,13
				,4
				,32815.74
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3098.92)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17018.84)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",201.55)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",617.26)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",15558.35)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",272.80)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",620.03)}
				)
		,E_654_5 ("654.5","Comercio al por menor de toda clase de maquinaria (excepto aparatos del hogar, de oficina, m\u00E9dicos, ortop\u00E9dicos, \u00F3pticos y fotogr\u00E1ficos)."
				,13
				,3
				,31367.06
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",9422.71)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",18858.03)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",39.05)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",18021.89)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",74.41)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",82.66)}
				)
		,E_654_6 ("654.6","Comercio al por menor de cubiertas, bandas o bandajes y c\u00E1maras de aire para toda clase de veh\u00EDculos, excepto las actividades de comercio al por mayor de los art\u00EDculos citados."
				,13
				,4
				,26970.63
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2746.19)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14222.25)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",119.67)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",377.92)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",14152.98)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",206.67)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",372.02)}
				)

		,E_659_2 ("659.2","Comercio al por menor de muebles de oficina y de m\u00E1quinas y equipos de oficina."
				,0
				,4
				,30718.31
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4157.08)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16521.25)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",56.69)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",17.01)}
				,null
				)
		,E_659_3 ("659.3","Comerciantes minoristas matriculados en el ep\u00EDgrafe 659.3 por el servicio de recogida de negativos y otro material fotogr\u00E1fico impresionado para su procesado en laboratorio de terceros y la entrega de las correspondientes copias y ampliaciones."
				,13
				,3
				,35524.14
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",7174.12)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",19273.74)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",119.67)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",1070.76)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M62,"Persona",14533.25)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",30.59)}
				)			
		,E_659_4A("659.4","Comercio al por menor de libros, peri\u00F3dicos, art\u00EDculos de papeler\u00EDa y escritorio y art\u00EDculos de dibujo y bellas artes, excepto en quioscos situados en la v\u00EDa p\u00FAblica."
				,75
				,3
				,25207.02
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4648.37)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17176.30)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",57.94)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",30.86)
					,new Module(5,FiscalActivityInfoKey.M24,"CVF",535.38)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_659_4B("659.4","Comercio al por menor de prensa, revistas y libros en quioscos situados en la v\u00EDa p\u00FAblica."
				,75
				,2
				,28860.22
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3476.83)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17220.39)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",403.11)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",844.02)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)

		,E_659_6 ("659.6","Comercio al por menor de juguetes, art\u00EDculos de deporte, prendas deportivas de vestido, calzado y tocado, armas, cartucher\u00EDa y art\u00EDculos de pirotecnia."
				,0
				,3
				,24948.78
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2916.26)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13258.56)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",138.57)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",32.75)}
				,null
				)
		,E_659_7 ("659.7","Comercio al por menor de semillas, abonos, flores y plantas y peque\u00F1os animales."
				,0
				,4
				,23978.80
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4988.50)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16124.43)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",258.24)}
				,null
				)

		,E_662_2 ("662.2","Comerciantes minoristas matriculados en el ep\u00EDgrafe 662.2 por el servicio de comercializaci\u00F3n de loter\u00EDas."
				,75
				,3
				,16395.27
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4868.82)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",9429.01)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",28.98)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",37.79)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_663_1 ("663.1","Comercio al por menor fuera de un establecimiento comercial permanente dedicado exclusivamente a la comercializaci\u00F3n de masas fritas, con o sin coberturas o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparaci\u00F3n de chocolate y bebidas refrescantes y facultado para la elaboraci\u00F3n de los productos propios de churrer\u00EDa y patatas fritas en la propia instalaci\u00F3n o veh\u00EDculo."
				,20
				,2
				,14379.72
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1398.29)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13989.21)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",113.37)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5881.32)
					,new Module(2,FiscalActivityInfoKey.M24,"CVF",43.40)}
				)
		
		,E_663_2 ("663.2","Comercio al por menor fuera de un establecimiento comercial permanente de art\u00EDculos textiles y de confecci\u00F3n."
				,0
				,2
				,19059.58
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2991.84)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13982.91)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",239.34)}
				,null
				)
		,E_663_3 ("663.3","Comercio al por menor fuera de un establecimiento comercial permanente de calzado, pieles y art\u00EDculos de cuero."
				,0
				,2
				,17081.82
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2613.92)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11186.32)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",151.16)}
				,null
				)
		,E_663_4 ("663.4","Comercio al por menor fuera de un establecimiento comercial permanente de art\u00EDculos de droguer\u00EDa y cosm\u00E9ticos y de productos qu\u00EDmicos en general."
				,0
				,2
				,16886.56
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3678.39)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",12641.30)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",113.37)}
				,null
				)
		,E_663_9 ("663.9","Comercio al por menor fuera de un establecimiento comercial permanente de otras clases de mercanc\u00EDas n.c.o.p."
				,0
				,2
				,18354.14
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",5448.29)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10537.57)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",283.44)}
				,null
				)
		,E_671_4 ("671.4","Restaurantes de dos tenedores."
				,13
				,10
				,51617.08
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3709.88)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17434.55)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",201.55)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",585.77)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",1077.06)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",3810.65)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2993.81)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",150.57)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",168.29)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",239.15)
					,new Module(5,FiscalActivityInfoKey.M07,"Maquina B",841.46)
					,new Module(6,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_671_5 ("671.5","Restaurantes de un tenedor."
				,20
				,10
				,38081.38
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3602.80)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16174.82)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",125.97)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",220.45)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",1077.06)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",3810.65)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2400.36)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",70.86)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",124.00)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",239.15)
					,new Module(5,FiscalActivityInfoKey.M07,"Maquina B",841.46)
					,new Module(6,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_672_1 ("672.1","Cafeter\u00EDas."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1448.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13743.56)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",478.69)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",377.92)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",957.39)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",3747.67)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2356.07)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",124.00)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",70.86)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",221.43)
					,new Module(5,FiscalActivityInfoKey.M07,"Maquina B",832.60)
					,new Module(6,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_672_2 ("672.2","Cafeter\u00EDas."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1448.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13743.56)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",478.69)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",377.92)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",957.39)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",3747.67)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2356.07)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",124.00)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",70.86)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",221.43)
					,new Module(5,FiscalActivityInfoKey.M07,"Maquina B",832.60)
					,new Module(6,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_672_3 ("672.3","Cafeter\u00EDas."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1448.68)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",13743.56)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",478.69)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",377.92)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",957.39)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",3747.67)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2356.07)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",124.00)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",70.86)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",221.43)
					,new Module(5,FiscalActivityInfoKey.M07,"Maquina B",832.60)
					,new Module(6,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_673_1 ("673.1","Caf\u00E9s y bares de categor\u00EDa especial."
				,6
				,8
				,30586.03
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4056.30)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",15538.66)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",321.23)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",233.04)
					,new Module(5,FiscalActivityInfoKey.M05,"Metro",371.62)
					,new Module(6,FiscalActivityInfoKey.M06,"Maquina A",957.39)
					,new Module(7,FiscalActivityInfoKey.M07,"Maquina B",2903.66)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3294.97)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",69.09)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",60.23)
					,new Module(4,FiscalActivityInfoKey.M05,"Metro",77.95)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",221.43)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",655.45)
					,new Module(7,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_673_2 ("673.2","Otros caf\u00E9s y bares."
				,6
				,8
				,19084.78
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1643.93)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",11413.08)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",94.48)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",119.67)
					,new Module(5,FiscalActivityInfoKey.M05,"Metro",163.76)
					,new Module(6,FiscalActivityInfoKey.M06,"Maquina A",806.23)
					,new Module(7,FiscalActivityInfoKey.M07,"Maquina B",2947.75)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2577.52)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",47.83)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",56.69)
					,new Module(4,FiscalActivityInfoKey.M05,"Metros",62.89)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",177.15)
					,new Module(6,FiscalActivityInfoKey.M07,"Maquina B",655.45)
					,new Module(7,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_675   ("675"	 ,"Servicios en quioscos, cajones, barracas u otros locales an\u00E1logos."
				,3
				,3
				,16596.83
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2802.88)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14461.60)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",107.07)
					,new Module(4,FiscalActivityInfoKey.M09,"Metro cua.",26.45)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4357.86)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",50.48)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",4.06)
					,new Module(4,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_676   ("676"	 ,"Servicios en chocolater\u00EDas, helader\u00EDas y horchater\u00EDas."
				,20
				,3
				,25528.25
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2418.67)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20016.97)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",541.68)
					,new Module(4,FiscalActivityInfoKey.M04,"Mesa",220.45)
					,new Module(5,FiscalActivityInfoKey.M06,"Maquina A",806.23)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3817.55)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",141.72)
					,new Module(3,FiscalActivityInfoKey.M04,"Mesa",46.05)
					,new Module(4,FiscalActivityInfoKey.M06,"Maquina A",177.15)
					,new Module(5,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_681   ("681"	 ,"Servicio de hospedaje en hoteles y moteles de una o dos estrellas."
				,20
				,10
				,61512.19
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",6223.02)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20438.98)
					,new Module(3,FiscalActivityInfoKey.M20,"Plaza",371.62)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2604.09)
					,new Module(2,FiscalActivityInfoKey.M20,"Plaza",53.14)
					,new Module(3,FiscalActivityInfoKey.M58,"Euro",0.21)}
				)
		,E_682   ("682"	 ,"Servicio de hospedaje en hostales y pensiones."
				,20
				,8
				,32840.94
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",5145.96)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17541.62)
					,new Module(3,FiscalActivityInfoKey.M20,"Plaza",270.85)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2533.22)
					,new Module(2,FiscalActivityInfoKey.M20,"Plaza",55.80)}
				)
		,E_683   ("683"	 ,"Servicio de hospedaje en fondas y casas de hu\u00E9spedes."
				,30
				,8
				,16256.70
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4478.31)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14587.57)
					,new Module(3,FiscalActivityInfoKey.M20,"Plaza",132.27)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1762.62)
					,new Module(2,FiscalActivityInfoKey.M20,"Plaza",29.22)}
				)
		,E_691_1 ("691.1","Reparaci\u00F3n de art\u00EDculos el\u00E9ctricos para el hogar."
				,48
				,3
				,21585.33
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4314.54)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",15538.66)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",17.01)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5819.91)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.71)}
				)
		,E_691_2 ("691.2","Reparaci\u00F3n de veh\u00EDculos autom\u00F3viles, bicicletas y otros veh\u00EDculos."
				,30
				,5
				,33729.04
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4157.08)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17094.42)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",27.08)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8556.27)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",16.54)}
				)
		,E_691_9A("691.9","Reparaci\u00F3n de calzado."
				,48
				,2
				,16552.74
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1845.50)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10014.78)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",125.97)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3000.89)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",36.37)}
				)
		,E_691_9B("691.9","Reparaci\u00F3n de otros bienes de consumo n.c.o.p. (excepto reparaci\u00F3n de calzado, restauraci\u00F3n de obras de arte, muebles, antig\u00FCedades e instrumentos musicales)."
				,48
				,2
				,24803.91
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4094.10)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16187.42)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",45.35)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5381.76)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",13.23)}
				)
		
		,E_692   ("692"	 ,"Reparaci\u00F3n de maquinaria industrial."
				,30
				,2
				,30352.99
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4409.02)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",18146.29)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",94.48)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",9721.90)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",53.75)}
				)
		,E_699   ("699"	 ,"Otras reparaciones n.c.o.p."
				,32
				,2
				,23607.18
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3703.58)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",15230.03)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",88.18)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",7671.69)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",46.31)}
				)
		,E_721_1 ("721.1","Transporte urbano colectivo y de viajeros por carretera."
				,1
				,5
				,35196.62
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2981.02)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16016.97)
					,new Module(3,FiscalActivityInfoKey.M21,"Asiento",121.40)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1700.62)
					,new Module(2,FiscalActivityInfoKey.M21,"Asiento",79.72)}
				)
		,E_721_2 ("721.2","Transporte por autotaxis."
				,10
				,3
				,9999999.99
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1346.27)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",7656.89)
					,new Module(3,FiscalActivityInfoKey.M12,"1000 Km",45.08)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",903.46)
					,new Module(2,FiscalActivityInfoKey.M12,"1000 Km",8.78)}
				)
		,E_721_3 ("721.3","Transporte urbano colectivo y de viajeros por carretera."
				,1
				,5
				,35196.62
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2981.02)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16016.97)
					,new Module(3,FiscalActivityInfoKey.M21,"Asiento",121.40)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1700.62)
					,new Module(2,FiscalActivityInfoKey.M21,"Asiento",79.72)}
				)
		,E_722A  ("722"	 ,"Transporte de mercanc\u00EDas por carretera, expto residuos"
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2728.59)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10090.99)
					,new Module(3,FiscalActivityInfoKey.M23,"Tonelada",126.21)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4149.99)
					,new Module(2,FiscalActivityInfoKey.M23,"Tonelada",388.55)}
				)
		,E_722B  ("722"	 ,"Transporte de residuos por carretera."
				,1
				,5
				,33640.86
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2728.59)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10090.99)
					,new Module(3,FiscalActivityInfoKey.M23,"Tonelada",126.21)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",1948.64)
					,new Module(2,FiscalActivityInfoKey.M23,"Tonelada",181.58)}
				)

		,E_751_5 ("751.5","Engrase y lavado de veh\u00EDculos."
				,30
				,5
				,28280.74
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4667.27)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",19191.86)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",30.23)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",8556.27)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",16.54)}
				)
		,E_757   ("757"	 ,"Servicios de mudanzas."
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2566.32)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10175.13)
					,new Module(3,FiscalActivityInfoKey.M23,"Tonelada",48.08)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",5712.43)
					,new Module(2,FiscalActivityInfoKey.M23,"Tonelada",256.27)}
				)
		,E_849_5 ("849.5","Transporte de mensajer\u00EDa y recader\u00EDa, cuando la actividad se realice exclusivamente con medios de transporte propios."
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",2728.59)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",10090.99)
					,new Module(3,FiscalActivityInfoKey.M23,"Tonelada",126.21)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",4149.99)
					,new Module(2,FiscalActivityInfoKey.M23,"Tonelada",388.55)}
				)
		,E_933_1 ("933.1","Ense\u00F1anza de conducci\u00F3n de veh\u00EDculos terrestres, acu\u00E1ticos, aeron\u00E1uticos, etc."
				,48
				,4
				,47233.25
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3067.42)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",20596.45)
					,new Module(3,FiscalActivityInfoKey.M25,"veh\u00EDculo",774.72)
					,new Module(4,FiscalActivityInfoKey.M24,"CVF",258.24)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3000.89)
					,new Module(2,FiscalActivityInfoKey.M25,"veh\u00EDculo",256.27)
					,new Module(3,FiscalActivityInfoKey.M24,"CVF",107.47)}
				)
		,E_933_9 ("933.9","Otras actividades de ense\u00F1anza, tales como idiomas, corte y confecci\u00F3n, mecanograf\u00EDa, taquigraf\u00EDa, preparaci\u00F3n de ex\u00E1menes y oposiciones y similares n.c.o.p."
				,48
				,5
				,33697.55
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1253.49)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",15727.62)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",62.36)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2413.95)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",2.64)}
				)
		,E_967_2 ("967.2","Escuelas y servicios de perfeccionamiento del deporte."
				,3
				,3
				,37067.30
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",7035.55)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14215.95)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",34.01)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3441.10)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",5.58)}
				)
		,E_971_1 ("971.1","Tinte, limpieza en seco, lavado y planchado de ropas hechas y de prendas y art\u00EDculos del hogar usados."
				,48
				,4
				,37224.77
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4553.90)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",16773.19)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",45.98)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",3844.13)
					,new Module(2,FiscalActivityInfoKey.M03,"100 Kwh",14.06)}
				)
		,E_972_1 ("972.1","Servicios de peluquer\u00EDa de se\u00F1ora y caballero."
				,13
				,6
				,18051.81
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",3161.90)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",9649.47)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",94.48)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",81.88)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2562.75)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",41.33)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",17.48)}
				)
		,E_972_2 ("972.2","Salones e institutos de belleza."
				,32
				,6
				,26945.44
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",1788.80)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",14896.21)
					,new Module(3,FiscalActivityInfoKey.M09,"Metro cua.",88.18)
					,new Module(4,FiscalActivityInfoKey.M03,"100 Kwh",55.43)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",2562.75)
					,new Module(2,FiscalActivityInfoKey.M09,"Metro cua.",41.33)
					,new Module(3,FiscalActivityInfoKey.M03,"100 Kwh",17.36)}
				)
		,E_973_3 ("973.3","Servicios de copias de documentos con m\u00E1quinas fotocopiadoras."
				,30
				,4
				,24192.95
				,new Module[]{new Module(1,FiscalActivityInfoKey.M01,"Persona",4125.59)
					,new Module(2,FiscalActivityInfoKey.M02,"Persona",17044.03)
					,new Module(3,FiscalActivityInfoKey.M08,"Kw cont.",541.68)}
				,new Module[]{new Module(1,FiscalActivityInfoKey.M26,"Persona",13136.13)
					,new Module(2,FiscalActivityInfoKey.M08,"Kw cont.",239.74)}
				)
				;
		private String epigraph;
		private String description;
		private Module[] irpfModules;
		private Module[] vatModules;
		private double porcMin;
		private double limPers;
		private double limExceso;
		
		private Epigraph(String epigraph, String description,
				double porcMin,double limPers,double limExceso
				, Module[] irpfModules, Module[] ivaModules) {
			this.epigraph = epigraph;
			this.description = description;
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
	}
}
