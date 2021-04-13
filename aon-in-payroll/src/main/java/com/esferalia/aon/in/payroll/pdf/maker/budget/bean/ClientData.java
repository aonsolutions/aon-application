package com.esferalia.aon.in.payroll.pdf.maker.budget.bean;

import java.util.Optional;

public class ClientData {

	private Optional<String> enterpriseName;
	private Optional<String> address;
	private Optional<String> nif;
	private Optional<String> city;
	private Optional<String> postalCode;
	private Optional<String> province;
	private Optional<String> phone;
	private Optional<String> mobile;
	private Optional<String> email;
	private Optional<String> contact;

	/**
	 * ClientData constructor [privates]
	 */
	private ClientData() {
	}

	/**
	 * Get enterprise name or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getEnterpriseName(String defaultValue) {
		return enterpriseName.orElse(defaultValue);
	}

	/**
	 * Set the enterprise name
	 * 
	 * @param enterpriseName - The enterprise name
	 */
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = Optional.ofNullable(enterpriseName);
	}

	/**
	 * Get the address or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getAddress(String defaultValue) {
		return address.orElse(defaultValue);
	}

	/**
	 * Set the address
	 * 
	 * @param address - The address
	 */
	public void setAddress(String address) {
		this.address = Optional.ofNullable(address);
	}

	/**
	 * Get NIF or default value
	 * 
	 * @param defaultValue - The defualt value
	 * @return
	 */
	public String getNif(String defaultValue) {
		return nif.orElse(defaultValue);
	}

	/**
	 * Set the NIf
	 * 
	 * @param nif - The NIF
	 */
	public void setNif(String nif) {
		this.nif = Optional.ofNullable(nif);
	}

	/**
	 * Get city or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getCity(String defaultValue) {
		return city.orElse(defaultValue);
	}

	/**
	 * SEt the city
	 * 
	 * @param city - The city
	 */
	public void setCity(String city) {
		this.city = Optional.ofNullable(city);
	}

	/**
	 * Get the postal code or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getPostalCode(String defaultValue) {
		return postalCode.orElse(defaultValue);
	}

	/**
	 * Set the postal code
	 * 
	 * @param postalCode - The postal code
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = Optional.ofNullable(postalCode);
	}

	/**
	 * Get the province or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getProvince(String defaultValue) {
		return province.orElse(defaultValue);
	}

	/**
	 * Set the province
	 * 
	 * @param province - The province
	 */
	public void setProvince(String province) {
		this.province = Optional.ofNullable(province);
	}

	/**
	 * Get the phone or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getPhone(String defaultValue) {
		return phone.orElse(defaultValue);
	}

	/**
	 * Set the phone
	 * 
	 * @param phone - The phone
	 */
	public void setPhone(String phone) {
		this.phone = Optional.ofNullable(phone);
	}

	/**
	 * Get the mobile or default value
	 * 
	 * @return [String]
	 */
	public String getMobile(String defaultValue) {
		return mobile.orElse(defaultValue);
	}

	/**
	 * Set the mobile
	 * 
	 * @param mobile - The mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = Optional.ofNullable(mobile);
	}

	/**
	 * Get the email or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getEmail(String defaultValue) {
		return email.orElse(defaultValue);
	}

	/**
	 * Set the email
	 * 
	 * @param email - The email
	 */
	public void setEmail(String email) {
		this.email = Optional.ofNullable(email);
	}

	/**
	 * Get the contact or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [String]
	 */
	public String getContact(String defaultValue) {
		return contact.orElse(defaultValue);
	}

	/**
	 * Set the contact
	 * 
	 * @param contact - The contact
	 */
	public void setContact(String contact) {
		this.contact = Optional.ofNullable(contact);
	}

	/**
	 * Builder of the ClientData Objects
	 * 
	 * @author akrck02
	 *
	 */
	public static class ClientDataBuilder {

		private Optional<String> enterpriseName;
		private Optional<String> address;
		private Optional<String> nif;
		private Optional<String> city;
		private Optional<String> postalCode;
		private Optional<String> province;
		private Optional<String> phone;
		private Optional<String> mobile;
		private Optional<String> email;
		private Optional<String> contact;

		public ClientDataBuilder() {
			enterpriseName = Optional.empty();
			address		   = Optional.empty();
			nif			   = Optional.empty();
			city		   = Optional.empty();
			enterpriseName = Optional.empty();
			postalCode	   = Optional.empty();
			province	   = Optional.empty();
			phone		   = Optional.empty();
			mobile		   = Optional.empty();
			email		   = Optional.empty();
			contact		   = Optional.empty();
		}

		/**
		 * Set the enterprise name
		 * 
		 * @param enterpriseName - The enterprise name
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setEnterpriseName(String enterpriseName) {
			this.enterpriseName = Optional.ofNullable(enterpriseName);
			return this;
		}

		/**
		 * Set the address
		 * 
		 * @param address - The address
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setAddress(String address) {
			this.address = Optional.ofNullable(address);
			return this;
		}

		/**
		 * Set the NIf
		 * 
		 * @param nif - The NIF
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setNif(String nif) {
			this.nif = Optional.ofNullable(nif);
			return this;
		}

		/**
		 * Set the city
		 * 
		 * @param city - The city
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setCity(String city) {
			this.city = Optional.ofNullable(city);
			return this;
		}

		/**
		 * Set the postal code
		 * 
		 * @param postalCode - The postal code
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setPostalCode(String postalCode) {
			this.postalCode = Optional.ofNullable(postalCode);
			return this;
		}

		/**
		 * Set the province
		 * 
		 * @param province - The province
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setProvince(String province) {
			this.province = Optional.ofNullable(province);
			return this;
		}

		/**
		 * Set the phone
		 * 
		 * @param phone - The phone
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setPhone(String phone) {
			this.phone = Optional.ofNullable(phone);
			return this;
		}

		/**
		 * Set the mobile
		 * 
		 * @param mobile - The mobile
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setMobile(String mobile) {
			this.mobile = Optional.ofNullable(mobile);
			return this;
		}

		/**
		 * Set the email
		 * 
		 * @param email - The email
		 */
		public ClientDataBuilder setEmail(String email) {
			this.email = Optional.ofNullable(email);
			return this;
		}

		/**
		 * Set the contact
		 * 
		 * @param contact - The contact
		 * @return [CliendDataBuilder] The caller Object
		 */
		public ClientDataBuilder setContact(String contact) {
			this.contact = Optional.ofNullable(contact);
			return this;
		}

		/**
		 * Build a ClientData Object
		 * 
		 * @return [ClientData]
		 */
		public ClientData build() {

			ClientData data = new ClientData();

			data.enterpriseName	= this.enterpriseName;
			data.address		= this.address;
			data.nif			= this.nif;
			data.city			= this.city;
			data.postalCode		= this.postalCode;
			data.province		= this.province;
			data.phone			= this.phone;
			data.mobile			= this.mobile;
			data.email			= this.email;
			data.contact		= this.contact;

			return data;
		}

	}

}
