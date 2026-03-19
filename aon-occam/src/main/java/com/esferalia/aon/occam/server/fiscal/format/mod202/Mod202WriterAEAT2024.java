package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer.IMod202Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202WriterAEAT2024 implements IMod202Writer{ 

	private static enum Mod202File {
		
		AEAT ( mod202 -> true,new IPropertyFiller[] { 
			 (wr,mod) -> wr.append("<T")
			,(wr,mod) -> wr.append("202")
			,(wr,mod) -> wr.append("0")
			,(wr,mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.getMod202Period(mod))
			,(wr,mod) -> wr.append("0000>")
			,(wr,mod) -> wr.append("<AUX>")
		    ,(wr,mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		    ,(wr,mod) -> wr.append(AonFiscalFileUtils.text("2020", 4))
		    ,(wr,mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		    ,(wr,mod) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9))
		    ,(wr,mod) -> wr.append(AonStringUtils.repeat(' ', 213))
			,(wr,mod) -> wr.append("</AUX>")
		}),
		AEAT_1 ( mod202 -> true,new IPropertyFiller[] { 
			 (wr,mod) -> wr.append("<T20201000>")
			,(wr,mod) -> wr.append( AonStringUtils.SPACE )
			,(wr,mod) -> wr.append( mod.getAeatDeclarationType() )
			,(wr,mod) -> wr.append( AonFiscalFileUtils.document(mod.getDocument()))
			,(wr,mod) -> wr.append(getName(mod)) // 60
			,(wr,mod) -> wr.append(getSurname(mod)) // 20
			,(wr,mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.getMod202Period(mod)) 
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod202Key.P02),8))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getCnae()==null?"0000":AonStringUtils.remove(mod.getCnae(), "."),4))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X01)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X02)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X04)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X12)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X06)))
			,(wr,mod) -> wr.append(getAEAT20171X13X14(mod) )
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod202Key.X08),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.X09),1 ,0))
			,(wr,mod) -> wr.append(mod.getAmount(Mod202Key.X11) > 0?'X':' ')
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C01),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C02),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C03),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C04),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C05),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C06),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C37),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C07),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C08),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C38),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C39),17))
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
			,(wr,mod) -> wr.append(AonMathUtils.isZero(mod.getAmount(Mod202Key.X00))?"A":"B")
			,(wr,mod) -> {
				if (mod.getAmount(Mod202Key.X15) == 1) {
					wr.append('1');		
				} else if (mod.getAmount(Mod202Key.X16) == 1) {
					wr.append('2');
				} else if (mod.getAmount(Mod202Key.X17) == 1) {
					wr.append('3');
				} else if (mod.getAmount(Mod202Key.X18) == 1) {
					wr.append('4');
				} else {
					wr.append(' ');
				}
			}
			,(wr,mod) -> wr.append( AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X19) == 1))
			,(wr,mod) -> wr.append( AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.X20) == 1))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.spaces(172))
			,(wr,mod) -> wr.append("</T20201000>")
		}),
		AEAT_2 ( mod202 -> true,new IPropertyFiller[] { 
			 (wr,mod) -> wr.append("<T20202000>")
			,(wr,mod) -> wr.append( AonStringUtils.SPACE )
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C19),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C20),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C21),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C22),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C23),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C24),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod202Key.C25),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C50),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C42),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C51),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C52),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C26),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C27),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C28),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C29),5))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C30),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C31),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C32),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C33),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.C34),17))
			
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod202Key.A01)))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod202Key.A02),22))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A03),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A04),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A05),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A06),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A07),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A08),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A09),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A10),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A11),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A12),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod202Key.A13),17))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.mark(mod.isComplementary()))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.text(mod.getReplacedNumber(),13))
		    ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
			,(wr,mod) -> wr.append(AonFiscalFileUtils.spaces(101))
			,(wr,mod) -> wr.append(AonFiscalFileUtils.spaces(13))
			,(wr, mod) -> wr.append("</T20202000>")			
			
			,(wr, mod) -> wr.append("</T")
			,(wr, mod) -> wr.append("202")
			,(wr, mod) -> wr.append("0")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.getMod202Period(mod))
			,(wr, mod) -> wr.append("0000>")
			})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod202File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		
		private static String getName(Mod202 mod) {
			if ( AonDocumentUtil.isEntity(mod.getDocument()) ) {
				return AonFiscalFileUtils.fullName(mod.getName(),mod.getSurname(),60);
			}
			return AonFiscalFileUtils.text(mod.getSurname(),60);
		}
		private static String getSurname(Mod202 mod) {
			if ( AonDocumentUtil.isEntity(mod.getDocument()) ) {
				return AonFiscalFileUtils.spaces(20);
			}
			return AonFiscalFileUtils.text(mod.getName(),20);
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
	
	private static char getAEAT20171X13X14(Mod202 mod) {
		boolean x13 = mod.getAmount(Mod202Key.X13) == 1;
		boolean x14 = mod.getAmount(Mod202Key.X14) == 1;
		if (x13 && x14) return '2';
		if (x13) return '1';
		if (x14) return '2';
		return '0';
	}	
}
