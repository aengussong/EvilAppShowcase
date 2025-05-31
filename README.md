# Evil App Showcase
Demonstrates different techniques for hiding the app from the user, obfuscation, detection evasion, persistence and abuse.
All these were gathered from the papers or from the apps, found in the wild. 

### Abuse
Autostart application after installation without user interaction.
The system should query ContactProvider in order to abuse this behavior. Unfortunately, I can't
reproduce it on Android 13.

https://www.bitdefender.com/en-us/blog/labs/malicious-google-play-apps-bypassed-android-security
