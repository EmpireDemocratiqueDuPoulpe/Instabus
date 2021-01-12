package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.Post
import com.eddp.busapp.views.recycler_view.PostAdapter
import com.eddp.busapp.views.recycler_view.StationAdapter

class Home : Fragment() {
    private var _postRecyclerView: RecyclerView? = null
    private lateinit var _postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._postAdapter = PostAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_home, container, false)

        this._postRecyclerView = v.findViewById(R.id.post_recycler_view)

        if (this._postRecyclerView != null) {
            this._postRecyclerView!!.layoutManager = LinearLayoutManager(context)

            this._postAdapter.setData(
                arrayListOf(
                    Post(
                        1,
                        "Some user",
                        "Post title",
                        "1/1/1970",
                        "path/to/img",
                        5
                    ),
                    Post(
                        2,
                        "Another user",
                        "Post title 2",
                        "1/1/1970",
                        "path/to/img",
                        2
                    ),
                    Post(
                        3,
                        "Some user",
                        "Shit post",
                        "1/1/1970",
                        "path/to/img",
                        999
                    ),
                )
            )
            this._postRecyclerView!!.adapter = this._postAdapter
        }

        return v
    }
}