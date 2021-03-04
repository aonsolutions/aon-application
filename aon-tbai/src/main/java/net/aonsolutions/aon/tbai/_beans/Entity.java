package net.aonsolutions.aon.tbai._beans;

import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.tbai._enums.IDtype;

public class Entity {

	private String nif;  				
	private String name;				
	
	private Country country;		
	private IDtype id_type;				
	private String id;					
	private String zip;					
	private String address;				
	
	public Entity(final String nif, final String name, final Country country, final IDtype id_type, final String id, final String zip, final String address) {
		this.nif = nif;
		this.name = name;
		this.country = country;
		this.id_type = id_type;
		this.id = id;
		this.zip = zip;
		this.address = address;
	}
	public Entity() {}

	public Optional<String> getNif() 					{return Optional.ofNullable(nif);}
	public Entity setNif(final String nif) 				{this.nif = nif;     return this;}

	public Optional<String> getName() 					{return Optional.ofNullable(name);}
	public Entity setName(final String name) 			{this.name = name;    return this;}

	public Optional<Country> getCountry() 				{return Optional.ofNullable(country);}
	public Entity setCountry(final Country country) 	{this.country = country; return this;}

	public Optional<IDtype> getId_type() 				{return Optional.ofNullable(id_type);}
	public Entity setId_type(final IDtype id_type) 		{this.id_type = id_type; return this;}

	public Optional<String> getId() 					{return Optional.ofNullable(id);}
	public Entity setId(final String id) 				{this.id = id;      return this;}

	public Optional<String> getZip() 					{return Optional.ofNullable(zip);}
	public Entity setZip(final String zip) 				{this.zip = zip;     return this;}

	public Optional<String> getAddress() 				{return Optional.ofNullable(address);}
	public Entity setAddress(final String address) 		{this.address = address; return this;}

	@Override
	public String toString() {
		return "Entity :\t\n{ \n\tnif: \t\t" + nif + ", \n\tname: \t\t" + name + ", \n\tcountry: \t\t" + country
				+ ", \n\tid_type: \t\t" + id_type + ", \n\tid: \t\t" + id + ", \n\tzip: \t\t" + zip
				+ ", \n\taddress: \t\t" + address + "\n}";
	}
	
	
}
