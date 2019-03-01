Meta:

Narrative:
As a user
I want to perform an action
So that I can achieve a business goal

Scenario: Passed epam.com open page
Meta:
@JIRATestKey EPMFARMATS-7894
@Smoke
Given I am on page with url 'https://www.epam.com'
Then see 'EPAM' in title