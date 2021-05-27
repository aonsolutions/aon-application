package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Expedient;
import com.esferalia.aon.occam.api.model.ProjectFilter;

public interface IExpedient {


	public Stream<Expedient> getResumeExpedientStream(AONContext ctx, Integer domain, ProjectFilter filter);	
	public Stream<Expedient> getFullExpedientStream(AONContext ctx, Integer domain, ProjectFilter filter);

}
