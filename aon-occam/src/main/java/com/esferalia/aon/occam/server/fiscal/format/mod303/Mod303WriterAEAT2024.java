package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303WriterAEAT2024 implements IMod303Writer{

	private enum Mod303File {
		T303_START (mod303 -> true ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text("2021", 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		})
		
		,T30301000 (mod303 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30301000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationResultType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),80))  // Apellidos y Nombre o Razón Social
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))	
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A12),1,0 ))  // Tributacion exclusivamente foral
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CM_002)==1?"1":"2")  // Inscrito en registro de devolución mensual
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getAmount(Mod303Key.CT_A02)+1),1))  // tributa en regimen simplificado		   
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A03)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A07)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A08)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A09)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A10)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A04)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateES(mod.getDescription(Mod303Key.CT_A05)))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A06)==0?" ": AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_A06),1) )
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A13),1,0 ))
		   ,(wr, mod) -> wr.append(!mod.isLastPeriod()?"0":AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A14),1,0 ))
		   ,(wr, mod) -> wr.append(!mod.isLastPeriod()?"0":AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_A11),1,0 ))		   
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C150),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C151), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C152),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C02), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C153),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C154), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C155),17,2))
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
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C156),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C157), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C158),17,2))
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
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 600))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T30301000>")
		})
		
		,T30302001 (mod303 -> mod303.getAmount(Mod303Key.CT_A02) < 2 ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30302000>")
		   ,(wr, mod) -> wr.append(" ")
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA11))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA12) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA13) / 10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA14) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA15) ,5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA16) ,17,2))
		   // ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA17) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA1A) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA18) ,17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA21))  ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA22)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA23) / 10000 ,6 ,5))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA24)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA25)  ,5 ,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA26)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA2A),17,2))
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
		   
//		   90	1105	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - ACTIVIDAD DE TEMPORADA: nº de días en los que se ejerció la actividad en el año anterior - 1T/2T/3T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1X1) ,3,0))
//		   91	1108	2	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Número de días de ejercicio de la actividad en el trimestre - 1T/2T/3T		2 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1X2) ,2,0))
//		   92	1110	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Nº de empleados a uno de enero del ejercicio (o en la fecha de inicio de la actividad) - 1T/2T/3T		3 enteros
//	,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1X3) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0.0,3,0))
//		   93	1113	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Actividad de temporada. Número de días de ejercicio - 4T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1Y1) ,3,0))
//		   94	1116	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Nº máximo de asalariados que han trabajado simultáneamente durante el ejercicio - 4T		3 enteros
// ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1Y2) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0.0,3,0))
//		   95	1119	1	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Si en el ejercicio realiza la actividad en LORCA, seleccione lo que proceda		Nota 5
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1X4) ,1,0))
//		   96	1120	17	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Cuotas soportadas - 4T		15 enteros y 2 decimales
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S12Y)  ,17,2))
//		   97	1137	17	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. - 4T		15 enteros y 2 decimales
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S13Y)  ,17,2))
//		   98	1154	7	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal Asalariado - Horas anuales - Mayores de 19 años		7 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1P1) ,7,0))
//		   99	1161	7	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal Asalariado - Horas anuales - Menores de 19 años y trabajadores con contratos de aprendizaje o formación que no sean discapacitados		7 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1P2) ,7,0))
//		   100	1168	7	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal Asalariado - Horas anuales - Discapacitados con grado de minusvalía igual o superior al 33 por 100		7 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1P3) ,7,0))
//		   101	1175	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal Asalariado - Horas anuales - Horas anuales fijadas en el convenio colectivo vigente		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1P4) ,4,0))
//		   102	1179	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: titular		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1E1) ,4,0))
//		   103	1183	1	An	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal No Asalariado - El titular es discapacitado en grado igual o superior al 33 por 100.		X o blanco
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_S1E2)==1?"X":" ",1))  // Declaración complementaria
//		   104	1184	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: cónyuge		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1E3) ,4,0))
//		   105	1188	7	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: hijos menores de 18 años		7 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1E4) ,7,0))
//		   106	1195	2	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Capacidad		2 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1C1) ,2,0))
//		   107	1197	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Mesas		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1M1) ,4,0))
//		   108	1201	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Días - 4T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1D1) ,3,0))
//		   109	1204	2	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Capacidad		2 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1C2) ,2,0))
//		   110	1206	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Mesas		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1M2) ,4,0))
//		   111	1210	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Días - 4T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1D2) ,3,0))
//		   112	1213	2	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Capacidad		2 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1C3) ,2,0))
//		   113	1215	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Mesas		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1M3) ,4,0))
//		   114	1219	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Días - 4T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1D3) ,3,0))
//		   115	1222	2	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Capacidad		2 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1C4) ,2,0))
//		   116	1224	4	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Mesas		4 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S1M4) ,4,0))
//		   117	1228	3	Num	C	Liquidación (3) - RS - (B) Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Mesas - Días - 4T		3 enteros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S1D4) ,3,0))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2X1) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2X2) ,2,0))
//		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2X3) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0.0,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2Y1) ,3,0))
//		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2Y2) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0.0 ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2X4) ,1,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S22Y)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S23Y)  ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2P1) ,7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2P2) ,7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2P3) ,7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2P4) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2E1) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_S2E2)==1?"X":" ",1))  // Declaración complementaria
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2E3) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2E4) ,7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2C1) ,2,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2M1) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2D1) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2C2) ,2,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2M2) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2D2) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2C3) ,2,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2M3) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2D3) ,3,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2C4) ,2,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S2M4) ,4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S2D4) ,3,0))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA17) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA1B) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA27) ,17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA2B) ,17,2))

		   
		   ,(wr, mod) -> wr.append("000") // Actividades en RS (exc. a, g y f) - Actividad 1 - Módulo Superficie de horno - Días - 4T' Número línea 2
		   ,(wr, mod) -> wr.append("000") // Actividades en RS (exc. a, g y f) - Actividad 2 - Módulo Superficie de horno - Días - 4T' Número línea 2
		   
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 264))
		   ,(wr, mod) -> wr.append("</T30302000>")
		})
		
		,T30302002 (mod303 -> mod303.getAmount(Mod303Key.CT_A02) < 2
				&& ( AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_SA31))
				  || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_SA41))
				  || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_S301))
				  || AonStringUtils.isNotBlank(mod303.getDescription(Mod303Key.CT_S401)))
				,new IPropertyFiller[] {
				(wr, mod) -> wr.append("<T30302000>")
			   ,(wr, mod) -> wr.append("C")
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA31))  ,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA32) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA33) / 10000 ,6 ,5))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA34) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA35) ,5 ,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA36) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA3A) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA38) ,17,2))
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned( AonNumberUtils.toint(mod.getDescription(Mod303Key.CT_SA41))  ,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA42)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA43) / 10000 ,6 ,5))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA44)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA45)  ,5 ,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_SA46)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA4A),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_SA48),17,2))

			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S301), '.')  ,4))
			   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S301, Mod303Key.CT_S302, Mod303Key.CT_S32F, 181.58, 13.23))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S31I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S31R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S32I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S32R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S33I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S33R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S34I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S34R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S35I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S35R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S36I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S36R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S37I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S37R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S317)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S318)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S319)  , 3,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S320)  , 5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S321)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S322)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S323)  , 3,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S324)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S325)  , 5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S326)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S327)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S328)  ,17,2))
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( AonStringUtils.remove(mod.getDescription(Mod303Key.CT_S401), '.')  ,4))
			   ,(wr, mod) -> wr.append(appendMark(mod, Mod303Key.CT_S401, Mod303Key.CT_S402, Mod303Key.CT_S42F, 181.58, 13.23))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S41I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S41R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S42I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S42R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S43I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S43R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S44I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S44R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S45I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S45R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S46I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S46R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S47I)  ,10,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S47R)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S417)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S418)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S419)  , 3,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S420)  , 5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S421)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S422)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S423)  , 3,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S424)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S425)  , 5,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S426)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(  mod.getAmount(Mod303Key.CT_S427)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_S428)  ,17,2))

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

			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3X1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3X2) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3X3) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3Y1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3Y2) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3X4) ,1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S32Y)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S33Y)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3P1) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3P2) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3P3) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3P4) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3E1) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_S3E2)==1?"X":" ",1))  // Declaración complementaria
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3E3) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3E4) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3C1) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3M1) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3D1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3C2) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3M2) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3D2) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3C3) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3M3) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3D3) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3C4) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S3M4) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S3D4) ,3,0))


			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4X1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4X2) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4X3) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4Y1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4Y2) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4X4) ,1,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S42Y)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S43Y)  ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4P1) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4P2) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4P3) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4P4) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4E1) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getAmount(Mod303Key.CT_S4E2)==1?"X":" ",1))  // Declaración complementaria
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4E3) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4E4) ,7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4C1) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4M1) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4D1) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4C2) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4M2) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4D2) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4C3) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4M3) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4D3) ,3,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4C4) ,2,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_S4M4) ,4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S4D4) ,3,0))
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA37) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA3B) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA47) ,17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(!mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_SA4B) ,17,2))
			   
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 270))
			   ,(wr, mod) -> wr.append("</T30302000>")
		})
		
		,T30303000 (mod303 -> true ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("<T30303000>")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C59),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C60),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C120),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C122),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C123),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C124),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C62),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C63),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C74),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C75),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C76),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C64),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C65),5,2))			      
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C66),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C77),17,2))			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C110),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C78),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C87),17,2))  
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C68),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C69),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C70),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C109),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C71),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isWithoutActivity()?"X":" ",1))  // Sin actividad
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))  // Declaración complementaria
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   ?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
							   :AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 35))			// Reservado para la AEAT
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 86))			// Reservado para la AEAT
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 479))			// Reservado para la AEAT
			   ,(wr, mod) -> wr.append("</T30303000>")
		})
		
		,T30304000 (IFiscalModel::isLastPeriod ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30304000>")
		   ,(wr, mod) -> wr.append(" ")
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U1C), 3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U1E),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U2C), 3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U2E),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U3C), 3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U3E),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U4C), 3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U4E),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U5C), 3))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod303Key.CT_U5E),4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 3	))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
		   
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_U13)==1?'X':' ')
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C89),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C90),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C91),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C92),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C107),5,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C80),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C81),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C93),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C94),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C83),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C84),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C125),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C126),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C127),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C128),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C86),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C95),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C96),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C97),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C98),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C79),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C99),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C88),17,2))
		   
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ',600))  // Reservado para la AEAT
		   ,(wr, mod) -> wr.append("</T30304000>")
		})
		
		,T30305000 (IFiscalModel::isLastPeriod ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30305000>")
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
		   ,(wr, mod) -> wr.append("</T30305000>")
		})		
		,T303DID00 (mod303 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T303DID00>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 11))                   	// Devolución - SWIFT-BIC
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getFinanceIban(mod),34)) 	// Domiciliacion/Devolucion - IBAN
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))          		 	// Devolución - Banco/Bank name
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 35))					 	// Devolución - Dirección del Banco/ Bank address
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 30))					 	// Devolución - Ciudad/City
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))					 	// Devolución - Código País/Country code
		   ,(wr, mod) -> wr.append(														// Devolución - Marca SEPA
				   mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK?"1":"0")				
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 617))						// Reservado	
		   ,(wr, mod) -> wr.append("</T303DID00>")
		})
		// >
		
		,T303_END (mod303 -> true ,new IPropertyFiller[] { 
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
		private static String getFinanceIban(Mod303 mod) {
			// No pongo el IBAN cuando es aplazamiento, porque si no, no valida la declaración al sacar el borrador
			if (mod.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT || mod.getDeclarationResultType() == FiscalModelDeclarationType.DEFERRAL) {
				return null;
			}
			return mod.getFinanceIban();
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
			throw new AonCoreException("La generaci\u00F3n del modelo no est\u00E1 soportada.");
		}
	}
	
}
