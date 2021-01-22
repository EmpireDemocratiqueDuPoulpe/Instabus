package com.eddp.busapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import net.gotev.uploadservice.MultipartUploadRequest
import java.util.*

class AddPicture : Fragment(), View.OnClickListener, WebServiceReceiver {
    private var _webServiceLink: WebServiceLink? = null
    private var _context: Context? = null

    private lateinit var _imgPath: Uri
    private lateinit var _title: EditText
    private lateinit var _addBtn: Button
    private lateinit var _progressBar: ProgressBar

    // Views
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this._webServiceLink = WebServiceLink(this)
        this._context = activity

        this._imgPath = Uri.parse(arguments?.getString("img_path"))

        fillNewPictureInfo(view)
        getAddBtn(view)
    }

    private fun fillNewPictureInfo(view: View) {
        view.findViewById<ImageView>(R.id.new_img_container).setImageURI(this._imgPath)
        this._title = view.findViewById(R.id.add_picture_title)
    }

    private fun getAddBtn(view: View) {
        this._addBtn = view.findViewById(R.id.add_picture_btn)
        this._addBtn.setOnClickListener(this)
        this._progressBar = view.findViewById(R.id.add_picture_loading)
    }

    // On add picture listener
    override fun onClick(v: View?) {
        if (v?.id != R.id.add_picture_btn) return;

        // Prevent empty post title
        val postTitle: String = this._title.text.toString()

        if (postTitle.isEmpty()) {
            val errorBorder = GradientDrawable()
            errorBorder.setColor(Color.WHITE)
            errorBorder.setStroke(2, Color.RED)

            this._title.background = errorBorder
            return
        }

        // Change button to load bar
        this._addBtn.visibility = View.INVISIBLE
        this._progressBar.visibility = View.VISIBLE

        // Upload
        this._webServiceLink?.addPost(1, 1498, postTitle, this._imgPath)
    }

    override fun addSuccessful(success: Boolean) {
        super.addSuccessful(success)

        if (success) {
            returnToMainActivity()
        } else {
            if (this._context == null) returnToMainActivity()

            AlertDialog
                .Builder(this._context!!)
                .setTitle(this._context!!.getString(R.string.cam_add_post_error_title))
                .setMessage(this._context!!.getString(R.string.cam_add_post_error))
                .setPositiveButton(this._context!!.getString(R.string.cam_add_post_error_positive_btn)) { _, _ ->
                    returnToMainActivity()
                }
        }
    }

    private fun returnToMainActivity() {
        startActivity(Intent(this._context, MainActivity::class.java))
    }

    //private fun uploadPost(title: String, imgPath: Uri) {
    //    try {
    //        val uploadId: String = UUID.randomUUID().toString()
//
//
    //    }
    //}
}