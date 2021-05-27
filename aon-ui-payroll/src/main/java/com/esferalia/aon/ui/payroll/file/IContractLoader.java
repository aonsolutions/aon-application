package com.esferalia.aon.ui.payroll.file;

import java.io.BufferedReader;
import java.io.InputStream;

import org.hibernate.Session;

import com.code.aon.common.AonException;


public interface IContractLoader {
	
	boolean isValidFile(BufferedReader reader, InputStream input)  throws AonException;
	
	public String getFileTypeDescription();
	
	void checkMetadata(InputStream input) throws AonException;
	
	void validate(InputStream input) throws AonException;
	 
	void load(InputStream input, Session session) throws AonException;
	
}
