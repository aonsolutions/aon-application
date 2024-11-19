package net.aonsolutions.occam.api.model;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;


public class CompanyFull extends RegistryFull<Company> {

	private static final long serialVersionUID = 2481288189326103898L;
	
	private LinkedList<RegistryDirStaff> dirStaff;
	
	@Override
	public CompanyFull setRegistry(Company registry) {
		super.setRegistry(registry);
		return this;
	}
	
	// ------------------------------------------ REGISTRY DIR STAFF
	public Stream<RegistryDirStaff> dirStaffStream() {
		return AonCollectionUtils.stream(dirStaff);
	}
	public CompanyFull addDirStaff(RegistryDirStaff rDirStaff) {
		if (this.dirStaff == null) this.dirStaff = new LinkedList<>(); 
		this.dirStaff.add(rDirStaff);
		return this;
	}
}
