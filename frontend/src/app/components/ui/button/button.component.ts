import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ui-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {
  @Input() variant: 'default' | 'ghost' | 'outline' = 'default';
  @Input() size: 'default' | 'sm' | 'lg' = 'default';
  @Input() href = '';
}
