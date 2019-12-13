# SnapNow
A custom Android camera app that uses both Camera1 and Camera2 APIs to support Android phones running pre-V21, V21 or Post-V21 OS. It includes both the custom camera code and a sample use case.

# Overview

The **Picture Picker** is a fragment file that manages image capturing (using an in-app custom camera) and picture selection (via selecting picture from the Gallery)

The picture picker is designed to be inflated (like the regular fragment) into another Android Component, e.g. Activity or Fragment.

# How to use

Assuming that the picture picker will be hosted by fragment `PicturePickerTestFragment`, do the following:

1. Create an instance of class **PicturePickerOption** _[see below for more explanation]_ to customize the view and behavior of the picture picker. For example:
```kotlin
     val picturePickerOption = PicturePickerOption(
                 promptText = "Snap the customer and position the head between the box",
                 showCaptureFrame = true)
```

2. Make **PicturePickerTestFragment** class implement interface **PicturePickerCallback** _[see below for more explanation]_

3. Create a new instance of **PicturePickerFragment** (inside `PicturePickerTestFragment#onCreateView`) by passing the created instances in 1. and 2. above to the newInstance() function of the PicturePickerFragment class. For example:

```kotlin
     val picturePicker = PicturePickerFragment.newInstance(this, picturePickerOption)
```
**NB: 'picturePicker' object can be promoted to a class variable for some use cases of the picture picker.**

4. Inflate the **picturePicker** fragment instance into the fragment container (usually, a FrameLayout or FragmentContainer) prepared by **fragment_picture_picker_test.xml** inside `PicturePickerTestFragment#onCreateView` (see `appendix I` for the complete content in the XML file). For example:

```kotlin
     childFragmentManager.beginTransaction()
                .replace(R.id.container, picturePicker)
                .commit()
```
See more in `appendix II`.

**RECOMMENDATION: `DO NOT` add this fragment transaction to the transaction backstack.**

At this point, you should be able to interact with the Picture Picker UI.


(See Wiki - https://github.com/SirGoingFar/SnapNow/wiki/SnapNow - for more)
