package com.eddp.busapp.interfaces

interface AsyncDataObservable {
    fun registerReceiver(dataObserver: AsyncDataObserver)
    fun unregisterReceiver(dataObserver: AsyncDataObserver)
    fun notifyGet()
}