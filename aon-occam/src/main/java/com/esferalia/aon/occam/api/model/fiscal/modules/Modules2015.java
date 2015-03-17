package com.esferalia.aon.occam.api.model.fiscal.modules;

public class Modules2015 {

	public enum FarmerIRPF {
		 A01 (0.13,0.23,"Agrícola dedicada a la obtención de remolacha azucarera y ganadera de explotación de ganado porcino de carne, de ganado bovino de carne, de ganado ovino de carne, de ganado caprino de carne, avicultura y cunicultura.")
		,A02 (0.13,0.23,"Forestal con un \"período medio de corta\" superior a 30 años.")
		,A03 (0.26,0.36,"Agrícola dedicada a la obtención de cereales, cítricos, frutos secos, hortícultura, leguminosas, uva para vino de mesa sin denominación de origen, productos del olivo y hongos para el consumo humano y ganadera de explotación de ganado porcino de cría, de ganado bovino de cría, de ganado ovino de leche, de ganado caprino de leche y apicultura.")
		,A04 (0.26,0.36,"Forestal con un \"período medio de corta\" igual o inferior a 30 años.")
		,A05 (0.32,0.42,"Agrícola dedicada a la obtención de uva para vino de mesa con denominación de origen, y oleaginosas, ganadera de explotación de ganado bovino de leche y otras actividades ganaderas no comprendidas expresamente en otros apartados y forestal dedicada a la extracción de resina.")
		,A06 (0.37,0.47,"Agrícola dedicada a la obtención de raíces, tubérculos, forrajes, arroz, algodón, frutos no cítricos, tabaco y otros productos agrícolas no comprendidos expresamente en otros apartados.")
		,A07 (0.42,0.52,"Agrícola dedicada a la obtención de plantas textiles y uva de mesa, actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales y servicios de cría, guarda y engorde de aves.")
		,A08 (0.56,0.00,"Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales y servicios de cría, guarda y engorde de ganado, excepto aves.")
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
		 A01 (0.10,	  12.00, "Ganadera de explotación intensiva de ganado porcino de carne y avicultura de carne.")
		,A02 (0.04,	   2.00, "Ganadera de explotación intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche.")
		,A03 (0.10,	  24.00, "Ganadera de explotación intensiva de ganado bovino de carne y cunicultura.")
		,A04 (0.10,	  32.00, "Ganadera de explotación intensiva de ganado porcino de cría, bovino de cría y otras intensivas o extensivas no comprendidas expresamente en otros apartados.")
		,A05 (0.10,	  40.00, "Ganadera de explotación intensiva de ganado ovino y caprino de carne.")
		,A06 (0.09375,40.00, "Servicios de cría, guarda y engorde de aves.")
		,A07 (0.10,	  48.00, "Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales que estén excluidos del régimen especial de la agricultura, ganadería y pesca del Impuesto sobre el Valor Añadido, y servicios de cría, guarda y engorde de ganado, excepto aves.")
		,A08 (0.21,   80.00, "Actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales no incluidas en el régimen especial de la agricultura, ganadería y pesca del Impuesto sobre el Valor Añadido.")
		,A09 (0.04,    2.00, "Aprovechamientos que correspondan al cedente en las actividades agrícolas, desarrolladas en régimen de aparcería, dedicadas a la obtención de productos agrícolas no comprendidas en los apartados siguientes.")
		,A10 (0.07625,28.00, "Aprovechamientos que correspondan al cedente en las actividades agrícolas, desarrolladas en régimen de aparcería, dedicadas a la obtención de forrajes.")
		,A11 (0.21,	  44.00, "Aprovechamientos que correspondan al cedente en las actividades agrícolas, desarrolladas en régimen de aparcería, dedicadas a la obtención de plantas textiles y tabaco.")
		,A12 (0.21,	  44.00, "Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en régimen de aparcería.")
		,A13 (0.070,  28.00, "Procesos de transformación, elaboración o manufactura de productos naturales para la obtención de queso.")
		,A14 (0.2675, 80.00, "Procesos de transformación, elaboración o manufactura de productos naturales para la obtención de vino de mesa.")
		,A15 (0.2675, 80.00, "Procesos de transformación, elaboración o manufactura de productos naturales para la obtención de vino con denominación de origen.")
		,A16 (0.19625,80.00, "Procesos de transformación, elaboración o manufactura de productos naturales para la obtención de otros productos distintos a los anteriores.")
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
	
	public enum Epigraph implements IModuleEpigraph {
		E_314(
				"314",
				"Carpintería metálica y fabricación de estructuras metálicas y calderería.",
				30,
				4,
				32475.62,
				new Module[] {new Module(1, "Personal asalariado", "Persona", 3577.61),
						new Module(2, "Personal no asalariado", "Persona",17044.03),
						new Module(3, "Consumo de energía eléctrica","100 Kwh", 61.10),
						new Module(4, "Potencia fiscal vehículo", "CVF", 170.06) },
				new Module[] {new Module(1, "Personal empleado", "Persona", 5968.73),
						new Module(2, "Consumo de energía eléctrica","100 Kwh", 43.81),
						new Module(3, "Potencia fiscal vehículo", "CVF", 289.34) }		 
				 )
		, E_315(
				"315",
				"Carpintería metálica y fabricación de estructuras metálicas y calderería.",
				30,
				4,
				32475.62,
				new Module[] {new Module(1, "Personal asalariado", "Persona", 3577.61),
						new Module(2, "Personal no asalariado", "Persona",17044.03),
						new Module(3, "Consumo de energía eléctrica","100 Kwh", 61.10),
						new Module(4, "Potencia fiscal vehículo", "CVF", 170.06) },
				new Module[] {new Module(1, "Personal empleado", "Persona", 5968.73),
						new Module(2, "Consumo de energía eléctrica","100 Kwh", 43.81),
						new Module(3, "Potencia fiscal vehículo", "CVF", 289.34) })
						
		,E_316_2 ("316.2","Fabricación de artículos de ferretería, cerrajería, tornillería, derivados del alambre, menaje y otros artículos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,"Personal asalariado","Persona",3678.39)
					,new Module(2,"Personal no asalariado","Persona",16351.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",62.98)
					,new Module(4,"Potencia fiscal vehículo","CVF",125.97)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8010.65)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",42.16)
					,new Module(3,"Potencia fiscal vehículo","CVF",28.10)}
				)				
		,E_316_3 ("316.3","Fabricación de artículos de ferretería, cerrajería, tornillería, derivados del alambre, menaje y otros artículos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,"Personal asalariado","Persona",3678.39)
					,new Module(2,"Personal no asalariado","Persona",16351.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",62.98)
					,new Module(4,"Potencia fiscal vehículo","CVF",125.97)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8010.65)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",42.16)
					,new Module(3,"Potencia fiscal vehículo","CVF",28.10)}
				)
		,E_316_4 ("316.4","Fabricación de artículos de ferretería, cerrajería, tornillería, derivados del alambre, menaje y otros artículos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,"Personal asalariado","Persona",3678.39)
					,new Module(2,"Personal no asalariado","Persona",16351.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",62.98)
					,new Module(4,"Potencia fiscal vehículo","CVF",125.97)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8010.65)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",42.16)
					,new Module(3,"Potencia fiscal vehículo","CVF",28.10)}
				)
		,E_316_9 ("316.9","Fabricación de artículos de ferretería, cerrajería, tornillería, derivados del alambre, menaje y otros artículos en metales n.c.o.p."
				,32
				,5
				,32752.76
				,new Module[]{new Module(1,"Personal asalariado","Persona",3678.39)
					,new Module(2,"Personal no asalariado","Persona",16351.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",62.98)
					,new Module(4,"Potencia fiscal vehículo","CVF",125.97)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8010.65)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",42.16)
					,new Module(3,"Potencia fiscal vehículo","CVF",28.10)}
				)
		,E_419_1 ("419.1","Industrias del pan y de la bollería."
				,20
				,6
				,41602.30
				,new Module[]{new Module(1,"Personal asalariado","Persona",6248.22)
					,new Module(2,"Personal no asalariado","Persona",14530.89)
					,new Module(3,"Superficie del local","Metro cua.",49.13)
					,new Module(4,"Superficie del horno","100dm.cua.",629.86)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1693.54)
					,new Module(2,"Superficie del local","Metro cua.",7.09)
					,new Module(3,"Superficie del horno","100dm.cua.",30.47)}
				)
		,E_419_2 ("419.2","Industrias de la bollería, pastelería y galletas."
				,30
				,6
				,33760.53
				,new Module[]{new Module(1,"Personal asalariado","Persona",6657.63)
					,new Module(2,"Personal no asalariado","Persona",13485.32)
					,new Module(3,"Superficie del local","Metro cua.",45.35)
					,new Module(4,"Superficie del horno","100dm.cua.",541.68)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3064.67)
					,new Module(2,"Superficie del local","Metro cua.",8.86)
					,new Module(3,"Superficie del horno","100dm.cua.",59.34)}
				)
		,E_419_3 ("419.3","Industrias de elaboración de masas fritas."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,"Personal asalariado","Persona",4238.96)
					,new Module(2,"Personal no asalariado","Persona",12301.18)
					,new Module(3,"Superficie del local","Metro cua.",25.82)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2550.92)
					,new Module(2,"Superficie del local","Metro cua.",10.63)}
				)
		,E_423_9 ("423.9","Elaboración de patatas fritas, palomitas de maíz y similares."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,"Personal asalariado","Persona",4390.12)
					,new Module(2,"Personal no asalariado","Persona",12723.18)
					,new Module(3,"Superficie del local","Metro cua.",26.45)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2550.92)
					,new Module(2,"Superficie del local","Metro cua.",10.63)}
				)
		,E_453A  ("453"  ,"Confección en serie de prendas de vestir y sus complementos, excepto cuando su ejecución se realice mayoritariamente por encargo a terceros."
				,32
				,5
				,38969.48
				,new Module[]{new Module(1,"Personal asalariado","Persona",3382.36)
					,new Module(2,"Personal no asalariado","Persona",13730.96)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",125.97)
					,new Module(4,"Potencia fiscal vehículo","CVF",529.08)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5232.96)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",25.62)
					,new Module(3,"Potencia fiscal vehículo","CVF",198.40)}
				)
		,E_453B  ("453"  ,"Confección en serie de prendas de vestir y sus complementos, ejecutada directamente por la propia empresa, cuando se realice exclusivamente para terceros y por encargo."
				,32
				,5
				,29225.54
				,new Module[]{new Module(1,"Personal asalariado","Persona",2538.34)
					,new Module(2,"Personal no asalariado","Persona",10298.22)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",94.48)
					,new Module(4,"Potencia fiscal vehículo","CVF",396.81)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3926.79)
					,new Module(2,"Consumo energía eléctrica","100 Kwh",19.01)
					,new Module(3,"Potencia fiscal vehículo","CVF",148.80)}
				)
		,E_463   ("463"  ,"Fabricación en serie de piezas de carpintería, parqué y estructuras de madera para la construcción."
				,32
				,5
				,28463.40
				,new Module[]{new Module(1,"Personal asalariado","Personas",4037.41)
					,new Module(2,"Personal no asalariado","Personas",18404.53)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",62.98)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8126.38)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",13.23)}
				)
		,E_468   ("468"  ,"Industria del mueble de madera."
				,32
				,4
				,29534.17
				,new Module[]{new Module(1,"Personal asalariado","Persona",2947.75)
					,new Module(2,"Personal no asalariado","Persona",16300.79)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",49.76)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5522.30)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",28.10)}
				)
		,E_474_1 ("474.1","Impresión de textos o imágenes."
				,30
				,4
				,40418.16
				,new Module[]{new Module(1,"Personal asalariado","Persona",5208.95)
					,new Module(2,"Personal no asalariado","Persona",21534.93)
					,new Module(3,"Potencia eléctrica","Kw cont.",484.99)
					,new Module(4,"Potencia fiscal vehículo","CVF",680.25)}
				,new Module[]{new Module(1,"Personal empleado","Persona",7861.85)
					,new Module(2,"Potencia eléctrica","Kw cont.",165.34)
					,new Module(3,"Potencia fiscal vehículo","CVF",264.53)}
				)
		,E_501_3 ("501.3","Albañilería y pequeños trabajos de construcción en general."
				,9
				,6
				,32078.80
				,new Module[]{new Module(1,"Personal asalariado","Personas",3640.59)
					,new Module(2,"Personal no asalariado","Personas",17988.83)
					,new Module(3,"Superficie del local","Metro cua.",46.60)
					,new Module(4,"Potencia fiscal vehículo","CVF",201.55)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3401.25)
					,new Module(2,"Superficie del local","Metro cua.",23.03)
					,new Module(3,"Potencia fiscal vehículo","CVF",76.18)}
				)
		,E_504_1 ("504.1","Instalaciones y montajes (excepto fontanería, frío, calor y acondicionamiento de aire)."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_504_2 ("504.2","Instalaciones de fontanería, frío, calor y acondicionamiento de aire."
				,20
				,4
				,33332.23
				,new Module[]{new Module(1,"Personal asalariado","Persona",8238.58)
					,new Module(2,"Personal no asalariado","Persona",22360.05)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",138.57)
					,new Module(4,"Potencia fiscal vehículo","CVF",138.57)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4775.14)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",50.61)
					,new Module(3,"Potencia fiscal vehículo","CVF",2.96)}
				)
		,E_504_3 ("504.3","Instalaciones de fontanería, frío, calor y acondicionamiento de aire."
				,20
				,4
				,33332.23
				,new Module[]{new Module(1,"Personal asalariado","Persona",8238.58)
					,new Module(2,"Personal no asalariado","Persona",22360.05)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",138.57)
					,new Module(4,"Potencia fiscal vehículo","CVF",138.57)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4775.14)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",50.61)
					,new Module(3,"Potencia fiscal vehículo","CVF",2.96)}
				)
		,E_504_4 ("504.4","Instalación de pararrayos y similares. Montaje e instalación de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalación de aparatos elevadores de cualquier clase y tipo. Instalaciones telefónicas, telegráficas, telegráficas sin hilos y de televisión, en edificios y construcciones de cualquier clase. Montajes metálicos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalación o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_504_5 ("504.5","Instalación de pararrayos y similares. Montaje e instalación de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalación de aparatos elevadores de cualquier clase y tipo. Instalaciones telefónicas, telegráficas, telegráficas sin hilos y de televisión, en edificios y construcciones de cualquier clase. Montajes metálicos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalación o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_504_6 ("504.6","Instalación de pararrayos y similares. Montaje e instalación de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalación de aparatos elevadores de cualquier clase y tipo. Instalaciones telefónicas, telegráficas, telegráficas sin hilos y de televisión, en edificios y construcciones de cualquier clase. Montajes metálicos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalación o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_504_7 ("504.7","Instalación de pararrayos y similares. Montaje e instalación de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalación de aparatos elevadores de cualquier clase y tipo. Instalaciones telefónicas, telegráficas, telegráficas sin hilos y de televisión, en edificios y construcciones de cualquier clase. Montajes metálicos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalación o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_504_8 ("504.8","Instalación de pararrayos y similares. Montaje e instalación de cocinas de todo tipo y clase, con todos sus accesorios. Montaje e instalación de aparatos elevadores de cualquier clase y tipo. Instalaciones telefónicas, telegráficas, telegráficas sin hilos y de televisión, en edificios y construcciones de cualquier clase. Montajes metálicos e instalaciones industriales completas, sin vender ni aportar la maquinaria ni los elementos objeto de la instalación o montaje."
				,19
				,3
				,40002.45
				,new Module[]{new Module(1,"Personal asalariado","Persona",6575.75)
					,new Module(2,"Personal no asalariado","Persona",20854.69)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",69.28)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4601.93)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",30.31)
					,new Module(3,"Potencia fiscal vehículo","CVF",3.70)}
				)
		,E_505_1 ("505.1","Revestimientos, solados y pavimentos y colocación de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,"Personal asalariado","Persona",4112.99)
					,new Module(2,"Personal no asalariado","Persona",20325.60)
					,new Module(3,"Superficie del local","Metro cua.",21.41)
					,new Module(4,"Potencia fiscal vehículo","CVF",245.64)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2432.84)
					,new Module(2,"Superficie del local","Metro cua.",5.91)
					,new Module(3,"Potencia fiscal vehículo","CVF",59.05)}
				)
		,E_505_2 ("505.2","Revestimientos, solados y pavimentos y colocación de aislamientos."	,20
				,4
				,30038.06
				,new Module[]{new Module(1,"Personal asalariado","Persona",4112.99)
					,new Module(2,"Personal no asalariado","Persona",20325.60)
					,new Module(3,"Superficie del local","Metro cua.",21.41)
					,new Module(4,"Potencia fiscal vehículo","CVF",245.64)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2432.84)
					,new Module(2,"Superficie del local","Metro cua.",5.91)
					,new Module(3,"Potencia fiscal vehículo","CVF",59.05)}
				)
		,E_505_3 ("505.3","Revestimientos, solados y pavimentos y colocación de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,"Personal asalariado","Persona",4112.99)
					,new Module(2,"Personal no asalariado","Persona",20325.60)
					,new Module(3,"Superficie del local","Metro cua.",21.41)
					,new Module(4,"Potencia fiscal vehículo","CVF",245.64)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2432.84)
					,new Module(2,"Superficie del local","Metro cua.",5.91)
					,new Module(3,"Potencia fiscal vehículo","CVF",59.05)}
				)
		,E_505_4 ("505.4","Revestimientos, solados y pavimentos y colocación de aislamientos."
				,20
				,4
				,30038.06
				,new Module[]{new Module(1,"Personal asalariado","Persona",4112.99)
					,new Module(2,"Personal no asalariado","Persona",20325.60)
					,new Module(3,"Superficie del local","Metro cua.",21.41)
					,new Module(4,"Potencia fiscal vehículo","CVF",245.64)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2432.84)
					,new Module(2,"Superficie del local","Metro cua.",5.91)
					,new Module(3,"Potencia fiscal vehículo","CVF",59.05)}
				)
		,E_505_5 ("505.5","Carpintería y cerrajería."
				,20
				,4
				,28356.33
				,new Module[]{new Module(1,"Personal asalariado","Persona",6689.12)
					,new Module(2,"Personal no asalariado","Persona",19154.07)
					,new Module(3,"Potencia fiscal vehículo","CVF",144.87)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3354.01)
					,new Module(2,"Potencia fiscal vehículo","CVF",16.93)}
				)
		,E_505_6 ("505.6","Pintura de cualquier tipo y clase y revestimiento con papel, tejidos o plásticos y terminación y decoración de edificios y locales."
				,30
				,3
				,26687.20
				,new Module[]{new Module(1,"Personal asalariado","Persona",6027.77)
					,new Module(2,"Personal no asalariado","Persona",17648.70)
					,new Module(3,"Potencia fiscal vehículo","CVF",144.87)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2629.68)
					,new Module(2,"Potencia fiscal vehículo","CVF",14.18)}
				)
		,E_505_7 ("505.7","Trabajos en yeso y escayola y decoración de edificios y locales."
				,30
				,3
				,26687.20
				,new Module[]{new Module(1,"Personal asalariado","Persona",6027.77)
					,new Module(2,"Personal no asalariado","Persona",17648.70)
					,new Module(3,"Potencia fiscal vehículo","CVF",144.87)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2629.68)
					,new Module(2,"Potencia fiscal vehículo","CVF",14.18)}
				)
		,E_641 ("641","Comercio al por menor de frutas, verduras, hortalizas y tubérculos."
				,0
				,5
				,16867.67
				,new Module[]{new Module(1,"Personal asalariado","Persona",2387.18)
					,new Module(2,"Personal no asalariado","Persona",10581.66)
					,new Module(3,"Superficie del local independiente","Metro cua.",57.94)
					,new Module(4,"Superficie del local no independiente","Metro cua.",88.18)
					,new Module(5,"Carga elementos de transporte","Kilogramo",1.01)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				,null
				)
		,E_642_1 ("642.1","Comercio al por menor de carne y despojos; de productos y derivados cárnicos elaborados, salvo casquerías."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,"Personal asalariado","Persona",2355.68)
					,new Module(2,"Personal no asalariado","Persona",10991.07)
					,new Module(3,"Superficie local independiente","Metro cua.",35.90)
					,new Module(4,"Superficie local no independiente","Metro cua.",81.88)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",39.05)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1753.76)
					,new Module(2,"Superficie del local","Metro cua.",2.21)}
				)
		,E_642_2 ("642.2","Comercio al por menor de carne y despojos; de productos y derivados cárnicos elaborados, salvo casquerías."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,"Personal asalariado","Persona",2355.68)
					,new Module(2,"Personal no asalariado","Persona",10991.07)
					,new Module(3,"Superficie local independiente","Metro cua.",35.90)
					,new Module(4,"Superficie local no independiente","Metro cua.",81.88)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",39.05)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1753.76)
					,new Module(2,"Superficie del local","Metro cua.",2.21)}
				)
		,E_642_3 ("642.3","Comercio al por menor de carne y despojos; de productos y derivados cárnicos elaborados, salvo casquerías."
				,32
				,5
				,21635.71
				,new Module[]{new Module(1,"Personal asalariado","Persona",2355.68)
					,new Module(2,"Personal no asalariado","Persona",10991.07)
					,new Module(3,"Superficie local independiente","Metro cua.",35.90)
					,new Module(4,"Superficie local no independiente","Metro cua.",81.88)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",39.05)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1753.76)
					,new Module(2,"Superficie del local","Metro cua.",2.21)}
				)
		,E_642_4 ("642.4","Comercio al por menor de carne y despojos; de productos y derivados cárnicos elaborados, salvo casquerías."
				,0
				,5
				,21635.71
				,new Module[]{new Module(1,"Personal asalariado","Persona",2355.68)
					,new Module(2,"Personal no asalariado","Persona",10991.07)
					,new Module(3,"Superficie local independiente","Metro cua.",35.90)
					,new Module(4,"Superficie local no independiente","Metro cua.",81.88)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",39.05)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				,null
				)
		,E_642_5 ("642.5","Comerciantes minoristas matriculados en el epígrafe 642.5 por el asado de pollos."
				,32
				,4
				,20136.65
				,new Module[]{new Module(1,"Personal asalariado","Persona",3382.36)
					,new Module(2,"Personal no asalariado","Persona",11337.49)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",25.19)
					,new Module(4,"Superficie del local independiente","Metro cua.",27.08)
					,new Module(5,"Superficie del local no independiente","Metro cua.",58.57)}
				,new Module[]{new Module(1,"Personal empleado","Persona",673.16)
					,new Module(2,"Superficie del local","Metro cua.",13.29)
					,new Module(3,"Capacidad del asador","Pieza",62.89)}
				)
		,E_642_6 ("642.6","Comercio al por menor, en casquerías, de vísceras y despojos procedentes de animales de abasto, frescos y congelados."
				,0
				,5
				,16237.81
				,new Module[]{new Module(1,"Personal asalariado","Persona",2254.90)
					,new Module(2,"Personal no asalariado","Persona",11098.14)
					,new Module(3,"Superficie local independiente","Metro cua.",27.71)
					,new Module(4,"Superficie local no independiente","Metro cua.",69.28)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",35.90)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				,null
				)
		,E_643_1 ("643.1","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0
				,5
				,24551.97
				,new Module[]{new Module(1,"Personal asalariado","Persona",3823.25)
					,new Module(2,"Personal no asalariado","Persona",13296.36)
					,new Module(3,"Superficie local independiente","Metro cua.",36.53)
					,new Module(4,"Superficie local no independiente","Metro cua.",113.37)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",28.98)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				,null
				)
		,E_643_2 ("643.2","Comercio al por menor de pescados y otros productos de la pesca y de la acuicultura y de caracoles."
				,0
				,5
				,24551.97
				,new Module[]{new Module(1,"Personal asalariado","Persona",3823.25)
					,new Module(2,"Personal no asalariado","Persona",13296.36)
					,new Module(3,"Superficie local independiente","Metro cua.",36.53)
					,new Module(4,"Superficie local no independiente","Metro cua.",113.37)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",28.98)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				,null
				)
		,E_644_1 ("644.1","Comercio al por menor de pan, pastelería, confitería y similares y de leche y productos lácteos."
				,20
				,6
				,43605.26
				,new Module[]{new Module(1,"Personal asalariado de fabricación","Persona",6248.22)
					,new Module(2,"Resto personal asalariado","Persona",1058.17)
					,new Module(3,"Personal no asalariado","Persona",14530.89)
					,new Module(4,"Superficie del local de fabricación","Metro cua.",49.13)
					,new Module(5,"Resto superficie local independiente","Metro cua.",34.01)
					,new Module(6,"Resto superficie local no independiente.","Metro cua.",125.97)
					,new Module(7,"Superficie del horno","100dm.cua.",629.86)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2090.35)
					,new Module(2,"Superficie del local","Metro cua.",8.86)
					,new Module(3,"Superficie del horno","100 dmcua.",40.75)
					,new Module(4,"Importe total de las comisiones loterias","Euro",0.21)
					,new Module(5,"","",0.00)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				)
		,E_644_2 ("644.2","Despachos de pan, panes especiales y bollería."
				,20
				,6
				,42925.01
				,new Module[]{new Module(1,"Personal asalariado de fabricación","Persona",6134.85)
					,new Module(2,"Resto personal asalariado","Persona",1039.27)
					,new Module(3,"Personal no asalariado","Persona",14266.34)
					,new Module(4,"Superficie del local de fabricación","Metro cua.",48.50)
					,new Module(5,"Resto superficie local independiente","Metro cua.",33.38)
					,new Module(6,"Resto superficie local no independiente.","Metro cua.",125.97)
					,new Module(7,"Superficie del horno","100dm.cua.",629.86)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2090.35)
					,new Module(2,"Superficie del local","Metro cua.",8.86)
					,new Module(3,"Superficie del horno","100 dmcua.",40.75)
					,new Module(4,"Importe total de las comisiones loterias","Euro",0.21)
					,new Module(5,"","",0.00)
					,new Module(6,"","",0.00)
					,new Module(7,"","",0.00)}
				)
		,E_644_3 ("644.3","Comercio al por menor de productos de pastelería, bollería y confitería."
				,30
				,6
				,33760.53
				,new Module[]{new Module(1,"Personal asalariado de fabricación","Persona",6367.89)
					,new Module(2,"Resto personal asalariado","Persona",1014.08)
					,new Module(3,"Personal no asalariado","Persona",12912.15)
					,new Module(4,"Superficie del local de fabricación","Metro cua.",43.46)
					,new Module(5,"Resto superficie local independiente","Metro cua.",34.01)
					,new Module(6,"Resto superficie local no independiente.","Metro cua.",113.37)
					,new Module(7,"Superficie del horno","100dm.cua.",522.78)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3064.67)
					,new Module(2,"Superficie del local","Metro cua.",8.86)
					,new Module(3,"Superficie del horno","100 dmcua.",59.34)
					,new Module(4,"Importe total de las comisiones loterias","Euro",0.21)}
				)
		,E_644_6 ("644.6","Comercio al por menor de masas fritas, con o sin coberturas o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparados de chocolate y bebidas refrescantes."
				,32
				,6
				,19670.55
				,new Module[]{new Module(1,"Personal asalariado de fabricación","Persona",6852.88)
					,new Module(2,"Resto personal asalariado","Persona",2254.90)
					,new Module(3,"Personal no asalariado","Persona",13214.47)
					,new Module(4,"Superficie del local de fabricación","Metro cua.",27.71)
					,new Module(5,"Resto superficie local independiente","Metro cua.",21.41)
					,new Module(6,"Resto superficie local no independiente","Metro cua.",36.53)
					,new Module(7,"","",0.00)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2550.92)
					,new Module(2,"Superficie del local","Metro cua.",10.63)
					,new Module(3,"Importe total de las comisiones loterias","Euro",0.21)}
				)
		,E_647_1 ("647.1","Comerciantes minoristas matriculados en el epígrafe 647.1 por el servicio de comercialización de loterías."
				,75
				,5
				,15822.10
				,new Module[]{new Module(1,"Personal asalariado","Persona",1026.67)
					,new Module(2,"Personal no asalariado","Persona",10839.90)
					,new Module(3,"Superficie local independiente","Metro cua.",20.15)
					,new Module(4,"Superficie local no independiente","Metro cua.",68.65)
					,new Module(5,"Consumo de energía eléctrica","100 Kwh",8.81)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_647_2 ("647.2","Comerciantes minoristas matriculados en el epígrafe 647.2 y 3 por el servicio de comercialización de loterías."
				,75
				,4
				,25219.62
				,new Module[]{new Module(1,"Personal asalariado","Persona",1788.80)
					,new Module(2,"Personal no asalariado","Persona",10827.31)
					,new Module(3,"Superficie del local","Metro cua.",23.31)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",32.75)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_647_3 ("647.3","Comerciantes minoristas matriculados en el epígrafe 647.2 y 3 por el servicio de comercialización de loterías."
				,75
				,4
				,25219.62
				,new Module[]{new Module(1,"Personal asalariado","Persona",1788.80)
					,new Module(2,"Personal no asalariado","Persona",10827.31)
					,new Module(3,"Superficie del local","Metro cua.",23.31)
					,new Module(4,"Consumo energía eléctrica","100 Kwh",32.75)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_651_1 ("651.1","Comercio al por menor de productos textiles, confecciones para el hogar, alfombras y similares y artículos de tapicería."
				,0
				,4
				,23638.67
				,new Module[]{new Module(1,"Personal asalariado","Personas",3010.74)
					,new Module(2,"Personal no asalariado","Personas",13812.85)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",38.42)
					,new Module(4,"Superficie del local independiente","Metro cua.",35.28)
					,new Module(5,"Superficie del local no independiente","Metro cua.",107.07)}
				,null
				)
		,E_651_2 ("651.2","Comercio al por menor de toda clase de prendas para el vestido y tocado."
				,0
				,5
				,24848.00
				,new Module[]{new Module(1,"Personal asalariado","Persona",2569.83)
					,new Module(2,"Personal no asalariado","Persona",13995.51)
					,new Module(3,"Superficie del local","Metro cua.",49.13)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",56.69)}
				,null
				)
		,E_651_3 ("651.3","Comercio al por menor de lencería, corsetería y prendas especiales."
				,0
				,3
				,19626.46
				,new Module[]{new Module(1,"Personal asalariado","Persona",2198.21)
					,new Module(2,"Personal no asalariado","Persona",11998.85)
					,new Module(3,"Superficie del local","Metro cua.",47.87)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",75.58)}
				,null
				)
		,E_651_4 ("651.4","Comercio al por menor de artículos de mercería y paquetería."
				,0
				,4
				,14862.05
				,new Module[]{new Module(1,"Personal asalariado","Persona",1902.18)
					,new Module(2,"Personal no asalariado","Persona",10291.93)
					,new Module(3,"Superficie del local","Metro cua.",28.98)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",58.57)}
				,null
				)
		,E_651_5 ("651.5","Comercio al por menor de lencería, corsetería y prendas especiales."
				,0
				,3
				,19626.46
				,new Module[]{new Module(1,"Personal asalariado","Persona",2198.21)
					,new Module(2,"Personal no asalariado","Persona",11998.85)
					,new Module(3,"Superficie del local","Metro cua.",47.87)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",75.58)}
				,null
				)
		,E_651_6 ("651.6","Comercio al por menor de calzado, artículos de piel e imitación o productos sustitutivos, cinturones, carteras, bolsos, maletas y artículos de viaje en general."
				,0
				,5
				,24306.32
				,new Module[]{new Module(1,"Personal asalariado","Persona",3130.41)
					,new Module(2,"Personal no asalariado","Persona",13453.83)
					,new Module(3,"Superficie del local","Metro cua.",27.71)
					,new Module(4,"Consumo energía eléctrica","100 Kwh",52.27)}
				,null
				)
		,E_652_2 ("652.2","Comerciantes minoristas matriculados en el epígrafe 652.2 y 3 por el servicio de comercialización de loterías."
				,75
				,4
				,25333.00
				,new Module[]{new Module(1,"Personal asalariado","Personas",3722.48)
					,new Module(2,"Personal no asalariado","Personas",12786.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",31.49)
					,new Module(4,"Superficie del local independiente","Metro cua.",18.27)
					,new Module(5,"Superficie del local no independiente","Metro cua.",55.43)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_652_3 ("652.3","Comerciantes minoristas matriculados en el epígrafe 652.2 y 3 por el servicio de comercialización de loterías."
				,75
				,4
				,25333.00
				,new Module[]{new Module(1,"Personal asalariado","Personas",3722.48)
					,new Module(2,"Personal no asalariado","Personas",12786.18)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",31.49)
					,new Module(4,"Superficie del local independiente","Metro cua.",18.27)
					,new Module(5,"Superficie del local no independiente","Metro cua.",55.43)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_653_1 ("653.1","Comercio al por menor de muebles."
				,0
				,4
				,30718.31
				,new Module[]{new Module(1,"Personal asalariado","Persona",4075.20)
					,new Module(2,"Personal no asalariado","Persona",16200.02)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",50.39)
					,new Module(4,"Superficie del local","Metro cua.",16.38)}
				,null
				)
		,E_653_2 ("653.2","Comercio al por menor de material y aparatos eléctricos, electrónicos, electrodomésticos y otros aparatos de uso doméstico accionados por otro tipo de energía distinta de la eléctrica, así como muebles de cocina"
				,48
				,3
				,26189.61
				,new Module[]{new Module(1,"Personal asalariado","Persona",2884.77)
					,new Module(2,"Personal no asalariado","Persona",14656.86)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",100.78)
					,new Module(4,"Superficie del local independiente","Metro cua.",36.53)
					,new Module(5,"Superficie del local no independiente","Metro cua.",113.37)}
				,new Module[]{new Module(1,"Personal empleado reparacion","Persona",5819.91)
					,new Module(2,"Superficie taller reparación","Metro cua.",5.71)}
				)
		,E_653_3 ("653.3","Comercio al por menor de artículos de menaje, ferretería, adorno, regalo, o reclamo (incluyendo bisutería y pequeños electrodomésticos)."
				,0
				,4
				,24470.09
				,new Module[]{new Module(1,"Personal asalariado","Personas",3709.88)
					,new Module(2,"Personal no asalariado","Personas",15116.66)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",51.02)
					,new Module(4,"Superficie del local","Metro cua.",21.41)}
				,null
				)
		,E_653_4 ("653.4","Comercio al por menor de materiales de construcción, artículos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,13
				,3
				,26454.15
				,new Module[]{new Module(1,"Personal asalariado","Persona",3061.12)
					,new Module(2,"Personal no asalariado","Persona",16527.55)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",94.48)
					,new Module(4,"Superficie del local","Metro cua.",8.81)}
				,new Module[]{new Module(1,"Personal empleado","Persona",12317.71)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",239.74)
					,new Module(3,"Superficie del local","Metro cua.",9.10)}
				)
		,E_653_5 ("653.5","Comercio al por menor de materiales de construcción, artículos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,13
				,3
				,26454.15
				,new Module[]{new Module(1,"Personal asalariado","Persona",3061.12)
					,new Module(2,"Personal no asalariado","Persona",16527.55)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",94.48)
					,new Module(4,"Superficie del local","Metro cua.",8.81)}
				,new Module[]{new Module(1,"Personal empleado","Persona",12317.71)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",239.74)
					,new Module(3,"Superficie del local","Metro cua.",9.10)}
				)
		,E_653_9 ("653.9","Comercio al por menor de otros artículos para el equipamiento del hogar n.c.o.p."
				,0
				,3
				,32765.35
				,new Module[]{new Module(1,"Personal asalariado","Persona",4950.71)
					,new Module(2,"Personal no asalariado","Persona",20061.07)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",81.88)
					,new Module(4,"Superficie del local","Metro cua.",39.68)}
				,null
				)
		,E_654_2 ("654.2","Comercio al por menor de accesorios y piezas de recambio para vehículos terrestres."
				,13
				,4
				,32815.74
				,new Module[]{new Module(1,"Personal asalariado","Persona",3098.92)
					,new Module(2,"Personal no asalariado","Persona",17018.84)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",201.55)
					,new Module(4,"Potencia fiscal vehículo","CVF",617.26)}
				,new Module[]{new Module(1,"Personal empleado","Persona",15558.35)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",272.80)
					,new Module(3,"Potencia fiscal vehículo","CVF",620.03)}
				)
		,E_654_5 ("654.5","Comercio al por menor de toda clase de maquinaria (excepto aparatos del hogar, de oficina, médicos, ortopédicos, ópticos y fotográficos)."
				,13
				,3
				,31367.06
				,new Module[]{new Module(1,"Personal asalariado","Persona",9422.71)
					,new Module(2,"Personal no asalariado","Persona",18858.03)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",39.05)
					,new Module(4,"Potencia fiscal vehículo","CVF",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",18021.89)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",74.41)
					,new Module(3,"Potencia fiscal vehículo","CVF",82.66)}
				)
		,E_654_6 ("654.6","Comercio al por menor de cubiertas, bandas o bandajes y cámaras de aire para toda clase de vehículos, excepto las actividades de comercio al por mayor de los artículos citados."
				,13
				,4
				,26970.63
				,new Module[]{new Module(1,"Personal asalariado","Persona",2746.19)
					,new Module(2,"Personal no asalariado","Persona",14222.25)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",119.67)
					,new Module(4,"Potencia fiscal vehículo","CVF",377.92)}
				,new Module[]{new Module(1,"Personal empleado","Persona",14152.98)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",206.67)
					,new Module(3,"Potencia fiscal vehículo","CVF",372.02)}
				)

		,E_659_2 ("659.2","Comercio al por menor de muebles de oficina y de máquinas y equipos de oficina."
				,0
				,4
				,30718.31
				,new Module[]{new Module(1,"Personal asalariado","Persona",4157.08)
					,new Module(2,"Personal no asalariado","Persona",16521.25)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",56.69)
					,new Module(4,"Superficie","Metro cua.",17.01)}
				,null
				)
		,E_659_3 ("659.3","Comerciantes minoristas matriculados en el epígrafe 659.3 por el servicio de recogida de negativos y otro material fotográfico impresionado para su procesado en laboratorio de terceros y la entrega de las correspondientes copias y ampliaciones."
				,13
				,3
				,35524.14
				,new Module[]{new Module(1,"Personal asalariado","Persona",7174.12)
					,new Module(2,"Personal no asalariado","Persona",19273.74)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",119.67)
					,new Module(4,"Potencia fiscal vehículo","CVF",1070.76)}
				,new Module[]{new Module(1,"Personal recogida material fotográfico","Persona",14533.25)
					,new Module(2,"Superficie del local","Metro cua.",30.59)}
				)			
		,E_659_4A("659.4","Comercio al por menor de libros, periódicos, artículos de papelería y escritorio y artículos de dibujo y bellas artes, excepto en quioscos situados en la vía pública."
				,75
				,3
				,25207.02
				,new Module[]{new Module(1,"Personal asalariado","Persona",4648.37)
					,new Module(2,"Personal no asalariado","Persona",17176.30)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",57.94)
					,new Module(4,"Superficie del local","Metro cua.",30.86)
					,new Module(5,"Potencia fiscal del vehículo","CVF",535.38)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_659_4B("659.4","Comercio al por menor de prensa, revistas y libros en quioscos situados en la vía pública."
				,75
				,2
				,28860.22
				,new Module[]{new Module(1,"Personal asalariado","Persona",3476.83)
					,new Module(2,"Personal no asalariado","Persona",17220.39)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",403.11)
					,new Module(4,"Superficie del local","Metro cua.",844.02)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)

		,E_659_6 ("659.6","Comercio al por menor de juguetes, artículos de deporte, prendas deportivas de vestido, calzado y tocado, armas, cartuchería y artículos de pirotecnia."
				,0
				,3
				,24948.78
				,new Module[]{new Module(1,"Personal asalariado","Persona",2916.26)
					,new Module(2,"Personal no asalariado","Persona",13258.56)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",138.57)
					,new Module(4,"Superficie del local","Metro cua.",32.75)}
				,null
				)
		,E_659_7 ("659.7","Comercio al por menor de semillas, abonos, flores y plantas y pequeños animales."
				,0
				,4
				,23978.80
				,new Module[]{new Module(1,"Personal asalariado","Persona",4988.50)
					,new Module(2,"Personal no asalariado","Persona",16124.43)
					,new Module(3,"Potencia fiscal vehículo","CVF",258.24)}
				,null
				)

		,E_662_2 ("662.2","Comerciantes minoristas matriculados en el epígrafe 662.2 por el servicio de comercialización de loterías."
				,75
				,3
				,16395.27
				,new Module[]{new Module(1,"Personal asalariado","Persona",4868.82)
					,new Module(2,"Personal no asalariado","Persona",9429.01)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",28.98)
					,new Module(4,"Superficie del local","Metro cua.",37.79)}
				,new Module[]{new Module(1,"Importe total comisiones percibidas","Euro",0.21)}
				)
		,E_663_1 ("663.1","Comercio al por menor fuera de un establecimiento comercial permanente dedicado exclusivamente a la comercialización de masas fritas, con o sin coberturas o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparación de chocolate y bebidas refrescantes y facultado para la elaboración de los productos propios de churrería y patatas fritas en la propia instalación o vehículo."
				,20
				,2
				,14379.72
				,new Module[]{new Module(1,"Personal asalariado","Persona",1398.29)
					,new Module(2,"Personal no asalariado","Persona",13989.21)
					,new Module(3,"Potencia fiscal vehículo","CVF",113.37)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5881.32)
					,new Module(2,"Potencia fiscal del vehículo","CVF",43.40)}
				)
		
		,E_663_2 ("663.2","Comercio al por menor fuera de un establecimiento comercial permanente de artículos textiles y de confección."
				,0
				,2
				,19059.58
				,new Module[]{new Module(1,"Personal asalariado","Persona",2991.84)
					,new Module(2,"Personal no asalariado","Persona",13982.91)
					,new Module(3,"Potencia fiscal vehículo","CVF",239.34)}
				,null
				)
		,E_663_3 ("663.3","Comercio al por menor fuera de un establecimiento comercial permanente de calzado, pieles y artículos de cuero."
				,0
				,2
				,17081.82
				,new Module[]{new Module(1,"Personal asalariado","Persona",2613.92)
					,new Module(2,"Personal no asalariado","Persona",11186.32)
					,new Module(3,"Potencia fiscal vehículo","CVF",151.16)}
				,null
				)
		,E_663_4 ("663.4","Comercio al por menor fuera de un establecimiento comercial permanente de artículos de droguería y cosméticos y de productos químicos en general."
				,0
				,2
				,16886.56
				,new Module[]{new Module(1,"Personal asalariado","Persona",3678.39)
					,new Module(2,"Personal no asalariado","Persona",12641.30)
					,new Module(3,"Potencia fiscal vehículo","CVF",113.37)}
				,null
				)
		,E_663_9 ("663.9","Comercio al por menor fuera de un establecimiento comercial permanente de otras clases de mercancías n.c.o.p."
				,0
				,2
				,18354.14
				,new Module[]{new Module(1,"Personal asalariado","Persona",5448.29)
					,new Module(2,"Personal no asalariado","Persona",10537.57)
					,new Module(3,"Potencia fiscal vehículo","CVF",283.44)}
				,null
				)
		,E_671_4 ("671.4","Restaurantes de dos tenedores."
				,13
				,10
				,51617.08
				,new Module[]{new Module(1,"Personal asalariado","Persona",3709.88)
					,new Module(2,"Personal no asalariado","Persona",17434.55)
					,new Module(3,"Potencia eléctrica","Kw cont.",201.55)
					,new Module(4,"Mesas","Mesa",585.77)
					,new Module(5,"Maquinas tipo A","Maquina A",1077.06)
					,new Module(6,"Maquinas tipo B","Maquina B",3810.65)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2993.81)
					,new Module(2,"Potencia eléctrica","Kw cont.",150.57)
					,new Module(3,"Mesas","Mesa",168.29)
					,new Module(4,"Maquinas tipo A","Maquina A",239.15)
					,new Module(5,"Maquinas tipo B","Maquina B",841.46)
					,new Module(6,"Importe total comisiones por loterías","Euro",0.21)}
				)
		,E_671_5 ("671.5","Restaurantes de un tenedor."
				,20
				,10
				,38081.38
				,new Module[]{new Module(1,"Personal asalariado","Persona",3602.80)
					,new Module(2,"Personal no asalariado","Persona",16174.82)
					,new Module(3,"Potencia eléctrica","Kw cont.",125.97)
					,new Module(4,"Mesas","Mesa",220.45)
					,new Module(5,"Maquinas tipo A","Maquina A",1077.06)
					,new Module(6,"Maquinas tipo B","Maquina B",3810.65)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2400.36)
					,new Module(2,"Potencia eléctrica","Kw cont.",70.86)
					,new Module(3,"Mesas","Mesa",124.00)
					,new Module(4,"Maquinas tipo A","Maquina A",239.15)
					,new Module(5,"Maquinas tipo B","Maquina B",841.46)
					,new Module(6,"Importe total comisiones por loterías","Euro",0.21)}
				)
		,E_672_1 ("672.1","Cafeterías."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,"Personal asalariado","Persona",1448.68)
					,new Module(2,"Personal no asalariado","Persona",13743.56)
					,new Module(3,"Potencia eléctrica","Kw cont.",478.69)
					,new Module(4,"Mesas","Mesa",377.92)
					,new Module(5,"Maquinas tipo A","Maquina A",957.39)
					,new Module(6,"Maquinas tipo B","Maquina B",3747.67)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2356.07)
					,new Module(2,"Potencia eléctrica","Kw cont.",124.00)
					,new Module(3,"Mesas","Mesa",70.86)
					,new Module(4,"Maquinas tipo A","Maquina A",221.43)
					,new Module(5,"Maquinas tipo B","Maquina B",832.60)
					,new Module(6,"Importe total comisiones por loterías","Euro",0.21)}
				)
		,E_672_2 ("672.2","Cafeterías."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,"Personal asalariado","Persona",1448.68)
					,new Module(2,"Personal no asalariado","Persona",13743.56)
					,new Module(3,"Potencia eléctrica","Kw cont.",478.69)
					,new Module(4,"Mesas","Mesa",377.92)
					,new Module(5,"Maquinas tipo A","Maquina A",957.39)
					,new Module(6,"Maquinas tipo B","Maquina B",3747.67)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2356.07)
					,new Module(2,"Potencia eléctrica","Kw cont.",124.00)
					,new Module(3,"Mesas","Mesa",70.86)
					,new Module(4,"Maquinas tipo A","Maquina A",221.43)
					,new Module(5,"Maquinas tipo B","Maquina B",832.60)
					,new Module(6,"Importe total comisiones por loterías","Euro",0.21)}
				)
		,E_672_3 ("672.3","Cafeterías."
				,13
				,8
				,39070.26
				,new Module[]{new Module(1,"Personal asalariado","Persona",1448.68)
					,new Module(2,"Personal no asalariado","Persona",13743.56)
					,new Module(3,"Potencia eléctrica","Kw cont.",478.69)
					,new Module(4,"Mesas","Mesa",377.92)
					,new Module(5,"Maquinas tipo A","Maquina A",957.39)
					,new Module(6,"Maquinas tipo B","Maquina B",3747.67)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2356.07)
					,new Module(2,"Potencia eléctrica","Kw cont.",124.00)
					,new Module(3,"Mesas","Mesa",70.86)
					,new Module(4,"Maquinas tipo A","Maquina A",221.43)
					,new Module(5,"Maquinas tipo B","Maquina B",832.60)
					,new Module(6,"Importe total comisiones por loterías","Euro",0.21)}
				)
		,E_673_1 ("673.1","Cafés y bares de categoría especial."
				,6
				,8
				,30586.03
				,new Module[]{new Module(1,"Personal asalariado","Persona",4056.30)
					,new Module(2,"Personal no asalariado","Persona",15538.66)
					,new Module(3,"Potencia eléctrica","Kw cont.",321.23)
					,new Module(4,"Mesas","Mesa",233.04)
					,new Module(5,"Longitud de Barra","Metro",371.62)
					,new Module(6,"Maquinas tipo A","Maquina A",957.39)
					,new Module(7,"Maquinas tipo B","Maquina B",2903.66)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3294.97)
					,new Module(2,"Potencia eléctrica","Kw cont.",69.09)
					,new Module(3,"Mesas","Mesa",60.23)
					,new Module(4,"Longitud de barra","Metro",77.95)
					,new Module(5,"Maquinas tipo A","Maquina A",221.43)
					,new Module(6,"Maquinas tipo B","Maquina B",655.45)
					,new Module(7,"Importe total de las comisiones loterias","Euro",0.21)}
				)
		,E_673_2 ("673.2","Otros cafés y bares."
				,6
				,8
				,19084.78
				,new Module[]{new Module(1,"Personal asalariado","Persona",1643.93)
					,new Module(2,"Personal no asalariado","Persona",11413.08)
					,new Module(3,"Potencia eléctrica","Kw cont.",94.48)
					,new Module(4,"Mesas","Mesa",119.67)
					,new Module(5,"Longitud barra","Metro",163.76)
					,new Module(6,"Maquinas tipo A","Maquina A",806.23)
					,new Module(7,"Maquinas tipo B","Maquina B",2947.75)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2577.52)
					,new Module(2,"Potencia eléctrica","Kw cont.",47.83)
					,new Module(3,"Mesas","Mesa",56.69)
					,new Module(4,"Longitud barra","Metros",62.89)
					,new Module(5,"Maquinas tipo A","Maquina A",177.15)
					,new Module(6,"Maquinas tipo B","Maquina B",655.45)
					,new Module(7,"Importe total de las comisiones loterias","Euro",0.21)}
				)
		,E_675   ("675"	 ,"Servicios en quioscos, cajones, barracas u otros locales análogos."
				,3
				,3
				,16596.83
				,new Module[]{new Module(1,"Personal asalariado","Persona",2802.88)
					,new Module(2,"Personal no asalariado","Persona",14461.60)
					,new Module(3,"Potencia eléctrica","Kw cont.",107.07)
					,new Module(4,"Superficie del local","Metro cua.",26.45)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4357.86)
					,new Module(2,"Potencia eléctrica","Kw cont.",50.48)
					,new Module(3,"Superficie del local","Metro cua.",4.06)
					,new Module(4,"Importe total comisiones por loterias","Euro",0.21)}
				)
		,E_676   ("676"	 ,"Servicios en chocolaterías, heladerías y horchaterías."
				,20
				,3
				,25528.25
				,new Module[]{new Module(1,"Personal asalariado","Persona",2418.67)
					,new Module(2,"Personal no asalariado","Persona",20016.97)
					,new Module(3,"Potencia eléctrica","Kw cont.",541.68)
					,new Module(4,"Mesas","Mesa",220.45)
					,new Module(5,"Maquinas tipo A","Maquina A",806.23)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3817.55)
					,new Module(2,"Potencia eléctrica","Kw cont.",141.72)
					,new Module(3,"Mesas","Mesa",46.05)
					,new Module(4,"Maquinas tipo A","Maquina A",177.15)
					,new Module(5,"Importe total comisiones por loterias","Euro",0.21)}
				)
		,E_681   ("681"	 ,"Servicio de hospedaje en hoteles y moteles de una o dos estrellas."
				,20
				,10
				,61512.19
				,new Module[]{new Module(1,"Personal asalariado","Persona",6223.02)
					,new Module(2,"Personal no asalariado","Persona",20438.98)
					,new Module(3,"Numero de plazas","Plaza",371.62)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2604.09)
					,new Module(2,"Numero de plazas","Plaza",53.14)
					,new Module(3,"Importe total comisiones por loterias","Euro",0.21)}
				)
		,E_682   ("682"	 ,"Servicio de hospedaje en hostales y pensiones."
				,20
				,8
				,32840.94
				,new Module[]{new Module(1,"Personal asalariado","Persona",5145.96)
					,new Module(2,"Personal no asalariado","Persona",17541.62)
					,new Module(3,"Numero de plazas","Plaza",270.85)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2533.22)
					,new Module(2,"Numero de plazas","Plaza",55.80)}
				)
		,E_683   ("683"	 ,"Servicio de hospedaje en fondas y casas de huéspedes."
				,30
				,8
				,16256.70
				,new Module[]{new Module(1,"Personal asalariado","Persona",4478.31)
					,new Module(2,"Personal no asalariado","Persona",14587.57)
					,new Module(3,"Numero de plazas","Plaza",132.27)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1762.62)
					,new Module(2,"Numero de plazas","Plaza",29.22)}
				)
		,E_691_1 ("691.1","Reparación de artículos eléctricos para el hogar."
				,48
				,3
				,21585.33
				,new Module[]{new Module(1,"Personal asalariado","Persona",4314.54)
					,new Module(2,"Personal no asalariado","Persona",15538.66)
					,new Module(3,"Superficie del local","Metro cua.",17.01)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5819.91)
					,new Module(2,"Superficie del local","Metro cua.",5.71)}
				)
		,E_691_2 ("691.2","Reparación de vehículos automóviles, bicicletas y otros vehículos."
				,30
				,5
				,33729.04
				,new Module[]{new Module(1,"Personal asalariado","Persona",4157.08)
					,new Module(2,"Personal no asalariado","Persona",17094.42)
					,new Module(3,"Superficie del local","Metro cua.",27.08)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8556.27)
					,new Module(2,"Superficie del local","Metro cua.",16.54)}
				)
		,E_691_9A("691.9","Reparación de calzado."
				,48
				,2
				,16552.74
				,new Module[]{new Module(1,"Personal asalariado","Persona",1845.50)
					,new Module(2,"Personal no asalariado","Persona",10014.78)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",125.97)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3000.89)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",36.37)}
				)
		,E_691_9B("691.9","Reparación de otros bienes de consumo n.c.o.p. (excepto reparación de calzado, restauración de obras de arte, muebles, antigüedades e instrumentos musicales)."
				,48
				,2
				,24803.91
				,new Module[]{new Module(1,"Personal asalariado","Persona",4094.10)
					,new Module(2,"Personal no asalariado","Persona",16187.42)
					,new Module(3,"Superficie del local","Metro cua.",45.35)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5381.76)
					,new Module(2,"Superficie del local","Metro cua.",13.23)}
				)
		
		,E_692   ("692"	 ,"Reparación de maquinaria industrial."
				,30
				,2
				,30352.99
				,new Module[]{new Module(1,"Personal asalariado","Persona",4409.02)
					,new Module(2,"Personal no asalariado","Persona",18146.29)
					,new Module(3,"Superficie del local","Metro cua.",94.48)}
				,new Module[]{new Module(1,"Personal empleado","Persona",9721.90)
					,new Module(2,"Superficie del local","Metro cua.",53.75)}
				)
		,E_699   ("699"	 ,"Otras reparaciones n.c.o.p."
				,32
				,2
				,23607.18
				,new Module[]{new Module(1,"Personal asalariado","Persona",3703.58)
					,new Module(2,"Personal no asalariado","Persona",15230.03)
					,new Module(3,"Superficie del local","Metro cua.",88.18)}
				,new Module[]{new Module(1,"Personal empleado","Persona",7671.69)
					,new Module(2,"Superficie del local","Metro cua.",46.31)}
				)
		,E_721_1 ("721.1","Transporte urbano colectivo y de viajeros por carretera."
				,1
				,5
				,35196.62
				,new Module[]{new Module(1,"Personal asalariado","Persona",2981.02)
					,new Module(2,"Personal no asalariado","Persona",16016.97)
					,new Module(3,"Numero de asientos","Asiento",121.40)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1700.62)
					,new Module(2,"Numero de asientos","Asiento",79.72)}
				)
		,E_721_2 ("721.2","Transporte por autotaxis."
				,10
				,3
				,9999999.99
				,new Module[]{new Module(1,"Personal asalariado","Persona",1346.27)
					,new Module(2,"Personal no asalariado","Persona",7656.89)
					,new Module(3,"Distancia recorrida","1000 Km",45.08)}
				,new Module[]{new Module(1,"Personal empleado","Persona",903.46)
					,new Module(2,"Distancia recorrida","1000 Km",8.78)}
				)
		,E_721_3 ("721.3","Transporte urbano colectivo y de viajeros por carretera."
				,1
				,5
				,35196.62
				,new Module[]{new Module(1,"Personal asalariado","Persona",2981.02)
					,new Module(2,"Personal no asalariado","Persona",16016.97)
					,new Module(3,"Numero de asientos","Asiento",121.40)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1700.62)
					,new Module(2,"Numero de asientos","Asiento",79.72)}
				)
		,E_722A  ("722"	 ,"Transporte de mercancías por carretera, expto residuos"
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,"Personal asalariado","Persona",2728.59)
					,new Module(2,"Personal no asalariado","Persona",10090.99)
					,new Module(3,"Carga vehículos","Tonelada",126.21)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4149.99)
					,new Module(2,"Carga vehículos","Tonelada",388.55)}
				)
		,E_722B  ("722"	 ,"Transporte de residuos por carretera."
				,1
				,5
				,33640.86
				,new Module[]{new Module(1,"Personal asalariado","Persona",2728.59)
					,new Module(2,"Personal no asalariado","Persona",10090.99)
					,new Module(3,"Carga vehículos","Tonelada",126.21)}
				,new Module[]{new Module(1,"Personal empleado","Persona",1948.64)
					,new Module(2,"Carga vehículos","Tonelada",181.58)}
				)

		,E_751_5 ("751.5","Engrase y lavado de vehículos."
				,30
				,5
				,28280.74
				,new Module[]{new Module(1,"Personal asalariado","Persona",4667.27)
					,new Module(2,"Personal no asalariado","Persona",19191.86)
					,new Module(3,"Superficie del local","Metro cua.",30.23)}
				,new Module[]{new Module(1,"Personal empleado","Persona",8556.27)
					,new Module(2,"Superficie del local","Metro cua.",16.54)}
				)
		,E_757   ("757"	 ,"Servicios de mudanzas."
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,"Personal asalariado","Persona",2566.32)
					,new Module(2,"Personal no asalariado","Persona",10175.13)
					,new Module(3,"Carga vehículos","Tonelada",48.08)}
				,new Module[]{new Module(1,"Personal empleado","Persona",5712.43)
					,new Module(2,"Carga vehículos","Tonelada",256.27)}
				)
		,E_849_5 ("849.5","Transporte de mensajería y recadería, cuando la actividad se realice exclusivamente con medios de transporte propios."
				,10
				,5
				,33640.86
				,new Module[]{new Module(1,"Personal asalariado","Persona",2728.59)
					,new Module(2,"Personal no asalariado","Persona",10090.99)
					,new Module(3,"Carga vehículos","Tonelada",126.21)}
				,new Module[]{new Module(1,"Personal empleado","Persona",4149.99)
					,new Module(2,"Carga vehículos","Tonelada",388.55)}
				)
		,E_933_1 ("933.1","Enseñanza de conducción de vehículos terrestres, acuáticos, aeronáuticos, etc."
				,48
				,4
				,47233.25
				,new Module[]{new Module(1,"Personal asalariado","Persona",3067.42)
					,new Module(2,"Personal no asalariado","Persona",20596.45)
					,new Module(3,"Numero de vehículos","vehículo",774.72)
					,new Module(4,"Potencia fiscal vehículo","CVF",258.24)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3000.89)
					,new Module(2,"Numero de vehículos","vehículo",256.27)
					,new Module(3,"Potencia fiscal vehículo","CVF",107.47)}
				)
		,E_933_9 ("933.9","Otras actividades de enseñanza, tales como idiomas, corte y confección, mecanografía, taquigrafía, preparación de exámenes y oposiciones y similares n.c.o.p."
				,48
				,5
				,33697.55
				,new Module[]{new Module(1,"Personal asalariado","Persona",1253.49)
					,new Module(2,"Personal no asalariado","Persona",15727.62)
					,new Module(3,"Superficie del local","Metro cua.",62.36)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2413.95)
					,new Module(2,"Superficie del local","Metro cua.",2.64)}
				)
		,E_967_2 ("967.2","Escuelas y servicios de perfeccionamiento del deporte."
				,3
				,3
				,37067.30
				,new Module[]{new Module(1,"Personal asalariado","Persona",7035.55)
					,new Module(2,"Personal no asalariado","Persona",14215.95)
					,new Module(3,"Superficie del local","Metro cua.",34.01)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3441.10)
					,new Module(2,"Superficie del local","Metro cua.",5.58)}
				)
		,E_971_1 ("971.1","Tinte, limpieza en seco, lavado y planchado de ropas hechas y de prendas y artículos del hogar usados."
				,48
				,4
				,37224.77
				,new Module[]{new Module(1,"Personal asalariado","Persona",4553.90)
					,new Module(2,"Personal no asalariado","Persona",16773.19)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",45.98)}
				,new Module[]{new Module(1,"Personal empleado","Persona",3844.13)
					,new Module(2,"Consumo de energía eléctrica","100 Kwh",14.06)}
				)
		,E_972_1 ("972.1","Servicios de peluquería de señora y caballero."
				,13
				,6
				,18051.81
				,new Module[]{new Module(1,"Personal asalariado","Persona",3161.90)
					,new Module(2,"Personal no asalariado","Persona",9649.47)
					,new Module(3,"Superficie del local","Metro cua.",94.48)
					,new Module(4,"Consumo energía eléctrica","100 Kwh",81.88)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2562.75)
					,new Module(2,"Superficie del local","Metro cua.",41.33)
					,new Module(3,"Consumo energía eléctrica","100 Kwh",17.48)}
				)
		,E_972_2 ("972.2","Salones e institutos de belleza."
				,32
				,6
				,26945.44
				,new Module[]{new Module(1,"Personal asalariado","Persona",1788.80)
					,new Module(2,"Personal no asalariado","Persona",14896.21)
					,new Module(3,"Superficie del local","Metro cua.",88.18)
					,new Module(4,"Consumo de energía eléctrica","100 Kwh",55.43)}
				,new Module[]{new Module(1,"Personal empleado","Persona",2562.75)
					,new Module(2,"Superficie del local","Metro cua.",41.33)
					,new Module(3,"Consumo de energía eléctrica","100 Kwh",17.36)}
				)
		,E_973_3 ("973.3","Servicios de copias de documentos con máquinas fotocopiadoras."
				,30
				,4
				,24192.95
				,new Module[]{new Module(1,"Personal asalariado","Persona",4125.59)
					,new Module(2,"Personal no asalariado","Persona",17044.03)
					,new Module(3,"Potencia eléctrica","Kw cont.",541.68)}
				,new Module[]{new Module(1,"Personal empleado","Persona",13136.13)
					,new Module(2,"Potencia eléctrica","Kw cont.",239.74)}
				)
				;
		private String epigraph;
		private String description;
		private Module[] irpfModules;
		private Module[] ivaModules;
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
			this.ivaModules=ivaModules;
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
		public Module[] getIrpfModules() {
			return irpfModules;
		}
		public Module[] getIvaModules() {
			return ivaModules;
		}
	}
}

