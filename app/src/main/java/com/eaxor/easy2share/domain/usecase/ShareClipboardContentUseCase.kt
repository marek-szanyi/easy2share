/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.ClipboardRepository
import javax.inject.Inject

/** Shares clipboard content with the currently connected clients. */
class ShareClipboardContentUseCase
    @Inject
    constructor(
        private val clipboardRepository: ClipboardRepository,
    ) {
        suspend operator fun invoke(content: String) = clipboardRepository.share(content)
    }
