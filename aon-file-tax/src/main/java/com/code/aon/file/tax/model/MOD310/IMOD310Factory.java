package com.code.aon.file.tax.model.MOD310;

import java.io.Writer;
import java.util.List;

import com.code.aon.file.tax.model.MOD310.data.Declaration;

public interface IMOD310Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
