# 说明

## 效果图

![DatePickerView.png](https://github.com/aii1991/DatePicker/blob/master/screenshot/DatePickerView.png)

## 使用

```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context="com.boildcoffee.datepickview.MainActivity">

      <com.boildcoffee.datepicker.widget.DatePicker
          android:id="@+id/date_picker"
          android:layout_width="match_parent"
          android:layout_height="match_parent"/>

  </LinearLayout>
```

```java
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
```
