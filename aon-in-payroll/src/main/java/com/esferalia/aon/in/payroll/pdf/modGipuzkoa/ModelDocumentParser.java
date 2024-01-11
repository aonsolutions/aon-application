package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ModelDocumentExtracters.IModelDocumentExtracter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public class ModelDocumentParser {

	private ModelDocumentParser() {
		
	}
	
	public static FiscalModel parse(byte[] bytes) {
		IModelDocumentExtracter extracter = ModelDocumentExtracters.getExtracter(bytes);
		String text = extracter.extract(bytes);
		return ModelDocumentParsers.parse(text);
	}
}
