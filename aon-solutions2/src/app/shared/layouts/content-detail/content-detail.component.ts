import { Component, Input, OnInit } from '@angular/core';
import { ContainerService } from '../../services/container.service';

@Component({
  selector: 'app-content-detail',
  templateUrl: './content-detail.component.html',
  styleUrls: ['./content-detail.component.scss']
})
export class ContentDetailComponent implements OnInit {

  @Input() heightHeader : string;
  @Input() padding : string;

  constructor(public containerService : ContainerService) {
    this.heightHeader = '0';
    this.padding = '0';
  }

  ngOnInit(): void {
  }

}
