package com.jobcoin.di

import com.jobcoin.viewmodel.SendViewModel
import com.jobcoin.viewmodel.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val jobcoinViewerModule = module {
    viewModel { SignInViewModel(get()) }
    viewModel { SendViewModel(get(), get(), get()) }
}