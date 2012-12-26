package com.code.aon.file.tax.model.MOD115;

import java.io.Writer;
import java.util.List;

import com.code.aon.file.tax.model.MOD115.data.Declaration;

public interface IMOD115Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
