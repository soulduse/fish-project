import com.dave.fish.network.BaseRetrofit
import io.reactivex.Observable

abstract class BaseHttpLoader<T, T1>(private val mOnLoadListener: OnLoadListener<T1>?) {

    private var mObservable : Observable<T>? = null

    enum class ERR_CODE {
        CONNECTION,
        TIMEOUT,
        CONTENT
    }

    fun start(){
        val service = BaseRetrofit.instance.getRetrofit(getBaseUrl())
        this.mObservable = loadService(service)
        this.mObservable?.subscribe({
            response ->
            if (response != null) {
                val data = loadContent(response)
                sendResult(data)
            }else{
                sendErrResult(ERR_CODE.CONTENT)
            }
        },{
            sendErrResult(ERR_CODE.TIMEOUT)
        })
    }

    private fun sendResult(data: T1) {
        mOnLoadListener?.onLoad(this, data)
    }

    private fun sendErrResult(errCode: ERR_CODE) {
        mOnLoadListener?.onFail(this, errCode)
    }

    fun retry(){
        start()
    }

    abstract fun getBaseUrl() : String

    abstract fun loadService(service: Any): Observable<T>

    abstract fun loadContent(data: T?): T1

    interface OnLoadListener<T> {
        fun onLoad(loader: BaseHttpLoader<*, *>, data: T)
        fun onFail(loader: BaseHttpLoader<*, *>, code: ERR_CODE)
    }
}
