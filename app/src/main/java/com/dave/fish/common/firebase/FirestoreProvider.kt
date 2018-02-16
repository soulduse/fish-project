package com.dave.fish.common.firebase

import com.dave.fish.util.DLog
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * Created by soul on 2018. 2. 16..
 */
class FirestoreProvider {

    private var mFirestore: FirebaseFirestore? = null

    private fun getFirestore(): FirebaseFirestore {
        if (mFirestore == null) {
            mFirestore = FirebaseFirestore.getInstance()
        }

        return mFirestore!!
    }

    fun read(collectionName: String, success: (QuerySnapshot?)-> Unit, fail: ((Exception?)-> Unit) = {}) {
        getFirestore()
                .collection(collectionName)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        success(it.result)
                        it.result.forEach {
                            DLog.w("id : ${it.id} => ${it.data}")
                        }
                    } else {
                        fail(it.exception)
                        DLog.e("error => ${it.exception}")
                    }
                }
    }

    fun readItems(collectionName: String): Task<QuerySnapshot> {
        return getFirestore()
                .collection(collectionName)
                .get()
    }

    fun readFromId(collectionName: String, id: String): DocumentSnapshot? {
        return getFirestore()
                .collection(collectionName)
                .get()
                .result
                .first { it.id == id }

    }


    object Holder {
        val INSTANCE = FirestoreProvider()
    }

    companion object {
        val instance: FirestoreProvider by lazy { Holder.INSTANCE }
    }
}