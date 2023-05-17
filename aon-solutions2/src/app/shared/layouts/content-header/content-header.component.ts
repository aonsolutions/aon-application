import { Component, Input, OnInit } from '@angular/core';
import { ContainerService } from '../../services/container.service';

@Component({
  selector: 'app-content-header',
  templateUrl: './content-header.component.html',
  styleUrls: ['./content-header.component.scss']
})
export class ContentHeaderComponent implements OnInit {

  @Input() heightHeader : number;

  constructor(public containerService : ContainerService) { 
    this.heightHeader = 0;
  }

  ngOnInit(): void {
  }

}
