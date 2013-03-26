package com.code.aon.file.tax.model.MOD130;

import java.io.Writer;
import java.util.List;

import com.code.aon.file.tax.model.MOD130.data.Declaration;

public interface IMOD130Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
