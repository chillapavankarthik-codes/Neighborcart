import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, Input, OnDestroy, ViewChild } from '@angular/core';

@Component({
  selector: 'app-spotlight-hover',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './spotlight-hover.component.html',
  styleUrls: ['./spotlight-hover.component.scss']
})
export class SpotlightHoverComponent implements AfterViewInit, OnDestroy {
  @ViewChild('spotlightNode', { static: true }) spotlightNode!: ElementRef<HTMLDivElement>;

  @Input() className = '';
  @Input() size = 220;

  isHovered = false;
  mouseX = 0;
  mouseY = 0;

  private parentElement: HTMLElement | null = null;
  private readonly onMouseMove = (event: MouseEvent) => {
    if (!this.parentElement) {
      return;
    }

    const bounds = this.parentElement.getBoundingClientRect();
    this.mouseX = event.clientX - bounds.left;
    this.mouseY = event.clientY - bounds.top;
  };

  private readonly onMouseEnter = () => {
    this.isHovered = true;
  };

  private readonly onMouseLeave = () => {
    this.isHovered = false;
  };

  ngAfterViewInit(): void {
    const parent = this.spotlightNode.nativeElement.parentElement;
    if (!parent) {
      return;
    }

    parent.style.position = parent.style.position || 'relative';
    parent.style.overflow = 'hidden';
    this.parentElement = parent;

    parent.addEventListener('mousemove', this.onMouseMove);
    parent.addEventListener('mouseenter', this.onMouseEnter);
    parent.addEventListener('mouseleave', this.onMouseLeave);
  }

  ngOnDestroy(): void {
    if (!this.parentElement) {
      return;
    }

    this.parentElement.removeEventListener('mousemove', this.onMouseMove);
    this.parentElement.removeEventListener('mouseenter', this.onMouseEnter);
    this.parentElement.removeEventListener('mouseleave', this.onMouseLeave);
  }
}
