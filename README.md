![infernologo](https://user-images.githubusercontent.com/90464553/133526203-64b11a67-e7c6-4f31-9fd1-05d9142227aa.png)
# Inferno

A re-done version of my very very very old anarchy Minecraft client using Forge 1.12.2

This is nowhere near completion, and I have much to learn and do with this client. It'll eventually get there, but it's 100% a WIP client.

If for some reason you'd like to run the client, you can download a pre compiled release, or build it yourself for the latest build.

---

## Building

You need any version over Java 8u101, because forgegradle doesn't want you using anything less, although you could try. You can then download the code and run the following three commands in a command prompt:

If you are on MacOS or Linux, run the commands below prefixed by `./` so that you can run these gradle tasks.

```
gradlew setupDecompWorkspace
gradlew clean
gradlew build
```

Your jar will be under `build/libs/inferno-1.0.0-release.jar`

---

## Credits

These are clients/people who have helped with the development of the client

I'm going to be honest, this is one of the first serious clients I'm making, and I don't know how to do alot of stuff. I'm definitely learning, but some modules could be paritally or even fully skidded from clients. I do plan to remove them with my own code soon, but just know that some things are not made by me and I try to keep the credits updated as much as I remember.

If you feel as I took your code and completely ripped it without crediting you properly, please open an issue and tell me what code was stolen and what client or how to credit you.

#### Clients:
- Phobos 1.9.0 - CrystalChams + Custom Font renderer, probably alot more shit. Need to remove alot of that though
- GameSense - NCP Step bypass packets
- Cosmos 1.0.0 - 2b2t Criticals bypass, ReverseStep shit

#### People
- [Sxmurai](https://github.com/Sxmurai) - Main Developer
- [HausemasterIssue](https://github.com/HausemasterIssue) - Contributor
- [linustouchtips](https://github.com/linustouchtips/) - Telling me to get this client's shit together (which im almost done doing)

---

<h5>Sxmurai - 2021</h5>
