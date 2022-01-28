# Summary

Copyright 2017-2018 AT&T Intellectual Property. All rights reserved.
Copyright (C) 2021 Nordix Foundation.
This file is licensed under the CREATIVE COMMONS ATTRIBUTION 4.0 INTERNATIONAL LICENSE
Full license text at https://creativecommons.org/licenses/by/4.0/legalcode

This source repository contains the ONAP Policy GUI code.

To build it using Maven 3, run: mvn clean install -P docker


# Docker image

Maven produces a single docker image containing the policy GUIs.
These are exposed on the same port (2443) using different URLs:
- Apex Policy Editor: https://localhost:2443/apex-editor
- CLAMP Designer UI: https://localhost:2443/clamp

## Building
You can use the following command to build the policy-gui docker image:
```
mvn clean install -P docker
```

## Deployment
Currently, the policy-gui docker image can be deployed without configuration.
For the GUI container to start correctly, the CLAMP backend
`policy-clamp-backend` should be started first.

For local testing, if the CLAMP backend is running on localhost port 8443,
the policy-gui docker container can be started with:
```
docker run -p 2443:2443 --add-host policy-clamp-backend:host-gateway onap/policy-gui
```

## Client Credentials
A certificate must be added in the browser and is required to log in properly:

[org.onap.clamp.p12 (from clamp master)](URL "https://gerrit.onap.org/r/gitweb?p=clamp.git;a=blob_plain;f=src/main/resources/clds/aaf/org.onap.clamp.p12;hb=refs/heads/master")
(Password: "China in the Spring")

See onap/clamp repo README for details.
