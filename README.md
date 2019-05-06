# JoyCouch (broken)
My own library (obviously in the works) that allows the implementation of the Nintendo Joy-Con. This requires bluetooth.

### This project is now being written in Rust under the name [joycon-rs](https://www.github.com/AlexCouch/joycon-rs).

I'll be updating this README with some more detailed docs in the future. For now, I am trying to get this to be in working condition.

NOTE: This project is currently migrating to Kotlin/Native in a completely different workspace. Because of this, there will be a new repository (linked here when created) created for the development from here on out.

I have decided to make this a stable working version with documentation as a prototype before moving to a native version. I will of course work on the native porting but I think it'd be good to have something working before I actually port it.

I am also thinking about a new name for this library. Any suggestions are welcome.

Right now, this, to my knowledge, only works on mac. I am sure it might be functional to some degree on linux since mac and linux are both unix based. It is currently broken on windows and I am working to make it more portable. See issue #3

JoyCouch is a WIP easy-to-use, efficient, flexible, thread-safe Nintendo Joy-Con API. It is very early in development and currently includes:
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
* Player LED Settings
* Detecting Battery life
* Custom Rumbling
* Analog Stick Calculations/Calibration
    * This is still being worked on so this will also be in the wip list was well
* Basic async support of Joycons
    * The async functionality is very early and must be improved upon and optimized.

JoyCouch is going to include the following (note: the following is not a final list and will change over time):
* Analog Stick Calculations/Calibration
    * Custom user calibration
* Mono-and-Dual Joy-Con support
* Gyro/Accelerometer support
* Infrared (far future, no idea what to use it for imo)
* Multiple player support
* Better asynchronous support of JoyCons

Currently referencing this: https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering

