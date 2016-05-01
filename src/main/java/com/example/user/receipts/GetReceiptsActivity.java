package com.example.user.receipts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

public class GetReceiptsActivity extends AppCompatActivity {

    final String TAG = "myLog";
    final int NUMBER_OF_TABLES = 7;
    Cursor c;
    String table;
    EditText typeRec;
    ListView listRec;
    ArrayList <String> values;
    ArrayAdapter<String> adapter;
    DBHelper DbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_receipts);

        typeRec = (EditText) findViewById(R.id.typeRec);
        listRec = (ListView) findViewById(R.id.listRec);
        DbHelper = new DBHelper(this);
        typeRec.addTextChangedListener(editWatcher);
        values = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        values.clear();
        values.trimToSize();
        listRec.setAdapter(adapter);

        listRec.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(),SelectPartsActivity.class);
                intent.putExtra("name", adapter.getItem(position));
                startActivity(intent);
            }
        });

    }
    private final TextWatcher editWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            adapter.clear();
            if (s.length() != 0){
                try {
                    DbHelper.openDataBase();
                    db = DbHelper.getReadableDatabase();

                    for (int i = 1; i <= NUMBER_OF_TABLES; i++) {
                        table = "n" + String.valueOf(i);
                        c = db.query(table, new String[]{"name"}, "name LIKE ? OR UPPER(name) LIKE ? OR part1 LIKE ?",
                                new String[]{"%"+s.toString()+"%","%"+s.toString().toUpperCase()+"%","%"+s.toString()+"%"}, null, null, null);
                        if (c.moveToFirst()){
                            int nameIndex = c.getColumnIndex("name");
                            do{
                                values.add(c.getString(nameIndex));
                            }while (c.moveToNext());
                        }
                        c.close();
                    }
                /*    db.close();  */
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                finally {
                    db.close();
                    c.close();
                }
            }
        }
    };
    }

