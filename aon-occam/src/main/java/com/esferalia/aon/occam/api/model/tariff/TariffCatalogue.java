package com.esferalia.aon.occam.api.model.tariff;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.catalogue.Catalogue;

public class TariffCatalogue implements Serializable {
	
	private static final long serialVersionUID = 1699629262657144964L;
	
	private Integer id;
	private Integer domain;
	private Tariff tariff;
	private Catalogue catalogue;

	public Integer getId() {
		return id;
	}
	public TariffCatalogue setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public TariffCatalogue setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Tariff getTariff() {
		return tariff;
	}
	public TariffCatalogue setTariff(Tariff tariff) {
		this.tariff = tariff;
		return this;
	}
	public Catalogue getCatalogue() {
		return catalogue;
	}
	public TariffCatalogue setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
		return this;
	}
	
}
