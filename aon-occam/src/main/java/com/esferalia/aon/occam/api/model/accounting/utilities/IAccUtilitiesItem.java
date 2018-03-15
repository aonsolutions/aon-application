package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.io.Serializable;

public interface IAccUtilitiesItem extends Serializable {
	public AccUtilitiesItemType getType();
	public String getMessage();
	public Integer getDomain();
	public String getDomainName();
	
	public static interface IAccUtilitiesItemTypeVisitor {
		void visitOther(AccUtilitiesItemType type);
		void visitErrorMessage(AccUtilitiesItemType type);
		void visitInfoMessage(AccUtilitiesItemType type);
		void visitParentAccountLinker(AccUtilitiesItemType type);
		void visitEmptyEntry(AccUtilitiesItemType type);
		void visitUnbalancedEntry(AccUtilitiesItemType type);
	}

	public static enum AccUtilitiesItemType {
		 ERROR_MESSAGE{
			@Override
			public void visit(IAccUtilitiesItemTypeVisitor visitor) {
				visitor.visitErrorMessage(this);
			}
		 }
		 ,INFO_MESSAGE{
			@Override
			public void visit(IAccUtilitiesItemTypeVisitor visitor) {
				visitor.visitInfoMessage(this);
			}
		 }
		,EMPTY_ENTRY{
			@Override
			public void visit(IAccUtilitiesItemTypeVisitor visitor) {
				visitor.visitEmptyEntry(this);		
			}
		}
		,UNBALANCED_ENTRY{
			@Override
			public void visit(IAccUtilitiesItemTypeVisitor visitor) {
				visitor.visitUnbalancedEntry(this);		
			}
		}
		,PARENT_ACCOUNT_LINKER{
			@Override
			public void visit(IAccUtilitiesItemTypeVisitor visitor) {
				visitor.visitParentAccountLinker(this);
			}
		 }
		;
		
		public void visit(IAccUtilitiesItemTypeVisitor visitor) {
			visitor.visitOther(this);
		}
	}
	
}
