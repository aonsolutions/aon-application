import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles, getNotification, getTastHolders, markReadNotification } from "../../services/service.js";
import { CONSTANT, EVENT, MSG } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonApplication } from "../../components/aon-application.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import { NotificationEnums } from "./NotificationEnums.js";
import { NotificationUtils } from "./utils/NotificationUtils.js";
import { NotificationDeskUtils } from "./utils/NotificationDeskUtils.js";

export class AonNotificationDesk extends AonElement {
  AON_NOTIFICATION_DESK;
  MORE;
  TASK_HOLDERS;
  dur;
  DIV_PARENT;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return this.getAttribute(CONSTANT.DATA) ? JSON.parse(this.getAttribute(CONSTANT.DATA)) : null;
  }

  set data(data) {
    this.setAttribute(CONSTANT.DATA, data);
  }

  getFilter() {
		return this.hasAttribute(CONSTANT.FILTER)? JSON.parse(this.getAttribute(CONSTANT.FILTER))	: {page:0, perPage:10};
	}

	setFilter(filter) {
		return this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
	}

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.id = NotificationEnums.NOTIFICATION_IDS.AON_NOTIFICATION_DESK;
    this.AON_NOTIFICATION_DESK = this.id+"Application";
    this.aonNotifyIconEl = document.querySelector("aon-notification-icon");
    this.TASK_HOLDERS = [];
    this.MORE = true;
  }

  getDur() {
    return this.dur;  
  }

  build() {
		this.createApplication(this.AON_NOTIFICATION_DESK, MSG.NOTIFICATIONS, new AonApplication());
    this.buildToobar();

    this.paintView();

    this.loadMore(true);
  }

  buildToobar(){
    const application = this.getApplication();

    // if(!this.getDur().isEmptyDomain() && !this.getDur().isEmployee()) {
      application.addToolbarOption2(SigninSidenav.ADD, () => NotificationUtils.openDialog(this, this.getApplication().getDialog()));
    // }

    this.buildToolbarSearch();

    this.navBar();
  }

  buildToolbarSearch(){

		let btnSearch = this.getApplication().addSearchOption(!this.isMobile());
		let timeOut = null;
		
		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
		  clearTimeout(timeOut);
		  
		  timeOut = setTimeout(() => {
			  this.setFilter({
          ...this.getFilter(), 
          page:0, 
          perPage:30, 
          search: detail.search
			  });
        this.loadMore(true);
		  }, 300);
		});

    btnSearch.removeButtonAvanced();
	}

  navBar(){
    const notificationOptions = NotificationEnums.NotificationOptions;
    let filter = {page:0, perPage:10, status: undefined};
    let notificationOpts = [
			{
				...notificationOptions.NOTIFICATION_ALL,
				fn: () =>{
          this.setFilter(filter);
          this.loadMore(true);
				}
			},
			{
				...notificationOptions.NOTIFICATION_NOT_READ,
				fn: () =>{
          this.setFilter({...filter, status:"unread"});
          this.loadMore(true);
				}
			}
		];
		
		this.getApplication().addSidenavOptions(MSG.STATUS, notificationOpts);
  }

  paintView() {
    this.DIV_PARENT = NotificationDeskUtils.build(this.id + "DivParent");
 
    this.getApplication().setContent(this.DIV_PARENT);

    let timeOut = undefined;
    this.DIV_PARENT.addEventListener("scroll", ({target}) => {

      clearTimeout(timeOut);
      if(this.MORE){
        const scrollTop = target.scrollTop;
        const offsetHeight = target.offsetHeight;
        const physicalSize = target.scrollHeight;
        const maxScrollPosition = physicalSize - offsetHeight;

        if (scrollTop >= maxScrollPosition) {
          timeOut = setTimeout(async () => {
            await this.loadMore(false);
          }, 300);
        }
      }
    });
  }

  async loadMore(reload) {
    
    let application = this.getApplication();

    application.startLoader();
    
    const datos = await this.getData();

    if (reload){
      this.DIV_PARENT.innerHTML ="";
    } 

    datos.forEach((res) => {
      this.DIV_PARENT.appendChild(
        NotificationDeskUtils.buildRow(res)
      );
    });

    application.stopLoader();
  }

  async getData() {
    let data = [];
    try {
      let filter = this.getFilter();
        filter.page = filter.page + 1;
        this.setFilter(filter);
        const datos = await getNotification(filter);
      
        if(datos.length == 0 ){
          this.MORE = false;
        } else {
          data = datos;
        }
    } catch (error) {
      console.log("error>>", error);
      this.showError(error);
    }
    return data;
  }
  

  async listTaskHolder(){
    if(this.TASK_HOLDERS.length <= 0){
      let result = await getTastHolders().catch(e=>null);
      if(result) this.TASK_HOLDERS = result.map(r=>({name:r.name,value:r.id}));
    }
    return this.TASK_HOLDERS;
  }
  
  markReadNotification(res){
    if(res.status ===0){
      markReadNotification(res);
      res.status=1;
      let aonNotificationIcon = document.querySelector("aon-notification-icon");
      if (aonNotificationIcon) { 
        aonNotificationIcon.getTotalNotification();
      }
    }
  }

  goNotification(data) {
    const aonComponent = NotificationUtils.getNotificationComponent(data);
    if(aonComponent){
      this.rootPanel(aonComponent);
    }
  }
}
window.customElements.define("aon-notification-desk", AonNotificationDesk);
