![infernologo](https://user-images.githubusercontent.com/90464553/133526203-64b11a67-e7c6-4f31-9fd1-05d9142227aa.png)
# Inferno

A re-done version of my very very very old anarchy Minecraft client using Forge 1.12.2

This isn't fully done, so if you want to use it you can either find a JAR in the releases, but I barely add new things there. So, you should [build](#building) it yourself.

This is also meant for Anarchy Environments, I do not encourage you to use this on non anarchy servers.

---

## Building

You need Java 8 installed to build the client. I personally use Java 8u101, but any version of Java 8 should work just fine with building the client.

Download the code and run these commands in a terminal opened in the directory where you downloaded the code:

For Windows:
```
gradlew setupDecompWorkspace
gradlew clean
gradlew build
```
For Mac:
```
./gradlew setupDecompWorkspace
./gradlew clean
./gradlew build
```

Look in the builds/libs folder, and use the one with the -release tag at the end of it before the .jar file extension.

---

## Credits

These are clients/people who have helped with the development of the client

#### Clients:
- Phobos 1.9.0 - Original access transformers + CrystalChams + Custom Font renderer
- Cosmos 1.0.0 - 2b2t Criticals bypass
- CookieClient 1.0.1 - AntiAFK ideas + TPS Sync timer idea aswell

#### People
- [Sxmurai](https://github.com/Sxmurai) - Main Developer
- [HausemasterIssue](https://github.com/HausemasterIssue) - Contributor

---

<h5>Sxmurai - 2021</h5>
