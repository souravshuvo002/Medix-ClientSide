package com.example.medix.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medix.Common.Common;
import com.example.medix.R;

public class DescriptionFragment extends Fragment {
    private static final String TAG = "DescriptionFragment";
    private TextView textViewLongDesc, textViewShortDesc;
    private LinearLayout linearLongLay, linearShortLay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.description_fragment, container, false);
        linearLongLay = (LinearLayout) view.findViewById(R.id.linearLongLay);
        linearShortLay = (LinearLayout) view.findViewById(R.id.linearShortLay);
        textViewLongDesc = (TextView) view.findViewById(R.id.textViewLongDesc);
        textViewShortDesc = (TextView) view.findViewById(R.id.textViewShortDesc);

        getDesc();

        return view;
    }

    private void getDesc() {

        if (!Common.singleTest.getLong_desc().isEmpty()) {
            linearLongLay.setVisibility(View.VISIBLE);
            textViewLongDesc.setText(Common.singleTest.getLong_desc());
        }

        if (!Common.singleTest.getShort_desc().isEmpty()) {
            linearShortLay.setVisibility(View.VISIBLE);
            textViewShortDesc.setText(Common.singleTest.getShort_desc());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDesc();
    }
}