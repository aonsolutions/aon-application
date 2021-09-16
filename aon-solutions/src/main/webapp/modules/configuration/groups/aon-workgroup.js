import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSearch } from "../../../components/aon-search.js";
import { AonElement } from "../../../components/AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../../../environments/environments.js";
import { assignUserWorkgroup, removeUserWorkgroup, getUserList } from "../../../services/userService.js";
import { AonUserSimpleList } from "../../user/aon-user-simple-list.js";
import { AonGroupList } from "./aon-group-list.js";

export class AonWorkgroup extends AonElement {

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }
    
    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor() {
        super();
    }
    
    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonWorkgroup';
        this.USER_LIST = this.id + 'UserList';
        this.add = false;
        this.DIV = this.id + CONSTANT.DIV.initCap();
        this.LIST = this.id + CONSTANT.LIST.initCap();
        this.USERS = this.id + CONSTANT.USERS.initCap();
    }
    
    build(){
        getUserList().then(users => this.users = users);
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.className = CSS.AON_FLEX;
        this.appendChild(div);

        let groupsDiv = this.createElement(TAG.DIV);
        groupsDiv.id = this.LIST;
        groupsDiv.style.width = "100%";
        let list = new AonGroupList();
        div.appendChild(groupsDiv);
        groupsDiv.appendChild(list);

        let usersDiv = this.createElement(TAG.DIV);
        usersDiv.id = this.USERS;
        usersDiv.style.display = 'none';
        usersDiv.style.width = "50%";
        usersDiv.style.borderLeft = '1px solid #ddd';
        div.appendChild(usersDiv);

        list.addEventListener(EVENT.SELECT, (event) => {
            groupsDiv.style.width = "50%";
            usersDiv.style.display = 'block';
            this.buildUsers(usersDiv, event.detail);
        });
    }

    buildUsers(parent, workgroup) {
        this.clearElement(parent);

        let div = this.createElement(TAG.DIV);
        div.style.borderBottom = '1px solid #ddd';
        div.style.height = '48px';
        parent.appendChild(div);
        
        let span = this.createElement(TAG.SPAN);
        span.innerHTML = workgroup.description;
        span.style.position = 'absolute';
        span.style.margin =  '20px';
        span.style.fontWeight = '500';
        span.style.color = 'rgb(95, 99, 104)';
        div.appendChild(span);

        let aonSearch = new AonSearch();
        aonSearch.classList.add(CSS.AON_RIGHT_60);
        aonSearch.style.top = '6px';
        div.appendChild(aonSearch);
        aonSearch.addEventListener(EVENT.SEARCH, (event) => this.loadUserList(workgroup, event.detail).init());
        let user = new AonIconButton();
        user.id = this.USERS + 'User';
        user.icon = MATERIAL_ICONS.PERSON;
        user.style.position = 'absolute';
        user.style.display = 'none';
        user.style.top = '6px';
        user.classList.add(CSS.AON_RIGHT_20);
        div.appendChild(user);
     

        let addButton = new AonIconButton();
        addButton.id = this.USERS + 'AddButton';
        addButton.icon = MATERIAL_ICONS.PERSON_ADD;
        addButton.style.position = 'absolute';
        addButton.style.top = '6px';
        addButton.classList.add(CSS.AON_RIGHT_20);
        div.appendChild(addButton);

        this.add = false;
        let userList = this.loadUserList(workgroup);
        parent.appendChild(userList);
    
        user.addEventListener(EVENT.CLICK, () => {
            addButton.style.display = 'block';
            user.style.display = 'none';
            this.add = false;
            this.loadUserList(workgroup).init();
        });

        addButton.addEventListener(EVENT.CLICK, () => {
            addButton.style.display = 'none';
            user.style.display = 'block';
            this.add = true;
            this.loadUserList(workgroup).init();
        });
    }

    addUser(user, workgroup){
        let data = {
            user: user.id,
            workgroup: workgroup.id
        }
        this.users.forEach((usr, i) => {
            if(user.id === usr.id)
                usr.workgroups.push(workgroup);
        });
        assignUserWorkgroup(data);   
        this.loadUserList(workgroup).init();
    }

    removeUser(user, workgroup) {
        let data = {
            user: user.id,
            workgroup: workgroup.id
        }
        this.users.forEach((usr, i) => {
            if(user.id === usr.id)
                usr.workgroups.forEach((wg, j) => {
                    if(wg.id === workgroup.id)
                       usr.workgroups.splice(j, 1);
                });
        });
        removeUserWorkgroup(data);
        this.loadUserList(workgroup).init();
    }


    loadUserList(workgroup, search) {
        search = search || '';
        let userList = this.getElement(this.USER_LIST) || new AonUserSimpleList();
        userList = new AonUserSimpleList();
        userList.id = this.USER_LIST;
        userList.users = this.users
        .filter(f => ((this.add && !f.workgroups.map(r => r.id).includes(workgroup.id)) 
                ||  (!this.add && f.workgroups.map(r => r.id).includes(workgroup.id))) 
            && (
                (f.email && f.email.toUpperCase().includes(search.toUpperCase())) 
                || ((f.name ? f.name : '') + ' ' + (f.surname ? f.surname : '')).toUpperCase().includes(search.toUpperCase())
            )
        );
        let icon = this.add ?  MATERIAL_ICONS.PERSON_ADD :  MATERIAL_ICONS.CLOSE;
        let fn = this.add 
            ?(user) => this.addUser(user, workgroup)
            :(user) => this.removeUser(user, workgroup);
        userList.option = {
            icon,
            fn
        };
        return userList;
    }
}
if(!window.customElements.get('aon-workgroup')){
	window.customElements.define('aon-workgroup', AonWorkgroup);
}