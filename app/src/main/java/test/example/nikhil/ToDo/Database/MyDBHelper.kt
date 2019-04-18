package test.example.nikhil.ToDo.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import test.example.nikhil.ToDo.Pojo.Item

        // DB info
        const val DB_NAME = "db_2do"
        const val DB_VERSION=4


        // Table info
        const val TBL_NAME = "tbl_2do"
        const val COLUMN_ID="id"
        const val COLUMN_DESC = "desc"
        const val COLUMN_STATUS = "status"
        const val COLUMN_PRIORITY="priority"


        // Table create query
const val CREATE_TBL_QUERY="CREATE TABLE $TBL_NAME " +
        "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
        "$COLUMN_DESC VARCHAR(50)," +
        "$COLUMN_PRIORITY INTEGER," +
        "$COLUMN_STATUS INTEGER)"




class MyDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TBL_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_NAME")
        db?.execSQL(CREATE_TBL_QUERY)
    }


    fun insert(desc: String, status: Int,priority:Int): Int? {
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_STATUS, status)
        contentValues.put(COLUMN_DESC, desc)
        contentValues.put(COLUMN_PRIORITY,priority)

        return db?.insert(TBL_NAME, null, contentValues)?.toInt()
        // return newly created row or -1 if fails

        //db.execSQL("Insert into $TBL_NAME($COLUMN_STATUS,$COLUMN_DESC) values ($status,$desc)")
    }


    fun update(id: Int,desc: String,priority: Int): Int? {
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID, id)
        contentValues.put(COLUMN_DESC, desc)
        contentValues.put(COLUMN_PRIORITY,priority)

        return db?.update(TBL_NAME, contentValues, "id=$id",null)
        // return newly created row or -1 if fails

        //db.execSQL("Insert into $TBL_NAME($COLUMN_STATUS,$COLUMN_DESC) values ($status,$desc)")
    }

    fun getAllItems(): ArrayList<Item> {
        val list: ArrayList<Item> = ArrayList()
        val db=this.readableDatabase
        val query = "Select * from $TBL_NAME order by $COLUMN_PRIORITY asc, $COLUMN_ID asc"

        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val item = Item()
                item.id=result.getString(result.getColumnIndex(COLUMN_ID)).toInt()
                item.title = result.getString(result.getColumnIndex(COLUMN_DESC))
                item.check = result.getInt(result.getColumnIndex(COLUMN_STATUS))
                item.priority=result.getInt(result.getColumnIndex(COLUMN_PRIORITY))
                list.add(item)
            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }


    fun deleteItem(id:Int){
        val db:SQLiteDatabase?=this.writableDatabase
        db?.delete(TBL_NAME,"$COLUMN_ID='$id'",null)
    }

    fun updateCheck(id:Int,isCheck:Int):Int?{
        val db:SQLiteDatabase?=this.writableDatabase
        val cv=ContentValues()
        cv.put(COLUMN_STATUS,isCheck)
        return db?.update(TBL_NAME,cv, "$COLUMN_ID = $id",null)
    }

    fun deleteAllItems():Int?{
        val db:SQLiteDatabase?=this.writableDatabase
        return db?.delete(TBL_NAME,"1",null)
    }

}