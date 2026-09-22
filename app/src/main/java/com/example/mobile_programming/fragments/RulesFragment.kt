package com.example.mobile_programming.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R

class RulesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.webViewRules)

        // Читаем HTML-файл из res/raw
        val html = resources.openRawResource(R.raw.rules)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}