describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createAssessmentDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create assessment', () => {
    const ASSESSMENT = 'ASSESSMENT-ASSESSMENT';

    cy.intercept('GET', '/users/*/getInstitution').as('getInstitution');
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('POST', '/assessments').as('createAssessment');

    cy.demoVolunteerLogin();

    // Go to volunteer activities view
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@getActivities');

    // Check if there are 6 activities
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr').should(
      'have.length',
      6,
    );

    // check first activity has name 'A1'
    cy.get(
      '[data-cy="volunteerActivitiesTable"] tbody tr:first td:first',
    ).should('have.text', 'A1');

    // assess first activity
    cy.get('[data-cy="writeAssessmentButton"]').first().click();

    // write assessment
    cy.get('[data-cy="newAssessmentInput"]').type(ASSESSMENT);
    // submit assessment
    cy.get('[data-cy="writeAssessment"]').click();

    cy.logout();

    // login as member
    cy.demoMemberLogin();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="assessments"]').click();
    cy.wait('@getInstitution');

    // check if there is one assessment
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr').should(
      'have.length',
      1,
    );

    // check if the assessment is the one we created
    cy.get(
      '[data-cy="institutionAssessmentsTable"] tbody tr:first td:first',
    ).should('have.text', ASSESSMENT);

    cy.logout();


  });
});