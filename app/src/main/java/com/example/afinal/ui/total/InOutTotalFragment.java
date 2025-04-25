package com.example.afinal.ui.total;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.afinal.MyDatabaseHelper;
import com.example.afinal.R;
import com.example.afinal.databinding.FragmentInOutTotal2Binding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InOutTotalFragment extends Fragment {

    private FragmentInOutTotal2Binding binding;

    // 資料庫變數
    private MyDatabaseHelper dbHelper ;
    private SQLiteDatabase db;
    Context mContext = null;
    private static final String db_name = "itemMem.db";
    private static final String tb_name = "item_info";
    private static final int DATABASEVERSION = 1;

    int year = 0;
    int previousYear = 0;
    int currentYear = LocalDate.now().getYear();

    int[] totalArr;

    public static InOutTotalFragment newInstance() {
        return new InOutTotalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // dbHelper
        dbHelper = new MyDatabaseHelper(this.getContext(), db_name, null, DATABASEVERSION, tb_name);
        db = dbHelper.getWritableDatabase();

        binding = FragmentInOutTotal2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // create cursor
        Cursor cur;
        cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, null, null, null, null, "year DESC, month DESC, day DESC");

        totalArr = resetList(cur);

        Spinner spinner = binding.totalYear;
        setTimeSelected(spinner, cur);

        setPieChart(totalArr[0], totalArr[1]);

        return root;
    }

    private int[] resetList(Cursor cur) {
        int inTotal = 0, outTotal = 0;
        List<Integer> lvYear = new ArrayList<>();
        List<Integer> lvCash = new ArrayList<>();
        List<Integer> lvType = new ArrayList<>();

        if(cur.moveToFirst()){
            // 依照 column 順序存入 arrayList
            do{
                if(year != 0 && cur.getInt(0) != year) continue;
                else{
                    lvYear.add(cur.getInt(0));
                    lvType.add(cur.getInt(3));

                    if(cur.getInt(6) > 0) inTotal += cur.getInt(6);
                    else outTotal += cur.getInt(6);

                    lvCash.add(cur.getInt(6));
                }

            }while(cur.moveToNext());
        }

        return new int[]{inTotal, outTotal};
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setTimeSelected(Spinner chooseYear, Cursor cur){

        // choose year
        year = currentYear;

        String[] yearArray = {"-", Integer.toString(currentYear-5), Integer.toString(currentYear-4),
                Integer.toString(currentYear-3), Integer.toString(currentYear-2),
                Integer.toString(currentYear-1), Integer.toString(currentYear),
                Integer.toString(currentYear+1)};

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_spinner_item,
                yearArray);

        chooseYear.setAdapter(yearAdapter);

        chooseYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                previousYear = year;
                if(i > 0) setYear(currentYear - 6 + i);
                if(i == 0) setYear(i);

                if(year != previousYear){ // 與之前不同才進行重置
                    totalArr = resetList(cur);
                    setPieChart(totalArr[0], totalArr[1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

            public void setYear(int y){
                year = y;
            }
        });

    }

    public void setPieChart(int inTotal, int outTotal) {
        int total = inTotal + outTotal;
        PieChart pieChart = binding.inoutFragmentPie;
        List<PieEntry> pieEntries = new ArrayList<>();

        if(inTotal != 0) pieEntries.add(new PieEntry(inTotal, getString(R.string.income)));
        if(outTotal != 0) pieEntries.add(new PieEntry((outTotal * (-1)), getString(R.string.expend)));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(15f);
        PieData data = new PieData(pieDataSet);
        pieChart.setEntryLabelColor(Color.BLUE);
        Legend legend = pieChart.getLegend();
        legend.setTextSize(15f);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.text_earn) + " $" + total);
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(1000);


    }
}