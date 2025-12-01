import { AonDialog } from "../../../components/aon-dialog";
import { AonElement } from "../../../components/AonElement";
import { createInput } from "../../../components/CreateComponent";
import { CONSTANT, TAG, MATERIAL_ICONS, EVENT } from "../../../environments/environments";
import { getPackage } from "../../../services/productService";
import { saveInventoryDetails } from "../../../services/warehouseService";

export class AonMobileInventory extends AonElement {

  inventory;

  SCANNER;

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
    this.id = this.id || 'aonMobileInventory';
    this.SCANNER = this.id + 'Scanner';
  }

  build() {
    let div =this.createDiv();
    div.id = this.id + 'Div';
    div.style.margin = '10px';
    this.appendChild(div);

    let scanner = this.buildScanner(div);
    this.buildDetails(div);

    scanner.focus();
  }

  buildScanner(div) {
    let scanner = createInput(this.SCANNER, "Escanea SSCC...");
    div.appendChild(scanner);
    scanner.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode());
    scanner.addEventListener(EVENT.CHANGE, () => this.addInventoryDetail());
    return scanner;
 }

 addInventoryDetail() {
    let scanner = this.getElement(this.SCANNER);
    let sscc = scanner.getValue();
    alert('Añadir detalle de inventario con SSCC: ' + sscc);
    let data = {sscc, full:true};
    getPackage(data).then(itemPackage => {
        if(itemPackage.delivery) {
            this.showMessageError("El envase está incluido en el albarán: " + itemPackage.delivery.reference);
        } else {
            let data = {
                inventory: this.inventory.id,
                itemPackage: itemPackage.id,
                details: []
            }
            if(this.inventory.details.filter(f => f.item == itemPackage.id).length > 0) {
                this.showMessageError("El envase ya está incluido en el inventario.");
            } else {
                itemPackage.itemComposition.forEach(element => {

                    this.inventory.details.filter(f => f.item == element.composition.id).forEach(d => {
                        let detail = {
                            detail: d.id,
                            itemPackage: itemPackage.id,
                            item: element.composition.id,
                            quantity: element.quantity
                        };
                        data.details.push(detail);
                    });
                });
                if(data.details.length == 0) {
                    this.showMessageError("El envase no contiene ningún artículo incluido en el inventario.");
                } else this.buildInventoryDialog(data);
            }
        }
    }).catch(err => {
        this.showError(err);
    }); 
 }

 buildInventoryDialog(data) {
    let div = this.createDiv();
    data.details.forEach(detail => {
        let detailDiv = this.createDiv();
        detailDiv.style.borderBottom = '1px solid #ccc';
        detailDiv.style.padding = '10px 0';

        let itemSpan = this.createSpan();
        itemSpan.innerHTML = `Item: ${detail.item}`;
        detailDiv.appendChild(itemSpan);
        
        let quantitySpan = this.createSpan();
        quantitySpan.innerHTML = `Cantidad: ${detail.quantity}`;
        detailDiv.appendChild(quantitySpan);

        div.appendChild(detailDiv);
    });
  
    let d = this.getDialog();
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle("Añadir al Inventario");
    d.setContent(div);
    d.addAcceptAction(() => {
        d.remove();
        this.saveInventoryDetails(data);
        this.resetScanner();
        // AÑADIR DETALLES AL INVENTARIO
    });
    d.open();
 }

 saveInventoryDetails(data) {    
    saveInventoryDetails(data).then(() => {
        this.showMessage("Detalles de inventario actualizados.");
        
        if(this.inventory.details.filter(f => f.item == data.itemPackage).length == 0) {
            this.inventory.details.push({
                actualQuantity:1,
                realQuantity:1,
                item: data.itemPackage,
                inventory: this.inventory.id,
                domain: this.inventory.domain 
            });
        }

        data.details.forEach(detail => {
            this.inventory.details.filter(f => f.id == detail.detail).forEach(d => {
                d.realQuantity = d.realQuantity + detail.quantity;
            });
        });

        this.clearDetails();
        this.buildDetails(this.getElement(this.id + 'Div'));
    }).catch(err => {
        this.showError(err);
    });
 }

  getDialog() {
    let dialog = new AonDialog();
    document.body.appendChild(dialog);
    dialog.addEventListener(EVENT.CLOSE, () => {
        dialog.remove();
        this.resetScanner();
    });
    return dialog;
}

resetScanner() {
    let scanner = this.getElement(this.SCANNER);
    scanner.setValue(CONSTANT.EMPTY);
    scanner.focus();
}

 openBarcode() {
    alert('Abrir escáner de código de barras');
 }

 clearDetails() {
    let div = this.getElement(this.id + 'DetailsDiv');
    if(div) this.clearElement(div);
 }

 buildDetails(parent) {
    let div = this.getElement(this.id + 'DetailsDiv') ;
    if(!div) {
        div = this.createDiv(this.id + 'DetailsDiv');
        parent.appendChild(div);
    }

    this.inventory.details.forEach(detail => {
        let detailDiv = this.createDiv();
        detailDiv.style.borderBottom = '1px solid #ccc';
        detailDiv.style.padding = '10px 0';

        let itemSpan = this.createSpan();
        itemSpan.innerHTML = `Item: ${detail.item}`;
        detailDiv.appendChild(itemSpan);
        
        let quantitySpan = this.createSpan();
        quantitySpan.innerHTML = `Cantidad: ${detail.realQuantity}`;
        detailDiv.appendChild(quantitySpan);

        div.appendChild(detailDiv);
    }); 
 }

 setInventory(inventory) {
      this.inventory = inventory;
  } 
}
if (!window.customElements.get(TAG.AON_MOBILE_INVENTORY)) {
    window.customElements.define(TAG.AON_MOBILE_INVENTORY, AonMobileInventory);
}