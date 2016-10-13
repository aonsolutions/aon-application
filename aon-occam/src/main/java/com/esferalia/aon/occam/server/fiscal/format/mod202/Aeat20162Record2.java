package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.jooq.tools.csv.CSVReader;

import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Aeat20162Record2 implements Serializable,IMod202Record{
	 C01((writer,mod202) -> writer.append("<T"))
	,C02((writer,mod202) -> writer.append("202"))
	,C03((writer,mod202) -> writer.append("02"))
	,C04((writer,mod202) -> writer.append(">"))
	,C05((writer,mod202) -> writer.append( AonStringUtils.SPACE ))
	,C06((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C19),17)))
	,C07((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C20),17)))
	,C08((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C21),5)))
	,C09((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C22),17)))
	,C10((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C23),17)))
	,C11((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C24),5)))
	,C12((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C25),17)))
	,C13((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C50),17)))
	,C14((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C42),17)))
	,C15((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C51),17)))
	,C16((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C52),17)))
	,C17((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C26),17)))
	,C18((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C27),17)))
	,C19((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C28),17)))
	,C20((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C29),5)))
	,C21((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C30),17)))
	,C22((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C31),17)))
	,C23((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C32),17)))
	,C24((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C33),17)))
	,C25((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C34),17)))
	,C26((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.A01))))
	,C27((writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.A02),22)))
	,C28((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A03),17)))
	,C29((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A04),17)))
	,C30((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.isReplacement())))
	,C31((writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getReplacedNumber(),13)))
	,C32((writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getIban(),34)))
	,C33((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(160)))
	,C34((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(13)))
	,C35((writer,mod202) -> writer.append("</T20202>"))
	;
	
	private IRecordFiller filler;
	
	 private Aeat20162Record2(IRecordFiller filler) {
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
