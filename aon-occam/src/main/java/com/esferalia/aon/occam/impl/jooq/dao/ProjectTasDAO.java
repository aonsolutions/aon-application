package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Make.MAKE;
import static com.esferalia.aon.jooq.tables.Model.MODEL;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectTas.PROJECT_TAS;
import static com.esferalia.aon.jooq.tables.ProjectType.PROJECT_TYPE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TasItem.TAS_ITEM;

import java.sql.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.ProjectTasFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.ProjectTasProperties;
import com.esferalia.aon.occam.api.model.Status;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.occam.api.model.project.TasItem;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO.ProjectFiller;

public class ProjectTasDAO {
	
	private ProjectTasDAO() {}
	
	private static final ProjectTasPropertiesDAO PROJECT_TAS_PROPERTIES = new ProjectTasPropertiesDAO();
	
	protected static class ProjectTasPropertiesDAO implements ProjectTasProperties {
		protected Condition[] getConditions(ProjectTasFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ACTIVE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ALIAS);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.COMMERCIAL);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.DATE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.NAME);}
		@Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.PROJECT_TYPE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.REGISTRY);}
		@Override public Property<Byte> getReservationProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.RESERVATION);}
		@Override public Property<Byte> getTasProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.TAS);}
		
		
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getTypeDescriptionProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.DESCRIPTION);}
		
		@Override public Property<Integer> getIdTasProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.PROJECT); }
		@Override public Property<String> getSeriesProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.SERIES); }
		@Override public Property<Integer> getNumberProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.NUMBER); }
		@Override public Property<Integer> getTargetProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.TARGET); }
		@Override public Property<Integer> getTasItemProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.TAS_ITEM); }
		@Override public Property<Double> getCounterProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.COUNTER); }
		@Override public Property<Integer> getTaskHolderProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.TASK_HOLDER); }
		@Override public Property<Byte> getStatusProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.STATUS); }
		@Override public Property<Date> getStatusDateProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.STATUS_DATE); }
		@Override public Property<Integer> getWorkplaceProperty() { return new FilterDAO.PropertyDAO<>(PROJECT_TAS.WORKPLACE); }
	}

	private static SelectConditionStep<Record> select(AONContext ctx, ProjectTasFilter filter) {
		
		System.out.println(
				ctx.getDslContext().select()
				.from(PROJECT_TAS)
				.join(DOMAIN).on(PROJECT_TAS.DOMAIN.eq(DOMAIN.ID))
				.join(PROJECT).on(PROJECT.ID.eq(PROJECT_TAS.PROJECT))
				.join(REGISTRY).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(PROJECT_TYPE).on(PROJECT.PROJECT_TYPE.eq(PROJECT_TYPE.ID))
				.leftOuterJoin(TAS_ITEM).on(TAS_ITEM.ID.eq(PROJECT_TAS.TAS_ITEM))
				.leftOuterJoin(MODEL).on(MODEL.ID.eq(TAS_ITEM.MODEL))
				.leftOuterJoin(MAKE).on(MAKE.ID.eq(MODEL.MAKE))
				.where(PROJECT_TAS_PROPERTIES.getConditions(filter)).getSQL(ParamType.INLINED)
		);
		
		return 
			ctx.getDslContext().select()
				.from(PROJECT_TAS)
				.join(DOMAIN).on(PROJECT_TAS.DOMAIN.eq(DOMAIN.ID))
				.join(PROJECT).on(PROJECT.ID.eq(PROJECT_TAS.PROJECT))
				.join(REGISTRY).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(PROJECT_TYPE).on(PROJECT.PROJECT_TYPE.eq(PROJECT_TYPE.ID))
				.leftOuterJoin(TAS_ITEM).on(TAS_ITEM.ID.eq(PROJECT_TAS.TAS_ITEM))
				.leftOuterJoin(MODEL).on(MODEL.ID.eq(TAS_ITEM.MODEL))
				.leftOuterJoin(MAKE).on(MAKE.ID.eq(MODEL.MAKE))
				.where(PROJECT_TAS_PROPERTIES.getConditions(filter));
	}
	
	public static ProjectTas get(AONContext ctx, ProjectTasFilter filter){
		return select(ctx, filter)
				.limit(1)
				.fetch()
				.stream()
				.map(r -> {
					ProjectTas pt = ProjectTasFiller.build(r);
					
					pt.setTarget(TargetDAO.get(ctx, r.get(PROJECT_TAS.TARGET)));
					pt.setTaskHolder(TaskHolderDAO.get(ctx, r.get(PROJECT_TAS.TASK_HOLDER)));
					pt.setWorkplace(WorkplaceDAO.get(ctx, f -> f.getIdProperty().eq(r.getValue(PROJECT_TAS.WORKPLACE)), new Options().setSecurity(false)));
					
					return pt;
				})
				.findFirst()
				.orElse(new ProjectTas());
	}
	
	public static Stream<ProjectTas> getStream(AONContext ctx, ProjectTasFilter filter){
		return select(ctx, filter)
				.fetch()
				.stream()
				.map(r -> {
					ProjectTas pt = ProjectTasFiller.build(r);
					
					pt.setTarget(TargetDAO.get(ctx, r.get(PROJECT_TAS.TARGET)));
					pt.setTaskHolder(TaskHolderDAO.get(ctx, r.get(PROJECT_TAS.TASK_HOLDER)));
					pt.setWorkplace(WorkplaceDAO.get(ctx, f -> f.getIdProperty().eq(r.getValue(PROJECT_TAS.WORKPLACE)), new Options().setSecurity(false)));
					
					return pt;
				});
	}
	
	public static class ProjectTasFiller extends Filler implements Function<Record, ProjectTas> {
		
		@Override
		public ProjectTas apply(Record r) {
			return build(r);
		}
		
		public static ProjectTas build(Record r) {
			
			ProjectTas projectTas = new ProjectTas();
			projectTas.copyFrom(ProjectFiller.build(r));
			
			projectTas.setSeries(r.getValue(PROJECT_TAS.SERIES));
			projectTas.setNumber(r.getValue(PROJECT_TAS.NUMBER));
			projectTas.setCounter(r.getValue(PROJECT_TAS.COUNTER));
			projectTas.setComments(r.getValue(PROJECT_TAS.COMMENTS));
			projectTas.setStatus(Status.safeValueOf(r.getValue(PROJECT_TAS.STATUS)));
			projectTas.setStatusDate(r.getValue(PROJECT_TAS.STATUS_DATE));
			
			TasItem tasItem = new TasItem();
			tasItem.setDomain(r.getValue(TAS_ITEM.DOMAIN));
			tasItem.setPublicCode(r.getValue(TAS_ITEM.PUBLICCODE));
			tasItem.setPrivateCode(r.getValue(TAS_ITEM.PRIVATECODE));
			tasItem.setDescription(r.getValue(TAS_ITEM.DESCRIPTION));
			tasItem.setMakeName(r.getValue(MAKE.NAME));
			tasItem.setModelName(r.getValue(MODEL.NAME));
			
			projectTas.setTasItem(tasItem);
			
			return projectTas;		
		}

	}
	
}
