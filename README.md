# FoxCommon [![Build Status](https://travis-ci.org/FoxDenStudio/FoxCommon.svg?branch=master)](https://travis-ci.org/FoxDenStudio/FoxCommon)
A Common Library for [FoxGuard](https://github.com/FoxDenStudio/FoxGuard) written for the [SpongeAPI](https://github.com/SpongePowered/SpongeAPI)

## Overview
This is a common library split off from FoxGuard. It contains the entire state buffer interface and will also contain the upcoming selections interface.
Selections are a flexible and powerful way to describe an area and then execute a command on them.
Plugins may want to use the selections in their own plugins. As a result I am putting it here to make it easier for that to happen, as this is a standalone plugin.

## Building from source
### Downloading
If you have git installed:

`git clone https://github.com/FoxDenStudio/FoxCommon.git`

If you don't have git:

Download the latest source as a zip from the GitHub page.

### Building
Open a command line inside the directory. Make sure that the directory has been uncompressed/unzipped if you downloaded it that way.

If you have gradle installed:

`gradle build`

If you don't have gradle installed:

`gradlew build`