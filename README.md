# Meet4j (Spring)

## Description

We will create a RESTful (best effort = no HATEOAS) API for helping to schedule meetings.
No UI needs to be created.
On the one hand, the application maintains a set of people (id, first name, last name, email, password).
On the other hand, it maintains a set of meetings (starttime (date + time), id and optionally a description).
A person can be invited to a meeting, but must also confirm their attendance.

Given a set of attendees of the meeting, there should be a way to ask the system for a recommended available time slot starting from the next day.
Assume that a meeting is always 1h long.
The system should return the first available slot (between 9 and 15 o'clock on workdays).
If no slot can be found in this or the next work week, no suggestion is returned.
(Bonus: make the meeting duration dynamic.
Use `java.time.Duration` and add the necessary AttributeConverter (JPA) and Json converter (jackson or json-b)).

It should also be possible to effectively create the meeting invitation.
(Bonus: send meeting invitations per email: use the REST client to send emails through your previously developed mailer (micro-)service.
You can even use the greeting endpoint to generate the greeting of the email!)

The system must also be capable of returning a list of upcoming meetings for any specific person (ordered chronologically).

Finally, the system must be capable of showing the details of an upcoming meeting: when it is, who is invited and who has accepted.

By working contract-first, we start by defining what operations must be available:
* Creating a user
* Listing all users
* Listing all meetings for a person
* Getting a proposition for a time slot for a meeting, given at least two users
* Creating a meeting invite for a given time slot and at least two users
* Viewing the details of a meeting (including who has accepted and who hasn't)
* Accepting/rejecting a meeting invitation (only if addressed to yourself, of course)

Next, we will define (integration) tests which validate the correct behavior of these operations.
At first, they will all fail, but we will gradually implement more and more until finally all tests should pass.

You should use a database to persist all data.
(Extra: add audit fields "created at" and "last modified at" to your entities using a mapped superclass and entity listeners.)

Add customized exception handling for at least:
* Invalid requests (data does not satisfy constraints) -> 400
* Illegal requests (trying to get data for a non-existing resource) -> 404

## Database (Postgres)

### Docker

```shell
docker run --detach --name pg --rm -p 5434:5432 \
    -e POSTGRES_PASSWORD=acaddemicts -e POSTGRES_USER=acaddemicts \
    -e POSTGRES_DB=meet4j postgres:alpine
```

For testing, you could run a second instance by repeating the same command with a different port binding.

## Properties

You need to pass the following properties:

* SPRING_DATASOURCE_URL
* SPRING_DATASOURCE_USERNAME
* SPRING_DATASOURCE_PASSWORD
* MEET4J_EMAIL_URL
* MEET4J_EMAIL_USERNAME
* MEET4J_EMAIL_PASSWORD