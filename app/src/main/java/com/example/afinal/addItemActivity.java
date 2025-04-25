package com.example.afinal;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import android.app.DatePickerDialog;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.afinal.ui.home.HomeFragment;

import java.util.Calendar;
import java.util.Locale;

public class addItemActivity extends AppCompatActivity {

    int currentYear = 0, currentMonth = 0, currentDay = 0;
    int previousCash = 0;
    int viewCash = 0;

    Button lastOpButton;
    boolean calEnable = false;

    int catChoose = 0; // 使用 index 紀錄分類
    int typeChoose = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addItem), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 呼叫 CalculateHelper 物件
        CalculateHelper calculateHelper = new CalculateHelper();

        // 設定支出 / 收入類型選單
        Spinner inputTypeSpinner = findViewById(R.id.input_type);
        String[] inputType = {getString(R.string.income), getString(R.string.expend)};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, inputType);
        inputTypeSpinner.setAdapter(typeAdapter);

        // 設定消費 / 收入類型選單
        Spinner inputCategorySpinner = findViewById(R.id.input_category);
        String[] incomeCategory = {getString(R.string.cat_salary)
                , getString(R.string.cat_save)
                , getString(R.string.cat_invest)};
        String[] expendCategory = {getString(R.string.cat_food)
                , getString(R.string.cat_traffic)
                , getString(R.string.cat_entertainment)};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeCategory);
        inputCategorySpinner.setAdapter(categoryAdapter);

        TextView date, tvCash;
        tvCash = findViewById(R.id.input_cash);
        int currentCash = 0;

        Button dateChoose;
        DatePickerDialog.OnDateSetListener dateSetListener;
        Calendar calendar = Calendar.getInstance();

        EditText inputName = findViewById(R.id.input_itemName);
        String name = "";

        Button btnMulti = findViewById(R.id.btn_multi);
        Button btn_div = findViewById(R.id.btn_div);
        Button btn_add = findViewById(R.id.btn_plus);
        Button btn_minus = findViewById(R.id.btn_minus);

        Button btnOne = findViewById(R.id.btn_1);
        Button btn_two = findViewById(R.id.btn_2);
        Button btn_three = findViewById(R.id.btn_3);
        Button btn_four = findViewById(R.id.btn_4);
        Button btn_five = findViewById(R.id.btn_5);
        Button btn_six = findViewById(R.id.btn_6);
        Button btn_seven = findViewById(R.id.btn_7);
        Button btn_eight = findViewById(R.id.btn_8);
        Button btn_nine = findViewById(R.id.btn_9);
        Button btn_zero = findViewById(R.id.btn_0);

        Button btn_confirm = findViewById(R.id.btn_confirm);
        Button btn_backHome = findViewById(R.id.btn_backHome);
        Button btnEqual = findViewById(R.id.btn_equal);
        Button btn_back = findViewById(R.id.btn_back);

        Button[] numButtons = {btnOne, btn_two, btn_three, btn_four, btn_five, btn_six, btn_seven, btn_eight, btn_nine, btn_zero};
        Button[] operatorButtons = {btnMulti, btn_div, btn_add, btn_minus};

        // 日期選擇
        dateChoose = findViewById(R.id.button_dateChoose);
        date = findViewById(R.id.date);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setButtonEnabled(btn_confirm);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String dateFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.TAIWAN);

                currentYear = year;
                currentMonth = month + 1;
                currentDay = day;

                date.setText(sdf.format(calendar.getTime()));
            }
        };

        dateChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("test", "test");
                DatePickerDialog calenderDialog = new DatePickerDialog(addItemActivity.this
                        , dateSetListener
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));
                calenderDialog.show();
            }
        });

        // 設定確認按鈕事件 V
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 獲取 / 傳遞 EditText 文字
                EditText itemName = findViewById(R.id.input_itemName);
                String name = itemName.getText().toString();
                if(name.equals("")){
                    itemName.setHint("請輸入名稱");
                    itemName.setHintTextColor(getColor(android.R.color.holo_red_dark));
                    return;
                }
                getIntent().putExtra("itemName", name);

                // 獲取分類
                getIntent().putExtra("category", Integer.toString(catChoose));

                // 獲取收入 或 支出
                getIntent().putExtra("type", Integer.toString(typeChoose));

                // 金額
                String cash = tvCash.getText().toString();
                getIntent().putExtra("cash", cash);
                tvCash.setText("0"); // 金額處理完歸零

                // 傳遞日期資訊
                if(currentYear == 0 || currentMonth == 0 || currentDay == 0){
                    date.setText("請輸入日期");
                    date.setTextColor(getColor(android.R.color.holo_red_dark));
                    return;
                }
                getIntent().putExtra("year", Integer.toString(currentYear));
                getIntent().putExtra("month", Integer.toString(currentMonth));
                getIntent().putExtra("day", Integer.toString(currentDay));

                Log.e("return", "new item return.");

                setResult(HomeFragment.RESULT_CODE, getIntent());
                finish();
            }
        });

        // 設定回家按鈕事件 V
        btn_backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_backHome.isEnabled()){
                    // 可以按下，做完資料傳遞後結束此活動
                    finish();
                }
            }
        });

        // 設定運算按鈕事件 V
        for(Button btnOperator : operatorButtons) {
            btnOperator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewCash = Integer.parseInt(tvCash.getText().toString());

                    if(btn_confirm.isEnabled()){
                        setButtonDisabled(btn_confirm);
                    }
                    if(calEnable) setButtonEnabled(btnEqual);

                    // Check whether another operator not calculate yet
                    String current_op = btnOperator.getText().toString();
                    String lastOp = "";
                    if(lastOpButton != null){ // has last opButton
                        lastOp = lastOpButton.getText().toString();
                        Log.e("last Button", lastOp);
                    }

                    setButtonDisabled(btnOperator); // set current button disabled

                    if(lastOpButton == null){
                        Log.e("Button clicked : ", current_op + "Is first");
                        pushPrevious(viewCash); // push current textview to previous
                        viewCash = 0;
                        changeLastOp(btnOperator); // change lastOp
                    }
                    else{
                        // calculate previous and show
                        // [previous] [lastOp] [tvCash]
                        Log.e("Button clicked : ", current_op + "Not first");
                        viewCash = Integer.parseInt(tvCash.getText().toString());
                        String resultStr = "";

                        switch(lastOp){
                            case "+":
                                setButtonEnabled(btn_add); // set last button
                                if(calEnable) viewCash = calculateHelper.add(previousCash, viewCash);
                                break;
                            case "-":
                                setButtonEnabled(btn_minus);
                                if(calEnable) viewCash = calculateHelper.minus(previousCash, viewCash);
                                break;
                            case "*":
                                setButtonEnabled(btnMulti);
                                if(calEnable) viewCash = calculateHelper.multi(previousCash, viewCash);
                                break;
                            case "/":
                                setButtonEnabled(btn_div);
                                if(calEnable) viewCash = calculateHelper.div(previousCash, viewCash);
                                break;
                            default:
                                break;
                        }

                        // change lastOp & disable the clicked button
                        calEnable = false;
                        changeLastOp(btnOperator); // change lastOp
                        setButtonDisabled(btnOperator); // the clicked button disabled

                        // show result
                        resultStr = Integer.toString(viewCash);
                        tvCash.setText(resultStr);

                        // update viewCash
                        pushPrevious(viewCash); // push current textview to previous
                        viewCash = 0;
                    }
                }
            });
        }

        // 設定數字按鈕事件 V
        for(Button btnNum : numButtons) {
            btnNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Num click", "previous : " + previousCash);
                    if(calEnable) setButtonEnabled(btnEqual);

                    if(!btn_confirm.isEnabled()){
                        setButtonEnabled(btn_confirm);
                    }

                    if(!btnEqual.isEnabled() && calEnable){
                        setButtonEnabled(btnEqual);
                    }

                    String btn_name = btnNum.getText().toString(); // 0 ~ 9 in string
                    String strCash = tvCash.getText().toString(); // num str show on screen
                    int screenCash = Integer.parseInt(strCash); // num show on screen
                    int numTap = Integer.parseInt(btn_name); // 點擊的數字
                    Log.e("Num click", "num : " + btn_name);

                    if(lastOpButton == null){
                        calEnable = false;
                        setButtonDisabled(btnEqual); // disable equal button
                        if(screenCash == 0){ // screen num = buttonTap
                            viewCash = numTap;
                            strCash = btn_name;
                            tvCash.setText(strCash);
                        }
                        else{ // 往後加一位
                            viewCash = screenCash; // 現在顯示數字
                            viewCash = viewCash * 10 + numTap;
                            strCash = Integer.toString(viewCash);
                            tvCash.setText(strCash);
                        }
                    }
                    else{ // 有上一個運算子
                        calEnable = true;
                        if(!btnEqual.isEnabled() && calEnable){
                            setButtonEnabled(btnEqual);
                        }

                        if(viewCash > 0){ // 無論 numTap 多少都是往後+一位
                            viewCash = viewCash * 10 + numTap;
                            strCash = Integer.toString(viewCash);
                            tvCash.setText(strCash);
                        }
                        else if(numTap > 0 && viewCash == 0){ // view = 0 and numTap > 0
                            viewCash = numTap;
                            strCash = btn_name;
                            tvCash.setText(strCash);
                        }
                        else if(numTap == 0 && viewCash == 0){ // view = 0 and numTap = 0 => 0
                            strCash = "0";
                            tvCash.setText(strCash);
                        }
                        else{
                            calEnable = false;
                        }
                    }
                }
            });
        }

        // 設定等於按鈕事件 V
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // if onClick => btnEqual is enabled
                Log.e("Calculate", "equal button clicked");
                if(calEnable){ // calculate enable => operate
                    viewCash = Integer.parseInt(tvCash.getText().toString());
                    Button lastOp = getLastOp();
                    switch (lastOp.getText().toString()){
                        case "+":
                            Log.e("Calculate", "add calculate");
                            viewCash = calculateHelper.add(previousCash, viewCash);
                            break;
                        case "-":
                            Log.e("Calculate", "minus calculate");
                            viewCash = calculateHelper.minus(previousCash, viewCash);
                            break;
                        case "*":
                            Log.e("Calculate", "multiply calculate");
                            viewCash = calculateHelper.multi(previousCash, viewCash);
                            break;
                        case "/":
                            Log.e("Calculate", "divide calculate");
                            viewCash = calculateHelper.div(previousCash, viewCash);
                            break;
                        default:
                            Log.e("Calculate", "abnormal calculate");
                            break;
                    }
                    if(lastOpButton != null){
                        setButtonEnabled(lastOpButton); // set last button enable
                    }
                    setLastOp(null); // clear last button
                    setButtonDisabled(btnEqual);
                    previousCash = 0;
                    String resultStr = Integer.toString(viewCash);
                    tvCash.setText(resultStr);
                }
            }
        });

        // 設定倒退按鈕事件 V
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateHelper.back(tvCash, previousCash);
            }
        });

        // 設定分類選單事件
        inputCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catChoose = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                catChoose = 0;
            }
        });

        // 設定支出/收入選單事件
        inputTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeChoose = i;
                if(i == 0) setCatSpinner(inputCategorySpinner, incomeCategory);
                if(i == 1) setCatSpinner(inputCategorySpinner, expendCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setCatSpinner(inputCategorySpinner, incomeCategory);
            }
        });
    }

    // set up equal & confirm button
    public void setButtonEnabled(@NonNull Button btn_name){
        btn_name.setEnabled(true);
        btn_name.setBackgroundResource(R.drawable.button_background);
    }

    // turn down equal & confirm button
    public void setButtonDisabled(@NonNull Button btn_name){
        btn_name.setBackgroundResource(R.drawable.button_background_enable);
        btn_name.setEnabled(false);
    }

    // push previousCash
    public void pushPrevious(int previous){
        previousCash = previous;
    }

    public void changeLastOp(Button currentButton){
        lastOpButton = currentButton;
    }

    public Button getLastOp(){
        return lastOpButton;
    }

    public void setLastOp(Button currentButton){
        lastOpButton = currentButton;
    }

    public void setCatSpinner(Spinner spinner, String[] catArray){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, catArray);
        spinner.setAdapter(adapter);
    }

}
