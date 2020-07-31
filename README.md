# Compose on the server side

Now [live on Heroku](https://compose-test-app.herokuapp.com/)!

This is a prototype of porting compose as a feature of a Ktor server rather than running it on Android.

The original idea belongs to [SwiftWebUI](https://github.com/SwiftWebUI/SwiftWebUI) project,
rendering HTML page using websocket commands from server.

Works with dev-15 with minor changes.

### Show me the code!
```kotlin
fun Application.module() {
    install(Compose)

    routing {
        compose {
            var state by remember { mutableStateOf(1) }
            h1 { 
                text("Counter value is $state") 
            }
            button(Modifier.onClick { state++ }) { 
                text("Increment!")
            }
        }
    }
}
```

### How does it work?
TODO: link to article

### Building it
You can use `deploy` branch with prebuilt artifacts of runtime.

`integration` module, contains full stack implementation of actual app. Ideally, this is the one you could write if this library will be ever published.

The Ktor feature with all the definitions is in the `server` module and browser runtime is in the `client` one.
