/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.FileRepository
import javax.inject.Inject

/** Shares the selected files with the currently connected clients. */
class ShareFilesUseCase
    @Inject
    constructor(
        private val fileRepository: FileRepository,
    ) {
        /** Returns the number of files that were successfully queued for sharing. */
        suspend operator fun invoke(fileUris: List<String>): Int = fileRepository.share(fileUris)
    }
