package test.example.nikhil.ToDo.Activities

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import test.example.nikhil.ToDo.Adapter.RecyclerAdapter
import test.example.nikhil.ToDo.Pojo.Item
import com.example.nikhil.ToDo.R
import kotlinx.android.synthetic.main.about_developer.view.*
import test.example.nikhil.ToDo.Database.MyDBHelper
import test.example.nikhil.ToDo.Util.MyUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity(),android.support.v7.widget.SearchView.OnQueryTextListener {

    private val db= MyDBHelper(this)
    private var list: ArrayList<Item> = ArrayList()
    private lateinit var myRecyclerAdapter: RecyclerAdapter
    private lateinit var myIntent:Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // creating locale
            val loc = "hi"
            val locale2 = Locale(loc)
            Locale.setDefault(locale2)
            val config2 = Configuration()
            config2.locale = locale2

// updating locale
            this.getResources().updateConfiguration(config2, null)


            // handle shared text through any other app
            if(intent!=null){
                handleSharedIntent(intent)
            }


            fabInsert.setOnClickListener {
                myInsertFabOnClickListener()
            }
        } catch (ex: Exception) {
            // logging exception and notifying user
            Log.d(MyUtil.getTag(), ex.message)
            Snackbar.make(mainLayout, "Unexpected error occured. Please try again or report us if error persist", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // loading data in recyclerView
        loadData()
    }

    // Changing text in SearchView
    override fun onQueryTextChange(newText: String?): Boolean {
        val str=newText?.toLowerCase()
        val newList:ArrayList<Item> = ArrayList()
        for(item in list){
            if(item.title.toLowerCase().contains(str as CharSequence))
            {
                newList.add(item)
            }
        }
        myRecyclerAdapter.updateList(newList)
        return true
    }

    // Submit text in SearchView
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }


    private fun handleSharedIntent(sharedIntent: Intent) {
        // when the other app Shares text it is placed as a text/plan mime type
        // on the intent so we can then retrieve that text off the incoming intent
        if (sharedIntent.action == Intent.ACTION_SEND && sharedIntent.type == "text/plain") {
            val sharedText = sharedIntent.getStringExtra(Intent.EXTRA_TEXT)
            val result = db.insert(sharedText, 0, 0)  // high priority
            if (result == 1) {
                Snackbar.make(mainLayout, getString(R.string.insert_success), Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(mainLayout, getString(R.string.internal_error), Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    // onClick of Insert FAB
    private fun myInsertFabOnClickListener() {
        val bSheet=BottomSheetDialog(this)
        val v=layoutInflater.inflate(R.layout.bottom_sheet,myRecyclerView,false)
        bSheet.setContentView(v)
        val btnInsert=v.findViewById<Button>(R.id.btnInsert)
        btnInsert.setOnClickListener {
            val tvText=v.findViewById<EditText>(R.id.neweditText)
            val sanPriority=v.findViewById<Spinner>(R.id.spnPriority)
            val noteText=tvText.text.toString()
            val priorty=sanPriority.selectedItemPosition

            if(noteText.isNotEmpty()){
                val result = db.insert(noteText, 0,priorty)
                if (result != -1) {
                    Snackbar.make(mainLayout, getString(R.string.insert_success), Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(mainLayout, getString(R.string.internal_error), Snackbar.LENGTH_SHORT).show()
                }
                bSheet.cancel()
                loadData()
            }
            else {
                Snackbar.make(mainLayout, getString(R.string.text_required), Snackbar.LENGTH_SHORT).show()
            }
        }
        bSheet.setCancelable(true)
        bSheet.show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inflating custom menu
        menuInflater.inflate(R.menu.menu, menu)

        // Registering the top SearchView
        val etSearch=menu!!.findItem(R.id.menuSearch)
        val searchView=etSearch.actionView as android.support.v7.widget.SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.btnClearAll -> {
                clearAllOptionClick()
            }
            R.id.btnAboutApp -> {
                btnAboutDeveloperClick()
            }
            R.id.btnFeedBack -> {
                feedbackOptionClick()
            }
//            R.id.btnShareApp->{
//
//            }
        }
        return true
    }

    private fun btnAboutDeveloperClick() {

        val bSheet=BottomSheetDialog(this)
        val v=layoutInflater.inflate(R.layout.about_developer,myRecyclerView,false)
        bSheet.setContentView(v)
        bSheet.setCancelable(true)


        val btnMail = v.findViewById<ImageButton>(R.id.btnMail)
        val btnLinkedIn =v.findViewById<ImageButton>(R.id.btnLinkedIn)
        val btnFb=v.findViewById<ImageButton>(R.id.btnFb)
        val btnGit=v.findViewById<ImageButton>(R.id.btnGit)
        val imgProfile=v.ImgProfile

        Glide.with(this)
                .load(R.drawable.myphoto)
                .circleCrop()
                .into(imgProfile)

        // Connecting to social Links of the developer
        btnMail.setOnClickListener{
            myIntent = Intent(Intent.ACTION_SENDTO)
            myIntent.data = Uri.parse("mailto:${MyUtil.DEVELOPER_EMAIL}")
            if (myIntent.resolveActivity(packageManager) != null) {
                // There is an activity which can handle this intent.
                startActivity(myIntent)
            }
            else{
                // No Activity found that can handle this intent.
                Toast.makeText(this, getString(R.string.no_email_client_app_found), Toast.LENGTH_SHORT).show()

            }
        }

        btnLinkedIn.setOnClickListener{
            myIntent = Intent(Intent.ACTION_VIEW)
            myIntent.data = Uri.parse(MyUtil.DEVELOPER_LINKEDIN)
            if (myIntent.resolveActivity(packageManager) != null) {
                startActivity(myIntent)
            }
            else{
                Snackbar.make(it, getString(R.string.no_browser_found), Snackbar.LENGTH_SHORT).show()
            }
        }

        btnFb.setOnClickListener{
            myIntent = Intent(Intent.ACTION_VIEW)
            myIntent.data=(Uri.parse(MyUtil.DEVELOPER_FB))
            if (myIntent.resolveActivity(packageManager) != null) {
                startActivity(myIntent)
            }
            else{
                Snackbar.make(it, getString(R.string.no_browser_found), Snackbar.LENGTH_SHORT).show()

            }
        }

        btnGit.setOnClickListener {
            myIntent = Intent(Intent.ACTION_VIEW)
            myIntent.data = Uri.parse(MyUtil.DEVELOPER_GIT)
            if (myIntent.resolveActivity(packageManager) != null) {
                startActivity(myIntent)
            }
            else{
                Snackbar.make(View(parent), getString(R.string.no_browser_found), Snackbar.LENGTH_SHORT).show()
            }
        }

        bSheet.show()
    }

    private fun feedbackOptionClick() {

        // showing an AlertDialog for confirming to lead to email app
        val alertDialog:AlertDialog.Builder=AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.lead_to_email_for_feedback))
        alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${MyUtil.DEVELOPER_EMAIL}")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            else{
                // No Activity found that can handle this intent.
                Snackbar.make(mainLayout, getString(R.string.no_email_client_app_found), Snackbar.LENGTH_SHORT).show()

            }
        }
        alertDialog.setNegativeButton(getString(R.string.no),null)
        alertDialog.setCancelable(true)
        val alert2Dialog = alertDialog.create()
        alert2Dialog.show()
    }


    // Clearing all the db onClick of clear all options
    private fun clearAllOptionClick() {
        val adelAll:AlertDialog.Builder=AlertDialog.Builder(this)
        adelAll.setTitle(getString(R.string.sure_to_del_all_notes))
        adelAll.setCancelable(true)
        adelAll.setPositiveButton(getString(R.string.yes)) { _, _ ->
            db.deleteAllItems()
            Snackbar.make(mainLayout, getString(R.string.cleared_all_items), Snackbar.LENGTH_SHORT).show()
            loadData()
        }
        adelAll.setNegativeButton(getString(R.string.no),null)
        adelAll.setCancelable(true)
        val alertDialog = adelAll.create()
        alertDialog.show()
    }


    // Loading data in RecyclerView
    private fun loadData() {
        list = db.getAllItems()
        if (list.count() == 0) {
            Snackbar.make(mainLayout, getString(R.string.no_items_avl_do_insrt), Snackbar.LENGTH_SHORT).show()
        }
        myRecyclerAdapter= RecyclerAdapter(list)
        myRecyclerView.adapter = myRecyclerAdapter
    }
}
