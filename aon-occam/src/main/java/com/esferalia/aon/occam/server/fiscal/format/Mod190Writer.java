package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
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
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(
			   det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( 
			   det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL())).sum(),15,2))
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
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfData().isCeutaMelilla()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getBirthYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getFamilySituation(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getIrpfData().getSpouseDocument(), 9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().ensureDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(" ")
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfData().isGeographicMobility()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getFoodAnnuality()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan3Descendent(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan3DescendentRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendentRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent33(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent33Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendentDependence(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendentDependenceRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent65(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent65Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan75Ascendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan75AscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getAscendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getAscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant33(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant33Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendantDependence(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendantDependenceRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant65(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant65Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getFirstChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getSecondChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getThirdChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfResult().isHomeLoanCommunnication()?"1":"0")
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
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfData().isCeutaMelilla()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getBirthYear(), 4,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getFamilySituation(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.text(detail.getIrpfData().getSpouseDocument(), 9))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().ensureDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getContract(), 1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfData().isWorkActivityExtension()?"X":" ")
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfData().isGeographicMobility()?"1":"0")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getFoodAnnuality()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan3Descendent(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan3DescendentRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendent(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendentRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent33(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent33Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendentDependence(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendentDependenceRatio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent65(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityDescendent65Ratio(),2,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan75Ascendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getLessThan75AscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getAscendant(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getAscendantRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant33(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant33Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendantDependence(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendantDependenceRatio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant65(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getDisabilityAscendant65Ratio(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getFirstChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getSecondChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getThirdChildCalculation(),1,0))
		   ,(wr, mod190,detail) -> wr.append(detail.getIrpfResult().isHomeLoanCommunnication()?"1":"0")
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
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.signedSpace(mod190.getDetails().stream().mapToDouble(
			   det -> AonMathUtils.round(det.getPerception() + det.getInKindPerception() + det.getPerceptionIL())).sum(),16,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(mod190.getDetails().stream().mapToDouble( 
			   det -> AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL())).sum(),15,2))
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
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().ensureDisability(), 1,0))
		   ,(wr, mod190,detail) -> wr.append("000")
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getApplicableReduction()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getDeducibleExpense()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getCompensatoryPension()),13,2))
		   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat('0', 17))
		   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendent(),2,0))
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
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().ensureDisability(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getContract(), 1,0))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getApplicableReduction()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getDeducibleExpense()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getCompensatoryPension()),13,2))
			   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 17))
			   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendent(),2,0))
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
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().ensureDisability(), 1,0))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfData().getContract(), 1,0)) 
				   ,(wr, mod190,detail) -> wr.append("00")
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getApplicableReduction()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getDeducibleExpense()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(AonMathUtils.round(detail.getIrpfResult().getCompensatoryPension()),13,2))
				   ,(wr, mod190,detail) -> wr.append(AonStringUtils.repeat(' ', 17))
				   ,(wr, mod190,detail) -> wr.append(AonFiscalFileUtils.unsigned(detail.getIrpfResult().getOtherDescendent(),2,0))
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
		Administration adm = Administration.values()[mod190.getAdministration()];
		if (adm == Administration.GIPUZKOA) {
			if (mod190.getYear() < 2016) {
				fillWriterGipuzkoa2015(mod190, wr);
			} else {
				fillWriterGipuzkoa2016(mod190, wr);
			}
				
				
		} else if (adm == Administration.BIZKAIA) {
			fillWriterBizkaia(mod190, wr);
		} else {
			if (mod190.getYear() < 2016) {
				fillWriterAeat2015(mod190, wr);
			} else {
				fillWriterAeat2016(mod190, wr);
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

	private static void fillWriterBizkaia(Mod190 mod190, Writer wr) throws IOException {
		Mod190File2016Bizkaia.TYPE_1.fillPage(mod190, null, wr);	
		for (Mod190Detail detail : mod190.getDetails()) {
			Mod190File2016Bizkaia.TYPE_2.fillPage(mod190, detail, wr);	
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

}
