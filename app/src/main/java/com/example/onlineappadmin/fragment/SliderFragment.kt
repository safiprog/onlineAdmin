
package com.example.onlineappadmin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onlineappadmin.R
import com.example.onlineappadmin.databinding.FragmentSliderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SliderFragment : Fragment() {
    private lateinit var binding: FragmentSliderBinding
    private var imageUrl: Uri?=null
    private lateinit var dailog:Dialog


    private var LauchGalleryActivity=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode== Activity.RESULT_OK){
            imageUrl=it.data!!.data
            binding.imageView.setImageURI(imageUrl)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSliderBinding.inflate(layoutInflater)

        dailog= Dialog(requireContext())
        dailog.setContentView(R.layout.progress_layout)
        dailog.setCancelable(false)

        binding.apply {
            imageView.setOnClickListener {
                val intent=Intent("android.intent.action.GET_CONTENT")
                intent.type="image/*"
                LauchGalleryActivity.launch(intent)
            }
            button5.setOnClickListener {
//                fdsfhsdhfdsh
                if (imageUrl != null){
                    upLoadImage(imageUrl!!)
                }else{
                    Toast.makeText(requireContext(),"please select the Image",Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun upLoadImage(Url: Uri) {
        dailog.show()
        val filename=UUID.randomUUID().toString()+".jpg"
        val refStorage=FirebaseStorage.getInstance().reference.child("Slider/$filename")
        refStorage.putFile(Url)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    storeData(image.toString())
                }
            }.addOnFailureListener {
                dailog.dismiss()
                Toast.makeText(requireContext(),"storage mein problem hai bhia ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(image: String) {
        val db= Firebase.firestore
        val data=hashMapOf<String,Any>(
            "img" to image

        )
        db.collection("slider").document("item").set(data)
            .addOnSuccessListener {
                dailog.dismiss()
                Toast.makeText(requireContext(),"slider",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                dailog.dismiss()
                Toast.makeText(requireContext(),"bhai kuch problem hai",Toast.LENGTH_SHORT).show()
            }


    }


}