package com.akostyukewicz.bottomsheetpanel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akostyukewicz.bottomsheet.BottomSheet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LAYOUT = R.layout.activity_main;

    private View firstLayout;
    private View secondLayout;

    private Button firstLayoutShowBtn;
    private Button secondLayoutShowBtn;
    private Button disableHalfStatusBtn;
    private BottomSheet bottomSheet;
    private TextView status;
    private BottomSheet.BottomSheetBehavior behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        firstLayout = LayoutInflater.from(this).inflate(R.layout.first_layout, null);
        secondLayout = LayoutInflater.from(this).inflate(R.layout.second_layout, null);

        firstLayoutShowBtn = (Button)findViewById(R.id.firstLayoutBtn);
        secondLayoutShowBtn = (Button)findViewById(R.id.secondLayoutBtn);
        disableHalfStatusBtn = (Button)findViewById(R.id.disableHalfBtn);
        bottomSheet = (BottomSheet)findViewById(R.id.bottomSheet);
        status = (TextView)findViewById(R.id.status);

        behavior = new BottomSheet.BottomSheetBehavior() {
            @Override
            public void onFullOpened() {
                status.setText("Full opened");
            }

            @Override
            public void onHalfOpened() {
                status.setText("Half opened");
            }

            @Override
            public void onClosed() {
                status.setText("Closed");
            }
        };

        //bottomSheet.setStaticHalfHeight(300);
        bottomSheet.setBehavior(behavior);
        firstLayoutShowBtn.setOnClickListener(this);
        secondLayoutShowBtn.setOnClickListener(this);
        disableHalfStatusBtn.setOnClickListener(this);
    }

    private void addViewToBottomsheet(View someLayout) {
        bottomSheet.removeAllViews();
        bottomSheet.addView(someLayout);
        bottomSheet.openPanel(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disableHalfBtn:
                if (bottomSheet.isHalfStatusDisabled()) {
                    bottomSheet.setHalfStatusDisabled(false);
                    disableHalfStatusBtn.setText("Disable half state");
                } else {
                    bottomSheet.setHalfStatusDisabled(true);
                    disableHalfStatusBtn.setText("Enable half state");
                }
                break;
            case R.id.firstLayoutBtn:
                addViewToBottomsheet(firstLayout);
                break;
            case R.id.secondLayoutBtn:
                addViewToBottomsheet(secondLayout);
                break;
        }
    }

}
