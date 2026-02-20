import { AfterViewInit, Directive, ElementRef, Input, OnDestroy, booleanAttribute, numberAttribute } from '@angular/core';

@Directive({
  selector: '[appRevealOnScroll]',
  standalone: true
})
export class RevealOnScrollDirective implements AfterViewInit, OnDestroy {
  @Input({ transform: numberAttribute }) revealDelay = 0;
  @Input({ transform: booleanAttribute }) revealOnce = true;

  private observer: IntersectionObserver | null = null;

  constructor(private readonly elementRef: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const element = this.elementRef.nativeElement;
    element.classList.add('reveal-on-scroll');
    element.style.setProperty('--reveal-delay', `${this.revealDelay}ms`);

    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            element.classList.add('is-visible');
            if (this.revealOnce) {
              this.observer?.unobserve(element);
            }
            return;
          }

          if (!this.revealOnce) {
            element.classList.remove('is-visible');
          }
        });
      },
      {
        threshold: 0.18,
        rootMargin: '0px 0px -8% 0px'
      }
    );

    this.observer.observe(element);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
    this.observer = null;
  }
}
