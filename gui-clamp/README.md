# Summary

Copyright (C) 2021 Nordix Foundation.
This file is licensed under the CREATIVE COMMONS ATTRIBUTION 4.0 INTERNATIONAL LICENSE
Full license text at https://creativecommons.org/licenses/by/4.0/legalcode

This source repository contains the ONAP Policy GUI code.


The gui-clamp repo can be built with maven commands but it can also be build,run,test with npm commands.

Prerequsite:
The following tools should be installed to run this project,

```
Package      Version
node         14.18.1
npm           8.1.2
```

Build & Run:<br>
To install the npm packages,
```
npm install --legacy-peer-deps
```
To run test,
```
npm run test
[OR]
npm test -- -u
```
```
npm run build
```
```
npm start
```