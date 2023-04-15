<div align="center">
  <img align="center" src="https://socialify.git.ci/TimNekk/ChatterServer/image?description=1&font=Inter&language=1&name=1&pattern=Plus&theme=Light" alt="Chatter" width="640" height="320" />
</div>

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
