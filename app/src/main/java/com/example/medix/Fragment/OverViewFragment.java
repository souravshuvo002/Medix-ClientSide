package com.example.medix.Fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Database.Database;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverViewFragment extends Fragment {
    private static final String TAG = "OverViewFragment";
    private TextView textViewTestName, textViewPrice, textViewDiscountPrice,
            textViewTestShortName, textViewDiagnosticName, textViewStatus, textViewDate, textViewReviews;
    private LinearLayout linearLay;
    private Button btnBook;
    private RatingBar ratingBar;


    private Date cDate;
    private String currentDate, sell_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        // getting views
        linearLay = (LinearLayout) view.findViewById(R.id.linearLay);
        textViewReviews = (TextView) view.findViewById(R.id.textViewReviews);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        textViewTestName = (TextView) view.findViewById(R.id.textViewTestName);
        textViewPrice = (TextView) view.findViewById(R.id.textViewPrice);
        textViewDiscountPrice = (TextView) view.findViewById(R.id.textViewDiscountPrice);
        textViewTestShortName = (TextView) view.findViewById(R.id.textViewTestShortName);
        textViewDiagnosticName = (TextView) view.findViewById(R.id.textViewDiagnosticName);
        textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        btnBook = (Button) view.findViewById(R.id.btnBook);

        cDate = new Date();
        sell_date = new SimpleDateFormat("MM/dd/yyyy").format(cDate);
        currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);
        textViewDate.setText(currentDate);
        //Common.sellDate = sell_date;

        if (new Database(getActivity()).checkExistence(Common.singleTest.getTest_id())) {
            btnBook.setBackgroundResource(R.color.colorPrimary);
            btnBook.setText("Booked");
            btnBook.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + Common.singleTest.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new Database(getActivity()).clearCartITemFromProduct(Common.singleTest.getTest_id(), Common.singleTest.getCenter_id());
                            btnBook.setText("Book");
                            btnBook.setBackgroundResource(R.color.colorPrimary);

                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                    Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (b != null) {
                        b.setTextColor(Color.parseColor("#000000"));
                    }
                    Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    if (b2 != null) {
                        b2.setTextColor(Color.parseColor("#000000"));
                    }
                    return false;
                }
            });
        }


        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking if the test is already added or not
                if (!new Database(getActivity()).checkExistence(Common.singleTest.getTest_id())) {

                    if (Common.singleTest.getDiscount_price().equals("0.0000")) {
                        new Database(getActivity()).addToCart(new Cart(
                                Common.singleTest.getTest_id(),
                                Common.singleTest.getName(),
                                Common.singleTest.getShort_name(),
                                Common.singleTest.getCenter_id(),
                                Common.singleTest.getDiagnostic_center_name(),
                                Common.singleTest.getAddress(),
                                Common.singleTest.getPrice(),
                                "1"
                        ));
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date endDate = null;
                        try {
                            endDate = df.parse(Common.singleTest.getDis_end_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date currentDate = new Date();

                        if (endDate.before(currentDate)) {
                            new Database(getActivity()).addToCart(new Cart(
                                    Common.singleTest.getTest_id(),
                                    Common.singleTest.getName(),
                                    Common.singleTest.getShort_name(),
                                    Common.singleTest.getCenter_id(),
                                    Common.singleTest.getDiagnostic_center_name(),
                                    Common.singleTest.getAddress(),
                                    Common.singleTest.getPrice(),
                                    "1"
                            ));
                        } else {
                            new Database(getActivity()).addToCart(new Cart(
                                    Common.singleTest.getTest_id(),
                                    Common.singleTest.getName(),
                                    Common.singleTest.getShort_name(),
                                    Common.singleTest.getCenter_id(),
                                    Common.singleTest.getDiagnostic_center_name(),
                                    Common.singleTest.getAddress(),
                                    Common.singleTest.getDiscount_price(),
                                    "1"
                            ));
                        }
                    }
                    btnBook.setBackgroundResource(R.color.colorPrimary);
                    btnBook.setText("Booked");
                    Toast.makeText(getActivity(), Common.singleTest.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Long press to remove", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // get Test Details
        getDetails();

        return view;
    }
    private void getDetails() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.getTestReviews(Common.singleTest.getTest_id());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                linearLay.setVisibility(View.VISIBLE);

                int rating = 0;

                for (int i = 0; i < response.body().getRatingList().size(); i++) {
                    rating += response.body().getRatingList().get(i).getRating();
                }

                if (response.body().getRatingList().size() <= 0) {
                    ratingBar.setRating(0);
                    textViewReviews.setText("(0 reviews)");
                } else {
                    if (response.body().getRatingList().get(0).getStatus().equals("1")) {
                        textViewStatus.setText("In stock");
                    } else {
                        textViewStatus.setText("Out of stock");

                    }
                    ratingBar.setRating(rating / response.body().getRatingList().size());
                    textViewReviews.setText(String.valueOf("( " + response.body().getRatingList().size() + " reviews)"));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });


        textViewTestName.setText(Common.singleTest.getName());
        if (Common.singleTest.getDiscount_price().equals("0.0000")) {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(Common.singleTest.getPrice());
            textViewPrice.setText(new StringBuilder(getActivity().getResources().getString(R.string.currency_sign)).append(df2.format(price)));
        } else {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(Common.singleTest.getDiscount_price());
            textViewPrice.setText(new StringBuilder(getActivity().getResources().getString(R.string.currency_sign)).append(df2.format(price)));
        }
        textViewTestShortName.setText(Common.singleTest.getShort_name());
        textViewDiagnosticName.setText(Common.singleTest.getDiagnostic_center_name());
        if (Common.singleTest.getStatus().equalsIgnoreCase("0")) {
            textViewStatus.setText("Unavailable");
        } else {
            textViewStatus.setText("Available");
        }
    }

    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date, day = null, month = null;

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);

                } else {
                    day = String.valueOf(dayOfMonth);
                }
                if (monthOfYear + 1 < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }

                sell_date = month + "/" + day + "/" + year;
                Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("MM/dd/yyyy");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(sell_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                textViewDate.setText(output1);
                //loadData(Common.sellDate);
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDetails();
    }
}