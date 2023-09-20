import { Injectable, Renderer2 } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ICollection, IFilter, IMark, MarkFactory } from 'libraries/AonSDK/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class MarkService extends CommonService {

  private singleObjectCrud = new MarkFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MarkFactory().createMultipleObjectCrud();

  constructor(
    private domSanitizer: DomSanitizer,
    private renderer: Renderer2
  ) {
    super();
  }


  async getMarkList(filter?: IFilter): Promise<ICollection<IMark>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getMark(pkey: any): Promise<IMark> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.createElement(mark)).result;
  }

  async updateMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.updateElement(mark)).result;
  }

  async deleteMark(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  // Obtener lista de marcajes y después generar un documento PDF
  async downloadMarkList(filter?: IFilter): Promise<void> {
    const markList = await this.getMarkList(filter);

    // Crear el contenido HTML para el documento PDF
    let content = `<h1>Lista de Marcajes</h1>`;
    content += `<table>`;
    content += `<tr><th>ID</th><th>Name</th><th>Last Name</th></tr>`;
    markList.forEach(mark => {
      content += `<tr><td>${mark.Id}</td><td>${mark.Name}</td><td>${mark.Lastname}</td></tr>`;
    });
    content += `</table>`;

    // Crear un elemento HTML oculto para generar el PDF
    const pdfContainer = this.renderer.createElement('div');
    this.renderer.setStyle(pdfContainer, 'display', 'none');
    this.renderer.setProperty(pdfContainer, 'innerHTML', content);
    this.renderer.appendChild(document.body, pdfContainer);

     // Generar el archivo PDF
    const pdfBlob = new Blob([pdfContainer.innerHTML], { type: 'application/pdf' });
    const pdfUrl: SafeUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(pdfBlob));

    // Descargar el archivo PDF
    const a = this.renderer.createElement('a');
    this.renderer.setAttribute(a, 'href', pdfUrl.toString());
    this.renderer.setAttribute(a, 'download', 'markList.pdf');
    this.renderer.appendChild(document.body, a);
    a.click();
    this.renderer.removeChild(document.body, a);
    this.renderer.removeChild(document.body, pdfContainer);

  }

}
