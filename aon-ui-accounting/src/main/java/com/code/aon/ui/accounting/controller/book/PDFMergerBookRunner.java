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
import org.apache.pdfbox.io.MemoryUsageSetting;
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
		LinkedList<File> files = new LinkedList<File>(); 
		File tempFile = null;
		FileOutputStream destStream = null;
		File runnerTempFile = null;
		FileInputStream input = null;
		try {
			tempFile = File.createTempFile("merge", ".pdf");
			files.add(tempFile);
			System.out.println("Main File ..: " + tempFile);
			PDFMergerUtility document = new PDFMergerUtility();
			destStream = new FileOutputStream(tempFile);
			document.setDestinationStream(destStream);
			for (IAccountingBookRunner runner : runners ) {
				runnerTempFile = File.createTempFile("merge", ".pdf");
				files.add(runnerTempFile);
				OutputStream output = new FileOutputStream(runnerTempFile);
				runner.run(output);
				document.addSource(runnerTempFile);
				IOUtils.closeQuietly(output);
				// FileUtils.deleteQuietly(runnerTempFile);
			}
			document.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
			input = new FileInputStream(tempFile); 
			IOUtils.copy(input, out);
			out.flush();
		} catch (IOException e) {
			throw new AccountingBookException(e);
		} finally {
			IOUtils.closeQuietly(destStream);
			IOUtils.closeQuietly(input);
			for (File file : files ) {
				FileUtils.deleteQuietly(file);	
			}
		}
	}
	
}
