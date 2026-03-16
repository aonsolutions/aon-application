package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ModulesCanarias2026 {

	public enum EpigraphCanarias implements Serializable, IEpigraphCanarias {
		
		 E_0001  ("0001" ,"Servicios de cr\u00EDa, guarda y engorde de aves.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0350)})
		,E_0002  ("0002" ,"Otros trabajos y servicios accesorios realizados por agricultores o ganaderos o titulares de actividades forestales que est\u00E9n excluidos del r\u00E9gimen especial de la agricultura y ganader\u00EDa del impuesto general indirecto canario, y servicos de cr\u00EDa y engorde de ganado, excepto aves.",48.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0700)})
		,E_0003  ("0003" ,"Actividades accesorias realizadas por agricultores o ganaderos o titulares de actividades forestales no incluidas en el r\u00E9gimen de la agricultura y ganader\u00EDa del impuesto general indirecto canario.",80.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0700)})
		,E_0004  ("0004" ,"Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de productos agr\u00EDcolas no comprendidas en los apartados siguientes.", 2.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0100)})
		,E_0005  ("0005" ,"Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de forrajes.",28.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0150)})
		,E_0006  ("0006" ,"Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en el r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de plantas textiles y tabaco.",44.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0500)})
		,E_0007  ("0007" ,"Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en r\u00E9gimen de aparcer\u00EDa.",44.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0500)})
		,E_0008  ("0008" ,"Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino de mesa.",80.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0550)})
		,E_0009  ("0009" ,"Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino con denominaci\u00F3n de origen.",80.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0550)})
		,E_0010  ("0010" ,"Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de otros productos distintos a los anteriores.",80.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0500)})
		,E_00011 ("00011","Ganadera de explotaci\u00F3n intensiva de ganado porcino de carne y avicultura de carne.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_00012 ("00012","Ganadera de explotaci\u00F3n intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_00013 ("00013","Ganadera de explotaci\u00F3n intensiva de ganado bovino de carne y cunicultura.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_00014 ("00014","Ganadera de explotaci\u00F3n intensiva de ganado de porcino de cr\u00EDa, bovino de cr\u00EDa y otras intensivas o extensivas no comprendidas expresamente en otros apartados.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_00015 ("00015","Ganadera de explotaci\u00F3n intensiva de ganado ovino y caprino de carne.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_00016 ("00016","Apicultura.",40.0,0,new Module[]{ new Module(1,ModuleInfo.M63,"",0.0300)})
		,E_14191 ("14191","Industria del pan y bolleria.", 9.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",479.85),new Module(2,ModuleInfo.M09,"Metro cua.",2.10),new Module(3,ModuleInfo.M14,"100dm.cua.",10.42)})
		,E_14192 ("14192","Industrias de la boller\u00EDa, pasteler\u00EDa y galletas.",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",975.54),new Module(2,ModuleInfo.M09,"Metro cua.",2.92),new Module(3,ModuleInfo.M14,"100dm.cua.",24.15)})
		,E_14193 ("14193","Indust. elaboracion masas fritas.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",811.36),new Module(2,ModuleInfo.M09,"Metro cua.",3.90)})
		,E_14239 ("14239","Elaboraci\u00F3n de patatas fritas, palomitas de ma\u00EDz y similares.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",811.36),new Module(2,ModuleInfo.M09,"Metro cua.",3.90)})
		,E_16421 ("16421","Com.men.carnes,huevos,caza y granja.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",550.56),new Module(2,ModuleInfo.M09,"Metro cua.",0.82)})
		,E_16422 ("16422","Com.men.carnicerias-charcuterias.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",550.56),new Module(2,ModuleInfo.M09,"Metro cua.",0.82)})
		,E_16423 ("16423","Com.men.carnicerias-salchicherias.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",550.56),new Module(2,ModuleInfo.M09,"Metro cua.",0.82)})
		,E_16425 ("16425","Com.men.huevos,aves,granja y caza (asado de pollos).",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",212.49),new Module(2,ModuleInfo.M09,"Metro cua.",4.87),new Module(3,ModuleInfo.M57,"Pieza",19.32)})
		,E_16441 ("16441","Com.men.pan,pasteles,confiteria,lacteos (fabricaci\u00F3n).", 9.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",499.85),new Module(2,ModuleInfo.M09,"Metro cua.",2.18),new Module(3,ModuleInfo.M14,"100 dmcua.",10.87),new Module(4,ModuleInfo.M58,"Euro",0.07)})
		,E_16442 ("16442","Despachos pan, pan especial, y bolleria (fabricaci\u00F3n).", 9.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",499.85),new Module(2,ModuleInfo.M09,"Metro cua.",2.18),new Module(3,ModuleInfo.M14,"100 dmcua.",10.87),new Module(4,ModuleInfo.M58,"Euro",0.07)})
		,E_16443 ("16443","Com.men.ptos.pasteleria,bolleria (fabricaci\u00F3n).",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",975.54),new Module(2,ModuleInfo.M09,"Metro cua.",2.92),new Module(3,ModuleInfo.M14,"100 dmcua.",19.32),new Module(4,ModuleInfo.M58,"Euro",0.07)})
		,E_16446 ("16446","Com.men.masas fritas.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",792.01),new Module(2,ModuleInfo.M09,"Metro cua.",3.90),new Module(3,ModuleInfo.M58,"Euro",0.07)})
		,E_16471 ("16471","Com.men.ptos.alimenticios y bebidas (comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16472 ("16472","Com.men.ptos.alimenticios menos 120 m2 (comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16473 ("16473","Com.men.ptos.alimenticios 120 - 399 m2 (comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16522 ("16522","Com.men.ptos.drogueria,perfumeria (comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16523 ("16523","Com.men.ptos.perfumeria y cosmetica (comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16532 ("16532","Com.men.aparatos de uso domestico.",23.0,48,new Module[]{ new Module(1,ModuleInfo.M27,"Persona",2110.40),new Module(2,ModuleInfo.M28,"Metro cua.",2.04)})
		,E_16534 ("16534","Com.men.materiales de construccion.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1847.65),new Module(2,ModuleInfo.M03,"100 Kwh",32.50),new Module(3,ModuleInfo.M09,"Metro cua.",1.34)})
		,E_16535 ("16535","Com.men.puertas,ventanas y persianas.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1847.65),new Module(2,ModuleInfo.M03,"100 Kwh",32.50),new Module(3,ModuleInfo.M09,"Metro cua.",1.34)})
		,E_16542 ("16542","Com.men.accesorios y recambios vehiculos.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",5823.49),new Module(2,ModuleInfo.M03,"100 Kwh",97.53),new Module(3,ModuleInfo.M24,"CVF",232.16)})
		,E_16545 ("16545","Com.men.de toda clase de maquinaria.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",6750.89),new Module(2,ModuleInfo.M03,"100 Kwh",26.93),new Module(3,ModuleInfo.M24,"CVF",30.95)})
		,E_16546 ("16546","Com.men.cubiertas,bandas y camaras aire.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",2110.96),new Module(2,ModuleInfo.M03,"100 Kwh",29.67),new Module(3,ModuleInfo.M24,"CVF",56.02)})
		,E_16593 ("16593","Com.men.aparatos medicos, ortopedicos y fotogr\u00E1ficos.", 6.0,13,new Module[]{ new Module(1,ModuleInfo.M62,"Persona",5228.37),new Module(2,ModuleInfo.M09,"Metro cua.",11.14)})
		,E_16594 ("16594","Com.men.libros,periodicos,revistas (venta tarj. transt. y tfno.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16622 ("16622","Com.men.toda clase art. en otros locales(comercializaci\u00F3n loter.).",100.0,75,new Module[]{ new Module(1,ModuleInfo.M58,"Euro",0.07)})
		,E_16631 ("16631","Com.men.ptos.aliment. sin establec. (elab. churrer\u00EDas y papas frit.).", 9.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1808.15),new Module(2,ModuleInfo.M24,"CVF",12.98)})
		,E_16714 ("16714","Restaurantes de dos tenedores.",12.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",2303.07),new Module(2,ModuleInfo.M08,"Kw cont.",111.42),new Module(3,ModuleInfo.M04,"Mesa",129.99),new Module(4,ModuleInfo.M06,"Maquina A",185.75),new Module(5,ModuleInfo.M07,"Maquina B",645.78),new Module(6,ModuleInfo.M58,"Euro",0.07)})
		,E_16715 ("16715","Restaurantes de un tenedor.",18.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1848.03),new Module(2,ModuleInfo.M08,"Kw cont.",53.85),new Module(3,ModuleInfo.M04,"Mesa",94.71),new Module(4,ModuleInfo.M06,"Maquina A",185.75),new Module(5,ModuleInfo.M07,"Maquina B",650.06),new Module(6,ModuleInfo.M58,"Euro",0.07)})
		,E_16721 ("16721","Cafeterias tres tazas (cafet. y comercializac. loter\u00EDa).",12.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1801.62),new Module(2,ModuleInfo.M08,"Kw cont.",94.72),new Module(3,ModuleInfo.M04,"Mesa",53.85),new Module(4,ModuleInfo.M06,"Maquina A",167.17),new Module(5,ModuleInfo.M07,"Maquina B",640.77),new Module(6,ModuleInfo.M58,"Euro",0.07)})
		,E_16722 ("16722","Cafeterias dos tazas (cafet. y comercializac. loter\u00EDa).",12.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1801.62),new Module(2,ModuleInfo.M08,"Kw cont.",94.72),new Module(3,ModuleInfo.M04,"Mesa",53.85),new Module(4,ModuleInfo.M06,"Maquina A",167.17),new Module(5,ModuleInfo.M07,"Maquina B",640.77),new Module(6,ModuleInfo.M58,"Euro",0.07)})
		,E_16723 ("16723","Cafeterias de una taza (cafet. y comercializac. loter\u00EDa).",12.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1801.62),new Module(2,ModuleInfo.M08,"Kw cont.",94.72),new Module(3,ModuleInfo.M04,"Mesa",53.85),new Module(4,ModuleInfo.M06,"Maquina A",167.17),new Module(5,ModuleInfo.M07,"Maquina B",640.77),new Module(6,ModuleInfo.M58,"Euro",0.07)})
		,E_16731 ("16731","Caf\u00E9s y bares de categor\u00EDa especial.", 8.0,6,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",2525.96),new Module(2,ModuleInfo.M08,"Kw cont.",51.09),new Module(3,ModuleInfo.M04,"Mesa",46.45),new Module(4,ModuleInfo.M05,"Metro",61.32),new Module(5,ModuleInfo.M06,"Maquina A",167.17),new Module(6,ModuleInfo.M07,"Maquina B",501.50),new Module(7,ModuleInfo.M58,"Euro",0.07)})
		,E_16732 ("16732","Otros cafes y bares.", 8.0,6,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1978.07),new Module(2,ModuleInfo.M08,"Kw cont.",36.20),new Module(3,ModuleInfo.M04,"Mesa",42.71),new Module(4,ModuleInfo.M05,"Metro",51.09),new Module(5,ModuleInfo.M06,"Maquina A",136.38),new Module(6,ModuleInfo.M07,"Maquina B",505.19),new Module(7,ModuleInfo.M58,"Euro",0.07)})
		,E_1675  ("1675" ,"Cafes-bares en quioscos,cajones,barracas.", 8.0,3,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",3298.31),new Module(2,ModuleInfo.M08,"Kw cont.",34.58),new Module(3,ModuleInfo.M09,"Metro cua.",3.06),new Module(4,ModuleInfo.M58,"Euro",0.07)})
		,E_1676  ("1676" ,"Chocolaterias,heladerias y horchaterias.",18.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1978.07),new Module(2,ModuleInfo.M08,"Kw cont.",113.31),new Module(3,ModuleInfo.M04,"Mesa",30.64),new Module(4,ModuleInfo.M06,"Maquina A",133.72),new Module(5,ModuleInfo.M58,"Euro",0.07)})
		,E_1681  ("1681" ,"Hospedaje en hoteles y moteles.",18.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1996.63),new Module(2,ModuleInfo.M20,"Plaza",40.83),new Module(3,ModuleInfo.M58,"Euro",0.07)})
		,E_1682  ("1682" ,"Hospedaje en hostales y pensiones.",18.0,20,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1996.63),new Module(2,ModuleInfo.M20,"Plaza",40.83)})
		,E_1683  ("1683" ,"Hospedajes en fondas y casas huespedes.",18.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1346.57),new Module(2,ModuleInfo.M20,"Plaza",22.29)})
		,E_16911 ("16911","Reparaci\u00F3n de art\u00EDculos el\u00E9ctricos para el hogar.",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",2089.51),new Module(2,ModuleInfo.M09,"Metro cua.",2.04)})
		,E_16912 ("16912","Reparaci\u00F3n de autom\u00F3viles, bicicletas y otros veh\u00EDculos.",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",3074.62),new Module(2,ModuleInfo.M09,"Metro cua.",6.15)})
		,E_16919A("16919","Reparaci\u00F3n de calzado.",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1077.23),new Module(2,ModuleInfo.M03,"100 Kwh",15.78)},1) 
		,E_16919B("16919","Reparacion de otros bienes n.c.o.p. (excepto reparacion de calzado).",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1940.91),new Module(2,ModuleInfo.M09,"Metro cua.",5.11)},2)
		,E_1692  ("1692" ,"Reparacion de maquinaria industrial.",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",3491.75),new Module(2,ModuleInfo.M09,"Metro cua.",20.43)})
		,E_1699  ("1699" ,"Otras reparaciones ncop.",15.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",2767.43),new Module(2,ModuleInfo.M09,"Metro cua.",16.70)})
		,E_17211 ("17211","Tte. urbano colectivo.", 8.0,1,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",525.18),new Module(2,ModuleInfo.M21,"Asiento",25.33)})
		,E_17212 ("17212","Tte. por autotaxis.",15.0,10,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",281.32),new Module(2,ModuleInfo.M12,"1000 Km",5.38)})
		,E_17213 ("17213","Tte. viajeros por carretera.", 8.0,1,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",525.18),new Module(2,ModuleInfo.M21,"Asiento",25.33)})
		,E_1722A ("1722" ,"Tte. mercancias por carretera.",14.0,10,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",600.17),new Module(2,ModuleInfo.M23,"Tonelada",56.27)},1) 
		,E_1722B ("1722" ,"Transporte de residuos por carretera.",14.0,1,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",263.04),new Module(2,ModuleInfo.M23,"Tonelada",24.53)},2)
		,E_17515 ("17515","Engrase y lavado de vehiculos.",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",3073.84),new Module(2,ModuleInfo.M09,"Metro cua.",6.14)})
		,E_1757  ("1757" ,"Servicio de mudanzas.",14.0,10,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",859.64),new Module(2,ModuleInfo.M23,"Tonelada",38.64)})
		,E_18495 ("18495","Transp.mensaj. y recad., cuando se realice exclusiv. con transp. prop.",14.0,10,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",980.97),new Module(2,ModuleInfo.M23,"Tonelada",271.96)})
		,E_19331 ("19331","Ense\u00F1. conduccion vehiculos.",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1077.23),new Module(2,ModuleInfo.M25,"veh\u00EDculo",92.86),new Module(3,ModuleInfo.M24,"CVF",37.14)})
		,E_19339 ("19339","Otros activ. ense\u00F1anza.",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",863.67),new Module(2,ModuleInfo.M09,"Metro cua.",0.95)})
		,E_19672 ("19672","Escuelas y serv. perfecc. del deporte.",23.0,3,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",940.30),new Module(2,ModuleInfo.M09,"Metro cua.",1.62)})
		,E_19711 ("19711","Tinte,limp. seco, lavado y planchado. ",23.0,48,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1383.69),new Module(2,ModuleInfo.M03,"100 Kwh",5.56)})
		,E_19721 ("19721","Serv. peluqueria se\u00F1oras y caballeros.",23.0,13,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",835.80),new Module(2,ModuleInfo.M09,"Metro cua.",18.76),new Module(3,ModuleInfo.M03,"100 Kwh",8.45)})
		,E_19722 ("19722","Salones e institutos de belleza.",23.0,32,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",1067.97),new Module(2,ModuleInfo.M09,"Metro cua.",18.57),new Module(3,ModuleInfo.M03,"100 Kwh",8.36)})
		,E_19733 ("19733","Serv. copias documentos maq.fotocopia.",14.0,30,new Module[]{ new Module(1,ModuleInfo.M26,"Persona",4726.89),new Module(2,ModuleInfo.M08,"Kw cont.",86.74)})
		;
		
		private String epigraph;
		private String description;
		private Module[] igicModules; 
		private double porcIng;       // Porcentaje de ingreso a cuenta
		private double porcMin;       // Porcentaje de cuota mínima
		private int specialEpigraph;  // Indicador auxiliar de actividad para determinados epígrafes (16919 y 1722)
		
		private EpigraphCanarias(String epigraph, String description, double porcIng, double porcMin, Module[] igicModules) {
			this.epigraph = epigraph;
			this.description = description;
			this.porcIng = porcIng; 
			this.porcMin = porcMin;
			this.igicModules = igicModules;
			this.specialEpigraph = 0;
		}
		
		private EpigraphCanarias(String epigraph, String description, double porcIng, double porcMin, Module[] igicModules, int specialEpigraph) {
			this.epigraph = epigraph;
			this.description = description;
			this.porcIng = porcIng; 
			this.porcMin = porcMin;
			this.igicModules = igicModules;
			this.specialEpigraph = specialEpigraph;
		}
		
		public String getEpigraph() {
			return epigraph;
		}
		public String getDescription() {
			return description;
		}
		public double getPorcIng() {
			return porcIng;
		}
		public double getPorcMin() {
			return porcMin;
		}
		public Module[] getIgicModules() {
			return igicModules;
		}
		
		@Override
		public int getSpecialEpigraph() {
			return specialEpigraph;
		}

		public static EpigraphCanarias getEpigraph(String code, int specialEpigraph) {
			for (EpigraphCanarias epi: EpigraphCanarias.values()) {
				if (AonStringUtils.equals(epi.getEpigraph(), code) && specialEpigraph == epi.getSpecialEpigraph()) {
					return epi;
				}
			}
			return null;
		}

		public static int getSpecialEpigraph(String code, String description) {
			// Epígrafes que tienen indicador auxiliar, porque llevan el mismo código de epígrafe
			if (AonStringUtils.equals(code,"16919")) {
				if (AonStringUtils.contains(AonStringUtils.upperCase(description),"OTROS BIENES")) {
					return 2; // Resto
				} else {
					return 1; // Reparación de calzado.
				}
			} else if (AonStringUtils.equals(code,"1722")) {
				if (AonStringUtils.contains(AonStringUtils.upperCase(description),"TRANSPORTE DE RESIDUOS")) {
					return 2; // Transporte de residuos por carretera
				} else {
					return 1; // Transporte de mercancías por carretera, excepto residuos
				}
			} else {
				return 0;				
			}
		}
		
	}
	
}