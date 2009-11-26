package com.code.aon.cms;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="bulletin")
public class Bulletin implements ITransferObject {

	private Integer id;
	
	private String alias;
	
	private Date publish_date;
	
	private String template;
	
	private Set<BulletinDetail> details;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=64)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(nullable=false)
	public Date getPublish_date() {
		return publish_date;
	}

	public void setPublish_date(Date publish_date) {
		this.publish_date = publish_date;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@OneToMany(mappedBy = "bulletin", cascade={CascadeType.REMOVE})
	public Set<BulletinDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<BulletinDetail> details) {
		this.details = details;
	}
	

}