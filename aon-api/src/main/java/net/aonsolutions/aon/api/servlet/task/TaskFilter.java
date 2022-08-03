package net.aonsolutions.aon.api.servlet.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskWorkflowProperties;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

public class TaskFilter {
	
	private static final Byte[] PENDING = Arrays.asList(TaskStatus.PENDING.value(), TaskStatus.IN_PROGRESS.value()).toArray(Byte[]::new);
	
	private TaskFilter() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static Filter task(AonApiData api, TaskProperties f, Domain domain, Customer customer) {
		JSONObject params  = api.getData();
		String email       = params.optString(IJsonNames.EMAIL);
		String search      = params.optString(IJsonNames.SEARCH);
		String status      = params.optString(IJsonNames.STATUS);
		String source      = params.optString(IJsonNames.SOURCE);
		Integer taskHolder = params.optInt(IJsonNames.TASK_HOLDER);
		Integer tag        = params.optInt(IJsonNames.TAG);
		boolean isParent = params.optBoolean(IJsonNames.PARENT);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(!status.isEmpty() && !TaskStatus.safeValueOf(status).equals(TaskStatus.PENDING) ) {
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
		} else {	
			filter = filter.and(f.getStatusProperty().in(PENDING));
		}
		
		if(isParent) {
			filter = filter.and(f.getParentProperty().isNull());
			return filter;
		}
		
//		if("pending".equalsIgnoreCase(status) || ( status.isEmpty() && customer!=null && customer.getId()!=null) ) 
//			filter = filter.and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
//		else if(!status.isEmpty())
//			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
		
		if(tag!=0) {
			filter = filter.and(f.getTagIdProperty().eq(tag));
		}
			
		if(!search.isEmpty()) {
			filter = filter.and(getSearchCombination(f, search));
		}

		if(!source.isEmpty()) {			
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		}

		if(TaskUtils.isCau(api.getData())) {
			filter = filter.and(getFilterCau(api, f));
		} else if(customer!=null && customer.getId()!=null) { //CUSTOMER
			
			Integer workgroup = params.optInt(IJsonNames.WORKGROUP);
			
			Integer sender = params.optInt(IJsonNames.SENDER);
			
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
			
			if(workgroup !=0) {
				filter = filter.and(f.getWorkgroupProperty().eq(workgroup));
			} else {

				if(taskHolder!=0 && sender!=0) {
				} else if(taskHolder!=0) { //----------RECIBIDAS
					filter = filter.and( f.getSenderProperty().isNotNull());
				} else if(sender!=0) {//----------ENVIADAS
					filter = filter.and(f.getSenderProperty().isNull().or(f.getGtaskIdProperty().eq(email)) );
				}
			}
		} else {
			filter = filter.and(taskNotCustomer(api, f, domain));
		}
		
		return filter;
	}	
	
	public static Filter taskStatusCount(TaskProperties f, AonApiData api, Domain domain, Customer customer) {
		return taskNotCustomer(api, f, domain);
	}
	
	public static Filter taskWorkgroupCount(TaskProperties f, AonApiData api, Domain domain) {
		JSONObject params = api.getData();
		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().in(PENDING));
		
		if(TaskUtils.isCau(params)) {
			filter = filter.and(getFilterCau(api, f));
		} else {
			List<Integer> list = getWorkgroupList(params.optString(IJsonNames.WORKGROUPS));
		
			if(!list.isEmpty()) {
				filter = filter.and(
					f.getWorkgroupProperty().in(list.toArray(Integer[]::new))
					.or(f.getWorkgroupProperty().isNull().and(f.getTaskHolderProperty().isNull()) )
				);
			} 
		}

		return filter;
	}
	
	public static Filter taskTagCount(TaskProperties f, AonApiData api, Domain domain) {
		JSONObject params  = api.getData();
		String source      = params.optString(IJsonNames.SOURCE);
		String tagStr      = params.optString(IJsonNames.TAG);
		Integer taskHolder = params.optInt(IJsonNames.TASK_HOLDER);

		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().in(PENDING));
		
		if(!source.isEmpty()) {			
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		}
		
		if(!tagStr.isEmpty()) {
			String[]  str = tagStr.split(",");
			Integer[] arr = new Integer[str.length];
			for(int i=0; i<str.length; i++) {
				arr[i] = Integer.parseInt(str[i]);
			}
			
			if(TaskUtils.isCau(params)) {
				filter = filter.and(f.getTagIdProperty().in(arr).and(getFilterCau(api, f)));
			} else if(api.getDur().isMessengerManager()) {
				filter = filter.and(f.getTagIdProperty().in(arr));
			} else {
				filter = filter.and(
						f.getTagIdProperty().in(arr)
						.and(
							f.getTaskHolderProperty().eq(taskHolder)
							.or(f.getTaskHolderProperty().isNull())
						)
						.or(f.getSenderProperty().eq(taskHolder))
				);
			}
		} else {
			filter = filter.and(f.getTagIdProperty().eq(0));
		}

		return filter;
	}
	
	public static Filter taskSenderCount(AonApiData api, Domain domain,  TaskProperties f, Customer customer) {
		JSONObject params  = api.getData();
		Integer taskHolder = params.optInt(IJsonNames.TASK_HOLDER);
		String email       = params.optString(IJsonNames.EMAIL);

		boolean isCau = TaskUtils.isCau(params);
	
		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().in(PENDING));
		
		if(isCau) {
			filter = filter.and( f.getGtaskIdProperty().eq(email) );
		} else if(customer.getId()!=null) {
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()))
					.and(f.getSenderProperty().isNull().or(f.getGtaskIdProperty().eq(email)) );	
		} else if(taskHolder!=0) {
			filter = filter.and(
				f.getSenderProperty().eq(taskHolder)
				.and(
					f.getTaskHolderProperty().ne(taskHolder).or(f.getTaskHolderProperty().isNull())
				)
			);
		} 

		return filter;
	}
	
	public static Filter taskReceiverCount(AonApiData api, Domain domain,  TaskProperties f, Customer customer) {
		JSONObject params   = api.getData();
		Integer taskHolder  = params.optInt(IJsonNames.TASK_HOLDER);
		String workgroupStr = params.optString(IJsonNames.WORKGROUPS);
		String email        =  params.optString(IJsonNames.EMAIL);
		
		boolean isCau = TaskUtils.isCau(params);
		
		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().in(PENDING));
		
		if(isCau) {
			filter = filter.and( f.getGtaskIdProperty().eq(email) );
		} else if(customer.getId()!=null) {
			filter = filter.and(f.getRegistryProperty().eq(customer.getId())).and(f.getSenderProperty().isNotNull());	
		} else {
			if(taskHolder==0) {
				try {
					TaskHolder tmp = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
							t->t.getUserIdProperty().eq(api.getUser().getId())
					);
					if(tmp.getId()!=null) {
						taskHolder = tmp.getId();
					}
				} catch (Exception e) {e.printStackTrace();}
			}
	
			List<Integer> list = new ArrayList<>();
			if(workgroupStr.isEmpty()) {
				if( api.getDur().isMessengerManager()) {
					AON.getWorkgroupStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
						t-> t.getDomainProperty().eq(domain.getId()).and(t.getStatusProperty().eq(WorkgroupStatus.ACTIVE.value()))
					)
					.forEach(w-> list.add(w.getId()));
				} else {
					AON.getWorkgroupByTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(),
						t-> t.getDomainProperty().eq(domain.getId()).and(t.getStatusProperty().eq(WorkgroupStatus.ACTIVE.value())),
						taskHolder)
					.forEach(w-> list.add(w.getId()));
				}
		
			} else {
				list.addAll(getWorkgroupList(workgroupStr));
			}

			if(!list.isEmpty()) {
				filter = filter.and(
					f.getWorkgroupProperty().in(list.toArray(Integer[]::new))
					.and(
						f.getTaskHolderProperty().eq(taskHolder)
						.or(f.getTaskHolderProperty().isNull())
					)
				);
			} else {
				 filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));
			}
		}
		
		return filter;
	}
	
	public static Filter workflow(AonApiData api, TaskWorkflowProperties f) {
		JSONObject params = api.getData();
		Integer task      = params.optInt(IJsonNames.TASK);
		Integer parent    = params.optInt(IJsonNames.PARENT);
		
		Filter filter = f.getTaskProperty().eq(task);

		if(TaskUtils.isCau(params)) {
			String email = params.optString(IJsonNames.EMAIL);
			
			List<Byte> types = new ArrayList<>(Arrays.asList(TaskWorkflowType.OPEN.value(), TaskWorkflowType.CLOSE.value(), TaskWorkflowType.CONNECTED.value()));

			filter = filter.and(
				f.getEmailProperty().eq(email)
				.or(f.getNotificationUserProperty().isNotNull())
				.or(f.getTypeProperty().in(types.toArray(Byte[]::new)))
			);
		} else if(parent!=0) {
			filter = filter.or(f.getTaskProperty().eq(parent));
		}
		
		return filter;
	}
	
	public static Filter tags(TagProperties f, AonApiData api, Domain domain) {
		JSONObject params = api.getData();
		String type       = params.optString(IJsonNames.TYPE);
		String tagStr     = params.optString(IJsonNames.TAG);
		
		Filter filter     = f.getDomainProperty().eq(domain.getId());
		
		if(!tagStr.isEmpty()) {
			String[] arr = tagStr.split(",");
			filter = filter.and(f.getNameProperty().in(arr));
		}  
		
		if(!type.isEmpty()) {
			filter = filter.and(f.getTypeProperty().eq(TagType.safeValueOf(type).value()));
		}

		return filter;
	}
	
	private static Filter taskNotCustomer(AonApiData api, TaskProperties f, Domain domain) {
		JSONObject params        = api.getData();
		Integer registry         = params.optInt(IJsonNames.REGISTRY);
		Integer workgroup        = params.optInt(IJsonNames.WORKGROUP);
		String workgroupStr      = params.optString(IJsonNames.WORKGROUPS);
		Integer taskHolder       = params.optInt(IJsonNames.TASK_HOLDER);
		Integer sender           = params.optInt(IJsonNames.SENDER);
		Integer searchTaskHolder = params.optInt(IJsonNames.SEARCH+IJsonNames.TASK_HOLDER);
		Integer searchSender     = params.optInt(IJsonNames.SEARCH+IJsonNames.SENDER);

		List<Integer> workgroupList = getWorkgroupList(workgroupStr);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(!params.optString("startDate").isEmpty()) {
			Date startDate = AonDateUtils.parse(params.optString("startDate"), "yyyy-MM-dd");
			filter = filter.and(f.getStartDateProperty().eq(AonDateUtils.toTimestamp(startDate)));
		}

		if(registry!=0) {			
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}
		
		if(searchTaskHolder!=0) {			
			filter = filter.and(f.getTaskHolderProperty().eq(searchTaskHolder));
		}
		
		if(searchSender!=0) {			
			filter = filter.and(f.getSenderProperty().eq(searchSender));
		}
		
		if(!workgroupList.isEmpty() && workgroupStr.contains("all")) {
			filter = filter.and(f.getWorkgroupProperty().in(workgroupList.toArray(Integer[]::new)));
		} else if(workgroup!=0) {
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));
		} else if(params.optBoolean(IJsonNames.WORKGROUP)) {//TRUE = ALL
			filter = filter.and(f.getWorkgroupProperty().isNull()).and(f.getTaskHolderProperty().isNull());
		} else if(!workgroupList.isEmpty()) {

			 if(taskHolder!=0 && sender!=0){
				filter = filter.and(
					f.getWorkgroupProperty().in(workgroupList.toArray(Integer[]::new))
					.and(
						f.getTaskHolderProperty().eq(taskHolder)
						.or(f.getTaskHolderProperty().isNull())
					)
					.or(f.getSenderProperty().eq(sender))
				);
			} else if(taskHolder!=0) {
				filter = filter.and(
					f.getWorkgroupProperty().in(workgroupList.toArray(Integer[]::new))
					.and(
						f.getTaskHolderProperty().eq(taskHolder)
						.or(f.getTaskHolderProperty().isNull())
					)
				);
			} else if(sender!=0) {
				filter = filter.and(f.getSenderProperty().eq(sender)
					.and(
						f.getTaskHolderProperty().ne(sender).or(f.getTaskHolderProperty().isNull())
					)
				);
			} else {
				filter = filter.and(f.getWorkgroupProperty().in(workgroupList.toArray(Integer[]::new)));
			} 
		} else {

			if(taskHolder!=0 && sender!=0){
				filter = filter.and(
					f.getTaskHolderProperty().eq(taskHolder)
					.or(f.getTaskHolderProperty().isNull())
					.or(f.getSenderProperty().eq(sender))
				);
			} else if(taskHolder!=0) {
				filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));
			} else if(sender!=0) {
				filter = filter.and(f.getSenderProperty().eq(sender).and(f.getTaskHolderProperty().ne(sender).or(f.getTaskHolderProperty().isNull())));
			}
		 }
	
		return filter;
	}
	
	private static Filter getSearchCombination(TaskProperties f, String search) {
		Filter joinSequence = null;
		String[] comb = search.trim().split("\\s");
		
		for (String word : comb) {
			StringBuilder processed = new StringBuilder("%").append(word).append("%");
			if(joinSequence == null) {
				joinSequence = getSearch(f, processed.toString()); 
			} else {
				joinSequence = joinSequence.and(getSearch(f, processed.toString()));		
			}
		}			
		return joinSequence;
	}
	
	private static Filter getSearch(TaskProperties f, String search) {
		Filter filter = f.getDescriptionProperty().like(search)
		.or(f.getRegistryNameProperty().like(search))
		.or(f.getCommentsProperty().like(search)) 
		.or(f.getTagNameProperty().like(search))
		.or(f.getGtaskIdProperty().like(search))
		.or(f.getCommentsWorkflowProperty().like(search))
		.or(f.getTaskHolderNameProperty().like(search));
		
		Integer numberSearch = 0;
		
		try { numberSearch = Integer.parseInt(search.replaceAll("[^\\d]", "")); } 
		catch(NumberFormatException e){}
		
		if(numberSearch!=0) {
			filter = filter.or(f.getNumberProperty().like(numberSearch));
		}

		return filter;
	}	
	
	private static Filter getFilterCau(AonApiData api, TaskProperties f) {
		JSONObject params = api.getData();
		String document   = params.optString(IJsonNames.DOCUMENT);
		String email      = params.optString(IJsonNames.EMAIL);
		
		Filter filterCau  = f.getGtaskIdProperty().eq(email)
		;
		
		if(!document.isEmpty()) {
			Registry registry = AON.getRegistry(api.getDomain(), api.getUser(), r->r.getDocumentProperty().eq(document));
			if(registry!=null && registry.getId()!=null) {					
				filterCau = filterCau.or(
					f.getRegistryProperty().eq(registry.getId())
					.and( f.getGtaskIdProperty().isNotNull() )
				);
			}
		}
		return filterCau;
	}
	
	private static List<Integer> getWorkgroupList(String workgroupStr) {
		List<Integer> workgroupList = new ArrayList<>();
		if(!workgroupStr.isEmpty()) {
			String newStr = workgroupStr;
			if(newStr.contains(",all") || newStr.equals("0")) {
				newStr = newStr.replace(",all","").replace("0", "");
			}
			
			if(newStr!=null && !"".equals(newStr)) {
				String[] str = newStr.split(",");
		
				for(int i=0; i<str.length; i++) {
					workgroupList.add( Integer.parseInt(str[i]) );
				}
			}
		}
		
		return workgroupList;
	}
}
