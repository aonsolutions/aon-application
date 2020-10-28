package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.idc.SSBonusListener.SSBonus;

public class IdcParseSSBonus {
	
	public IdcParseSSBonus() {
		super();
	}
	
	public List<SSBonus> getSSBonusesFromIdc(Byte idcType, String filePath){
		if(idcType == (byte) 0)
			return getSSBonusesFromEnterpriseIdc(filePath);
		else
			return getSSBonusesFromEmployeeIdc(filePath);
	}
	
	public List<SSBonus> getSSBonusesFromIdc(Byte idcType, InputStream is){
		if(idcType == (byte) 0)
			return getSSBonusesFromEnterpriseIdc(is);
		else
			return getSSBonusesFromEmployeeIdc(is);
	}
	
	private List<SSBonus> getSSBonusesFromEmployeeIdc(String filePath) {
		List<SSBonus> ssBonuses = new ArrayList<SSBonus>();
		ssBonuses = parseEmployeeIdc(filePath);
		return ssBonuses;
	}
	
	private List<SSBonus> getSSBonusesFromEmployeeIdc(InputStream is) {
		List<SSBonus> ssBonuses = new ArrayList<SSBonus>();
		ssBonuses = parseEmployeeIdc(is);
		return ssBonuses;
	}
	
	private List<SSBonus> getSSBonusesFromEnterpriseIdc(String filePath) {
		List<SSBonus> ssBonuses = new ArrayList<SSBonus>();
		ssBonuses = parseEnterpriseIdc(filePath);
		return ssBonuses;
	}

	private List<SSBonus> getSSBonusesFromEnterpriseIdc(InputStream is) {
		List<SSBonus> ssBonuses = new ArrayList<SSBonus>();
		ssBonuses = parseEnterpriseIdc(is);
		return ssBonuses;
	}

	private List<SSBonus> parseEmployeeIdc(String filePath) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		try {
			IdcParser.parse(new File(filePath), ssBonusListener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownPDFException e) {
			e.printStackTrace();
		}
		return ssBonusListener.getSSBonus();
	}
	
	private List<SSBonus> parseEmployeeIdc(InputStream is) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		try {
			IdcParser.parse(is, ssBonusListener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownPDFException e) {
			e.printStackTrace();
		}
		return ssBonusListener.getSSBonus();
	}

	private List<SSBonus> parseEnterpriseIdc(String filePath) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		try {
			IdcplcccParser.parse(new File(filePath), ssBonusListener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownPDFException e) {
			e.printStackTrace();
		}
		return ssBonusListener.getSSBonus();
	}
	
	private List<SSBonus> parseEnterpriseIdc(InputStream is) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		try {
			IdcplcccParser.parse(is,  ssBonusListener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownPDFException e) {
			e.printStackTrace();
		}
		return ssBonusListener.getSSBonus();
	}

	public static void main(String[] args) {
		IdcParseSSBonus idcParseSSBonus = new IdcParseSSBonus();
		// Prueba IDC empleado
		List<SSBonus> ssBonuses = idcParseSSBonus.getSSBonusesFromIdc((byte)1, "/Users/sergio/Desktop/idc_josefa_lopez.pdf");
		// Prueba IDC empresa
//		List<SSBonus> ssBonuses = idcParseSSBonus.getSSBonusesFromIdc((byte)0, "/Users/sergio/Desktop/idcplcccI.pdf");
		ssBonuses.forEach(b -> System.out.println(b.toString()));
	}
}
