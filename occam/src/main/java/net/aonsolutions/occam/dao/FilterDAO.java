package net.aonsolutions.occam.dao;

import org.jooq.Condition;

import net.aonsolutions.occam.api.Filter;

public class FilterDAO implements Filter {
	
	private static final long serialVersionUID = -5253820507002269214L;

	private Condition condition;
	
	public FilterDAO(Condition condition) {
		this.condition = condition;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public Filter or(Filter filter) {
		return (filter == null)?this:new FilterDAO(condition.or(((FilterDAO)filter).condition));	
	}

	@Override
	public Filter and(Filter filter) {
		return (filter == null)?this:new FilterDAO(condition.and(((FilterDAO)filter).condition));
	}
	
}
