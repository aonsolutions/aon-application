package net.aonsolutions.aon.in.pdf.maker.warehouse;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.registry.CompanyFull;

public class PackagingTag {
	
	private CompanyFull company;
	private byte[] logo;
	
	private List<PackagingTagDetail> details;

	public CompanyFull getCompany() {
		return company;
	}

	public PackagingTag setCompany(CompanyFull company) {
		this.company = company;
		return this;
	}

	public byte[] getLogo() {
		return logo;
	}

	public PackagingTag setLogo(byte[] logo) {
		this.logo = logo;
		return this;
	}

	public List<PackagingTagDetail> getDetails() {
		if(details == null) {
			details = new LinkedList<>();
		}
		return details;
	}
	
	public PackagingTag addDetail(PackagingTagDetail detail) {
		getDetails().add(detail);
		return this;
	}

	public PackagingTag setDetails(List<PackagingTagDetail> details) {
		this.details = details;
		return this;
	}

}
