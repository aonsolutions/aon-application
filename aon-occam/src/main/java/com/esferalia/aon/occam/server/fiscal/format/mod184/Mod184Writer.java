package com.esferalia.aon.occam.server.fiscal.format.mod184;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod184Writer {

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod184 mod184, Mod184Partner prt, Mod184Income inc) throws IOException;
	}

	private enum Mod184File2014 {

		TYPE_1 (new IPropertyFiller[] {

			(wr, mod184,prt,inc) -> wr.append("1")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("T")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getContactPhone(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getContactPerson(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getReceipt(),13,0))
		   ,(wr, mod184,prt,inc) -> wr.append(" ")
		   ,(wr, mod184,prt,inc) -> wr.append(mod184.isReplacement()?"S":" ")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getReplacedReceipt(),13,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getPartners().size(),9,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getEntityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getMainActivity(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getForeignEntityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getForeignObject(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getResidentPercent(),5,2))
		   ,(wr, mod184,prt,inc) -> wr.append(mod184.isTaxIS()?"X":" ")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getNetSalesAmount(),15,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getLrDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getLrName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 267))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
		})
		
		,TYPE_2 (new IPropertyFiller[] { 
			(wr, mod184,prt,inc) -> wr.append("2")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(" ",9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("E")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getKey(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getSubKey(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getRegime(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getActivityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getEpigraph(),4))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getGranteeDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getGranteeName(),20))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.dateZero(inc.getAdqDate()))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getIncrease(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getDecrease(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getAccountingResult(),14,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getExpenses(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getNetYield(),14,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getReductionPercent(),5,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getDeductionRightRent(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getResult(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getDeductionBase(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getRetention(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 256))
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
		})
		
		,TYPE_3 (new IPropertyFiller[] { 
			(wr, mod184,prt,inc) -> wr.append("2")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getRepresentativeDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("S")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getProvince(),2,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getPartType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(prt.isMemberEndOfYear()?"X":"")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getMemberDays(),3))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getPartPercent(),7,4))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getKey(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getSubKey(),2,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(prt.getAmount(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getReduction(),11,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getAddress(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 341))		
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
				})
		;

		private IPropertyFiller[] propertyFillers;

		private Mod184File2014(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod184 mod184, Mod184Partner prt,Mod184Income inc, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod184, prt, inc);
			}
		}

	}

	private enum Mod184File2015 {

		TYPE_1 (new IPropertyFiller[] {

			(wr, mod184,prt,inc) -> wr.append("1")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("T")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getContactPhone(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getContactPerson(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getReceipt(),13,0))
		   ,(wr, mod184,prt,inc) -> wr.append(" ")
		   ,(wr, mod184,prt,inc) -> wr.append(mod184.isReplacement()?"S":" ")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getReplacedReceipt(),13,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getPartners().size(),9,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getEntityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getMainActivity(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getForeignEntityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getForeignObject(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getResidentPercent(),5,2))
		   ,(wr, mod184,prt,inc) -> wr.append(mod184.isTaxIS()?"X":" ")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getNetSalesAmount(),15,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getLrDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getLrName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 267))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
		})
		
		,TYPE_2 (new IPropertyFiller[] { 
			(wr, mod184,prt,inc) -> wr.append("2")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(" ",9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("E")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getKey(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getSubKey(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getRegime(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getActivityType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getEpigraph(),4))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getGranteeDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getGranteeName(),20))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.dateZero(inc.getAdqDate()))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getIncrease(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getDecrease(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getAccountingResult(),14,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getExpenses(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getNetYield(),14,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getReductionPercent(),5,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(inc.getDeductionRightRent(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getResult(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getDeductionBase(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getRetention(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getLocation(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(inc.getCadasdralReference(),20))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getStaffExpenses(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getAssetAcquisition(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getTaxDeduction(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(inc.getOtherTaxDeduction(),12,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 187))
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
		})
		,TYPE_3 (new IPropertyFiller[] { 
			(wr, mod184,prt,inc) -> wr.append("2")
		   ,(wr, mod184,prt,inc) -> wr.append("184")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(mod184.getYear(), 4,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(mod184.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getRepresentativeDocument(),9))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getName(),40))
		   ,(wr, mod184,prt,inc) -> wr.append("S")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getProvince(),2,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getCountry(),2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getPartType(),1,0))
		   ,(wr, mod184,prt,inc) -> wr.append(prt.isMemberEndOfYear()?"X":"")
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getMemberDays(),3))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getPartPercent(),7,4))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getKey(),1))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getSubKey(),2,0))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.signedSpace(prt.getAmount(),13,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.unsigned(prt.getReduction(),11,2))
		   ,(wr, mod184,prt,inc) -> wr.append(AonFiscalFileUtils.text(prt.getAddress(),40))
		   ,(wr, mod184,prt,inc) -> wr.append(AonStringUtils.repeat(' ', 341))		
		   ,(wr, mod184,prt,inc) -> wr.append("\r\n")
				})
		;

		private IPropertyFiller[] propertyFillers;

		private Mod184File2015(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod184 mod184, Mod184Partner prt,Mod184Income inc, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod184, prt, inc);
			}
		}

	}

	public static void fillWriter(Mod184 mod184, Writer writer) throws IOException {
		if (mod184.getYear() < 2015 ) {
			Mod184File2014.TYPE_1.fillPage(mod184, null, null, writer);
			for (Mod184Income inc : mod184.getIncomes()) {
				Mod184File2014.TYPE_2.fillPage(mod184, null, inc, writer);	
			}
			for (Mod184Partner prt : mod184.getPartners()) {
				Mod184File2014.TYPE_3.fillPage(mod184, prt, null, writer);	
			}
		} else {
			Mod184File2015.TYPE_1.fillPage(mod184, null, null, writer);
			for (Mod184Income inc : mod184.getIncomes()) {
				Mod184File2015.TYPE_2.fillPage(mod184, null, inc, writer);	
			}
			for (Mod184Partner prt : mod184.getPartners()) {
				Mod184File2015.TYPE_3.fillPage(mod184, prt, null, writer);	
			}
		}
		writer.flush();
	}
}
