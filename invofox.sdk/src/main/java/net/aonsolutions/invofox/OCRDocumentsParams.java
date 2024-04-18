package net.aonsolutions.invofox;

import java.util.function.Predicate;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRSeverity;
import net.aonsolutions.invofox.model.OCRType;

public class OCRDocumentsParams extends OCRParams {
    
	public static final Sort ASC = Sort.ASC;
	public static final Sort DESC = Sort.DESC;
	
	private Predicate<OCRDocument> filter;

	public enum Sort {
		ASC(""), DESC("-");

		private String str;

		Sort(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}
	
	
	
	public static OCRDocumentsParams get() {
		return new OCRDocumentsParams();
	}
	private OCRDocumentsParams() {
		filter = ocrDocument -> true; 
	}
	protected OCRDocumentsParams append(String name, int value) {
		super.append(name, AonNumberUtils.toString(value));
		return this;
	}
	protected OCRDocumentsParams append(String name, String value) {
		super.append(name, value);
		return this; 
	}
	
	protected OCRDocumentsParams append(Predicate<OCRDocument> predicate) {
		this.filter = this.filter.and(predicate);
		return this;
	}
	
	public boolean filter(OCRDocument ocrDocument ) {
		return filter.test(ocrDocument);
	}
	
	public OCRDocumentsParams limit(int param) {
		return append(OCRNames.LIMIT, param);
	}

	public OCRDocumentsParams skiping(int param) {
		return append(OCRNames.SKIP, param);
	}

	public OCRDocumentsParams withType(OCRType param) {
		return append(OCRNames.TYPE, param.toString());
	}

	public OCRDocumentsParams withPublicState(OCRSeverity param) {
		return append(OCRNames.PUBLIC_STATE, param.toString());
	}

	public OCRDocumentsParams withCompany(String param) {
		return append(OCRNames.COMPANY, param);
	}
	
	public OCRDocumentsParams withIssuerTaxId(String param) {
		return append(ocrDocument -> ocrDocument.getData()
				.map(data -> AonStringUtils.equalsIgnoreCase(normalize(data.getIssuerDocument()),param)).orElse(true));
	}

	public OCRDocumentsParams withRecipientTaxId(String param) {
		return append(ocrDocument -> ocrDocument.getData()
				.map(data -> AonStringUtils.equalsIgnoreCase(normalize(data.getRecipientDocument()),param)).orElse(true));
	}

	public OCRDocumentsParams withPredicate(Predicate<OCRDocument> predicate) {
		return append(predicate);
	}

	public OCRDocumentsParams sort(String param, Sort sort) {
		return append(OCRNames.SORT, sort.getStr() + param);
	}

	public static String normalize(String ocrDocument) {
		return AonStringUtils.removeStartIgnoreCase(ocrDocument, "ES");
	}
}
