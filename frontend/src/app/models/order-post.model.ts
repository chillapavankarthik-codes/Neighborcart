export interface OrderPost {
  id: string;
  initiatorAlias: string;
  storeName: string;
  latitude: number;
  longitude: number;
  addressHint: string;
  expectedDeliveryTime: string;
  minimumOrderAmount: number;
  currentCartAmount: number;
  remainingAmount: number;
  postRadiusMiles: number;
  title: string;
  notes: string;
  visiblePhone: string;
  phoneRevealEnabled: boolean;
  interestedCount: number;
  createdAt: string;
  distanceMiles: number;
  viewerInterested: boolean;
  canManageContact: boolean;
}
