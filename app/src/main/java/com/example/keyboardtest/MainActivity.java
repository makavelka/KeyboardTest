package com.example.keyboardtest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private View mContent;
    private View mLayout;
    private int mHeightKeyboard;
    private boolean isFirstPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        mContent = findViewById(R.id.root);
        mLayout = findViewById(R.id.layout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //При первом показе на экран выводится клавиатура. Используется для расчёта ее высоты
                if (!isFirstPress) {
                    isFirstPress = true;
                    show();
                    checkKeyboardHeight();
                } else {
                    //При повторном показе прячем клавиатуру и поднимаем эдит на высоту клавиатуры
                    //плюс высоту нижнего бара, если он есть
                    hide();
                    moveEditText(mHeightKeyboard + getHeightNavigationBar(MainActivity.this));
                }
            }
        });

    }

    /**
     * Метод поднимает эдит над нижним баром
     * @param height - высота
     */
    private void moveEditText(int height) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mLayout.setLayoutParams(params);
    }

    /**
     * показать виртуальную клавиатуру
     */
    private void show() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText,
                InputMethodManager.SHOW_IMPLICIT);
        checkKeyboardHeight();
    }

    /**
     * спрятать виртуальную клавиатуру
     */
    private void hide() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * Получить высоту нижнего бара, нужен для корректного расчета высоты эдита
     * @param context - контекст
     * @return - высоту нижнего бара
     */
    private int getHeightNavigationBar(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Метод проверяет изменение экрана при показе клавиатуры, для корректного расчёта ее высоты
     */
    private void checkKeyboardHeight() {
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int height = mContent.getHeight();
                        Log.w("Foo", String.format("mLayout height: %d", height));
                        Rect r = new Rect();
                        mContent.getWindowVisibleDisplayFrame(r);
                        int visible = r.bottom - r.top;
                        Log.w("Foo", String.format("visible height: %d", visible));
                        Log.w("Foo", String.format("keyboard height: %d", height - visible));
                        int temp =  height - visible;
                        if (temp > 100) {
                            mHeightKeyboard = temp;
                        }
                    }
                });
    }
}
