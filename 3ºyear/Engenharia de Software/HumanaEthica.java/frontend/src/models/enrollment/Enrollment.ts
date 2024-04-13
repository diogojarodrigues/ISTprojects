import { ISOtoString } from '@/services/ConvertDateService';
import User from '../user/User';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  activityId: number | null = null;
  volunteer!: User;
  participating!: boolean;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.activityId = jsonObj.activityId;
      this.volunteer = jsonObj.volunteer;
      this.participating = jsonObj.participating;
    }
  }
}
