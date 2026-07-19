/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point.
 *
 * Annotated with [HiltAndroidApp] so Hilt can generate the application-level
 * dependency container and serve as the parent of every other Hilt component.
 */
@HiltAndroidApp
class Easy2ShareApplication : Application()
