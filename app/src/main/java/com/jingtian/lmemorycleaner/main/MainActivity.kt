package com.jingtian.lmemorycleaner.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jingtian.lmemorycleaner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainContract.View {
    private val binding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }
    private val logic by lazy {
        MainLogic(this)
    }

    override fun onResume() {
        super.onResume()
        logic.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()
        setContentView(binding.root)
        binding.rvFunctions.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2).apply {
                orientation = GridLayoutManager.VERTICAL
            }
            adapter = MainFunctionRVAdapter(logic.getFunctionBean()) {
                logic.startFunction(it)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        logic.onDestroy()
        logic.unBind()
    }
    override fun bind() {
        logic.bind(this)
    }
}