package com.example.afinal.ui.total;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.afinal.MyDatabaseHelper;
import com.example.afinal.R;
import com.example.afinal.databinding.FragmentCatTotal2Binding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatTotalFragment extends Fragment {

    private FragmentCatTotal2Binding binding;

    // 資料庫變數
    Cursor cur;
    private MyDatabaseHelper dbHelper ;
    private SQLiteDatabase db;
    private static final String db_name = "itemMem.db";
    private static final String tb_name = "item_info";
    private static final int DATABASEVERSION = 1;

    int year = 0;
    int month = 0;
    int previousYear = 0;
    int previousMonth = 0;
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();

    boolean pieMode = false;

    SimpleAdapter adapter;

    public static CatTotalFragment newInstance() {
        return new CatTotalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // dbHelper
        dbHelper = new MyDatabaseHelper(this.getContext(), db_name, null, DATABASEVERSION, tb_name);
        db = dbHelper.getWritableDatabase();

        binding = FragmentCatTotal2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // create cursor
        cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, null, null, null, null, "year DESC, month DESC, day DESC");

        // choose month
        final Spinner chooseMonth = binding.totalMonth;
        final Spinner chooseYear = binding.totalYear;
        setTimeSelected(chooseMonth, chooseYear);
        resetTable(cur);

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void resetTable(Cursor cur){
        List<Integer> lvYear = new ArrayList<>();
        List<Integer> lvMonth = new ArrayList<>();
        List<Integer> lvDay = new ArrayList<>();
        List<Integer> lvCategory = new ArrayList<>();
        List<String> lvItemName = new ArrayList<>();
        List<Integer> lvCash = new ArrayList<>();
        List<Integer> lvType = new ArrayList<>();

        if(cur.moveToFirst()){
            // 依照 column 順序存入 arrayList
            do{
                if(month != 0 && cur.getInt(1) != month) continue;
                else if(year != 0 && cur.getInt(0) != year) continue;
                else{
                    lvYear.add(cur.getInt(0));
                    lvMonth.add(cur.getInt(1));
                    lvDay.add(cur.getInt(2));
                    lvType.add(cur.getInt(3));
                    lvCategory.add(cur.getInt(4));
                    lvItemName.add(cur.getString(5));
                    lvCash.add(cur.getInt(6));
                }

            }while(cur.moveToNext());

        }

        int[] inTotal = {0, 0, 0}, outTotal = {0, 0, 0};

        HashMap<Object, Integer> typeTotal = new HashMap<Object, Integer>();

        for(int i = 0 ; i < lvCategory.size() ; i++){
            Map<Integer, Integer> item = new HashMap<Integer, Integer>();
            item.put(lvType.get(i), lvCategory.get(i));
            String testStr = "{" + lvType.get(i) + "=" + lvCategory.get(i) + "}";
            if(item.toString().equals(testStr)) typeTotal.put(item, lvCash.get(i) + typeTotal.getOrDefault(item, 0));
            else typeTotal.put(item, lvCash.get(i));
        }

        for(Map.Entry<Object,Integer> entry : typeTotal.entrySet()){
            Log.e("Switch String", entry.getKey().toString());
            switch (entry.getKey().toString()){
                case "{0=0}":
                    inTotal[0] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                case "{0=1}":
                    inTotal[1] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                case "{0=2}":
                    inTotal[2] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                case "{1=0}":
                    outTotal[0] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                case "{1=1}":
                    outTotal[1] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                case "{1=2}":
                    outTotal[2] += entry.getValue();
                    Log.e("Total", Integer.toString(inTotal[0]));
                    break;
                default: break;
            }
        }

        setPieChart(inTotal, outTotal);

        ListView lv = binding.catList;
        List<Map<String, Object>> inList = new ArrayList<Map<String, Object>>();
        String[] inName = {getString(R.string.cat_salary), getString(R.string.cat_save), getString(R.string.cat_invest)};
        for(int i = 0 ; i < inTotal.length ; i++){
            if(inTotal[i] > 0){
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("category", inName[i]);
                item.put("cash", inTotal[i]);
                inList.add(item);
            }

        }

        List<Map<String, Object>> outList = new ArrayList<Map<String, Object>>();
        String[] outName = {getString(R.string.cat_food), getString(R.string.cat_traffic), getString(R.string.cat_entertainment)};
        for(int i = 0 ; i < outTotal.length ; i++){
            if(outTotal[i] < 0){
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("category", outName[i]);
                item.put("cash", outTotal[i]);
                outList.add(item);
            }
        }

        Context mContext = this.getActivity();


        Button inButton = binding.inButton;
        Button outButton = binding.outButton;

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieMode = false;
                inButton.setEnabled(false);
                outButton.setEnabled(true);
                Log.e("Button", "inClick");
                adapter = new SimpleAdapter(mContext, inList, R.layout.fragment_cat_list, new String[]{"category", "cash"}, new int[]{R.id.category, R.id.cash});
                lv.setAdapter(adapter);
                setPieChart(inTotal, outTotal);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieMode = true;
                inButton.setEnabled(true);
                outButton.setEnabled(false);
                Log.e("Button", "outClick");
                adapter = new SimpleAdapter(mContext, outList, R.layout.fragment_cat_list, new String[]{"category", "cash"}, new int[]{R.id.category, R.id.cash});
                lv.setAdapter(adapter);
                setPieChart(inTotal, outTotal);
            }
        });
        if(!pieMode) adapter = new SimpleAdapter(mContext, inList, R.layout.fragment_cat_list, new String[]{"category", "cash"}, new int[]{R.id.category, R.id.cash});
        else adapter = new SimpleAdapter(mContext, outList, R.layout.fragment_cat_list, new String[]{"category", "cash"}, new int[]{R.id.category, R.id.cash});
        lv.setAdapter(adapter);
    }

    private void setPieChart(int[] inTotal, int[] outTotal) {
        int total;
        PieChart pieChart = binding.catFragmentPie;
        List<PieEntry> pieEntries = new ArrayList<>();

        if(!pieMode){
            if(inTotal[0] > 0) pieEntries.add(new PieEntry(inTotal[0], getString(R.string.cat_salary)));
            if(inTotal[1] > 0) pieEntries.add(new PieEntry(inTotal[1], getString(R.string.cat_save)));
            if(inTotal[2] > 0) pieEntries.add(new PieEntry(inTotal[2], getString(R.string.cat_invest)));

        }
        else {
            if(outTotal[0] < 0) pieEntries.add(new PieEntry((outTotal[0] * (-1)), getString(R.string.cat_food)));
            if(outTotal[1] < 0) pieEntries.add(new PieEntry((outTotal[1] * (-1)), getString(R.string.cat_traffic)));
            if(outTotal[2] < 0) pieEntries.add(new PieEntry((outTotal[2] * (-1)), getString(R.string.cat_entertainment)));
        }


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "分類統計");
        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueTextColor(Color.BLUE);
        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLUE);
        Legend legend = pieChart.getLegend();
        legend.setTextSize(15f);
        pieChart.invalidate();

        pieChart.getDescription().setEnabled(false);
        if(year > 0) pieChart.setCenterText(year + "年");
        else pieChart.setCenterText(getString(R.string.menu_total));
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(1000);

    }

    private void setTimeSelected(Spinner chooseMonth, Spinner chooseYear){

        // choose year
        Log.e("Time", currentYear + "/" + currentMonth);

        year = currentYear;
        month = currentMonth;

        String[] yearArray = {"-", Integer.toString(currentYear-5), Integer.toString(currentYear-4),
                Integer.toString(currentYear-3), Integer.toString(currentYear-2),
                Integer.toString(currentYear-1), Integer.toString(currentYear),
                Integer.toString(currentYear+1)};

        // set adapter
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.month_selected));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                yearArray);

        chooseMonth.setAdapter(monthAdapter);
        chooseYear.setAdapter(yearAdapter);

        chooseYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                previousYear = year;
                if(i > 0) setYear(currentYear - 6 + i);
                if(i == 0) setYear(i);

                if(year != previousYear){
                    resetTable(cur); // 與之前不同才進行重置
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

            public void setYear(int y){
                year = y;
            }
        });

        chooseMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                previousMonth = month;
                setMonth(i);
                if(i != previousMonth) {
                    resetTable(cur); // 與之前不同才進行重置
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

            public void setMonth(int m){
                month = m;
            }
        });

    }

}