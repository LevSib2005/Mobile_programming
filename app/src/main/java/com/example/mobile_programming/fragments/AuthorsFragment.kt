package com.example.mobile_programming.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R
import com.example.mobile_programming.adapters.AuthorAdapter
import com.example.mobile_programming.models.Author

class AuthorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authors = listOf(
            Author(R.mipmap.ic_launcher, "Голещихин Данил"),
            Author(R.mipmap.ic_launcher, "Гунзенов Лев")
        )

        val listView = view.findViewById<ListView>(R.id.listAuthors)
        listView.adapter = AuthorAdapter(requireContext(), authors)
    }
}