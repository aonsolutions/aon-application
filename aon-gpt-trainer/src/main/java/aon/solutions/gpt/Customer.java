package aon.solutions.gpt;

import java.util.Optional;

public class Customer {
	
	public static class CustomerFilter {
		Optional<String> dni;
		Optional<String> cif;
		
		Optional<String> doc;
		Optional<String> docType;
		
		Optional<String> name;

		public Optional<String> getDni() {
			return dni;
		}

		public Optional<String> getCif() {
			return cif;
		}

		public Optional<String> getDoc() {
			return doc;
		}

		public Optional<String> getDocType() {
			return docType;
		}

		public Optional<String> getName() {
			return name;
		}
		
		
		public CustomerFilter withDni(String dni) {
			this.dni = Optional.ofNullable(dni);
			return this;
		}
		
		public CustomerFilter withCif(String cif) {
			this.cif = Optional.ofNullable(cif);
			return this;
		}
		
	}
	
	public static <T> T getCustomers() {
		return null;
	}

}
