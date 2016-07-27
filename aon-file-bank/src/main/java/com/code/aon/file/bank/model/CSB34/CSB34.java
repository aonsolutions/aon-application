package com.code.aon.file.bank.model.CSB34;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.file.bank.model.CSB34.checks.CheckDetail;
import com.code.aon.file.bank.model.CSB34.checks.CheckMaster;
import com.code.aon.file.bank.model.CSB34.data.Detail;
import com.code.aon.file.bank.model.CSB34.data.Master;
import com.code.aon.file.bank.model.CSB34.xml.XMLLoader;
import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;

public class CSB34 extends AbstractFileFiller{

	private static String MASTER = "MASTER";
	private static String MASTER_PERSON = "MASTER_PERSON";
	private static String MASTER_ACCOUNT = "MASTER_ACCOUNT";
	private static String MASTER_OPERATOR = "MASTER_OPERATOR";
	private static String DETAIL = "DETAIL";
	private static String DETAIL_PERSON = "DETAIL_PERSON";
	private static String DETAIL_ACCOUNT = "DETAIL_ACCOUNT";

	private Master master;
	private int numreg;
	private int num010;
	private double amount;
	
	public CSB34(Master master, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		this.master = master;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Cabecera_1.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_2.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_3.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_4.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_5.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_6.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_1.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_2.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_3.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_4.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_5.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_6.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_7.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_8.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_9.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Totales_1.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	public ArrayList<Exception> create(){
		amount = num010 = numreg = 0;

		try{
			if (CheckMaster.parse(master,exceptions) == false)
				throw new Fd0Exception("ABORTED: ", master.toString());

			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(CSB34.MASTER, master);
			properties.put(CSB34.MASTER_PERSON, master.getOrderer());
			properties.put(CSB34.MASTER_ACCOUNT, master.getAccount());

			if (createLine("Cabecera_1", properties) != null)			
				++numreg;
			if (createLine("Cabecera_2", properties) != null)			
				++numreg;
			if (createLine("Cabecera_3", properties) != null)			
				++numreg;
			if (createLine("Cabecera_4", properties) != null)			
				++numreg;
			if (master.getOperator() != null) {
				properties.put(CSB34.MASTER_OPERATOR, master.getOperator());
				if (createLine("Cabecera_5", properties) != null)			
					++numreg;
				if (createLine("Cabecera_6", properties) != null)			
					++numreg;
			}

			Iterator<Detail> iterator = master.getReceiversIterator();
			while (iterator.hasNext()){
				Detail detail = (Detail)iterator.next();
				try{
					if (CheckDetail.parse(detail,exceptions) == false)
						throw new Fd0Exception( "ABORTED: ",detail.toString());

					properties.put(CSB34.DETAIL, detail);
					properties.put(CSB34.DETAIL_PERSON, detail.getReceiver());
					properties.put(CSB34.DETAIL_ACCOUNT, detail.getAccount());
					if (createLine("Beneficiario_1", properties) != null) {
						amount += detail.getAmount();
						++num010;
						++numreg;
					}
					if (createLine("Beneficiario_2", properties) != null)			
						++numreg;
					if (StringUtils.isNotEmpty(detail.getReceiver().getAddressPart1())) {
						if (createLine("Beneficiario_3", properties) != null)			
							++numreg;
					}
					if (StringUtils.isNotEmpty(detail.getReceiver().getAddressPart2())) {
						if (createLine("Beneficiario_4", properties) != null)			
							++numreg;
					}
					if (StringUtils.isNotEmpty(detail.getReceiver().getZip()) && StringUtils.isNotEmpty(detail.getReceiver().getCity())) {
						if (createLine("Beneficiario_5", properties) != null)
							++numreg;
					}
					if (StringUtils.isNotEmpty(detail.getReceiver().getProvince())) {
						if (createLine("Beneficiario_6", properties) != null)			
							++numreg;
					}
					if (StringUtils.isNotEmpty(detail.getConceptPart1())) {
						if (createLine("Beneficiario_7", properties) != null)			
							++numreg;
					}
					if (StringUtils.isNotEmpty(detail.getConceptPart2())) {
						if (createLine("Beneficiario_8", properties) != null)			
							++numreg;
					}
					//if (createLine("Beneficiario_9", properties) != null)			
						//++numreg;
				} catch (Exception ex) {
					if (ex instanceof Fd0Exception) {
						exceptions.add(ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception(ex.getMessage(), detail.toString());
						exceptions.add(e);
					}
				}
			}

			master.setAmount(amount);
			master.setNum010(num010);
			master.setNumreg(numreg+1);
			createLine("Totales_1", properties);
		} catch (Exception ex) {
			if (ex instanceof Fd0Exception) {
				exceptions.add(ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception(ex.getMessage(), master.toString());
				exceptions.add(e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	
}
