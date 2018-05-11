package com.esferalia.aon.gwt.payroll.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Countries {
	private Map<String, String> countries;
	
	public Countries() {
		this.countries = new HashMap<String, String>();
		this.countries.put("Afganistán", "AF");
		this.countries.put("Aland", "AX");
		this.countries.put("Albania", "AL");
		this.countries.put("Alemania", "DE");
		this.countries.put("Andorra", "AD");
		this.countries.put("Angola", "AO");
		this.countries.put("Anguila", "AI");
		this.countries.put("Antártida", "AQ");
		this.countries.put("Antigua y Barbuda", "AG");
		this.countries.put("Antillas Neerlandesas", "AN");
		this.countries.put("Arabia Saudita", "SA");
		this.countries.put("Argelia", "DZ");
		this.countries.put("Argentina", "AR");
		this.countries.put("Armenia", "AM");
		this.countries.put("Aruba", "AW");
		this.countries.put("Australia", "AU");
		this.countries.put("Austria", "AT");
		this.countries.put("Azerbaiyán", "AZ");
		
		this.countries.put("Bahamas", "BS");
		this.countries.put("Bahréin", "BH");
		this.countries.put("Bangladesh", "BD");
		this.countries.put("Barbados", "BB");
		this.countries.put("Bielorrusia", "BY");
		this.countries.put("Bélgica", "BE");
		this.countries.put("Belice", "BZ");
		this.countries.put("Benín", "BJ");
		this.countries.put("Bermudas", "BM");
		this.countries.put("Bután", "BT");
		this.countries.put("Bolivia", "BO");
		this.countries.put("Bosnia y Herzegovina", "BA");
		this.countries.put("Botsuana", "BW");
		this.countries.put("Isla Bouvet", "BV");
		this.countries.put("Brasil", "BR");
		this.countries.put("Brunéi", "BN");
		this.countries.put("Bulgaria", "BG");
		this.countries.put("Burkina Faso", "BF");
		this.countries.put("Burundi", "BI");
	}
	
	public Set<String> getCountries(){
		return this.countries.keySet();
	}
	
	public Collection<String> getCodes(){
		return this.countries.values();
	}
	
	public String getCode (String country){
		return this.countries.get(country);
	}
}
