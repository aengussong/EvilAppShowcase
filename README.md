# Evil App Showcase
Demonstrates different techniques for hiding the app from the user, obfuscation, detection evasion, persistence and abuse.
All these were gathered from the papers or from the apps, found in the wild. 

### App hiding
- [Hide app icon from the launcher by setting LEANBACK_LAUNCHER as the main activity category](https://github.com/aengussong/EvilAppShowcase/blob/fe712e8db0d22d5ae651ba9b1369a6e5d217ca7f/app/src/main/AndroidManifest.xml#L24)[^1]. 
- Exclude activity from the recents.
  - `excludeFromRecents` [excludes the activity](https://github.com/aengussong/EvilAppShowcase/blob/ad1833f9746a1735209e0275f5d9ce2d4a9cb30f/app/src/main/AndroidManifest.xml#L18) from the recents[^1]. 
  - `finishOnCloseSystemDialogs` [closes the activity](https://github.com/aengussong/EvilAppShowcase/blob/ad1833f9746a1735209e0275f5d9ce2d4a9cb30f/app/src/main/AndroidManifest.xml#L19) when the system dialog (power menu, recent apps, etc) opens [^1]. It should be noted that the activity is still shown if it was in the foreground when the user opened the recent apps screen. 
- Disguise the app’s name and icon to resemble a legitimate application and reduce the risk of being uninstalled by the user[^1]. This name and icon will be displayed in the system settings.
### Detection evasion
- Detect emulator[^2][^3]
### Credits
[^1]: https://www.bitdefender.com/en-us/blog/labs/malicious-google-play-apps-bypassed-android-security
[^2]: https://github.com/OWASP/owasp-mastg/blob/master/Document/0x05j-Testing-Resiliency-Against-Reverse-Engineering.md#emulator-detection-examples
[^3]: https://www.justmobilesec.com/en/blog/bypassing-android-anti-emulation-part-i
