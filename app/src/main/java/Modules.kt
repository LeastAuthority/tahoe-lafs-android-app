import org.koin.dsl.module
import org.tahoe.lafs.ScanCodeViewModel

@JvmField
val appModule = module {

    factory { ScanCodeViewModel(get()) }
}