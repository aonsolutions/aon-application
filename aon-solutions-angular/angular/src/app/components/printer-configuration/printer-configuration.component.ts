import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { SharedService, CompanyService } from '../../services/services';
import { InvoiceService } from '../../invoice/invoice.service';
import { PrinterConfiguration, AonMaker } from '../../models/models';
import TEDI from '@translogia/tedi-sdk';
import { AngularFireStorage } from '@angular/fire/storage';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-printer-configuration',
  templateUrl: './printer-configuration.component.html',
  styleUrls: ['./printer-configuration.component.css'],
})
export class PrinterConfigurationComponent implements OnInit {
  private readonly API_URL = environment.apiUrl;

  properties: PrinterConfiguration = AonMaker.createPrinterConfiguration();

  acceptedMimeTypes = [
    'image/gif',
    'image/jpeg',
    'image/png'
  ];

  @ViewChild('fileInput', {static: false}) fileInput: ElementRef;
  constructor(private storage: AngularFireStorage, private service: SharedService,
    private iService: InvoiceService, public cpService: CompanyService) {
  }

  ngOnInit() {
    this.properties = AonMaker.createPrinterConfiguration(this.cpService.company.printer_configuration);
  }

  public onchange(header: string, footer: string, adjustment: boolean, detailed: boolean): void {
    this.properties.header = + header;
    this.properties.footer = + footer;
    this.properties.adjustment = adjustment || false;
    this.properties.detailed = detailed || false;
    const cp = {
      document: this.cpService.company.document,
      printer_configuration: this.properties
    };
    TEDI.company.updateCompany(cp, this.service.getToken(), this.API_URL)
    .subscribe();
  }

  attach(): void {
    const file = this.fileInput.nativeElement.files[0];
    if (file && this.validateFile(file)) {
      this.storage.upload('printer_configuration/' + this.service.getActualCompany(), file);
    }
  }

  validateFile(file: any) {
    return this.acceptedMimeTypes.includes(file.type); // && file.size < 500000;
  }

  showPreview(): void {
    this.iService.downloadPreview(this.service.getActualCompany())
      .subscribe(
        data => {
          const byteCharacters = atob(data);
          const byteNumbers = new Array(byteCharacters.length);
          for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
          }
          const byteArray = new Uint8Array(byteNumbers);
          const blob = new Blob([byteArray], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          window.open(url);
        },
        error => {
          if (error.error.text) {
            const byteCharacters = atob(error.error.text);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: 'application/pdf' });
            const url = window.URL.createObjectURL(blob);
            window.open(url);
          }
        }
      );
  }
}
