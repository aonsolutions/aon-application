package com.code.ui.gbp.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.Supplier;
import com.code.ui.gbp.controller.SupplierController;

public class BankAccountValidationListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SupplierController supplierController = (SupplierController)event.getController();
		if(!supplierController.getControl().equals(calculaDC(supplierController.getEntity(), supplierController.getOffice(), supplierController.getAccount()))){
			throw new ControllerListenerException("Invalid Bank Account");
		}else{
			String bankAccount = supplierController.getEntity() + supplierController.getOffice() + supplierController.getControl() + supplierController.getAccount();
			((Supplier)supplierController.getTo()).setBankAccount(bankAccount);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SupplierController supplierController = (SupplierController)event.getController();
		if(!supplierController.getControl().equals(calculaDC(supplierController.getEntity(), supplierController.getOffice(), supplierController.getAccount()))){
			throw new ControllerListenerException("Invalid Bank Account");
		}else{
			String bankAccount = supplierController.getEntity() + supplierController.getOffice() + supplierController.getControl() + supplierController.getAccount();
			((Supplier)supplierController.getTo()).setBankAccount(bankAccount);
		}
	}
	
	private String calculaDC(String entidad, String oficina, String cuenta) {
		if ((entidad.length() != 4) || (oficina.length() != 4)
				|| (cuenta.length() != 10)) {
			return "XX";
		}
		int[] ccc_pesos = new int[10];
		ccc_pesos[0] = 6;
		ccc_pesos[1] = 3;
		ccc_pesos[2] = 7;
		ccc_pesos[3] = 9;
		ccc_pesos[4] = 10;
		ccc_pesos[5] = 5;
		ccc_pesos[6] = 8;
		ccc_pesos[7] = 4;
		ccc_pesos[8] = 2;
		ccc_pesos[9] = 1;
		String entofi = entidad + oficina;
		int suma = 0;
		int total = 0;
		for (int i = 0; i < entofi.length(); i++) {
			int digito = Integer.parseInt(String.valueOf(entofi.charAt(entofi
					.length()
					- 1 - i)));
			suma = digito * ccc_pesos[i];
			total = total + suma;
		}
		total = 11 - (total % 11);
		if (total == 10) {
			total = 1;
		}
		if (total == 11) {
			total = 0;
		}
		int numero = 0;
		int control = 0;
		int c = 0;
		for (int i = 0; i < cuenta.length(); i++) {
			numero = Integer.parseInt(String.valueOf(cuenta.charAt(cuenta
					.length()
					- 1 - i)));
			control = numero * ccc_pesos[i];
			c = c + control;
		}
		c = 11 - (c % 11);
		if (c == 10) {
			c = 1;
		}
		if (c == 11) {
			c = 0;
		}
		return String.valueOf(total) + String.valueOf(c);
	}
}