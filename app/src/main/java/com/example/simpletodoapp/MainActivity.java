package com.example.simpletodoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POS = "item_pos";
    public static final int REQUEST_CODE = 202;

    List<String> items;
    EditText insertItemsText;
    Button addButton;
    RecyclerView rView;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadItems();

        insertItemsText = findViewById(R.id.editText);
        addButton = findViewById(R.id.btnAdd);
        rView = findViewById(R.id.listRecycleId);

        ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                items.remove(position);
                itemAdapter.notifyItemRemoved(position);
                saveItems();

            }
        };

        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener(){
          @Override
            public void onItemClick (int position) {
              Intent intent = new Intent(MainActivity.this, EditActivity.class);
              intent.putExtra(KEY_ITEM_TEXT, items.get(position));
              intent.putExtra(KEY_ITEM_POS, position);

              startActivityForResult(intent, REQUEST_CODE);
          }
        };
        itemAdapter = new ItemAdapter(items, onLongClickListener, onClickListener);
        rView.setAdapter(itemAdapter);
        rView.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (insertItemsText.getText().toString() == null || insertItemsText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Nothing to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                items.add(insertItemsText.getText().toString());
                itemAdapter.notifyItemInserted(items.size()-1);

                insertItemsText.setText("");
                Toast.makeText(getApplicationContext(), "Item was added successfully", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    private File getDataFile () {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems () {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (Exception e) {
            items = new ArrayList<>();
            Log.e("MainActivity", "Error reading items", e);
        }
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (Exception e){
            Log.e("MainActivity", "Error writing items");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            int pos = data.getExtras().getInt(KEY_ITEM_POS);
            items.set(pos, data.getStringExtra(KEY_ITEM_TEXT));
            itemAdapter.notifyItemChanged(data.getExtras().getInt(KEY_ITEM_POS));
            saveItems();
            Toast.makeText(getApplicationContext(), "Successfully updated Item", Toast.LENGTH_SHORT).show();
        }
    }
}
