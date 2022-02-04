package com.jingtian.lmemorycleaner.main

import com.jingtian.lmemorycleaner.bean.MainFunctionsBean

interface MainContract {
    interface View {
        fun bind()
        fun finish()
    }
    interface Presenter {
        fun bind(v:View)
        fun unBind()

        fun onResume()
        fun onDestroy()
        fun getFunctionBean():List<MainFunctionsBean>
        fun startFunction(id:Int)
    }
}