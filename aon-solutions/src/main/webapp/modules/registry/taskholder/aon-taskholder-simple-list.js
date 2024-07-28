import { AonSimpleList } from '../../../components/aon-simple-list.js';
import { MATERIAL_ICONS } from '../../../environments/environments.js';
import { getTastHoldersWithWorkgroupsList } from '../../../services/taskHolderService.js';

export class AonTaskHolderSimpleList extends AonSimpleList {

    more;
    option;
    constructor () {
        super();
    }

    connectedCallback () {
        this.more = false;
        this.init();
        this.addEventListener('more', () => {
    		if(this.more)
    			this.loadMore()
    	});
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getTastHoldersWithWorkgroupsList(filter).then(users => {
                if(users.length == 0)
                    this.more = false;
                users.forEach((user, i) => this.addRow(user, i));
            });
        }
    }

    init() {
        this.initialize();
        this.build();
        if(this.users) {
            this.removeAllLi();
            this.users.forEach((user, i) => this.addRow(user, i));
        } else {
            getTastHoldersWithWorkgroupsList(this.getFilter()).then(users => {
                users.forEach((user, i) => this.addRow(user, i));
            });
        }
    }

    addRow(user, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PERSON,
            title: user.name,
            // subtitle:  user.email
        }
        this.addLi(liValue, i, () => {}, this.option.icon, () => this.option.fn(user));
    }

    setValue(value) {
        getTastHoldersWithWorkgroupsList(this.getFilter()).then(users => {
            this.build();
            users.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                // || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((user, i) => {
                this.addRow(user, i)
            });
        });
	}

    setFilter(filter) {
        this.filter = filter;
        getTastHoldersWithWorkgroupsList(this.getFilter()).then(users => {
            this.build();
            users.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                // || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((user, i) => {
                this.addRow(user, i)
            });
        });
	}
}
if(!window.customElements.get('aon-taskholder-simple-list')){
    window.customElements.define('aon-taskholder-simple-list', AonTaskHolderSimpleList);
}
