package com.example.afinal;

import android.widget.TextView;

public class CalculateHelper {

    public int add(int previous, int adder) {
        return previous + adder;
    }

    public int minus(int previous, int minus) {
        previous -= minus;
        return Math.max(previous, 0); // if previous < 0, return 0
    }

    public int multi(int previous, int multi) {
        return previous * multi;
    }

    public int div(int previous, int div) {
        return previous / div;
    }

    public void back(TextView tv, int previous) {
        String strCash = tv.getText().toString();
        int cash = Integer.parseInt(strCash);

        if(cash == 0 && previous > 0){
            cash = previous;
        }
        else cash /= 10;

        strCash = Integer.toString(cash);
        tv.setText(strCash);
    }

}
