package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.jooq.tools.csv.CSVReader;

import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Aeat2015Record2 implements Serializable,IMod202Record{
	//	Inicio del identificador de modelo y página
	//	Constante "<T"
	//	Tipo: An Pos:1 Long: 2
	//	Inicio del identificador de modelo y página
	C1(
			 (writer,mod202) -> {writer.append("<T");}
	)
	//	Modelo
	//	Constante "202"
	//	Tipo: Num Pos:3 Long: 3
	//	Modelo
	,C2(
			 (writer,mod202) -> {writer.append("202");}
	)
	//	Página
	//	Constante "02"
	//	Tipo: Num Pos:6 Long: 2
	//	Página
	,C3(
			 (writer,mod202) -> {writer.append("02");}
	)
	//	Fin de identificador de modelo
	//	Constante ">
	//	Tipo: An Pos:8 Long: 1
	//	Fin de identificador de modelo
	,C4(
			 (writer,mod202) -> {writer.append(">");}
	)
	//	Reservado para la Administración
	//	En blanco
	//	Tipo: An Pos:9 Long: 1
	//	Reservado para la Administración
	,C5(
			 (writer,mod202) -> {writer.append( AonStringUtils.SPACE );}
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base del pago fraccionado [19]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:10 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base del pago fraccionado [19]
	,C6(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C19),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 1 [20]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:27 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 1 [20]
	,C7(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C20),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [21]
	//	3 enteros + 2 decimales
	//	Tipo: Num Pos:44 Long: 5
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [21]
	,C8(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C21),5))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [22]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:49 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [22]
	,C9(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C22),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 2 [23]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:66 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 2 [23]
	,C10(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C23),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [24]
	//	3 enteros + 2 decimales
	//	Tipo: Num Pos:83 Long: 5
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [24]
	,C11(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C24),5))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [25]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:88 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [25]
	,C12(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C25),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje). Dotaciones del art. 11.12 de la LIS (sólo cooperativas) (DF 4ª LIS) [50]
	//	15 enteros + 2 decimales
	//	Tipo: N Pos:105 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje). Dotaciones del art. 11.12 de la LIS (sólo cooperativas) (DF 4ª LIS) [50]
	,C13(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.signed(mod202.getAmount(Mod202Key.C50),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje). Compensación de cuotas negativas de períodos anteriores (sólo cooperativas) [42]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:122 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje). Compensación de cuotas negativas de períodos anteriores (sólo cooperativas) [42]
	,C14(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C42),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (sólo entidades del art. 101 LIS). Aumentos [51]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:139 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (sólo entidades del art. 101 LIS). Aumentos [51]
	,C15(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C51),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (sólo entidades del art. 101 LIS). Disminuciones [52]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:156 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (sólo entidades del art. 101 LIS). Disminuciones [52]
	,C16(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C52),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Resultado previo(claves [22]+[25]-[50]-[42]+[51]-[52]) [26]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:173 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Resultado previo(claves [22]+[25]-[50]-[42]+[51]-[52]) [26]
	,C17(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C26),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Bonificaciones correspondientes al periodo computado (total) [27]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:190 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Bonificaciones correspondientes al periodo computado (total) [27]
	,C18(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C27),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Retenciones e ingresos a cuenta practicados sobre ingresos periodo computado [28]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:207 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Retenciones e ingresos a cuenta practicados sobre ingresos periodo computado [28]
	,C19(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C28),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Volumen operaciones en Territorio Común (%) [29]
	//	3 enteros + 2 decimales
	//	Tipo: Num Pos:224 Long: 5
	//	B) Liquidación. Mod. 40.3 LIS - Volumen operaciones en Territorio Común (%) [29]
	,C20(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C29),5))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Pagos fraccionados de periodos anteriores en Territorio Común [30]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:229 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Pagos fraccionados de periodos anteriores en Territorio Común [30]
	,C21(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C30),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Resultado de la declaración anterior (exclusivamente si ésta es complementaria) [31]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:246 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Resultado de la declaración anterior (exclusivamente si ésta es complementaria) [31]
	,C22(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C31),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Resultado [32]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:263 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Resultado [32]
	,C23(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C32),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Mínimo a ingresar (sólo para empresas con CN igual o superior a 20 millones euros) [33]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:280 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Mínimo a ingresar (sólo para empresas con CN igual o superior a 20 millones euros) [33]
	,C24(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C33),17))
	)
	//	B) Liquidación. Mod. 40.3 LIS - Cantidad a ingresar (mayor de claves [32] y [33] )  [34]
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:297 Long: 17
	//	B) Liquidación. Mod. 40.3 LIS - Cantidad a ingresar (mayor de claves [32] y [33] )  [34]
	,C25(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C34),17))
	)
	//	Información adicional  (5). Comunicación de datos adicionales a la declaración
	//	X o blanco
	//	Tipo: An Pos:314 Long: 1
	//	Información adicional  (5). Comunicación de datos adicionales a la declaración
	,C26(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.A01)))
	)
	//	Información adicional  (5). Numero de Referencia de Sociedades (NRS)
	//	
	//	Tipo: An Pos:315 Long: 22
	//	Información adicional  (5). Numero de Referencia de Sociedades (NRS)
	,C27(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.A02),22))
	)
	//	Información adicional  (5). Importe excluido por operaciones de quita o espera
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:337 Long: 17
	//	Información adicional  (5). Importe excluido por operaciones de quita o espera
	,C28(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A03),17))
	)
	//	Información adicional  (5). Parte integrada en la base imponible por operaciones de quita o espera
	//	15 enteros + 2 decimales
	//	Tipo: Num Pos:354 Long: 17
	//	Información adicional  (5). Parte integrada en la base imponible por operaciones de quita o espera
	,C29(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A04),17))
	)
	//	Declaración complementaria (6)
	//	X o blanco
	//	Tipo: An Pos:371 Long: 1
	//	Declaración complementaria (6)
	,C30(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.isReplacement()))
	)
	//	Declaración complementaria (6). Número de justificante de la declaración anterior
	//	
	//	Tipo: An Pos:372 Long: 13
	//	Declaración complementaria (6). Número de justificante de la declaración anterior
	,C31(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getReplacedNumber(),13))
	)
	//	Domiciliación - IBAN 
	//	nota 7
	//	Tipo: An Pos:385 Long: 34
	//	Domiciliación - IBAN 
	,C32(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getIban(),34))
	)
	//	Reservado para la Administración
	//	En blanco
	//	Tipo: An Pos:419 Long: 160
	//	Reservado para la Administración
	,C33(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(160))
	)
	//	Reservado para el sello electrónico de la AEAT
	//	
	//	Tipo: An Pos:579 Long: 13
	//	Reservado para el sello electrónico de la AEAT
	,C34(
			(writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(13))
	)
	//	Indicador de fin de registro
	//	Constante "</T20202>"
	//	Tipo: An Pos:592 Long: 9
	//	Indicador de fin de registro
	,C35(
			(writer,mod202) -> writer.append("</T20202>")
	)
	;
	
	private IRecordFiller filler;
	
	 private Aeat2015Record2(IRecordFiller filler) {
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
