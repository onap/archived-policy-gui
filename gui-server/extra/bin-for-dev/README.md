# gui-server helper scripts
This directory contains helper scripts for running gui-server.

- start-gui-server-jar.sh starts the JAR.

- start-gui-server-docker.sh starts the docker image.
To ensure the latest development snapshot is run, first build the run `mvn clean install -P docker` from the gui repo.

If you wish to test client cert authentication, you may import the certificate demo-clamp.keystore.p12 into your browser
(password is 'changeit').
