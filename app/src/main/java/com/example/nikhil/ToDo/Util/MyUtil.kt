package com.example.nikhil.ToDo.Util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object MyUtil{

    // Social Links of the developer

    const val DEVELOPER_EMAIL= "coolnikhilmaurya@gmail.com"
    const val DEVELOPER_FB= "http://facebook.com/coolnikhilmaurya"
    const val DEVELOPER_LINKEDIN= "http://linkedin.com/in/coolnikhilmaurya"
    const val DEVELOPER_GIT= "http://gitlab.com/coolnikhilmaurya"

    fun copyTextOnClipboard(ctx:Context,text:String) {
        //get instance of Clipboard Manager
        val clipboard=ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Creates a new text clip to put on the clipboard
        val clip: ClipData = ClipData.newPlainText("simple text", text)
        // Set the clipboard's primary clip.
        clipboard.primaryClip = clip
    }

    fun getTag():String{
        return this.javaClass.simpleName
    }
}
