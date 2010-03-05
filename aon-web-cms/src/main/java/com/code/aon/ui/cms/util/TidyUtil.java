package com.code.aon.ui.cms.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;
import org.w3c.tidy.TidyMessage.Level;

import com.code.aon.ui.cms.IGeneratorLogger;

public class TidyUtil implements TidyMessageListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(TidyUtil.class);
	
	private static String TIDY_PROPERTIES = "tidy.properties";
	
	private IGeneratorLogger logger;
	
	private String relativePath;
	
	private Tidy tidy;
	
	public TidyUtil( IGeneratorLogger logger ) {
		this.tidy = new Tidy();
		this.tidy.setConfigurationFromFile(getConfigurationFile().getAbsolutePath());
		this.tidy.setMessageListener(this);		
		this.tidy.setErrout(new PrintWriter(NullWriter.NULL_WRITER));
		this.logger = logger;
	}

	public static File getConfigurationFile() {
		return new File( ControllerUtil.getConfigPath(), TIDY_PROPERTIES );
	}
	
	public static boolean isEnabled() {
		return getConfigurationFile().exists();
	}
	
	@Override
	public void messageReceived(TidyMessage msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("Tidy: ").append(this.relativePath);
		sb.append("(").append(msg.getLine()).append(",").append(msg.getColumn()).append(") ");
		if ( StringUtils.isBlank(msg.getMessage()) ) {
			sb.append( "Error Code ").append(msg.getErrorCode());
		} else {
			sb.append(msg.getMessage());
		}
		String message = sb.toString();
		if ( msg.getLevel() == Level.ERROR) {
			LOGGER.error( message );
			logger.error( StringEscapeUtils.escapeHtml(message) );
		} else if ( msg.getLevel() == Level.INFO ) {
			LOGGER.info( message );
		} else if ( msg.getLevel() == Level.WARNING ) {
			LOGGER.warn( message );
			logger.warning( StringEscapeUtils.escapeHtml(message) );
		} else if ( msg.getLevel() == Level.SUMMARY ) {
			LOGGER.debug( message );
		}
	}
	
	public void parse( File file ) {
		this.relativePath = ControllerUtil.getRelativePath( ControllerUtil.getPreviewPath(), file);
		String name = FilenameUtils.getBaseName(file.getName()) + "_tidy";
		String extension = FilenameUtils.getExtension(file.getName());
		File outFile = null;
		Writer out = null;
		Reader in = null;
		try {
			outFile = File.createTempFile(name, extension, ControllerUtil.getTemporalPath());
			out = new FileWriter(outFile);
			in = new FileReader(file);
			tidy.parse(in, out);
		} catch (IOException e) {
			LOGGER.error( "Error parsing " + file, e);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
		if ( tidy.getParseErrors() == 0 ) {
			try {
				File tempFile = new File( ControllerUtil.getTemporalPath(), this.relativePath );
				if ( tempFile.exists() ) {
					tempFile.delete();
				} else {
					tempFile.getParentFile().mkdirs();	
				}
				FileUtils.moveFile(file, tempFile);
				FileUtils.moveFile(outFile, file);
			} catch (IOException e) {
				LOGGER.error( "Error moving tidied file " + file, e);
			}
		}
	}
	
}
