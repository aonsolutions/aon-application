package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.jooq.tools.csv.CSVReader;

import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Aeat2015Record1 implements Serializable,IMod202Record{
	//	Inicio del identificador de modelo y página
	//	Constante "<T"
	//	Tipo: An Pos:1 Long: 2
	//	Inicio del identificador de modelo y página
	C01(
		 (writer,mod202) -> {writer.append("<T");}
	)
	//	Modelo
	//	Constante "202"
	//	Tipo: Num Pos:3 Long: 3
	//	Modelo
	,C02(
		 (writer,mod202) -> {writer.append("202");}
	)
	//	Página
	//	Constante "01"
	//	Tipo: Num Pos:6 Long: 2
	//	Página
	,C03(
		 (writer,mod202) -> {writer.append("01");}
	)
	//	Fin de identificador de modelo
	//	Constante ">"
	//	Tipo: An Pos:8 Long: 1
	//	Fin de identificador de modelo
	,C04(
		 (writer,mod202) -> {writer.append(">");}
	)
	//	Reservado para la Administración
	//	En blanco
	//	Tipo: An Pos:9 Long: 1
	//	Reservado para la Administración
	,C05(
		 (writer,mod202) -> {writer.append( AonStringUtils.SPACE );}
	)
	//	Tipo de declaración
	//	Ver nota 1
	//	Tipo: A Pos:10 Long: 1
	//	Tipo de declaración
	,C06(
		 (writer,mod202) -> {writer.append( mod202.getAeatDeclarationType() );}
	)
	//	Identificación (1). NIF
	//	
	//	Tipo: An Pos:11 Long: 9
	//	Identificación (1). NIF
	,C07(
		 (writer,mod202) -> {writer.append( AonFiscalFileUtils.document(mod202.getDocument()));}
	)
	//	Identificación (1). Apellidos y nombre o razón social
	//	
	//	Tipo: An Pos:20 Long: 60
	//	Identificación (1). Apellidos y nombre o razón social
	,C08(
		 (writer,mod202) -> {writer.append(AonFiscalFileUtils.fullName(mod202.getName(),mod202.getSurname(),60));}
	)
	//	Reservado para la Administración
	//	
	//	Tipo: An Pos:80 Long: 20
	//	Reservado para la Administración
	,C09(
		 (writer,mod202) -> {writer.append(AonFiscalFileUtils.spaces(20));}
	)
	//	Devengo (2). Ejercicio
	//	
	//	Tipo: Num Pos:100 Long: 4
	//	Devengo (2). Ejercicio
	,C10(
		 (writer,mod202) -> {writer.append(AonFiscalFileUtils.year(mod202.getYear()));}
	)
	//	Devengo (2). Periodo
	//	1P", "2P" o "3P
	//	Tipo: An Pos:104 Long: 2
	//	Devengo (2). Periodo
	,C11(
		 (writer,mod202) -> {
				 if (mod202.getPeriod() == Period.T1) {
					 writer.append("1P"); 
				 } else if (mod202.getPeriod() == Period.T2) {
					 writer.append("2P");
				 } else if (mod202.getPeriod() == Period.T3) {
					 writer.append("3P");
				 } else {
						 writer.append("  ");
				 }
			 }
	)
	//	Devengo (2). Fecha de inicio del período impositivo
	//	ddmmaaaa
	//	Tipo: Num Pos:106 Long: 8
	//	Devengo (2). Fecha de inicio del período impositivo
	,C12(
		(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.P02),8))
	)
	//	Devengo (2). C.N.A.E. actividad principal
	//	
	//	Tipo: Num Pos:114 Long: 4
	//	Devengo (2). C.N.A.E. actividad principal
	,C13(
		(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.P01),4))
	)
	//	Datos adicionales (3) - Entidad que aplica el régimen de la Ley 49/2002 de 23 de diciembre
	//	X o blanco
	//	Tipo: An Pos:118 Long: 1
	//	Datos adicionales (3) - Entidad que aplica el régimen de la Ley 49/2002 de 23 de diciembre
	,C14(
		(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X01)))
	)
	//	Datos adicionales (3) - Entidad que aplica el régimen de la Ley 11/2009 de 26 de octubre
	//	X o blanco
	//	Tipo: An Pos:119 Long: 1
	//	Datos adicionales (3) - Entidad que aplica el régimen de la Ley 11/2009 de 26 de octubre
	,C15(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X02)))
	)
	//	Datos adicionales (3) - Volumen de operaciones superior a 6.010.121 euros
	//	X o blanco
	//	Tipo: An Pos:120 Long: 1
	//	Datos adicionales (3) - Volumen de operaciones superior a 6.010.121 euros
	,C16(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X03)))
	)
	//	Datos adicionales (3) - Entidad que aplica el régimen de las entidades navieras en función del tonelaje
	//	X o blanco
	//	Tipo: An Pos:121 Long: 1
	//	Datos adicionales (3) - Entidad que aplica el régimen de las entidades navieras en función del tonelaje
	,C17(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X04)))
	)
	//	Datos adicionales (3) - Entidades que aplican incentivos de empresa de reducida dimensión
	//	X o blanco
	//	Tipo: An Pos:122 Long: 1
	//	Datos adicionales (3) - Entidades que aplican incentivos de empresa de reducida dimensión
	,C18(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X05)))
	)
	//	Datos adicionales (3) - Cifra de negocios de los 12 meses anteriores a la fecha de inicio del período impositivo > 6.000.000  ?
	//	X o blanco
	//	Tipo: An Pos:123 Long: 1
	//	Datos adicionales (3) - Cifra de negocios de los 12 meses anteriores a la fecha de inicio del período impositivo > 6.000.000  ?
	,C19(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X06)))
	)
	//	Datos adicionales (3) - Cooperativa fiscalmente protegida u Otras entidades con posibilidad de aplicar dos tipos impositivos (ej. entidades ZEC)
	//	0" No consta, "1" Cooperativa, "2" Otras entidades
	//	Tipo: Num Pos:124 Long: 1
	//	Datos adicionales (3) - Cooperativa fiscalmente protegida u Otras entidades con posibilidad de aplicar dos tipos impositivos (ej. entidades ZEC)
	,C20(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.X07),1,0))
	)
	//	Datos adicionales (3) - Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso
	//	Cadena alfanumérica de 5 posiciones para permitir consignar dos tipos. Ejemplos: "00", "01", "25", "20/25
	//	Tipo: An Pos:125 Long: 5
	//	Datos adicionales (3) - Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso
	,C21(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.X08),5))
	)
	//	Datos adicionales (3) - Importe neto de la cifra de negocios 
	//	0" No consta, "1" (>= 10 M y < 20 M ?), "2" (>= 20  M y < 60 M ?), "3" (>= 60 M ?)
	//	Tipo: Num Pos:130 Long: 1
	//	Datos adicionales (3) - Importe neto de la cifra de negocios 
	,C22(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.X09),1 ,0))
	)
	//	Datos adicionales (3) - Entidades en las que al menos el 85% de ingresos del periodo impositivo(?)
	//	X o blanco
	//	Tipo: An Pos:131 Long: 1
	//	Datos adicionales (3) - Entidades en las que al menos el 85% de ingresos del periodo impositivo(?)
	,C23(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X10)))
	)
	//	A) Liquidación. Mod. 40.2 LIS - Base del pago fraccionado [01]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:132 Long: 17
	//	A) Liquidación. Mod. 40.2 LIS - Base del pago fraccionado [01]
	,C24(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C01),17))
	)
	//	A) Liquidación. Mod. 40.2 LIS - Resultado de la declaración anterior (complementarias) [02]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:149 Long: 17
	//	A) Liquidación. Mod. 40.2 LIS - Resultado de la declaración anterior (complementarias) [02]
	,C25(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C02),17))
	)
	//	A) Liquidación. Mod. 40.2 LIS - A Ingresar [03]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:166 Long: 17
	//	A) Liquidación. Mod. 40.2 LIS - A Ingresar [03]
	,C26(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C03),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Resultado contable después del IS [04]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:183 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Resultado contable después del IS [04]
	,C27(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C04),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades - Aumentos [05]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:200 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades - Aumentos [05]
	,C28(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C05),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades - Disminuciones [06]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:217 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades - Disminuciones [06]
	,C29(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C06),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - 30% gastos amortiz (exc.  emp. reducidas) - Aumentos [36]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:234 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - 30% gastos amortiz (exc.  emp. reducidas) - Aumentos [36]
	,C30(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C36),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - 30% gastos amortiz (exc.  emp. reducidas) - Disminuciones [37]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:251 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - 30% gastos amortiz (exc.  emp. reducidas) - Disminuciones [37]
	,C31(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C37),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Aumentos [07]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:268 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Aumentos [07]
	,C32(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C07),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Disminuciones [08]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:285 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Disminuciones [08]
	,C33(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C08),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - TOTAL. - Aumentos [38]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:302 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - TOTAL. - Aumentos [38]
	,C34(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C38),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - TOTAL - Disminuciones [39]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:319 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - TOTAL - Disminuciones [39]
	,C35(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C39),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - 25% del importe de los dividendos y rentas devengadas de fuente extranjera [09]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:336 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - 25% del importe de los dividendos y rentas devengadas de fuente extranjera [09]
	,C36(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C09),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - 100% del importe de los dividendos y rentas devengadas de entidades residentes [43]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:353 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - 100% del importe de los dividendos y rentas devengadas de entidades residentes [43]
	,C37(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C43),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Base imponible previa [13]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:370 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Base imponible previa [13]
	,C38(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C13),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Remanente reserva de capitalización no aplicada por insuficiencia de base [44]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:387 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Remanente reserva de capitalización no aplicada por insuficiencia de base [44]
	,C39(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C44),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Compensación de bases negativas de ejercicios anteriores [14]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:404 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Compensación de bases negativas de ejercicios anteriores [14]
	,C40(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C14),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (Solo entidades del art. 101 LIS) - Aumentos [45]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:421 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (Solo entidades del art. 101 LIS) - Aumentos [45]
	,C41(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C45),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (Solo entidades del art. 101 LIS) - Disminuciones [46]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:438 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (Solo entidades del art. 101 LIS) - Disminuciones [46]
	,C42(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C46),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Base pago fraccionado [16]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:455 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Base pago fraccionado [16]
	,C43(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C16),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Porcentaje [17]
	//	3 enteros + 2 decimales
	//	Tipo: Num Pos:472 Long: 5
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Porcentaje [17]
	,C44(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C17),5))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Dotaciones del art. 11.12 LIS (DF 4ª LIS) [47]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:477 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Dotaciones del art. 11.12 LIS (DF 4ª LIS) [47]
	,C45(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C47),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Compensación de cuotas negativas ejer. anteriores (sólo cooperativas) [40]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:494 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Compensación de cuotas negativas ejer. anteriores (sólo cooperativas) [40]
	,C46(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C40),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas - Aumentos [48]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:511 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas - Aumentos [48]
	,C47(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C48),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas - Disminuciones [49]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:528 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas - Disminuciones [49]
	,C48(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C49),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general  -  Resultado previo (clave ([16] x [17]) - [47]-[40]+[48]-[49]) [18]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:545 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B1 - Caso general  -  Resultado previo (clave ([16] x [17]) - [47]-[40]+[48]-[49]) [18]
	,C49(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C18),17))
	)
	//	Reservado para la Administración
	//	En blanco
	//	Tipo: An Pos:562 Long: 130
	//	Reservado para la Administración
	,C50(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(130))
	)
	//	Indicador de fin de registro
	//	Constante "</T20201>"
	//	Tipo: An Pos:692 Long: 9
	//	Indicador de fin de registro
	,C51(
			(writer,mod202) -> writer.append("</T20201>")
	)
	;
	
	private IRecordFiller filler;
	
	 private Aeat2015Record1(IRecordFiller filler) {
		 this.filler = filler;
	 }
	 
	public IRecordFiller getFiller() {
		return filler;
	}

	public static void main(String[] args) throws IOException {
		FileReader reader = new FileReader("/home/ecastellano/Documents/mod202.csv");
		CSVReader csvreader = new CSVReader(reader,'|');
		PrintStream out = System.out;
		while ( csvreader.hasNext() ) {
			String[] tokens = csvreader.readNext();
			if (tokens != null && tokens.length>0) {
				out.println("//\t" + tokens[5]);
				out.println("//\t" + tokens[4]);
				out.println("//\t" + "Tipo: " + tokens[3] + " Pos:" + tokens[1] + " Long: " + tokens[2]);
				out.println("//\t" + tokens[5]);
				out.println(",C" + tokens[0] + "(" );
				out.println("\t (writer,mod202) -> {writer.append(' ');}");
				out.println(")" );
			}
		}
		csvreader.close();
	}
	
}
