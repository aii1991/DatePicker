package com.boildcoffee.datepickview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.boildcoffee.datepicker.widget.DatePicker;
import com.boildcoffee.datepicker.widget.MonthView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatePicker datePicker = findViewById(R.id.date_picker);
        datePicker.putDescText("2018-12-3","5节");
        datePicker.putDescText("2018-12-4","4节");
        datePicker.putDescText("2018-12-10","3节");
        datePicker.setOnSingleSelectedListener(new MonthView.OnSingleSelectedListener() {
            @Override
            public void onSingleSelected(MonthView.DayCell dayCell) {
                Toast.makeText(MainActivity.this,dayCell.getYear() + "-" + dayCell.getMonth() + "-" + dayCell.getDay(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
