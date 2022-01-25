# 2021-ARMessaging #
## Summary ##
ARMessaging will allow a sender to select an appropriate message (eg Happy Birthday,
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
Currently there is no server code so no deployment is required.

## Blog ##
Our [blog](https://sky-write.github.io/) contains documentation as well as updates on the development process.
