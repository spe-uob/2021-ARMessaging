# 2021-ARMessaging - SkyWrite #
## Summary ##
SkyWrite is an Android app that will allow a sender to select an appropriate message (eg Happy Birthday,
Merry Christmas etc) to a postcode. The message will appear in augmented reality 100ft above the ground.
Those with the app/service will be notified that a message exists in a nearby postcode.

You can read more about the development in our [blog](https://sky-write.github.io/)

## User Guide ##
### How to run ###
1. Make sure you have a [compatible](https://developers.google.com/ar/devices) mobile device
1. Download the apk file
2. Navigate to Settings and make sure to give permissions to install unknown apps from unknown sources
3. Locate the apk file in your files and tap on it to install it
4. Open the app and accept all permissions - if you do not have Google Play Services for AR installed already, SkyWrite will ask you to install it

## Developer Guide ##

### Prerequisites ###
- Java 11
- Gradle
- Android Studio

### Set up ###
1. Install the prerequisites
2. Clone the repository: `git clone git@github.com:spe-uob/2021-ARMessaging.git`

## Getting Started ##
This project uses the Gradle build system. To build the project use `gradlew build` in the terminal or import the project in Android Studio.

To run tests, run `gradlew test` or navigate to the tests on Android Studio and run from there.

It is recommended to run the app using the [Android Emulator](https://developer.android.com/studio/run/emulator) but it is also possible to run the app on a [hardware device](https://developer.android.com/studio/run/device).

### Deployment ###
Since the front-end is an Android App, deployment is done via APK release. The CircleCI script includes a `release-build` which updates with every push to the Master branch and produces an APK. If APK creation via IDE is preferred, Android Studio documentation for release builds can be found here: https://developer.android.com/studio/run
Our website below contains a table of previous APK releases made during development.

Server-side deployment is recommended through IBMCloud and a Kubernetes toolchain, which can be set up using the Dockerfile and shell script provided.
Alternatively, a local environment can be set up for personal development; the SpringBoot server running on localhost with a connection to a local PostgreSQL server will suffice.

## Blog ##
Our [blog](https://sky-write.github.io/) contains all the documentation as well as updates on the development process.
