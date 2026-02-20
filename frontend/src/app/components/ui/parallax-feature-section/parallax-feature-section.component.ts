import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { ParallaxScrollDirective } from '../../../directives/parallax-scroll.directive';
import { RevealOnScrollDirective } from '../../../directives/reveal-on-scroll.directive';

interface FeatureSection {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  reverse: boolean;
  tag: string;
}

@Component({
  selector: 'app-parallax-feature-section',
  standalone: true,
  imports: [CommonModule, RevealOnScrollDirective, ParallaxScrollDirective],
  templateUrl: './parallax-feature-section.component.html',
  styleUrls: ['./parallax-feature-section.component.scss']
})
export class ParallaxFeatureSectionComponent {
  readonly sections: FeatureSection[] = [
    {
      id: 1,
      title: 'Post Order Gap Instantly',
      description: 'Create a nearby collaboration post in seconds with store, ETA, and amount remaining for minimum checkout.',
      imageUrl: 'https://images.unsplash.com/photo-1556740758-90de374c12ad?auto=format&fit=crop&w=1200&q=80',
      reverse: false,
      tag: 'Feature 01'
    },
    {
      id: 2,
      title: 'Find People In Your Radius',
      description: 'See active feeds only around your area and join orders that match your location and timing preferences.',
      imageUrl: 'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1200&q=80',
      reverse: true,
      tag: 'Feature 02'
    },
    {
      id: 3,
      title: 'Close Handoffs Securely',
      description: 'Track interested collaborators, reveal contact only when needed, and complete the exchange with clarity.',
      imageUrl: 'https://images.unsplash.com/photo-1556742393-d75f468bfcb0?auto=format&fit=crop&w=1200&q=80',
      reverse: false,
      tag: 'Feature 03'
    }
  ];
}
