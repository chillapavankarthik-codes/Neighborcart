import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { ParallaxScrollDirective } from '../../../directives/parallax-scroll.directive';
import { RevealOnScrollDirective } from '../../../directives/reveal-on-scroll.directive';
import { BentoCardComponent } from '../bento-card/bento-card.component';
import { BentoGridComponent } from '../bento-grid/bento-grid.component';

interface BentoFeature {
  icon: 'file' | 'search' | 'globe' | 'calendar' | 'bell';
  name: string;
  description: string;
  metric: string;
  helperText: string;
  href: string;
  cta: string;
  image: string;
  className: string;
}

@Component({
  selector: 'app-bento-demo',
  standalone: true,
  imports: [CommonModule, BentoGridComponent, BentoCardComponent, RevealOnScrollDirective, ParallaxScrollDirective],
  templateUrl: './bento-demo.component.html',
  styleUrls: ['./bento-demo.component.scss']
})
export class BentoDemoComponent {
  readonly features: BentoFeature[] = [
    {
      icon: 'file',
      name: 'Instant Gap Posting',
      description: 'Drop a post with store, ETA, and amount left to unlock free delivery.',
      metric: '15 sec setup',
      helperText: 'No extra cart stuffing',
      href: '#create-post',
      cta: 'Create post',
      image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
      className: 'feature-1'
    },
    {
      icon: 'search',
      name: 'Radius-Based Discovery',
      description: 'See nearby neighbors only in your chosen range and join what fits your trip.',
      metric: '1-10 mile filter',
      helperText: 'Fast local matching',
      href: '#nearby-feed',
      cta: 'Open feed',
      image: 'https://images.unsplash.com/photo-1481437156560-3205f6a55735?auto=format&fit=crop&w=1200&q=80',
      className: 'feature-2'
    },
    {
      icon: 'globe',
      name: 'Multi-Store Support',
      description: 'Match orders from Walmart, Costco, Sam\'s Club, Instacart, and food apps.',
      metric: '6+ platforms',
      helperText: 'One flow, many stores',
      href: '#nearby-feed',
      cta: 'Browse stores',
      image: 'https://images.unsplash.com/photo-1604719312566-8912e9227c6a?auto=format&fit=crop&w=1200&q=80',
      className: 'feature-3'
    },
    {
      icon: 'calendar',
      name: 'ETA-Driven Handoffs',
      description: 'Plan pickup around ETA so both people save time and avoid uncertain waits.',
      metric: 'Shared timeline',
      helperText: 'Clear handoff window',
      href: '#chat',
      cta: 'Plan handoff',
      image: 'https://images.unsplash.com/photo-1521790797524-b2497295b8a0?auto=format&fit=crop&w=1200&q=80',
      className: 'feature-4'
    },
    {
      icon: 'bell',
      name: 'Secure Contact Reveal',
      description: 'Keep numbers masked by default and reveal only when the order host approves.',
      metric: 'Privacy first',
      helperText: 'Controlled reveal flow',
      href: '#chat',
      cta: 'See privacy flow',
      image: 'https://images.unsplash.com/photo-1556745757-8d76bdb6984b?auto=format&fit=crop&w=1200&q=80',
      className: 'feature-5'
    }
  ];
}
