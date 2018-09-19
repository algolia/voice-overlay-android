![Voice Overlay for Android](./docs/banner.png)

<p align="center">
<img src="https://img.shields.io/badge/platform-Android-blue.svg?style=flat" alt="Platform Android" />
<a href="./LICENSE"><img src="http://img.shields.io/badge/license-MIT-blue.svg?style=flat" alt="License: MIT" /></a>
</p>

# Overview TODO: Update gifs

<p float="left">
  <img src="./docs/permission.png" width="200" />
  &nbsp;
  <img src="./docs/listeninginitial.png" width="200" />
  &nbsp;
  <img src="./docs/ripple.gif" width="200" />
  &nbsp;
  <img src="./docs/nopermission.png" width="200" />
  &nbsp;
</p>

# Demo TODO: Update gifs

You can clone this repo, then run the Demo project by doing `./gradlew app:installDebug` and launching the application:

<img src="./Resources/demo.gif" width="250">

# Installation

The Voice overlay is available as a gradle dependency via [JCenter](https://bintray.com/bintray/jcenter). To install
it, add the following line to your app's `build.gradle`:

```groovy
dependencies {
    // [...]
    implementation 'com.algolia.instantsearch-android:voice:1.+'
    // [...]
}
```


# Usage

## Basic usage
1. In your Activity, check if you have the permission and show the appropriate `Dialog`:
```kotlin
if (!isRecordAudioPermissionGranted()) {
    VoicePermissionDialogFragment().show(supportFragmentManager, "DIALOG_PERMISSION")
} else {
    VoiceInputDialogFragment().show(supportFragmentManager, "DIALOG_INPUT")
}
```
_See [it implementated in the demo app](app/src/main/kotlin/com/algolia/instantsearch/voice/demo/MainActivity.kt#L26-L30)._

This will display the permission dialog if the `RECORD_AUDIO` permission was not yet granted, then the voice input dialog once the permission is granted.

Once the user speaks, you will get their input back by implementing `VoiceSpeechRecognizer.ResultsListener`:
```kotlin
override fun onResults(possibleTexts: Array<out String>) {
    // Do something with the results, for example:
    resultView.text = possibleTexts.firstOrNull()?.capitalize()
}
```

## When the permission is not granted

If the user didn't accept the permission, you [should explain](https://developer.android.com/training/permissions/requesting#explain) the permission's rationale. If they deny the permission, you need to guide them into manually enabling it if they want to use the voice-input feature. 

Voice overlay makes it easy to handle all these cases:

```groovy
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (Voice.isRecordPermissionWithResults(requestCode, grantResults)) {
        when {
            Voice.isPermissionGranted(grantResults) -> showVoiceDialog()
            Voice.shouldExplainPermission(this) -> Voice.showPermissionRationale(permissionView, this)
            else -> Voice.showPermissionManualInstructions(permissionView)
        }
    }
    // [...] eventual handling of other permissions requested by your app 
}
```
_See [it implementated in the demo app](app/src/main/kotlin/com/algolia/instantsearch/voice/demo/MainActivity.kt#L42-L51)._

This will display the permission rationale when the user doesn't allow it, and the manual instructions in case they denied it.

## Customization TODO Update for android
You can customize your voice overlay by modifying the `settings` property of the voiceOverlayController:

```swift
/// Specifies whether the overlay directly starts recording (true), 
/// or if it requires the user to click the mic (false). Defaults to true.
voiceOverlayController.settings.autoStart = true

/// Specifies whether the overlay stops recording after the user stops talking for `autoStopTimeout`
/// seconds (true), or if it requires the user to click the mic (false). Defaults to true.
voiceOverlayController.settings.autoStop = true

/// When autoStop is set to true, autoStopTimeout determines the amount of
/// silence time of the user that causes the recording to stop. Defaults to 2.
voiceOverlayController.settings.autoStopTimeout = 2

/// The layout and style of all screens of the voice overlay.
voiceOverlayController.settings.layout.<someScreen>.<someConstant>

// Use XCode autocomplete to see all possible screens and constants that are customisable.
// Examples:

/// The voice suggestions that appear in bullet points
voiceOverlayController.settings.layout.inputScreen.subtitleBulletList = ["Suggestion1", "Sug2"]
/// Change the title of the input screen when the recording is ongoing.
voiceOverlayController.settings.layout.inputScreen.titleListening = "my custom title"
/// Change the background color of the permission screen.
voiceOverlayController.settings.layout.permissionScreen.backgroundColor = UIColor.red
/// And many more...
```

## Getting Help

- **Need help**? Ask a question to the [Algolia Community](https://discourse.algolia.com/) or on [Stack Overflow](http://stackoverflow.com/questions/tagged/algolia).
- **Found a bug?** You can open a [GitHub issue](https://github.com/algolia/instantsearch-ios-insights).
- **Questions about Algolia?** You can search our [FAQ in our website](https://www.algolia.com/doc/faq/).


## Getting involved

* If you **want to contribute** please feel free to **submit pull requests**.
* If you **have a feature request** please **open an issue**.
* If you use **InstantSearch** in your app, we would love to hear about it! Drop us a line on [discourse](https://discourse.algolia.com/) or [twitter](https://twitter.com/algolia).

## License

InstantSearchVoiceOverlay is available under the MIT license. See the [LICENSE file](./LICENSE) for more info.
