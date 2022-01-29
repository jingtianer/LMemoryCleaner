package com.jingtian.lmemorycleaner.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Application
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.*

lateinit var app: Application
/* 新的协程job */
fun newMainCoroutineJob(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch(block = block)
}

suspend fun <T> newIOTask(block: suspend CoroutineScope.() -> T) :T{
    return withContext(Dispatchers.IO, block)
}

fun toast(string: String) {
    Toast.makeText(app.baseContext, string, Toast.LENGTH_SHORT).show()
}
fun LottieAnimationView?.start(imageFolder: String, jsonFile: String, repeat: Int = ValueAnimator.INFINITE, listener : Animator.AnimatorListener?=null) {
    this ?: return
    kotlin.runCatching {
        this.imageAssetsFolder = imageFolder
        this.setAnimation(jsonFile)
        this.repeatCount = repeat
        this.playAnimation()
        listener?.let {
            this.addAnimatorListener(it)
        }
    }
}
object Utils {
    fun init(application:Application) {
        app = application
        MeowUtil
    }
}