import { CollectionViewer, SelectionChange } from '@angular/cdk/collections';
import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, ViewChild, OnInit, OnDestroy, AfterViewChecked, Injectable, ViewChildren, ElementRef, Renderer2 } from '@angular/core';
import { MatTreeNode } from '@angular/material';
import { BehaviorSubject, merge, Observable, Observer, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { InvoiceService } from '../invoice.service';
import { SharedService, AonService } from '../../services/services';
import { AonMaker } from '../../models/models';
import { Invoice, Company, InvoiceType} from '../../models/AonModel';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { InvoiceNewDialogComponent } from '../invoice-new-dialog/invoice-new-dialog.component';
import { MatDialog } from '@angular/material';
import { NgxImageCompressService } from 'ngx-image-compress';
import { RootLoader } from '../../utils/loader';

/** Flat node with expandable and level information */
export class InvoiceTypeNode {
  constructor(public item: string, public level = 1,
     public expandable = false, public icon: string, public filter = {}) {

     }
}
@Injectable()
export class InvoiceTypeDataSource {
  dataChange = new BehaviorSubject<InvoiceTypeNode[]>([]);
  get data(): InvoiceTypeNode[] { return this.dataChange.value; }
  set data(value: InvoiceTypeNode[]) {
    this.treeControl.dataNodes = value;
    this.dataChange.next(value);
  }

  constructor(private treeControl: FlatTreeControl<InvoiceTypeNode>,
      private invoiceService: InvoiceService) {
  }

  connect(collectionViewer: CollectionViewer): Observable<InvoiceTypeNode[]> {
    this.treeControl.expansionModel.changed.subscribe(change => {
      if ((change as SelectionChange<InvoiceTypeNode>).added ||
        (change as SelectionChange<InvoiceTypeNode>).removed) {
        this.handleTreeControl(change as SelectionChange<InvoiceTypeNode>);
      }
    });
    return merge(collectionViewer.viewChange, this.dataChange).pipe(map(() => this.data));
  }

  /** Handle expand/collapse behaviors */
  handleTreeControl(change: SelectionChange<InvoiceTypeNode>) {
    if (change.added) {
      change.added.forEach(node => this.toggleNode(node, true));
    }
    if (change.removed) {
      change.removed.slice().reverse().forEach(node => this.toggleNode(node, false));
    }
  }

  /**
   * Toggle the node, remove from display list
   */
  toggleNode(node: InvoiceTypeNode, expand: boolean) {
    let ni = InvoiceType.SALES;
    if (node.item === 'Tickets/Justificantes') {
      ni =  InvoiceType.UNDEDUCTIBLE;
    } else if (node.item === 'Recibidas') {
      ni =  InvoiceType.PURCHASE;
    }

    const children = node.item === 'Recibidas' ?  this.initializeRecibidasData() : this.invoiceService.getInvoiceType(ni);
    const index = this.data.indexOf(node);
    if (!children || index < 0) { // If no children, or cannot find the node, no op
      return;
    }

    if (expand) {
      children.subscribe(result => {
        const nodes = result.map(o =>
          new InvoiceTypeNode(this.abreviate(o.name), node.level + 1, false, o.icon ? o.icon : 'folder',
           o.filter
            ? o.filter
            : {
              category: o.category,
              type: ni,
              status: 'accepted'
          })); // false de MOMENTO1!!
        this.data.splice(index + 1, 0, ...nodes);
        this.dataChange.next(this.data);
      });
    } else {
      let count = 0;
      for (let i = index + 1; i < this.data.length
        && this.data[i].level > node.level; i++, count++) {}
      this.data.splice(index + 1, count);
      this.dataChange.next(this.data);
    }
  }

  initializeRecibidasData(): Observable<any> {
    return Observable.create((observer: Observer<any>) => {
      observer.next([
        {
          name: 'Compra',
          expand: false,
          filter: {status: 'accepted', type: InvoiceType.PURCHASE},
          icon: 'archive'
        }, {
          name: 'Gastos',
          expand: false,
          filter: {status: 'accepted', type: InvoiceType.EXPENSES},
          icon: 'archive'
        }
      ]);
      observer.complete();
    });
  }

  initializeInboxData(): Observable<any> {
    return Observable.create((observer: Observer<any>) => {
      observer.next([
        {
          name: 'Pendientes',
          expand: false,
          filter: {status: 'inbox', pending: true},
          icon: 'error'
        }, {
          name: 'Verificadas',
          expand: false,
          filter: {status: 'inbox', verified: true},
          icon: 'check_circle'
        }
      ]);
      observer.complete();
    });
  }

  abreviate(str: string) {
    if (str.length < 31) {
      return str;
    } else {
      return str.substring(0, 30) + '...';
    }
  }

}

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css']
})
export class InvoiceComponent implements OnInit, OnDestroy, AfterViewChecked {

  data = this.initializeData();
  data2 = this.initializeData2();
  treeControl: FlatTreeControl<InvoiceTypeNode>;
  dataSource: InvoiceTypeDataSource;
  dataSource2: InvoiceTypeDataSource;
  hasListener: any[] = [];
  oldHighlight: ElementRef;
  sheet: boolean;
  company: Company = AonMaker.createCompany();
  subscription: Subscription;
  acceptedMimeTypes = [
    'image/gif',
    'image/jpeg',
    'image/png',
    'application/pdf'
  ];

  @ViewChildren(MatTreeNode, { read: ElementRef }) treeNodes: ElementRef[];
  @ViewChild('fileInvoiceInput', {static: false}) fileInvoiceInput: ElementRef;
  constructor(private invoiceService: InvoiceService, private router: Router,
      private renderer: Renderer2, private service: SharedService,
      private aonService: AonService, private location: Location,
      private matIconRegistry: MatIconRegistry, public dialog: MatDialog,
      private domSanitizer: DomSanitizer, private imageCompress: NgxImageCompressService ) {

    this.service.isSheet.subscribe(value => {
      this.sheet = value;
    });

    this.matIconRegistry.addSvgIcon(
      'tedi-document-accepted',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../../assets/icons/document-accepted.svg')
    );

    this.matIconRegistry.addSvgIcon(
      'tedi-document-pending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../../assets/icons/document-pending.svg')
    );

    this.invoiceService.isMenuExpanded = !this.isMobile();
    this.invoiceService.menuExpanded.subscribe(() => {
      if (!this.invoiceService.isMenuExpanded) {
        this.treeControl.collapseAll();
      }
    });

    this.treeControl = new FlatTreeControl<InvoiceTypeNode>(this.getLevel, this.isExpandable);
    this.dataSource = new InvoiceTypeDataSource(this.treeControl, invoiceService);
    this.dataSource2 = new InvoiceTypeDataSource(this.treeControl, invoiceService);

    this.dataSource.data = this.initialize();
    this.dataSource2.data = this.initialize2();

    this.onload();
  }

  ngOnInit() {

  }

  ngOnDestroy() {
    this.invoiceService.filter = undefined;
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  invoiceNew(): void {
    const dialogRef = this.dialog.open(InvoiceNewDialogComponent, {
      width: '250px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'tedi-user-dialog-panel',
      position: {
        bottom: '20px',
        right: '20px'
      },
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const srr = this.router.routeReuseStrategy.shouldReuseRoute;
        if(this.sheet){
          this.router.routeReuseStrategy.shouldReuseRoute = function() {
            return false;
          };
        }

        this.invoiceService.isNew = true;
        this.invoiceService.newType = result.type;
        RootLoader.angularPanel(this.router, this.location, '/invoice/sheet').then(r => {
          this.router.routeReuseStrategy.shouldReuseRoute = srr;
          return r;
        });
      }
    });
  }

  hasFile() : boolean {
    return this.invoiceService.invoice.file != undefined;
  }

  validateFile(file: any) {
    return this.acceptedMimeTypes.includes(file.type); // && file.size < 500000;
  }

  preview() {
    const file = this.fileInvoiceInput.nativeElement.files[0];
    const READER = new FileReader();
    READER.readAsDataURL(file);
    READER.onload = (_event) => {
      if (file.type.match(/image\/*/) == null) {
        this.attach(READER.result as string, file.type)
      } else {
        var img = new Image();
        img.onload = () => {
          let ratio: number;
          if ( img.height > img.width ) {
            ratio = (100 / (img.height / 1024));
          } else {
            ratio = (100 / ( img.width / 1024));
          }

          let orientation: any;
          this.imageCompress.compressFile(img.src, orientation, ratio, 100).then(
            result => this.attach(result as string, file.type)
          );
        };
        img.src = READER.result as string;
      }
    };
  }

  attach(fileDataUri: string,  mimetype: string): void {
    if (fileDataUri.length > 0) {
      const base64File = fileDataUri.split(',')[1];
      const cp = this.service.getActualCompany();
      const data = {
        company: cp ,
        content: base64File,
        contentType: mimetype,
        contentEncoding: 'base64',
        invoice: undefined,
        uuid: undefined,
        status: undefined
      };
      if (this.invoiceService.invoice && this.invoiceService.invoice.id) {
        data.uuid = this.invoiceService.invoice.id;
        data.status = this.invoiceService.invoice.status;
        data.invoice = this.invoiceService.invoice;
      }
      this.service.loading = true;
      this.aonService.createInvoice(data).subscribe((r: Invoice) => {
          if (!this.invoiceService.invoice.id) {
            this.invoiceService.setInvoice(r);
          }
          this.invoiceService.invoice.file = r.file;
          this.invoiceService.invoiceFile = undefined;
          this.invoiceService.getFile().subscribe();
          this.invoiceService.expandFile(true);
          this.service.loading = false;
        });
    }
  }

  initialize(): InvoiceTypeNode[] {
    return this.data.map(o => new InvoiceTypeNode(o.name, 0, o.expand, o.icon, o.filter));
  }

  initialize2(): InvoiceTypeNode[] {
    return this.data2.map(o => new InvoiceTypeNode(o.name, 0, o.expand, o.icon, o.filter));
  }

  getLevel = (node: InvoiceTypeNode) => node.level;
  isExpandable = (node: InvoiceTypeNode) => node.expandable;
  hasChild = (_: number, _nodeData: InvoiceTypeNode) => _nodeData.expandable;


  updateHighlight = (newHighlight: ElementRef) => {
    if (this.oldHighlight) {
      this.renderer.removeClass(this.oldHighlight.nativeElement, 'tedi-selected-node');
    }
    this.renderer.addClass(newHighlight.nativeElement, 'tedi-selected-node');
    this.oldHighlight = newHighlight;
  }

  ngAfterViewChecked() {
    this.onload();
  }

  onload() {
    if (this.treeNodes) {
      this.treeNodes.forEach((reference) => {
        if (!this.hasListener.includes(reference.nativeElement)) {
          this.renderer.listen(reference.nativeElement, 'click', () => {
            this.updateHighlight(reference);
          });

          if (this.hasListener.length === 0) {
            this.renderer.addClass(reference.nativeElement, 'tedi-selected-node');
            this.oldHighlight = reference;
          }
          this.hasListener = this.hasListener.concat([ reference.nativeElement ]);
        }
      });
    }
  }

  getMarginA(): string {
      return this.invoiceService.isMenuExpanded ? '0px' : '20px';
  }

  getMarginB(node: any): string {
    const a = (node.item === 'Pendientes' || node.item === 'Verificadas') ? '50px' : '40px';
    return this.invoiceService.isMenuExpanded ? a : '20px';
  }

  getMarginC(): string {
      return this.invoiceService.isMenuExpanded ? '20px' : '0px';
  }

  getColor(node:any): string {
    return (node.item === 'Pendientes' || node.item === 'Verificadas') ? 'gray' : 'black';
  }

  isMenuExpanded(): boolean {
    return this.invoiceService.isMenuExpanded;
  }

  isMobile(): boolean {
    return this.service.isMobile;
  }

  selection(node) {
    //this.invoiceService.invoiceList = undefined;
    const nav = this.isMobile() ? 'invoice/list' : 'invoice/table';
    RootLoader.angularPanel(this.router, this.location, nav);
    this.service.isMultipleSelection.next(false);
    this.invoiceService.setFilter(node.filter);
    if (this.isMobile()) {
      this.invoiceService.expandMenu(false);
    }
  }

  initializeData(): any[] {
    return [{
        name: 'Emitidas',
        expand: false,
        filter:  {
          type: [InvoiceType.SALES],
          status: 'accepted'
        },
        icon: 'unarchive'
      },
      {
        name: 'Recibidas',
        expand: true,
        filter:  {
          type: [InvoiceType.PURCHASE, InvoiceType.EXPENSES],
          status: 'accepted'
        },
        icon: 'archive'
      },
      {
        name: 'Tickets/Justificantes',
        expand: false,
        filter:  {
          type: [InvoiceType.UNDEDUCTIBLE],
          status: 'accepted'
        },
        icon: 'receipt'
      }
    ];
  }


  initializeData2(): any[] {
    return [
      {
        name: 'Inbox',
        expand: false,
        filter: {status: 'inbox'},
        icon: 'inbox'
      },
      {
        name: 'Rechazadas',
        expand: false,
        filter: {status: 'refused'},
        icon: 'report'
      }, {
        name: 'Papelera',
        expand: false,
        filter: {status: 'trash'},
        icon: 'delete'
      }
    ];
  }
}
