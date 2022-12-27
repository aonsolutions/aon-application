import { AonElement } from '../../components/AonElement.js';
import { TAG } from '../../environments/environments.js';

export class AonSales extends AonElement {

}

if(!window.customElements.get(TAG.AON_SALES)) {
    window.customElements.define(TAG.AON_SALES, AonSales);
}