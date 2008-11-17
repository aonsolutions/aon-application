package com.code.aon.infoweb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.RegistryAttachment;

@Entity
@Table(name="web_info_page_resource")
public class WebInfoPageResource implements ITransferObject {
	
	private Integer id;

	private WebInfoPage webInfoPage;

	private RegistryAttachment rattach;
	
	private String content;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="web_info_page",nullable=false )
	public WebInfoPage getWebInfoPage() {
		return webInfoPage;
	}

	public void setWebInfoPage(WebInfoPage webInfoPage) {
		this.webInfoPage = webInfoPage;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="rattach",nullable=false )
	public RegistryAttachment getRattach() {
		return rattach;
	}

	public void setRattach(RegistryAttachment rattach) {
		this.rattach = rattach;
	}

	@Column(name="content",	length=255)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}