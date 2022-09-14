
import { AonSimpleList } from '../../components/aon-simple-list.js';
import { EVENT } from '../../environments/environments.js';
import { getTasks } from '../../services/taskService.js';
import {TaskListUtils} from "./utils/TaskListUtils.js";
import { TaskUtils } from './utils/TaskUtils.js';

export class AonMessengerSimpleList extends AonSimpleList {

    more;
    option;
    constructor () {
        super();
    }

    connectedCallback () {
        this.more = false;
        this.init();
        this.addEventListener(EVENT.MORE, () => {
    		if(this.more){
                this.loadMore()
            }
    	});
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getTasks(filter).then(tasks => {
                if(tasks.length == 0){
                    this.more = false;
                }
                tasks.forEach((task, i) => this.addRow(task, i));
            });
        }
    }

    init() {
        this.initialize();
        this.build();
        if(this.tasks && this.tasks.length) {
            this.removeAllLi();
            this.tasks.forEach((task, i) => this.addRow(task, i));
        }  else {
            this.empty("Sin resultados");
        }
    }

    addRow(task, i) {
        let iconHtmlCustom = TaskListUtils.getIcon(task);
        iconHtmlCustom.style.marginRight = '10px';
        let liValue = {
            iconHtmlCustom: iconHtmlCustom.outerHTML,
            title: task.title,
            subtitle: TaskUtils.taskNumberParse(task.number)
        }
        this.addLi(liValue, i, () => {}, this.option.icon, () => this.option.fn(task));
    }

    setValue(value) {
        getTasks(this.getFilter()).then(tasks => {
            this.build();
            tasks.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((task, i) => {
                this.addRow(task, i)
            });
        });
	}

    setFilter(filter) {
        this.filter = filter;
        getTasks(this.getFilter())
        .then(tasks => {
            this.build();
            tasks.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((task, i) => {
                this.addRow(task, i)
            });
        });
	}
}
if(!window.customElements.get('aon-messenger-simple-list')){
    window.customElements.define('aon-messenger-simple-list', AonMessengerSimpleList);
}
