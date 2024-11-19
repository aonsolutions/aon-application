package com.esferalia.aon.in.payroll.img;


import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import software.amazon.awssdk.services.textract.model.Document;

class PersonDocumentParserValidation {

	private PersonDocumentParserValidation() {

	}
	/*
	 * Validations for DNIParser
	 */

	private static final Consumer<byte[]> NULL_BYTES_FILE_UPLOADED = bytes -> {
		if (bytes == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
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
		if (document != null && document.bytes() != null
				&& (document.bytes().asByteBuffer().position() + document.bytes().asByteBuffer().remaining()) > (5 * 1024 * 1024)) {
			throw new AonCoreException(AonError.FILE_SIZE_EXCEEDED.getMessage());
		}
	};

	private static final Consumer<String> NULL_TEXT_RECEIVED = text -> {
		if (text == null) {
			throw new AonCoreException(AonError.NULL_TEXT_RECEIVED.getMessage());
		}
	};
	
	private static final Consumer<String> NULL_DATE_STRING = date ->{
		if (date == null) {
			throw new AonCoreException(AonError.NULL_DATE_STRING.getMessage());
		}
	};
	
	
	private static final Consumer<String> GROUP_NOT_MATCH = data ->{
		if (data == null || data.equals("")) {
			data = "";
		}
	};

	static void validateBytes(byte[] bytes) throws AonCoreException {
		NULL_BYTES_FILE_UPLOADED.accept(bytes);
	}

	static void validatePDDoc(PDDocument document) throws AonCoreException {
		NULL_PDDOC_FILE_UPLOADED.accept(document);
	}

	static void validateDoc(Document document) throws AonCoreException {
		NULL_DOC_FILE_UPLOADED.andThen(DOC_FILE_SIZE_EXCEEDED).accept(document);
	}

	static void validateText(String text) throws AonCoreException {
		NULL_TEXT_RECEIVED.accept(text);
	}	
	
	static void validateDates(String date) throws AonCoreException{
		NULL_DATE_STRING.accept(date);
	}
	
	static void validateGroup(String data) throws AonCoreException{
		GROUP_NOT_MATCH.accept(data);
	}
	
}
