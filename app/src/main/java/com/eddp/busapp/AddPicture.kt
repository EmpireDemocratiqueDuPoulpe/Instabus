package com.eddp.busapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import java.io.ByteArrayOutputStream
import java.io.File

class AddPicture : Fragment(), View.OnClickListener, WebServiceReceiver {
    private var _webServiceLink: WebServiceLink? = null
    private var _context: Context? = null
    private var _userId: Int = Int.MIN_VALUE

    private lateinit var _imgPath: Uri
    private lateinit var _title: EditText
    private lateinit var _addBtn: Button
    private lateinit var _progressBar: ProgressBar

    // Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this._webServiceLink = WebServiceLink(this)
        this._context = activity

        if (context != null) {
            this._userId = PreferenceManager.getDefaultSharedPreferences(context).getInt("user_id", Int.MIN_VALUE)
        }

        this._imgPath = Uri.parse(arguments?.getString("img_path"))

        fillNewPictureInfo(view)
        getAddBtn(view)
        initShareBtn(view)
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

    private fun initShareBtn(view: View) {
        (activity as CameraActivity).showShareBtn(true)
        (activity as CameraActivity).getShareBtn().setOnClickListener {
            //share image
            try {
                val intent = Intent(Intent.ACTION_SEND).setType("image/*")

                val bitmap = view.findViewById<ImageView>(R.id.new_img_container).drawable.toBitmap()
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "busapp_shared_tmp", null)
                val uri = Uri.parse(path)

                intent.putExtra(Intent.EXTRA_STREAM, uri)

                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        this._webServiceLink?.addPost(
            this._userId,
            (this._context as CameraActivity).getStationId(),
            postTitle,
            this._imgPath
        )
    }

    override fun addSuccessful(success: Boolean, message: String) {
        super.addSuccessful(success, message)

        // Delete the saved picture
        if(this._imgPath.path != null){
            val picture = File(this._imgPath.path!!)
            if (picture.exists()) picture.delete()
        }

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
        activity?.finish()
    }
}