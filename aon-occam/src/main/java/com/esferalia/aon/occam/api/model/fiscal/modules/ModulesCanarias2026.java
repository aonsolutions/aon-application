package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ModulesCanarias2026 {
	

	// EN CANARIAS ESTAN TODOS JUNTOS
//	public enum FarmerIVA implements IFarmerIVA {
//		 A01 ("01",0.10,	  	12.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de carne y avicultura de carne.")
//		,A02 ("02",0.04,	     2.00, "Ganadera de explotaci\u00F3n intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche.")
//		,A03 ("03",0.10,	  	24.00, "Ganadera de explotaci\u00F3n intensiva de ganado bovino de carne y cunicultura.")
//		,A04 ("04",0.10,	  	32.00, "Ganadera de explotaci\u00F3n intensiva de ganado porcino de cr\u00EDa, bovino de cr\u00EDa y otras intensivas o extensivas no comprendidas expresamente en otros apartados.")
//		,A05 ("05",0.10,	  	40.00, "Ganadera de explotaci\u00F3n intensiva de ganado ovino y caprino de carne.")
//		,A06 ("06",0.06625,		40.00, "Servicios de cr\u00EDa, guarda y engorde de aves.")
//		,A07 ("07",0.07,  		32.00, "Apicultura.")
//		,A08 ("08",0.10,	    48.00, "Otros trabajos y servicios accesorios realizados por agricultores, ganaderos o titulares de actividades forestales que est\u00E9n excluidos del r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido, y servicios de cr\u00EDa, guarda y engorde de ganado, excepto aves.")
//		,A09 ("09",0.21,   	  	80.00, "Actividades accesorias realizadas por agricultores, ganaderos o titulares de actividades forestales no incluidas en el r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca del Impuesto sobre el Valor A\u00F1adido.")
//		,A10 ("10",0.04,         2.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de productos agr\u00EDcolas no comprendidas en los apartados siguientes.")
//		,A11 ("11",0.07625,	  	28.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de forrajes.")
//		,A12 ("12",0.21,	  	44.00, "Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de plantas textiles y tabaco.")
//		,A13 ("13",0.21,	  	44.00, "Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en r\u00E9gimen de aparcer\u00EDa.")
//		,A14 ("14",0.07,  		28.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de queso.")
//		,A15 ("15",0.2675, 		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino de mesa.")
//		,A16 ("16",0.2675, 		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino con denominaci\u00F3n de origen.")
//		,A17 ("17",0.16406, 	80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de aceites de oliva.")
//		,A18 ("18",0.19625,		80.00, "Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de otros productos distintos a los anteriores.")
//		;
//
//		private String code;
//		private double indiceRendimientoNeto;
//		private double porcentaje;
//		private String description;
//		
//		private FarmerIVA(String code,double indiceRendimientoNeto, double porcentaje, String description) {
//			this.code = code;
//			this.indiceRendimientoNeto =indiceRendimientoNeto;
//			this.porcentaje = porcentaje;
//			this.description = description;
//		}
//		@Override
//		public String getCode() {
//			return code;
//		}
//		@Override
//		public double getIndiceRendimientoNeto() {
//			return indiceRendimientoNeto;
//		}
//		@Override
//		public double getPorcentaje() {
//			return porcentaje;
//		} 
//		@Override
//		public String getDescription() {
//			return description;
//		}
//		public static FarmerIVA safeValueOf( String code ) {
//			if (AonStringUtils.isBlank(code)) {
//				return null;
//			}
//			for (FarmerIVA farmerIVA : FarmerIVA.values()) {
//				if (AonStringUtils.equals(farmerIVA.getCode(), code)) {
//					return farmerIVA;
//				}
//			}
//			return null;
//		}
//		public static FarmerIVA getFarmerIVA(String code) {
//			for (FarmerIVA farmerIVA: FarmerIVA.values()) {
//				if (AonStringUtils.equals(farmerIVA.getCode(), code)) {
//					return farmerIVA;
//				}
//			}
//			return null;
//		}
//	}
	
	public enum EpigraphCanarias implements Serializable, IEpigraphCanarias {
		
		// ACTIVIDADES AGRICOLAS Y GANADERAS
//		0001 2016010100021001231SERVICIOS DE CRÍA, GUARDA Y ENGORDE DE AVES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            00000000000                         00035040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 E_0001 ("0001","Servicios de cr\u00EDa, guarda y engorde de aves."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0350)} 
					)

//		0002 2020010100021001231OTROS TRABAJOS Y SERVICIOS ACCESORIOS REALIZADOS POR AGRICULTORES O GANADEROS O TITULARES DE ACTIVIDADES FORESTALES QUE ESTÉN EXCLUIDOS DEL RÉGIMEN ESPECIAL DE LA AGRICULTURA Y GANADERÍA DEL IMPUESTO GENERAL INDIRECTO CANARIO, Y SERVICOS DE CRÍA Y ENGORDE DE GANADO, EXCEPTO AVES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                00000000000                         00070048000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0002 ("0002","Otros trabajos y servicios accesorios realizados por agricultores o ganaderos o titulares de actividades forestales que est\u00E9n excluidos del r\u00E9gimen especial de la agricultura y ganader\u00EDa del impuesto general indirecto canario, y servicios de cr\u00EDa y engorde de ganado, excepto aves."
					,48.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0700)} 
					)

//		0003 2020010100021001231ACTIVIDADES ACCESORIAS REALIZADAS POR AGRICULTORES O GANADEROS O TITULARES DE ACTIVIDADES FORESTALES NO INCLUIDAS EN EL RÉGIMEN DE LA AGRICULTURA Y GANADERÍA DEL IMPUESTO GENERAL INDIRECTO CANARIO                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   00000000000                         00070080000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0003 ("0003","Actividades accesorias realizadas por agricultores o ganaderos o titulares de actividades forestales no incluidas en el r\u00E9gimen de la agricultura y ganader\u00EDa del impuesto general indirecto canario."
					,80.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0700)} 
					)
		
//		0004 2014010100021001231APROVECHAMIENTOS QUE CORRESPONDAN AL CEDENTE EN LAS ACTIVIDADES AGRÍCOLAS, DESARROLLADAS EN RÉGIMEN DE APARCERÍA, DEDICADAS A LA OBTENCIÓN DE PRODUCTOS AGRÍCOLAS NO COMPRENDIDAS EN LOS APARTADOS SIGUIENTES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          00000000000                         00010002000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0004 ("0004","Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de productos agr\u00EDcolas no comprendidas en los apartados siguientes."
					,2.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0100)} 
					)
//		0005 2014010100021001231APROVECHAMIENTOS QUE CORRESPONDAN AL CEDENTE EN LAS ACTIVIDADES AGRÍCOLAS, DESARROLLADAS EN RÉGIMEN DE APARCERÍA, DEDICADAS A LA OBTENCIÓN DE FORRAJES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 00000000000                         00015028000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0005 ("0005","Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de forrajes."
					,28.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0150)} 
					)
//		0006 2014010100021001231APROVECHAMIENTOS QUE CORRESPONDAN AL CEDENTE EN LAS ACTIVIDADES AGRÍCOLAS, DESARROLLADAS EN EL RÉGIMEN DE APARCERÍA, DEDICADAS A LA OBTENCIÓN DE PLANTAS TEXTILES Y TABACO                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             00000000000                         00050044000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0006 ("0006","Aprovechamientos que correspondan al cedente en las actividades agr\u00EDcolas, desarrolladas en el r\u00E9gimen de aparcer\u00EDa, dedicadas a la obtenci\u00F3n de plantas textiles y tabaco."
					,44.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0500)} 
					)
//		0007 2014010100021001231APROVECHAMIENTOS QUE CORRESPONDAN AL CEDENTE EN LAS ACTIVIDADES FORESTALES, DESARROLLADAS EN RÉGIMEN DE APARCERÍA                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      00000000000                         00050044000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0007 ("0007","Aprovechamientos que correspondan al cedente en las actividades forestales, desarrolladas en r\u00E9gimen de aparcer\u00EDa."
					,44.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0500)} 
					)
//		0008 2014010100021001231PROCESOS DE TRANSFORMACIÓN, ELABORACIÓN O MANUFACTURA DE PRODUCTOS NATURALES PARA LA OBTENCIÓN DE VINO DE MESA                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         00000000000                         00055080000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0008 ("0008","Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino de mesa."
					,80.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0550)} 
					)
//		0009 2014010100021001231PROCESOS DE TRANSFORMACIÓN, ELABORACIÓN O MANUFACTURA DE PRODUCTOS NATURALES PARA LA OBTENCIÓN DE VINO CON DENOMINACIÓN DE ORIGEN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      00000000000                         00055080000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0009 ("0009","Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de vino con denominaci\u00F3n de origen."
					,80.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0550)} 
					)
//		0010 2014010100021001231PROCESOS DE TRANSFORMACIÓN, ELABORACIÓN O MANUFACTURA DE PRODUCTOS NATURALES PARA LA OBTENCIÓN DE OTROS PRODUCTOS DISTINTOS A LOS ANTERIORES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           00000000000                         00050080000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_0010 ("0010","Procesos de transformaci\u00F3n, elaboraci\u00F3n o manufactura de productos naturales para la obtenci\u00F3n de otros productos distintos a los anteriores."
					,80.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0500)} 
					)
//		000112016010100021001231GANADERA DE EXPLOTACIÓN INTENSIVA DE GANADO PORCINO DE CARNE Y AVICULTURA DE CARNE.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00011 ("00011","Ganadera de explotaci\u00F3n intensiva de ganado porcino de carne y avicultura de carne."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
//		000122016010100021001231GANADERA DE EXPLOTACIÓN INTENSIVA DE AVICULTURA DE HUEVOS Y, GANADO OVINO, CAPRINO Y BOVINO DE LECHE.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00012 ("00012","Ganadera de explotaci\u00F3n intensiva de avicultura de huevos y, ganado ovino, caprino y bovino de leche."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
//		000132016010100021001231GANADERA DE EXPLOTACIÓN INTENSIVA DE GANADO BOVINO DE CARNE Y CUNICULTURA.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00013 ("00013","Ganadera de explotaci\u00F3n intensiva de ganado bovino de carne y cunicultura."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
//		000142016010100021001231GANADERA DE EXPLOTACIÓN INTENSIVA DE GANADO DE PORCINO DE CRÍA, BOVINO DE CRÍA Y OTRAS INTENSIVAS O EXTENSIVAS NO COMPRENDIDAS EXPRESAMENTE EN OTROS APARTADOS.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00014 ("00014","Ganadera de explotaci\u00F3n intensiva de ganado de porcino de cr\u00EDa, bovino de cr\u00EDa y otras intensivas o extensivas no comprendidas expresamente en otros apartados."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
//		000152016010100021001231GANADERA DE EXPLOTACIÓN INTENSIVA DE GANADO OVINO Y CAPRINO DE CARNE.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00015 ("00015","Ganadera de explotaci\u00F3n intensiva de ganado ovino y caprino de carne."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
//		000162016010100021001231APICULTURA.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            00000000000                         00030040000000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000000                                                                           000                         0000000000
		 ,E_00016 ("00016","Apicultura."
					,40.0
					,0
//					,0
					,new Module[]{
						 new Module(1,ModuleInfo.M63,"",0.0300)} 
					)
		
		 ,E_14191 ("14191","Industrias del pan y de la boller\u00EDa."
				,9.0
				,20
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",479.85)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.10)
					,new Module(3,ModuleInfo.M14,"100dm.cua.",10.42)}
				)
		,E_14192 ("14192","Industrias de la boller\u00EDa, pasteler\u00EDa y galletas."
				,14.0
				,30
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",975.54)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.92)
					,new Module(3,ModuleInfo.M14,"100dm.cua.",24.15)}
				)
		,E_14193 ("14193","Industrias de elaboraci\u00F3n de masas fritas."
				,15.0
				,32
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",811.36)
					,new Module(2,ModuleInfo.M09,"Metro cua.",3.90)}
				)
		,E_14239 ("14239","Industrias de patatas fritas, palomitas de ma\u00EDz y similares."
				,15.0
				,32
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",811.36)
					,new Module(2,ModuleInfo.M09,"Metro cua.",3.90)}
				)
		,E_16421 ("16421","Elaboraci\u00F3n de productos de charcuter\u00EDa por minoristas de carne."
				,15.0
				,32
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",550.56)
					,new Module(2,ModuleInfo.M09,"Metro cua.",0.82)}
				)
		,E_16422 ("16422","Elaboraci\u00F3n de productos de charcuter\u00EDa por minoristas de carne."
				,15.0
				,32
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",550.56)
					,new Module(2,ModuleInfo.M09,"Metro cua.",0.82)}
				)
		,E_16423 ("16423","Elaboraci\u00F3n de productos de charcuter\u00EDa por minoristas de carne."
				,15.0
				,32
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",550.56)
					,new Module(2,ModuleInfo.M09,"Metro cua.",0.82)}
				)
		,E_16425 ("16425","Comerciantes minoristas matriculados en el ep\u00EDgrafe 642.5 por el asado de pollos."
				,15.0
				,32
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",212.49)
					,new Module(2,ModuleInfo.M09,"Metro cua.",4.87)
					,new Module(3,ModuleInfo.M57,"Pieza",19.32)}
				)
		,E_16441 ("16441","Fabricaci\u00F3n de pan especial y productos de pasteler\u00EDa, boller\u00EDa, confiter\u00EDa y helados para su venta en el propio establecimiento, y por servicios de comercializaci\u00F3n de loter\u00EDa."
				,9.0
				,20
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",499.85)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.18)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",10.87)
					,new Module(4,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16442 ("16442","Fabricaci\u00F3n de pan especial y productos de boller\u00EDa para su venta en el propio establecimiento, y por servicios de comercializaci\u00F3n de loter\u00EDa."
				,9.0
				,20
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",499.85)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.18)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",10.87)
					,new Module(4,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16443 ("16443","Fabricaci\u00F3n de productos de pasteler\u00EDa, boller\u00EDa y confiter\u00EDa para su venta en el propio establecimiento, y por servicios de comercializaci\u00F3n de loter\u00EDa."
				,14.0
				,30
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",975.54)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.92)
					,new Module(3,ModuleInfo.M14,"100 dmcua.",19.32)
					,new Module(4,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16446 ("16446","Elaboraci\u00F3n de masas fritas con o sin cobertura o rellenos, patatas fritas, productos de aperitivo, frutos secos, golosinas, preparados de chocolate y bebidas refrescantes para su venta en el propio establecimiento, y por servicios de comercializaci\u00F3n de loter\u00EDa."
				,15.0
				,32
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",792.01)
					,new Module(2,ModuleInfo.M09,"Metro cua.",3.90)
					,new Module(3,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16471 ("16471","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,5
				,new Module[]{
						new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16472 ("16472","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,4
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16473 ("16473","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,4
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16522 ("16522","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,4
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16523 ("16523","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,4
				,new Module[]{
					new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16532 ("16532","Instalaci\u00F3n y reparaci\u00F3n de aparatos el\u00E9ctricos, electr\u00F3nicos, electrodom\u00E9sticos y otros aparatos de uso dom\u00E9stico accionados por otro tipo de energ\u00EDa distinta de la el\u00E9ctrica, as\u00ED como de muebles de cocina."
				,23.0
				,48
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M27,"Persona",2110.40)
					,new Module(2,ModuleInfo.M28,"Metro cua.",2.04)}
				)
		,E_16534 ("16534","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,6.0
				,13
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1847.65)
					,new Module(2,ModuleInfo.M03,"100 Kwh",32.50)
					,new Module(3,ModuleInfo.M09,"Metro cua.",1.34)}
				)
		,E_16535 ("16535","Comercio al por menor de materiales de construcci\u00F3n, art\u00EDculos y mobiliario de saneamiento, puertas, ventanas, persianas, etc."
				,6.0
				,13
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1847.65)
					,new Module(2,ModuleInfo.M03,"100 Kwh",32.50)
					,new Module(3,ModuleInfo.M09,"Metro cua.",1.34)}
				)
		,E_16542 ("16542","Comercio al por menor de accesorios y piezas de recambio para veh\u00EDculos terrestres."
				,6.0
				,13
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",5823.49)
					,new Module(2,ModuleInfo.M03,"100 Kwh",97.53)
					,new Module(3,ModuleInfo.M24,"CVF",232.16)}
				)
		,E_16545 ("16545","Comercio al por menor de toda clase de maquinaria (excepto aparatos del hogar, de oficina, m\u00E9dicos, ortop\u00E9dicos, \u00F3pticos y fotogr\u00E1ficos)."
				,6.0
				,13
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",6750.89)
					,new Module(2,ModuleInfo.M03,"100 Kwh",26.93)
					,new Module(3,ModuleInfo.M24,"CVF",30.95)}
				)
		,E_16546 ("16546","Comercio al por menor de cubiertas, bandas o bandajes y c\u00E1maras de aire para toda clase de veh\u00EDculos."
				,6.0
				,13
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2110.96)
					,new Module(2,ModuleInfo.M03,"100 Kwh",29.677)
					,new Module(3,ModuleInfo.M24,"CVF",56.02)}
				)
		,E_16593 ("16593","Servicio de recogida de negativos y otro material fotogr\u00E1fico impresionado para su procesado en laboratorio de terceros y la entrega de las correspondientes copias y ampliaciones."
				,6.0
				,13
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M62,"Persona",5228.37)
					,new Module(2,ModuleInfo.M09,"Metro cua.",11.14)}
				)
		,E_16594("16594","Servicio de venta de tarjetas para el transporte p\u00FAblico, tarjetas de uso telef\u00F3nico y otras similares, as\u00ED como loter\u00EDas."
				,100.0
				,75
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M58,"Euro",0.07)}				
				)
		,E_16622 ("16622","Servicios de comercialización de loter\u00EDa."
				,0.0
				,75
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16631 ("16631","Elaboraci\u00F3n de churrer\u00EDa y patatas fritas para su venta en la propia instalaci\u00F3n o veh\u00EDculo."
				,9.0
				,20
//				,2
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1808.15)
					,new Module(2,ModuleInfo.M24,"CVF",12.98)}
				)
		,E_16714 ("16714","Restaurantes de dos tenedores y por servicios de comercialización de loter\u00EDa."
				,12.0
				,13
//				,10
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2303.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",111.42)
					,new Module(3,ModuleInfo.M04,"Mesa",129.99)
					,new Module(4,ModuleInfo.M06,"Maquina A",185.75)
					,new Module(5,ModuleInfo.M07,"Maquina B",645.78)
					,new Module(6,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16715 ("16715","Restaurantes de un tenedor y por servicios de comercialización de loter\u00EDa."
				,18.0
				,20
//				,10
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1848.03)
					,new Module(2,ModuleInfo.M08,"Kw cont.",53.85)
					,new Module(3,ModuleInfo.M04,"Mesa",94.71)
					,new Module(4,ModuleInfo.M06,"Maquina A",185.75)
					,new Module(5,ModuleInfo.M07,"Maquina B",650.06)
					,new Module(6,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16721 ("16721","Cafeter\u00EDas y por servicios de comercialización de loter\u00EDa."
				,12.0
				,13
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1801.62)
					,new Module(2,ModuleInfo.M08,"Kw cont.",94.72)
					,new Module(3,ModuleInfo.M04,"Mesa",53.85)
					,new Module(4,ModuleInfo.M06,"Maquina A",167.17)
					,new Module(5,ModuleInfo.M07,"Maquina B",640.77)
					,new Module(6,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16722 ("16722","Cafeter\u00EDas."
				,12.0
				,13
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1801.62)
					,new Module(2,ModuleInfo.M08,"Kw cont.",94.72)
					,new Module(3,ModuleInfo.M04,"Mesa",53.85)
					,new Module(4,ModuleInfo.M06,"Maquina A",167.17)
					,new Module(5,ModuleInfo.M07,"Maquina B",640.77)
					,new Module(6,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16723 ("16723","Cafeter\u00EDas."
				,12.0
				,13
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1801.62)
					,new Module(2,ModuleInfo.M08,"Kw cont.",94.72)
					,new Module(3,ModuleInfo.M04,"Mesa",53.85)
					,new Module(4,ModuleInfo.M06,"Maquina A",167.17)
					,new Module(5,ModuleInfo.M07,"Maquina B",640.77)
					,new Module(6,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16731 ("16731","Caf\u00E9s y bares de categor\u00EDa especial y por servicios de comercialización de loter\u00EDa."
				,8.0
				,6
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2525.96)
					,new Module(2,ModuleInfo.M08,"Kw cont.",51.09)
					,new Module(3,ModuleInfo.M04,"Mesa",46.45)
					,new Module(4,ModuleInfo.M05,"Metro",61.32)
					,new Module(5,ModuleInfo.M06,"Maquina A",167.17)
					,new Module(6,ModuleInfo.M07,"Maquina B",501.50)
					,new Module(7,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_16732 ("16732","Otros caf\u00E9s y bares, y por servicios de comercialización de loter\u00EDa.."
				,8.0
				,6
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1978.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",36.20)
					,new Module(3,ModuleInfo.M04,"Mesa",42.71)
					,new Module(4,ModuleInfo.M05,"Metro",51.09)
					,new Module(5,ModuleInfo.M06,"Maquina A",136.38)
					,new Module(6,ModuleInfo.M07,"Maquina B",505.19)
					,new Module(7,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_1675   ("1675"	 ,"Servicios en quioscos, cajones, barracas u otros locales an\u00E1logos, y por servicios de comercialización de loter\u00EDa."
				,8.0
				,3
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3298.31)
					,new Module(2,ModuleInfo.M08,"Kw cont.",34.58)
					,new Module(3,ModuleInfo.M09,"Metro cua.",3.06)
					,new Module(4,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_1676   ("1676"	 ,"Servicios en chocolater\u00EDas, helader\u00EDas y horchater\u00EDas, y por servicios de comercialización de loter\u00EDa."
				,18.0
				,20
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1978.07)
					,new Module(2,ModuleInfo.M08,"Kw cont.",113.31)
					,new Module(3,ModuleInfo.M04,"Mesa",30.64)
					,new Module(4,ModuleInfo.M06,"Maquina A",133.72)
					,new Module(5,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_1681   ("1681"	 ,"Servicio de hospedaje en hoteles y moteles de una o dos estrellas, y por servicios de comercialización de loter\u00EDa."
				,18.0
				,20
//				,10
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1996.63)
					,new Module(2,ModuleInfo.M20,"Plaza",40.83)
					,new Module(3,ModuleInfo.M58,"Euro",0.07)}
				)
		,E_1682   ("1682"	 ,"Servicio de hospedaje en hostales y pensiones."
				,18.0
				,20
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1996.63)
					,new Module(2,ModuleInfo.M20,"Plaza",40.83)}
				)
		,E_1683   ("1683"	 ,"Servicio de hospedaje en fondas y casas de hu\u00E9spedes."
				,18.0
				,30
//				,8
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1346.57)
					,new Module(2,ModuleInfo.M20,"Plaza",22.29)}
				)
		,E_1691_1 ("16911","Reparaci\u00F3n de art\u00EDculos el\u00E9ctricos para el hogar."
				,23.0
				,48
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2089.51)
					,new Module(2,ModuleInfo.M09,"Metro cua.",2.04)}
				)
		,E_1691_2 ("16912","Reparaci\u00F3n de veh\u00EDculos autom\u00F3viles, bicicletas y otros veh\u00EDculos."
				,14.0
				,30
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3074.62)
					,new Module(2,ModuleInfo.M09,"Metro cua.",6.15)}
				)
		,E_16919A("16919","Reparaci\u00F3n de calzado."
				,23.0
				,48
//				,2
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1077.23)
					,new Module(2,ModuleInfo.M03,"100 Kwh",15.78)}
				,1)
		,E_16919B("16919","Reparaci\u00F3n de otros bienes de consumo n.c.o.p. (excepto reparaci\u00F3n de calzado, restauraci\u00F3n de obras de arte, muebles, antig\u00FCedades e instrumentos musicales)."
				,23.0
				,48
//				,2
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1940.91)
					,new Module(2,ModuleInfo.M09,"Metro cua.",5.11)}
				,2)
		
		,E_1692   ("1692"	 ,"Reparaci\u00F3n de maquinaria industrial."
				,14.0
				,30
//				,2
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3491.75)
					,new Module(2,ModuleInfo.M09,"Metro cua.",20.43)}
				)
		,E_1699   ("1699"	 ,"Otras reparaciones n.c.o.p."
				,15.0
				,32
//				,2
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",2767.43)
					,new Module(2,ModuleInfo.M09,"Metro cua.",16.70)}
				)
		,E_17211 ("17211","Transporte urbano colectivo y de viajeros por carretera."
				,8.0
				,1
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",525.18)
					,new Module(2,ModuleInfo.M21,"Asiento",25.33)}
				)
		,E_17212 ("17212","Transporte por autotaxis."
				,15.0
				,10
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",281.32)
					,new Module(2,ModuleInfo.M12,"1000 Km",5.38)}
				)
		,E_17213 ("17213","Transporte urbano colectivo y de viajeros por carretera."
				,8.0
				,1
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",525.18)
					,new Module(2,ModuleInfo.M21,"Asiento",25.33)}
				)
		,E_1722A  ("1722", "Transporte de mercanc\u00EDas por carretera, excepto residuos."
				,14.0
				,10
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",600.17)
					,new Module(2,ModuleInfo.M23,"Tonelada",56.27)}
				,1)
		,E_1722B  ("1722", "Transporte de residuos por carretera."
				,14.0  
				,1
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",263.04)
					,new Module(2,ModuleInfo.M23,"Tonelada",24.53)}
				,2)
		,E_17515 ("17515","Engrase y lavado de veh\u00EDculos."
				,14.0
				,30
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",3073.84)
					,new Module(2,ModuleInfo.M09,"Metro cua.",6.14)}
				)
		,E_1757   ("1757"	 ,"Servicios de mudanzas."
				,14.0
				,10
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",859.64)
					,new Module(2,ModuleInfo.M23,"Tonelada",38.64)}
				)
		,E_18495 ("18495","Transporte de mensajer\u00EDa y recader\u00EDa, cuando la actividad se realice exclusivamente con medios de transporte propios."
				,14.0
				,10
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",980.97)
					,new Module(2,ModuleInfo.M23,"Tonelada",271.96)}
				)
		,E_19331 ("19331","Ense\u00F1anza de conducci\u00F3n de veh\u00EDculos terrestres, acu\u00E1ticos, aeron\u00E1uticos, etc."
				,23.0
				,48
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1077.23)
					,new Module(2,ModuleInfo.M25,"veh\u00EDculo",92.86)
					,new Module(3,ModuleInfo.M24,"CVF",37.14)}
				)
		,E_19339 ("19339","Otras actividades de ense\u00F1anza, tales como idiomas, corte y confecci\u00F3n, mecanograf\u00EDa, taquigraf\u00EDa, preparaci\u00F3n de ex\u00E1menes y oposiciones y similares n.c.o.p."
				,23.0
				,48
//				,5
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",863.67)
					,new Module(2,ModuleInfo.M09,"Metro cua.",0.95)}
				)
		,E_19672 ("19672","Escuelas y servicios de perfeccionamiento del deporte."
				,23.0
				,3
//				,3
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",940.30)
					,new Module(2,ModuleInfo.M09,"Metro cua.",1.62)}
				)
		,E_19711 ("19711","Tinte, limpieza en seco, lavado y planchado de ropas hechas y de prendas y art\u00EDculos del hogar usados."
				,23.0
				,48
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1383.69)
					,new Module(2,ModuleInfo.M03,"100 Kwh",5.56)}
				)
		,E_19721 ("19721","Servicios de peluquer\u00EDa de se\u00F1ora y caballero."
				,23.0
				,13
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",835.80)
					,new Module(2,ModuleInfo.M09,"Metro cua.",18.76)
					,new Module(3,ModuleInfo.M03,"100 Kwh",8.45)}
				)
		,E_19722 ("19722","Salones e institutos de belleza."
				,23.0
				,32
//				,6
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",1067.97)
					,new Module(2,ModuleInfo.M09,"Metro cua.",18.57)
					,new Module(3,ModuleInfo.M03,"100 Kwh",8.36)}
				)
		,E_19733 ("19733","Servicios de copias de documentos con m\u00E1quinas fotocopiadoras."
				,14.0
				,30
//				,4
				,new Module[]{
					 new Module(1,ModuleInfo.M26,"Persona",4726.89)
					,new Module(2,ModuleInfo.M08,"Kw cont.",86.74)}
				)
		;
		
		private String epigraph;
		private String description;
//		private Module[] irpfModules;
		private Module[] vatModules;
		private double vatPorc;
		private double porcMin;
//		private double limPers; // FALTA - NO SE SI ES NECESARIO
//		private double limExceso;
		private int specialEpigraph; // Indicador auxiliar de actividad para determinados epígrafes
		
//		private EpigraphCanarias(String epigraph, String description, double vatPorc, double porcMin, double limPers, Module[] ivaModules) {
//			this.epigraph = epigraph;
//			this.description = description;
//			this.vatPorc = vatPorc; 
//			this.porcMin=porcMin;
//			this.limPers=limPers;
////			this.limExceso=limExceso;
////			this.irpfModules=irpfModules;
//			this.vatModules=ivaModules;
//			this.specialEpigraph=0;
//		}
//		
//		private EpigraphCanarias(String epigraph, String description, double vatPorc, double porcMin, double limPers, Module[] ivaModules, int specialEpigraph) {
//			this.epigraph = epigraph;
//			this.description = description;
//			this.vatPorc = vatPorc; 
//			this.porcMin=porcMin;
//			this.limPers=limPers;
////			this.limExceso=limExceso;
////			this.irpfModules=irpfModules;
//			this.vatModules=ivaModules;
//			this.specialEpigraph=specialEpigraph;
//		}
		
		private EpigraphCanarias(String epigraph, String description, double vatPorc, double porcMin, Module[] ivaModules) {
			this.epigraph = epigraph;
			this.description = description;
			this.vatPorc = vatPorc; 
			this.porcMin=porcMin;
//			this.limPers=limPers;
//			this.limExceso=limExceso;
//			this.irpfModules=irpfModules;
			this.vatModules=ivaModules;
			this.specialEpigraph=0;
		}
		
		private EpigraphCanarias(String epigraph, String description, double vatPorc, double porcMin, Module[] ivaModules, int specialEpigraph) {
			this.epigraph = epigraph;
			this.description = description;
			this.vatPorc = vatPorc; 
			this.porcMin=porcMin;
//			this.limPers=limPers;
//			this.limExceso=limExceso;
//			this.irpfModules=irpfModules;
			this.vatModules=ivaModules;
			this.specialEpigraph=specialEpigraph;
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
		// FALTA - ESTO NO SE SI SE USA EN ALGUN SITIO
//		public double getLimPers() {
//			return limPers;
//		}
		
//		public double getLimExceso() {
//			return limExceso;
//		}
//		public Module[] getIRPFModules() {
//			return irpfModules;
//		}
		public Module[] getVATModules() {
			return vatModules;
		}
//		public boolean hasVATModules() {
//			return vatModules != null;
//		}
//		public boolean hasIRPFModules() {
//			return irpfModules != null;
//		}
		@Override
		public int getSpecialEpigraph() {
			return specialEpigraph;
		}

// NO SE PUEDE UTILIZAR ESTE METODO PARA OBTENER EL EPIGRAFE PORQUE HAY MODULOS QUE TIENEN EL MISMO CODIGO DE EPIGRAFE 
//		public static boolean hasEpigraph(String code) {
//			return getEpigraph(code) != null; 
//		}
//		public static Epigraph getEpigraph(String code) {
//			for (Epigraph epi: Epigraph.values()) {
//				if (AonStringUtils.equals(epi.getEpigraph(), code)) {
//					return epi;
//				}
//			}
//			return null;
//		}
		
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
				if (AonStringUtils.contains(description,"consumo")) {
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
