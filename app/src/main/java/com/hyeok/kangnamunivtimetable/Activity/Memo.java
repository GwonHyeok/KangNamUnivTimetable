package com.hyeok.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;
import com.hyeok.kangnamunivtimetable.R;

public class Memo extends Activity {
    EditText memoText;
    private ControlSharedPref MemoDate = new ControlSharedPref(Memo.this,
            "MemoDate");
    public static String MEMO_DATE_KEY = "memo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);
        memoText = (EditText) findViewById(R.id.MemoEditText);
        SetPreviewMemo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memomenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.memo_save:
                SaveMemo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        SaveMemo();
        super.onBackPressed();
    }

    public void SetPreviewMemo() {
        String Text = MemoDate.getValue(MEMO_DATE_KEY, "null");
        if (!Text.equals("null")) {
            memoText.setText(Text);
        }
    }

    public void SaveMemo() {
        Toast.makeText(this, getResources().getString(R.string.MEMO_SAVE_MSG), Toast.LENGTH_SHORT).show();
        MemoDate.put(MEMO_DATE_KEY, memoText.getText().toString());
    }
}
