/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

import com.eaxor.easy2share.Constants
import com.eaxor.easy2share.domain.repository.ClipboardRepository
import com.eaxor.easy2share.domain.repository.FileRepository
import com.eaxor.easy2share.domain.repository.NetworkRepository
import com.eaxor.easy2share.domain.usecase.GetClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.GetIpAddressUseCase
import com.eaxor.easy2share.domain.usecase.ShareClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.ShareFilesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `scanned key starts the server and shows it as running`() =
        runTest {
            val key = ByteArray(32) { it.toByte() }
            val viewModel = createViewModel()

            viewModel.setScannedKey(key)

            assertEquals(
                HomeUiState.ServerRunning("192.168.0.25", Constants.DEFAULT_PORT),
                viewModel.uiState.value,
            )
            assertEquals(
                HomeEvents.StartServer(key, Constants.DEFAULT_PORT),
                viewModel.events.first(),
            )
        }

    private fun createViewModel(): HomeViewModel {
        val clipboardRepository =
            object : ClipboardRepository {
                override suspend fun getContent(): String? = null

                override suspend fun share(content: String) = Unit
            }
        val networkRepository =
            object : NetworkRepository {
                override fun getIpAddress(): String = "192.168.0.25"
            }
        val fileRepository =
            object : FileRepository {
                override suspend fun share(fileUris: List<String>): Int = 0
            }
        return HomeViewModel(
            GetClipboardContentUseCase(clipboardRepository),
            GetIpAddressUseCase(networkRepository),
            ShareClipboardContentUseCase(clipboardRepository),
            ShareFilesUseCase(fileRepository),
        )
    }
}
