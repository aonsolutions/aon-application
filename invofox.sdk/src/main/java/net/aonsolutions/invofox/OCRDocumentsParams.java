package net.aonsolutions.invofox;

import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRSeverity;
import net.aonsolutions.invofox.model.OCRType;

public class OCRDocumentsParams extends OCRParams {
	
	public static OCRDocumentsParams get() {
		return new OCRDocumentsParams();
	}
	private OCRDocumentsParams() {
		
	}
	protected OCRDocumentsParams append(String name, int value) {
		super.append(name, AonNumberUtils.toString(value));
		return this;
	}
	protected OCRDocumentsParams append(String name, String value) {
		super.append(name, value);
		return this; 
	}
	
	public OCRDocumentsParams withType( OCRType param ) { return append( OCRNames.TYPE, param.toString());}
	public OCRDocumentsParams withPublicState( OCRSeverity param) { return append( OCRNames.PUBLIC_STATE, param.toString());}
	public OCRDocumentsParams withCompany( String param) { return append( OCRNames.COMPANY, param);}
	public OCRDocumentsParams skiping(int param) { return append( OCRNames.SKIP, param);}
	public OCRDocumentsParams limit(int param) { return append( OCRNames.LIMIT, param);}
}
