package com.ctcstudio.ebooktemplate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.entities.SettingBook;
import com.ctcstudio.ebooktemplate.view.ContentBookView;

/**
 * Created by HungHN on 5/10/2015.
 */
public class ContentChapFragment extends Fragment {
    private static final String DATA_CONTENT = "CONTENT";


    private static final String DATA_SETTING = "SETTING";

    private SettingBook mSetting;

    private String dataContent = "";

    private ContentBookView bookView;

    private ContentCallBack mCallBack = sDummyCallbacks;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ContentChapFragment create(String page, SettingBook setting) {
        ContentChapFragment fragment = new ContentChapFragment();
        Bundle args = new Bundle();
        args.putString(DATA_CONTENT, page);
        args.putSerializable(DATA_SETTING, setting);
        fragment.setArguments(args);
        return fragment;

    }

    public String getDataContent() {
        return dataContent;
    }

    public ContentChapFragment() {
    }

    private static ContentCallBack sDummyCallbacks = new ContentCallBack() {
        @Override
        public void ContentClick() {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof ContentCallBack)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallBack = (ContentCallBack) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallBack = sDummyCallbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetting = (SettingBook) getArguments().getSerializable(DATA_SETTING);
        dataContent = getArguments().getString(DATA_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_content_chap, container, false);
        bookView = ((ContentBookView) rootView.findViewById(R.id.data_content));

        bookView.setBookPaddingTop(mSetting.getTopPadding());
        bookView.setBookPaddingBottom(mSetting.getBottomPadding());
        bookView.setBookPaddingLeft(mSetting.getLeftPadding());
        bookView.setBookPaddingRight(mSetting.getRightPadding());
        bookView.setSpacingMult(mSetting.getSpacingMult());
        bookView.setSpacingAdd(mSetting.getSpacingAdd());
        bookView.setBookTextColor(mSetting.getTextColor());
        bookView.setBookBackground(mSetting.getPageColor());
        bookView.setBookTextFont(mSetting.getFont());
        bookView.setBookTextSize(mSetting.getTextSize());
        bookView.setTextContent(dataContent);

        bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.ContentClick();
            }
        });

        return rootView;
    }

    public interface ContentCallBack {
        public void ContentClick();
    }
}
