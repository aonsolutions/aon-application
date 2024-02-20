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

	public void setCompany(CompanyFull company) {
		this.company = company;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
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

	public void setDetails(List<PackagingTagDetail> details) {
		this.details = details;
	}

}
