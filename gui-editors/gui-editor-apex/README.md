# Apex Editor

## Compiling and running
As Apex Editor is a Spring Boot application, it may be compiled and run using:
```
mvn spring-boot:run
```
Alternatively, you may build and run a jar:
```
mvn clean install
java -jar target/gui-editor-apex-2.2.1-SNAPSHOT-exec.jar
```
Once started, navigate to http://localhost:18989/.

There are some sample models in _src/test/resources/models_

### Build artifacts
Note maven produces two jars:
- a regular jar that we can include in other modules: _gui-editor-apex-VERSION.jar_
- an executable spring boot jar with the suffix 'exec': _gui-editor-apex-VERSION-exec.jar_

## Setting upload URL and user ID
There are two Spring properties for Apex Editor:
- `apex-editor.upload-url` sets the URL for the model upload feature.
- `apex-editor.upload-userid` sets the default userId for uploads.

These may be set in a Spring properties file:
```
server.port=18989
apex-editor.upload-url=http://localhost:12345
apex-editor.upload-userid=DefaultUser
```

### Overriding upload user ID at runtime
To override the upload userId for your session, append `?userId=YourId` to the URL,
e.g. http://localhost:18989/?userId=MyUser
