package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303WriterAEAT2019 implements IMod303Writer{

	private static enum Mod303File {
		AEAT_2017_4T_REG_0_START (mod303 -> true ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text("2020", 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		})
		,AEAT_2017_4T_REG_1 (mod303 -> true ,new IPropertyFiller[] {
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
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateES(mod.getDescription(Mod303Key.CT_A05)))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A06)==0?" ": AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_A06),1) )
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
		   ,(wr, mod) -> wr.append(!mod.isLastPeriod()?"0":AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A11),1,0 ))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A12),1,0 ))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A13),1,0 ))
		   ,(wr, mod) -> wr.append(!mod.isLastPeriod()?"0":AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A14),1,0 ))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 578))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T30301000>")
		})
		,AEAT_2017_4T_REG_2 (mod303 -> mod303.getAmount(Mod303Key.CT_A02) < 2 ,new IPropertyFiller[] {
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
		})
		,AEAT_2017_4T_REG_3 (mod303 -> true ,new IPropertyFiller[] {
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
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 4))   
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
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',1))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C89),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C90),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C91),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C92),5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C94),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C95),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C96),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C97),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C98),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C99),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C107),5,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 463))
			   ,(wr, mod) -> wr.append("</T30303000>")
		})
		,AEAT_2017_4T_REG_4 (mod303 -> mod303.isLastPeriod() ,new IPropertyFiller[] {
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
		})
		,AEAT_2017_4T_REG_0_END (mod303 -> (mod303.isAEAT() && mod303.getYear() >= 2017) ,new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File(IModelAccepter accepter,IPropertyFiller[] pf) {
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

	public void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File format : Mod303File.values()) {
			if (format.accept(mod303)) {
				format.fillPage(mod303, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
