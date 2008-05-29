package com.code.aon.ui.academy.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.util.AonUtil;

public class MailingController {

	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	protected String getFicheroDestino() {
		return getTmpPath()+FILENAME;
	}

	protected String getTmpPath() {
		String path = System.getProperty("java.io.tmpdir");
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return path;
	}
	
	@SuppressWarnings("unused")
	public void onGenerateFile(ActionEvent event) throws ManagerBeanException {
		String filename = getFicheroDestino();
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(filename)));
		} catch (FileNotFoundException e) {
			throw new ManagerBeanException(e);
		}
		
        CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
        Criteria criteria = customerController.getCriteria();
        List<ITransferObject> customers = BeanManager.getManagerBean(Customer.class).getList(criteria);
        Iterator<ITransferObject> customersIter = customers.iterator();
    	pw.print(parseStructure());
		pw.println();
        while (customersIter.hasNext()){
        	Customer customer = (Customer)customersIter.next();
        	pw.print(parseLine(customer));
			pw.println();
        }
		pw.flush();
		pw.close();
        
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType("aplication/disk");
			response.setHeader("content-disposition", "attachment;filename=\"" + FILENAME + "\"");
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			byte[] data = new byte[1024];
			FileInputStream file = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(file);
			boolean eof = false;
			while (!eof) {
				int length = bis.read(data);
				if (length == -1) {
					eof = true;
				} else {
					bos.write(data, 0, length);
				}
			}
			bos.flush();
			bos.close();
			bis.close();
			faces.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parseStructure() throws ManagerBeanException{
		return "ID,DOCUMENT,NAME,SURNAME,PHONE,FAX,ADDRESS,CITY,GEOZONE,ZIP";
	}

	private String parseLine(Customer customer) throws ManagerBeanException{
		String line;
    	line = "\"" + parseValue(customer.getId()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(customer.getRegistry().getDocument()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(customer.getRegistry().getName()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(customer.getRegistry().getSurname()) + "\"" + SEPARATOR;
    	if (customer.getRegistry().getPhone()!=null){
    		line += "\"" + parseValue(customer.getRegistry().getPhone().getValue()) + "\"" + SEPARATOR;
    	}else{
        	line += SEPARATOR;
    	}
    	if (customer.getRegistry().getFax()!=null){
    		line += "\"" + parseValue(customer.getRegistry().getFax().getValue()) + "\"" + SEPARATOR;
    	}else{
        	line += SEPARATOR;
    	}
    	RegistryAddress raddress = customer.getRegistry().getDefaultAddress();
    	if (raddress==null){
        	line += SEPARATOR;
        	line += SEPARATOR;
        	line += SEPARATOR;
        	line += SEPARATOR;
    	}else{
        	String address = "";
        	address += parseValue(customer.getRegistry().getDefaultAddress().getAddress());
        	address += parseValue(customer.getRegistry().getDefaultAddress().getAddress2());
        	address += parseValue(customer.getRegistry().getDefaultAddress().getAddress3());
        	line += "\"" + address + "\"" + SEPARATOR;
        	line += "\"" + parseValue(customer.getRegistry().getDefaultAddress().getCity()) + "\"" + SEPARATOR;
        	if (customer.getRegistry().getDefaultAddress().getGeozone()!=null){
        		line += "\"" + parseValue(customer.getRegistry().getDefaultAddress().getGeozone().getName()) + "\"" + SEPARATOR;
        	}else{
            	line += SEPARATOR;
        	}
        	line += "\"" + parseValue(customer.getRegistry().getDefaultAddress().getZip()) + "\"";
    	}
    	return line;
	}
	
	private String parseValue(Object value){
		try{
			return value.toString();
		}catch (Exception e) {
			return "";
		}
	}
	
	private static final String SEPARATOR = ",";
	private static final String FILENAME = "mailing.csv";
	
}
