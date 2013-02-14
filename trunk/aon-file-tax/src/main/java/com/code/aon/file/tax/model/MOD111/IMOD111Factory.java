package com.code.aon.file.tax.model.MOD111;

import java.io.Writer;
import java.util.List;

import com.code.aon.file.tax.model.MOD111.data.Declaration;

public interface IMOD111Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
