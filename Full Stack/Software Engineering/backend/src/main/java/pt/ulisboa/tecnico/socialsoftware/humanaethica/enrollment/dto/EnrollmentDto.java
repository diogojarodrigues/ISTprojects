package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
  private Integer id;
  private String motivation;
  private Integer activityId;
  private UserDto volunteerDto;
  private String enrollmentDateTime;
  private boolean participating;
    
  public EnrollmentDto() {}

  public EnrollmentDto(Enrollment enrollment) {
    this.id = enrollment.getId();
    this.motivation = enrollment.getMotivation();
    this.enrollmentDateTime = DateHandler.toISOString(enrollment.getEnrollmentDateTime());
    this.activityId = enrollment.getActivity().getId();
    this.volunteerDto = new UserDto(enrollment.getVolunteer());

    this.participating =
        enrollment.getActivity().getParticipations().stream()
            .anyMatch(
                participation ->
                    participation.getVolunteer().getId() == enrollment.getVolunteer().getId());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMotivation() {
    return motivation;
  }

  public UserDto getVolunteer() {
    return volunteerDto;
  }

  public void setVolunteer(UserDto volunteerDto) {
    this.volunteerDto = volunteerDto;
  }

  public void setMotivation(String motivation) {
    this.motivation = motivation;
  }

  public String getEnrollmentDateTime() {
    return enrollmentDateTime;
  }

  public Integer getActivityId() {
      return activityId;
  }

  public void setActivityId(Integer activityId) {
      this.activityId = activityId;
  }

  public void setEnrollmentDateTime(String enrollmentDateTime) {
    this.enrollmentDateTime = enrollmentDateTime;
  }

  public boolean getParticipating() {
    return participating;
  }
}
