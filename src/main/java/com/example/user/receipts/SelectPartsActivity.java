package com.example.user.receipts;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

public class SelectPartsActivity extends AppCompatActivity {

    static final int NUMBER_OF_TABLES = 7;
    String table;
    String part;
    Cursor c;
    TextView nameRec;
    ListView listParts;
    DBHelper DbHelper;
    SQLiteDatabase db;
    ArrayList<String> values;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_parts);

        listParts = (ListView) findViewById(R.id.listParts);
        DbHelper = new DBHelper(this);
        values = new ArrayList<>();
        listParts.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        nameRec = (TextView)findViewById(R.id.nameRec);
        String receivedName = getIntent().getStringExtra("name");
        nameRec.setText(receivedName);
        try {
            DbHelper.openDataBase();
            db = DbHelper.getReadableDatabase();
            for (int i = 1; i <= NUMBER_OF_TABLES; i++) {
                table = "n" + String.valueOf(i);
                c = db.query(table, null,
                        "name LIKE ?",
                        new String[]{"%" + receivedName + "%"},
                        null, null, null);
                if (c.moveToFirst()) {
                    int nameIndex = c.getColumnIndex("name");
                    int noticeIndex = c.getColumnIndex("notice");
                    int partIndex;
                    do {
                        for (int k = 1; k <= i; k++) {
                            part = "part" + String.valueOf(k);
                            partIndex = c.getColumnIndex(part);
                            values.add(String.valueOf(k)+"." + c.getString(partIndex));
                            part = null;
                        }

                    } while (c.moveToNext());
                }
                c.close();
            }
        /*    db.close();  */
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            db.close();
            c.close();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, values);
        listParts.setAdapter(adapter);
    }
}
