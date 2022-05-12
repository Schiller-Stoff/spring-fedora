
# spring-fedora

Testing out spring with fedora 

https://wiki.lyrasis.org/display/FEDORAM6M1P0/Fedora+6.1.0+Documentation


# Quick start

- via local development setup (with disabled security etc.)

## Installation

### Requirements

Needs fedora 6.1 One-Click-Run running on port 8082 
  from here: https://wiki.lyrasis.org/display/FEDORAM6M1P0/Quick+Start

There are additional __optional requirements__ (dependent on used spring profile / either for production or local development )
  By default the __spring profile__ is set to "dev" inside __application.properties__
  (which disables all necessary http dependencies like keycloak, except fedora6.1 on port 8082)

  Additional profiles are defined in application.properties file

### Startup

1. Launch fedora 6.1 One-Click-Run (on port 8082)!
2. Launch spring boot app afterwards


## Staging setup

- includes all features not required for direct production setup (like for providing stable links via handle etc.)


### Requirements

#### 0. Fedora 6.1
- running on port 8082

#### 1. Keycloak

- running on port 8182
- (thymeleaf web client uses the authorization-code-flow to protect server based web clients)

Setup:
  realm: spring-fedora
  client: spring-fedora-client


