package com.jingtian.lmemorycleaner.boost

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.jingtian.lmemorycleaner.databinding.ActivityBoostBinding
import com.jingtian.lmemorycleaner.utils.*
import com.jingtian.lmemorycleaner.utils.MeowUtil.gradient
import com.jingtian.lmemorycleaner.utils.MeowUtil.killBgProcess

class BoostActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityBoostBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lottie.start(
            "boost_images",
            "boost_images/data.json",
            repeat = ValueAnimator.REVERSE
        )
        newMainCoroutineJob {
            binding.root.gradient("#ffff3300", "#ff0099ff", "#ff00cc66")
            killBgProcess()
            binding.lottie.cancelAnimation()
            finish()
        }
    }

}