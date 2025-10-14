import { AonSimpleList } from '../../components/aon-simple-list.js';
import { MATERIAL_ICONS } from '../../environments/environments.js';

export class AonScopeSimpleList extends AonSimpleList {

    scopes;
    option;

    constructor () {
        super();
    }

    connectedCallback () {
        this.init();
    }

    init() {
        this.initialize();
        this.build();
        if(this.scopes) {
            this.removeAllLi();
            this.scopes.forEach((scope, i) => this.addRow(scope, i));
        }
    }

    addRow(scope, i) {
        let liValue = {
            icon: MATERIAL_ICONS.SECURITY,
            title: scope.name
        }
        this.addLi(liValue, i, () => {}, this.option.icon, () => this.option.fn(scope));
    }

}
if(!window.customElements.get('aon-scope-simple-list')){
    window.customElements.define('aon-scope-simple-list', AonScopeSimpleList);
}
