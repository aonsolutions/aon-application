package net.aonsolutions.aon.tbai.beans.invoice.parts;

import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

public class Entity {

	private String nif;  				//NIF-FORMAT 9
	private String name;				//ALPHANUMERIC 120
	
	private Country country;		
	private String id_type;				// [02 - 06] L2
	private String id;					// ALPHANUMERIC 20
	private String zip;					// ALPHANUMERIC 20
	private String address;				// ALPHANUMERIC 250
	
	public Entity(String nif, String name, Country country, String id_type, String id, String zip, String address) {
		this.nif = nif;
		this.name = name;
		this.country = country;
		this.id_type = id_type;
		this.id = id;
		this.zip = zip;
		this.address = address;
	}

	public Optional<String> getNif() {return Optional.ofNullable(nif);}
	public void setNif(String nif) {this.nif = nif;}

	public Optional<String> getName() {return Optional.ofNullable(name);}
	public void setName(String name) {this.name = name;}

	public Optional<Country> getCountry() {return Optional.ofNullable(country);}
	public void setCountry(Country country) {this.country = country;}

	public Optional<String> getId_type() {return Optional.ofNullable(id_type);}
	public void setId_type(String id_type) {this.id_type = id_type;}

	public Optional<String> getId() {return Optional.ofNullable(id);}
	public void setId(String id) {this.id = id;}

	public Optional<String> getZip() {return Optional.ofNullable(zip);}
	public void setZip(String zip) {this.zip = zip;}

	public Optional<String> getAddress() {return Optional.ofNullable(address);}
	public void setAddress(String address) {this.address = address;}

	@Override
	public String toString() {
		return "Entity :\t\n{ \n\tnif: \t\t" + nif + ", \n\tname: \t\t" + name + ", \n\tcountry: \t\t" + country
				+ ", \n\tid_type: \t\t" + id_type + ", \n\tid: \t\t" + id + ", \n\tzip: \t\t" + zip
				+ ", \n\taddress: \t\t" + address + "\n}";
	}
	
	
}
