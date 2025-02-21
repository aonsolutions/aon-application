import { AonSimpleList } from '../../components/aon-simple-list.js';
import { MATERIAL_ICONS } from '../../environments/environments.js';
import {getUserList} from '../../services/service.js';

export class AonUserSimpleList extends AonSimpleList {

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
            getUserList(filter).then(users => {
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
            getUserList(this.getFilter()).then(users => {
                users.forEach((user, i) => this.addRow(user, i));
            });
        }
    }

    addRow(user, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PERSON,
            title: user.name + ' ' + user.surname,
            subtitle:  user.email
        }
        this.addLi(liValue, i, () => {}, this.option.icon, () => this.option.fn(user));
    }

    setValue(value) {
        getUserList(this.getFilter()).then(users => {
            this.build();
            users.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((user, i) => {
                this.addRow(user, i)
            });
        });
	}

    setFilter(filter) {
        this.filter = filter;
        getUserList(this.getFilter()).then(users => {
            this.build();
            users.filter(f => 
                f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
                || f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
            ).forEach((user, i) => {
                this.addRow(user, i)
            });
        });
	}
}
if(!window.customElements.get('aon-user-simple-list')){
    window.customElements.define('aon-user-simple-list', AonUserSimpleList);
}
