# JoyCouch
My own library (obviously in the works) that allows the implementation of the Nintendo Joy-Con. This requires bluetooth.

JoyCouch is a WIP easy-to-use, efficient, flexible, thread-safe Nintendo Joy-Con API. It is very early in development and currently includes:
* Input Handlers
* Rumbling
* JoyconManager for managing the interactions with either left and right JoyCons
* Delegated Input Handling (docs coming soon)
* Input/Output handling
    * Customizing input intervals
    * Customizing output reports
    * Customizing input/output handlers
    * Customizing subcommands
    * Access to HID input and output
* Basic Memory reading
* Detecting Battery life

JoyCouch is going to include the following (note: the following is not a final list and will change over time):
* Analog Stick Calculations
* Analog Stick Calibration
* Player LED settings
* Custom Rumbling
* Mono-and-Dual Joy-Con support
* Gyro/Accelerometer support
* Multiple player support

Currently referencing this: https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering

### NOTE: There is currently not working version at the moment. It currently going under a lot of refactoring, reoganization, and optimizations. There are lots of testing that needs to be done. Any contribution must be discussed with me on discord @Alex Couch#5275. Please specify your first message with me to include this project so I know why you're messaging me.
### NOTE: All documentation and JavaDoc comments are NOT final. Sometimes things change and I don't update the docs. They are there mostly for me. Please take some of it with a grain of salt.