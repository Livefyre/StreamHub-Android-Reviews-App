StreamHub Android Reviews Application
=============================

Make Android apps powered by Livefyre StreamHub

Read the docs: [http://livefyre.github.com/StreamHub-Android-SDK/](http://livefyre.github.com/StreamHub-Android-SDK/)

--------
##### Dependencies
This Livefyre StreamHub Reviews example uses the following dependencies

* [StreamHub-Android-SDK](https://github.com/Livefyre/StreamHub-Android-SDK/)

* [Filepicker](https://github.com/Ink/filepicker-android)

* [Asynchronous Http Client](https://github.com/loopj/android-async-http)

* [Picasso](https://github.com/square/picasso)

----
Installation
--------------

```

####Android Reviews App Build Steps

1. git clone https://github.com/Livefyre/StreamHub-Android-Reviews-App.git

2. Open Android Studio and import project and open the directory that was just cloned in step 1

3. (Optional you can change the config params for the endpoints under StreamHub-Android-Reviews-App/streamHubAndroidSDK/src/main/java/livefyre/streamhub/Config.java )

4. Click on the Build Gradle button 

5. click the Run button and the app will open in the simulator or device if it is connected.



#####Please note that if you want to customize the SDK you might need to

git clone https://github.com/Livefyre/StreamHub-Android-SDK.git
In your example app go to "File" => "Import Module". Browse and select SDK location.
In build.gradle file add following,

dependencies {
compile files('libs/android-async-http.jar')
compile files('libs/picasso-2.3.4.jar')
compile project(':filePickerSDK')
compile project(':streamHubAndroidSDK')
}

After adding dependencies build gradle and run the app.

```

Add Filepicker, Picasso libraries to app/libs directory.

Filepicker SDK is used to upload image/files to the host.

Picasso is an powerful image downloading and caching library for Android

Android-async-http is an asynchronous, callback-based Http client for Android built on top of Apache's HttpClient libraries.

-----

###Example StreamHub Android Reviews Application includes :

With Livefyre’s new LiveReviews application, users can easily rate and review products, services, articles or any other content in real time. They can recommend and comment on each other’s reviews, embed photos or other rich media, and share reviews to their social networks.

### Deving

Clone the project, run the tests, and notice a few undocumented classes. Kindly treat the project as alpha code.

## License

Copyright 2013 Livefyre Inc.

Licensed under the MIT License