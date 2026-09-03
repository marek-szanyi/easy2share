/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.NetworkRepository
import javax.inject.Inject

class GetIpAddressUseCase
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) {
        operator fun invoke(): String = networkRepository.getIpAddress() ?: UNAVAILABLE_ADDRESS

        private companion object {
            const val UNAVAILABLE_ADDRESS = "---------"
        }
    }
