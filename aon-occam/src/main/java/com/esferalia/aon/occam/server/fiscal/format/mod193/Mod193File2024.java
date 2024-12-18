package com.esferalia.aon.occam.server.fiscal.format.mod193;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod193.Mod193Writer.IPropertyFiller;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

enum Mod193File2024 {

	TYPE_1 (new IPropertyFiller[] { 
		(wr, mod193,detail) -> wr.append("1")
	   ,(wr, mod193,detail) -> wr.append("193")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod193.getYear(), 4,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(mod193.getDocument(),9))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(mod193.getName(),40))
	   ,(wr, mod193,detail) -> wr.append("T")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(mod193.getContactPhone(),9))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(mod193.getContactPerson(),40))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod193.getReceipt(),13,0))
	   ,(wr, mod193,detail) -> wr.append(mod193.isComplementary()?"C":" ")
	   ,(wr, mod193,detail) -> wr.append(mod193.isReplacement()?"S":" ")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod193.getReplacedReceipt(),13,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod193.getDetails().size(),9,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(
			   mod193.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det .getRetentionBase()))
			   .sum(),15,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(
			   mod193.getDetails()
			   .stream()
			   .mapToDouble( det -> AonMathUtils.round(det.getRetention()))
			   .sum(),15,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(
			   mod193.getDetails()
			   .stream()
			   .filter( det -> "C".equals(det.getKey()) || det.getPayment() == 1 || det.getPayment() == 3)
			   .mapToDouble( det -> AonMathUtils.round(det.getRetention()))
			   .sum(),15,2))
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 30))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(
			   mod193.getExpenses()
			   .stream()
			   .mapToDouble( exp -> AonMathUtils.round(exp.getExpenses()))
			   .sum(),15,2))
	   ,(wr, mod193,detail) -> wr.append(" ")
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 252))		
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 13))
	   ,(wr, mod193,detail) -> wr.append("\r\n")
	})
	
	,TYPE_2 (new IPropertyFiller[] { 
		(wr, mod193,detail) -> wr.append("2")
	   ,(wr, mod193,detail) -> wr.append("193")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod193.getYear(), 4,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(mod193.getDocument(),9))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getDocument(),9))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getRepresentativeDocument(),9))	 									
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.changeInvalidCharacters(detail.getName()),40))
	   ,(wr, mod193,detail) -> wr.append(detail.isIntermediaryPayment()?"X":" ") 
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getProvince(),2,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getKeyCode(),1))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getIssuingCode(),12))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getKey(),1))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getNature(),2,0))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getPayment(),1))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getCodeType(),1))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getAccountCode(),20))
	   ,(wr, mod193,detail) -> wr.append(detail.isPending()?"X":" ")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAccrualYear(), 4,0))
	   ,(wr, mod193,detail) -> wr.append(detail.isInKind()?"2":"1")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getLenderAmount()),13,2))
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 3))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getReduction()),13,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionBase()),13,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getPercent()),4,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetention()),13,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getPenalization()),11,2))
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 15))
	   ,(wr, mod193,detail) -> wr.append(detail.isDeclarantNature()?"S":" ")
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.dateZero(detail.getLoanStartDate()))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.dateZero(detail.getLoanDueDate()))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensation()),12,2))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getGuarantee()),12,2))
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCommonRetention()),13,2)) // HACIENDA ESTATAL
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getNavarraRetention()),13,2)) // COMUNIDAD FORAL DE NAVARRA.
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getArabaRetention()),13,2)) // DIPUTACIÓN FORAL DE ARABA/ÁLAVA.
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getGipuzkoaRetention()),13,2)) // DIPUTACIÓN FORAL DE GIPUZKOA.
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getBizkaiaRetention()),13,2)) // DIPUTACIÓN FORAL DE BIZKAIA.
	   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getCeutaMelillaPalma(),1))
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.unsigned(getOrderNumber(),7,0))            // Número de orden: A cada registro del perceptor se le asignará de forma secuencial un número de orden.
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getPreviousPayerDocument(),9)) // NIF del pagador anterior
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.dateZeroES(detail.getAccrualDate()))       // Fecha de devengo
	   ,(wr, mod193,detail) -> wr.append(AonFiscalFileUtils.text(detail.getMarketKey(),1))             // Clave de Mercado
	   ,(wr, mod193,detail) -> wr.append(AonStringUtils.repeat(' ', 161))		
	   ,(wr, mod193,detail) -> wr.append("\r\n")
	})
	;

	private static int orderNumber;
	private IPropertyFiller[] propertyFillers;

	private Mod193File2024(IPropertyFiller[] pf) {
		this.propertyFillers = pf;
	}

	private static int getOrderNumber() {
		return ++orderNumber;
	}

	private void fillPage(Mod193 mod193, Mod193Detail detail, Writer wr) throws IOException {
		for (IPropertyFiller propertyFiller : this.propertyFillers) {
			propertyFiller.propertyFill(wr, mod193, detail);
		}
	}

	static void fill(Mod193 mod193, Writer wr) throws IOException {
		orderNumber = 0;
		Mod193File2024.TYPE_1.fillPage(mod193, null, wr);
		for (Mod193Detail detail : mod193.getDetails()) {
			Mod193File2024.TYPE_2.fillPage(mod193, detail, wr);	
		}
	}
}
