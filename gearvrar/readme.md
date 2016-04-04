### Gear VR mixed reality demo with animated 3d models. 

#### Android studio project.

* Grab dependencies from:
	* gearvrf :  <https://github.com/Samsung/GearVRf>
	* oculus mobile sdk : <https://developer.oculus.com/downloads/mobile/1.0.0.1/Oculus_Mobile_SDK/>
	* vuforia mobile sdk:  <https://developer.vuforia.com/downloads/sdk>  (vuforia-sdk-android-5-5-9) 

	app/libs

		android-support-v4.jar
		gson-2.2.4.jar
		gvrf_exported.jar
		jline-android.jar
		jnlua-android.jar
		js.jar
		jsr223.jar
		SystemUtils.jar
		VrApi.jar
		VrAppFramework.jar
		Vuforia.jar


	app/src/main/jniLibs/armeabi-v7a 

		libassimp.so	
		libgvrf.so
		libjnlua.so
		libvrapi.so
		libVuforia.so

##### Test images (markers):
    images/
        dolphinmarker2.jpg
        trexmarker2.jpg

* In order to deploy in your device, put your oculus osig file in the assets folder.




