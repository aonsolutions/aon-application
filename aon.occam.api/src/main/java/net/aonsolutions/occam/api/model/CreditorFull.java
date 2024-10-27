package net.aonsolutions.occam.api.model;

public class CreditorFull extends RegistryFull<Creditor> {

	private static final long serialVersionUID = -6807179702470003551L;
	
	@Override
	public CreditorFull setRegistry(Creditor registry) {
		super.setRegistry(registry);
		return this;
	}
}
