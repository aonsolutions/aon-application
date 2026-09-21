import {AonMobileList} from '../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS } from '../../environments/environments.js';
import {getUserList, getUserRoles} from '../../services/service.js';
import { AonUser } from './aon-user.js';

export class AonMobileUserList extends AonMobileList {

    PER_PAGE = 30;

    more;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
    }

    moreFn = () => {
        if(this.more)
            this.loadMore();
    };

    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        super.initialize();
        this.more = true;
    }

    loadMore() {
        let filter = this.getFilter();
        if(!filter.page) return;
        this.more = false;
        filter.page = filter.page + 1;
        this.setFilter(filter);
        getUserList(filter).then(users => {
            if(users.length > 0)
                this.more = true;
            users.forEach((user, i) => this.addRow(user, i));
        });
    }

    init() {
        let filter = this.getFilter();
        filter.page = 1;
        filter.perPage = filter.perPage || this.PER_PAGE;
        this.setFilter(filter);
        this.more = true;

        this.build();
        getUserList(filter).then(users => {
            if(users.length == 0){
                this.empty();
            }
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
        // setIndex(i); //TODO NOT EXIST
    	getUserRoles({user: user.id}).then(roles => {
			user.roles = roles;
			let aonUser = new AonUser();
			aonUser.id = 'aonUser-' + user.id;
			aonUser.setShowApps(true);
			aonUser.setShowToolbar(true);
			aonUser.setUser(user);
			aonUser.style.width = "100%";
	
			this.getApplication().setContent(aonUser);
		});
	}

    setValue(value) {
        let filter = this.getFilter();
        filter.value = value;
        this.setFilter(filter);
        this.init();
	}
}
window.customElements.define('aon-mobile-user-list', AonMobileUserList);
