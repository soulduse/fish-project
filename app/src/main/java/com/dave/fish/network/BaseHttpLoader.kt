import com.dave.fish.network.BaseRetrofit
import com.dave.fish.network.NetConfig
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseHttpLoader<T, T1>(private val mOnLoadListener: OnLoadListener<T1>?) {
    private var mCall: Call<T>? = null
    private var mObservable : Observable<T>? = null

    abstract val baseURL: String

    enum class ERR_CODE {
        CONNECTION,
        TIMEOUT,
        CONTENT
    }

    fun startKma(){
        val service = BaseRetrofit.instance.getKmaRetrofit()
        this.mObservable = loadService(service)
        this.mObservable?.subscribe({
            response ->

        },{
            throwable ->

        })
    }

    fun startTide(){
        val service = BaseRetrofit.instance.getTideRetrofit()
        this.mObservable = loadService(service)
    }

    fun start() {
        val retrofit = BaseRetrofit..getRetrofit(baseURL)
        val service = retrofit.create(NetConfig.Service::class.java!!)
        this.mCall = loadService(service)
        this.mCall!!.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>?) {

                if (response != null
                        && response.isSuccessful
                        && response.body() != null) {

                    val data = loadContent(response.body())
                    sendResult(data)

                } else {
                    sendErrResult(ERR_CODE.CONTENT)
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                sendErrResult(ERR_CODE.TIMEOUT)
            }
        })

    }
    */

    private fun sendResult(data: T1) {

        mOnLoadListener?.onLoad(this, data)

    }

    private fun sendErrResult(errCode: ERR_CODE) {

        mOnLoadListener?.onFail(this, errCode)

    }

//    fun retry() {
//        start()
//    }
//
    abstract fun loadService(service: Any): Observable<T>

    abstract fun loadContent(data: T?): T1

    interface OnLoadListener<T> {
        fun onLoad(loader: BaseHttpLoader<*, *>, data: T)
        fun onFail(loader: BaseHttpLoader<*, *>, code: ERR_CODE)
    }
}
