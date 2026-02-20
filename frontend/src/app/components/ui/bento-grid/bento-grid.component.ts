import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bento-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bento-grid.component.html',
  styleUrls: ['./bento-grid.component.scss']
})
export class BentoGridComponent {
  @Input() className = '';
}
