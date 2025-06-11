package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ImpersonateUserPanel extends ScrollPanel {
	
	public static class ImpersonateUserParams {
		private Integer fromDomain; 
		private String fromDomainName;
		private String fromUser;
		private Integer toDomain;
		private String toDomainName;
		private String toUser;
		
		public Integer getFromDomain() {
			return fromDomain;
		}
		public ImpersonateUserParams setFromDomain(Integer fromDomain) {
			this.fromDomain = fromDomain;
			return this;
		}
		
		public String getFromDomainName() {
			return fromDomainName;
		}
		public ImpersonateUserParams setFromDomainName(String fromDomainName) {
			this.fromDomainName = fromDomainName;
			return this;
		}
		
		public String getFromUser() {
			return fromUser;
		}
		public ImpersonateUserParams setFromUser(String fromUser) {
			this.fromUser = fromUser;
			return this;
		}
		
		public Integer getToDomain() {
			return toDomain;
		}
		public ImpersonateUserParams setToDomain(Integer toDomain) {
			this.toDomain = toDomain;
			return this;
		}
		
		public String getToDomainName() {
			return toDomainName;
		}
		public ImpersonateUserParams setToDomainName(String toDomainName) {
			this.toDomainName = toDomainName;
			return this;
		}
		
		public String getToUser() {
			return toUser;
		}
		public ImpersonateUserParams setToUser(String toUser) {
			this.toUser = toUser;
			return this;
		}
		
	}
	
	public ImpersonateUserPanel(ImpersonateUserParams params) {
		Occam occam = new Occam()
			.setDomainName( params.getToDomainName() )
			.setDomain(params.getToDomain())
			.setUser( params.getToUser() );

		FlowPanel container = new FlowPanel();
		setWidget(container);
		
		ConsoleModule.CONSOLE_SERVICE.availableUsers(occam, params.getToDomain(), new AsyncCallback<LinkedList<User>>() {
				
			@Override
			public void onFailure(Throwable t) {
				Label label = new Label("No se pudo mostrar los usuarios. ("+ t.getMessage() +")");
				label.setStyleName(AON.CSS.aonBlockMessage());
				label.setStyleName(AON.CSS.aonBlockErrorMessage());
				label.setStyleName(AON.CSS.aonMarginTop());
			}

			@Override
			public void onSuccess(LinkedList<User> users) {
				Hidden fromDomain = new Hidden(IRequestParamsNames.FROM_DOMAIN);
				Hidden fromDomainName = new Hidden(IRequestParamsNames.FROM_DOMAIN_NAME);
				Hidden fromUser = new Hidden(IRequestParamsNames.FROM_USER);
				fromDomain.setValue( AonNumberUtils.toString(params.getFromDomain()));
				fromDomainName.setValue( params.getFromDomainName() );
				fromUser.setValue( params.getFromUser() );
				
				Hidden toDomain = new Hidden(IRequestParamsNames.TO_DOMAIN);
				Hidden toDomainName = new Hidden(IRequestParamsNames.TO_DOMAIN_NAME);
				Hidden toUser = new Hidden(IRequestParamsNames.TO_USER);
				toDomain.setValue( AonNumberUtils.toString(params.getToDomain()));
				toDomainName.setValue( params.getToDomainName() );
				
				FormPanel locForm = new FormPanel(params.getToDomainName());
				locForm.setMethod(FormPanel.METHOD_POST);
				FlowPanel locFormPanel = new FlowPanel();
				locFormPanel.add(fromDomain); 
				locFormPanel.add(fromDomainName);
				locFormPanel.add(fromUser);
				locFormPanel.add(toDomain);
				locFormPanel.add(toDomainName);
				locFormPanel.add(toUser);
				locForm.setWidget(locFormPanel);
				container.add(locForm);
				
				FlowPanel header = new FlowPanel();
				header.setStyleName(AON.CSS.aonMarginBottom());
				header.addStyleName(AON.CSS.aonMarginTop());
				header.addStyleName(AON.CSS.aonTextCenter());
				InlineLabel info = new InlineLabel("Seleccione un usuario para conectar al dominio ");
				InlineLabel domInfo = new InlineLabel(params.getToDomainName());
				domInfo.setStyleName( AON.CSS.aonMarginLeft() );
				domInfo.addStyleName( AON.CSS.aonBold() );
				header.add(info);
				header.add(domInfo);
				container.add(header);
				
				AonDisplayGrid grid = new AonDisplayGrid();
				grid.addStyleName(AON.CSS.aonMarginTop());
				grid.addStyleName(AON.CSS.aonWidthAlmostAll());
				grid.addStyleName(AON.CSS.aonBlockCenter());
				grid.addHeaderRow()
					.addCell(new Label(""), AON.CSS.aonWidth30())
					.addCell(new Label("Usuario"), AON.CSS.aonWidth150())
					.addCell(new Label("Nombre"), AON.CSS.aonFlexGrow1());
				users.stream()	
					.forEach( u -> {
						Label topLevel = new Label();
						if (AonNumberUtils.notEquals(params.getToDomain(),u.getDomain().getId())) {
							topLevel.setStyleName(AON.CSS.aonTabIcon());
							topLevel.addStyleName(AON.CSS.aonIconLevelTop());
						}
						grid.addRow()
							.addCell( topLevel )
							.addCell(new Label(u.getLogin()))
							.addCell(new Label(u.getName()))
							.addClickHandler( e -> {
								String url = 
									(AonStringUtils.isBlank(Location.getProtocol())? "http:" : Location.getProtocol())
									+ "//"
									+ occam.getDomainName()
									+ (AonStringUtils.isNotBlank(Location.getPort()) ? (":" + Location.getPort() + "/aon-aio") : "")
//									+ "/login"
									+ "/impuser/home.jsf"
								;
								locForm.setAction(url);
								toUser.setValue(u.getLogin());
								locForm.submit();
							});
						
					});
				container.add(grid);
			}
		});
	}

}
