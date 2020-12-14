package org.tahoe.lafs.di

import org.koin.dsl.module
import org.tahoe.lafs.ui.viewmodel.ScanCodeViewModel

@JvmField
val appModule = module {

    factory { ScanCodeViewModel(get()) }
}