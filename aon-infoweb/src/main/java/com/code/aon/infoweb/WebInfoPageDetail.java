package com.code.aon.infoweb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.infoweb.enumeration.WebInfoLayoutType;

@Entity
@Table(name="web_info_page_detail")
public class WebInfoPageDetail implements ITransferObject {
	
	private Integer id;
	
	private WebInfoPage webInfoPage;
	
	private String title;
		
	private WebInfoLayoutType layout;
	
	private String content;

	private String extra;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="web_info_page",nullable=false )
	public WebInfoPage getWebInfoPage() {
		return webInfoPage;
	}

	public void setWebInfoPage(WebInfoPage webInfoPage) {
		this.webInfoPage = webInfoPage;
	}

	@Column(name="title",length=255)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="layout")
	public WebInfoLayoutType getLayout() {
		return layout;
	}

	public void setLayout(WebInfoLayoutType layout) {
		this.layout = layout;
	}

	@Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name="extra",length=255)
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

}