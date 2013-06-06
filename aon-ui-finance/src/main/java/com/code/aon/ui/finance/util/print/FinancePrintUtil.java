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
		        int num_ = Integer.parseInt(str1);
		        int dec_ = Integer.parseInt(str2);
		        numero = new Number2Text();
		        res = numero.convertirLetras(num_) + " euros";
		        res += " con ";
		        res += numero.convertirLetras(dec_) + " céntimos";
			} else {
		        int num_ = Integer.parseInt(num);
		        numero = new Number2Text();
		        res = numero.convertirLetras(num_) + " euros";
			}
			return res.toUpperCase();
    	} catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
		}
    	return null;
    }

}