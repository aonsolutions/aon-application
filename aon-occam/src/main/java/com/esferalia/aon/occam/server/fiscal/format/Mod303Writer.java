package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303Writer {

	@FunctionalInterface
	private interface IModelAccepter {
		public boolean accept(Mod303 mod303);
	}
	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod303 mod303) throws IOException;
	}
	
	private static SimpleDateFormat df = new SimpleDateFormat("MMMMM");
	
	private enum Mod303File2016 {
		
		// **************************************************************** AEAT < 2017 - 4T 									
		AEAT_2017_REG_0_START (mod303 -> (mod303.isAEAT() && ((mod303.getYear() <= 2016) || (mod303.getYear() == 2017 && !mod303.isLastPeriod()))) ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_1 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() <= 2016) || (mod303.getYear() == 2017 && !mod303.isLastPeriod()))) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30301000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CM_002)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getAmount(Mod303Key.CT_A02)+1),1))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A03)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A04)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.CT_A05)))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A06)==0?" ":"1")		// TODO Soporte POSTCONCURSAL
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A07)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A08)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A09)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A10)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C02), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C04),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C05), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C08), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C09),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C11),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C13),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C16),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C17), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C18),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C19),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C20), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C21),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C22),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C23), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C24),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C25),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C26),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C27),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C28),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C29),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C30),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C31),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C32),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C33),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C34),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C35),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C36),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C37),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C38),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C39),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C40),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C41),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C42),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C43),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C44),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C45),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C46),17,2))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 582))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T30301000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_2 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() <= 2016) || (mod303.getYear() == 2017 && !mod303.isLastPeriod())))
				&& mod303.getAmount(Mod303Key.CT_A02) < 2 // Simplificado
				,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30302000>")
		   ,(wr, mod) -> wr.append(" ")
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA11))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA13)/10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA15),5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA16),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA17),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA18),17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA21))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA22),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA23)/10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA24),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA25),5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA26),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA27),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_SA28),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S101), '.')  ,4))
		   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S101, Mod303Key.CT_S102, Mod303Key.CT_S12F, 181.58, 13.23))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S11I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S11R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S12I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S12R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S13I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S13R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S14I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S14R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S15I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S15R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S16I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S16R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S17I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S17R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S117)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S118)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S119)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S120)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S121)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S122)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S123)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S124)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S125)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S126)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S127)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S128)  ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S201), '.')  ,4))
		   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S201, Mod303Key.CT_S202, Mod303Key.CT_S22F, 181.58, 13.23))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S21I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S21R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S22I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S22R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S23I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S23R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S24I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S24R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S25I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S25R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S26I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S26R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S27I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S27R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S217)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S218)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S219)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S220)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S221)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S222)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S223)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S224)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S225)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S226)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S227)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S228)  ,17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S47)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S48)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S49)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S50)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S51)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S52)  ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S53)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S54)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S55)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S56)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S57)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S58)  ,17,2))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 590))
		   ,(wr, mod) -> wr.append("</T30302000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_3 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() <= 2016) || (mod303.getYear() == 2017 && !mod303.isLastPeriod()))) ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("<T30303000>")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C59),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C60),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C61),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C62),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C63),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C74),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C75),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C76),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C64),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C65),5,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 4	))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C66),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_C77),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_C67),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C68),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C69),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C70),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C71),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
					   					:AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isWithoutActivity()?"X":" ",1))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 11))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A11)==0?"0":"1")
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 590))
			   ,(wr, mod) -> wr.append("</T30303000>")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_0_END (mod303 -> (mod303.isAEAT() && ((mod303.getYear() <= 2016) || (mod303.getYear() == 2017 && !mod303.isLastPeriod()))) ,new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		// **************************************************************** AEAT = 2017 - 4T 									
		,AEAT_2017_4T_REG_0_START (mod303 -> (mod303.isAEAT() && ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))) ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_4T_REG_1 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30301000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CM_002)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getAmount(Mod303Key.CT_A02)+1),1))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A03)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A04)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.CT_A05)))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A06)==0?" ":"1")		// TODO Soporte POSTCONCURSAL
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A07)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A08)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A09)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A10)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C02), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C04),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C05), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C08), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C09),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C11),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C13),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C16),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C17), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C18),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C19),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C20), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C21),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C22),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C23), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C24),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C25),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C26),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C27),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C28),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C29),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C30),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C31),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C32),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C33),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C34),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C35),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C36),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C37),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C38),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C39),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C40),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C41),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C42),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C43),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C44),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C45),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C46),17,2))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A11)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 581))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T30301000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_4T_REG_2 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30302000>")
		   ,(wr, mod) -> wr.append(" ")
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA11))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA12) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA13) / 10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA14) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA15) ,5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA16) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA17) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA18) ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA21))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA22)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA23) / 10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA24)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA25)  ,5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA26)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA27),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA28),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S101), '.')  ,4))
		   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S101, Mod303Key.CT_S102, Mod303Key.CT_S12F, 181.58, 13.23))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S11I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S11R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S12I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S12R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S13I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S13R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S14I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S14R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S15I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S15R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S16I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S16R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S17I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S17R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S117)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S118)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S119)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S120)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S121)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S122)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S123)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S124)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S125)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S126)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S127)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S128)  ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S201), '.')  ,4))
		   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S201, Mod303Key.CT_S202, Mod303Key.CT_S22F, 181.58, 13.23))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S21I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S21R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S22I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S22R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S23I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S23R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S24I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S24R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S25I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S25R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S26I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S26R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S27I)  ,10,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S27R)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S217)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S218)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S219)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S220)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S221)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S222)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S223)  , 3,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S224)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S225)  , 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S226)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S227)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S228)  ,17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S47)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S48)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S49)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S50)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S51)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S52)  ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S53)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S54)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S55)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S56)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S57)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S58)  ,17,2))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 590))
		   ,(wr, mod) -> wr.append("</T30302000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_4T_REG_3 (mod303 -> (mod303.isAEAT() && ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))) ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("<T30303000>")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C59),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C60),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C61),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C62),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C63),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C74),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C75),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C76),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C64),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C65),9,6))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C66),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C77),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C67),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C68),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C69),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C70),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C71),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
					   					:AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isWithoutActivity()?"X":" ",1))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 11))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34))
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod303Key.CT_U1C), 1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U1E),4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod303Key.CT_U2C), 1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U2E),4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod303Key.CT_U3C), 1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U3E),4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod303Key.CT_U4C), 1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U4E),4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod303Key.CT_U5C), 1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U5E),4))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   
			   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_U13)==1?'X':' ')
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C80),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C81),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C93),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C83),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C84),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C85),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C86),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C79),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C88),17,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C89),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C90),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C91),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C92),5,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 468))
			   ,(wr, mod) -> wr.append("</T30303000>")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_4T_REG_4 (mod303 -> (mod303.isAEAT() 
				&& ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))
				&& (AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_P1C))
				 || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_P2C))
				 || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_P3C))
				 || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_P4C))
				 || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_P5C))
				)) 
				,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30304000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P1C),3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P1I),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P1D),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P1T),1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( mod.getAmount( Mod303Key.CT_P1P),5,2))
			   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P2C),3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P2I),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P2D),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P2T),1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( mod.getAmount( Mod303Key.CT_P2P),5,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P3C),3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P3I),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P3D),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P3T),1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( mod.getAmount( Mod303Key.CT_P3P),5,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P4C),3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P4I),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P4D),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P4T),1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( mod.getAmount( Mod303Key.CT_P4P),5,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P5C),3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P5I),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_P5D),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text (mod.getDescription(Mod303Key.CT_P5T),1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( mod.getAmount( Mod303Key.CT_P5P),5,2))
		   
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 672))
		   ,(wr, mod) -> wr.append("</T30304000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_4T_REG_0_END (mod303 -> (mod303.isAEAT() && ((mod303.getYear() > 2017) || (mod303.getYear() == 2017 && mod303.isLastPeriod()))) ,new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})

		// **************************************************************** BIZKAIA		
		,BIZKAIA_2017_R01 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R01")			
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 25))
		   ,(wr, mod) -> wr.append("C")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getTown(), 15))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getDay(new Date()),2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(df.format(new Date()),10))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getYear(new Date()),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.BZ_C185_1)))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.BZ_C185_2)))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_2017_RA3 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_2017_R05 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("R05")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getName()
					   					+ AonStringUtils.SPACE
					   					+ AonStringUtils.trimToEmpty(mod.getSurname()),40))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 33))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 6))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPhone(), 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 3))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 12))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactEmail(), 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		
		,BIZKAIA_P00_2017 ( mod -> (mod.isBizkaia() && mod.getYear() >= 2017) ,new IPropertyFiller[] {
				
				(wr, mod) -> wr.append( mod.isComplementary() ? "P00TX0001"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")
			   ,(wr, mod) -> wr.append( mod.isEnrolledInDevolutionRegistry() ? "P00TX0002"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")			   
			   ,(wr, mod) -> wr.append("P00IM0003"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C003),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0004"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C004),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0005"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C005),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0006"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C006),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0007"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C007),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0008"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C008),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0009"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C009),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0010"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C010),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0011"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C011),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0012"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C012),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0013"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C013),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0014"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C014),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0015"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C015),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0016"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C016),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0017"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C017),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0018"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C018),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0019"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C019),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0020"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C020),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0021"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C021),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0022"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C022),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0023"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C023),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0024"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C024),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0025"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C025),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0026"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C026),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0027"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C027),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0028"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C028),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   // FALTA - [29] Regularización por aplicación porcentaje definitivo de prorrata (sólo en el mes 12)
			   // En el PDF que saca el programa de ayuda (tambien en la orden publicada), si que está esta casilla, pero en el documento disponible en la Web no lo está
			   // de cualquier forma en la plataforma de AON, aún no existe este campo
			   //,(wr, mod) -> wr.append("P00IM0029"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C029),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append("P00IM0030"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C030),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0031"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C031),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)			   
			   ,(wr, mod) -> wr.append("P00P30032"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getAmount(Mod303Key.BZ_C032))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)			   
			   ,(wr, mod) -> wr.append("P00IM0033"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C033),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0034"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C034),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0035"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C035),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0036"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C036),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append( mod.isWithoutActivity() ? "P00MR0037" + "X" + AonStringUtils.CR_LF : "" ) 
			   ,(wr, mod) -> wr.append("P00IM0038"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C038),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0039"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C039),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0040"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C040),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0041"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C041),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0042"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C042),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0043"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C043),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0045"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C045),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0046"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C046),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0047"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C047),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0050"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C050),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0051"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C051),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0052"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C052),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0053"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C053),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0054"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C054),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0055"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C055),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0056"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C056),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0057"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C057),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0058"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C058),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0059"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C059),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0060"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C060),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0061"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C061),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0062"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C062),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0063"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C063),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0064"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C064),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0065"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C065),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0066"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C066),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0067"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C067),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0068"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C068),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0069"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C069),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0070"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C070),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0071"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C071),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0072"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C072),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0073"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C073),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0074"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C074),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0075"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C075),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0076"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C076),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0077"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C077),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0078"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C078),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0079"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C079),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0080"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C080),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0081"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C081),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0082"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C082),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0083"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C083),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0084"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C084),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0085"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C085),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0086"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C086),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0087"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C087),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0088"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C088),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0089"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C089),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0090"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C090),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0091"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C091),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0092"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C092),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0093"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C093),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0094"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C094),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0095"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C095),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0096"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C096),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0097"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C097),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0098"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C098),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0099"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C099),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0100"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C100),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append( mod.getProratePercent() != 0.0 && mod.getProratePercent() != 100.0 ? "P00TX0101" + AonFiscalFileUtils.text("S",40) + AonStringUtils.CR_LF : "")  

			   // FALTA - [102] - Prorrata especial - Existe la clave, pero no aparece en el formulario en pantalla
			   //,(wr, mod) -> wr.append("P00TX0102"),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getProratePercent())),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0104"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C104),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0105"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C105),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0106"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C106),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0107"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C107),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0108"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C108),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append( mod.getDescription(Mod303Key.BZ_C185_1) == null && mod.getDescription(Mod303Key.BZ_C185_2) == null ? "" : "P00TX0185" + AonFiscalFileUtils.text("S",40) + AonStringUtils.CR_LF )
			   ,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C186) == 1 ? "P00MR0186" + "X" + AonStringUtils.CR_LF : "")
			   ,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C187) == 1 ? "P00MR0187" + "X" + AonStringUtils.CR_LF : "")
			   
			   // FALTA - No estan los campos
			   // [188] - Opción por la aplicación de la prorrata especial
			   //,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C188) == 1 ? "P00MR0188" + "X" + AonStringUtils.CR_LF : "")
			   // [189] - Revocación de la opción por la aplicación de la prorrata especial
			   //,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C189) == 1 ? "P00MR0189" + "X" + AonStringUtils.CR_LF : "")

			   ,(wr, mod) -> wr.append("P00IM0200"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C200),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0201"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C201),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0202"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C202),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0203"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C203),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})		
		
		// Dejo este solo para el 2016, pues hay cosas que han cambiado para el 2017
		,BIZKAIA_P00_2016 ( mod -> (mod.isBizkaia() && mod.getYear() == 2016) ,new IPropertyFiller[] {
		    (wr, mod) -> wr.append("P00TX0001"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isComplementary())),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces( 39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0002"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.CM_002))),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0003"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C003),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0004"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C004),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0005"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C005),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0006"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C006),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0007"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C007),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0008"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C008),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0009"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C009),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0010"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C010),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0011"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C011),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0012"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C012),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0013"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C013),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0014"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C014),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0015"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C015),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0016"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C016),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0017"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C017),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0018"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C018),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0019"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C019),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0020"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C020),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0021"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C021),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0022"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C022),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0023"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C023),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0024"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C024),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0025"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C025),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0026"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C026),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0027"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C027),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0028"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C028),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00IM0029"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C029),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0030"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C030),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0031"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C031),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getAmount(Mod303Key.BZ_C032))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0033"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C033),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0034"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C034),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0035"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C035),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0036"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C036),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0187"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.isWithoutActivity() )),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0038"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C038),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0039"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C039),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0040"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C040),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0041"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C041),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0042"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C042),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0043"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C043),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0045"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C045),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0046"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C046),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0047"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C047),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0050"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C050),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0051"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C051),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0052"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C052),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0053"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C053),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0054"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C054),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0055"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C055),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0056"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C056),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0057"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C057),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0058"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C058),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0059"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C059),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0060"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C060),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0061"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C061),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0062"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C062),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0063"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C063),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0064"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C064),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0065"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C065),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0066"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C066),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0067"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C067),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0068"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C068),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0069"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C069),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0070"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C070),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0071"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C071),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0072"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C072),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0073"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C073),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0074"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C074),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0075"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C075),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0076"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C076),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0077"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C077),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0078"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C078),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0079"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C079),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0080"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C080),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0081"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C081),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0082"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C082),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0083"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C083),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0084"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C084),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0085"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C085),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0086"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C086),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0087"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C087),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0088"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C088),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0089"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C089),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0090"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C090),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0091"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C091),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0092"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C092),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0093"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C093),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0094"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C094),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0095"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C095),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0096"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C096),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0097"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C097),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0098"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C098),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0099"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C099),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0100"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C100),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0101"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getProratePercent() != 0.0 && mod.getProratePercent() != 100.0)),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0102"),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getProratePercent())),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0104"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C104),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0105"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C105),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0106"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C106),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0107"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C107),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0108"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C108),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0185"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getDescription(Mod303Key.BZ_C185_1) == null && mod.getDescription(Mod303Key.BZ_C185_2) == null)),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0186"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C186))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0187"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C187))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00TX0188"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C188))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00TX0189"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C189))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0200"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C200),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0201"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C201),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0202"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C202),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0203"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C203),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		// **************************************************************** // 
		//                           A R A B A                              //
		// **************************************************************** //
		
		// ARABA - Registro Cabecera
		,ARABA_2016_RC ( mod -> (mod.isAraba() && mod.getYear() >= 2016) ,new IPropertyFiller[] {
				
			 // Habría que ver si aqui se pueden poner siempre los datos del contribuyente, o cuando
			 // la presentación la hace una asesoría o profesional, necesariamente deben ir los datos del profesional
			 // El programa de ayuda lo carga bien, pongas lo que pongas, así que por ahora, se ponen los mismo datos del contribuyente
			 (wr, mod) -> wr.append("C") // Tipo de registro = C
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social del profesional X(50)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))                 // Libre a blancos X(40)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1,6,0))            // Número de declaraciones presentadas 9(6) (siempre se presenta 1 declaración) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(1994))               // Libre a blancos X(1994)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		// ARABA - Registro Detalle
		,ARABA_2016_RD ( mod -> (mod.isAraba() && mod.getYear() >= 2016) ,new IPropertyFiller[] {
     		 (wr, mod) -> wr.append("D")                                           // Tipo de registro = D
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9) (El mismo que hayamos puesto en el registro de cabecera) 			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del declarante X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getStartMonth()+1,2,0)) // Período inicial (AAAAMM) 9(6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getDueMonth()+1,2,0))   // Período final (AAAAMM) 9(6)
			,(wr, mod) -> wr.append("303")                                         // Modelo de la declaración (303)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C080),15))  // Resultado de la declaración: Signo (blanco ó N) + Resultado 9(12) V99
			,(wr, mod) -> wr.append("E")                                           // Moneda (blanco ó E)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social X(50)
			
		    ,(wr, mod) -> {
		    	String streetName = (mod.getStreetInitial()+" "+mod.getStreetName()).trim();
				String streetNumber = mod.getStreetNumber();
				
				// Como el formato del numero debe ser 3 dígitos, si lleva letras o es mayor de 3 entonces se pone junto con la direccion
				if (streetNumber != null && (!AonStringUtils.isNumericSpace(streetNumber) || streetNumber.length() > 3)) {					
					streetName = streetName + ", " + streetNumber;
					streetNumber = "";
				}
				
				wr.append(AonFiscalFileUtils.text(streetName, 25));         // Domicilio - Texto calle Domicilio del declarante X(25)
				wr.append(AonFiscalFileUtils.unsigned(streetNumber, 3, 0)); // Domicilio - Número portal 9(3)		    
		    }
		    
			,(wr, mod) -> wr.append(AonStringUtils.SPACE)                               // Domicilio - Letra portal X(1)    
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetStair(),2))    // Domicilio - Escalera portal X(2) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetFloor(),2))    // Domicilio - Piso X(2) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetDoor(),3))     // Domicilio - Mano X(3) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(25))                      // Texto entidad X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getTown(), 25))         // Texto municipio X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getProvince(), 25))		// Texto provincia X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getZip(), 5, 0))	// Código postal 9(5)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPhone(),9,0))    // Teléfono del declarante 9(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(25))                      // Anagrama X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(17)) 						// Clave de actividad: 1 (Modelo 310)  X(17)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(17)) 						// Clave de actividad: 11 (Modelo 310) X(17)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(16)) 						// Clave de actividad: 21 (Modelo 310) X(16)
			
			// CLAVES-IMPORTES declaración (95 ocurrencias.) - Las claves posibles del modelo 303
			// Todas llevan el importe con signo (blanco o N) + importe (12 enteros y 2 decimales), 
			// excepto determinadas claves que solo usan la parte entera por que llevan 5 decimales 
			// o por que son casilla Si/No
			
			,(wr, mod) -> wr.append("090" + AonFiscalFileUtils.signedSpace((mod.isWithoutActivity()?1.0:0.0),15)) // [90] Sin actividad
			,(wr, mod) -> wr.append("091" + AonFiscalFileUtils.signedSpace((mod.isReplacement()?1.0:0.0),15))     // [91] Sustitutiva
			
			// En el numero anterior hay que poner año + numero anterior, se supone que en el programa 
			// ya se introducira el año primero y despues el numero, todo junto			
			,(wr, mod) -> wr.append(mod.isReplacement() ? "902"+AonStringUtils.SPACE+AonFiscalFileUtils.unsigned(mod.getReplacedNumber(),14,2) : "000"+AonStringUtils.SPACE+AonFiscalFileUtils.zeros(14)) // [902] Sustitutiva Numero declaracion anterior (año y numero) - solo si se ha presentado por internet
			
			,(wr, mod) -> wr.append("907" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C907),15))  // [907] Ha sido declarado en concurso de acreedores en el presente período de liquidación
			,(wr, mod) -> wr.append("092" + AonFiscalFileUtils.signedSpace((mod.isEnrolledInDevolutionRegistry()?1.0:0.0),15)) // [92] Está inscrito en el Registro de devolución mensual
			,(wr, mod) -> wr.append("910" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C910),15))  // [910] Ha optado por el Régimen especial del criterio de caja 
			,(wr, mod) -> wr.append("911" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C911),15))  // [911] Es destinatario de operaciones a las que se aplique el Régimen especial del criterio de caja
			// La casilla 908 va al final en las posiciones 2011 a 2018
			,(wr, mod) -> wr.append("909" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C909),15))  // [909] Si se ha dictado auto de declaración de concurso en este periodo indique el tipo de autoliquidación indicar si es Preconcursal o Postconcursal
			
			// IVA Devengado
			,(wr, mod) -> wr.append("001" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C001),15))  
			,(wr, mod) -> wr.append("002" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C002),15))
			,(wr, mod) -> wr.append("003" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C003),15))
			,(wr, mod) -> wr.append("204" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C204),15))
			,(wr, mod) -> wr.append("205" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C205),15))
			,(wr, mod) -> wr.append("206" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C206),15))
			,(wr, mod) -> wr.append("207" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C207),15))
			,(wr, mod) -> wr.append("208" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C208),15))
			,(wr, mod) -> wr.append("209" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C209),15))
			,(wr, mod) -> wr.append("370" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C370),15))
			,(wr, mod) -> wr.append("371" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C371),15))
			,(wr, mod) -> wr.append("372" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C372),15))
			,(wr, mod) -> wr.append("373" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C373),15))
			,(wr, mod) -> wr.append("010" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C010),15))
			,(wr, mod) -> wr.append("011" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C011),15))
			,(wr, mod) -> wr.append("012" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C012),15))
			,(wr, mod) -> wr.append("213" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C213),15))
			,(wr, mod) -> wr.append("214" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C214),15))
			,(wr, mod) -> wr.append("215" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C215),15))
			,(wr, mod) -> wr.append("216" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C216),15))
			,(wr, mod) -> wr.append("217" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C217),15))
			,(wr, mod) -> wr.append("218" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C218),15))
			,(wr, mod) -> wr.append("374" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C374),15))
			,(wr, mod) -> wr.append("375" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C375),15))
			,(wr, mod) -> wr.append("019" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C019),15))
			,(wr, mod) -> wr.append("020" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C020),15))
			,(wr, mod) -> wr.append("021" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C021),15))
			,(wr, mod) -> wr.append("222" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C222),15))
			,(wr, mod) -> wr.append("223" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C223),15))
			,(wr, mod) -> wr.append("224" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C224),15))
			,(wr, mod) -> wr.append("225" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C225),15))
			,(wr, mod) -> wr.append("226" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C226),15))
			,(wr, mod) -> wr.append("227" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C227),15))
			,(wr, mod) -> wr.append("376" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C376),15))
			,(wr, mod) -> wr.append("377" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C377),15))
			,(wr, mod) -> wr.append("028" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C028),15))
			
			// Deducciones
			,(wr, mod) -> wr.append("030" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C030),15))
			,(wr, mod) -> wr.append("031" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C031),15))
			,(wr, mod) -> wr.append("032" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C032),15))
			,(wr, mod) -> wr.append("033" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C033),15))
			,(wr, mod) -> wr.append("034" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C034),15))
			,(wr, mod) -> wr.append("035" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C035),15))
			,(wr, mod) -> wr.append("036" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C036),15))
			,(wr, mod) -> wr.append("037" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C037),15))
			
			// FALTA - Casilla [46] no existe en el modelo
			,(wr, mod) -> wr.append("046" + AonFiscalFileUtils.signedSpace(0.0,15))
			
			,(wr, mod) -> wr.append("038" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C038),15))
			
			// Diferencia
			,(wr, mod) -> wr.append("039" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C039),15))
			
			// Volumen de Operaciones, va con 5 decimales. Se pone todo el numero (incluido los decimales) en la parte entera
			,(wr, mod) -> wr.append("040" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C040),13,5)+"00" )
			,(wr, mod) -> wr.append("041" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C041),13,5)+"00" )
			,(wr, mod) -> wr.append("042" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C042),13,5)+"00" )
			,(wr, mod) -> wr.append("043" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C043),13,5)+"00" )
			
			// Cuotas atribuible / cuotas a compensar / Resultado autoliquidacion / recargos / total
			,(wr, mod) -> wr.append("044" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C044),15))
			,(wr, mod) -> wr.append("045" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C045),15))
			,(wr, mod) -> wr.append("060" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C060),15))
			,(wr, mod) -> wr.append("061" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C061),15))
			,(wr, mod) -> wr.append("062" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C062),15))
			,(wr, mod) -> wr.append("063" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C063),15))
			,(wr, mod) -> wr.append("080" + AonFiscalFileUtils.signedSpace((mod.getAmount(Mod303Key.AR_C080)>=0?mod.getAmount(Mod303Key.AR_C080):0.0),15))
			,(wr, mod) -> wr.append("081" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C081),15))
			,(wr, mod) -> wr.append("082" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C082),15))
			
			// Informacion adicional
			,(wr, mod) -> wr.append("050" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C050),15))
			,(wr, mod) -> wr.append("051" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C051),15))
			,(wr, mod) -> wr.append("052" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C052),15))
			,(wr, mod) -> wr.append("180" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C180),15))
			,(wr, mod) -> wr.append("181" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C181),15))
			,(wr, mod) -> wr.append("182" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C182),15))
			,(wr, mod) -> wr.append("183" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C183),15))
			
			// Datos bancarios (CCC), Solo si es devolucion o es domiciliacion y está cumplimentada la cuenta bancaria			
			,(wr, mod) -> {
				if ((mod.getDeclarationType() == FiscalModelDeclarationType.BANK || mod.getDeclarationType() == FiscalModelDeclarationType.PAYBACK) &&
					(mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20))
					{
						wr.append("301" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 0, 4),14,2));
						wr.append("302" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 4, 8),14,2));
						wr.append("303" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 8,10),14,2));
						wr.append("304" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring(10,20),14,2));						
					}
				else					
				{
					for (int i=1;i<=4;i++)
						wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,15));				
				}
			}
			
			// Resto de casillas hasta completar las 95 (total 16, ya que hasta ahora van 79)			
			,(wr, mod) -> {
				for (int i=1;i<=16;i++)
					wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,15));
			}
			
			// Fecha en la que se dicto el concurso de acreedores
			,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateZero(mod.getDescription(Mod303Key.AR_C908)))			
			
			// Libre a blancos X(82) y fin de registro
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(82))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			
	    })
		
		// **************************************************************** GIPUZKOA		
		,GIPUZKOA_2015_R01 ( mod -> (mod.isGipuzkoa() && mod.getYear() >= 2015) ,new IPropertyFiller[] {
			
// 			Los campos de Importes con signo, serán: Signo (0 o -) + X enteros + 2 decimales, excepto
//			el Porcentaje de Gipuzkoa, que se compone de 3 posiciones enteras y 4 posiciones decimales			

			 (wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      // Nif presentador AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      // Nif declarante AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))   // Ejercicio N4
			,(wr, mod) -> wr.append(mod.getPeriod().isQuarterPeriod() ? "300" : "320") // Modelo 300 (si presentacion trimestral) o 320 (si presentacion mensual) AN3
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2)) // Periodo AN2
			
			// FALTA - Que hay que poner ?? en los que genera el programa de ayuda, y en los que genera hasta ahora AON, siempre pone 01
			,(wr, mod) -> wr.append("01") // Código de registro N2
			
			,(wr, mod) -> wr.append((mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20)?AonFiscalFileUtils.text(mod.getFinanceCCC(),20):AonFiscalFileUtils.zeros(20)) // Código cuenta cliente N20
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))	                // Cuatro primeras posiciones del IBAN (ESXX) AN4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(3)) 							            // Libre blancos AN3 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isWithoutActivity()))                   // [01] Sin actividad AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A001)))          // [PRE] Autoliquidacion Concursal PRE AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A002)))          // [POST] Autoliquidacion Concursal POST AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C002),15))     //	[02] Base al 21 Signo+N14 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C003),14))     //	[03] Cuota al 21 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C004),15))     // [04] Base al 10 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C005),14))     // [05] Cuota al 10 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C006),15))     // [06] Base al 4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C007),14))     // [07] Cuota al 4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C039),15))     // [39] Modificación de base régimen general Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C040),14))     // [40] Modificación de cuota régimen general Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C008),15))     // [08] Base recargo al 5,2 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C009),14))     // [09] Cuota recargo al 5,2 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C010),15))     // [10] Base recargo al 1,4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C011),14))     // [11] Cuota recargo al 1,4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C012),15))     // [12] Base recargo al 0,5 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C013),14))     // [13] Cuota recargo al 0,5 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C041),15))     // [41] Modificación de base recargo equivalencia Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C042),14))     // [42] Modificación de cuota recargo equivalencia Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C014),15))     // [14] Base adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C015),14))     // [15] Cuota adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C043),15))     // [43] Base otras operaciones inversión sujeto pasivo (compras) Signo+N14	
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C044),14))     // [44] Cuota otras operaciones inversión sujeto pasivo (compras) Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C016),15))     // [16] Total cuota devengada Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C017),15))     // [17] Base Iva deducible operaciones interiores Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C018),14))     // [18] Cuota Iva deducible operaciones interiores Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C019),15))     // [19] Base Iva deducible importaciones Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C020),14))     // [20] Cuota Iva deducible importaciones Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C021),15))     // [21] Base Iva deducible adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C022),14))     // [22] Cuota Iva deducible adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C045),15))     // [45] Rectificación de deducciones. Base. Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C046),14))     // [46] Rectificación de deducciones. Cuota. Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C023),14))     // [23] Cuota compensaciones Reg A.G. y P. Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C024),14))     // [24] Cuota Iva deducible Regularización Inversiones Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C025),14))     // [25] Cuota total a deducir Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C026),14))     // [26] Diferencia Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C027),7,4))  // [27] Porcentaje Gipuzkoa N7 (3 enteros + 4 decimales)
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C028),12))     // [28] Atribuible T. H. Gipuzkoa Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C029),11))   // [29] Cuotas a compensar de periodos anteriores en T.H. Gipuzkoa N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C030),15))     // [30] Entregas intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C031),15))     // [31] Exportaciones y operaciones asimiladas Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C032),15))     // [32] Op. No sujetas o con inversión del sujeto pasivo Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C047),15))     // [47] Base imponible Entregas criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C048),14))     // [48] Cuota Entregas criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C049),15))     // [49] Base imponible Adquisiciones criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C050),14))     // [50] Cuota Adquisiciones criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C035),12))     // [35] Resultado Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)>=0?mod.getAmount(Mod303Key.GP_C035):0.0,11))  // [36] A ingresar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.COMPENSATE?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))  // [37] A compensar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.PAYBACK?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))     // [38] A devolver N11
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})				
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File2016(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303);
		}
		private void fillPage(Mod303 mod303, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod303);
			}
		}
		private static String appendMark(Mod303 mod, Mod303Key epigrphKey, Mod303Key markKey, Mod303Key modulFactorKey , double factor1, double factor2) {
			   if ("722".equals(mod.getDescription(epigrphKey))) {
				   if (AonNumberUtils.equals( mod.getAmount(markKey) , 1)) return "1";
				   if (AonNumberUtils.equals( mod.getAmount(markKey) , 2)) return "2";
				   if (AonNumberUtils.equals( mod.getAmount(modulFactorKey), factor1)) return "2";
				   return "1";
			   }
			   if ("691.9".equals(mod.getDescription(epigrphKey))) {
				   if (AonNumberUtils.equals( mod.getAmount(markKey) , 1)) return "1";
				   if (AonNumberUtils.equals( mod.getAmount(markKey) , 2)) return "2";
				   if (AonNumberUtils.equals( mod.getAmount(modulFactorKey), factor2)) return "2";
				   return "1";
			   } 
			   return " ";   
		}
	}

	public static void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File2016 format : Mod303File2016.values()) {
			if (format.accept(mod303)) {
				format.fillPage(mod303, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
