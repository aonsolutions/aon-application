package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.format.Mod202Writer.IMod202Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod202Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod202Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202WriterAEAT2015 implements IMod202Writer{ 

	private static enum Mod202File {
		
		AEAT_2015_1 ( mod202 -> true ,new IPropertyFiller[] { 
			 (wr,mod) -> wr.append("<T")
			,(wr,mod) -> wr.append("202")
			,(wr,mod) -> wr.append("01")
			,(wr,mod) -> wr.append(">")
			,(wr,mod) -> wr.append( AonStringUtils.SPACE )
			,(wr,mod) -> wr.append( mod.getAeatDeclarationType() )
			,(wr,mod) -> wr.append( AonFiscalFileUtils.document(mod.getDocument()))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.fullName(mod.getName(),mod.getSurname(),60))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.spaces(20))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))
			,(wr,mod) -> {
				if (mod.getPeriod() == Period.T1) {
					wr.append("1P"); 
				} else if (mod.getPeriod() == Period.T2) {
					wr.append("2P");
				} else if (mod.getPeriod() == Period.T3) {
					wr.append("3P");
				} else {
					wr.append("  ");
				}
						 }
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod202Key.P02),8))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.P01),4))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X01)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X02)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X03)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X04)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X05)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X06)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.X07),1,0))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod202Key.X08),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.X09),1 ,0))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X10)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C01),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C02),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C03),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C04),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C05),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C06),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C36),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C37),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C07),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C08),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C38),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C39),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C09),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C43),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C13),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C44),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C14),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C45),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C46),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C16),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C17),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C47),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C40),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C48),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C49),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C18),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.spaces(130))
			,(wr,mod) -> wr.append("</T20201>")
		})
		,AEAT_2015_2 ( mod202 ->  true,new IPropertyFiller[] { 
			 (writer,mod202) -> writer.append("<T")
			,(writer,mod202) -> writer.append("202")
			,(writer,mod202) -> writer.append("02")
			,(writer,mod202) -> writer.append(">")
			,(writer,mod202) -> writer.append( AonStringUtils.SPACE )
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C19),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C20),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C21),5))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C22),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C23),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C24),5))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C25),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.signedZero(mod202.getAmount(Mod202Key.C50),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C42),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C51),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C52),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C26),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C27),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C28),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C29),5))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C30),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C31),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C32),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C33),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.C34),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.getAmount(Mod202Key.A01)))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getDescription(Mod202Key.A02),22))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A03),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.unsigned(mod202.getAmount(Mod202Key.A04),17))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.mark(mod202.isReplacement()))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getReplacedNumber(),13))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.text(mod202.getIban(),34))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(160))
			,(writer,mod202) -> writer.append(AonFiscalFileUtils.spaces(13))
			,(writer,mod202) -> writer.append("</T20202>")
		});
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod202File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod202 mod202) {
			return accepter.accept(mod202);
		}
		private void fillPage(Mod202 mod202, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod202);
			}
		}
	}

	public void fillWriter(Mod202 mod202, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod202File format : Mod202File.values()) {
			if (format.accept(mod202)) {
				format.fillPage(mod202, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
