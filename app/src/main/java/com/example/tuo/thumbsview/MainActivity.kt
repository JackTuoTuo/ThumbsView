package com.example.tuo.thumbsview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.tuo.thumbsview.view.ThumbsView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv1: ThumbsView = findViewById(R.id.thumbsView1) as ThumbsView
        val tv2: ThumbsView = findViewById(R.id.thumbsView2) as ThumbsView
        val tv3: ThumbsView = findViewById(R.id.thumbsView3) as ThumbsView
        val tv4: ThumbsView = findViewById(R.id.thumbsView4) as ThumbsView
        val tv5: ThumbsView = findViewById(R.id.thumbsView5) as ThumbsView
        val btn: Button = findViewById(R.id.btn) as Button


        btn.setOnClickListener({
            tv1.show()
            tv2.show()
            tv3.show()
            tv4.show()
            tv5.show()
        })

    }
}
