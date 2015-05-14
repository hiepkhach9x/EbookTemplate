package com.ctcstudio.ebooktemplate.task;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.ctcstudio.ebooktemplate.entities.SettingBook;

import java.util.ArrayList;

/**
 * Created by HungHN on 5/10/2015.
 */
public class SplitTextTask extends AsyncTask<Void, Void, ArrayList<String>> {

    private SettingBook mSetting;

    private onLoadAsync ls;

    private String data;

    private TextPaint mTextPaint;

    public SplitTextTask(Context ctx, String data, SettingBook setting, onLoadAsync ls) {

        this.data = data;
        this.mSetting = setting;
        this.ls = ls;

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mSetting.getTextColor());
        mTextPaint.setTextSize(mSetting.getTextSize());
        mTextPaint.bgColor = mSetting.getPageColor();
        mTextPaint.setAntiAlias(true);
        if (!mSetting.getFont().isEmpty()) {
            Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + mSetting.getFont());
            mTextPaint.setTypeface(tf);
        }

    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> lstSplitText = new ArrayList<String>();
        int offsetI = 0, offsetII = 0;
        StaticLayout layout = new StaticLayout(data, mTextPaint, mSetting.getWidth() - (mSetting.getLeftPadding() + mSetting.getRightPadding()), Layout.Alignment.ALIGN_NORMAL, mSetting.getSpacingMult(), mSetting.getSpacingAdd(), false);
        int totalLines = layout.getLineCount();
        int linePerPage = layout.getLineForVertical(mSetting.getHeight() - mSetting.getBottomPadding()) - 1;
        int i = 0;
        do {
            int line = Math.min(linePerPage * (i + 1), totalLines - 1);
            offsetII = layout.getOffsetForHorizontal(line, mSetting.getWidth() - mSetting.getLeftPadding() - mSetting.getRightPadding());
            String sub = data.substring(offsetI, offsetII).trim();
            offsetI = offsetII;
            lstSplitText.add(sub);
            i++;
        } while (offsetII < data.length());

        return lstSplitText;
    }

    @Override
    protected void onCancelled(ArrayList<String> result) {
        ls.onLoadFail(result);
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        ls.onLoadDone(result);
    }

    public interface onLoadAsync {

        public void onLoadDone(ArrayList<String> results);

        public void onLoadFail(ArrayList<String> results);
    }
}
