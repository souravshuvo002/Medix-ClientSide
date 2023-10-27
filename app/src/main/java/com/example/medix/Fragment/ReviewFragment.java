package com.example.medix.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medix.Adapter.TestCommentsAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Model.Rating;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragment extends Fragment implements RatingDialogListener {

    private static final String TAG = "ReviewFragment";
    private LinearLayout linearLay;
    private RelativeLayout layEmpty;
    private Button btn_write_review;
    private RecyclerView recyclerViewComments;
    private ImageView fab;
    private TestCommentsAdapter adapter;
    private List<Rating> ratingList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment,container,false);
        fab = (ImageView) view.findViewById(R.id.fab);
        btn_write_review = (Button) view.findViewById(R.id.btn_write_review);
        layEmpty = (RelativeLayout) view.findViewById(R.id.layEmpty);
        linearLay = (LinearLayout) view.findViewById(R.id.linearLay);
        recyclerViewComments = (RecyclerView) view.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingBar();
            }
        });

        btn_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingBar();
            }
        });

        getProductReviews(Common.singleTest.getTest_id());
        return view;
    }

    private void getProductReviews(final String test_id) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.getTestReviews(test_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if(response.body().getRatingList().size() <=0)
                {
                    linearLay.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    layEmpty.setVisibility(View.VISIBLE);
                }
                else
                {
                    layEmpty.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    linearLay.setVisibility(View.VISIBLE);
                    ratingList = response.body().getRatingList();
                    adapter = new TestCommentsAdapter(ratingList, getActivity());
                    recyclerViewComments.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    private void showRatingBar() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this item")
                .setDescription("Please select some stars and give feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here....")
                .setHintTextColor(android.R.color.white)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(getActivity())
                .setTargetFragment(this, 1) // only if listener is implemented by fragment
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int rateValue, String comment) {

        String CUSTOMER_ID, USERNAME;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        USERNAME = sharedPreferences.getString("USERNAME", null);
        CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.addTestReview(Common.singleTest.getTest_id(), CUSTOMER_ID, USERNAME, comment, String.valueOf(rateValue), "1");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                linearLay.setVisibility(View.VISIBLE);
                getProductReviews(Common.singleTest.getTest_id());
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

}