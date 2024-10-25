package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.occam.api.model.type.AppParam;

public class ApplicationParameter implements Serializable {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private AppParam param;
	private String value;

	public Integer getId() {
		return id;
	}
	public ApplicationParameter setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ApplicationParameter setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public AppParam getParam() {
		return param;
	}

	public ApplicationParameter setParam(AppParam param) {
		this.param = param;
		return this;
	}

	public String getValue() {
		return value;
	}
	public ApplicationParameter setValue(String value) {
		this.value = value;
		return this;
	}
	public ApplicationParameter setValue(boolean b) {
		return setValue(b?Boolean.TRUE.toString():Boolean.FALSE.toString());
	}
	public ApplicationParameter setValue(Integer number) {
		return setValue(AonNumberUtils.toString(number));
	}

}