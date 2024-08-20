# A basic Roles-to-Resources Mapper Overview

<!-- TOC -->
* [Overview](#overview)
  * [Sample Schema](#sample-schema)
  * [Sample ERD](#sample-erd)
  * [Sample API Return](#sample-api-return)
  * [API](#api)
  * [Database / Sources](#database--sources)
<!-- TOC -->

**NOTE**
There's a lot of stuff in here that is done for demonstration purposes that would need
some further robustness given a production environment (validations, use of liquibase/migrations, turning off Hibernate
DDL, API endpoint security, DTOs, test coverage, etc)...

---

This service shows how to map many-roles to many-nested-resources.

This service is intended to be fed a "role" name from an IdP (e.g. Keycloak) and then fetch what that "role"
can do within the given schema (amongst the schema's forms and program levels).

Permissions are synonymous with "Security Functions"... they can be created per program unit.

## Sample Schema

Some overall rules enforced:

* All form names, role names, program names, and security functions are uppercased automatically prior to persistence
  for ease of demo
* All form names, role names, program names, and security function comparisons are case-insensitive for easy equals()
  implementation
* Programs can have zero-to-many forms
* Programs can have zero-to-many security functions
* Forms under programs can have zero-to-many of that owning program's security functions
* Roles can be mapped to zero-to-many programs with a set of that program's security functions
* Forms nested under Programs only go one (1) level deep (can't have programs with programs or forms within forms, etc)
* Security functions are unique to each program (i.e. they are not shared... if READ needs to be in each program then
  that will need to be created as such)
    * This allows for auditing of security function lifespans, etc

## Sample ERD

![testmapp - public.png](testmapp%20-%20public.png)

## Sample API Return

A sample output from the `/roleDetails` endpoint for all persisted roles... shows what they can do within each persisted program/form.

```agsl
  {
    "roleName": "ROLE1",
    "programs": [
      {
        "id": 1,
        "name": "PRG123",
        "forms": [
          {
            "id": 352,
            "name": "FORM1",
            "roleMappings": {
              "ROLE1": [
                "UPDATE"
              ]
            }
          }
        ],
        "roleMappings": {
          "ROLE1": [
            "READ",
            "UPDATE"
          ]
        }
      }
    ]
  },
  {
    "roleName": "ADMIN",
    "programs": [
      {
        "name": "PRG123",
        "forms": [
          {
            "name": "FORM1",
            "roleMappings": {
              "ADMIN": [
                "UPDATE"
              ]
            }
          }
        ],
        "roleMappings": {
          "ADMIN": [
            "UPDATE"
          ]
        }
      }
    ]
  }
]
```

## API

The REST controller offers easy management of said schema.  The swagger doc is available at `/swagger-ui.html`

## Database / Sources

- Testing is done in an embedded H2 database
- Running the program will look for a local Pg database named 'testmapp' with (postgres/postgres) as the username/password
- These can be edited by the DataSource beans in the app's main class file.
