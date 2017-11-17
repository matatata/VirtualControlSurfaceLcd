# VirtualControlSurfaceLcd

I own a Behringer Midi-Controller BCF2000 that can be setup to emulate a LogicControl (kindof Mackie Control) to control Digital Audio Worktations (DAWs) like Logic Pro or Cubase. The original control surfaces usually have LCD-Display that display textual feedback from the DAW such as the parameter names and values you're currently changing. The BCF2000 has no display, so Behringer offered a PC-Application that emulates the display in a window on your computer screen. Since I use macOS I started writing a Java-Application that does the same. I do not offer prebuild binaries, so if you're interested you'll need to checkout, build an install https://github.com/matatata/curcuma first `mvn clean install` before doing the same with this repository. Then configure the app by double clicking the small blue window and put the controller in the middle of the midi-flow between the DAW and the controller. Configure the DAW to send midi-feedback to the virtual MIDI port this app creates.
