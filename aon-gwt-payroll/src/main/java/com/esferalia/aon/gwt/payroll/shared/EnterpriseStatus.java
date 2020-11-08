package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public abstract class EnterpriseStatus implements Serializable {

	public static interface Visitor  {
		void up2Date();
		void forbidden();
		void saltraCredentialsNotFound();
		void affiliatedAtTrash(AffiliatedAtTrash affiliatedAtTrash);
		void affiliatedNotFound(AffiliatedNotFound affiliatedNotFound);
	}
	
	public static class Forbidden extends EnterpriseStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.forbidden();
		}
	}
	
	public static class CredentialsNotFound extends EnterpriseStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.saltraCredentialsNotFound();
		}
	}	
	
	public static class AndEnterpriseStatus extends EnterpriseStatus {
		
		private AndEnterpriseStatus next;
		
		@Override
		public void visit(Visitor visitor) {
			if ( next != null ) {
				next.visit(visitor);
			}
		}
		
		public AndEnterpriseStatus and(AndEnterpriseStatus status) {

			AndEnterpriseStatus last = this;
			while ( last.next != null ) 
				last = last.next;
			
			last.next = status;
			
			return this;
		}
	}
	
	public static class Up2Date extends AndEnterpriseStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.up2Date();
			super.visit(visitor);
		}
	}		
	
	public static class AffiliatedNotFound extends AndEnterpriseStatus{
		
		Date date ;
		String naf;
		String dni;
		String ccc;
		String name;
		String regime;
		
		public String getDni() {
			return dni;
		}
		
		public String getNaf() {
			return naf;
		}
		
		public String getName() {
			return name;
		}
		
		public Date getDate() {
			return date;
		}
		
		public String getCcc() {
			return ccc;
		}
		
		public String getRegime() {
			return regime;
		}
		
		public AffiliatedNotFound setDate(Date date) {
			this.date = date;
			return this;
		}
		
		public AffiliatedNotFound setNaf(String naf) {
			this.naf = naf;
			return this;
		}
		
		public AffiliatedNotFound setDni(String dni) {
			this.dni = dni;
			return this;
		}
		
		public AffiliatedNotFound setCcc(String ccc) {
			this.ccc = ccc;
			return this;
		}
		
		public AffiliatedNotFound setName(String name) {
			this.name = name;
			return this;
		}
		
		public AffiliatedNotFound setRegime(String regime) {
			this.regime = regime;
			return this;
		}
		
		
		@Override
		public void visit(Visitor visitor) {
			visitor.affiliatedNotFound(this);
			super.visit(visitor);
		}
		
		protected void super_visit( Visitor visitor) {
			super.visit(visitor);
		}
	}		
	
	public static class AffiliatedAtTrash extends AffiliatedNotFound {
		
		Integer id; 
		
		@Override
		public void visit(Visitor visitor) {
			visitor.affiliatedAtTrash(this);
			super_visit(visitor);
		}	
		
		public Integer getId() {
			return id;
		}
		
		public AffiliatedAtTrash setId(Integer id) {
			this.id = id;
			return this;
		}
		
	}

	public abstract void visit(Visitor visitor);
	
	
	public static void ifSistemaREDEnabled(EnterpriseStatus enterpriseStatus, Runnable saltraEnable,
			Runnable saltraDisabled) {
		enterpriseStatus.visit(new Visitor() {
	
			@Override
			public void up2Date() {
				saltraEnable.run();
			}
	
			@Override
			public void forbidden() {
				saltraDisabled.run();
			}
	
			@Override
			public void saltraCredentialsNotFound() {
				saltraDisabled.run();
			}
	
			
			@Override
			public void affiliatedAtTrash(AffiliatedAtTrash affiliatedAtTrash) {
				saltraEnable.run();
			}
			
			@Override
			public void affiliatedNotFound(AffiliatedNotFound affiliatedNotFound) {
				saltraEnable.run();
			}
			
			
		});
	}


	public static <T extends EnterpriseStatus> T trace( T status ) {
		status.visit(new Visitor() {
			
			@Override
			public void up2Date() {
				System.out.println("up2Date");
			}
			
			@Override
			public void forbidden() {
				System.out.println("forbidden");
			}
			
			@Override
			public void saltraCredentialsNotFound() {
				System.out.println("saltraCredentialsNotFound");
			}
			
			@Override
			public void affiliatedAtTrash(AffiliatedAtTrash affiliatedAtTrash) {
				System.out.println("affiliatedAtTrash " + affiliatedAtTrash.name + "[" + affiliatedAtTrash.date + "]");
			}

			@Override
			public void affiliatedNotFound(AffiliatedNotFound affiliatedNotFound) {
				System.out.println("affiliatedNotFound " + affiliatedNotFound.name + "[" + affiliatedNotFound.date + "]");
			}
			
		});
		return status;
	}

	public static <T extends EnterpriseStatus> T isUp2Date( T status ) {
		status.visit(new Visitor() {
			
			@Override
			public void up2Date() {
			}
			
			@Override
			public void forbidden() {
				throw new OutOfDateException();
			}
			
			@Override
			public void saltraCredentialsNotFound() {
				throw new OutOfDateException();
			}

			@Override
			public void affiliatedAtTrash(AffiliatedAtTrash affiliatedAtTrash) {
				throw new OutOfDateException();
			}

			@Override
			public void affiliatedNotFound(AffiliatedNotFound affiliatedNotFound) {
				throw new OutOfDateException();
			}
			
		});
		return status;
	}
}
