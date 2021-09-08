# Summary

Copyright 2017-2018 AT&T Intellectual Property. All rights reserved.
Copyright (C) 2021 Nordix Foundation.
This file is licensed under the CREATIVE COMMONS ATTRIBUTION 4.0 INTERNATIONAL LICENSE
Full license text at https://creativecommons.org/licenses/by/4.0/legalcode

This source repository contains the ONAP Policy GUI code.

To build it using Maven 3, run: mvn clean install -P docker


# Docker image

Maven produces a single docker image containing the policy GUIs.
These are exposed on the same port (8080) using different URLs:
- Apex Policy Editor: http://localhost:8080/apex-editor
- PDP Monitoring UI: http://localhost:8080/pdp-monitoring
- CLAMP Designer UI: http://localhost:8080/clamp

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
docker run -p 8080:8080 --add-host policy-clamp-backend:host-gateway onap/policy-gui
```
