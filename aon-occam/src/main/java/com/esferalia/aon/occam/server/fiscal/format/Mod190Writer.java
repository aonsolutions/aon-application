package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190Writer {

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod190 mod190, Mod190Detail detail) throws IOException;
	}
	// **********************************************************************
	// 								AEAT 									
	// **********************************************************************
	
	private enum Mod190File2017Aeat {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(mod190.isComplementary()?"C":" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReplacedReceipt(),13,0))
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
		   ,(wr, mod190,detail) -> wr.append(detail.isCeutaMelilla()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getBirthYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFamilySituation(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getSpouseDocument(), 9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(detail.isGeographicMobility()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getFoodAnnuality()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3Descendent(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3DescendentRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendentRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependence(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependenceRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75Ascendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75AscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependence(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependenceRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFirstChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getSecondChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getThirdChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.isHomeLoanCommunnication()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getInKindPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindDepositIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindOutputDepositIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 179))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2017Aeat(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}
	
	private enum Mod190File2016Aeat {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReplacedReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   // repasar
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL()) ).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
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
		   ,(wr, mod190,detail) -> wr.append(detail.isCeutaMelilla()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getBirthYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFamilySituation(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getSpouseDocument(), 9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(detail.isGeographicMobility()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getFoodAnnuality()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3Descendent(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3DescendentRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendentRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependence(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependenceRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75Ascendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75AscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependence(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependenceRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFirstChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getSecondChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getThirdChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.isHomeLoanCommunnication()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getOutputRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 206))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2016Aeat(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}

	private enum Mod190File2015Aeat {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReplacedReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit())).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
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
		   ,(wr, mod190,detail) -> wr.append(detail.isCeutaMelilla()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getBirthYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFamilySituation(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getSpouseDocument(), 9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.isWorkActivityExtension()?"X":" ")
		   ,(wr, mod190,detail) -> wr.append(detail.isGeographicMobility()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getFoodAnnuality()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3Descendent(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan3DescendentRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendentRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent33Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependence(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendentDependenceRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityDescendent65Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75Ascendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getLessThan75AscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getAscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant33Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependence(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendantDependenceRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisabilityAscendant65Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getFirstChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getSecondChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getThirdChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.isHomeLoanCommunnication()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 246))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2015Aeat(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}

	// **********************************************************************
	// 								BIZKAIA 								
	// **********************************************************************
	private enum Mod190File2016Bizkaia {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReplacedReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   // repasar
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL())).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
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
		   ,(wr, mod190,detail) -> wr.append("0")
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append("000")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 17))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 26))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getOutputRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 206))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2016Bizkaia(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}

	private enum Mod190File2017Bizkaia {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(0,13,0))
		   ,(wr, mod190,detail) -> wr.append(mod190.isComplementary()?"C":" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(0,13,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(
				   det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL() + det.getInKindPerceptionIL())
				   ).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( 
				   det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL() + det.getInKindDepositIL()) 
				   ).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactMail(),50))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 275)) 
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
			
		,TYPE_2 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("2")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getRepresentativeDocument(),9))
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
		   ,(wr, mod190,detail) -> wr.append("0")
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append("000")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 17))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 26))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getInKindPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindDepositIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getInKindOutputDepositIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 179))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2017Bizkaia(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}
	// **********************************************************************
	// 								ARABA
	// **********************************************************************
	private enum Mod190File2016Araba {
		TYPE_1 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("1")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getName(),40))
		   ,(wr, mod190,detail) -> wr.append("T")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPhone(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getContactPerson(),40))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(mod190.isReplacement()?"S":" ")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getReplacedReceipt(),13,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().size(),9,0))
		   // repasar
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL())).sum(),15,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
			
		,TYPE_2 (new IPropertyFiller[] { 
			(wr, mod190,detail) -> wr.append("2")
		   ,(wr, mod190,detail) -> wr.append("190")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(mod190.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getDocument(),9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getRepresentativeDocument(),9))
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
		   ,(wr, mod190,detail) -> wr.append("0")
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 17))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 26))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getOutputRetentionIL()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 206))		
		   ,(wr, mod190,detail) -> wr.append("\r\n")
		})
		;
		private IPropertyFiller[] propertyFillers;

		private Mod190File2016Araba(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}
	}

	// **********************************************************************
	// 								GIPUZKOA 								
	// **********************************************************************
	private enum Mod190File2015Gipuzkoa {

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
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det .getPerception())).sum(),16,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention())).sum(),15,2))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
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
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 17))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 272))		
			   ,(wr, mod190,detail) -> wr.append("\r\n")
			})
		;

		private IPropertyFiller[] propertyFillers;

		private Mod190File2015Gipuzkoa(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}

	}

	private enum Mod190File2016Gipuzkoa {

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
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(det -> AonMathUtils.round(det .getPerception())).sum(),16,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( det -> AonMathUtils.round(det.getRetention())).sum(),15,2))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 312))		
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
				   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 15))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getDisability(), 1,0))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getContract(), 1,0)) 
				   ,(wr, mod190,detail) -> wr.append("00")
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getApplicableReduction()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getDeducibleExpense()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getCompensatoryPension()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 17))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getOtherDescendent(),2,0))
				   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 26))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(AonMathUtils.round(detail.getPerceptionIL()),14,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getRetentionIL()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getOutputRetentionIL()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 206))		
				   ,(wr, mod190,detail) -> wr.append("\r\n")
			})
		;

		private IPropertyFiller[] propertyFillers;

		private Mod190File2016Gipuzkoa(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod190 mod190, Mod190Detail detail, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod190, detail);
			}
		}

	}

	public static void fillWriter(Mod190 mod190, Writer wr) throws IOException {
		if (mod190.isGipuzkoa()) {
			if (mod190.getYear() < 2016) {
				fillWriterGipuzkoa2015(mod190, wr);
			} else {
				fillWriterGipuzkoa2016(mod190, wr);
			}
		} else if (mod190.isBizkaia()) {
			if (mod190.getYear() < 2017) {
				fillWriterBizkaia2016(mod190, wr);
			} else {
				fillWriterBizkaia2017(mod190, wr);
			}
		} else if (mod190.isAraba()) {
			fillWriterAraba(mod190, wr);
		} else {
			if (mod190.getYear() < 2016) {
				fillWriterAeat2015(mod190, wr);
			} if (mod190.getYear() == 2016) {
				fillWriterAeat2016(mod190, wr);
			} else {
				fillWriterAeat2017(mod190, wr);
			}
		}
		wr.flush();
	}
	
	private static void fillWriterGipuzkoa2015(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2015Gipuzkoa.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2015Gipuzkoa.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterGipuzkoa2016(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2016Gipuzkoa.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2016Gipuzkoa.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterBizkaia2016(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2016Bizkaia.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2016Bizkaia.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterBizkaia2017(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2017Bizkaia.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2017Bizkaia.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterAraba(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2016Araba.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2016Araba.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterAeat2015(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2015Aeat.TYPE_1.fillPage(mod190, null, wr);
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2015Aeat.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterAeat2016(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2016Aeat.TYPE_1.fillPage(mod190, null, wr);
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2016Aeat.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}

	private static void fillWriterAeat2017(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2017Aeat.TYPE_1.fillPage(mod190, null, wr);
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2017Aeat.TYPE_2.fillPage(mod190, detail, wr);	
		}
	}
}
