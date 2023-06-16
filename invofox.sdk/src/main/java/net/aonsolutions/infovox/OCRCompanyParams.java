package net.aonsolutions.infovox;

import net.aonsolutions.infovox.json.OCRNames;

public class OCRCompanyParams extends OCRParams {
	
	public static OCRCompanyParams get() {
		return new OCRCompanyParams();
	}
	private OCRCompanyParams() {
		
	}
	protected OCRCompanyParams append(String name, String value) {
		super.append(name, value);
		return this; 
	}
	public OCRCompanyParams withId(String param) 	{ return append( OCRNames.ID, param);}
	public OCRCompanyParams withTaxId(String param) { return append( OCRNames.TAX_ID, param);}
	public OCRCompanyParams withName(String param) 	{ return append( OCRNames.NAME, param);}
	
}
