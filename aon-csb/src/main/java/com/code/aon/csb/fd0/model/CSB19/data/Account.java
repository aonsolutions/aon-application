package com.code.aon.csb.fd0.model.CSB19.data;

/**
 * A bank account structure
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Account {

	/**
	 * Entity
	 */
	private String ccc_e;

	/**
	 * Office
	 */
	private String ccc_o;

	/**
	 * Account
	 */
	private String ccc_a;

	/**
	 * Control digit
	 */
	private String ccc_dc;

	/**
	 * @return the ccc_a
	 */
	public String getCcc_a() {
		return ccc_a;
	}

	/**
	 * @param ccc_a
	 *            the ccc_a to set
	 */
	public void setCcc_a(String ccc_a) {
		this.ccc_a = ccc_a;
	}

	/**
	 * @return the ccc_dc
	 */
	public String getCcc_dc() {
		return ccc_dc;
	}

	/**
	 * @param ccc_dc
	 *            the ccc_dc to set
	 */
	public void setCcc_dc(String ccc_dc) {
		this.ccc_dc = ccc_dc;
	}

	/**
	 * @return the ccc_e
	 */
	public String getCcc_e() {
		return ccc_e;
	}

	/**
	 * @param ccc_e
	 *            the ccc_e to set
	 */
	public void setCcc_e(String ccc_e) {
		this.ccc_e = ccc_e;
	}

	/**
	 * @return the ccc_o
	 */
	public String getCcc_o() {
		return ccc_o;
	}

	/**
	 * @param ccc_o
	 *            the ccc_o to set
	 */
	public void setCcc_o(String ccc_o) {
		this.ccc_o = ccc_o;
	}

	/**
	 * Parses a string into an account  
	 * 
	 * @param ccc the account string
	 */
	public void parse(String ccc) {
		setCcc_e(ccc.substring(0, 4));
		setCcc_o(ccc.substring(4, 8));
		setCcc_dc(ccc.substring(8, 10));
		setCcc_a(ccc.substring(10, 20));
	}

	/**
	 * @return all the account parameters in one string
	 */
	public String getCcc() {
		return getCcc_e() + getCcc_o() + getCcc_dc() + getCcc_a();
	}

	/**
	 * @return true if is a correct account
	 */
	public boolean isCorrect(){
		String dc = calculaDC(getCcc_e(),getCcc_o(),getCcc_a());
		return dc.equals(getCcc_dc());
	}
	
	/**
	 * Finds the control digit
	 * 
	 * @param entidad entity
	 * @param oficina office
	 * @param cuenta account
	 * @return control digit
	 */
	private String calculaDC(String entidad, String oficina, String cuenta) {
		if ((entidad.length() != 4) || (oficina.length() != 4)
				|| (cuenta.length() != 10)) {
			return "XX";
		} else {
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
				int digito = Integer.parseInt(String.valueOf(entofi
						.charAt(entofi.length() - 1 - i)));
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

}
