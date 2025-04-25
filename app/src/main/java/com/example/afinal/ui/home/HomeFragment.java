package com.example.afinal.ui.home;

import android.content.ContentValues;
import android.database.Cursor;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.afinal.MyDatabaseHelper;
import com.example.afinal.R;
import com.example.afinal.addItemActivity;
import com.example.afinal.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public static final int RESULT_CODE = 2;

    // 資料庫變數
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

    SimpleAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        int inTotal = 0, outTotal = 0;

        // dbHelper
        dbHelper = new MyDatabaseHelper(this.getContext(), db_name, null, DATABASEVERSION, tb_name);
        db = dbHelper.getWritableDatabase();

        // create cursor
        Cursor cur;
        Log.e("insertYear", Integer.toString(year));
        if(year != 0) cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, "year = ?", new String[]{Integer.toString(year)}, null, null, "year DESC, month DESC, day DESC");
        else cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, null, null, null, null, "year DESC, month DESC, day DESC");
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
                if(year != 0 && cur.getInt(0) != year) ;
                else{
                    lvYear.add(cur.getInt(0));
                    lvMonth.add(cur.getInt(1));
                    lvDay.add(cur.getInt(2));
                    lvType.add(cur.getInt(3));
                    lvCategory.add(cur.getInt(4));
                    lvItemName.add(cur.getString(5));

                    if(cur.getInt(6) > 0) inTotal += cur.getInt(6);
                    else outTotal += cur.getInt(6);

                    lvCash.add(cur.getInt(6));
                }

            }while(cur.moveToNext());

        }

        // binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // draw chart
        setTotalChart(inTotal, outTotal);

        // choose month
        final Spinner chooseMonth = binding.inputMonth;
        final Spinner chooseYear = binding.inputYear;
        setTimeSelected(chooseMonth, chooseYear);

        // total calculate
        totalSet(inTotal, outTotal);

        final ListView lv = binding.itemList;

        final Button newItem = binding.newItem;
        newItem.setOnClickListener(this::addData);

        int[] values = {
                R.id.year, R.id.month, R.id.day,
                R.id.category, R.id.itemName, R.id.cash
                , R.id.deleteBtn
        };

        String[][] categoryType = {{getString(R.string.cat_salary), getString(R.string.cat_save), getString(R.string.cat_invest)}
                                , {getString(R.string.cat_food), getString(R.string.cat_traffic), getString(R.string.cat_entertainment)}};

        if(!lvYear.isEmpty()){
            List<Map<String, Object>> items = new ArrayList<>();
            for(int i = 0 ; i < lvYear.size() ; i++){
                Map<String, Object> item = new HashMap<>();
                String category = categoryType[lvType.get(i)][lvCategory.get(i)];

                item.put("year", Integer.toString(lvYear.get(i)));
                if(lvMonth.get(i) < 10) item.put("month", "0" + lvMonth.get(i));
                else item.put("month", Integer.toString(lvMonth.get(i)));

                if(lvDay.get(i) < 10) item.put("day", "0" + lvDay.get(i));
                else item.put("day", Integer.toString(lvDay.get(i)));

                item.put("category", category);
                item.put("itemName", lvItemName.get(i));
                item.put("cash", Integer.toString(lvCash.get(i)));
                item.put("deleteBtn", R.string.text_noDelete);
                items.add(item);
            }
            adapter = new SimpleAdapter(this.getActivity(), items, R.layout.item_content, new String[]{"year", "month", "day", "category", "itemName", "cash", "deleteBtn"}, values);

            lv.setAdapter(adapter);
            // 刪除功能監聽器
            lv.setOnItemClickListener((adapterView, view, i, l) -> {
                Button btn = view.findViewById(R.id.deleteBtn);
                btn.setEnabled(true);
                btn.setBackgroundResource(R.drawable.deletebtn_back_disable);
                String[] dataInfo = {
                        ((TextView)view.findViewById(R.id.year)).getText().toString()
                        , ((TextView)view.findViewById(R.id.month)).getText().toString()
                        , ((TextView)view.findViewById(R.id.day)).getText().toString()
                        , ((TextView)view.findViewById(R.id.itemName)).getText().toString()
                        , ((TextView)view.findViewById(R.id.cash)).getText().toString()
                };

                btn.setText(R.string.text_canDelete);
                btn.setOnClickListener(view1 -> deleteData(dataInfo));
            });

        }

        cur.close();
        return root;
    }

    private void setTotalChart(int inTotal, int outTotal) {
        int total = inTotal + outTotal;
        PieChart pieChart = binding.totalPie;
        List<PieEntry> pieEntries = new ArrayList<>();

        if(inTotal != 0) pieEntries.add(new PieEntry(inTotal, ""));
        if(outTotal != 0) pieEntries.add(new PieEntry((outTotal * (-1)), ""));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(12f);
        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.text_earn) + " $" + total);
        pieChart.animateY(1000);


    }

    private void totalSet(int inTotal, int outTotal) {
        TextView totalIncome = binding.totalIncome;
        TextView totalExpend = binding.totalExpend;
        String incomeStr = "$ " + inTotal;
        String expendStr = "$ " + outTotal;
        totalIncome.setText(incomeStr);
        totalExpend.setText(expendStr);
    }

    public void addData(View view){
        Intent intent = new Intent(this.getActivity(), addItemActivity.class);
        addItemLauncher.launch(intent);
    }

    // 新增品項 V
    ActivityResultLauncher<Intent> addItemLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_CODE && result.getData() != null){
                    db = dbHelper.getWritableDatabase();

                    String newYear = result.getData().getStringExtra("year");
                    String newMonth = result.getData().getStringExtra("month");
                    String newDay = result.getData().getStringExtra("day");

                    String itemName = result.getData().getStringExtra("itemName");

                    String category = result.getData().getStringExtra("category"); // category index
                    String categoryText = "";

                    String type = result.getData().getStringExtra("type");
                    String cash = result.getData().getStringExtra("cash");

                    if(type.equals("0")){ // 收入
                        type = getString(R.string.income);
                        switch (Integer.parseInt(category)){
                            case 0 :
                                categoryText = getString(R.string.cat_salary);
                                break;
                            case 1 :
                                categoryText = getString(R.string.cat_save);
                                break;
                            case 2 :
                                categoryText = getString(R.string.cat_invest);
                                break;
                            default :
                                break;
                        }
                    }
                    else{ // 支出
                        type = getString(R.string.expend);
                        switch (Integer.parseInt(category)){
                            case 0 :
                                categoryText = getString(R.string.cat_food);
                                break;
                            case 1 :
                                categoryText = getString(R.string.cat_traffic);
                                break;
                            case 2 :
                                categoryText = getString(R.string.cat_entertainment);
                                break;
                            default :
                                break;
                        }
                    }

                    String newItemStr = "日期 : " + newYear + "-" + newMonth + "-" + newDay + "\n"
                                        + "名稱 : " + itemName + "\n"
                                        + "分類 : " + categoryText + "\n"
                                        + "支出 / 收入 : " + type + "\n"
                                        + "金額 : " + cash;

                    // 收入 => cash (+), 支出 => cash (-)
                    if(type.equals(getString(R.string.expend))){
                        type = "1";
                        cash = "-" + cash;
                    }
                    else type = "0";

                    // alert dialog
                    new MaterialAlertDialogBuilder(this.getContext())
                            .setTitle("新增品項")
                            .setMessage(newItemStr)
                            .setPositiveButton("確定", null)
                            .show();
                    Log.e("insert", "insert message success");

                    // put new content into db
                    ContentValues cv = new ContentValues();
                    Log.e("insert", "cv created");

                    cv.put("year", Integer.parseInt(newYear));
                    cv.put("month", Integer.parseInt(newMonth));
                    cv.put("day", Integer.parseInt(newDay));
                    cv.put("type", Integer.parseInt(type));
                    cv.put("category", Integer.parseInt(category));
                    cv.put("itemName", itemName);
                    cv.put("cash", Integer.parseInt(cash));

                    Log.e("insert", "cv put success");

                    db.insert(tb_name, null, cv);

                    Log.e("insert", "insert success");

                    reQuery();
                    Log.e("insert", "update success");

                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void reQuery(){
        int inTotal = 0, outTotal = 0;
        db = dbHelper.getWritableDatabase();

        int[] values = {
                R.id.year, R.id.month, R.id.day,
                R.id.category, R.id.itemName, R.id.cash, R.id.deleteBtn
        };

        // create cursor
        Cursor cur;
        if(year != 0){
            if(month != 0) cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, "year = ? AND month = ?", new String[]{Integer.toString(year), Integer.toString(month)}, null, null, "year DESC, month DESC, day DESC");
            else cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, "year = ?", new String[]{Integer.toString(year)}, null, null, "year DESC, month DESC, day DESC");
            Log.e("insertYear", Integer.toString(month));
        }
        else if(month != 0) cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, "month = ?", new String[]{Integer.toString(month)}, null, null, "year DESC, month DESC, day DESC");
        else cur = db.query(tb_name, new String[]{"year", "month", "day", "type", "category", "itemName", "cash"}, null, null, null, null, "year DESC, month DESC, day DESC");
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
                if(year != 0 && cur.getInt(0) != year) continue;
                if(month != 0 && cur.getInt(1) != month) continue;

                lvYear.add(cur.getInt(0));
                lvMonth.add(cur.getInt(1));
                lvDay.add(cur.getInt(2));
                lvType.add(cur.getInt(3));
                lvCategory.add(cur.getInt(4));
                lvItemName.add(cur.getString(5));

                if(cur.getInt(6) > 0) inTotal += cur.getInt(6);
                else outTotal += cur.getInt(6);

                lvCash.add(cur.getInt(6));
            }while(cur.moveToNext());

        }

        totalSet(inTotal, outTotal);

        // draw chart
        setTotalChart(inTotal, outTotal);

        String[][] categoryType = {{getString(R.string.cat_salary), getString(R.string.cat_save), getString(R.string.cat_invest)}
                , {getString(R.string.cat_food), getString(R.string.cat_traffic), getString(R.string.cat_entertainment)}};
        final ListView lv_reset = binding.itemList;

        List<Map<String, Object>> items = new ArrayList<>();
        if(!lvYear.isEmpty()){
            for(int i = 0 ; i < lvYear.size() ; i++){
                Map<String, Object> item = new HashMap<>();
                String category = categoryType[lvType.get(i)][lvCategory.get(i)];
                Log.e("type category", "Type " + lvType.get(i) + " Category " + lvCategory.get(i));

                item.put("year", Integer.toString(lvYear.get(i)));
                if(lvMonth.get(i) < 10) item.put("month", "0" + lvMonth.get(i));
                else item.put("month", Integer.toString(lvMonth.get(i)));

                if(lvDay.get(i) < 10) item.put("day", "0" + lvDay.get(i));
                else item.put("day", Integer.toString(lvDay.get(i)));

                item.put("category", category);
                item.put("itemName", lvItemName.get(i));
                item.put("cash", Integer.toString(lvCash.get(i)));
                item.put("deleteBtn", getString(R.string.text_noDelete));
                items.add(item);
            }

        }
        adapter = new SimpleAdapter(this.getActivity(), items, R.layout.item_content, new String[]{"year", "month", "day", "category", "itemName", "cash", "deleteBtn"}, values);
        lv_reset.setAdapter(adapter);

        cur.close();
        db.close();
    }

    private void deleteData(String[] info){
        Log.e("Delete", "Delete process");
        db = dbHelper.getWritableDatabase();
        // year = ? AND month = ? AND day = ? AND category = ? AND itemName = ? AND cash = ?
        db.delete(tb_name, "year = ? AND month = ? AND day = ? AND itemName = ? AND cash = ?", info);
        Log.e("Delete", "Delete success");
        db.close();
        refresh();
    }

    private void refresh(){
        this.getActivity().recreate();
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
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.month_selected));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getContext(),
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
                    reQuery(); // 與之前不同才進行重置
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
                    reQuery();
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