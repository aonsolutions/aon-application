package net.aonsolutions.occam.api.model;

public class SupplierFull extends RegistryFull<Supplier> {

	private static final long serialVersionUID = 3114569282696003781L;

	@Override
	public SupplierFull setRegistry(Supplier registry) {
		super.setRegistry(registry);
		return this;
	}
}
