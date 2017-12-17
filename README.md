# FoxCore [![Build Status](https://travis-ci.org/FoxDenStudio/FoxCore-Sponge.svg?branch=master)](https://travis-ci.org/FoxDenStudio/FoxCore-Sponge)
A Core Library for [FoxGuard](https://github.com/FoxDenStudio/FoxGuard-Sponge) written for the [SpongeAPI](https://github.com/SpongePowered/SpongeAPI)

## Overview
This is a core library split off from FoxGuard. It contains the entire state buffer interface and will also contain the upcoming selections interface.
Selections are a flexible and powerful way to describe an area and then execute a command on them.
Plugins may want to use the selections in their own plugins. As a result I am putting it here to make it easier for that to happen, as this is a standalone plugin.

## Downloads
Jars can be found under Github releases. Use the shadowed -all.jar only if you are installing the client forge mod without SpongeForge installed on your client as well.
Use the regular (no suffix) jar for everything else.

## Building from source
### Downloading
If you have git installed:

`git clone https://github.com/FoxDenStudio/FoxCore.git`

If you don't have git:

Download the latest source as a zip from the GitHub page.

### Building
Open a command line inside the directory. Make sure that the directory has been uncompressed/unzipped if you downloaded it that way.

If you have gradle installed:

`gradle build`

If you don't have gradle installed:

`gradlew build`
