import { AonBasicTable } from "../../../components/aon-basic-table";
import { AonDialog } from "../../../components/aon-dialog";
import { AonElement } from "../../../components/AonElement";
import { createInput, createQuantity } from "../../../components/CreateComponent";
import { CONSTANT, TAG, MATERIAL_ICONS, EVENT, MSG } from "../../../environments/environments";
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
    let data = {sscc, full:true};
    getPackage(data).then(itemPackage => {
        if(itemPackage.delivery) {
            this.showMessageError("El envase está incluido en el albarán: " + itemPackage.delivery.reference);
        } else {
            let data = {
                inventory: this.inventory.id,
                itemPackage,
                details: []
            }
            if(this.inventory.details.filter(f => f.item.id == itemPackage.id).length > 0) {
                this.showMessageError("El envase ya está incluido en el inventario.");
            } else {
                itemPackage.itemComposition.forEach(element => {
                    this.inventory.details.filter(f => f.item.id == element.composition.id).forEach(d => {
                        let detail = {
                            detail: d.id,
                            itemPackage: itemPackage.id,
                            item: element.composition,
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

    let table = new AonBasicTable();
    table.id = this.id + 'DialogTable';
    div.appendChild(table);
    this.getElement(table.TABLE).style.borderSpacing = '0px 10px';
    

  
    let d = this.getDialog();
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.ADD_TO_INVENTORY);
    d.setContent(div);
    d.addAcceptAction(() => {
        d.remove();
        this.saveInventoryDetails(data);
        this.resetScanner();
        // AÑADIR DETALLES AL INVENTARIO
    });
    
    data.details.forEach(detail => {
        table.addRow();

        let itemSpan = this.createSpan();
        itemSpan.innerHTML = `${detail.item.name} #${detail.item.serialNumber}`;
        table.addCell(itemSpan);
        
        let qId = this.id + 'Quantity' + detail.item.id;
        let quantityBox = createQuantity(qId, MSG.QUANTITY);
        quantityBox.addEventListener(EVENT.CHANGE, () => {
            if(quantityBox.getQuantity() <= 0) {
                this.showMessageError("La cantidad debe ser mayor que cero.");
                quantityBox.setQuantity(detail.quantity);
            } else {
                quantityBox.setQuantityFormat(quantityBox.value);
                detail.quantity = quantityBox.getQuantity();
            }
        });
        table.addCell(quantityBox);
        quantityBox.setTags(detail.item);
        quantityBox.setQuantity(detail.quantity);
    });

    d.open();
 }

 saveInventoryDetails(data) {    
    let object = {
        inventory: data.inventory,
        itemPackage: data.itemPackage.id,
        details: data.details.map(detail => {
            detail.item = detail.item.id;
            return detail;
        })
    };
    saveInventoryDetails(object).then(() => {
        this.showMessage("Detalles de inventario actualizados.");
        
        if(this.inventory.details.filter(f => f.item.id == data.itemPackage).length == 0) {
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

     
    let table = new AonBasicTable();
    table.id = this.DETAIL_TABLE;
    div.appendChild(table);
    this.getElement(table.TABLE).style.borderSpacing = '0px 10px';

    this.inventory.details.forEach(detail => {
        table.addRow();
        
        let itemSpan = this.createSpan();
        itemSpan.innerHTML = `${detail.item.name} #${detail.item.serialNumber}`;
        table.addCell(itemSpan);

        let quantitySpan = this.createSpan();
        quantitySpan.innerHTML = `${detail.realQuantity}`;
        table.addCell(quantitySpan);
    }); 
}

 setInventory(inventory) {
      this.inventory = inventory;
  } 
}
if (!window.customElements.get(TAG.AON_MOBILE_INVENTORY)) {
    window.customElements.define(TAG.AON_MOBILE_INVENTORY, AonMobileInventory);
}