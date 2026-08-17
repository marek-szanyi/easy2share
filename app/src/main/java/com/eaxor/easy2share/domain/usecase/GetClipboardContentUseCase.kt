/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.ClipboardRepository
import javax.inject.Inject

/**
 * Reads the current primary clip and normalises it into a single string ready
 * for sharing:
 *
 *  - textual content is returned as-is;
 *  - non-textual content (e.g. an image behind a content URI) is read as raw
 *    bytes and Base64 encoded (standard alphabet with padding, matching what
 *    the web client decodes);
 *  - `null` when the clipboard is empty or unreadable.
 */
class GetClipboardContentUseCase
    @Inject
    constructor(
        private val clipboardRepository: ClipboardRepository,
    ) {
        suspend operator fun invoke(): String? = clipboardRepository.getContent()
    }
