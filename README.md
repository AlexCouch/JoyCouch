# JoyCouch
My own library (obviously in the works) that allows the implementation of the Nintendo Joy-Con. This requires bluetooth.

JoyCouch is a WIP lightweight, flexible, thread-safe Nintendo Joy-Con API. It is very early in development and currently includes:
* Input Handlers
* Rumbling
* JoyconManager for accessing the connected left and right controllers

JoyCouch is going to include the following (note: the following is not a final list and will change over time):
* Analog Stick Calculations
* Analog Stick Calibration
* Player LED settings
* Custom Rumbling
* Mono-and-Dual Joy-Con support
* Thread-safe handling of input and output (will emphasize in future)
* Custom input handling (with raw bytes; e.g.: unsafe input handling)
* JoyCon Factories
  * Customizing input intervals
  * Customizing output reports
  * Customizing Joystick and Gyro/Accel calibration
* Memory reading and writing (will expand upon after more studying and research)
* Battery life and detecting whether Joy-Con is charging
* Multiple player support

Currently referencing this: https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering
