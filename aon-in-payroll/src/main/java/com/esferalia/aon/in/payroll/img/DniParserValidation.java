package com.esferalia.aon.in.payroll.img;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DniParserValidation {
	
	
	private DniParserValidation() {
		
	}
	
	private static final Consumer<byte[]> NULL_BYTES_FILE_UPLOADED = bytes -> {
		if (bytes == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer<InputStream> NULL_IS_FILE_UPLOADED = is ->{
		if (is == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer<List<InputStream>> NULL_LIST_FILES_UPLOADED = list ->{
		if (list == null) {
			throw new AonCoreException(AonError.NULL_FILES_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer<PDDocument> NULL_PDDOC_FILE_UPLOADED = document -> {
		if (document == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer<Document> NULL_DOC_FILE_UPLOADED = document -> {
		if (document == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer<Document> DOC_FILE_SIZE_EXCEEDED = document -> {
		if (document != null && 
				document.getBytes() != null && 
					(document.getBytes().position() + document.getBytes().remaining()) >(5 * 1024 * 1024)) {
			throw new AonCoreException(AonError.FILE_SIZE_EXCEEDED.getMessage());
		}
	};
	
	private static final Consumer<String> NULL_TEXT_RECEIVED = text -> {
		if (text == null) {
			throw new AonCoreException(AonError.NULL_TEXT_RECEIVED.getMessage());
		}
	};
	
//	private static final Consumer<String> INVALID_DNI_FORMAT = dni -> {
//		dni = dni.trim();
//		String patternDni = "\\d{8}[A-HJ-NP-TV-Z]";
//		Pattern pattern = Pattern.compile(patternDni);
//		Matcher matcher = pattern.matcher(dni);
//		if (!matcher.matches()) {
//			throw new AonCoreException(AonError.INVALID_DNI_FORMAT.getMessage());
//		}
//		
//	};
	
	public static void validateBytes(byte[] bytes) throws AonCoreException{
		NULL_BYTES_FILE_UPLOADED.accept(bytes);
	}
	
	public static void validateInputstream(InputStream is) throws AonCoreException{
		NULL_IS_FILE_UPLOADED.accept(is);
	}
	
	public static void validateInputStreamList(List<InputStream> list) throws AonCoreException{
		NULL_LIST_FILES_UPLOADED.accept(list);
	}
	
	public static void validatePDDoc(PDDocument document) throws AonCoreException{
		NULL_PDDOC_FILE_UPLOADED.accept(document);
	}
	
	public static void validateDoc(Document document) throws AonCoreException{
		NULL_DOC_FILE_UPLOADED.
		andThen(DOC_FILE_SIZE_EXCEEDED).
		accept(document);
	}
	
	public static void validateText(String text) throws AonCoreException{
		NULL_TEXT_RECEIVED.accept(text);
	}
	
//	public static void validateDni(String dni) throws AonCoreException{
//		INVALID_DNI_FORMAT.accept(dni);
//	}
}
