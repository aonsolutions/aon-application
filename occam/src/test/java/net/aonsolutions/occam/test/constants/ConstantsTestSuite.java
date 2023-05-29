package net.aonsolutions.occam.test.constants;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 AccountingPeriodStatusTest.class
	,AonAppTest.class
	,AonModuleTest.class
	,AonStatusTest.class
	,AonLanguageTest.class
	,AdministrationTest.class
	,DocumentTypeTest.class	
	,DomainTypeTest.class
	,InvoiceSourceTest.class
	,InvoiceTypeTest.class
	,RectificationTypeTest.class
	,SecurityLevelTest.class
	,StreetTypeTest.class
	,TaxTypeTest.class
	,TransactionTypeTest.class
	,VatDeductionTypeTest.class
	,WithholdingTypeTest.class
	,WithholdingTypeGroupTest.class
})
public class ConstantsTestSuite {

	
}
