# Local Notification Plugin for PhoneGap 2.x.x

Tested on `PhoneGap 2.6.0, 2.7.0, 2.8.1`

Originally forked from https://github.com/phonegap/phonegap-plugins/tree/master/Android/LocalNotification

You have to have `Android Support Library`, as it provides the best notification support for a wide range of platforms:

- Right click on your project, select `Android Tools`, choose `Add Support Library` (on Eclipse).

# Readme

To use this plugin, you need to perform the following steps:

1. Copy `src/javascript/LocalNotification.js` file to your `www` folder and include it in your `index.html`
2. Create a package `com.bicrement.plugins.localNotification` in your `src` folder
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
          id: 1,
          date: new Date(),
          message: "Phonegap - Local Notification",
          subtitle: "Subtitle is here",
          ticker: "This is a sample ticker text",
          repeatDaily: false
      });
  }
  </script>
  ```

8. You can use the following commands:

  ```javascript
  notification.add({ date: new Date(), message: 'an Android alarm', id: 123 });
  notification.cancel(123); 
  notification.cancelAll();
  ```

9. **There is a detail example in the `demo` folder**, refer to `www\index.html` file.

# Licence

The MIT License

Copyright (c) 2013 Wang Zhuochun

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
