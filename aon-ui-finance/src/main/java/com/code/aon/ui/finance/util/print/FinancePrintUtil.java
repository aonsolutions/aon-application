package com.code.aon.ui.finance.util.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.registry.report.Number2Text;

public class FinancePrintUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FinancePrintUtil.class);
	
	private FinancePrintUtil instance;
	
	public FinancePrintUtil getInstance(){
		if(instance == null){
			instance = new FinancePrintUtil();
		}
		return instance;
	}
	
	public static String numberToText(Integer amount){
		return numberToText(new Double(amount));
	}
	
	public static String numberToText(Double amount){
    	try{
			String res;
			Number2Text numero;
			String num = String.valueOf(CommonUtil.round(amount, 2));
			String decimalChar = ".";
			if (num.lastIndexOf(",") != -1) decimalChar = ",";
			if (num.lastIndexOf(decimalChar) != -1){
				String str1 = num.substring(0,num.lastIndexOf(decimalChar));
				String str2 = num.substring(num.lastIndexOf(decimalChar)+1);
				// si la parte decimal no consta de unidad de centesima, esta se incluye con valor cero 
				str2 = str2.length()==1?str2.concat("0"):str2;
		        int ent_ = Integer.parseInt(str1);
		        int dec_ = Integer.parseInt(str2);
		        numero = new Number2Text();
		        res = numero.convertirLetras(ent_);
		        res = (ent_==1?res.substring(0, res.length()-1):res);
		        res += " euro" + (ent_==1?"":"s");
		        res += " con ";
		        res += numero.convertirLetras(dec_);
		        res = (dec_==1?res.substring(0, res.length()-1):res);
		        res += " céntimo" + (dec_==1?"":"s");
			} else {
		        int num_ = Integer.parseInt(num);
		        numero = new Number2Text();
		        res = numero.convertirLetras(num_) + " euro" + (num_==1?"":"s");
			}
			return res.toUpperCase();
    	} catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
		}
    	return null;
    }
	
	public static String monthDayToText(Integer dayNumber){
		Number2Text numero = new Number2Text();
		return numero.convertirLetras(dayNumber);
	}

}