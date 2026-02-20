import { AfterViewInit, Directive, ElementRef, Input, NgZone, OnDestroy, inject, numberAttribute } from '@angular/core';

@Directive({
  selector: '[appParallax]',
  standalone: true
})
export class ParallaxScrollDirective implements AfterViewInit, OnDestroy {
  @Input({ alias: 'appParallax', transform: numberAttribute }) strength = 26;

  private readonly zone = inject(NgZone);
  private rafId = 0;

  private readonly onViewportChange = () => {
    if (this.rafId !== 0) {
      return;
    }

    this.rafId = window.requestAnimationFrame(() => {
      this.rafId = 0;
      this.updateOffset();
    });
  };

  constructor(private readonly elementRef: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const element = this.elementRef.nativeElement;
    element.classList.add('parallax-scroll');

    this.zone.runOutsideAngular(() => {
      window.addEventListener('scroll', this.onViewportChange, { passive: true });
      window.addEventListener('resize', this.onViewportChange, { passive: true });
      this.onViewportChange();
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.onViewportChange);
    window.removeEventListener('resize', this.onViewportChange);
    if (this.rafId !== 0) {
      window.cancelAnimationFrame(this.rafId);
      this.rafId = 0;
    }
  }

  private updateOffset(): void {
    const element = this.elementRef.nativeElement;
    const viewportHeight = window.innerHeight || 1;
    const rect = element.getBoundingClientRect();
    const elementCenter = rect.top + rect.height / 2;
    const viewportCenter = viewportHeight / 2;
    const distanceFromCenter = (viewportCenter - elementCenter) / viewportCenter;
    const bounded = Math.max(-1, Math.min(1, distanceFromCenter));
    const offset = bounded * this.strength;

    element.style.setProperty('--parallax-offset', `${offset.toFixed(2)}px`);
  }
}
