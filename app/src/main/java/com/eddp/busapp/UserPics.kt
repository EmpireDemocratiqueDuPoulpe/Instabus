package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.recycler_view.UserPicAdapter

class UserPics : Fragment() {
    private var _userPicRecyclerView: RecyclerView? = null
    private val _userPicAdapter = UserPicAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_user_pics, container, false)

        this._userPicRecyclerView = v.findViewById(R.id.user_pic_recycler_view)

        if (this._userPicRecyclerView != null) {
            this._userPicRecyclerView!!.layoutManager = GridLayoutManager(context, 2)
            this._userPicRecyclerView!!.addItemDecoration(
                GridSpacingItemDecoration(2, 40, true)
            )

            this._userPicAdapter.setData(
                arrayListOf(
                    UserPic(
                        1,
                        1,
                        "Post title",
                        "1/1/1970",
                        "path/to/img",
                        5
                    ),
                    UserPic(
                        3,
                        1,
                        "Shit post",
                        "1/1/1970",
                        "path/to/img",
                        999
                    ),
                )
            )
            this._userPicRecyclerView!!.adapter = this._userPicAdapter
        }

        return v
    }
}