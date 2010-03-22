package com.code.aon.csb.fd0.model.CSB32.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB32.data.Delivery;


public class CheckDelivery extends Check{

	public static boolean parse(Delivery delivery,ArrayList exceptions){
		boolean status = true;
		if (delivery.getDeliveyNumber()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_1") ,delivery.toString()) );
			status = false;
		}else{
			if (delivery.getDeliveyNumber().intValue()<0 ||
					delivery.getDeliveyNumber().intValue()>9999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_2") ,delivery.toString()) );
				status = false;
			}
		}
		if (delivery.getTruncatedEffects()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_3") ,delivery.toString()) );
			status = false;
		}else{
			if (delivery.getTruncatedEffects().intValue()<0 ||
					delivery.getTruncatedEffects().intValue()>9){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_4") ,delivery.toString()) );
				status = false;
			}
		}
		if (delivery.getPaymentAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_5") ,delivery.toString()) );
			status = false;
		}else{
			try{
				if (!delivery.getPaymentAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_6") ,delivery.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_6") ,delivery.toString()) );
				status = false;
			}
		}
		if (delivery.getOweAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_7") ,delivery.toString()) );
			status = false;
		}else{
			try{
				if (!delivery.getOweAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_8") ,delivery.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_8") ,delivery.toString()) );
				status = false;
			}
		}
		if (delivery.getNotPayedAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_9") ,delivery.toString()) );
			status = false;
		}else{
			try{
				if (!delivery.getNotPayedAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_10") ,delivery.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_DELIVERY_10") ,delivery.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
