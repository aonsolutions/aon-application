package net.aonsolutions.occam.api.model;

public class CustomerFull extends RegistryFull<Customer> {

	private static final long serialVersionUID = 3114569282696003781L;

	@Override
	public CustomerFull setRegistry(Customer registry) {
		super.setRegistry(registry);
		return this;
	}
}
