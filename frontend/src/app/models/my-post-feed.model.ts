import { OrderPost } from './order-post.model';

export interface InterestedNeighbor {
  userId: string;
  displayName: string;
  phoneNumber: string;
  interestedAt: string;
}

export interface MyPostFeed {
  post: OrderPost;
  interestedUsers: InterestedNeighbor[];
}
