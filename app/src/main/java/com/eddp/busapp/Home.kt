package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.Post
import com.eddp.busapp.interfaces.AsyncDataObserver
import com.eddp.busapp.views.recycler_view.PostAdapter

class Home : Fragment(), AsyncDataObserver {
    private lateinit var _activity: MainActivity

    private var _postRecyclerView: RecyclerView? = null
    private lateinit var _postAdapter: PostAdapter

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add observer
        this._activity = activity as MainActivity
        this._activity.registerReceiver(this)

        // Adapter
        this._postAdapter = PostAdapter(this._activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillPostRecyclerView(view)
    }

    private fun fillPostRecyclerView(view: View) {
        this._postRecyclerView = view.findViewById(R.id.post_recycler_view)

        if (this._postRecyclerView != null) {
            this._postRecyclerView!!.layoutManager = LinearLayoutManager(context)
            this._postRecyclerView!!.adapter = this._postAdapter
        }
    }

    // Observer
    override fun onDataGet() {
        val posts: List<Post>? = this._activity.getPosts()

        if (posts != null) {
            this._postAdapter.setData(posts as MutableList<Post>)
        }
    }
}