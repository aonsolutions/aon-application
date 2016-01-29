package com.esferalia.aon.gwt.office.client.values.issues;

import com.esferalia.aon.gwt.office.client.values.Value;
import com.esferalia.aon.gwt.office.client.values.ValueProp;

public class IssueValue extends Value<IssueValue.Prop> {
	
	public static enum Prop implements ValueProp {
		Title("title"),
		Priority("priority"),
		Sender("sender"),
		Company("company"),
		Source("source"),
        Body("body"),
        Assignee("assignee"),
        Recipient("recipient"),
        State("state"),
        Milestone("milestone"),
        Type("type"),
        Workgroup("workgroup"),
        Labels("labels")
		;
        private final String value;
        
        private Prop(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}
	}
	
	public void setTitle (String title) {
		prop.put(Prop.Title, title);
	}
	
	public void setPriority(String priority) {
		prop.put(Prop.Priority, priority);
	}
	
	public void setSender(String sender) {
		prop.put(Prop.Sender, sender);
	}
	
	public void setBody(String body) {
		prop.put(Prop.Body, body);
	}
	
	public void setState(String state) {
		prop.put(Prop.State, state);
	}

	public void setAsignee(String user) {
		prop.put(Prop.Assignee, user);
	}
	
	public void setCompany(String company) {
		prop.put(Prop.Company, company);
	}
	
	public void setSource(String source) {
		prop.put(Prop.Source, source);
	}
	
	public void setRecipient(Integer recipient) {
		prop.put(Prop.Recipient, recipient);
	}

	public void setMilestone(Integer number) {
		prop.put(Prop.Milestone, number);
	}
	
	public void setWorkgroup(Integer workgroup) {
		prop.put(Prop.Workgroup, workgroup);
	}
	
	public void setType(String type) {
		prop.put(Prop.Type, type);
	}

	public void setLabels(String[] labels) {
		prop.put(Prop.Labels, labels);
	}
}
