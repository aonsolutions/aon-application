package solutions.aon.seg.social.objects;

public class BankData {
	private String dataType;
	private String iban;
	private String accountHolderType;
	private String holderID;
	private String holderName;
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getAccountHolderType() {
		return accountHolderType;
	}
	public void setAccountHolderType(String accountHolderType) {
		this.accountHolderType = accountHolderType;
	}
	public String getHolderID() {
		return holderID;
	}
	public void setHolderID(String holderID) {
		this.holderID = holderID;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	
	public interface Visitor{
		public void visit_dataType(String dataType);
		public void visit_IBAN(String iban);
		public void visit_accountHolderType(String accountHolderType);
		public void visit_holderID(String holderID);
		public void visit_holderName(String holderName);
	}
	
	public void accept(Visitor visitor) {
		if(this.dataType!=null) {
			visitor.visit_dataType(this.dataType);
		}
		if(this.iban!=null) {
			visitor.visit_IBAN(this.iban);
		}
		if(this.accountHolderType!=null) {
			visitor.visit_accountHolderType(this.accountHolderType);
		}
		if(this.holderID!=null) {
			visitor.visit_holderID(this.holderID);
		}
		if(this.holderName!=null) {
			visitor.visit_holderName(this.holderName);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append("Bank data: \n");
		accept(new Visitor() {
			
			@Override
			public void visit_dataType(String dataType) {
				stringBuffer.append(String.format("\tData type: \"%S\"\n", dataType));
				
			}
			
			@Override
			public void visit_holderName(String holderName) {
				stringBuffer.append(String.format("\t\tAccount holder name: \"%S\"\n", holderName));
			}
			
			@Override
			public void visit_holderID(String holderID) {
				stringBuffer.append(String.format("\t\tAccount holder ID: \"%S\"\n", holderID));
				
			}
			
			@Override
			public void visit_accountHolderType(String accountHolderType) {
				stringBuffer.append(String.format("\t\tAccount holder ID: \"%S\"\n", accountHolderType));
				
			}
			
			@Override
			public void visit_IBAN(String iban) {
				stringBuffer.append(String.format("\t\tIBAN ID: \"%S\"\n", iban));
				
			}
		});
		return stringBuffer.toString();
	}

	public static class BankDataBuilder{
		private String dataType;
		private String iban;
		private String accountHolderType;
		private String holderID;
		private String holderName;
		
		public BankDataBuilder setDataType(String dataType) {
			if(!((dataType==null)||(dataType.equals(""))))
			this.dataType = dataType;
			else
				this.dataType = null;
			return this;
		}
		public BankDataBuilder setIban(String iban) {
			if(!((iban==null)||(iban.equals(""))))
				this.iban = iban;
			else
				this.iban = null;
			return this;
		}
		public BankDataBuilder setAccountHolderType(String accountHolderType) {
			if(!((accountHolderType==null)||(accountHolderType.equals(""))))
				this.accountHolderType = accountHolderType;
			else
				this.accountHolderType = null;
			return this;
		}
		public BankDataBuilder setHolderID(String holderID) {
			if(!((holderID==null)||(holderID.equals(""))))
				this.holderID = holderID;
			else
				this.holderID = null;
			return this;
		}
		public BankDataBuilder setHolderName(String holderName) {
			if(!((holderName==null)||(holderName.equals(""))))
				this.holderName = holderName;
			else
				this.holderName = null;
			return this;
		}
		public BankData build() {
			BankData ret=new BankData();
			ret.setAccountHolderType(this.accountHolderType);
			ret.setDataType(this.dataType);
			ret.setHolderID(this.holderID);
			ret.setHolderName(this.holderName);
			ret.setIban(this.iban);
			return ret;
		}
	}
}
