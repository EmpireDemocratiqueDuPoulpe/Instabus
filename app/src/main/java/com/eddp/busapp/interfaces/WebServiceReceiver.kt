package com.eddp.busapp.interfaces

interface WebServiceReceiver {
    fun setData(data: Any?) {}
    fun addSuccessful(success: Boolean) {}
}