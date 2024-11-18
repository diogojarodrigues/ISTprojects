<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span 
        class="headline">
          {{ 'New Assessment' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form 
        ref="form" 
        lazy-validation>
          <v-row>
            <v-text-field 
            label="Review" 
            v-model="newAssessment.review" 
            data-cy="newAssessmentInput"
            :rules="[
    (input) =>
      reviewHasAtLeastTenCharacters(input) ||
      'Review must be at least 10 characters long',]"
            ></v-text-field>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn 
        variant="text" 
        @click="$emit('close-assessment-dialog')">
          Close
        </v-btn>
        <v-btn 
        variant="text" 
        data-cy="writeAssessment"
        @click="createAssessment"
        :disabled="!reviewHasAtLeastTenCharacters(newAssessment.review)">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Model, Component, Vue, Prop } from 'vue-property-decorator';
import Assessment from '@/models/assessment/Assessment';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';

@Component({})

export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  newAssessment: Assessment = new Assessment();

  reviewHasAtLeastTenCharacters(review: string) {
    if (review) {
      return review.length >= 10;
    }
  }

  async createAssessment() {
    try {
      // check if institution is set
      if(this.activity.institution.id){
        this.newAssessment.institutionId = this.activity.institution.id;
      }
      const result = await RemoteServices.createAssessment(
        this.newAssessment.institutionId,
        this.newAssessment);
      this.$emit('create-assessment', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped>
