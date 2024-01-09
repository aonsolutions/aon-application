package com.esferalia.aon.occam.server.fiscal.format.mod190;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod190.Mod190Writer.IPropertyFiller;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

enum Mod190File2023Gipuzkoa {

	TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(
				   det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL() + det.getInKindPerceptionIL())
				   ).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( 
				   det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL() + det.getInKindDepositIL()) 
				   ).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactMail(),50))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 262)) 
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})

	,TYPE_2 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("2")
			   ,(wr, mod190,detail) -> wr.append("190")
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getDocument(),9))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getRepresentativeDocument(),9))	 									// APELLIDOS Y NOMBRE, RAZÓN DENOMINACIÓN DEL PERCEPTOR
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.changeInvalidCharacters(detail.getName()),40))	 			
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getProvince(),2,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getKey(),1))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text((AonStringUtils.isBlank(detail.getSubKey())?"00":detail.getSubKey()),2))	
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerception()),14,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetention()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getInKindPerception()),14,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindDeposit()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindOutputDeposit()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAccrualYear(), 4,0))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 6))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getSpouseDocument(), 9))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned( detail.getTitConvivencia() ,1,0))
			   ,(wr, mod190,detail) -> wr.append("0")
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 17))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 26))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getInKindPerceptionIL()),14,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindDepositIL()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindOutputDepositIL()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned( getComplementoInfancia(detail),1,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCommonRetention()),13,2)) // HACIENDA ESTATAL
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getNavarraRetention()),13,2)) // COMUNIDAD FORAL DE NAVARRA.
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getArabaRetention()),13,2)) // DIPUTACIÓN FORAL DE ARABA/ÁLAVA.
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getGipuzkoaRetention()),13,2)) // DIPUTACIÓN FORAL DE GIPUZKOA.
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getBizkaiaRetention()),13,2)) // DIPUTACIÓN FORAL DE BIZKAIA.
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 113))
		
			   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
	;

	private IPropertyFiller[] propertyFillers;

	private Mod190File2023Gipuzkoa(IPropertyFiller[] pf) {
		this.propertyFillers = pf;
	}

	private static byte getComplementoInfancia(Mod190Detail detail) {
		if (detail != null 
			&& AonStringUtils.equals("L", detail.getKey()) 
			&& AonStringUtils.equals("29", detail.getSubKey())) 
			return detail.getCompInfancia();
		return 0;
	}
	
	private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
		for (IPropertyFiller propertyFiller : this.propertyFillers) {
			propertyFiller.propertyFill(wr, mod190, detail);
		}
	}
	static void fill(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2023Gipuzkoa.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2023Gipuzkoa.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

}
