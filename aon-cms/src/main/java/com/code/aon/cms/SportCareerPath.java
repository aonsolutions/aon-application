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

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "sport_career_path")
public class SportCareerPath implements ITransferObject {

	private Integer id;
	
	private SportPlayer sportPlayer;
	
	private String club;

	private Date initDate;
	
	private Date endDate;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_player", nullable = false)
	public SportPlayer getSportPlayer() {
		return sportPlayer;
	}

	public void setSportPlayer(SportPlayer sportPlayer) {
		this.sportPlayer = sportPlayer;
	}

	@Column(name = "init_date")
	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "club", nullable = false)
	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	
}
