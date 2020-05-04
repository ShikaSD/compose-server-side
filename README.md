# ComposeWebUI
## ...or kinda server side rendering using compose

This is a prototype of porting compose principles to other JVM application rather than Android.

The idea was originally done by [SwiftWebUI](https://github.com/SwiftWebUI/SwiftWebUI) project,
rendering HTML page using websocket commands from server.

### How it works?

Server renders a webpage, which creates a websocket connection. When connection is established,
server pushes updates for adding/removing/replacing HTML nodes on the page. In some way, it is similar
to Virtual DOM, but server side.

### Building it
Build requires you to have aosp repository with compose on your local machine.
Add path to the aosp repo to `local.properties` file:
```properties
aosp.location=/location/to/repo/aosp
```

Now, you can build the server using `./gradlew :server:build` command or run it using `./gradlew :server:build`.
