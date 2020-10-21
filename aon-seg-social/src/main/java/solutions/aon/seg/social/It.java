package solutions.aon.seg.social;

import java.util.Arrays;
import java.util.List;

import com.sun.tools.javac.code.Attribute.Array;

import solutions.aon.seg.social.ITPart.Visitor;

public class It {
	private ITPart start;
	private ITPart end;
	private ITPart[] confirmations;
	private It() {}
	
	public void accept(Visitor visitor) {
		if(start != null) visitor.visitStart(start);
		if(end != null) visitor.visitEnd(end);
		if(confirmations != null) visitor.visitConfirmations(confirmations);
	}
	
	public static interface Visitor{		
		void visitStart(ITPart start);
		void visitEnd(ITPart end);
		void visitConfirmations(ITPart[] confirmations);
	}
	
	
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		accept(new Visitor() {
			
			@Override
			public void visitStart(ITPart start) {
				stringBuffer.append(String.format(" start : \"%s\" ", start));
			}
			
			@Override
			public void visitEnd(ITPart end) {
				stringBuffer.append(String.format(" end : \"%s\" ", end));
			}
			
			@Override
			public void visitConfirmations(ITPart[] confirmations) {
				stringBuffer.append('[');
					for(ITPart part : confirmations) 
						if(part != null) 
							stringBuffer.append(String.format(" end : \"%s\" ", part.toString()));
				stringBuffer.append(']');
			}
		});
		
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	public static class ItBuilder {
				
		private ITPart start;
		private ITPart end;
		private ITPart[] confirmations;
		
		private void ItBuilder() {}
		
		private ItBuilder setStart(ITPart start) {
			this.start = start;
			return this;
		}
		
		private ItBuilder setEnd(ITPart end) {
			this.end = end;
			return this;
		}
		
		private ItBuilder setConfirmations(ITPart[] confirmations) {
			this.confirmations = confirmations;
			return this;
		}
		
		public It build() {
			It it = new It();
			
			it.confirmations = this.confirmations;
			it.end = this.end;
			it.start = this.start;
			
			return it;
		}
		
	}
	
	
	public static void main(String[] args) {
		
		ItBuilder itB = new ItBuilder();
		It it = itB.setStart(null)
				.setEnd(null)
				.setConfirmations(new ITPart[]{null,null,null})
				.build();
		System.out.println(it);
	}
	

}
