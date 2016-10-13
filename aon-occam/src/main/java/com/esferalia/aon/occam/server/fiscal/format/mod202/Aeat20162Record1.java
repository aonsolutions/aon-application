package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.jooq.tools.csv.CSVReader;

import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Aeat20162Record1 implements Serializable,IMod202Record{
	 C01((writer,mod202) -> writer.append("<T"))
	,C02((writer,mod202) -> writer.append("202"))
	,C03((writer,mod202) -> writer.append("01"))
	,C04((writer,mod202) -> writer.append(">"))
	,C05((writer,mod202) -> writer.append( AonStringUtils.SPACE ))
	,C06((writer,mod202) -> writer.append( mod202.getAeatDeclarationType() ))
	,C07((writer,mod202) -> writer.append( AonFiscalFileUtils.document(mod202.getDocument())))
	,C08((writer,mod202) -> writer.append(AonFiscalFileUtils.fullName(mod202.getName(),mod202.getSurname(),60)))
	,C09((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(20)))
	,C10((writer,mod202) -> writer.append(AonFiscalFileUtils.year(mod202.getYear())))
	,C11((writer,mod202) -> writer.append(AonFiscalFileUtils.getMod202Period(mod202.getPeriod()))) 
	,C12((writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.P02),8)))
	,C13((writer,mod202) -> writer.append(AonFiscalFileUtils.text(AonStringUtils.remove(mod202.getCnae(), "."),4)))
	,C14((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X01))))
	,C15((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X02))))
	,C16((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X03))))
	,C17((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X04))))
	,C18((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X05))))
	,C19((writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.X06))))
	,C20((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.X07),1,0)))
	,C21((writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.X08),5)))
	,C22((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.X09),1 ,0)))
	,C23((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.X10),1 ,0)))
	,C24((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C01),17)))
	,C25((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C02),17)))
	,C26((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C03),17)))
	,C27((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C04),17)))
	,C28((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C05),17)))
	,C29((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C06),17)))
	,C30((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C36),17)))
	,C31((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C37),17)))
	,C32((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C07),17)))
	,C33((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C08),17)))
	,C34((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C38),17)))
	,C35((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C39),17)))
	,C36((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(17)))
	,C37((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(17)))
	,C38((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C13),17)))
	,C39((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C44),17)))
	,C40((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C14),17)))
	,C41((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C45),17)))
	,C42((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C46),17)))
	,C43((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C16),17)))
	,C44((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C17),5)))
	,C45((writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C47),17)))
	,C46((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C40),17)))
	,C47((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C48),17)))
	,C48((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C49),17)))
	,C49((writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C18),17)))
	,C50((writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(130)))
	,C51((writer,mod202) -> writer.append("</T20201>"))
	;
	
	private IRecordFiller filler;
	
	 private Aeat20162Record1(IRecordFiller filler) {
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
