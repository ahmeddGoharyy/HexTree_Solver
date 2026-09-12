# HexTree Solver

A custom Jetpack Compose Android application designed to automate and execute exploit chains for the HexTree Android Attack Surface CTF. This project replaces manual ADB interactions with a streamlined, app-to-app exploitation framework.

## Overview

Developed for offensive security testing and Android vulnerability research, this solver interacts directly with the vulnerable target application via Inter-Process Communication (IPC). It demonstrates the practical exploitation of common Android misconfigurations by serving payloads directly from memory and bypassing standard sandbox boundaries.

## Note  !!

Many flags had additional steps, research on your own

## Tech Stack

* **Language:** Kotlin


## Usage

1. Install the vulnerable HexTree Attack Surface APK on your rooted emulator (e.g., Genymotion).
2. Build and deploy **HexTree Solver** to the same device.
3. Launch the solver and click the corresponding flag button to fire the payload.

> **Disclaimer:** This tool is built specifically for educational purposes and authorized CTF environments.
