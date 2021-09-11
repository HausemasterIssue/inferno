# inferno

A re-done version of my very very very old anarchy Minecraft client using Forge 1.12.2

This isn't fully done, so if you want to use it you can either find a JAR in the releases, but I barely add new things there. So, you should [build](#building) it yourself.

This is also meant for Anarchy Environments, I do not encourage you to use this on non anarchy servers.

---

## building

Download the code and run these commands in a terminal opened in the directory where you downloaded the code.

You need a Java JDK to build the client. Version 8u101 is what I use, but you can really use any version of Java 8.

```
./gradlew setupDecompWorkspace
./gradlew clean
./gradlew build
```

Look in the builds/libs folder, and use the one with the -release tag at the end of it before the .jar file extension.

---

## credits

These are clients/cool people who have helped with the development of the client

#### Clients:
- Phobos 1.9.0 - Original access transformers + CrystalChams + Custom Font renderer
- Cosmos 1.0.0 - 2b2t Criticals bypass

#### People
- [Sxmurai](https://github.com/Sxmurai) - Main Developer
- [HausemasterIssue](https://github.com/HausemasterIssue) - Contributor

---

<h5>Sxmurai - 2021</h5>