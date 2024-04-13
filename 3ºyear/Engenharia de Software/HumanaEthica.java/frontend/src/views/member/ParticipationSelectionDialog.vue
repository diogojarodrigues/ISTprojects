<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
          <span class="headline">
          {{ 'Select Participant' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-text-field
              label="Rating"
              :rules="[
                (v) =>
                  validateRating(v) ||
                  'Rating between 1 and 5 or it can be left empty',
              ]"
              v-model="selectParticipant.rating"
              data-cy="ratingInput"
            ></v-text-field>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          variant="text"
          @click="$emit('close-participation-selection-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          variant="text"
          @click="createParticipation"
          data-cy="makeParticipant"
        >
          Make Participant
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Participation from '@/models/participation/Participation';
import RemoteServices from '@/services/RemoteServices';
import Enrollment from '@/models/enrollment/Enrollment';

@Component({})

export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;

  selectParticipant: Participation = new Participation();

  validateRating(value: any) {
    if((value > 0 && value < 6) || value == '' || value == undefined || value == null) {
      return true;
    }
    return false;

  }

  async createParticipation() {
    try {
      this.selectParticipant.volunteerId = this.enrollment.volunteer.id;
      const result = await RemoteServices.createParticipation(
        this.$store.getters.getActivity.id,
        this.selectParticipant);
      this.$emit('select-participant', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

}
</script>

<style scoped lang="scss"></style>
