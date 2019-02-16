Meta:

Narrative:
As a user
I want to perform an action
So that I can achieve a business goal

Scenario: passed EPAM global search via google
Meta:
@JIRATestKey EPMFARMATS-8449
Given I am om google main page
When I enter 'EPAM global'
And I press ENTER on keyboard
Then first search result should contain 'EPAM'

Scenario: failed EPAM global search via google
Meta:
@JIRATestKey EPMFARMATS-4168
@Smoke
Given I am om google main page
When I enter 'EPAM global'
And I press ENTER on keyboard
Then first search result should contain 'EPAMfdsgdg'

Scenario: passed EPAM global search via google
Meta:
@JIRATestKey EPMFARMATS-2189
@Regression
Given I am om google main page
When I enter 'EPAM global'
And I press ENTER on keyboard
Then first search result should contain 'EPAM'

Scenario: passed EPAM global search via google
Meta:
@JIRATestKey EPMFARMATS-2195
@Regression
Given I am om google main page
When I enter 'EPAM global'
And I press ENTER on keyboard
Then first link in footer should contain 'epam'
Then first search result should contain 'EPAM'