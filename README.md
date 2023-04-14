<h1 align="center">
  Chatter Server
  <br>
</h1>

<h4 align="center">Server-side for <a href="https://github.com/TimNekk/Chatter">Chatter</a></h4>

## Overview

Java server based on sockets.
Supports authorization with username.

## Usage

Install server using Maven

```
mvn package
```

And start it on port 9999

```
java -jar target\chatter-server-1.0.jar start -P 9999
```

## Commands

- **start** - Runs the server (`--port` flag required)