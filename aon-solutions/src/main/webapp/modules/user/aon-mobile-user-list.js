import {AonMobileList} from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS } from '../../environments/environments.js';
import {getUsers} from '../../services/service.js';

export class AonMobileUserList extends AonMobileList {

    more;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener('more', () => {
    		if(this.more)
    			this.loadMore()
    	});
    }

    initialize() {
        this.more = false;
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getUsers(filter).then(users => {
                if(users.length == 0)
                    this.more = false;
                users.forEach((user, i) => this.addRow(user, i));
            });
        }
    }

    init() {
        this.build();
        getUsers(this.getFilter()).then(users => {
            users.forEach((user, i) => this.addRow(user, i));
        });
    }

    addRow(user, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PERSON,
            title: user.name + ' ' + user.surname,
            subtitle:  user.email
        }
        this.addLi(liValue, i, () => this.aonUser(user, i));
    }

    aonUser(user, i) {
        this.getApplication().setContentHTML('<aon-user id="aonUser-' + user.id + '" showApps="true" showInfo="true" showToolbar="true"><aon-user>');
		let aonUser = document.getElementById('aonUser-' + user.id);
		aonUser.style.width = "100%";
		aonUser.setAttribute('user', JSON.stringify(user));
	}

    setValue(value) {
        getUsers(this.getFilter()).then(users => {
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
window.customElements.define('aon-mobile-user-list', AonMobileUserList);
