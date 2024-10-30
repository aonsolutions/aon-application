package net.aonsolutions.occam.api.model.type.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.util.InvoiceUtil;

class InvoiceUtilTest {
	
	@Test
	void testSeriesNumber() {
		assertEquals("??????", InvoiceUtil.getSeriesNumber(null,null)); 
		assertEquals("??????", InvoiceUtil.getSeriesNumber("",null)); 
		assertEquals("??????", InvoiceUtil.getSeriesNumber(" ",null));
		
		assertEquals("000000", InvoiceUtil.getSeriesNumber(null,0)); 
		assertEquals("000000", InvoiceUtil.getSeriesNumber("",0)); 
		assertEquals("000000", InvoiceUtil.getSeriesNumber(" ",0));
		
		assertEquals("000010", InvoiceUtil.getSeriesNumber(null,10)); 
		assertEquals("000010", InvoiceUtil.getSeriesNumber("",10)); 
		assertEquals("000010", InvoiceUtil.getSeriesNumber(" ",10)); 
		
		assertEquals("PROFORMA", InvoiceUtil.getSeriesNumber(null,-10)); 
		assertEquals("PROFORMA", InvoiceUtil.getSeriesNumber("",-10)); 
		assertEquals("PROFORMA", InvoiceUtil.getSeriesNumber(" ",-10));
		
		assertEquals("24/??????", InvoiceUtil.getSeriesNumber("24",null)); 
		assertEquals("24/000000", InvoiceUtil.getSeriesNumber("24",0)); 
		assertEquals("24/000010", InvoiceUtil.getSeriesNumber("24",10)); 
		assertEquals("24/PROFORMA", InvoiceUtil.getSeriesNumber("24",-10)); 
		
		assertEquals("24/8888888", InvoiceUtil.getSeriesNumber("24",8888888));
		
	}
	
	@Test
	void testDocumentNumber() {
		assertEquals("??????", InvoiceUtil.getDocumentNumber(null,null,null)); 
		assertEquals("??????", InvoiceUtil.getDocumentNumber(null,"",null)); 
		assertEquals("??????", InvoiceUtil.getDocumentNumber(null," ",null));
		
		assertEquals("000000", InvoiceUtil.getDocumentNumber(null,null,0)); 
		assertEquals("000000", InvoiceUtil.getDocumentNumber(null,"",0)); 
		assertEquals("000000", InvoiceUtil.getDocumentNumber(null," ",0));
		
		assertEquals("000010", InvoiceUtil.getDocumentNumber(null,null,10)); 
		assertEquals("000010", InvoiceUtil.getDocumentNumber(null,"",10)); 
		assertEquals("000010", InvoiceUtil.getDocumentNumber(null," ",10)); 
		
		assertEquals("PROFORMA", InvoiceUtil.getDocumentNumber(null,null,-10)); 
		assertEquals("PROFORMA", InvoiceUtil.getDocumentNumber(null,"",-10)); 
		assertEquals("PROFORMA", InvoiceUtil.getDocumentNumber(null," ",-10));

		assertEquals("R-24/??????"  , InvoiceUtil.getDocumentNumber(InvoiceType.PURCHASE,"24",null)); 
		assertEquals("R-24/000000"  , InvoiceUtil.getDocumentNumber(InvoiceType.PURCHASE,"24",0)); 
		assertEquals("R-24/000010"  , InvoiceUtil.getDocumentNumber(InvoiceType.PURCHASE,"24",10)); 
		assertEquals("R-24/PROFORMA", InvoiceUtil.getDocumentNumber(InvoiceType.PURCHASE,"24",-10)); 
		assertEquals("R-24/8888888" , InvoiceUtil.getDocumentNumber(InvoiceType.PURCHASE,"24",8888888));
		
		assertEquals("R-24/??????"  , InvoiceUtil.getDocumentNumber(InvoiceType.EXPENSES,"24",null)); 
		assertEquals("R-24/000000"  , InvoiceUtil.getDocumentNumber(InvoiceType.EXPENSES,"24",0)); 
		assertEquals("R-24/000010"  , InvoiceUtil.getDocumentNumber(InvoiceType.EXPENSES,"24",10)); 
		assertEquals("R-24/PROFORMA", InvoiceUtil.getDocumentNumber(InvoiceType.EXPENSES,"24",-10)); 
		assertEquals("R-24/8888888" , InvoiceUtil.getDocumentNumber(InvoiceType.EXPENSES,"24",8888888));
		
		assertEquals("E-24/??????"  , InvoiceUtil.getDocumentNumber(InvoiceType.SALES,"24",null)); 
		assertEquals("E-24/000000"  , InvoiceUtil.getDocumentNumber(InvoiceType.SALES,"24",0)); 
		assertEquals("E-24/000010"  , InvoiceUtil.getDocumentNumber(InvoiceType.SALES,"24",10)); 
		assertEquals("E-24/PROFORMA", InvoiceUtil.getDocumentNumber(InvoiceType.SALES,"24",-10)); 
		assertEquals("E-24/8888888" , InvoiceUtil.getDocumentNumber(InvoiceType.SALES,"24",8888888));
		
		assertEquals("G-24/??????"  , InvoiceUtil.getDocumentNumber(InvoiceType.UNDEDUCTIBLE,"24",null)); 
		assertEquals("G-24/000000"  , InvoiceUtil.getDocumentNumber(InvoiceType.UNDEDUCTIBLE,"24",0)); 
		assertEquals("G-24/000010"  , InvoiceUtil.getDocumentNumber(InvoiceType.UNDEDUCTIBLE,"24",10)); 
		assertEquals("G-24/PROFORMA", InvoiceUtil.getDocumentNumber(InvoiceType.UNDEDUCTIBLE,"24",-10)); 
		assertEquals("G-24/8888888" , InvoiceUtil.getDocumentNumber(InvoiceType.UNDEDUCTIBLE,"24",8888888));
		
	}
	
}
