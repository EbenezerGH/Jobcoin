import com.api.BuildConfig
import com.api.domain.addressinfo.AddressInfoDataSource
import com.api.domain.addressinfo.AddressInfoRepository
import com.api.domain.addressinfo.AddressInfoRepositoryImpl
import com.api.domain.sendtransaction.SendTransactionDataSource
import com.api.domain.sendtransaction.SendTransactionRepository
import com.api.domain.sendtransaction.SendTransactionRepositoryImpl
import com.api.domain.transactionhistory.TransactionHistoryDataSource
import com.api.domain.transactionhistory.TransactionHistoryRepository
import com.api.domain.transactionhistory.TransactionHistoryRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DataSourceProperties {
    val SERVER_URL = BuildConfig.BASE_URL
}

val remoteDatasourceModule = module {
    single { createOkHttpClient() }
    single { AddressInfoRepositoryImpl(get()) as AddressInfoRepository }
    single { SendTransactionRepositoryImpl(get()) as SendTransactionRepository }
    single { TransactionHistoryRepositoryImpl(get()) as TransactionHistoryRepository }
    single { createWebService<AddressInfoDataSource>(DataSourceProperties.SERVER_URL, get()) }
    single { createWebService<SendTransactionDataSource>(DataSourceProperties.SERVER_URL, get()) }
    single { createWebService<TransactionHistoryDataSource>(DataSourceProperties.SERVER_URL, get()) }
}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
            .callTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
}

private inline fun <reified T> createWebService(url: String, client: OkHttpClient): T {
    return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(T::class.java)
}