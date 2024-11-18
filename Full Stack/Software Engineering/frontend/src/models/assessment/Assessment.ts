import { ISOtoString } from '@/services/ConvertDateService';

export default class Assessment {
  id: number | null = null;
  review!: string;
  reviewDate!: string;
  institutionId!: number;
  volunteerName!: string;

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.review = jsonObj.review;
      this.reviewDate = ISOtoString(jsonObj.reviewDate);
      this.institutionId = jsonObj.institutionId;
      this.volunteerName = jsonObj.volunteerName;
    }
  }
}
