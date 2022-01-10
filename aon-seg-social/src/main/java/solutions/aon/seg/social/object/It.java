package solutions.aon.seg.social.object;


import java.lang.reflect.Field;
import java.util.ArrayList;

public class It {
	private ITPart start;
	private ITPart end;
	private ArrayList<ITPart> confirmations;
	private It() {}
	
	public ITPart getStart() {
		return start;
	}
	
	public ITPart getEnd() {
		return end;
	}
	
	public ArrayList<ITPart> getConfirmations() {
		return confirmations;
	}
	
	
	public void accept(Visitor visitor) {
		if(start != null) visitor.visitStart(start);
		if(end != null) visitor.visitEnd(end);
		if(confirmations != null) visitor.visitConfirmations(confirmations);
	}
	
	public static interface Visitor{		
		void visitStart(ITPart start);
		void visitEnd(ITPart end);
		void visitConfirmations(ArrayList<ITPart> confirmations);
	}
		
	public String toString() {
	    for (Field field : getClass().getDeclaredFields()) {
	        field.setAccessible(true);   
	        try {
				return field.getName() + " = " + field.get(this) + ", ";
			} catch (Exception e) {}
	    }
		return null;
	}
	
	public static class ItBuilder {
				
		private ITPart start;
		private ITPart end;
		private ArrayList<ITPart> confirmations;
		
		public ItBuilder setStart(ITPart start) {
			this.start = start;
			return this;
		}
		
		public ItBuilder setEnd(ITPart end) {
			this.end = end;
			return this;
		}
		
		public ItBuilder setConfirmations(ArrayList<ITPart> confirmations) {
			this.confirmations = confirmations;
			return this;
		}
		
		public It build() {
			It it = new It();
			it.confirmations = this.confirmations;
			it.start = this.start;
			it.end = this.end;
			return it;
		}
		
	}
}

