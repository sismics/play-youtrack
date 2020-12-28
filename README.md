[![GitHub release](https://img.shields.io/github/release/sismics/play-youtrack.svg?style=flat-square)](https://github.com/sismics/play-youtrack/releases/latest)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# play-youtrack plugin

This plugin adds [YouTrack](https://www.jetbrains.com/youtrack/) support to Play! Framework 1 applications.

# Features

# How to use

####  Add the dependency to your `dependencies.yml` file

```
require:
    - youtrack -> youtrack 1.2.0

repositories:
    - sismicsNexusRaw:
        type: http
        artifact: "https://nexus.sismics.com/repository/sismics/[module]-[revision].zip"
        contains:
            - youtrack -> *

```
####  Set configuration parameters

Add the following parameters to **application.conf**:

```
# Youtrack configuration
# ~~~~~~~~~~~~~~~~~~~~
youTrack.mock=false
youTrack.url=https://youtrack.example.com
youTrack.token=perm:12345678
```
####  Use the API

```
YouTrackClient.get().getUserService().createUser("test", "test@example.com", "Full Name", "12345678")
```

####  Mock the Youtrack server in dev

We recommand to mock YouTrack in development mode and test profile.

Use the following configuration parameter:

```
youtrack.mock=true
```

# License

This software is released under the terms of the Apache License, Version 2.0. See `LICENSE` for more
information or see <https://opensource.org/licenses/Apache-2.0>.
