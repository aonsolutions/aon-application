package com.esferalia.aon.gwt.template.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RegistryAttachTag implements IsSerializable{
	private Integer id;
	private Integer tag;
	private Integer rattach;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTag() {
		return tag;
	}
	public void setTag(Integer tag) {
		this.tag = tag;
	}
	public Integer getRattach() {
		return rattach;
	}
	public void setRattach(Integer rattach) {
		this.rattach = rattach;
	}
	
	
}
