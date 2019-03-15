# JoyCouch
My own library (obviously in the works) that allows the implementation of the Nintendo Joy-Con. This requires bluetooth.

JoyCouch is a WIP lightweight, flexible, thread-safe Nintendo Joy-Con API. It is very early in development and currently includes:
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
