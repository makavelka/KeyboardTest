package com.example.keyboardtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private View mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        mContent = findViewById(R.id.root);
        final EmptyKeyboardView popup = new EmptyKeyboardView(mContent, this);

        //задаем размеры вью равные размерам клавиатуры
        popup.setSizeForSoftKeyboard();

        //если закрываем клавиатуру (нажимаем назад), то так же скрываем пустое вью
        popup.setOnSoftKeyboardOpenCloseListener(new EmptyKeyboardView.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!popup.isShowing()){

                    //если клавиатура видима, то показывать пустое вью
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                    }

                    //иначе, открывать клавиатуру сначала
                    else{
                        mEditText.setFocusableInTouchMode(true);
                        mEditText.requestFocus();
//                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }

                //если пустое вью на экране, то скрывать его
                else{
                    popup.dismiss();
                }
            }
        });

    }
}
