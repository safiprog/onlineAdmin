package com.example.onlineappadmin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onlineappadmin.R
import com.example.onlineappadmin.adapter.CategoryAdapter
import com.example.onlineappadmin.databinding.FragmentCategoryBinding
import com.example.onlineappadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var imageUrl: Uri?=null
    private lateinit var dailog: Dialog


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

        binding= FragmentCategoryBinding.inflate(layoutInflater)
        dailog= Dialog(requireContext())
        dailog.setContentView(R.layout.progress_layout)
        dailog.setCancelable(false)

        getData()


        binding.apply {
            imageView.setOnClickListener{
                val intent= Intent("android.intent.action.GET_CONTENT")
                intent.type="image/*"
                LauchGalleryActivity.launch(intent)
            }
            uploadCateBtn.setOnClickListener {
                validateData(binding.CategoryName.text.toString())
            }
        }
        return binding.root
    }

    private fun getData() {
        val list=ArrayList<CategoryModel>()
        Firebase.firestore.collection("category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){

                    val data=doc.toObject(CategoryModel::class.java)
                    list.add(data!!)

                }
                Log.d("hamzahero", "getData: ${list.toString()}")
                binding.categoryrecycler.adapter=CategoryAdapter(requireContext(),list)
            }
    }

    private fun validateData(categoryname: String) {
        if(categoryname.isEmpty()){
            Toast.makeText(requireContext(),"please provide the category name :",Toast.LENGTH_SHORT).show()
        }else if(imageUrl ==null){

            Toast.makeText(requireContext(),"Please Select the Image :",Toast.LENGTH_SHORT).show()
        }else{
            uploadImage(categoryname)
        }

    }

    private fun uploadImage(categoryname: String) {
        dailog.show()
        val filename= UUID.randomUUID().toString()+".jpg"
        val refStorage= FirebaseStorage.getInstance().reference.child("category/$filename")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    storeData(categoryname,image.toString())
                }
            }.addOnFailureListener {
                dailog.dismiss()
                Toast.makeText(requireContext(),"storage mein problem hai bhia ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(categoryName: String, url: String) {
        val db= Firebase.firestore
        val data=hashMapOf<String,Any>(
            "cate" to categoryName,
            "img" to url

        )
        db.collection("category").add(data)
            .addOnSuccessListener {
                dailog.dismiss()
                binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.image_preview))

                binding.CategoryName.text=null
                getData()
                Toast.makeText(requireContext(),"category added",Toast.LENGTH_SHORT).show()
                getData()
            }.addOnFailureListener{
                dailog.dismiss()
                Toast.makeText(requireContext(),"bhai kuch problem hai",Toast.LENGTH_SHORT).show()
            }
    }


}
