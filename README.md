# Scalar 2024 - Generative Art in Scala, No AI Required

This repository contains the code written during the "Generative Art in Scala, No AI Required" talk at Scalar 2024. Besides the code written live during the talk, the repo contains some auxiliary code that could not have been written live.

![Generative art](./pics/screenshot%201.png)

This is a Scala.js application that runs in a browser.

## How to Run

Prerequisites:

- IntelliJ or Metals
- SBT
- Node

Open the code in your favorite IDE.

In one terminal, compile the code in a watch-incremental fashion by running

```
sbt
project app
~fastOptJS
```

In another terminal, start a Parcel web server to show you the final JS by running

```kotlin
cd app
npm install
npm run start
```

You only need to run `npm install` just once, and you can simply run `npm run start` in the subsequent runs.

After you started the two terminals, go to http://localhost:1234 to see your code running live.

As you change and save your Scala files, the first terminal will catch the changes, recompile your code (to JS), which the second terminal (Parcel) will pick up and re-serve your web app.
