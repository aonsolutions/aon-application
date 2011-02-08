package com.code.aon.marketplace;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.marketplace.enumeration.ScaleModel;

@Entity
@Table(name="scale")
public class Scale implements ITransferObject {

	/**
	 * Id.
	 */
	private Integer id;

	/**
	 * Scale Model.
	 */
	private ScaleModel scaleModel;

	/**
	 * Program path.
	 */
	private String programPath;

	/**
	 * Initial Date.
	 */
	private Date inidate;

	/**
	 * End Date.
	 */
	private Date enddate;

	/**
	 * Verified Program path.
	 */
	private boolean verified;

	/**
	 * Serie of delivery
	 */
	private String serie;

	/**
	 * Free Field 1
	 */
	private String code1;

	/**
	 * Free Field 2
	 */
	private String code2;

	/**
	 * Free Field 3
	 */
	private String code3;

	/**
	 * Free Field 4
	 */
	private String code4;

	/**
	 * Free Field 5
	 */
	private String code5;

	/**
	 * Constructor
	 */
	public Scale() {
	}

    @Id
    @GeneratedValue
    @Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="scale_model")
	public ScaleModel getScaleModel() {
		return scaleModel;
	}

	public void setScaleModel(ScaleModel scaleModel) {
		this.scaleModel = scaleModel;
	}

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public String getCode3() {
		return code3;
	}

	public void setCode3(String code3) {
		this.code3 = code3;
	}

	public String getCode4() {
		return code4;
	}

	public void setCode4(String code4) {
		this.code4 = code4;
	}

	@Column(name="program_path")
	public String getProgramPath() {
		return programPath;
	}

	public void setProgramPath(String programPath) {
		this.programPath = programPath;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Date getInidate() {
		return inidate;
	}

	public void setInidate(Date inidate) {
		this.inidate = inidate;
	}

	@Column(length=5)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getCode5() {
		return code5;
	}

	public void setCode5(String code5) {
		this.code5 = code5;
	}
}
