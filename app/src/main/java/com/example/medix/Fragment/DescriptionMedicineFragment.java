package com.example.medix.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medix.Common.Common;
import com.example.medix.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DescriptionMedicineFragment extends Fragment {
    private static final String TAG = "DescriptionMedicineFragment";
    private TextView textViewFoodLongDesc;
    private LinearLayout linearLay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.description_medicine_fragment,container,false);
        linearLay = (LinearLayout) view.findViewById(R.id.linearLay);
        textViewFoodLongDesc = (TextView) view.findViewById(R.id.textViewFoodLongDesc);

        getProductDesc();

        return view;
    }

    private void getProductDesc() {
        linearLay.setVisibility(View.VISIBLE);
        textViewFoodLongDesc.setText(Html.fromHtml(Html.fromHtml(Common.singleProduct.getDescription()).toString()));
    }

}