package com.example.onlineappadmin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onlineappadmin.R
import com.example.onlineappadmin.adapter.AddProductImageAdap
import com.example.onlineappadmin.databinding.FragmentAddProductBinding
import com.example.onlineappadmin.model.AddProductModel
import com.example.onlineappadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import java.util.*
import kotlin.collections.ArrayList

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var list:ArrayList<Uri>
    private lateinit var listImage:ArrayList<String>
    private lateinit var adapter:AddProductImageAdap
    private var coverImage:Uri?=null
    private lateinit var dialog:Dialog
    private var coverImgUri:String?=""
    private lateinit var categoryList:ArrayList<String>
    private var LauchGalleryActivity=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode== Activity.RESULT_OK){
            coverImage=it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility=VISIBLE
        }
    }
    private var LauchProductActivity=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode== Activity.RESULT_OK){
            val imageUrl=it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddProductBinding.inflate(layoutInflater)

        list= ArrayList()
        listImage= ArrayList()
        dialog= Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            val intent= Intent("android.intent.action.GET_CONTENT")
            intent.type="image/*"
            LauchGalleryActivity.launch(intent)
        }
        binding.productImgBtn.setOnClickListener {
            val intent= Intent("android.intent.action.GET_CONTENT")
            intent.type="image/*"
            LauchProductActivity.launch(intent)
        }
        setProductCategory()

        adapter= AddProductImageAdap(list)
        binding.productImgRecyclerView.adapter=adapter
        binding.submitProductBtn.setOnClickListener {
            validateData()
        }

        return binding.root
    }

    private fun validateData() {
        if (binding.productNameEdit.text.toString().isEmpty()){
            binding.productNameEdit.requestFocus()
            binding.productNameEdit.error="Empty"
        }else if (binding.productSp.text.toString().isEmpty()){
            binding.productSp.requestFocus()
            binding.productSp.error="Empty"
        }else if (coverImage==null){
            Toast.makeText(requireContext(),"please select cover Image",Toast.LENGTH_SHORT).show()
        }else if (list.size<1){
            Toast.makeText(requireContext(),"please select product Images",Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()
        val filename= UUID.randomUUID().toString()+".jpg"
        val refStorage= FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    coverImgUri=image.toString()
                    uploadProductImage()
                }
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(),"storage mein problem hai bhia ",Toast.LENGTH_SHORT).show()
            }
    }
    private var i=0


    private fun uploadProductImage() {
        dialog.show()
        val filename= UUID.randomUUID().toString()+".jpg"
        val refStorage= FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    listImage.add(image.toString())
                    if (list.size==listImage.size){
                        storeData()
                    }else{
                        i+=1
                        uploadProductImage()
                    }


                }
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(),"storage mein problem hai bhia ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val db=Firebase.firestore.collection("products")
        val key=db.document().id
        val data=AddProductModel(
            binding.productNameEdit.text.toString(),
            binding.productDisEdit.text.toString(),
            coverImgUri.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.productMrp.text.toString(),
            binding.productSp.text.toString(),
            listImage

        )
        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(),"Product Added",Toast.LENGTH_SHORT).show()
            binding.productNameEdit.text=null
            binding.productDisEdit.text=null
            binding.productMrp.text=null
            binding.productSp.text=null

        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(requireContext(),"Something went Wrong",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProductCategory(){
        categoryList= ArrayList()
        Firebase.firestore.collection("category").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents){
                val data=doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.cate!!)
            }
            categoryList.add(0,"Select Category")
            val arrayAdapter=ArrayAdapter(requireContext(),R.layout.dropdown_item,categoryList)
            binding.productCategoryDropdown.adapter=arrayAdapter
        }
    }

}