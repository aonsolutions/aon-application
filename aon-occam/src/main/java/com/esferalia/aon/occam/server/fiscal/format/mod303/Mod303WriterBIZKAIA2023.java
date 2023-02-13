package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303WriterBIZKAIA2023 implements IMod303Writer{

	private static SimpleDateFormat df = new SimpleDateFormat("MMMMM");
	
	private static enum Mod303File {
		 BIZKAIA_2017_R01 ( mod -> true ,new IPropertyFiller[] {
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
		,BIZKAIA_2017_RA3 ( mod -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_2017_R05 ( mod -> true ,new IPropertyFiller[] {
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
		
		,BIZKAIA_P00_2017 ( mod -> true ,new IPropertyFiller[] {
				
				(wr, mod) -> wr.append( mod.isComplementary() ? "P00TX0001"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")
			   ,(wr, mod) -> wr.append( mod.isEnrolledInDevolutionRegistry() ? "P00TX0002"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")
			   
			   ,(wr, mod) -> wr.append("P00IM0048"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C048),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0049"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C049),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
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
			   
			   ,(wr, mod) -> wr.append("P00IM0110"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C110),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0111"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C111),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0112"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C112),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0113"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C113),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0114"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C114),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0115"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C115),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0116"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C116),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0117"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C117),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append( mod.getProratePercent() != 0.0 && mod.getProratePercent() != 100.0 ? "P00TX0101" + AonFiscalFileUtils.text("S",40) + AonStringUtils.CR_LF : "")  

			   // FALTA - [102] - Prorrata especial - Existe la clave, pero no aparece en el formulario en pantalla
			   //,(wr, mod) -> wr.append("P00TX0102"),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getProratePercent())),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0104"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C104),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0105"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C105),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0106"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C106),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0107"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C107),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0108"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C108),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0109"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C109),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
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
		,BIZKAIA_P00_2016 ( mod -> true ,new IPropertyFiller[] {
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
