import { AonElement } from "../../components/AonElement.js";
import { EVENT, TAG } from "../../environments/environments.js";
import { getTrailData } from "../../services/invoiceService.js";

export class AonTrial extends AonElement {

  trialData;

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  constructor() {
    super();
    this.id = this.id || "aonTrial";
    this.style.width = "100%";
    this.style.display = "flex";
    this.style.flexDirection = "column";
    this.style.gap = ".5rem";
    this.style.height = "100%";
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  async build() {
    this.draw();
  }

  async draw() {
    this.innerHTML = "";

    this.trialData = await this.getTrialData();
    console.log("Trial Data");
    console.log(this.trialData);

    let trialInvoiceDiv = this.createElement(TAG.DIV);
    trialInvoiceDiv.style.border = "1px solid #ebebeb";
    trialInvoiceDiv.style.textAlign = "center";
    trialInvoiceDiv.style.padding = "8px";
    trialInvoiceDiv.style.borderRadius = "5px";
    trialInvoiceDiv.style.flex = "1";
    trialInvoiceDiv.style.display = "flex";
    trialInvoiceDiv.style.justifyContent = "center";
    trialInvoiceDiv.style.flexDirection = "column";
    trialInvoiceDiv.classList.add("aonHoverGray");

    let trialCountDiv = this.createElement(TAG.DIV);
    trialCountDiv.classList.add("aonInvoiceHomePendingRecordNumber");
    trialCountDiv.innerHTML = !this.trialData || !this.trialData.trialInvoices ? 0 : this.trialData.trialInvoices;

    let trialTextDiv = this.createElement(TAG.DIV);
    trialTextDiv.classList.add("aonInvoiceHomePendingRecordName");
    trialTextDiv.innerHTML = "Facturas Disponibles";

    trialInvoiceDiv.appendChild(trialCountDiv);
    trialInvoiceDiv.appendChild(trialTextDiv);
  
    this.appendChild(trialInvoiceDiv);

    let typesInvoiceDiv = this.createElement(TAG.DIV);
    typesInvoiceDiv.style.border = "1px solid #ebebeb";
    typesInvoiceDiv.style.textAlign = "center";
    typesInvoiceDiv.style.padding = "8px";
    typesInvoiceDiv.style.borderRadius = "5px";
    typesInvoiceDiv.style.flex = "1";
    typesInvoiceDiv.style.display = "flex";
    typesInvoiceDiv.style.flexDirection = "column";
    typesInvoiceDiv.style.justifyContent = "center";
    typesInvoiceDiv.classList.add("aonHoverGray");

    let typesInvoiceTitleDiv = this.createElement(TAG.DIV);
    typesInvoiceTitleDiv.classList.add("aonInvoiceHomePendingName");
    typesInvoiceTitleDiv.innerHTML = "Detalle Facturas Registradas";
    typesInvoiceDiv.appendChild(typesInvoiceTitleDiv);

    let typesInvoiceListDiv = this.createElement(TAG.DIV);
    typesInvoiceListDiv.classList.add("aonInvoiceHomePendingCounterRow");

    let typesInvoiceSalesDiv = this.createElement(TAG.DIV);
    typesInvoiceSalesDiv.classList.add("aonInvoiceHomePendingIssuedDiv");

    let typesInvoiceSalesCountDiv = this.createElement(TAG.DIV);
    typesInvoiceSalesCountDiv.classList.add("aonInvoiceHomePendingIssuedNumber");
    typesInvoiceSalesCountDiv.innerHTML = !this.trialData ? 0 : this.trialData.saleInvoices;

    let typesInvoiceSalesTextDiv = this.createElement(TAG.DIV);
    typesInvoiceSalesTextDiv.classList.add("aonInvoiceHomePendingIssuedName");
    typesInvoiceSalesTextDiv.innerHTML = "Emitidas";

    typesInvoiceSalesDiv.appendChild(typesInvoiceSalesCountDiv);
    typesInvoiceSalesDiv.appendChild(typesInvoiceSalesTextDiv);
    typesInvoiceListDiv.appendChild(typesInvoiceSalesDiv);

    let typesInvoicePurchaseDiv = this.createElement(TAG.DIV);
    typesInvoicePurchaseDiv.classList.add("aonInvoiceHomePendingIssuedDiv");

    let typesInvoicePurchaseCountDiv = this.createElement(TAG.DIV);
    typesInvoicePurchaseCountDiv.classList.add("aonInvoiceHomePendingReceivedNumber");
    typesInvoicePurchaseCountDiv.innerHTML = !this.trialData ? 0 : this.trialData.expenseInvoices;

    let typesInvoicePurchaseTextDiv = this.createElement(TAG.DIV);
    typesInvoicePurchaseTextDiv.classList.add("aonInvoiceHomePendingReceivedName");
    typesInvoicePurchaseTextDiv.innerHTML = "Recibidas";

    typesInvoicePurchaseDiv.appendChild(typesInvoicePurchaseCountDiv);
    typesInvoicePurchaseDiv.appendChild(typesInvoicePurchaseTextDiv);
    typesInvoiceListDiv.appendChild(typesInvoicePurchaseDiv);

    let typesInvoiceTicketDiv = this.createElement(TAG.DIV);
    typesInvoiceTicketDiv.classList.add("aonInvoiceHomePendingIssuedDiv");

    let typesInvoiceTicketCountDiv = this.createElement(TAG.DIV);
    typesInvoiceTicketCountDiv.classList.add("aonInvoiceHomePendingTicketNumber");
    typesInvoiceTicketCountDiv.innerHTML = !this.trialData ? 0 : this.trialData.ticketInvoices;

    let typesInvoiceTicketTextDiv = this.createElement(TAG.DIV);
    typesInvoiceTicketTextDiv.classList.add("aonInvoiceHomePendingTicketName");
    typesInvoiceTicketTextDiv.innerHTML = "Tickets";

    typesInvoiceTicketDiv.appendChild(typesInvoiceTicketCountDiv);
    typesInvoiceTicketDiv.appendChild(typesInvoiceTicketTextDiv);
    typesInvoiceListDiv.appendChild(typesInvoiceTicketDiv);

    typesInvoiceDiv.appendChild(typesInvoiceListDiv);
  
    this.appendChild(typesInvoiceDiv);

    let peddingBookingDiv = this.createElement(TAG.DIV);
    peddingBookingDiv.style.border = "1px solid #ebebeb";
    peddingBookingDiv.style.textAlign = "center";
    peddingBookingDiv.style.padding = "8px";
    peddingBookingDiv.style.borderRadius = "5px";
    peddingBookingDiv.style.flex = "1";
    peddingBookingDiv.style.display = "flex";
    peddingBookingDiv.style.gap = ".5rem";

    let peddingDiv = this.createElement(TAG.DIV);
    peddingDiv.style.border = "1px solid #ebebeb";
    peddingDiv.style.textAlign = "center";
    peddingDiv.style.padding = "8px";
    peddingDiv.style.borderRadius = "5px";
    peddingDiv.style.flex = "1";
    peddingDiv.classList.add("aonHoverGray");

    let peddingCountDiv = this.createElement(TAG.DIV);
    peddingCountDiv.classList.add("aonInvoiceHomeRejectedNumber");
    peddingCountDiv.innerHTML = (!this.trialData || !this.trialData.trialInvoices ? 0 : this.trialData.trialInvoices) - this.trialData.saleInvoices - this.trialData.expenseInvoices - this.trialData.ticketInvoices;

    let peddingTextDiv = this.createElement(TAG.DIV);
    peddingTextDiv.classList.add("aonInvoiceHomeRejectedName");
    peddingTextDiv.innerHTML = "Restantes";

    peddingDiv.appendChild(peddingCountDiv);
    peddingDiv.appendChild(peddingTextDiv);

    peddingBookingDiv.appendChild(peddingDiv);

    let bookingDiv = this.createElement(TAG.DIV);
    bookingDiv.style.border = "1px solid #ebebeb";
    bookingDiv.style.textAlign = "center";
    bookingDiv.style.padding = "8px";
    bookingDiv.style.borderRadius = "5px";
    bookingDiv.style.flex = "1";
    bookingDiv.style.display = "flex";
    bookingDiv.style.flexDirection = "column";
    bookingDiv.style.justifyContent = "center";
    bookingDiv.classList.add("aonHoverGray");
    bookingDiv.addEventListener(EVENT.CLICK, () => {
      this.getApplication().confirmDialog(
        "Contratación",
        "Se va a proceder a navegar a la configuración para modificar la contratación. ¿Está seguro de que desea continuar?",
        async () => {
          //this.getApplication().startLoading();
  
          let aonHeader = this.getElement('aonHeader');
          aonHeader.aonConfiguration();
  
          //this.getApplication().stopLoading();
        }
      );
    });

    let bookingButton = this.createElement(TAG.I);
    bookingButton.classList.add("material-icons");
    bookingButton.style.fontSize = "2.7rem"
    bookingButton.innerHTML = "contract_edit";
    bookingDiv.appendChild(bookingButton);

    let bookingTextDiv = this.createElement(TAG.DIV);
    bookingTextDiv.classList.add("aonInvoiceHomeRejectedName");
    bookingTextDiv.innerHTML = "Ampliar Contratación";

    bookingDiv.appendChild(bookingTextDiv);

    peddingBookingDiv.appendChild(bookingDiv);
  
    this.appendChild(peddingBookingDiv);
  }
  
  async getTrialData(){
    try {
      let data = await getTrailData({});
      return data;
    } catch(e){
      console.log(e);
    }
  }
}

window.customElements.define("aon-trial", AonTrial);
