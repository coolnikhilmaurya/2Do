package test.example.nikhil.ToDo.Adapter

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import test.example.nikhil.ToDo.Pojo.Item
import com.example.nikhil.ToDo.R
import test.example.nikhil.ToDo.Database.MyDBHelper
import test.example.nikhil.ToDo.Util.MyUtil

class RecyclerAdapter(private var list: ArrayList<Item>) :RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private lateinit var db: MyDBHelper
    private lateinit var ctx:Context
    private lateinit var rview:View

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ctx=recyclerView.context
        db = MyDBHelper(ctx)
        rview=recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun getItemCount() = list.count()

    override fun onBindViewHolder(vholder: MyViewHolder, pos: Int){  //pos is the position
        vholder.tvListItem.text = list[pos].title
        vholder.chkListItem.isChecked = list[pos].check == 1

        // setting the background color of the Item
        when (list[pos].priority) {
            0 ->
                vholder.itemView.setBackgroundColor(Color.parseColor("#C1FF8484"))
            1 ->
                vholder.itemView.setBackgroundColor(Color.parseColor("#C08BF143"))
            2 ->
                vholder.itemView.setBackgroundColor(Color.parseColor("#C1716AF2"))
        }

        // updatation of check in db onClick of checkbox
        vholder.chkListItem.setOnClickListener {
            if (vholder.chkListItem.isChecked) {
                db.updateCheck(list[pos].id, 1)
                list[pos].check = 1
                Snackbar.make(rview, String.format(ctx.getString(R.string.note_checked),list[pos].title), Snackbar.LENGTH_SHORT).show()
            } else {
                db.updateCheck(list[pos].id, 0)
                list[pos].check = 0
                Snackbar.make(rview, String.format(ctx.getString(R.string.note_off_checked),list[pos].title), Snackbar.LENGTH_SHORT).show()

              //  Snackbar.make(rview, "Off Checked ${list[pos].title}", Snackbar.LENGTH_SHORT).show()
            }
            notifyItemChanged(pos)
        }

        // onClick of delete action via delete icon present in layout
        vholder.btnDelete.setOnClickListener {
            deleteOptionClickOnListItem(pos)
        }

        //  Displaying dialog menu with predefined option onItem click of any Item
        vholder.itemView.setOnClickListener{
            val optionAlert = AlertDialog.Builder(ctx)
            optionAlert.setTitle(it.context.getString(R.string.select_option))
            optionAlert.setItems(R.array.list_context_options) { _, which ->
                // The 'which' argument contains the index position of the selected Item
                when (which) {
                    0 -> { // edit
                        editOptionClickOnListItem(pos)
                    }
                    1 -> {  //copy
                       copyOptionClickOnListItem(pos)
                    }
                    2 -> {  // share
                        shareOptionClickOnListItem(pos)
                    }
                    3 -> {  //delste
                        deleteOptionClickOnListItem(pos)
                    }
                }
            }

            val alertDialog = optionAlert.create()
            alertDialog.show()
        }

    }

    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val tvListItem: TextView = v.findViewById(R.id.tvListItem)
        val chkListItem: CheckBox = v.findViewById(R.id.chkListItem)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
    }

    fun updateList(newlist: ArrayList<Item>){
        list= ArrayList()
        list.addAll(newlist)
        notifyDataSetChanged()
    }


    // onClick of edit of an Item
    private fun editOptionClickOnListItem(position: Int) {

        // Initializing BottomSheet

        val bsheet= BottomSheetDialog(ctx)
        val inf=ctx.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v=inf.inflate(R.layout.bottom_sheet,null,false)
        bsheet.setContentView(v)

        val tvText=v.findViewById<EditText>(R.id.neweditText)
        val spnPriorty=v.findViewById<Spinner>(R.id.spnPriority)
        val tvHead=v.findViewById<TextView>(R.id.tvBsHeader)
        tvHead.setText(R.string.update)
        tvText.setText(list[position].title)
        tvText.requestFocus()
        spnPriorty.setSelection(list[position].priority)


        // Initializing Update Button inside BottomSheet

        val btnInsert=v.findViewById<Button>(R.id.btnInsert)
        btnInsert.setText(R.string.update)
        btnInsert.setOnClickListener {
            val noteText=tvText.text.toString()
            val priorty=spnPriorty.selectedItemPosition

            if(noteText.isNotEmpty()){
                val result = db.update(list[position].id,noteText,priorty)
                if (result ==1) {
                    list[position].title=noteText
                    list[position].priority=priorty
                    Snackbar.make(rview, it.context.getText(R.string.insert_success), Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(rview, it.context.getText(R.string.internal_error), Snackbar.LENGTH_SHORT).show()
                }
                bsheet.cancel()
                notifyItemChanged(position)
            }
            else
                Snackbar.make(rview, it.context.getText(R.string.text_required), Snackbar.LENGTH_SHORT).show()
        }
        bsheet.setCancelable(false)
        bsheet.show()

    }

    // onClick of share of an Item
    private fun shareOptionClickOnListItem(position: Int) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, list[position].title)
        if (shareIntent.resolveActivity(ctx.packageManager)!=null) {
            ctx.startActivity(Intent.createChooser(shareIntent, ctx.getString(R.string.share_with)))
        }
        else
            Snackbar.make(rview, ctx.getText(R.string.no_browser_found), Snackbar.LENGTH_SHORT).show()
    }


    // onClick of delete of an Item
    private fun deleteOptionClickOnListItem(position:Int) {
        val noteIdtoDel = list[position].id
        db.deleteItem(noteIdtoDel)
        list.removeAt(position)
        Snackbar.make(rview, ctx.getString(R.string.deleted_item), Snackbar.LENGTH_SHORT).show()
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.count())
    }


    // onClick of copy of an Item
    private fun copyOptionClickOnListItem(position: Int) {
        val text= list[position].title
        MyUtil.copyTextOnClipboard(ctx,text)
        Snackbar.make(rview,ctx.getString(R.string.note_copied),Snackbar.LENGTH_SHORT).show()
    }
}