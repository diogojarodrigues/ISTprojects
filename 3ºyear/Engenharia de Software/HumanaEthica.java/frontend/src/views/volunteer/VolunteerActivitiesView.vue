<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />
            <v-spacer />
          </v-card-title>
        </template>
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip v-if="availableToEnroll(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="newEnrollment"
                @click="applyForActivity(item)"
                >fas fa-right-to-bracket</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
          <v-tooltip v-if="activityHasEnded(item) && volunteerHasParticipated(item) && !volunteerHasAssessedInstitution(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="writeAssessmentButton"
                @click="newAssessment(item)"
                >create</v-icon
              >
            </template>
            <span>Write Assessment</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="currentEnrollment && editEnrollmentDialog"
        v-model="editEnrollmentDialog"
        :enrollment="currentEnrollment"
        :activity="currentActivity"
        v-on:save-enrollment="onSaveEnrollment"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
      />
      <assessment-dialog
        v-if="newAssessmentDialog && currentActivity"
        v-model="newAssessmentDialog"
        :activity="currentActivity"
        v-on:create-assessment="createAssessment"
        v-on:close-assessment-dialog="onCloseAssessmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import Participation from '@/models/participation/Participation';
import Assessment from '@/models/assessment/Assessment';
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue';
import { show } from 'cli-cursor';
import EnrollmentDialog from './EnrollmentDialog.vue';

@Component({
  components: {
    'enrollment-dialog': EnrollmentDialog,
    'assessment-dialog': AssessmentDialog,
  },
  methods: { show },
})
export default class VolunteerActivitiesView extends Vue {
  activities: Activity[] = [];
  volunteerParticipations: Participation[] = [];
  volunteerAssessments: Assessment[] = [];
  volunteerEnrollments: Enrollment[] = [];
  search: string = '';

  currentActivity: Activity | null = null;
  currentEnrollment: Enrollment | null = null;
  editEnrollmentDialog: boolean = false;


  newAssessmentDialog: boolean = false;
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.volunteerEnrollments = await RemoteServices.getVolunteerEnrollments();
      this.volunteerParticipations = await RemoteServices.getVolunteerParticipations();
      this.volunteerAssessments = await RemoteServices.getVolunteerAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async applyForActivity(activity: Activity) {
    this.currentEnrollment = new Enrollment();
    this.currentActivity = activity;
    this.editEnrollmentDialog = true;
  }

  onCloseEnrollmentDialog() {
    this.currentEnrollment = null;
    this.editEnrollmentDialog = false;
  }

  async onSaveEnrollment(enrollment: Enrollment) {
    this.volunteerEnrollments.unshift(enrollment);
    this.editEnrollmentDialog = false;
    this.currentEnrollment = null;
  }

  availableToEnroll(activity: Activity): boolean {
    if (!this.volunteerEnrollments) {
      return true;
    }
    const enrolled = this.volunteerEnrollments.some(enrollment => enrollment.activityId === activity.id);
    const expired = new Date(activity.applicationDeadline) < new Date();
    return !enrolled && !expired;
  }

  activityHasEnded(activity: Activity) {
    return new Date(activity.endingDate) < new Date();
  }

  volunteerHasParticipated(activity: Activity) {
    return this.volunteerParticipations.some((p) => p.activityId === activity.id);
  }

  volunteerHasAssessedInstitution(activity: Activity) {
    return this.volunteerAssessments.some((a) => a.institutionId === activity.institution.id);
  }

  newAssessment(activity: Activity) {
    this.currentActivity = activity;
    this.newAssessmentDialog = true;
  }


  createAssessment(assessment: Assessment) {
    this.volunteerAssessments.push(assessment);
    this.currentActivity = null;
    this.newAssessmentDialog = false;
  }

  onCloseAssessmentDialog() {
    this.currentActivity = null;
    this.newAssessmentDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
