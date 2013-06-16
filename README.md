# Local Notification Plugin for PhoneGap 2.x.x

Tested on `PhoneGap 2.8.0`

Originally cloned from https://github.com/phonegap/phonegap-plugins/tree/master/Android/LocalNotification

# Readme

To use this plugin, you need to perform the following steps:

1. Copy `src/LocalNotification.js` file to your `www` folder and include it in your `index.html`
2. Create a package `com.bicrement.plugins.localNotification`
3. Copy all `src/*.java` files into this package
4. Fix the following in `AlarmReceiver.java`:
  - Reference `R.drawable.ic_launcher` on `line 78` to your own icon.
  - Change `YourClass.class` on `line 82` to your class where the intent will be called.
5. Update your `res/xml/config.xml` file, include the following line within the `plugins` tag:
  ```
  <plugin name="LocalNotification" value="com.bicrement.plugins.localNotification.LocalNotification" />
  ```
6. Add the following fragment in your `AndroidManifest.xml` inside the `application` tag:
  ```
  <receiver android:name="com.bicrement.plugins.localNotification.AlarmReceiver" >
  </receiver>
  
  <receiver android:name="com.bicrement.plugins.localNotification.AlarmRestoreOnBoot" >
      <intent-filter>
          <action android:name="android.intent.action.BOOT_COMPLETED" />
      </intent-filter>
  </receiver>
  ```
  The first part tells Android to launch the AlarmReceiver class when the alarm is be triggered. This will also work when the application is not running.
  The second part restores all added alarms upon device reboot (because Android 'forgets' all alarms after a restart).
7. The following piece of code is a minimal example in which you can test the notification:
  ```
  <script type="text/javascript">
  var notification = cordova.require("cordova/plugin/localNotification")
  
  document.addEventListener("deviceready", appReady, false);
  
  function appReady() {
      console.log("Device ready");
  
      notification.add({
          date : new Date(),
          message : "Phonegap - Local Notification\r\nSubtitle comes after linebreak",
          ticker : "This is a sample ticker text",
          repeatDaily : false,
          id : 4
      });
  }
  
  document.addEventListener("deviceready", appReady, false);
  </script>
  ```
8. You can use the following commands:
  ```
  notification.add({ date: new Date(), message: 'an Android alarm', id: 123 });
  notification.cancel(123); 
  notification.cancelAll();
  ```
