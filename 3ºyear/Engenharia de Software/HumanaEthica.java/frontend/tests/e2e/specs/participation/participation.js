describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createParticipationDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create participation', () => {
    const RATING = '3';

    //TODO: check if makes sense here
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitution');
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('POST', '/participations/activities/*').as(
      'createParticipation',
    );
    cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');

    cy.demoMemberLogin();

    // Go to institution activities view
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();

    cy.wait('@getInstitution');

    // check if there are two activities
    cy.get('[data-cy="memberActivitiesTable"] tbody tr').should(
      'have.length',
      2,
    );
    // check if first activity on table has one participation
    //   cy.get(
    //  '[data-cy="memberActivitiesTable"] tbody tr:first td:nth-child(4)',
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4 - 1)
      .should('have.text', '1');

    // select Show Enrollments on the table's first activity
    // cy.get('[data-cy="memberActivitiesTable"] tbody tr:first')
    cy.get('[data-cy="showEnrollments"]').eq(0).click();

    cy.wait('@getEnrollments');

    //Verificar que a tabela dos enrollments da atividade tem 2 instâncias
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr').should(
      'have.length',
      2,
    );
    // Verificar que o primeiro enrollment da tabela tem a coluna Participating como false

    // check if first enrollment has Participation set to false
    //  cy.get(
    //   '[data-cy="memberActivitiesTable"] tbody tr:first td:nth-child(3)',
    // )
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(3 - 1)
      .should('have.text', 'false');

    //Click button
    cy.get('[data-cy="selectParticipant"]').click();

    cy.get('[data-cy="ratingInput"]').type(RATING);

    cy.get('[data-cy="makeParticipant"]').click();

    cy.wait('@createParticipation');

    // check if first enrollment has Participation set to true
    cy.get(
      '[data-cy="activityEnrollmentsTable"] tbody tr:first td:nth-child(3)',
    ).should('have.text', 'true');

    //Go back to activities list
    cy.get('[data-cy="getActivities"]').click();

    cy.wait('@getInstitution');

    // check if first activity now has two participations
    cy.get(
      '[data-cy="memberActivitiesTable"] tbody tr:first td:nth-child(4)',
    ).should('have.text', '2');
  });
});
