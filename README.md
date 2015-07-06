StreamHub Android Reviews Application
=============================
With Livefyre’s Demonstrated LiveReviews application, users can easily rate and review products, services, articles or any other content in real time. They can recommend and comment on each other’s reviews, embed photos or other rich media, and share reviews to their social networks.

Make Android apps powered by Livefyre StreamHub

Read the docs: [http://livefyre.github.com/StreamHub-Android-SDK/](http://livefyre.github.com/StreamHub-Android-SDK/)

---
Installation
=============================
Android Reviews App Build Steps:

1. Clone this repo to get the project
2. Open Android Studio,file > import the project, and open the reviews app (cloned in step 1)
 * Optionally you can change the config params for the endpoints under StreamHub-Android-Reviews-App/streamHubAndroidSDK/src/main/java/livefyre/streamhub/Config.java
2. Ensure that you have the <a href="https://github.com/Livefyre/StreamHub-Android-SDK.git">SDK</a> loaded into your environment
 * You might have to explictily "File" => "Import Module" and point to the StreamHub SDK location.
3. Click on the Build Gradle button or just "run" if you are using Android Studio as it will manage gradle explicitly with UI elements.
 * Clicking the Run button will open the app in the simulator or device if it is connected.

Customizing the SDK
=============================
In some cases, the following might be required when customizing the SDK:

1. git clone https://github.com/Livefyre/StreamHub-Android-SDK.git
2. In your example app go to "File" => "Import Module". Browse to the SDK location cloned in the step above.
3. In **build.gradle** file add following before running the app:

```
dependencies {
  compile files('libs/android-async-http.jar')
  compile files('libs/picasso-2.3.4.jar')
  compile project(':filePickerSDK')
  compile project(':streamHubAndroidSDK')
}
```

---
Dependencies
=============================
This Livefyre StreamHub Reviews example App uses the following dependencies:
* [StreamHub-Android-SDK](https://github.com/Livefyre/StreamHub-Android-SDK/)

* [Filepicker](https://github.com/Ink/filepicker-android)
 * Filepicker SDK is used to upload image/files to the host.
* [Asynchronous Http Client](https://github.com/loopj/android-async-http)
 * Android-async-http is an asynchronous, callback-based Http client for Android built on top of Apache's HttpClient libraries.
* [Picasso](https://github.com/square/picasso)
 * Picasso is an powerful image downloading and caching library for Android
* In build.gradle the preferred version for targetSdkVersion is 20, but 21  will also work if using SDK Manager,  Android 5.0 (API 21) is  downloaded and installed

### Developers
Clone the project, run the tests, and notice a few undocumented classes. Kindly treat the project as alpha code.

## License
Copyright 2013-2015 Livefyre Inc.

Licensed under the MIT License