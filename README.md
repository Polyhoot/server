[![License: Apache-2.0](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![Java CI with Gradle](https://github.com/Polyhoot/server/actions/workflows/gradle.yml/badge.svg)](https://github.com/Polyhoot/server/actions/workflows/gradle.yml) [![Test with Gradle](https://github.com/Polyhoot/server/actions/workflows/gradle_test.yml/badge.svg)](https://github.com/Polyhoot/server/actions/workflows/gradle_test.yml)

# Polyhoot! Server backend

<img width="100" height="100" alt="Polyhoot! Logo" src="https://github.com/Polyhoot/.github/blob/main/logo.jpeg?raw=true" align="right">

### A Ktor-based server that handles game process, user registration and packs provision

## Deploying server locally

You can deploy Polyhoot! server to your machine with purpose of hosting games yourself.
You'll also have to build Android client with your own WebSocket IP in order to play.
These instructions will get you a copy of the project up and running on your local machine for any purpose possible.

### Prerequisities

* Java Development Kit 17

### Cloning the project

```shell
git clone https://github.com/polyhoot/server -b master
```

### Setting up the environment

At this point you have to export `MONGOURI` and `JWT_SECRET` environmental variables.

You should create a MongoDB and copy the connection URI and export it to `MONGOURI`

`JWT_SECRET` should be a random string, it will be used as key for JWT Auth

```shell
export MONGOURI="mongodb..."
export JWT_SECRET=<random string>
```

### Building server

To build server you should run `shadowJar` Gradle task. You can do that with:
```shell
./gradlew shadowJar
```
A runnable JAR file will be generated and placed to `build/libs/polyhoot_server.jar`

### Running the server

To run server use standard `java -jar` command

```shell
java -jar build/libs/polyhoot_server.jar <options>
```

### Server running options

There are only three options so far:
```
--port, -p [8080] -> Port server should listen to { Int }
--debug, -d [false] -> Debug mode (MongoDB logging mostly)
--help, -h -> Usage info 
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.