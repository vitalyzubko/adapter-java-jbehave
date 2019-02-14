Meta:

Narrative:
As a user
I want to perform an action
So that I can achieve a business goal

Scenario: passed EPAM global search via google
Given I am om google main page
When I enter 'EPAM global'
And I press ENTER on keyboard
Then first search result should contain 'EPAM'