package com.code.aon.ui.accounting.controller.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PDFMergerBookRunner extends AbsAccountingBookRunner {
	
	List<IAccountingBookRunner> runners = new LinkedList<IAccountingBookRunner>();
	
	public void addRunner( IAccountingBookRunner runner) {
		runners.add(runner);
	}
	public boolean isEmpty() {
		return runners.isEmpty();
	}
	
	@Override
	public boolean accept(BookType type) {
		// No esta registrado en el manager, se debe instanciar manualmente.
		return false;
	}

	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		File tempFile = null;
		FileOutputStream destStream = null;
		File runnerTempFile = null;
		FileInputStream input = null;
		try {
			tempFile = File.createTempFile("merge", ".pdf");
			PDFMergerUtility document = new PDFMergerUtility();
			destStream = new FileOutputStream(tempFile);
			document.setDestinationStream(destStream);
			for (IAccountingBookRunner runner : runners ) {
				runnerTempFile = File.createTempFile("merge", ".pdf");
				OutputStream output = new FileOutputStream(runnerTempFile);
				runner.run(output);
				document.addSource(runnerTempFile);
				IOUtils.closeQuietly(output);
				FileUtils.deleteQuietly(runnerTempFile);
			}
			document.mergeDocuments();
			input = new FileInputStream(tempFile); 
			IOUtils.copy(input, out);
			out.flush();
		} catch (IOException e) {
			throw new AccountingBookException(e);
		} finally {
			IOUtils.closeQuietly(destStream);
			IOUtils.closeQuietly(input);
			FileUtils.deleteQuietly(tempFile);
			FileUtils.deleteQuietly(runnerTempFile);
		}
	}
	
}
