package com.code.aon.cms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.cms.util.IActivableObject;

@Entity
@Table(name = "sport_player")
public class SportPlayer implements IActivableObject {

	private Integer id;
	
	private String name;
	
	private SportPosition sportPosition;
	
	private Date bornDate;
	
	private String bornPlace;
	
	private Double weight;
	
	private Double lenght;
	
	private SportNationality sportNationality;
	
	private boolean comunitary;
	
	private String photo;
	
	private SportClub sportClub;

	private boolean active = true;
	
	private Integer number;
  
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=128)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_position", nullable = false)
	public SportPosition getSportPosition() {
		return sportPosition;
	}

	public void setSportPosition(SportPosition sportPosition) {
		this.sportPosition = sportPosition;
	}

	@Column(name = "born_date")
	public Date getBornDate() {
		return bornDate;
	}

	public void setBornDate(Date bornDate) {
		this.bornDate = bornDate;
	}

	@Column(name = "born_place", length=64)
	public String getBornPlace() {
		return bornPlace;
	}

	public void setBornPlace(String bornPlace) {
		this.bornPlace = bornPlace;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getLenght() {
		return lenght;
	}

	public void setLenght(Double lenght) {
		this.lenght = lenght;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_nationality", nullable = false)
	public SportNationality getSportNationality() {
		return sportNationality;
	}

	public void setSportNationality(SportNationality sportNationality) {
		this.sportNationality = sportNationality;
	}

	public boolean isComunitary() {
		return comunitary;
	}

	public void setComunitary(boolean comunitary) {
		this.comunitary = comunitary;
	}

	@Column(length = 128)
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_club", nullable = false)
	public SportClub getSportClub() {
		return sportClub;
	}

	public void setSportClub(SportClub sportClub) {
		this.sportClub = sportClub;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	
}
