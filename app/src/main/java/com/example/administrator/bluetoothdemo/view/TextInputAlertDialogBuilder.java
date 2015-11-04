package com.example.administrator.bluetoothdemo.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Administrator on 2015/9/24.
 */
public class TextInputAlertDialogBuilder {

    private Context mParent;
    private AlertDialog.Builder mBuilder;


    public TextInputAlertDialogBuilder(Context parent) {
        mParent = parent;
        mBuilder = new AlertDialog.Builder(mParent);
    }

    public void setTitle(String title) {
        mBuilder = mBuilder.setTitle(title);
    }

    public void setView(View view) {
        mBuilder = mBuilder.setView(view);
    }
    public void setView(int layoutResId) {
        mBuilder = mBuilder.setView(layoutResId);
    }
    public void setPositiveButton(String text, DialogInterface.OnClickListener onClickListener){
        mBuilder = mBuilder.setPositiveButton(text, onClickListener);
    }
    public void setPositiveButton(int resId, DialogInterface.OnClickListener onClickListener){
        mBuilder = mBuilder.setPositiveButton(resId, onClickListener);
    }
    public void setNegativeButton(String text, DialogInterface.OnClickListener onClickListener){
        mBuilder = mBuilder.setNegativeButton(text, onClickListener);
    }
    public void setNegativeButton(int resId, DialogInterface.OnClickListener onClickListener){
        mBuilder = mBuilder.setNegativeButton(resId, onClickListener);
    }

    public AlertDialog show() {
        return mBuilder.show();
    }
}
