# JoyCouch (broken)
My own library (obviously in the works) that allows the implementation of the Nintendo Joy-Con. This requires bluetooth.

I'll be updating this README with some more detailed docs in the future. For now, I am trying to get this to be in working condition.

### NOTE: There is currently not working version at the moment. It currently going under a lot of refactoring, reoganization, and optimizations. There are lots of testing that needs to be done. Any contribution must be discussed with me on discord @Alex Couch#5275. Please specify your first message with me to include this project so I know why you're messaging me.
### NOTE: All documentation and JavaDoc comments are NOT final. Sometimes things change and I don't update the docs. They are there mostly for me. Please take some of it with a grain of salt.
### NOTE: The header will have either "broken" or "working". Obviously meaning that you can either test it and it'll work or it won't.

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

