# TMDB Sample 
Sample app to display latest movies from The Movie Database . APIs can be found here : [TMDB APIs](https://www.themoviedb.org/settings/api)

Rememeber to add your API key in `local.properties` file:  
**API_KEY="api key here"**

**Note 1**: Android Studio Chipmunk may show compilation errors with `androidTest` or `test` package.   
However, the tests can still be run from IDE or commandline:

    ./gradlew test  
    ./gradlew connectedAndroidTest  


**Note 2** : A few androidTest tests are flaky on an emulator (even with animations = off). However, all  
these tests are passing consistently on real devices - Moto G52, Xiami A3, Samsung M31
