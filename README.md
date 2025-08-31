# Evil Android App Showcase
Demonstrates different techniques for hiding the app from the user, obfuscation, detection evasion, persistence and abuse.
All these were gathered from the papers or from the apps, found in the wild. 

### App hiding
- [Hide app icon from the launcher by setting LEANBACK_LAUNCHER as the main activity category](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/AndroidManifest.xml#L26)[^1]. 
- Exclude activity from the recents.
  - `excludeFromRecents` [excludes the activity](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/AndroidManifest.xml#L18) from the recents[^1]. 
  - `finishOnCloseSystemDialogs` [closes the activity](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/AndroidManifest.xml#L19) when the system dialog (power menu, recent apps, etc) opens [^1]. It should be noted that the activity is still shown if it was in the foreground when the user opened the recent apps screen.
- [Disguise the app’s name and icon](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/AndroidManifest.xml#L9) to resemble a legitimate application and reduce the risk of being uninstalled by the user[^1]. This name and icon will be displayed in the system settings.
### Detection evasion
- [Detect emulator](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/java/com/aengussong/evilappshowcase/analysis_detectors/EmulatorDetector.kt#L17)[^2][^3][^4][^5][^6][^7]
- [Detect frida](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/java/com/aengussong/evilappshowcase/analysis_detectors/FridaDetector.kt#L9)[^4]
- [Detect root](https://github.com/aengussong/EvilAppShowcase/blob/master/app/src/main/java/com/aengussong/evilappshowcase/analysis_detectors/RootDetector.kt#L12)[^4]
### Credits
[^1]: https://www.bitdefender.com/en-us/blog/labs/malicious-google-play-apps-bypassed-android-security
[^2]: https://github.com/OWASP/owasp-mastg/blob/master/Document/0x05j-Testing-Resiliency-Against-Reverse-Engineering.md#emulator-detection-examples
[^3]: https://www.justmobilesec.com/en/blog/bypassing-android-anti-emulation-part-i
[^4]: https://labs.k7computing.com/index.php/android-spyware-alert-fake-government-app-targeting-android-users-in-india/
[^5]: https://www.trustwave.com/en-us/resources/blogs/spiderlabs-blog/unmasking-malicious-apks-android-malware-blending-click-fraud-and-credential-theft/
[^6]: https://www.bitsight.com/blog/toxicpanda-android-banking-malware-2025-study
[^7]: https://www.intel471.com/blog/android-trojan-tgtoxic-updates-its-capabilities