package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;
import com.esferalia.aon.occam.api.model.office.Tag;

@SuppressWarnings("serial")
public class RattachTag implements Serializable{
	
	Integer id;
	Integer domain;
	Integer rattach;
	Tag Tag;
	
	public Integer getId() {
		return id;
	}
	public RattachTag setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public RattachTag setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRattach() {
		return rattach;
	}
	public RattachTag setRattach(Integer rattach) {
		this.rattach = rattach;
		return this;
	}
	public Tag getTag() {
		return Tag;
	}
	public RattachTag setTag(Tag tag) {
		Tag = tag;
		return this;
	}
}
