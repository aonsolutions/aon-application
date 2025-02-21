import { AonSimpleList } from '../../components/aon-simple-list.js';
import { EVENT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import { ProjectHolder } from '../../models/project/ProjectHolder.js';
import { getProjectsHolders, saveProjectHolder, deleteProjectHolder } from '../../services/projectService.js';
import { getTastHolders } from '../../services/taskHolderService.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { ProjectUtils } from './ProjectUtils.js';

export class AonHolderSimpleList extends AonSimpleList {

    more;
    option;
    workgroups;
    taskHolders;
    data;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();

        if(this.data.length){
            this.initData();
        } else {
            this.init();
        }

        this.addEventListener('more', () => {
    		if(this.more)
    			this.loadMore()
    	});
    }

    initialize(){
        this.more = false;
        this.workgroups = [];
        this.taskHolders = [];
        this.data = this.data || [];
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            this.getData(filter)
            .then(holders => {
                if(holders.length == 0)
                    this.more = false;
                holders.forEach((holder, i) => this.addRow(holder, i));
            });
        }
    }

    init() {
        this.initialize();
        this.build();

        this.getData(this.getFilter())
        .then(holders => {
            holders.forEach((holder, i) => this.addRow(holder, i));
        });
    }

    initData() {
        this.initialize();
        this.build();

        this.data.forEach((holder, i) => {
            this.addRow(this.parseHolderData(holder, i));
        })
    }

    addRow(holder, i) {
        let liValue = {
            icon: holder.icon,
            title: holder.title,
            subtitle:  holder.date
        }
        this.addLi(liValue, i, () => {
            this.buildAdd(holder);
        },  
        MATERIAL_ICONS.DELETE, 
        (ev) => {
            ev.preventDefault();
            ev.stopPropagation();
            this.delete(holder);
        });
    }


    setFilter(filter) {
        this.filter = filter;
	}

    setData(data){
        this.data = data;
    }

    async getData(filter){
        try {
            const resp = await getProjectsHolders(filter);
            return resp.map(holder=> this.parseHolderData(holder));
        } catch (error) {
            this.showError(error);
        } 
    }

    parseHolderData(holder){
        const taskHolderName = holder.taskHolder && holder.taskHolder.name ? holder.taskHolder.name : undefined;
        const workgroupName  = holder.workgroup && holder.workgroup.description ? holder.workgroup.description : undefined;

        holder.icon  = taskHolderName ? MATERIAL_ICONS.PERSON : MATERIAL_ICONS.PEOPLE;
        holder.title = taskHolderName || workgroupName;

        if(holder.end_date){
            holder.date = AonDateUtils.setDate(holder.start_date)+ " - "+AonDateUtils.setDate(holder.end_date);
        } else {
            holder.date = AonDateUtils.setDate(holder.start_date);
        }

        return holder;
    }

    buildAdd(holder){
        ProjectUtils.buildDialogHolder(this, new ProjectHolder(holder));
    }

    async getWorkgroups(){
		if(this.workgroups.length==0){
			let wgs = await getWorkgroups().catch(()=> []);
			this.workgroups = wgs.map((r) => ({...r, name: r.description, value: r.id}))
		} 
		return this.workgroups;
	}

	async getTaskHolders(){
		if(this.taskHolders.length==0){
			let ths = await getTastHolders().catch(()=> []);
			this.taskHolders = ths.map((r) => ({...r, value: r.id}))
		} 
		return this.taskHolders;
	}

    async onSaveHolder(holder){
        this.getApplication().startLoading();
        try {
            await saveProjectHolder(holder);
            this.showMessage();
            this.init();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        } catch (error) {
            this.showError(error);
        }
        this.getApplication().stopLoading();
    }

    async delete(holder) {
        this.getApplication().confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM + " " + holder.title, async()=>{
          this.getApplication().startLoading();
          try {
            await deleteProjectHolder(holder);
            this.showToast({ message: MSG.DELETED_DATA });
            this.init();
            this.dispatchEvent(new Event(EVENT.CHANGE));
          } catch (error) {
            this.showToast(error);
          }
          this.getApplication().stopLoading();
        });
    }
}
if(!window.customElements.get('aon-holder-simple-list')){
    window.customElements.define('aon-holder-simple-list', AonHolderSimpleList);
}
