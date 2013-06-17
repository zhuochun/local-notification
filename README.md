# Local Notification Plugin for PhoneGap 2.x.x

Tested on `PhoneGap 2.8.0`

Originally forked from https://github.com/phonegap/phonegap-plugins/tree/master/Android/LocalNotification

You need to have `Android Support Library`, as it provides the best notification support for a wide range of platforms:

- Right click on your project, select `Android Tools`, choose `Add Support Library` (on Eclipse).

# Readme

To use this plugin, you need to perform the following steps:

1. Copy `src/javascript/LocalNotification.js` file to your `www` folder and include it in your `index.html`
2. Create a package `com.bicrement.plugins.localNotification`
3. Copy all `src/java/*.java` files into this package
4. Modify the following in `AlarmReceiver.java`:
  - Change `YourClass.class` on `line 73` to your class where the intent will be called.
  - Reference `R.drawable.ic_launcher` on `line 79` to your own icon.
5. Update your `res/xml/config.xml` file, include the following line within the `plugins` tag:

  ```xml
  <plugin name="LocalNotification" value="com.bicrement.plugins.localNotification.LocalNotification" />
  ```

6. Add the following fragment in your `AndroidManifest.xml`:
  - Add `android:launchMode="singleTop"` attribute to your `activity` tag.
  - Add the following lines just before the `application` tag closes:

  ```xml
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

  ```html
  <script type="text/javascript">
  var notification = cordova.require("cordova/plugin/localNotification")
  
  document.addEventListener("deviceready", appReady, false);
  
  function appReady() {
      console.log("Device ready");
  
      notification.add({
          id: id,
          date: new Date(),
          message: "Phonegap - Local Notification",
          subtitle: "Subtitle is here",
          ticker: "This is a sample ticker text",
          repeatDaily: false
      });
  }
  
  document.addEventListener("deviceready", appReady, false);
  </script>
  ```

8. You can use the following commands:

  ```javascript
  notification.add({ date: new Date(), message: 'an Android alarm', id: 123 });
  notification.cancel(123); 
  notification.cancelAll();
  ```

9. There is a detailed example in the `demo` folder, refer to `www\index.html` file.
