package aon.sepe.objects;

public class Certificates {

	private String regimen;
	private String ctaCti;
	private String ipf;
	
	private Certificates() {}
	
	public static class CertificatesBuilder {
		public CertificatesBuilder() {}
		
		public Certificates build() {
			Certificates certificates = new Certificates();
			
			return certificates;
		}
	}
}
