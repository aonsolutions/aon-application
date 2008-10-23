package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.StreetType;

/**
 * Transfer Object that represents the RegistryAddress.
 * 
 * @author Consulting & Development. Eugenio Castellano - 28-ene-2005
 * @since 1.0
 */
@Entity
@Table(name="raddress")
public class RegistryAddress implements ITransferObject, IAddress {

    /** The id. */
    private Integer id;

    /** The registry. */
    private Registry registry;

    /** The type. */
    private AddressType type;

    /** The recipient. */
    private String recipient;

    /** The street type. */
    private StreetType streetType;

    /** The 1st part of the address. */
    private String address;

    /** The 2nd part of the address. */
    private String address2;

    /** The 3rd part of the address. */
    private String address3;

    /** The zip code. */
    private String zip;

    /** The city. */
    private String city;

    /** The geozone. */
    private GeoZone geozone;

    /**
     * The empty constructor.
     */
    public RegistryAddress() {
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
	public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the registry.
     * 
     * @return the registry
     */
    @ManyToOne
    @JoinColumn(name="registry", nullable = false, updatable = false)    
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Sets the registry.
     * 
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * Gets the address.
     * 
     * @return the address
     */
    @Column(length=128)
	public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * 
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the address2.
     * 
     * @return the address2
     */
    @Column(length=128)
	public String getAddress2() {
        return address2;
    }

    /**
     * Sets the address2.
     * 
     * @param address2 the address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the address3.
     * 
     * @return the address3
     */
    @Column(length=128)
	public String getAddress3() {
        return address3;
    }

    /**
     * Sets the address3.
     * 
     * @param address3 the address3
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * Gets the address type.
     * 
     * @return the address type
     */
    @Column(name="type")
    public AddressType getAddressType() {
        return type;
    }

    /**
     * Sets the address type.
     * 
     * @param type the type
     */
    public void setAddressType(AddressType type) {
        this.type = type;
    }

    /**
     * Gets the city.
     * 
     * @return the city
     */
    @Column(length=64)
	public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     * 
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

	/**
	 * Gets the geozone.
	 * 
	 * @return the geozone
	 */
	@ManyToOne
    @JoinColumn( name="geozone" )
    public GeoZone getGeozone() {
        return geozone;
    }

    /**
     * Sets the geozone.
     * 
     * @param geozone the geozone
     */
    public void setGeozone(GeoZone geozone) {
        this.geozone = geozone;
    }

    /**
     * Gets the recipient.
     * 
     * @return the recipient
     */
    @Column(length=128)
	public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient.
     * 
     * @param recipient the recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the street type.
     * 
     * @return the street type
     */
    @Column(name="street_type")
    public StreetType getStreetType() {
        return streetType;
    }

    /**
     * Sets the street type.
     * 
     * @param streetType the street type
     */
    public void setStreetType(StreetType streetType) {
        this.streetType = streetType;
    }

    /**
     * Gets the zip code.
     * 
     * @return the zip code
     */
    @Column(length=16)
	public String getZip() {
        return zip;
    }

    /**
     * Sets the zip code.
     * 
     * @param zip the zip code
     */
    public void setZip(String zip) {
        this.zip = zip;
    }
}